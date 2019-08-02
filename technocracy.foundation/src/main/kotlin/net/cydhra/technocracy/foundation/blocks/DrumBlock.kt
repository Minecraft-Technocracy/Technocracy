package net.cydhra.technocracy.foundation.blocks

import net.cydhra.technocracy.foundation.blocks.api.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.blocks.util.IDynamicBlockDisplayName
import net.cydhra.technocracy.foundation.blocks.util.IDynamicBlockItemProperty
import net.cydhra.technocracy.foundation.tileentity.TileEntityDrum
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.item.IItemPropertyGetter
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class DrumBlock : AbstractTileEntityBlock("drum", material = Material.ROCK), IDynamicBlockItemProperty, IDynamicBlockDisplayName {
    override fun getUnlocalizedName(stack: ItemStack): String {
        return DrumType.values()[stack.metadata].getDrumName()
    }

    override fun getOverrides(): Map<ResourceLocation, IItemPropertyGetter> {
        val map = mutableMapOf<ResourceLocation, IItemPropertyGetter>()
        map[ResourceLocation("type")] = IItemPropertyGetter { stack, worldIn, entityIn ->
            if (entityIn == null) {
                0.0f
            } else {
                (1f / 16f) * stack.metadata.toFloat()
            }
        }
        return map
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityDrum(meta)
    }

    companion object {
        var DRUMTYPE: PropertyEnum<DrumType> = PropertyEnum.create("drumtype", DrumType::class.java)
    }

    init {
        setHardness(1f)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(DRUMTYPE).build()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(DRUMTYPE).ordinal
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(DRUMTYPE, DrumType.values()[meta])
    }

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        if (this.creativeTabToDisplayOn == itemIn) {
            for (type in DrumType.values()) {
                val stack = ItemStack(this, 1, type.ordinal)
                val compound = NBTTagCompound()
                compound.setInteger("fluidAmount", 0)
                compound.setString("fluidType", "d")
                stack.tagCompound = compound
                items.add(stack)
            }
        }
    }

    val boundingBox = AxisAlignedBB(0.1, 0.0, 0.1, 0.9, 1.0, 0.9)

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullBlock(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, boundingBox)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return boundingBox
    }

    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        return boundingBox.offset(pos)
    }

    enum class DrumType(val typeName: String, val amount: Int) :
            IStringSerializable {
        IRON("iron", 64000), STEEL("steel", 256000), BEDROCKIUM("bedrock", 2048000);

        override fun getName(): String {
            return this.typeName
        }

        fun getDrumName(): String {
            return "tile.drum.${getName()}.name"
        }
    }
}