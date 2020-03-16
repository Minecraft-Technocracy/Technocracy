package net.cydhra.technocracy.foundation.conduits.types

import net.cydhra.technocracy.foundation.content.capabilities.energy.EnergyCapabilityProvider
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler


enum class PipeType(val unlocalizedName: String,
                    val capability: Capability<*>,
                    val offersContent: (world: WorldServer, pos: BlockPos, facing: EnumFacing) -> Boolean,
                    val getContent: (world: WorldServer, pos: BlockPos, facing: EnumFacing, limit: Int) -> PipeContent)
    : IStringSerializable {
    ENERGY("energy",
            EnergyCapabilityProvider.CAPABILITY_ENERGY!!,
            { world, pos, facing ->
                world.getTileEntity(pos)
                        ?.getCapability(EnergyCapabilityProvider.CAPABILITY_ENERGY!!, facing)
                        ?.let { it.canExtract() && it.energyStored > 0 } ?: false
            },
            { world, pos, facing, limit ->
                world.getTileEntity(pos)
                        ?.getCapability(EnergyCapabilityProvider.CAPABILITY_ENERGY!!, facing)!!
                        .let { PipeEnergyContent(it, it.extractEnergy(limit, true)) }

            }),
    FLUID("fluid", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
            { world, pos, facing ->
                world.getTileEntity(pos)
                        ?.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY!!, facing)
                        ?.drain(1, false)?.amount ?: 0 > 0
            },
            { world, pos, facing, limit ->
                TODO()
            }),
    ITEM("item", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
            { world, pos, facing ->
                world.getTileEntity(pos)
                        ?.getCapability(net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY!!, facing)
                        ?.let { itemCap ->
                            (0 until itemCap.slots).any { !itemCap.extractItem(it, 1, true).isEmpty }
                        } ?: false
            },
            { world, pos, facing, limit ->
                TODO()
            });

    override fun getName(): String {
        return this.unlocalizedName
    }
}