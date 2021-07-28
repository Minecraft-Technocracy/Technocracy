package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.ecs.tileentities.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.util.propertys.POSITION
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState

/**
 * Blocks for machines or machine-like tile entities. This should only be used for tileentities inheriting
 * [TCAggregatableTileEntity].
 */
class MachineBlock(name: String, private val tileEntityConstructor: () -> TileEntity, renderLayer: BlockRenderLayer = BlockRenderLayer.SOLID) : AbstractRotatableTileEntityBlock(name, material = Material.ROCK, renderLayer = renderLayer) {

    override fun addExtendedPropertyToState(state: IExtendedBlockState, world: IBlockAccess?, pos: BlockPos?): IExtendedBlockState {
        return state.withProperty(POSITION, pos)
    }

    override fun addPropertyToBuilder(builder: BlockStateContainer.Builder): BlockStateContainer.Builder {
        return builder.add(POSITION)
    }

    init {
        this.setHardness(2f)
        this.setResistance(4f)
        lightOpacity = 0
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return tileEntityConstructor()
    }

    @Suppress("OverridingDeprecatedMember")
    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!playerIn.isSneaking) {
            if (!worldIn.isRemote) {
                playerIn.openGui(TCFoundation, TCGuiHandler.machineGui, worldIn, pos.x, pos.y, pos.z)
            }

            return true
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        if (te == null)
            throw AssertionError("machine block without tile entity")

        val itemStack = ItemStack(this)
        val machineCompound = itemStack.getOrCreateSubCompound("machinedata")

        if (te is TCAggregatableTileEntity) {
            te.getComponents().forEach { (id, component) ->
                machineCompound.setTag(id, component.serializeNBT())
            }
        }

        return itemStack
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        val machineData = stack.getSubCompound("machinedata")
        if (machineData != null) {
            val tileEntity = worldIn.getTileEntity(pos)

            if (tileEntity is TCAggregatableTileEntity) {
                tileEntity.getComponents().forEach { (id, component) ->
                    machineData.getCompoundTag(id).takeIf { it.size > 0 }?.let(component::deserializeNBT)
                }
            }
        }
    }
}