package net.cydhra.technocracy.foundation.content.commands

import net.cydhra.technocracy.foundation.client.model.facade.FacadeItemBakedModel
import net.cydhra.technocracy.foundation.client.model.pipe.FacadeBakery
import net.cydhra.technocracy.foundation.client.model.pipe.PipeItemRedirector
import net.cydhra.technocracy.foundation.client.model.pipe.PipeModelBakery
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraftforge.client.IClientCommand


class ClearTCCaches : CommandBase(), IClientCommand {
    override fun getUsage(sender: ICommandSender): String {
        return ""
    }

    override fun allowUsageWithoutPrefix(sender: ICommandSender?, message: String?): Boolean {
        return false
    }

    override fun getName(): String {
        return "clearTCCache"
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        FacadeBakery.ctmQuadCache.clear()
        FacadeBakery.quadCache.clear()
        PipeModelBakery.quadCache.clear()
        PipeItemRedirector.modelCache.clear()
    }

    override fun getAliases(): MutableList<String> {
        return mutableListOf("ctcc")
    }
}