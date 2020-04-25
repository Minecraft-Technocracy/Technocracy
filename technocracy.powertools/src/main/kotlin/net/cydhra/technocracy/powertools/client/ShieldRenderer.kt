package net.cydhra.technocracy.powertools.client

import net.cydhra.technocracy.coremod.event.RenderItemSideEvent
import net.cydhra.technocracy.foundation.client.shader.RefractionEffect
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.model.ModelShield
import net.minecraft.client.renderer.BannerTextures
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.util.vector.Vector2f

@SideOnly(Side.CLIENT)
object ShieldRenderer : TileEntityItemStackRenderer() {

    private val modelShield = object : ModelBase() {
        val handle: ModelRenderer
        val plate: ModelRenderer
        val plateItem: ModelRenderer

        init {
            this.textureWidth = 64
            this.textureHeight = 64
            handle = ModelRenderer(this, 0, 0)
            handle.addBox(-7.0f, -1.5f, -0.5f, 10, 3, 2, 0.0f)

            plate = ModelRenderer(this, 0, 0).setTextureSize(64,64)
            plate.addBox(-8.0f, -11.0f, -1f, 12, 22, 1, 0.0f)

            this.textureWidth = 512
            this.textureHeight = 512

            plateItem = ModelRenderer(this, 0, 0).setTextureSize(10,10)
            plateItem.addBox(-8.0f, -11.0f, -1f, 12, 22, 1, 0.0f)
        }
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    var time = Vector2f(0f, 0f)

    @SubscribeEvent
    fun update(event: TickEvent.ClientTickEvent) {
        time.x++
        time.y++
        time.y += 0.5f
    }

    @SubscribeEvent
    fun renderItem(event: RenderItemSideEvent) {
        //todo fix stuff not beeing rendered to fbo by rendering the held items on our own
        if (event.stack.item.unlocalizedName == "item.energy_shield") {
            val entity = event.entitylivingbase

            val usedShield = (entity as? EntityPlayer)?.cooldownTracker?.hasCooldown(event.stack.item) ?: false

            Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation("technocracy.powertools", "textures/entity/energy_shield.png"))
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.enableRescaleNormal()
            GlStateManager.alphaFunc(516, 0.1f)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.pushMatrix()

            val ibakedmodel: IBakedModel = Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(event.stack, event.entitylivingbase.world, event.entitylivingbase)
            ForgeHooksClient.handleCameraTransforms(ibakedmodel, event.transform, event.isLeftHanded)

            GlStateManager.translate(-0.5f, -0.5f, -0.5f)

            GlStateManager.pushMatrix()
            GlStateManager.scale(1.0f, -1.0f, -1.0f)

            //plate2.addBox(-8.0f, -11.0f, -0.5f, 12, 22, 1, 0.0f)

            modelShield.handle.render(0.0625f)
            if (event.stack == event.entitylivingbase.activeItemStack || usedShield) {
                GlStateManager.depthMask(false)
                RefractionEffect.colorShift.uploadUniform(usedShield)
                //shift to a more red
                RefractionEffect.colorShiftAmount.uploadUniform(Math.toRadians(150.0))
                RefractionEffect.time.uploadUniform(time.x + Minecraft.getMinecraft().renderPartialTicks, time.y + Minecraft.getMinecraft().renderPartialTicks)

                RefractionEffect.preRenderType(ResourceLocation("technocracy.foundation", "textures/fx/hex_diffuse.png"), ResourceLocation("technocracy.foundation", "textures/fx/hex_normal.png"))
                RefractionEffect.updateBackBuffer()
                RefractionEffect.beginRendering()

                modelShield.plate.render(0.0625f)

                RefractionEffect.postRenderType()
                GlStateManager.depthMask(true)
            }
            GlStateManager.popMatrix()
            //this.modelShield.render()

            //renderItem(stack, bakedmodel)
            GlStateManager.cullFace(GlStateManager.CullFace.BACK)
            GlStateManager.popMatrix()
            GlStateManager.disableRescaleNormal()
            GlStateManager.disableBlend()



            event.isCanceled = true
        }
    }

    override fun renderByItem(stack: ItemStack, partialTicks: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.scale(1.0f, -1.0f, -1.0f)
        Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation("technocracy.powertools", "textures/entity/energy_shield.png"))
        this.modelShield.handle.render(0.0625f)
        Minecraft.getMinecraft().renderEngine.bindTexture(ResourceLocation("technocracy.foundation", "textures/fx/hex_diffuse.png"))
        this.modelShield.plateItem.render(0.0625f)
        GlStateManager.popMatrix()
    }
}