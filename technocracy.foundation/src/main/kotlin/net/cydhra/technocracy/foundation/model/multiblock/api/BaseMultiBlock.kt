package net.cydhra.technocracy.foundation.model.multiblock.api

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockControllerBase
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError
import it.zerono.mods.zerocore.lib.block.ModTileEntity
import net.cydhra.technocracy.foundation.api.ecs.IComponent
import net.cydhra.technocracy.foundation.model.blocks.impl.PlainMultiBlockPartBlock
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import java.util.function.Predicate

/**
 * Base class for multiblocks of this mod. Handles common stuff and provides additional API to the multi-block logic
 * of subclasses.
 */
abstract class BaseMultiBlock(
        val frameBlockWhitelist: Predicate<IBlockState>?,
        val sideBlockWhitelist: Predicate<IBlockState>?,
        val topBlockWhitelist: Predicate<IBlockState>?,
        val bottomBlockWhitelist: Predicate<IBlockState>?,
        val interiorBlockWhitelist: Predicate<IBlockState>?,
        private val maximumSizeXZ: Int,
        private val maximumSizeY: Int,
        world: World)
    : RectangularMultiblockControllerBase(world) {

    override fun isBlockGoodForSides(world: World, x: Int, y: Int, z: Int, validator: IMultiblockValidator): Boolean {
        return sideBlockWhitelist == null || sideBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun isBlockGoodForFrame(world: World, x: Int, y: Int, z: Int, validator: IMultiblockValidator): Boolean {
        return frameBlockWhitelist == null || frameBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun isBlockGoodForTop(world: World, x: Int, y: Int, z: Int, validator: IMultiblockValidator): Boolean {
        return topBlockWhitelist == null || topBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun isBlockGoodForInterior(world: World, x: Int, y: Int, z: Int, validator: IMultiblockValidator): Boolean {
        return interiorBlockWhitelist == null || interiorBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun isBlockGoodForBottom(world: World, x: Int, y: Int, z: Int, validator: IMultiblockValidator): Boolean {
        return bottomBlockWhitelist == null || bottomBlockWhitelist.test(world.getBlockState(BlockPos(x, y, z)))
    }

    override fun getMaximumXSize(): Int {
        return this.maximumSizeXZ
    }

    override fun getMaximumYSize(): Int {
        return this.maximumSizeY
    }

    override fun getMaximumZSize(): Int {
        return this.maximumSizeXZ
    }

    override fun onMachineAssembled() {
        forceStructureUpdate(WORLD)
    }

    override fun onMachineRestored() {
        forceStructureUpdate(WORLD)
    }

    override fun onMachineDisassembled() {
        forceStructureUpdate(WORLD)
    }

    override fun onBlockAdded(p0: IMultiblockPart?) {
    }

    override fun onBlockRemoved(p0: IMultiblockPart?) {
    }

    override fun onAssimilate(p0: MultiblockControllerBase?) {
    }

    override fun onAttachedPartWithMultiblockData(p0: IMultiblockPart?, p1: NBTTagCompound?) {
    }

    override fun syncDataFrom(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {
    }

    override fun onAssimilated(p0: MultiblockControllerBase?) {
    }

    override fun syncDataTo(p0: NBTTagCompound?, p1: ModTileEntity.SyncReason?) {
    }

    override fun onMachinePaused() {
    }

    internal abstract fun getComponents(): MutableList<Pair<String, IComponent>>

    /**
     * Helper function to easily assemble a multi-block structure. This method is supposed t be called from
     * [isMachineWhole]. The given lambda block is executed in the context of a [MultiBlockAssemblyDomain] and
     * defines the searched multi block elements. If wrong amounts of blocks are detected, the given [validator] is
     * given an appropriate error and false is returned. The return value may be propagated to the return value of
     * [isMachineWhole]
     *
     * @param validator the validator of the multi-block structure that is used as error callback
     * @param configure a lambda defining the multiblock structure
     *
     * @return true if everything was found within the acceptable ranges
     */
    protected inline fun assemble(validator: IMultiblockValidator,
                                  configure: MultiBlockAssemblyDomain.() -> Unit): Boolean {
        val domain = MultiBlockAssemblyDomain()
        domain.configure()
        for (part in this.connectedParts) {
            domain.assemblyRules.filter { it.matches(part) }.forEach {
                @Suppress("UNCHECKED_CAST")
                (it.list as MutableList<in IMultiblockPart>).add(part)
            }
        }

        for ((list, _, range, name) in domain.assemblyRules) {
            if (range == null) continue
            if (list.size !in range) {
                when {
                    range.first == range.last ->
                        validator.lastError = ValidationError("multiblock.error.number_of_blocks",
                                TextComponentTranslation("$name.name").formattedText, range.first, list.size)
                    list.size < range.first ->
                        validator.lastError = ValidationError("multiblock.error.too_few_blocks",
                                TextComponentTranslation("$name.name").formattedText, range.first, list.size)
                    list.size > range.last ->
                        validator.lastError = ValidationError("multiblock.error.too_many_blocks",
                                TextComponentTranslation("$name.name").formattedText, range.last, list.size)
                }
                return false
            }
        }

        return domain.finishBlock?.invoke() ?: true
    }

    /**
     * A utility class used for the DSL for multi-block assembly. It offers more utility functions used to find and
     * process tile entities that must or can be part of the structure.
     *
     * @see collect
     * @see finishBlock
     */
    protected class MultiBlockAssemblyDomain {
        val assemblyRules = mutableListOf<AssemblyRule<*>>()

        var finishBlock: (() -> Boolean)? = null

        inline fun <reified T : IMultiblockPart> collect(unlocalizedBlock: String, list: MutableList<T>,
                                                         range: IntRange) {
            assemblyRules += AssemblyRule(list, { it is T }, range, unlocalizedBlock)
        }

        inline fun <reified T : IMultiblockPart> collect(list: MutableList<T>) {
            assemblyRules += AssemblyRule(list, { it is T }, null, "")
        }

        inline fun <reified T : IMultiblockPart> collect(unlocalizedBlock: String, list: MutableList<T>, amount: Int) {
            assemblyRules += AssemblyRule(list, { it is T }, amount..amount, unlocalizedBlock)
        }

        /**
         * @param block a lambda that is executed once all parts have been scanned and no parts are missing or too much
         */
        fun finishUp(block: () -> Boolean) {
            if (finishBlock != null)
                throw IllegalStateException("only one finish block is allowed")

            finishBlock = block
        }

        /**
         * The rules of structural assembly. Do not use this from subclasses
         */
        data class AssemblyRule<T : IMultiblockPart>(val list: MutableList<T>, val matches: (Any) -> Boolean,
                                                     val range: IntRange?, val unlocalizedBlock: String)
    }

    override fun forceStructureUpdate(world: World) {
        //TODO optimize, not all blocks need an update, only the ones with outgoing capabilitys (maybe add variable in tiles)
        val minCoord = this.minimumCoord
        val maxCoord = this.maximumCoord
        val minX = minCoord.x
        val minY = minCoord.y
        val minZ = minCoord.z
        val maxX = maxCoord.x
        val maxY = maxCoord.y
        val maxZ = maxCoord.z

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val pos = BlockPos(x, y, z)
                    val state = world.getBlockState(pos)
                    if(state.block is PlainMultiBlockPartBlock<*>) {
                        //send actual block update not just render update
                        world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 3)
                    }
                }
            }
        }

    }
}