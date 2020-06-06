package net.cydhra.technocracy.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;

public class TCTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (transformedName.equals("net.minecraft.client.renderer.ItemRenderer")) {
            ClassNode node = getNode(basicClass);
            for (MethodNode method : node.methods) {
                if (method.name.equals("renderItemSide")) {

                    System.out.println("Transformed ItemRenderer.renderItemSide");

                    InsnList ins = new InsnList();
                    //get event bus
                    ins.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));

                    //generate new RenderItemSideEvent
                    ins.add(new TypeInsnNode(Opcodes.NEW, "net/cydhra/technocracy/coremod/event/RenderItemSideEvent"));
                    ins.add(new InsnNode(Opcodes.DUP));
                    //entity
                    ins.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    //itemstack
                    ins.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    //transformtype
                    ins.add(new VarInsnNode(Opcodes.ALOAD, 3));
                    //lefthand
                    ins.add(new VarInsnNode(Opcodes.ILOAD, 4));
                    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/cydhra/technocracy/coremod/event/RenderItemSideEvent", "<init>", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V", false));
                    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));

                    //if post return is true return from method
                    LabelNode label = new LabelNode();
                    ins.add(new JumpInsnNode(Opcodes.IFEQ, label));
                    ins.add(new InsnNode(Opcodes.RETURN));
                    ins.add(label);
                    method.instructions.insert(ins);
                    break;
                }
            }
            return getBytes(node);
        } else if (transformedName.equals("net.minecraft.util.CooldownTracker")) {
            ClassNode node = getNode(basicClass);
            for (MethodNode method : node.methods) {
                if (method.name.equals("setCooldown")) {
                    System.out.println("Transformed CooldownTracker.setCooldown");

                    InsnList ins = new InsnList();
                    //crate event
                    ins.add(new TypeInsnNode(Opcodes.NEW, "net/cydhra/technocracy/coremod/event/ItemCooldownEvent"));
                    ins.add(new InsnNode(Opcodes.DUP));
                    //this
                    ins.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    //item
                    ins.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    //delay
                    ins.add(new VarInsnNode(Opcodes.ILOAD, 2));
                    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/cydhra/technocracy/coremod/event/ItemCooldownEvent", "<init>", "(Lnet/minecraft/util/CooldownTracker;Lnet/minecraft/item/Item;I)V", false));

                    //store event in new variable
                    int stack = method.maxStack++;
                    ins.add(new VarInsnNode(Opcodes.ASTORE, stack));

                    //get eventbus
                    ins.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
                    //load and call event
                    ins.add(new VarInsnNode(Opcodes.ALOAD, stack));
                    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
                    //pop boolean from post
                    ins.add(new InsnNode(Opcodes.POP));

                    //load event
                    ins.add(new VarInsnNode(Opcodes.ALOAD, stack));
                    //getDelay and save it in local delay
                    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/cydhra/technocracy/coremod/event/ItemCooldownEvent", "getDelay", "()I", false));
                    ins.add(new VarInsnNode(Opcodes.ISTORE, 2));

                    //load event
                    ins.add(new VarInsnNode(Opcodes.ALOAD, stack));
                    //getDelay and save it in local delay
                    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/cydhra/technocracy/coremod/event/ItemCooldownEvent", "getItem", "()Lnet/minecraft/item/Item;", false));
                    ins.add(new VarInsnNode(Opcodes.ASTORE, 1));

                    method.instructions.insert(ins);

                    break;
                }
            }
            return getBytes(node);
        } else if (transformedName.equals("net.minecraft.client.renderer.EntityRenderer")) {
            ClassNode node = getNode(basicClass);
            for (MethodNode method : node.methods) {
                if (method.name.equals("updateCameraAndRender")) {

                    for(AbstractInsnNode ain : method.instructions.toArray()) {
                        if(ain instanceof MethodInsnNode) {


                            /*ObfuscationReflectionHelper.remapFieldNames()
                            FMLDeobfuscatingRemapper.INSTANCE.map("net.minecraft.client.renderer.RenderGlobal")
                            FMLDeobfuscatingRemapper.INSTANCE.mapMethodName("net.minecraft.client.renderer.RenderGlobal", "", "")

                            if(((MethodInsnNode) ain).name == )*/

                            if (((MethodInsnNode) ain).name.equals("renderEntityOutlineFramebuffer")) {

                                System.out.println("Transformed EntityRenderer.updateCameraAndRender");


                                InsnList ins = new InsnList();

                                //get eventbus
                                ins.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));

                                //crate event
                                ins.add(new TypeInsnNode(Opcodes.NEW, "net/cydhra/technocracy/coremod/event/RenderShaderEvent"));
                                ins.add(new InsnNode(Opcodes.DUP));
                                //partialTicks
                                ins.add(new VarInsnNode(Opcodes.FLOAD, 1));
                                ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/cydhra/technocracy/coremod/event/RenderShaderEvent", "<init>", "(F)V", false));
                                //load and call event
                                ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
                                ins.add(new InsnNode(Opcodes.POP));

                                method.instructions.insertBefore(ain, ins);

                                break;
                            }
                        }
                    }
                }
            }
            return getBytes(node);
        }
        return basicClass;
    }

    private ClassNode getNode(byte[] basicClass) {
        ClassNode cn = new ClassNode();
        ClassReader reader = new ClassReader(basicClass);
        reader.accept(cn, 0);
        return cn;
    }

    private byte[] getBytes(ClassNode cn) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(writer);
        return writer.toByteArray();
    }


}
