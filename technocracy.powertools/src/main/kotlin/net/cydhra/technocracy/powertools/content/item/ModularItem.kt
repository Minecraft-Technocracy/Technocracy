package net.cydhra.technocracy.powertools.content.item

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.api.ecs.IAggregatable
import net.cydhra.technocracy.foundation.api.tileentities.TCTileEntityGuiProvider
import net.cydhra.technocracy.foundation.api.upgrades.UpgradeClass
import net.cydhra.technocracy.foundation.client.gui.item.ItemUpgradesTab
import net.cydhra.technocracy.foundation.client.gui.SimpleGui
import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.container.TCContainer
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.content.items.components.ItemUpgradesComponent
import net.cydhra.technocracy.foundation.model.items.api.BaseItem
import net.cydhra.technocracy.foundation.model.items.capability.ItemCapabilityWrapper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider


class ModularItem : BaseItem("modularitem"), TCTileEntityGuiProvider {
    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        val wrapper = ItemCapabilityWrapper(stack, mutableMapOf("upgardeable" to ItemUpgradesComponent(3, UpgradeClass.TOOL)))

        return wrapper
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {

        if (!playerIn.isSneaking) {
            if (!worldIn.isRemote && handIn == EnumHand.MAIN_HAND) {
                playerIn.openGui(TCFoundation, TCGuiHandler.itemGui, worldIn, playerIn.posX.toInt(), playerIn.posY.toInt(), playerIn.posY.toInt())
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    override fun getGui(player: EntityPlayer?): TCGui {
        val stack = player?.heldItemMainhand!!
        val wrapped = stack.getCapability(ItemCapabilityWrapper.CAPABILITY_WRAPPER, null)!!
        val gui = SimpleGui(container = TCContainer(wrapped))
        //lock the current stack so it cant be moved
        gui.container.lockedStacks.add(stack)

        val upgradesComponent = wrapped.getComponents().firstOrNull { (_, c) -> c is ItemUpgradesComponent }?.second
        if (upgradesComponent != null) {
            gui.registerTab(ItemUpgradesTab(gui, upgradesComponent as ItemUpgradesComponent, player))
        }
        return gui
    }
}