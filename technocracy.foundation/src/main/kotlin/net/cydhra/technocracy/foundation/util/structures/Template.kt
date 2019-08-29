package net.cydhra.technocracy.foundation.util.structures

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTUtil
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.common.Loader
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files


class Template() : INBTSerializable<NBTTagCompound> {
    val blocks = mutableListOf<BlockInfo>()
    var modules = mutableMapOf<Int, MutableList<BlockPos>>()

    var init = false
    lateinit var controller: BlockPos

    constructor(center: BlockPos, worldIn: World, blockList: List<BlockPos>) : this() {
        this.controller = center

        label@ for (boxBlocks in blockList) {
            val relativePos = boxBlocks.subtract(controller)

            val state = worldIn.getBlockState(boxBlocks)
            val block = state.block

            if (block == Blocks.AIR)
                continue

            val tileentity = worldIn.getTileEntity(boxBlocks)

            if (tileentity != null) {
                val tag = tileentity.writeToNBT(NBTTagCompound())
                tag.removeTag("x")
                tag.removeTag("y")
                tag.removeTag("z")
                blocks.add(BlockInfo(relativePos, block, block.getMetaFromState(state), tag))
            } else {
                blocks.add(BlockInfo(relativePos, block, block.getMetaFromState(state), null))
            }
        }
        init = true
    }

    fun loadFromAssets(name: String): Template {
        Loader.instance().indexedModList.forEach { (modId, modContainer) ->
            CraftingHelper.findFiles(
                    modContainer,
                    "assets/$modId/templates/$name.nbt",
                    { true },
                    { _, path ->
                        deserializeNBT(CompressedStreamTools.readCompressed(Files.newInputStream(path)))
                        init
                    },
                    true,
                    true)
        }
        return this
    }

    override fun deserializeNBT(compound: NBTTagCompound?) {
        if (compound == null) return
        this.blocks.clear()

        if (compound.hasKey("blocks")) {
            val blocks = compound.getTagList("blocks", 10)
            for (j in 0 until blocks.tagCount()) {
                val blockTag = blocks.getCompoundTagAt(j)
                val blockpos = NBTUtil.getPosFromTag(blockTag.getCompoundTag("pos"))
                val name = blockTag.getString("block")
                val meta = blockTag.getInteger("meta")
                var blockNBT: NBTTagCompound? = null

                if (blockTag.hasKey("nbt")) {
                    blockNBT = blockTag.getCompoundTag("nbt")
                }

                this.blocks.add(BlockInfo(blockpos, Block.getBlockFromName(name)!!, meta, blockNBT))
            }
        }

        if (compound.hasKey("modules")) {
            val modules = compound.getTagList("modules", 9)
            for (j in 0 until modules.tagCount()) {
                val posModules = modules.get(j) as NBTTagList
                val list = mutableListOf<BlockPos>()
                for (posPos in 0 until posModules.tagCount()) {
                    list.add(NBTUtil.getPosFromTag(posModules.getCompoundTagAt(posPos)))
                }
                this.modules[j] = list
            }
        }
        init = true
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbtTagCompound = NBTTagCompound()
        val nbttaglist = NBTTagList()

        for (block in this.blocks) {
            nbttaglist.appendTag(NBTTagCompound().apply {
                setTag("pos", NBTUtil.createPosTag(block.pos))
                setString("block", block.block.registryName.toString())
                setInteger("meta", block.meta)

                if (block.nbt != null) {
                    setTag("nbt", block.nbt)
                }
            })
        }

        val modulesList = NBTTagList()
        for (block in this.modules.entries) {
            modulesList.appendTag(NBTTagList().apply {
                for (pos in block.value) {
                    appendTag(NBTUtil.createPosTag(pos))
                }
            })
        }

        nbtTagCompound.setTag("blocks", nbttaglist)
        if (!modulesList.hasNoTags())
            nbtTagCompound.setTag("modules", modulesList)
        return nbtTagCompound
    }

