package net.cydhra.technocracy.foundation.content.commands

import net.cydhra.technocracy.foundation.model.fx.manager.TCParticleManager
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer


class ClearParticlesCommand : CommandBase() {
    override fun getName(): String {
        return "clearParticles"
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        TCParticleManager.particles.clear()
    }

    override fun getUsage(sender: ICommandSender): String {
        return "$name"
    }
}