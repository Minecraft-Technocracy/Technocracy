package net.cydhra.technocracy.foundation.content.commands

import net.cydhra.technocracy.foundation.util.structures.Template
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer


class PasteTemplateCommand : CommandBase() {
    override fun getName(): String {
        return "pasteTemplate"
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        val tmp = Template()
        tmp.loadFromAssets(args[0])

        val player = sender.commandSenderEntity!!
        val length = 10.0

        val trace = player.rayTrace(10.0, 0f)
        val pos = trace?.blockPos ?: player.position//player.getPositionEyes(0f).addVector(player.lookVec.x * length, player.lookVec.y * length, player.lookVec.z * length)

        for(info in tmp.blocks) {
            val state = if(info.meta != -1) info.block.getStateFromMeta(info.meta) else info.block.defaultState
            player.world.setBlockState(info.pos.add(pos.x, pos.y, pos.z), state)
        }
    }

    override fun getUsage(sender: ICommandSender): String {
        return "$name"
    }
}