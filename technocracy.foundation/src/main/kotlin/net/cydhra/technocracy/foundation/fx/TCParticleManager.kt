package net.cydhra.technocracy.foundation.fx

import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.world.World
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


@SideOnly(Side.CLIENT)
object TCParticleManager {
    lateinit var world: World

    val particles = mutableListOf<Particle>()

    fun addParticle(p: Particle) {
        particles.add(p)
    }

    fun tick(event: TickEvent.WorldTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (!Minecraft.getMinecraft().isGamePaused) {
                particles.removeIf {
                    it.onUpdate()
                    !it.isAlive
                }
            }
        }
    }

    @SubscribeEvent
    fun loadWorld(event: WorldEvent.Load) {
        this.world = event.world
    }
}