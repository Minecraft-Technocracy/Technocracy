package net.cydhra.technocracy.optics.api.tileentities.logic

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.content.tileentities.components.EnergyStorageTileEntityComponent
import net.cydhra.technocracy.foundation.data.config.IntegerConfigurable
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogic
import net.cydhra.technocracy.optics.api.tileentities.components.LaserAbsorberComponent
import net.cydhra.technocracy.optics.api.tileentities.components.LaserEmitterTileEntityComponent
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.absoluteValue

class LaserLogic(
        private val blockPos: BlockPos,
        private val world: World,
        private val laserEmitterComponent: LaserEmitterTileEntityComponent,
        private val energyStorage: EnergyStorageTileEntityComponent) : ILogic {

    companion object {
        /**
         * How many lasers are statistically checked for new/vanished connections. This value is the inverse of the
         * fraction of all lasers checked.
         */
        private const val FRACTION_OF_LASERS_CHECKED = 4

        private val laserDetectionDistance by IntegerConfigurable(TCFoundation.config,
                "laser",
                "connectionDistance",
                15,
                "how far away lasers search for possible absorbers",
                2,
                64)
    }

    override fun preProcessing(): Boolean {
        return true
    }

    override fun processing() {
        // energy transfer
        // TODO


        // only check for a fraction of all lasers
        if (this.blockPos.x.absoluteValue % FRACTION_OF_LASERS_CHECKED
                != (world.totalWorldTime % FRACTION_OF_LASERS_CHECKED).toInt()) {
            return
        }

        // connection / disconnection logic
        for (facing in this.laserEmitterComponent.canEmitAt) {
            var i = 0
            // minimum distance is two, so offset by one already
            var blockChecked = this.blockPos.offset(facing)
            while (i < laserDetectionDistance) {
                blockChecked = blockChecked.offset(facing)
                i++

                if (!this.world.getBlockState(blockChecked).isTranslucent) {
                    if (this.world.getTileEntity(blockChecked)?.hasCapability(
                                    LaserAbsorberComponent.CAPABILITY_LASER_ABSORBER, facing.opposite) == true) {
                        this.laserEmitterComponent.connectedTo[facing] = this.world.getTileEntity(blockChecked)!!
                                .getCapability(LaserAbsorberComponent.CAPABILITY_LASER_ABSORBER, facing.opposite)!!
                        break
                    } else {
                        this.laserEmitterComponent.connectedTo.remove(facing)
                        break
                    }
                } else {
                    if (i == laserDetectionDistance) {
                        this.laserEmitterComponent.connectedTo.remove(facing)
                    }
                }
            }

        }
    }

    override fun postProcessing(wasProcessing: Boolean) {

    }

}