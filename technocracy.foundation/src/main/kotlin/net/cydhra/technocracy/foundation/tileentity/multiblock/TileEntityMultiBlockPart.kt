package net.cydhra.technocracy.foundation.tileentity.multiblock

import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import net.cydhra.technocracy.foundation.multiblock.BaseMultiBlock
import net.cydhra.technocracy.foundation.tileentity.AbstractRectangularMultiBlockTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.reflect.KClass

/**
 * Base class for parts of a multi block. This class can be instantiated to create parts that do nothing
 *
 * @param clazz the class of the [MultiblockControllerBase] implementation responsible for this specific tile entity
 * @param constructController the constructor for the [MultiblockControllerBase] implementation
 */
abstract class TileEntityMultiBlockPart<T>(private val clazz: KClass<T>, private val constructController: (World) -> T)
    : AbstractRectangularMultiBlockTileEntity()
        where T : MultiblockControllerBase {

    override fun createNewMultiblock(): T {
        return constructController(this.world)
    }

    override fun getMultiblockControllerType(): Class<T> {
        return clazz.java
    }

    override fun getMultiblockController(): T {
        @Suppress("UNCHECKED_CAST")
        return super.getMultiblockController() as T
    }

    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    override fun isGoodForSides(validator: IMultiblockValidator?): Boolean {
        val controller = validator as? BaseMultiBlock ?: return true
        return controller.sideBlockWhitelist?.test(getBlockState()) ?: true
    }

    override fun isGoodForFrame(validator: IMultiblockValidator): Boolean {
        val controller = validator as? BaseMultiBlock ?: return true
        return controller.frameBlockWhitelist?.test(getBlockState()) ?: true
    }

    override fun isGoodForTop(validator: IMultiblockValidator): Boolean {
        val controller = validator as? BaseMultiBlock ?: return true
        return controller.topBlockWhitelist?.test(getBlockState()) ?: true
    }


    override fun isGoodForInterior(validator: IMultiblockValidator): Boolean {
        val controller = validator as? BaseMultiBlock ?: return true
        return controller.interiorBlockWhitelist?.test(getBlockState()) ?: true
    }

    override fun isGoodForBottom(validator: IMultiblockValidator): Boolean {
        val controller = validator as? BaseMultiBlock ?: return true
        return controller.bottomBlockWhitelist?.test(getBlockState()) ?: true
    }

    override fun validateStructure(): Boolean {
        return this.multiblockController.isAssembled
    }

    override fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing) {}
}