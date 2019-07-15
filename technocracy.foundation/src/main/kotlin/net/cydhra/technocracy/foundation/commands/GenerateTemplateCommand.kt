package net.cydhra.technocracy.foundation.commands

import net.cydhra.technocracy.foundation.items.general.StructureMarker
import net.cydhra.technocracy.foundation.items.general.structureMarker
import net.minecraft.block.Block
import net.minecraft.block.BlockStructure
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.init.Blocks
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.template.Template


class GenerateTemplateCommand : CommandBase() {
    override fun getName(): String {
        return "generateTemplate"
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (!server.isSinglePlayer) return

        if (StructureMarker.controller == null)
            return sender.sendMessage(TextComponentString("Controller not set"))

        if (StructureMarker.firstPos == null || StructureMarker.firstPos == null)
            return sender.sendMessage(TextComponentString("Selection not valid"))

        if (args.isEmpty())
            return sender.sendMessage(TextComponentString("No name set"))


        val controller = StructureMarker.controller!!
        val firstPos = StructureMarker.firstPos!!
        val secondPos = StructureMarker.secondPos!!
        val minX = Math.min(firstPos.x, secondPos.x)
        val maxX = Math.max(firstPos.x, secondPos.x)

        val minY = Math.min(firstPos.y, secondPos.y)
        val maxY = Math.max(firstPos.y, secondPos.y)

        val minZ = Math.min(firstPos.z, secondPos.z)
        val maxZ = Math.max(firstPos.z, secondPos.z)

        val template = net.cydhra.technocracy.foundation.util.structures.Template()
        template.generateTemplate(BlockPos(minX, minY, minZ), BlockPos(maxX, maxY, maxZ), controller, StructureMarker.wildcard, sender.entityWorld, args[0])
    }

    override fun getUsage(sender: ICommandSender): String {
        return "$name <Name>"
    }
}