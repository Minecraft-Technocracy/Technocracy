package net.cydhra.technocracy.foundation.model.tileentities.multiblock

import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
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
import net.cydhra.technocracy.foundation.network.componentsync.guiInfoPacketSubscribers
import net.cydhra.technocracy.foundation.model.tileentities.api.AbstractRectangularMultiBlockTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableDelegate
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatable
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatableTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.api.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.ProgressTileEntityComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
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

    override fun syncDataTo(data: NBTTagCompound?, syncReason: SyncReason?) {
        super.syncDataTo(this.serializeNBT(data!!), syncReason)
    }

    override fun syncDataFrom(data: NBTTagCompound?, syncReason: SyncReason?) {
        super.syncDataFrom(data, syncReason)
        this.deserializeNBT(data!!)
        markRenderUpdate()
    }

    fun markRenderUpdate() {
        //only update if the world is fully loaded
        if (Minecraft.getMinecraft().player != null)
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 0)
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
                    guiInfoPacketSubscribers[player as EntityPlayerMP] = Pair(pos, world.provider.dimension)
                }
            }
        }
    }

    override fun getGui(player: EntityPlayer?): TCGui {
        val gui = TCGui(container = MultiblockContainer(this))
        gui.registerTab(object : BaseMultiblockTab(this, gui, ResourceLocation("technocracy.foundation",
                "textures/item/silicon.png")) {
            override fun init() {
                var nextOutput = 125
                var nextInput = 10
                var inputNearestToTheMiddle = 0
                var outputNearestToTheMiddle = parent.guiWidth
                var foundProgressComponent: ProgressTileEntityComponent? = null
                val sortedComponents = listOf(*(this@TileEntityMultiBlockPart.multiblockController as BaseMultiBlock).getComponents().toTypedArray())
                        .sortedBy { (_, component) -> component !is FluidTileEntityComponent }
                        .sortedBy { (_, component) -> component !is EnergyStorageTileEntityComponent }
                sortedComponents.forEach { (name, component) ->
                    when (component) {
                        is EnergyStorageTileEntityComponent -> {
                            components.add(DefaultEnergyMeter(nextInput, 20, component, gui))
                            if (inputNearestToTheMiddle < 20) {
                                inputNearestToTheMiddle = 20
                                nextInput = 25
                            }
                        }
                        is FluidTileEntityComponent -> {
                            when {
                                component.fluid.tanktype == DynamicFluidCapability.TankType.INPUT -> {
                                    components.add(DefaultFluidMeter(nextInput, 20, component, gui))
                                    if (inputNearestToTheMiddle < nextInput - 5) {
                                        inputNearestToTheMiddle = nextInput - 5 // 5 is the space between components
                                    }
                                    nextInput += 15 // fluid meter width (10) + space (5)
                                }
                                component.fluid.tanktype == DynamicFluidCapability.TankType.OUTPUT -> {
                                    components.add(DefaultFluidMeter(nextOutput, 20, component, gui))
                                    if (outputNearestToTheMiddle > nextOutput)
                                        outputNearestToTheMiddle = nextOutput
                                    nextOutput += 15
                                }
                                component.fluid.tanktype == DynamicFluidCapability.TankType.BOTH -> {
                                    TODO("not implemented")
                                }
                            }
                        }
                        is InventoryTileEntityComponent -> {
                            when {
                                name.contains("input") -> {
                                    for (i in 0 until component.inventory.slots) {
                                        if(nextInput == 25)
                                            nextInput = 30
                                        components.add(TCSlotIO(component.inventory, i, nextInput, 40, gui))
                                        if (inputNearestToTheMiddle < nextInput)
                                            inputNearestToTheMiddle = nextInput
                                        nextInput += 20
                                    }

                                }
                                name.contains("output") -> {
                                    for (i in component.inventory.slots - 1 downTo 0) {
                                        components.add(TCSlotIO(component.inventory, i, 125 + i * 20, 40, gui))
                                        val newX = 125 + i * 20
                                        if (outputNearestToTheMiddle > newX)
                                            outputNearestToTheMiddle = newX
                                    }
                                }
                            }
                        }
                        is ProgressTileEntityComponent -> {
                            foundProgressComponent = component
                        }
                    }
                }
                if (foundProgressComponent != null)
                    components.add(DefaultProgressBar((outputNearestToTheMiddle - inputNearestToTheMiddle) / 2 + inputNearestToTheMiddle, 40, Orientation.RIGHT, foundProgressComponent as ProgressTileEntityComponent, gui))

                if (player != null)
                    addPlayerInventorySlots(player, 8, 84)
            }
        })
        initGui(gui)
        gui.registerTab(MultiblockSettingsTab(gui, this))
        return gui
    }

    open fun initGui(gui: TCGui) {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return multiblockController?.isAssembled ?: false && this.supportsCapability(capability, facing) && facing != null
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (multiblockController?.isAssembled == true)
            this.castCapability(capability, facing)
        else
            null
    }
}