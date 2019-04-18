package net.cydhra.technocracy.foundation.tileentity

import net.cydhra.technocracy.foundation.tileentity.components.*
import net.cydhra.technocracy.foundation.tileentity.logic.ILogicClient
import net.cydhra.technocracy.foundation.tileentity.logic.LogicClientDelegate
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability

/**
 * Base class for all machine [TileEntities][TileEntity]. The tile entity automatically has capabilities for energy
 * storage and a component that defines reactions to redstone signales. Note, that the component does not handle the
 * defined reactions itself.
 */
abstract class AbstractMachine : TileEntity(), ITickable, ILogicClient by LogicClientDelegate() {

    /**
     * The machine's redstone mode
     */
    protected val redstoneModeComponent = RedstoneModeComponent()

    /**
     * The machines internal energy storage and transfer limit state
     */
    protected val energyStorageComponent = EnergyStorageComponent(mutableSetOf(EnumFacing.UP))

    /**
     * The machine upgrades component.
     */
    /* TODO as the possible upgrades are dependant of machine type, either split this compound into single upgrades or
        at least handle it from subclass*/
    protected val machineUpgradesComponent = MachineUpgradesComponents()

    /**
     * All machine components that are saved to NBT and possibly accessible from GUI
     */
    private val components: MutableList<Pair<String, IComponent>> = mutableListOf()

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
        this.registerComponent(redstoneModeComponent, "redstone_mode")
        this.registerComponent(energyStorageComponent, "energy")
        this.registerComponent(machineUpgradesComponent, "upgrades")
    }

    fun getComponents(): MutableList<Pair<String, IComponent>> {
        return this.components
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        for ((name, component) in components) {
            compound.setTag(name, component.serializeNBT())
        }

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        for ((name, component) in components) {
            if (compound.hasKey(name))
                component.deserializeNBT(compound.getTag(name))
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

    override fun update() {
        // update ILogic strategies
        this.tick()
    }

    /**
     * Register a machine component. Should happen during construction of the tile entity instance.
     *
     * @param component [IComponent] implementation
     * @param name machine-unique name for the component. Used in NBT serialization
     */
    protected fun registerComponent(component: IComponent, name: String) {
        this.components += name to component

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