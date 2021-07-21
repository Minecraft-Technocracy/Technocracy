package net.cydhra.technocracy.optics.content.blocks

import net.cydhra.technocracy.foundation.content.blocks.MachineBlock
import net.cydhra.technocracy.optics.content.tileentities.machines.TileEntityLaser
import net.cydhra.technocracy.optics.content.tileentities.machines.TileEntityLaserDrill

val laserEmitterBlock = MachineBlock("laser_emitter", ::TileEntityLaser)
val laserDrillBlock = MachineBlock("laser_drill", ::TileEntityLaserDrill)