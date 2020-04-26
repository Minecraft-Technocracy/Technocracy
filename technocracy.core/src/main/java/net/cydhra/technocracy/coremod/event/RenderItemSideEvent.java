package net.cydhra.technocracy.coremod.event;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderItemSideEvent extends Event {

    private EntityLivingBase entitylivingbase;
    private ItemStack stack;
    private ItemCameraTransforms.TransformType transform;
    private boolean leftHanded;

    public RenderItemSideEvent(EntityLivingBase entitylivingbase, ItemStack stack, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
        this.entitylivingbase = entitylivingbase;
        this.stack = stack;
        this.transform = transform;
        this.leftHanded = leftHanded;
    }


    public ItemStack getStack() {
        return stack;
    }

    public ItemCameraTransforms.TransformType getTransform() {
        return transform;
    }

    public boolean isLeftHanded() {
        return leftHanded;
    }

    public EntityLivingBase getEntitylivingbase() {
        return entitylivingbase;
    }
}
