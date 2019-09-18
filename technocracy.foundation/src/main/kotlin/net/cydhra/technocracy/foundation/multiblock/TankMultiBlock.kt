package net.cydhra.technocracy.foundation.multiblock

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.blocks.general.*
import net.cydhra.technocracy.foundation.tileentity.components.AbstractComponent
import net.cydhra.technocracy.foundation.tileentity.multiblock.tank.TileEntityTankMultiBlockPart
import net.cydhra.technocracy.foundation.tileentity.multiblock.tank.TileEntityTankPort
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import java.util.function.Predicate


class TankMultiBlock(world: World) : BaseMultiBlock(
        frameBlockWhitelist = Predicate {
            it.block == tankWallBlock || it.block == tankIOBlock
        },
        sideBlockWhitelist = Predicate {
            it.block == tankWallBlock || it.block == tankGlassBlock || it.block == tankIOBlock
        },
        topBlockWhitelist = Predicate {
            it.block == tankWallBlock || it.block == tankGlassBlock || it.block == tankIOBlock
        },
        bottomBlockWhitelist = Predicate {
            it.block == tankWallBlock || it.block == tankGlassBlock || it.block == tankIOBlock
        },
        interiorBlockWhitelist = Predicate { it.block == Blocks.AIR },
        maximumSizeXZ = 40,
        maximumSizeY = 40,
        world = world
) {
    /**
     * The controller block of the tank
     */
    var controllerTileEntity: TileEntityTankMultiBlockPart? = null

    /**
     * All io ports of the structure walls
     */
    private var ports: List<TileEntityTankPort> = emptyList()

    /**
     * A list of all closed tubes inside the machine
     */
    override fun updateServer(): Boolean {
        return true
    }

    override fun updateClient() {

    }

    override fun isMachineWhole(validatorCallback: IMultiblockValidator): Boolean {
        if (!super.isMachineWhole(validatorCallback)) {
            println(validatorCallback.lastError.chatMessage)
            return false
        }

        val sizeX = maximumCoord.x - minimumCoord.x + 1
        val sizeY = maximumCoord.y - minimumCoord.y + 1
        val sizeZ = maximumCoord.z - minimumCoord.z + 1


        val interiorMin = minimumCoord.add(1, 1, 1)
        val interiorMax = maximumCoord.add(-1, -1, -1)

        val height = interiorMax.y - interiorMin.y + 1

        if (height < 1) {
            validatorCallback.setLastError("multiblock.error.invalidSize", height)
            return false
        }

        val posX = minimumCoord.x + sizeX / 2
        val posZ = minimumCoord.z + sizeZ / 2

        if (!(sizeX == sizeZ && sizeX % 2 != 0)) {
            validatorCallback.setLastError("multiblock.error.invalidSize", sizeX, sizeZ)
            return false
        }

        return assemble(validatorCallback) {
            val inputPorts = mutableListOf<TileEntityTankPort>()
            val wallBlocks = mutableListOf<TileEntityTankMultiBlockPart>()

            collect(inputPorts)
            collect(wallBlocks)

            finishUp {
                var currentFluid: FluidStack? = null

                for (part in wallBlocks) {
                    if (part.fluidComp.isAttached) {
                        if (part.fluidComp.innerComponent.fluid.currentFluid != null) {
                            if (currentFluid == null) {
                                currentFluid = part.fluidComp.innerComponent.fluid.currentFluid
                            } else {
                                if (currentFluid.isFluidEqual(part.fluidComp.innerComponent.fluid.currentFluid)) {
                                    currentFluid.amount += part.fluidComp.innerComponent.fluid.currentFluid!!.amount
                                } else {
                                    validatorCallback.setLastError("multiblock.error.invalidFluidType", currentFluid.fluid, part.fluidComp.innerComponent.fluid.currentFluid!!.fluid)
                                    return@finishUp false
                                }
                            }
                        }
                    }
                }

                for (part in wallBlocks) {
                    if (part.fluidComp.isAttached) {
                        part.fluidComp.markDirty(true)
                        part.fluidComp.isAttached = false
                    }
                }

                val type = this@TankMultiBlock.connectedParts.find { it.worldPosition.x == posX && it.worldPosition.z == posZ && it.worldPosition.y == minimumCoord.y }
                this@TankMultiBlock.controllerTileEntity = type as TileEntityTankMultiBlockPart
                this@TankMultiBlock.controllerTileEntity!!.fluidComp.isAttached = true
                //todo config
                this@TankMultiBlock.controllerTileEntity!!.fluidComp.innerComponent.fluid.capacity = sizeX * sizeZ * sizeY * 25000

                if(currentFluid != null) {
                    //fill fluid back into tank
                    this@TankMultiBlock.controllerTileEntity!!.fluidComp.innerComponent.fluid.fill(currentFluid, true)
                }

                this@TankMultiBlock.controllerTileEntity!!.fluidComp.markDirty(true)

                //todo calc and set max capacity
                this@TankMultiBlock.ports = inputPorts

                return@finishUp true
            }
        }
    }

    override fun getMinimumNumberOfBlocksForAssembledMachine(): Int {
        return 1
    }

    override fun onBlockAdded(p0: IMultiblockPart?) {

    }

    override fun onBlockRemoved(p0: IMultiblockPart?) {

    }

    override fun onAssimilate(p0: MultiblockControllerBase?) {

    }

    override fun onAttachedPartWithMultiblockData(p0: IMultiblockPart?, p1: NBTTagCompound?) {

    }

    override fun onMachineAssembled() {

    }

    override fun syncDataFrom(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {

    }

    override fun onAssimilated(p0: MultiblockControllerBase?) {

    }

    override fun onMachineRestored() {

    }

    override fun syncDataTo(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {

    }

    override fun onMachinePaused() {

    }

    override fun onMachineDisassembled() {

    }

    override fun getComponents(): MutableList<Pair<String, AbstractComponent>> {
        val components = mutableListOf<Pair<String, AbstractComponent>>()
        this.ports.forEach { components.addAll(it.getComponents()) }
        if (controllerTileEntity != null) components.addAll(controllerTileEntity!!.getComponents())
        return components
    }
}