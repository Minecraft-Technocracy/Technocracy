package net.cydhra.technocracy.foundation.api.fx

import net.cydhra.technocracy.coremod.event.RenderWorldFirstEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.culling.Frustum
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*
import java.util.concurrent.Executors
import java.util.stream.Collectors
import kotlin.math.max


@SideOnly(Side.CLIENT)
object TCParticleManager {
    val particles = mutableMapOf<IParticleType, MutableList<AbstractParticle>>()
    var lastRender = 0

    val pool = Executors.newCachedThreadPool()

    val rnd = Random()

    fun addParticle(p: AbstractParticle) {
        val list = particles.getOrPut(p.getType()) { mutableListOf() }
        list.add(p)
    }

    @SubscribeEvent
    fun addDebugInfo(event: RenderGameOverlayEvent.Text) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            event.left.add("")
            event.left.add("TC_Particles: $lastRender/${particles.values.stream().mapToInt { it.size }.sum()}")
        }
    }

    @SubscribeEvent
    fun preRender(event: RenderWorldFirstEvent) {
        for ((type, list) in particles) {
            if (list.isEmpty()) continue
            val mut = type.mutex ?: continue

            pool.submit {
                mut.enter()

                var stream = list.stream()

                if (type.maxParticles != -1)
                    stream = stream.skip(max(0, list.size - type.maxParticles).toLong())

                type.uploadBuffers(stream, event.partialTicks)
                mut.leave()
            }
        }
    }

    @SubscribeEvent
    fun render(event: RenderWorldLastEvent) {

        val icamera = Frustum()
        val entity = Minecraft.getMinecraft().renderViewEntity!!
        val d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks.toDouble()
        val d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks.toDouble()
        val d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks.toDouble()
        icamera.setPosition(d0, d1, d2)

        lastRender = 0

        with(Minecraft.getMinecraft().mcProfiler) {
            startSection("TC_Particles")
            for ((type, list) in particles) {
                //val list = list.filter { icamera.isBoundingBoxInFrustum(it.boundingBox) }
                if (list.isEmpty()) continue

                startSection(type.name)

                type.mutex?.enter()

                type.preRenderType()

                if (type.perParticleRender) {
                    for (particle in list) {
                        lastRender++
                        particle.renderParticle(event.partialTicks)
                    }
                } else {
                    var stream = list.stream()

                    if (type.maxParticles != -1)
                        stream = stream.skip(max(0, list.size - type.maxParticles).toLong())

                    lastRender += type.render(stream, event.partialTicks)
                }

                type.postRenderType()
                type.mutex?.leave()
                endSection()
            }
            endSection()
        }
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            if (!Minecraft.getMinecraft().isGamePaused) {
                val player = Minecraft.getMinecraft().renderViewEntity ?: return

                Minecraft.getMinecraft().mcProfiler.startSection("TC_Particles_Sorting")

                particles.iterator().forEach {
                    try {
                        val lst = it.value.parallelStream().filter { p ->
                            p.onUpdate(player)
                        }.sorted { o1, o2 -> o2.lastDistance.compareTo(o1.lastDistance) }.collect(Collectors.toList())
                        it.setValue(lst)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                Minecraft.getMinecraft().mcProfiler.endSection()
            }
        }
    }

    @SubscribeEvent
    fun loadWorld(@Suppress("UNUSED_PARAMETER") event: WorldEvent.Load) {
        //clear particles as its per world only
        particles.clear()
    }
}