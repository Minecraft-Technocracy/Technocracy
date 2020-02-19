package net.cydhra.technocracy.astronautics.content.tileentity

import com.sun.org.apache.xpath.internal.operations.Bool
import net.cydhra.technocracy.astronautics.client.gui.TabPlanetaryRendering
import net.cydhra.technocracy.astronautics.content.entity.EntityRocket
import net.cydhra.technocracy.astronautics.content.fx.ParticleSmoke
import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCIcon
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.client.gui.components.fluidmeter.DefaultFluidMeter
import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.OwnerShipTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableTileEntity
import net.cydhra.technocracy.foundation.util.Interpolator
import net.cydhra.technocracy.foundation.util.color
import net.cydhra.technocracy.foundation.util.opengl.BasicShaderProgram
import net.cydhra.technocracy.foundation.util.opengl.OpenGLBoundingBox
import net.cydhra.technocracy.foundation.util.opengl.VAO
import net.cydhra.technocracy.foundation.util.opengl.VBO
import net.cydhra.technocracy.foundation.util.pos
import net.cydhra.technocracy.foundation.util.validateAndClear
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import net.minecraft.client.shader.ShaderGroup
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.ARBCullDistance
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL31
import org.lwjgl.util.glu.Project
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f
import java.nio.FloatBuffer
import kotlin.math.*
import kotlin.random.Random


class TileEntityRocketController : AggregatableTileEntity(), TEInventoryProvider, TCTileEntityGuiProvider, DynamicInventoryCapability.CustomItemStackStackLimit {

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {
    }

    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun getStackLimit(slot: Int, stack: ItemStack, default: Int): Int {
        if (currentRocket != null) {
            if (!currentRocket!!.dysonCargo)
                return 1
        }
        //Todo limit some kinds of cargo to one per slot
        //current dyson cargo is 16 per cargo element, max rocket is 6 modules * 8 storage slots * 16 items = 768 max dyson parts per rocket
        return 16//default
    }

    val ownerShip = OwnerShipTileEntityComponent()
    val dynCapability = DynamicFluidCapability(0, mutableListOf("rocket_fuel"))
    val fluidBuffer = FluidTileEntityComponent(dynCapability, EnumFacing.values().toMutableSet())
    val inventoryBuffer = InventoryTileEntityComponent(0, this, EnumFacing.values().toMutableSet())

    var currentRocket: EntityRocket? = null

    fun linkToCurrentRocket(rocket: EntityRocket): Boolean {
        if (currentRocket == null || currentRocket!!.isDead) {
            currentRocket = rocket

            //forward capability to entity
            fluidBuffer.fluid = rocket.tank.fluid

            inventoryBuffer.inventory.stacks = rocket.cargoSlots!!
            inventoryBuffer.inventory.forceSlotTypes(DynamicInventoryCapability.InventoryType.BOTH)

            return true
        }
        return false
    }


    override fun getGui(player: EntityPlayer?): TCGui {

        val gui = TCGui(guiHeight = 230, container = TCContainer(1, 1))
        gui.registerTab(object : TCTab("${getBlockType().localizedName} linked: ${currentRocket != null}", gui, -1, TCIcon(this.blockType)) {

            override fun init() {

                val fm = DefaultFluidMeter(10, 25, fluidBuffer, gui)
                fm.width = 20
                fm.height = 105

                components.add(fm)

                if (player != null) {
                    //stick to bottom
                    addPlayerInventorySlots(player, 8, gui.origHeight - 58 - 16 - 5 - 12)
                }
            }
        })

        gui.registerTab(TabPlanetaryRendering(gui))

        return gui
    }

    fun render() {

    }

    fun unlinkRocket() {
        currentRocket = null
        fluidBuffer.fluid = dynCapability
        inventoryBuffer.inventory.stacks = NonNullList.withSize(0, ItemStack.EMPTY)
    }

    init {
        registerComponent(ownerShip, "ownership")
        registerComponent(fluidBuffer, "fluidBuffer")
        registerComponent(inventoryBuffer, "invBuffer")
        //no need to save or update as it only references to the entity
        fluidBuffer.allowAutoSave = false
        inventoryBuffer.allowAutoSave = false
    }
}