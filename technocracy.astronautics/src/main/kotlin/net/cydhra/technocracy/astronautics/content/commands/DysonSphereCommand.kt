package net.cydhra.technocracy.astronautics.content.commands

import net.cydhra.technocracy.astronautics.dyson.DysonSphereController
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import kotlin.math.roundToInt

class DysonSphereCommand : CommandBase() {
    override fun getName(): String = "dyson"

    override fun getUsage(sender: ICommandSender): String = "$name [<amount>]"

    override fun getRequiredPermissionLevel(): Int {
        return 4
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (args.size == 1) {
            val amount = try {
                args[0].toInt()
            } catch (e: NumberFormatException) {
                sender.sendMessage(TextComponentString("argument is not a number"))
                return
            }

            DysonSphereController.sphereAmount = amount / 100f
        } else if (args.size > 1) {
            sender.sendMessage(TextComponentString("Usage: ${getUsage(sender)}").setStyle(Style().setColor(TextFormatting.DARK_RED)))
            return
        }

        // in any case, report current progress
        sender.sendMessage(
                TextComponentString("Dyson sphere progress is ${(DysonSphereController.sphereAmount * 100).roundToInt()}%"))
    }

}