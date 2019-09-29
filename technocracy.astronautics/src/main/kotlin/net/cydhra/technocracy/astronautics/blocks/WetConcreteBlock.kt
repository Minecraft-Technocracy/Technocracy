package net.cydhra.technocracy.astronautics.blocks

import net.cydhra.technocracy.foundation.blocks.api.ColoredPlainBlock
import net.cydhra.technocracy.foundation.blocks.color.DyeBlockColor
import net.minecraft.block.BlockColored
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*


class WetConcreteBlock : ColoredPlainBlock("wet_concrete", Material.ROCK) {
    init {
        setHardness(2F)
        setResistance(10.0f)
        setCreativeTab(CreativeTabs.SEARCH)
        soundType = SoundType.GROUND
    }

    override fun randomTick(worldIn: World, pos: BlockPos, state: IBlockState, random: Random) {
        if (random.nextInt(10) <= 2) {
            worldIn.setBlockState(pos,Blocks.CONCRETE.defaultState.withProperty(BlockColored.COLOR, state.getValue(DyeBlockColor.COLOR)))
        }
    }

    override fun getTickRandomly(): Boolean {
        return true
    }

    override fun onEntityWalk(worldIn: World, pos: BlockPos, entityIn: Entity) {
        entityIn.motionX /= 1.2
        entityIn.motionZ /= 1.2
    }
}