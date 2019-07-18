package net.cydhra.technocracy.foundation.commands

import net.cydhra.technocracy.foundation.items.general.StructureMarkerItem
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString


class GenerateTemplateCommand : CommandBase() {
    override fun getName(): String {
        return "generateTemplate"
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (!server.isSinglePlayer) return

        if (StructureMarkerItem.controller == null)
            return sender.sendMessage(TextComponentString("Controller not set"))

        if (StructureMarkerItem.firstPos == null || StructureMarkerItem.firstPos == null)
            return sender.sendMessage(TextComponentString("Selection not valid"))

        if (args.isEmpty())
            return sender.sendMessage(TextComponentString("No name set"))


        val controller = StructureMarkerItem.controller!!
        val firstPos = StructureMarkerItem.firstPos!!
        val secondPos = StructureMarkerItem.secondPos!!
        val minX = Math.min(firstPos.x, secondPos.x)
        val maxX = Math.max(firstPos.x, secondPos.x)

        val minY = Math.min(firstPos.y, secondPos.y)
        val maxY = Math.max(firstPos.y, secondPos.y)

        val minZ = Math.min(firstPos.z, secondPos.z)
        val maxZ = Math.max(firstPos.z, secondPos.z)

        val template = net.cydhra.technocracy.foundation.util.structures.Template()
        template.generateTemplate(BlockPos(minX, minY, minZ), BlockPos(maxX, maxY, maxZ), controller, StructureMarkerItem.wildcard, sender.entityWorld, args[0])
    }

    override fun getUsage(sender: ICommandSender): String {
        return "$name <Name>"
    }
}