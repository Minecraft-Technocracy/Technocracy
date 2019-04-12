package net.cydhra.technocracy.foundation.client.renderer.tileEntity

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity
import scala.reflect.internal.Mirrors.Roots.`RootSymbol$class`.mirror

class ConnectorModel : ModelBase() {
    var top: ModelRenderer
    var north: ModelRenderer
    var south: ModelRenderer
    var west: ModelRenderer
    var east: ModelRenderer
    var botom: ModelRenderer

    init {
        textureWidth = 64
        textureHeight = 32

        top = ModelRenderer(this, 0, 0)
        top.addBox(0f, 0f, 0f, 6, 6, 6)
        top.setRotationPoint(-3f, 8f, -3f)
        top.setTextureSize(32, 32)
        top.mirror = true
        setRotation(top, 0f, 0f, 0f)
        north = ModelRenderer(this, 0, 0)
        north.addBox(0f, 0f, 0f, 6, 6, 6)
        north.setRotationPoint(-3f, 13f, -8f)
        north.setTextureSize(32, 32)
        north.mirror = true
        setRotation(north, 0f, 0f, 0f)
        south = ModelRenderer(this, 0, 0)
        south.addBox(0f, 0f, 0f, 6, 6, 6)
        south.setRotationPoint(-3f, 13f, 2f)
        south.setTextureSize(32, 32)
        south.mirror = true
        setRotation(south, 0f, 0f, 0f)
        west = ModelRenderer(this, 0, 0)
        west.addBox(0f, 0f, 0f, 6, 6, 6)
        west.setRotationPoint(2f, 13f, -3f)
        west.setTextureSize(32, 32)
        west.mirror = true
        setRotation(west, 0f, 0f, 0f)
        east = ModelRenderer(this, 0, 0)
        east.addBox(-8f, 0f, -3f, 6, 6, 6)
        east.setRotationPoint(0f, 13f, 0f)
        east.setTextureSize(32, 32)
        east.mirror = true
        setRotation(east, 0f, 0f, 0f)
        botom = ModelRenderer(this, 0, 0)
        botom.addBox(-3f, 0f, -3f, 6, 6, 6)
        botom.setRotationPoint(0f, 18f, 0f)
        botom.setTextureSize(32, 32)
        botom.mirror = true
        setRotation(botom, 0f, 0f, 0f)
    }

    fun render(f5: Float) {
        top.render(f5)
        north.render(f5)
        south.render(f5)
        west.render(f5)
        east.render(f5)
        botom.render(f5)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }

    override fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, entity: Entity?) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
    }
}