    fun generateTemplate(startPos: BlockPos, endPos: BlockPos, controller: BlockPos, wildcard: MutableList<BlockPos>, modules: MutableMap<Int, MutableList<BlockPos>>, ignoreAir: Boolean, wildcardAll: Boolean, worldIn: World, name: String): Boolean {
        init = true
        this.controller = controller

        modules.forEach { id, list ->
            val newList = mutableListOf<BlockPos>()
            list.forEach { b ->
                newList.add(b.subtract(controller))
            }
            this.modules[id] = newList
        }

        label@ for (boxBlocks in BlockPos.getAllInBoxMutable(startPos, endPos)) {
            val relativePos = boxBlocks.subtract(controller)

            val state = worldIn.getBlockState(boxBlocks)
            val block = state.block

            for (mod in this.modules.values) {
                if (mod.contains(relativePos))
                    continue@label
            }

            if (ignoreAir) {
                if (block == Blocks.AIR)
                    continue
            }
            val tileentity = worldIn.getTileEntity(boxBlocks)

            if (tileentity != null && (!wildcard.contains(boxBlocks) && !wildcardAll)) {
                val tag = tileentity.writeToNBT(NBTTagCompound())
                tag.removeTag("x")
                tag.removeTag("y")
                tag.removeTag("z")
                blocks.add(BlockInfo(relativePos, block, block.getMetaFromState(state), tag))
            } else {
                blocks.add(BlockInfo(relativePos, block, if (!wildcard.contains(boxBlocks) && !wildcardAll) block.getMetaFromState(state) else -1, null))
            }
        }

        val file = File(Minecraft.getMinecraft().mcDataDir.absolutePath + "/mods/${TCFoundation.MODID}/$name.nbt")
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        CompressedStreamTools.writeCompressed(serializeNBT(), FileOutputStream(file))

        return true
    }

    fun matches(worldIn: World,
                pos: BlockPos,
                fullMirror: Boolean = false,
                valid: (state: IBlockState, block: Block, layer: Int) -> Boolean = { _, _, _ -> true }):
            MutableList<BlockPos>? {
        val rots = if (fullMirror) Rotation.values() else arrayOf(Rotation.NONE, Rotation.CLOCKWISE_90)

        for (rot in rots) {
            if (matches(worldIn, pos, rot, valid)) {
                val list = mutableListOf<BlockPos>()
                for (block in blocks) {
                    list.add(pos.add(block.pos.rotate(rot)))
                }
                for (mods in modules) {
                    for(block in mods.value)
                    list.add(pos.add(block.rotate(rot)))
                }
                return list
            }
        }

        return null
    }

    private fun matches(worldIn: World, pos: BlockPos, rotation: Rotation, valid: (state: IBlockState, block: Block, layer: Int) -> Boolean): Boolean {
        label@ for (block in blocks) {
            val wildcard = block.meta == -1

            for (mod in this.modules.values) {
                if (mod.contains(block.pos))
                    continue@label
            }

            val pos = pos.add(block.pos.rotate(rotation))
            val state = worldIn.getBlockState(pos)
            val worldBlock = state.block

            if (wildcard) {
                if (worldBlock != block.block) {
                    return false
                }
            } else {
                var nbt = false

                if (block.nbt != null) {
                    val tile = worldIn.getTileEntity(pos)
                    if (tile != null) {
                        val tag = tile.writeToNBT(NBTTagCompound())
                        tag.removeTag("x")
                        tag.removeTag("y")
                        tag.removeTag("z")
                        nbt = tag != block.nbt
                    }
                }

                val rotState = block.block.getStateFromMeta(block.meta).withRotation(rotation)



                if (worldBlock != block.block || worldBlock.getMetaFromState(state) != block.block.getMetaFromState(rotState) || nbt) {
                    return false
                }
            }
        }

        for (module in modules) {
            val id = module.key

            for (modPos in module.value) {
                val pos = pos.add(modPos.rotate(rotation))
                val state = worldIn.getBlockState(pos)
                val worldBlock = state.block

                if (!valid.invoke(state, worldBlock, id)) return false
            }
        }
        return true
    }
}

data class BlockInfo(val pos: BlockPos, val block: Block, val meta: Int, val nbt: NBTTagCompound?)