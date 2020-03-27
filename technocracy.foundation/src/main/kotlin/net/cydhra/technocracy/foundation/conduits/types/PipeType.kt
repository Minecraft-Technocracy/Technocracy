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
                    val getContent: (world: WorldServer, pos: BlockPos, facing: EnumFacing, limit: Int) -> PipeContent,
                    val acceptsContent: (world: WorldServer, pos: BlockPos, facing: EnumFacing, content: PipeContent) -> PipeContent)
    : IStringSerializable {
    ENERGY(unlocalizedName = "energy",
            capability = EnergyCapabilityProvider.CAPABILITY_ENERGY!!,
            offersContent = { world, pos, facing ->
                world.getTileEntity(pos)
                        ?.getCapability(EnergyCapabilityProvider.CAPABILITY_ENERGY!!, facing)
                        ?.let { it.canExtract() && it.energyStored > 0 } ?: false
            },
            getContent = { world, pos, facing, limit ->
                world.getTileEntity(pos)
                        ?.getCapability(EnergyCapabilityProvider.CAPABILITY_ENERGY!!, facing)!!
                        .let { PipeEnergyContent(it, it.extractEnergy(limit, true)) }

            },
            acceptsContent = { world, pos, facing, content -> content }),
    FLUID(unlocalizedName = "fluid", capability = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
            offersContent = { world, pos, facing ->
                world.getTileEntity(pos)
                        ?.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY!!, facing)
                        ?.drain(1, false)?.amount ?: 0 > 0
            },
            getContent = { world, pos, facing, limit ->
                val cap = world.getTileEntity(pos)!!
                        .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY!!, facing)!!
                PipeFluidContent(cap, cap.drain(limit, false)!!)
            },
            acceptsContent = { world, pos, facing, content -> content }),
    ITEM(unlocalizedName = "item", capability = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
            offersContent = { world, pos, facing ->
                world.getTileEntity(pos)
                        ?.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY!!, facing)
                        ?.let { itemCap ->
                            (0 until itemCap.slots).any { !itemCap.extractItem(it, 1, true).isEmpty }
                        } ?: false
            },
            getContent = closure@{ world, pos, facing, limit ->
                val cap = world.getTileEntity(pos)!!
                        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY!!, facing)!!
                for (i in (0 until cap.slots)) {
                    val virtualStack = cap.extractItem(i, limit, true)
                    if (!virtualStack.isEmpty) {
                        return@closure PipeItemContent(cap, virtualStack)
                    }
                }

                throw AssertionError("no content is available")
            },
            acceptsContent = { world, pos, facing, content -> content });

    override fun getName(): String {
        return this.unlocalizedName
    }
}