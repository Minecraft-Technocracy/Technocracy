package net.cydhra.technocracy.foundation.blocks.api

import com.google.common.primitives.Ints
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.api.IBaseBlock
import net.cydhra.technocracy.foundation.blocks.color.ConstantBlockColor
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.fluids.BlockFluidBase
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fml.common.Mod
import java.util.*

@Mod.EventBusSubscriber(modid = TCFoundation.MODID)
open class BaseLiquidBlock(fluid: Fluid,
                           unlocalizedName: String,
                           material: Material,
                           registryName: String = fluid.name,
                           mapColor: MapColor = material.materialMapColor,
                           override val colorMultiplier: ConstantBlockColor? = null)
    : BlockFluidClassic(fluid, material, mapColor), IBaseBlock {

    override val modelLocation: String = registryName

    override val generateItem: Boolean
        get() = true

    //used to allow blocks to flow out of the water
    private var fakeSource = false

    init {
        this.unlocalizedName = unlocalizedName
        this.setRegistryName(registryName)
        this.setLightOpacity(100)
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun updateTick(world: World, pos: BlockPos, state: IBlockState, rand: Random) {


        super.updateTick(world, pos, state, rand)

        if(true)
            return

        var remainingHeight = quantaPerBlock - state.getValue(BlockFluidBase.LEVEL)
        val currentHeight: Int

        //basic check if it is submerged in a liquid
        if (touchesFluid(world, pos, true)) {
            //check if fluid is lighter, if so go up
            if (canDisplaceLiquid(world, pos.down(densityDir))) {
                //if an oil block is underneath add another on top
                if (isSourceBlock(world, pos) || (/*getFluidLevel(world, pos) == 1 && */world.getBlockState(pos.up(densityDir)).block == this
                                || touchesFluidWithState(world, pos, getFluidLevel(state) - 1))) {
                    world.setBlockState(pos.down(densityDir), state.withProperty(BlockFluidBase.LEVEL, 1), 2)
                    world.scheduleUpdate(pos.down(densityDir), this, tickRate)
                    world.notifyNeighborsOfStateChange(pos.down(densityDir), this, false)
                } else {
                    world.setBlockToAir(pos)
                }
            } else {
                if (!(isSourceBlock(world, pos) || (/*getFluidLevel(world, pos) == 1 && */world.getBlockState(pos.up
                        (densityDir)).block == this
                                || touchesFluidWithState(world, pos, getFluidLevel(state) - 1)))) {
                    world.setBlockToAir(pos)
                } else {
                    if (getFluidLevel(world, pos) < 8 && false) {
                        val flowTo = getOptimalFlowDirectionsUnderwater(world, pos)
                        val flowMeta = getFluidLevel(world, pos) + 1
                        for (i in 0..3) {
                            if (flowTo[i]) flowIntoFluid(world, pos.offset(SIDES[i]), flowMeta)
                        }
                    }
                }
            }
        } else {
            //forge copy pasta
            // check adjacent block levels if non-source
            if (remainingHeight < quantaPerBlock) {
                var sourceBlocks = 0

                if (ForgeEventFactory.canCreateFluidSource(world, pos, state, canCreateSources)) {
                    for (side in EnumFacing.Plane.HORIZONTAL) {
                        if (isSourceBlock(world, pos.offset(side))) sourceBlocks++
                    }
                }

                // new source block
                if (sourceBlocks >= 2 && (world.getBlockState(pos.up(densityDir)).material.isSolid ||
                                isSourceBlock(world, pos.up(densityDir)))) {
                    currentHeight = quantaPerBlock
                } else if (hasVerticalFlow(world, pos) ||
                        world.getBlockState(pos.up(densityDir)).block === this && touchesFluid(world, pos.up(densityDir),
                                true)) {
                    currentHeight = quantaPerBlock - 1
                } else {
                    var maxQuanta = -100
                    for (side in EnumFacing.Plane.HORIZONTAL) {
                        maxQuanta = getLargerQuanta(world, pos.offset(side), maxQuanta)
                    }
                    currentHeight = maxQuanta - 1
                }// vertical flow into block

                // decay calculation
                if (currentHeight != remainingHeight) {
                    remainingHeight = currentHeight

                    if (currentHeight <= 0) {
                        world.setBlockToAir(pos)
                    } else {
                        world.setBlockState(pos, state.withProperty(BlockFluidBase.LEVEL, quantaPerBlock - currentHeight), 2)
                        world.scheduleUpdate(pos, this, tickRate)
                        world.notifyNeighborsOfStateChange(pos, this, false)
                    }
                }
            }

            // Flow vertically if possible
            if (canDisplace(world, pos.up(densityDir))) {
                flowIntoBlock(world, pos.up(densityDir), 1)
                return
            }

            // Flow outward if possible
            var flowMeta = quantaPerBlock - remainingHeight + 1
            if (flowMeta >= quantaPerBlock) {
                return
            }

            if (isSourceBlock(world, pos) || !isFlowingVertically(world, pos)) {

                if (hasVerticalFlow(world, pos)) {
                    flowMeta = 1
                }
                //fakeSource = true
                val flowTo = getOptimalFlowDirections(world, pos)


                for (i in 0..3) {
                    if (flowTo[i]) flowIntoBlock(world, pos.offset(SIDES[i]), flowMeta)
                }
                //fakeSource = false
            }
        }
    }

    protected fun getOptimalFlowDirectionsUnderwater(world: World, pos: BlockPos): BooleanArray {
        for (side in 0..3) {
            flowCost[side] = 1000

            val pos2 = pos.offset(SIDES[side])

            val state = world.getBlockState(pos)
            val block = state.block

            if (!canDisplaceLiquid(world, pos2) || isSourceBlock(world, pos2) || block == this) {
                continue
            }

            flowCost[side] = 0
        }

        val min = Ints.min(*flowCost)
        for (side in 0..3) {
            isOptimalFlowDirection[side] = flowCost[side] == min
        }
        return isOptimalFlowDirection
    }

    override fun isFlowingVertically(world: IBlockAccess, pos: BlockPos): Boolean {

        val fromLiquid = touchesFluid(world, pos.up(densityDir), true)

        return if (fromLiquid) {

            ((touchesFluid(world, pos, true) && canDisplaceLiquid(world, pos.down(densityDir))) &&
                    (isSourceBlock(world, pos) || (getFluidLevel(world, pos) == 1 && world.getBlockState(pos.up(densityDir)).block == this)))
        } else {
            world.getBlockState(pos.up(densityDir)).block === this || world.getBlockState(pos).block === this && canFlowInto(world, pos.up(densityDir))
        }
    }

    private fun hasVerticalFlow(world: IBlockAccess, pos: BlockPos): Boolean {
        return world.getBlockState(pos.down(densityDir)).block === this
    }

    override fun isSourceBlock(world: IBlockAccess, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        return state.block === this && (state.getValue(BlockFluidBase.LEVEL) == 0 || fakeSource)
    }

    /**
     * Get the level of the fluid
     */
    private fun getFluidLevel(world: IBlockAccess, pos: BlockPos): Int {
        val state = world.getBlockState(pos)
        return if (state.block == this) state.getValue(BlockFluidBase.LEVEL) else -1
    }

    /**
     * Get the level of the fluid
     */
    private fun getFluidLevel(state: IBlockState): Int {
        return if (state.block == this) state.getValue(BlockFluidBase.LEVEL) else -1
    }

    /**
     * Returns true if one side (U,N,S,E,W) touches a fluid
     */
    private fun touchesFluid(world: IBlockAccess, pos: BlockPos, ignoreOwn: Boolean): Boolean {
        var liquid = false
        for (state in EnumFacing.values()) {
            if (state == EnumFacing.DOWN)
                continue
            val liquidState = world.getBlockState(pos.offset(state))
            if ((liquidState.block != this || ignoreOwn) && liquidState.block is BlockLiquid)
                liquid = true
        }

        return liquid
    }

    /**
     * Returns true if one side (U,N,S,E,W) touches a fluid
     */
    private fun touchesFluidWithState(world: IBlockAccess, pos: BlockPos, value: Int): Boolean {
        var liquid = false
        for (state in EnumFacing.HORIZONTALS) {
            val liquidState = world.getBlockState(pos.offset(state))
            if (liquidState.block == this && getFluidLevel(liquidState) == value)
                liquid = true
        }

        return liquid
    }

    protected fun flowIntoFluid(world: World, pos: BlockPos, meta: Int) {
        if (meta < 0 || meta > 15) return
        if (canDisplaceLiquid(world, pos)) {
            world.setBlockState(pos, this.defaultState.withProperty(BlockFluidBase.LEVEL, meta), 3)
        }
    }

    private fun canDisplaceLiquid(world: IBlockAccess, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        val block = state.block

        if (block.isAir(state, world, pos)) {
            return touchesFluid(world, pos.up(densityDir), true)
        }

        if (block == this) {
            return true
        }

        val density = getDensity(world, pos)

        if (density == Integer.MAX_VALUE)
            return false

        return this.density < density
    }

    /* acid water (eats blocks away)
    override fun canDisplace(world: IBlockAccess, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        val block = state.block

        if (block.isAir(state, world, pos)) {
            return true
        }

        if (block === this) {
            return false
        }

        if (displacements.containsKey(block)) {
            return displacements[block]!!
        }

        val density = getDensity(world, pos)
        return if (density == Integer.MAX_VALUE) {
            true
        } else this.density > density

    }*/
}