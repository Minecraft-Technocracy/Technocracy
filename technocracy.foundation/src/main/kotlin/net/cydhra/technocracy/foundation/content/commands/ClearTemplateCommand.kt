package net.cydhra.technocracy.foundation.content.commands

import net.cydhra.technocracy.foundation.content.items.StructureMarkerItem
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer


class ClearTemplateCommand : CommandBase() {
    override fun getName(): String {
        return "clearTemplate"
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (!server.isSinglePlayer) return

        StructureMarkerItem.controller = null
        StructureMarkerItem.firstPos = null
        StructureMarkerItem.secondPos = null

        StructureMarkerItem.wildcard.clear()
        StructureMarkerItem.modules.clear()

    }

    override fun getUsage(sender: ICommandSender): String {
        return "$name"
    }
}