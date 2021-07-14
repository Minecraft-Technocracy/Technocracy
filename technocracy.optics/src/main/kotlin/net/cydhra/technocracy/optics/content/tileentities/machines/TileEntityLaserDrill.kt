package net.cydhra.technocracy.optics.content.tileentities.machines

import net.cydhra.technocracy.foundation.api.ecs.IAggregatableGuiProvider
import net.cydhra.technocracy.foundation.api.ecs.logic.EmptyLogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicClient
import net.cydhra.technocracy.foundation.api.ecs.logic.ILogicParameters
import net.cydhra.technocracy.foundation.api.ecs.logic.LogicClientDelegate
import net.cydhra.technocracy.foundation.api.tileentities.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.api.tileentities.TEInventoryProvider
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityEnergyStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityHeatStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityProgressComponent
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.cydhra.technocracy.optics.api.tileentities.components.LaserAbsorberComponent
import net.cydhra.technocracy.optics.content.tileentities.logic.LaserDrillLogic
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable

class TileEntityLaserDrill : AggregatableTileEntity(),
        ITickable,
        IAggregatableGuiProvider,
        TCTileEntityGuiProvider,
        ILogicClient<ILogicParameters> by LogicClientDelegate(),
        TEInventoryProvider<DynamicInventoryCapability> {

    companion object {
        const val PADDING_LEFT = 8
        const val PADDING_TOP = 20
        const val SLOT_WIDTH_PLUS_PADDING = 18
        const val SLOTS_PER_ROW = 9
    }

    private val heatStorageComponent = TileEntityHeatStorageComponent(0, 8000)
    private val outputInventory = TileEntityInventoryComponent(27, this, EnumFacing.UP,
            DynamicInventoryCapability.InventoryType.OUTPUT)
    private val progressComponent = TileEntityProgressComponent()
    private val laserAbsorberComponent = LaserAbsorberComponent(
            mutableSetOf(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST))

    // this is just a temporary energy storage that gets immediately emptied by LaserDrillLogic
    private val energyComponent = TileEntityEnergyStorageComponent(mutableSetOf()).apply {
        this.energyStorage.capacity = 400_000
    }

    init {
        this.registerComponent(progressComponent, "progress")
        this.registerComponent(heatStorageComponent, "heat")
        this.registerComponent(outputInventory, "output")
        this.registerComponent(laserAbsorberComponent, "laser_input")
        this.registerComponent(energyComponent, "energy")
    }

    override fun onLoad() {
        this.addLogicStrategy(LaserDrillLogic(
                progressComponent = progressComponent,
                energyComponent = energyComponent,
                outputInventory = outputInventory,
                energyPerProgress = 40_000,
                world = this.world
        ), "drilling")
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return false
    }

    override fun onSlotUpdate(
        inventory: DynamicInventoryCapability,
        slot: Int,
        stack: ItemStack,
        originalStack: ItemStack
    ) {
    }

    override fun update() {
        // update ILogic strategies, but only server side
        if (!world.isRemote)
            this.tick(EmptyLogicParameters)
    }

    override fun getGui(player: EntityPlayer?, other: TCGui?): TCGui {
        val gui = other ?: SimpleGui(container = TCContainer(this))
        gui.registerTab(object : TCTab(this.blockType?.localizedName ?: "Laser Drill", gui) {
            override fun init() {
                for (i in 0 until outputInventory.inventory.size) {
                    components.add(
                        TCSlotIO(
                            outputInventory.inventory, i,
                            PADDING_LEFT + (i % SLOTS_PER_ROW) * SLOT_WIDTH_PLUS_PADDING,
                            PADDING_TOP + (i / SLOTS_PER_ROW) * SLOT_WIDTH_PLUS_PADDING, parent
                        )
                    )
                }

                if (player != null)
                    addPlayerInventorySlots(player, 8, gui.guiHeight - 58 - 16 - 5 - 12)
            }
        })
        return gui
    }

    override fun canInteractWith(player: EntityPlayer?): Boolean {
        if (player == null) return true
        return player.isEntityAlive && !tile.isInvalid && player.getDistanceSq(tile.pos) <= 16
    }
}