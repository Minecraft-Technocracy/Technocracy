package net.cydhra.technocracy.optics.content.tileentities.machines

import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.HeatStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.ProgressTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.TCMachineTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogicClient
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.LogicClientDelegate
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

class TileEntityLaserDrill : AggregatableTileEntity(), TCMachineTileEntity, ILogicClient by LogicClientDelegate(),
        TEInventoryProvider {

    private val heatStorageComponent = HeatStorageTileEntityComponent(0, 8000)
    private val outputInventory = InventoryTileEntityComponent(9, this, EnumFacing.UP,
            DynamicInventoryCapability.InventoryType.OUTPUT)
    private val progressComponent = ProgressTileEntityComponent()

    init {
        this.registerComponent(progressComponent, "progress")
        this.registerComponent(heatStorageComponent, "heat")
        this.registerComponent(outputInventory, "output")
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return false
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {}

    override fun update() {
        // update ILogic strategies, but only server side
        if (!world.isRemote)
            this.tick()
    }

    override fun getGui(player: EntityPlayer?): TCGui {
        val gui = TCGui(container = TCContainer())
        gui.registerTab(object : TCTab(this.blockType?.localizedName ?: "Laser Drill", gui) {
            override fun init() {
                // TODO
            }
        })
        return gui
    }
}