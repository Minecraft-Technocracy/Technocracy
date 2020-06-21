package net.cydhra.technocracy.foundation.content.commands

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraftforge.client.IClientCommand
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.client.resource.ReloadRequirements
import net.minecraftforge.client.resource.SelectiveReloadStateHandler
import net.minecraftforge.client.resource.VanillaResourceType
import net.minecraftforge.fml.client.FMLClientHandler


class ReloadShaderCommand : CommandBase(), IClientCommand {
    override fun getUsage(sender: ICommandSender): String {
        return ""
    }

    override fun allowUsageWithoutPrefix(sender: ICommandSender?, message: String?): Boolean {
        return false
    }

    override fun getName(): String {
        return "reloadShader"
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        val manager = Minecraft.getMinecraft().resourceManager
        SelectiveReloadStateHandler.INSTANCE.endReload()
        SelectiveReloadStateHandler.INSTANCE.beginReload(ReloadRequirements.include(VanillaResourceType.SHADERS))
        println("aaa")
        for (listener in (manager as SimpleReloadableResourceManager).reloadListeners) {
            if (listener is ISelectiveResourceReloadListener)
                listener.onResourceManagerReload(manager)
        }
        SelectiveReloadStateHandler.INSTANCE.endReload()
    }

    override fun getAliases(): MutableList<String> {
        return mutableListOf("rls")
    }
}