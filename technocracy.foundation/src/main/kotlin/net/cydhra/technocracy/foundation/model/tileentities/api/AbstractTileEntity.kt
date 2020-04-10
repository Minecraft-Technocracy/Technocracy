package net.cydhra.technocracy.foundation.model.tileentities.api

import net.cydhra.technocracy.foundation.api.tileentities.TCTileEntity
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity


abstract class AbstractTileEntity : TileEntity(), TCTileEntity {

    /**
     * Cached attached block's BlockState.
     */
    protected var state: IBlockState? = null

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

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        super.getUpdatePacket()
        return SPacketUpdateTileEntity(this.pos, 3, this.updateTag)
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        handleUpdateTag(pkt.nbtCompound)
        markRenderUpdate()
    }

    /**
     * Mark the block for a block update.
     */
    fun markForUpdate() {
        if (this.world != null) {
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 3)
            markDirty()
        }
    }

    fun markRenderUpdate() {
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 0)
    }
}