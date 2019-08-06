package net.cydhra.technocracy.foundation.tileentity.multiblock

import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.capabilities.fluid.DynamicFluidHandler
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.DefaultProgressBar
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.Orientation
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.client.gui.multiblock.BaseMultiblockTab
import net.cydhra.technocracy.foundation.client.gui.multiblock.MultiblockContainer
import net.cydhra.technocracy.foundation.client.gui.multiblock.MultiblockSettingsTab
import net.cydhra.technocracy.foundation.multiblock.BaseMultiBlock
import net.cydhra.technocracy.foundation.network.componentsync.guiInfoPacketSubscribers
import net.cydhra.technocracy.foundation.tileentity.AbstractRectangularMultiBlockTileEntity
import net.cydhra.technocracy.foundation.tileentity.AggregatableDelegate
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatable
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.tileentity.api.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.cydhra.technocracy.foundation.tileentity.components.FluidComponent
import net.cydhra.technocracy.foundation.tileentity.components.InventoryComponent
import net.cydhra.technocracy.foundation.tileentity.components.ProgressComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.reflect.KClass

/**
 * Base class for parts of a multi block. This class can be instantiated to create parts that do nothing
 *
 * @param clazz the class of the [MultiblockControllerBase] implementation responsible for this specific tile entity
 * @param constructController the constructor for the [MultiblockControllerBase] implementation
 */
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
abstract class TileEntityMultiBlockPart<T>(private val clazz: KClass<T>, private val constructController: (World) -> T)
    : TCAggregatableTileEntity, TCAggregatable by AggregatableDelegate(), TCTileEntityGuiProvider, AbstractRectangularMultiBlockTileEntity()
        where T : MultiblockControllerBase {

    init {
        this.tile = this
    }

    override fun createNewMultiblock(): T {
        return constructController(this.world)
    }

    override fun getMultiblockControllerType(): Class<T> {
        return clazz.java
    }

    override fun getMultiblockController(): T? {
        @Suppress("UNCHECKED_CAST")
        return super.getMultiblockController() as T?
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
        return this.multiblockController?.isAssembled ?: false
    }

    override fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing) {
        if (!player.isSneaking) {
            if (!world.isRemote) {
                if (this is ITileEntityMultiblockController && validateStructure()) {
                    player.openGui(TCFoundation, TCGuiHandler.multiblockGui, world, pos.x, pos.y, pos.z)
                    guiInfoPacketSubscribers[player as EntityPlayerMP] =  Pair(pos, world.provider.dimension)
                }
            }
        }
    }

    override fun getGui(player: EntityPlayer): TCGui {
        val gui = TCGui(player, container = MultiblockContainer(this))
        gui.registerTab(object : BaseMultiblockTab(this, gui, ResourceLocation("technocracy.foundation",
                "textures/item/silicon.png")) {
            override fun init() {
                addPlayerInventorySlots(player, 8, 84)

                var nextOutput = 125
                var inputNearestToTheMiddle = 0
                var outputNearestToTheMiddle = parent.guiWidth // nice names
                var foundProgressComponent: ProgressComponent? = null
                (this@TileEntityMultiBlockPart.multiblockController as BaseMultiBlock).getComponents().forEach {
                    when {
                        it.second is EnergyStorageComponent -> {
                            components.add(DefaultEnergyMeter(10, 20, it.second as EnergyStorageComponent, gui))
                            if (inputNearestToTheMiddle < 20)
                                inputNearestToTheMiddle = 20
                        }
                        it.second is FluidComponent -> {
                            val component: FluidComponent = it.second as FluidComponent
                            when {
                                component.fluid.tanktype == DynamicFluidHandler.TankType.INPUT -> {
                                    components.add(DefaultFluidMeter(25, 20, component, gui))
                                    if (inputNearestToTheMiddle < 35)
                                        inputNearestToTheMiddle = 35
                                }
                                component.fluid.tanktype == DynamicFluidHandler.TankType.OUTPUT -> {
                                    components.add(DefaultFluidMeter(nextOutput, 20, component, gui))
                                    if (outputNearestToTheMiddle > nextOutput)
                                        outputNearestToTheMiddle = nextOutput
                                    nextOutput += 15
                                }
                                component.fluid.tanktype == DynamicFluidHandler.TankType.BOTH -> {
                                    TODO("not implemented")
                                }
                            }
                        }
                        it.second is InventoryComponent -> {
                            val component: InventoryComponent = it.second as InventoryComponent
                            when {
                                it.first.contains("input") -> {
                                    for (i in 0 until component.inventory.slots) {
                                        components.add(TCSlotIO(component.inventory, i, 40 + i * 20, 40, gui))
                                        val newX = 40 + (i + 1) * 20
                                        if (inputNearestToTheMiddle < newX)
                                            inputNearestToTheMiddle = newX
                                    }

                                }
                                it.first.contains("output") -> {
                                    for (i in component.inventory.slots - 1 downTo 0) {
                                        components.add(TCSlotIO(component.inventory, i, 125 + i * 20, 40, gui))
                                        val newX = 125 + i * 20
                                        if (outputNearestToTheMiddle > newX)
                                            outputNearestToTheMiddle = newX
                                    }
                                }
                            }
                        }
                        it.second is ProgressComponent -> {
                            foundProgressComponent = it.second as ProgressComponent
                        }
                    }
                }
                if(foundProgressComponent != null)
                    components.add(DefaultProgressBar((outputNearestToTheMiddle - inputNearestToTheMiddle) / 2 - 11 + inputNearestToTheMiddle, 40, Orientation.RIGHT, foundProgressComponent as ProgressComponent, gui))
            }
        })
        initGui(gui)
        gui.registerTab(MultiblockSettingsTab(gui, this, player))
        return gui
    }

    open fun initGui(gui: TCGui) {}

}