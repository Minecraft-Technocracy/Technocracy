package net.cydhra.technocracy.optics.content.tileentities.logic

import it.zerono.mods.zerocore.util.OreDictionaryHelper
import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.ProgressTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogic
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.oredict.OreDictionary

class LaserDrillLogic(
        private val progressComponent: ProgressTileEntityComponent,
        private val energyComponent: EnergyStorageTileEntityComponent,
        private val outputInventory: InventoryTileEntityComponent,
        private val energyPerProgress: Int,
        private val world: World
) : ILogic {

    companion object {
        val registeredOres by lazy {
            OreDictionary.getOreNames().filter { it.startsWith("ore") }
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
            // TODO develop a workaround for bug in OreDictionary
//            var resource = this.generateResource()
//            var i = 0
//            while (i < this.outputInventory.inventory.size) {
//                resource = this.outputInventory.inventory.insertItem(i, resource, false)
//                if (resource.isEmpty)
//                    break
//                i++
//            }
        }
    }

    override fun postProcessing(wasProcessing: Boolean) {

    }

    private fun generateResource(): ItemStack {
        val randomOre = registeredOres[world.rand.nextInt(registeredOres.size)]

        // TODO this does yield an ArrayIOOBE, because the OreDictionary THINKS it has the itemstacks for ore names,
        //  but it does not actually have them. Problem here: The community might still use 1.12.X widely, but Lex
        //  does not care, so we won't even try to convince him to accept a PR fixing this. We need a workaround.
        return OreDictionaryHelper.getOre(randomOre).copy().apply { count = 1 }
    }
}