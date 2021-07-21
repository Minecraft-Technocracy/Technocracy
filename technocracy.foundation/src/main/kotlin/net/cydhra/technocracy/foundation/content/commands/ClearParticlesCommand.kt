package net.cydhra.technocracy.foundation.content.commands

import net.cydhra.technocracy.foundation.api.fx.TCParticleManager
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraftforge.client.IClientCommand


class ClearParticlesCommand : CommandBase(), IClientCommand {
    override fun getName(): String {
        return "clearParticles"
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        TCParticleManager.particles.clear()
    }

    override fun getUsage(sender: ICommandSender): String {
        return "$name"
    }

    override fun allowUsageWithoutPrefix(sender: ICommandSender?, message: String?): Boolean {
        return false
    }
}