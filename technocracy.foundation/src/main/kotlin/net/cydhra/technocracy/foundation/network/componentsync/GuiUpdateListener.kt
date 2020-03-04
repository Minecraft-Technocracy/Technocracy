package net.cydhra.technocracy.foundation.network.componentsync

import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.model.components.IAggregatable
import net.cydhra.technocracy.foundation.model.components.IComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.event.entity.player.PlayerContainerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side

val guiInfoPacketSubscribers = mutableMapOf<EntityPlayerMP, TCContainer>()
var tileEntityData = mutableMapOf<TCContainer, NBTTagCompound>()

object GuiUpdateListener {

    @SubscribeEvent
    fun onInventoryClose(event: PlayerContainerEvent.Close) {
        tileEntityData.remove(guiInfoPacketSubscribers[event.entityPlayer])
        guiInfoPacketSubscribers.remove(event.entityPlayer)
    }

    @SubscribeEvent
    fun onInventoryOpen(event: PlayerContainerEvent.Open) {
        val cont = event.container
        if(cont is TCContainer) {
            guiInfoPacketSubscribers[event.entityPlayer as EntityPlayerMP] = cont
        }
    }

    fun syncComponentsToClients() {
        guiInfoPacketSubscribers.forEach { (player, container) ->
            val te = container.tileEntity
            if(te != null) {

                var tag: NBTTagCompound? = null
                if (te is IAggregatable) {
                    tag = getTagForMachine(te.getComponents())
                } else if (te is TileEntityMultiBlockPart<*>) {
                    if(te.multiblockController != null)
                        tag = getTagForMachine((te.multiblockController as BaseMultiBlock).getComponents())
                }

                //val tag = te.updateTag
                if(tag != null && tileEntityData[container] != tag) {
                    tileEntityData[container] = tag
                    PacketHandler.sendToClient(MachineInfoPacket(tag), player)
                }
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent) {
        if (event.side == Side.SERVER) {

            //tileEntityData = tileEntityData.filter { !it.key.isInvalid }.toMutableMap()

            syncComponentsToClients()

            /*guiInfoPacketSubscribers.forEach { (player, tePos) ->
                val world = DimensionManager.getWorld(tePos.second)
                if (world.isBlockLoaded(tePos.first)) {
                    val te = world.getTileEntity(tePos.first)
                    var tag: NBTTagCompound? = null
                    if (te is MachineTileEntity) {
                        tag = getTagForMachine(te.getComponents())
                    } else if (te is TileEntityMultiBlockPart<*>) {
                        if(te.multiblockController != null)
                            tag = getTagForMachine((te.multiblockController as BaseMultiBlock).getComponents())
                    }

                    if (tag != null) {
                        tag.setLong("pos", tePos.first.toLong())
                        lastGuiStates.putIfAbsent(player, mutableMapOf())
                        if (lastGuiStates[player]!![tePos] != tag) {
                            lastGuiStates[player]!![tePos] = tag
                            PacketHandler.sendToClient(MachineInfoPacket(tag), player)
                        }
                    }
                }
            }*/
        }
    }

    fun getTagForMachine(components: MutableList<Pair<String, IComponent>>): NBTTagCompound {
        val tag = NBTTagCompound()
        components.forEach { (name, component) ->
            tag.setTag(name, component.serializeNBT())
        }
        return tag
    }

}