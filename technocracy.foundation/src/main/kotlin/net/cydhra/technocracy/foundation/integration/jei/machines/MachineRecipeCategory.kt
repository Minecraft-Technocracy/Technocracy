package net.cydhra.technocracy.foundation.integration.jei.machines

import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.gui.ITickTimer
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import net.cydhra.technocracy.foundation.client.gui.components.ITCComponent
import net.cydhra.technocracy.foundation.client.gui.components.energymeter.DefaultEnergyMeter
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.client.gui.components.slot.TCSlotIO
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.AbstractRecipeWrapper
import net.cydhra.technocracy.foundation.integration.jei.gui.FluidBackgroundDrawable
import net.cydhra.technocracy.foundation.integration.jei.gui.FluidOverlayDrawable
import net.cydhra.technocracy.foundation.integration.jei.gui.TabDrawable
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
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
    private val stolenComponents = mutableListOf<ITCComponent>() // component, isInput
    private val timer: ITickTimer

    init {
        tabDrawable.tab?.components?.forEach { guiComponent ->
            if (guiComponent is DefaultFluidMeter) {
                stolenComponents.add(guiComponent)
                //stolenComponents[guiComponent] = guiComponent.component.fluid.tanktype == DynamicFluidCapability.TankType.INPUT
            } else if (guiComponent is Slot) {
                if (guiComponent is TCSlotIO) {
                    stolenComponents.add(guiComponent)
                }
            } else if (guiComponent is DefaultEnergyMeter) {
                guiComponent.component.energyStorage.forceUpdateOfCurrentEnergy(guiComponent.component.energyStorage.maxEnergyStored)
                guiComponent.update()
            }
        }

        timer = guiHelper.createTickTimer(40,40,false)

        stolenComponents.forEach {
            // stolen components are handled by jei
            tabDrawable.tab?.components?.remove(it)
        }
    }


    override fun setRecipe(layout: IRecipeLayout, wrapper: MachineRecipeWrapper, ingredients: IIngredients) {
        val itemStacks = layout.itemStacks
        val fluidStacks = layout.fluidStacks
        val inputFluids = ingredients.getInputs(VanillaTypes.FLUID)
        val outputFluids = ingredients.getOutputs(VanillaTypes.FLUID)

        var itemIndex = 0
        var fluidIndex = 0

        stolenComponents.forEach {
            if (it is TCSlotIO) {
                itemStacks.init(itemIndex, it.type != DynamicInventoryCapability.InventoryType.OUTPUT, it.xPos, it.yPos)
                itemStacks.setBackground(itemIndex, slotDrawable)
                itemIndex++
            } else if (it is DefaultFluidMeter) {
                val input = it.component.fluid.tanktype != DynamicFluidCapability.TankType.OUTPUT

                val amount = if (input) {
                    if (inputFluids.size > fluidIndex) {
                        inputFluids[fluidIndex][0].amount
                    } else {
                        1000
                    }
                } else {
                    if (outputFluids.size > fluidIndex - inputFluids.size) {
                        outputFluids[fluidIndex - inputFluids.size][0].amount
                    } else {
                        1000
                    }
                }

                fluidStacks.init(fluidIndex, input, it.posX + 1, it.posY + 1, it.width - 2, it.height - 2,
                        amount, false, FluidOverlayDrawable(it))
                fluidStacks.setBackground(fluidIndex, FluidBackgroundDrawable(it))

                fluidIndex++
            }
        }

        itemStacks.set(ingredients)
        fluidStacks.set(ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        tabDrawable.deltaTime = timer.value / timer.maxValue.toFloat()
        tabDrawable.draw(minecraft)
    }

    override fun getTitle(): String {
        return block.localizedName
    }

    class MachineRecipeWrapper(inputStacks: List<List<ItemStack>>, outputStacks: List<ItemStack>, inputFluidStacks: List<FluidStack>, outputFluidStacks: List<FluidStack>)
        : AbstractRecipeWrapper(inputStacks, outputStacks, inputFluidStacks, outputFluidStacks)

}