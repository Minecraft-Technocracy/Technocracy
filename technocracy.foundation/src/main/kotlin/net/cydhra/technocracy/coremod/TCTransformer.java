package net.cydhra.technocracy.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class TCTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(transformedName.equals("net.minecraft.client.renderer.ItemRenderer")) {
            ClassNode node = getNode(basicClass);
            for(MethodNode method : node.methods) {
                if(method.name.equals("renderItemSide")) {

                    System.out.println("Transformed ItemRenderer.renderItemSide");

                    InsnList ins = new InsnList();
                    ins.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
                    ins.add(new TypeInsnNode(Opcodes.NEW, "net/cydhra/technocracy/coremod/event/RenderItemSideEvent"));
                    ins.add(new InsnNode(Opcodes.DUP));
                    ins.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    ins.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    ins.add(new VarInsnNode(Opcodes.ALOAD, 3));
                    ins.add(new VarInsnNode(Opcodes.ILOAD, 4));
                    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/cydhra/technocracy/coremod/event/RenderItemSideEvent", "<init>", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V", false));
                    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
                    LabelNode label = new LabelNode();
                    ins.add(new JumpInsnNode(Opcodes.IFEQ, label));
                    ins.add(new InsnNode(Opcodes.RETURN));
                    ins.add(label);
                    method.instructions.insert(ins);
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
