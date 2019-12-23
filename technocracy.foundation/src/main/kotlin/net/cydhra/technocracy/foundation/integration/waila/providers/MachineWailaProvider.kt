package net.cydhra.technocracy.foundation.integration.waila.providers

import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import mcp.mobius.waila.api.IWailaDataProvider
import mcp.mobius.waila.api.SpecialChars
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.content.capabilities.energy.DynamicEnergyStorageStrategy
import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatable
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants

class MachineWailaProvider : IWailaDataProvider {

    override fun getWailaBody(itemStack: ItemStack, tooltip: MutableList<String>, accessor: IWailaDataAccessor,
                              config: IWailaConfigHandler): MutableList<String> {
        if (!accessor.nbtData.hasKey(TCFoundation.MODID))
            return tooltip

        val tag: NBTTagCompound = accessor.nbtData.getCompoundTag(TCFoundation.MODID)
        val componentList = tag.getTagList("list", Constants.NBT.TAG_COMPOUND)
        tag.keySet
                .filter { it != "list" }
                .forEach { componentName ->
                    val componentIndex = tag.getCompoundTag(componentName)
                    val componentTag = componentList.get(componentIndex.getInteger("index")) as NBTTagCompound
                    val type = componentIndex.getInteger("type")

                    when (ComponentType.values()[type]) {
                        ComponentType.ENERGY -> {
                            if (config.getConfig("capability.energyinfo")) {
                                tooltip.add(SpecialChars.getRenderString("${TCFoundation.MODID}.energy",
                                        componentTag.getInteger(DynamicEnergyStorageStrategy.KEY_CURRENT_AMOUNT).toString(),
                                        componentTag.getInteger(DynamicEnergyStorageStrategy.KEY_CAPACITY).toString()))
                            }
                        }
                        ComponentType.FLUID -> {
                            if (config.getConfig("capability.tankinfo")) {
                                tooltip.add(SpecialChars.getRenderString("${TCFoundation.MODID}.fluid",
                                        componentTag.getInteger("Amount").toString(),
                                        componentTag.getInteger("Capacity").toString(),
                                        componentTag.getString("FluidName")))
                            }
                        }
                        ComponentType.INVENTORY -> {
                            val items = componentTag.getTagList("Items", Constants.NBT.TAG_COMPOUND)
                            items.forEach {
                                val compound = it as NBTTagCompound
                                tooltip.add(SpecialChars.getRenderString("${TCFoundation.MODID}.item", compound.getString("id"), compound.getInteger("Count").toString()))
                            }
                        }
                        else -> {
                        }
                    }
                }

        return tooltip
    }

    override fun getNBTData(player: EntityPlayerMP, te: TileEntity, tag: NBTTagCompound, world: World,
                            pos: BlockPos): NBTTagCompound {
        if (te is TileEntityMultiBlockPart<*>) {
            if(te.multiblockController == null || !te.multiblockController!!.isAssembled) return tag
            val compound = NBTTagCompound()
            val components = NBTTagList()
            (te.multiblockController as BaseMultiBlock).getComponents()
                    .filter { it.second.type.supportsWaila }
                    .forEach { (name, component) ->
                        val index = NBTTagCompound()
                        index.setInteger("index", components.tagCount())
                        index.setInteger("type", component.type.ordinal)

                        compound.setTag(name, index)
                        components.appendTag(component.serializeNBT())
                    }

            compound.setTag("list", components)
            tag.setTag(TCFoundation.MODID, compound)
        } else if (te is TCAggregatable) {
            te.generateNbtUpdateCompound(player, tag)
        }
        return tag
    }

}