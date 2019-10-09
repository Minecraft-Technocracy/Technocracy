package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.client.gui.components.TCComponent
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.gui.TabDrawable
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryComponent
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class MachineRecipeCategory(guiHelper: IGuiHelper, val tileEntity: MachineTileEntity, recipeType: RecipeManager.RecipeType, categoryUid: String, val block: Block) : AbstractRecipeCategory<MachineRecipeCategory.MachineRecipeWrapper>(
        guiHelper,
        block,
        recipeType,
        MachineRecipeWrapper::class.java,
        categoryUid
) {

    private val tabDrawable = TabDrawable(tileEntity)
    private val stolenComponents = mutableMapOf<TCComponent, Boolean>() // component, isInput


    init {
        tabDrawable.tab?.components?.forEach { guiComponent ->
            if (guiComponent is DefaultFluidMeter) {
                stolenComponents[guiComponent] = guiComponent.component.fluid.tanktype == DynamicFluidCapability.TankType.INPUT
            } else if (guiComponent is Slot) {
                if (guiComponent is TCSlotIO) {
                    tileEntity.getComponents().filter { it.second is InventoryComponent }
                            .filter { (_, component) -> (component as InventoryComponent).inventory == guiComponent.itemHandler }
                            .forEach { (name, _) ->
                                stolenComponents[guiComponent] = name.toLowerCase().contains("input")
                            }
                }
            } else if (guiComponent is DefaultEnergyMeter) {
                guiComponent.component.energyStorage.forceUpdateOfCurrentEnergy(guiComponent.component.energyStorage.maxEnergyStored)
                guiComponent.update()
            }
        }

        stolenComponents.forEach { (component, _) ->
            // stolen components are handled by jei
            tabDrawable.tab?.components?.remove(component)
        }
    }


    override fun setRecipe(layout: IRecipeLayout, wrapper: MachineRecipeWrapper, ingredients: IIngredients) {
        val itemStacks = layout.itemStacks
        val fluidStacks = layout.fluidStacks

        var itemIndex = 0
        var fluidIndex = 0

        stolenComponents.forEach { (component, isInput) ->
            if (component is TCSlotIO) {
                itemStacks.init(itemIndex, isInput, component.xPos, component.yPos)
                itemStacks.setBackground(itemIndex, slotDrawable)
                itemIndex++
            } else if (component is DefaultFluidMeter) {
                fluidStacks.init(fluidIndex, isInput, component.posX, component.posY, component.width, component.height, component.component.fluid.capacity, false, if (isInput) fluidInputOverlay else fluidOutputOverlay)
                fluidIndex++
            }
        }

        itemStacks.set(ingredients)
        fluidStacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        tabDrawable.draw(minecraft)
    }

    override fun getTitle(): String {
        return block.localizedName
    }

    class MachineRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}