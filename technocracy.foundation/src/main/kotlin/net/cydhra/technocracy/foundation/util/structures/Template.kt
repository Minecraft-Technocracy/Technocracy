package net.cydhra.technocracy.foundation.util.structures

import net.cydhra.technocracy.foundation.TCFoundation
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.nbt.*
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.fml.common.Loader
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files


class Template {
    val blocks = mutableListOf<BlockInfo>()
    lateinit var controller: BlockPos

    fun loadFromAssets(name: String): Template {
        Loader.instance().indexedModList.forEach { (modId, modContainer) ->
            CraftingHelper.findFiles(
                    modContainer,
                    "assets/$modId/templates/$name.nbt",
                    { true },
                    { _, path -> read(CompressedStreamTools.readCompressed(Files.newInputStream(path))) },
                    true,
                    true)
        }
        return this
    }

    private fun read(compound: NBTTagCompound): Boolean {
        this.blocks.clear()

        val nbttaglist3 = compound.getTagList("blocks", 10)
        controller = NBTUtil.getPosFromTag(compound.getCompoundTag("controller"))

        for (j in 0 until nbttaglist3.tagCount()) {
            val nbttagcompound = nbttaglist3.getCompoundTagAt(j)
            val blockpos = NBTUtil.getPosFromTag(nbttagcompound.getCompoundTag("pos"))
            val name = nbttagcompound.getString("block")
            val meta = nbttagcompound.getInteger("meta")
            var nbttagcompound1: NBTTagCompound? = null

            if (nbttagcompound.hasKey("nbt")) {
                nbttagcompound1 = nbttagcompound.getCompoundTag("nbt")
            }

            this.blocks.add(BlockInfo(blockpos, Block.getBlockFromName(name)!!, meta, nbttagcompound1))
        }

        return true
    }

    private fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
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

        nbt.setTag("blocks", nbttaglist)
        return nbt
    }

    fun generateTemplate(startPos: BlockPos, endPos: BlockPos, controller: BlockPos, wildcard: MutableList<BlockPos>, worldIn: World, name: String): Boolean {
        this.controller = controller

        for (boxBlocks in BlockPos.getAllInBoxMutable(startPos, endPos)) {
            val relativePos = boxBlocks.subtract(controller)
            val state = worldIn.getBlockState(boxBlocks)
            val block = state.block

            if (block != Blocks.BARRIER) {
                val tileentity = worldIn.getTileEntity(boxBlocks)

                if (tileentity != null && !wildcard.contains(boxBlocks)) {
                    val tag = tileentity.writeToNBT(NBTTagCompound())
                    tag.removeTag("x")
                    tag.removeTag("y")
                    tag.removeTag("z")
                    blocks.add(BlockInfo(relativePos, block, block.getMetaFromState(state), tag))
                } else {
                    blocks.add(BlockInfo(relativePos, block, if (!wildcard.contains(boxBlocks)) block.getMetaFromState(state) else -1, null))
                }
            }
        }

        val file = File(Minecraft.getMinecraft().mcDataDir.absolutePath + "/mods/${TCFoundation.MODID}/$name.nbt")
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        CompressedStreamTools.writeCompressed(writeToNBT(NBTTagCompound()), FileOutputStream(file))

        return true
    }

    fun matches(worldIn: World, pos: BlockPos, fullMirror: Boolean = false): MutableList<BlockPos>? {
        val rots = if (fullMirror) Rotation.values() else arrayOf(Rotation.NONE, Rotation.CLOCKWISE_90)

        for (rot in rots) {
            if (matches(worldIn, pos, rot)) {
                val list = mutableListOf<BlockPos>()
                for (block in blocks) {
                    if (block.meta == -1) {
                        list.add(pos.add(block.pos.rotate(rot)))
                    }
                }
                return list
            }
        }

        return null
    }

    private fun matches(worldIn: World, pos: BlockPos, rotation: Rotation): Boolean {
        for (block in blocks) {
            val wildcard = block.meta == -1
            val pos = pos.add(block.pos.rotate(rotation))
            val state = worldIn.getBlockState(pos)
            val worldBlock = state.block
            if (wildcard) {
                if (worldBlock != block.block) return false
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
                if (worldBlock != block.block || worldBlock.getMetaFromState(state) != block.block.getMetaFromState(rotState) || nbt) return false
            }
        }
        return true
    }
}

data class BlockInfo(val pos: BlockPos, val block: Block, val meta: Int, val nbt: NBTTagCompound?)