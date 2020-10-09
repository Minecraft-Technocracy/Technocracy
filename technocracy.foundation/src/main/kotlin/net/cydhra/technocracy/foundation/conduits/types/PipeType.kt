package net.cydhra.technocracy.foundation.conduits.types

import net.cydhra.technocracy.foundation.client.textures.TextureAtlasManager
import net.cydhra.technocracy.foundation.content.capabilities.energy.EnergyCapabilityProvider
import net.cydhra.technocracy.foundation.util.DynamicTextureAtlasSprite
import net.cydhra.technocracy.foundation.util.get
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler

class PipeType(val unlocalizedName: String,
               val capability: Capability<*>,
               val offersContent: (world: WorldServer, pos: BlockPos, facing: EnumFacing) -> Boolean,
               val getContent: (world: WorldServer, pos: BlockPos, facing: EnumFacing, limit: Int) -> PipeContent,
               val acceptContent: (world: WorldServer, pos: BlockPos, facing: EnumFacing, content: PipeContent, simulate: Boolean) -> PipeContent)
    : IStringSerializable, Comparable<PipeType> {

    companion object {

        private val types = mutableSetOf<PipeType>()

        val ENERGY = PipeType(unlocalizedName = "energy",
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
                acceptContent = { world, pos, facing, content, simulate ->
                    val cap = world.getTileEntity(pos)
                            ?.getCapability(EnergyCapabilityProvider.CAPABILITY_ENERGY!!, facing)!!
                    val totalReceived = cap.receiveEnergy((content as PipeEnergyContent).amount, simulate)
                    PipeEnergyContent(content.source, content.amount - totalReceived)
                })

        val FLUID = PipeType(unlocalizedName = "fluid", capability = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
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
                acceptContent = { world, pos, facing, content, simulate ->
                    val cap = world.getTileEntity(pos)!!
                            .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY!!, facing)!!
                    val fill = cap.fill((content as PipeFluidContent).simulatedStack, !simulate)
                    PipeFluidContent(content.source, content.simulatedStack.copy().apply { amount -= fill })
                })

        val ITEM = PipeType(unlocalizedName = "item", capability = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
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
                            return@closure PipeItemContent(cap, i, virtualStack)
                        }
                    }

                    throw AssertionError("no content is available")
                },
                acceptContent = acceptContent@{ world, pos, facing, content, simulate ->
                    val cap = world.getTileEntity(pos)!!
                            .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY!!, facing)!!
                    var virtualStack: ItemStack = (content as PipeItemContent).simulatedStack

                    for (i in (0 until cap.slots)) {
                        virtualStack = cap.insertItem(i, virtualStack, simulate)
                        if (virtualStack.isEmpty) {
                            return@acceptContent PipeItemContent(content.source, content.slot, virtualStack)
                        }
                    }

                    return@acceptContent PipeItemContent(content.source, content.slot, virtualStack)
                })

        operator fun get(index: Int) : PipeType {
            return types[index]
        }

        fun values(): Set<PipeType> = types

        fun valueOf(name: String): PipeType? = types.find { it.name == name }
    }

    init {
        types.add(this)
    }

    override fun getName(): String {
        return this.unlocalizedName
    }

    @SideOnly(Side.CLIENT)
    val texture: DynamicTextureAtlasSprite = TextureAtlasManager.pipeTextures[this]!!

    val ordinal: Int = types.indexOf(this)

    override fun compareTo(other: PipeType): Int {
        return ordinal.compareTo(other.ordinal)
    }
}