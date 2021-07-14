package net.cydhra.technocracy.foundation.integration.jei

import mezz.jei.api.IJeiRuntime
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.client.gui.TCClientGuiImpl
import net.cydhra.technocracy.foundation.client.gui.components.progressbar.ProgressBar
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.integration.jei.gui.TCGuiHandler
import net.cydhra.technocracy.foundation.integration.jei.machines.MachineRecipeCategory
import net.cydhra.technocracy.foundation.integration.jei.multiblocks.RefineryRecipeCategory
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.config.GuiUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Mouse

@JEIPlugin
class TechnocracyPlugin : IModPlugin {

    private val categories = mutableListOf<AbstractRecipeCategory<*>>()
    private val tileCategoryMap = mutableMapOf<Class<out TileEntity>, AbstractRecipeCategory<*>>()

    lateinit var jeiRuntime: IJeiRuntime

    override fun registerCategories(registry: IRecipeCategoryRegistration) {

        MinecraftForge.EVENT_BUS.register(this)

        val guiHelper = registry.jeiHelpers.guiHelper

        // automatically add categories for machines
        RecipeManager.RecipeType.values().forEach { recipeType ->
            if (recipeType.machineBlock != null && recipeType.tileEntityClass != null) {
                val category = MachineRecipeCategory(
                    guiHelper,
                    recipeType.tileEntityClass.getDeclaredConstructor().newInstance(),
                    recipeType,
                    "${TCFoundation.MODID}.${recipeType.toString().toLowerCase()}",
                    recipeType.machineBlock
                )
                categories.add(category)
                tileCategoryMap[recipeType.tileEntityClass] = category
            }
        }

        // manually add categories for multiblocks
        categories.add(RefineryRecipeCategory(guiHelper))

        categories.forEach { category ->
            registry.addRecipeCategories(category)
        }
    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        this.jeiRuntime = jeiRuntime
    }

    @SubscribeEvent
    fun onDrawScreenEventPost(event: GuiScreenEvent.DrawScreenEvent.Post) {
        val gui = event.gui
        val mc = gui.mc ?: return

        if (gui is TCClientGuiImpl) {
            for (component in gui.simpleGui.getActiveTab().components) {
                if (component is ProgressBar) {

                    if (component.isMouseOnComponent(event.mouseX - gui.guiX, event.mouseY - gui.guiY)) {
                        val showRecipesText: String = translateToLocal("jei.tooltip.show.recipes")

                        val scaledresolution = ScaledResolution(mc)
                        GuiUtils.drawHoveringText(
                            ItemStack.EMPTY,
                            mutableListOf(showRecipesText),
                            event.mouseX, event.mouseY,
                            scaledresolution.scaledWidth,
                            scaledresolution.scaledHeight,
                            -1,
                            mc.fontRenderer
                        )
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onGuiMouseEvent(event: GuiScreenEvent.MouseInputEvent.Pre) {
        val guiScreen = event.gui
        val minecraft = guiScreen.mc
        if (minecraft != null) {
            val x = Mouse.getEventX() * guiScreen.width / minecraft.displayWidth
            val y = guiScreen.height - Mouse.getEventY() * guiScreen.height / minecraft.displayHeight - 1
            if (handleMouseEvent(guiScreen, x, y)) {
                event.isCanceled = true
            }
        }
    }

    fun handleMouseEvent(guiScreen: GuiScreen, mouseX: Int, mouseY: Int): Boolean {
        var cancelEvent = false
        val eventButton = Mouse.getEventButton()
        if (eventButton > -1) {
            if (Mouse.getEventButtonState()) {
                return handleMouseClick(guiScreen, eventButton, mouseX, mouseY)
            }
        }
        return cancelEvent
    }

    private fun handleMouseClick(gui: GuiScreen, mouseButton: Int, mouseX: Int, mouseY: Int): Boolean {
        if (mouseButton != 0) return false

        if (gui is TCClientGuiImpl && gui.simpleGui.container.provider is TileEntity) {
            for (component in gui.simpleGui.getActiveTab().components) {
                if (component is ProgressBar) {
                    if (component.isMouseOnComponent(mouseX - gui.guiX, mouseY - gui.guiY)) {
                        val cat =
                            tileCategoryMap[(gui.simpleGui.container.provider as TileEntity).javaClass] ?: continue
                        jeiRuntime.recipesGui.showCategories(listOf(cat.categoryUid))
                        return true
                    }
                }
            }
        }

        return false
    }

    fun translateToLocal(key: String): String {
        return if (I18n.canTranslate(key)) {
            I18n.translateToLocal(key)
        } else {
            I18n.translateToFallback(key)
        }
    }

    override fun register(registry: IModRegistry) {
        categories.forEach { category ->
            registry.addRecipes(loadRecipes(category.recipeType, category.recipeWrapperClass), category.categoryUid)
            registry.addRecipeCatalyst(ItemStack(category.displayBlock), category.categoryUid)
        }

        registry.addAdvancedGuiHandlers(TCGuiHandler)
    }

    private fun loadRecipes(
        type: RecipeManager.RecipeType,
        wrapperClass: Class<out AbstractRecipeWrapper>
    ): List<AbstractRecipeWrapper> {
        val recipes = mutableListOf<AbstractRecipeWrapper>()
        RecipeManager.getMachineRecipesByType(type)?.forEach { recipe ->

            val inputStacks = mutableListOf<List<ItemStack>>()
            recipe.getInput().forEach { ingredient ->
                val oreDictInputs = mutableListOf<ItemStack>()
                oreDictInputs.addAll(ingredient.matchingStacks)
                inputStacks.add(oreDictInputs)
            }

            recipes.add(
                wrapperClass.getDeclaredConstructor(
                    List::class.java,
                    List::class.java,
                    List::class.java,
                    List::class.java
                )
                    .newInstance(
                        inputStacks,
                        recipe.getOutput(),
                        recipe.getFluidInput(),
                        recipe.getFluidOutput()
                    ) as AbstractRecipeWrapper
            )
        }
        return recipes
    }

}