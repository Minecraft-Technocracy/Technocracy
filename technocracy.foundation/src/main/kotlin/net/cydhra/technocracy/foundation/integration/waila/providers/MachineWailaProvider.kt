package net.cydhra.technocracy.foundation.integration.waila.providers

import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import mcp.mobius.waila.api.IWailaDataProvider
import mcp.mobius.waila.api.SpecialChars
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorageStategy
import net.cydhra.technocracy.foundation.tileentity.api.TCAggregatable
import net.cydhra.technocracy.foundation.tileentity.components.ComponentType
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
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
                                tooltip.add(SpecialChars.getRenderString("technocracy.energy",
                                        componentTag.getInteger(DynamicEnergyStorageStategy.KEY_CURRENT_AMOUNT).toString(),
                                        componentTag.getInteger(DynamicEnergyStorageStategy.KEY_CAPACITY).toString()))
                            }
                        }
                        ComponentType.FLUID -> {
                        }
                        ComponentType.PIPE_TYPES -> {
                        }
                        ComponentType.HEAT -> {
                        }
                        ComponentType.UPGRADES -> {
                        }
                        ComponentType.REDSTONE_MODE -> {
                        }
                    }
                }
        return tooltip
    }

    override fun getNBTData(player: EntityPlayerMP, te: TileEntity, tag: NBTTagCompound, world: World,
                            pos: BlockPos): NBTTagCompound {
        if (te is TCAggregatable) {
            te.generateNbtUpdateCompound(player, tag)
        }
        return tag
    }

}