package net.cydhra.technocracy.astronautics.blocks

import net.cydhra.technocracy.astronautics.blocks.general.reinforcedConcreteBlock
import net.cydhra.technocracy.astronautics.client.astronauticsColorTabs
import net.cydhra.technocracy.foundation.model.blocks.impl.ColoredPlainBlock
import net.cydhra.technocracy.foundation.model.blocks.color.DyeBlockColor
import net.cydhra.technocracy.foundation.model.blocks.util.IDynamicBlockDisplayName
import net.minecraft.block.BlockColored
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*


class WetReinforcedConcreteBlock : ColoredPlainBlock("wet_reinforced_concrete", Material.IRON, colorTab = astronauticsColorTabs), IDynamicBlockDisplayName {
    init {
        setHardness(2F)
        setResistance(10.0f)
        soundType = SoundType.GROUND
    }

    override fun randomTick(worldIn: World, pos: BlockPos, state: IBlockState, random: Random) {
        if (random.nextInt(10) <= 2) {
            worldIn.setBlockState(pos,reinforcedConcreteBlock.defaultState.withProperty(BlockColored.COLOR, state.getValue(DyeBlockColor.COLOR)))
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