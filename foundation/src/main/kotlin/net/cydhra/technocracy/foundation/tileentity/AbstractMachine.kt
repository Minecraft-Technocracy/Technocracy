package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.client.renderer.tileEntity.TileEntityElectricFurnaceRenderer
import net.cydhra.technocracy.foundation.tileentity.components.*
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.client.registry.ClientRegistry

/**
 * Base class for all machine [TileEntities][TileEntity]. The tile entity automatically has capabilities for energy
 * storage and a component that defines reactions to redstone signales. Note, that the component does not handle the
 * defined reactions itself.
 */
abstract class AbstractMachine : TileEntity(), ITickable {

    /**
     * The machine's redstone mode
     */
    protected val redstoneModeComponent = RedstoneModeComponent()

    /**
     * The machines internal energy storage and transfer limit state
     */
    protected val energyStorageComponent = EnergyStorageComponent()

    /**
     * The machine upgrades component.
     */
    /* TODO as the possible upgrades are dependant of machine type, either split this compound into single upgrades or
        at least handle it from subclass*/
    protected val machineUpgradesComponent = MachineUpgradesComponents()

    /**
     * All machine components that are saved to NBT and possibly accessible from GUI
     */
    private val components: MutableSet<IComponent> = mutableSetOf()

    /**
     * All components that also offer a capability. They must also be added to [components] but for speed they are
     * also collected in this list for quick query times in [hasCapability]
     */
    private val capabilityComponents: MutableSet<AbstractCapabilityComponent> = mutableSetOf()

    /**
     * The attached block's BlockState.
     */
    protected var state: IBlockState? = null

    init {
        TileEntity.register("${TCFoundation.MODID}:${javaClass.simpleName}", this.javaClass)
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElectricFurnace::class.java, TileEntityElectricFurnaceRenderer())

        this.registerComponent(redstoneModeComponent)
        this.registerComponent(energyStorageComponent)
        this.registerComponent(machineUpgradesComponent)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        for (comp in components) {
            comp.writeToNBT(compound)
        }

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        for (comp in components) {
            comp.readFromNBT(compound)
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capabilityComponents.any { it.hasCapability(capability, facing) }
                || super.hasCapability(capability, facing)
    }

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return capabilityComponents
                .firstOrNull { it.hasCapability(capability, facing) }
                ?.getCapability(capability, facing) ?: super.getCapability(capability, facing)
    }

    /**
     * Register a machine component. Should happen during construction of the tile entity instance.
     *
     * @param component [IComponent] implementation
     */
    protected fun registerComponent(component: IComponent) {
        this.components += component

        if (component is AbstractCapabilityComponent) {
            capabilityComponents += component
        }
    }

    /**
     * Query the world for the [IBlockState] associated with this entity
     *
     * @return the block state of the associated block in world
     */
    fun getBlockState(): IBlockState {
        if (this.state == null) {
            this.state = this.world.getBlockState(this.getPos())
        }
        return this.state!!
    }

    /**
     * Mark the block for a block update. Does not mark the chunk dirty.
     */
    fun markForUpdate() {
        if (this.world != null) {
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 3)
        }
    }
}