package net.cydhra.technocracy.optics.content.tileentities.logic

import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.ProgressTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogic
import net.cydhra.technocracy.optics.TCOptics
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class LaserDrillLogic(
        private val progressComponent: ProgressTileEntityComponent,
        private val energyComponent: EnergyStorageTileEntityComponent,
        private val outputInventory: InventoryTileEntityComponent,
        private val energyPerProgress: Int,
        private val world: World
) : ILogic {

    companion object {
        val registeredOres by lazy {
            TCOptics.shadowOreDictionary.keys.toList()
        }
    }

    override fun preProcessing(): Boolean {
        return energyComponent.energyStorage.currentEnergy > energyPerProgress
    }

    override fun processing() {
        val energyAvailable = energyComponent.energyStorage.currentEnergy
        val progress = energyAvailable / energyPerProgress

        if (energyComponent.energyStorage.consumeEnergy(progress * energyPerProgress)) {
            this.progressComponent.progress += progress
        }

        if (this.progressComponent.progress > 100) {
            var resource = this.generateResource()
            var i = 0
            while (i < this.outputInventory.inventory.size) {
                resource = this.outputInventory.inventory.insertItem(i, resource, simulate = false, forced = true)
                if (resource.isEmpty) {
                    this.progressComponent.progress -= 100
                    this.outputInventory.markDirty(true)
                    break
                }
                i++
            }
        }
    }

    override fun postProcessing(wasProcessing: Boolean) {

    }

    private fun generateResource(): ItemStack {
        val randomOre = registeredOres[world.rand.nextInt(registeredOres.size)]
        return (TCOptics.shadowOreDictionary[randomOre] ?: error("$randomOre is not part of the shadow ore dictionary"))
                .copy()
                .apply { this.count = 1 }
    }
}