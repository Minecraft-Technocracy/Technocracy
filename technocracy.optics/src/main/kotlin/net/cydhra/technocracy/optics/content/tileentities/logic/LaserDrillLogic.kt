package net.cydhra.technocracy.optics.content.tileentities.logic

import net.cydhra.technocracy.foundation.api.ecs.logic.ILogic
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityEnergyStorageComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityInventoryComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.TileEntityProgressComponent
import net.cydhra.technocracy.optics.TCOptics
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class LaserDrillLogic(
        private val progressComponent: TileEntityProgressComponent,
        private val energyComponent: TileEntityEnergyStorageComponent,
        private val outputInventory: TileEntityInventoryComponent,
        private val energyPerProgress: Int,
        private val world: World
) : ILogic {

    private var nextResource: ItemStack = ItemStack.EMPTY

    companion object {
        val registeredOres by lazy {
            TCOptics.shadowOreDictionary.keys.toList()
        }
    }

    override fun preProcessing(): Boolean {
        return energyComponent.energyStorage.currentEnergy > energyPerProgress &&
                (nextResource.isEmpty || outputInventory.inventory.stacks.any { it.isEmpty })
    }

    override fun processing() {
        val energyAvailable = energyComponent.energyStorage.currentEnergy
        val progress = energyAvailable / energyPerProgress

        if (energyComponent.energyStorage.consumeEnergy(progress * energyPerProgress)) {
            this.progressComponent.progress += progress
        }

        if (this.progressComponent.progress > 100) {
            nextResource = this.generateResource()
        }

        if (!nextResource.isEmpty) {
            var i = 0
            while (i < this.outputInventory.inventory.size) {
                nextResource = this.outputInventory.inventory.insertItem(i, nextResource, simulate = false, forced = true)
                if (nextResource.isEmpty) {
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