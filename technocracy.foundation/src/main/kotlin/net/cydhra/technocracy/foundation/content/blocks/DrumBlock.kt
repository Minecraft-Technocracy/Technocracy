package net.cydhra.technocracy.foundation.content.blocks

import net.cydhra.technocracy.foundation.api.IWrench
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicItemFluidStorage
import net.cydhra.technocracy.foundation.content.tileentities.storage.TileEntityDrum
import net.cydhra.technocracy.foundation.model.blocks.api.AbstractTileEntityBlock
import net.cydhra.technocracy.foundation.model.blocks.color.IBlockColor
import net.cydhra.technocracy.foundation.model.blocks.util.IDynamicBlockDisplayName
import net.cydhra.technocracy.foundation.model.blocks.util.IDynamicBlockItemCapability
import net.cydhra.technocracy.foundation.model.blocks.util.IDynamicBlockItemProperty
import net.cydhra.technocracy.foundation.model.blocks.util.IDynamicBlockPlaceBehavior
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.cydhra.technocracy.foundation.model.items.capability.ItemFluidTileEntityComponent
import net.cydhra.technocracy.foundation.util.ColorUtil
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.IItemPropertyGetter
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler


class DrumBlock : AbstractTileEntityBlock("drum", material = Material.ROCK, colorMultiplier = object : IBlockColor {
    override fun colorMultiplier(state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int {
        val tile = (worldIn!!.getTileEntity(pos!!) ?: return -1) as? TileEntityDrum
                ?: return -1
        val fluid = tile.fluidCapability.currentFluid
        return ColorUtil.getColor(fluid)
    }

    override fun colorMultiplier(stack: ItemStack, tintIndex: Int): Int {

        val cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)

        if (cap != null) {
            val fluid = (cap as DynamicItemFluidStorage).currentFluid
            return ColorUtil.getColor(fluid)
        }

        return -1
    }

    fun getFluid(nbt: NBTTagCompound): Fluid? {
        val stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluid")) ?: return null
        return stack.fluid
    }

}), IDynamicBlockItemProperty, IDynamicBlockDisplayName, IDynamicBlockPlaceBehavior, IDynamicBlockItemCapability {

    companion object {
        var DRUM_TYPE: PropertyEnum<DrumType> = PropertyEnum.create("drumtype", DrumType::class.java)
    }

    init {
        setHardness(1.5F)
    }

    override fun placeBlockAt(place: Boolean, stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, newState: IBlockState): Boolean {
        //if (!stack.hasTagCompound()) return place
        val tile = world.getTileEntity(pos) as? TileEntityDrum
                ?: return place

        val cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)

        if (cap != null) {
            val fluid = (cap as DynamicItemFluidStorage).currentFluid
            if (fluid != null)
                tile.fluidCapability.fill(fluid, true)
        }

        //tile.deserializeNBT(stack.tagCompound!!)
        return place
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        val stack = ItemStack(this, 1, getMetaFromState(state))
        if (te != null && te is TileEntityDrum && te.fluidCapability.currentFluid != null) {
            stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)!!.fill(te.fluidCapability.currentFluid, true)

            /*val comp = NBTTagCompound()
            te.serializeNBT(comp)
            stack.tagCompound = comp*/
        }
        return stack
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return DrumType.values()[stack.metadata].getDrumName()
    }

    override fun getOverrides(): Map<ResourceLocation, IItemPropertyGetter> {
        val map = mutableMapOf<ResourceLocation, IItemPropertyGetter>()
        map[ResourceLocation("type")] = IItemPropertyGetter { stack, _, _ ->
            (1f / 16f) * stack.metadata.toFloat()
        }
        return map
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityDrum()
    }

    init {
        setHardness(1f)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer.Builder(this).add(DRUM_TYPE).build()
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(DRUM_TYPE).ordinal
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(DRUM_TYPE, DrumType.values()[meta])
    }

    override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack>) {
        if (this.creativeTabToDisplayOn == itemIn) {
            for (type in DrumType.values()) {
                items.add(ItemStack(this, 1, type.ordinal))
            }
        }
    }

    val boundingBox = AxisAlignedBB(0.1, 0.0, 0.1, 0.9, 1.0, 0.9)

    override fun canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean {
        return layer == BlockRenderLayer.CUTOUT
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
        @Suppress("DEPRECATION") // very funny, forge. But not helpful
        addCollisionBoxToList(pos, entityBox, collidingBoxes, boundingBox)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return boundingBox
    }

    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        return boundingBox.offset(pos)
    }

    override fun onBlockWrenched(worldIn: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack): Boolean {
        if (player.isSneaking) {
            harvestBlock(worldIn, player, pos, state, te, stack)
            return true
        }
        return false
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ))
            return true

        val stack = playerIn.getHeldItem(hand)

        if (FluidUtil.getFluidHandler(stack) != null) {
            FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing)
        } else {
            val tile = worldIn.getTileEntity(pos) as? TileEntityDrum
                    ?: return true
            val fluid = tile.fluidCapability.currentFluid
            //TODO translate
            playerIn.sendStatusMessage(TextComponentString("Drum content: " + if (fluid == null) "Empty" else "${fluid.amount}mB"), true)
        }

        return true
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        return ItemCapabilityWrapper(stack, mutableMapOf("fluid" to ItemFluidTileEntityComponent(DynamicItemFluidStorage(stack, DrumType.values()[MathHelper.clamp(stack.metadata, 0, DrumType.values().size - 1)].amount, mutableListOf(), DynamicFluidCapability.TankType.BOTH))))
    }

    enum class DrumType(val typeName: String, val amount: Int) :
            IStringSerializable {
        IRON("iron", 64000), STEEL("steel", 256000);

        override fun getName(): String {
            return this.typeName
        }

        fun getDrumName(): String {
            return "tile.drum.${getName()}"
        }
    }
}