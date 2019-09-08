package net.cydhra.technocracy.astronautics.world

import net.cydhra.technocracy.astronautics.dyson.DysonSphereController
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.DimensionType
import net.minecraft.world.World
import net.minecraft.world.WorldProvider
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.border.WorldBorder
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class WrappedWorldProvider(val provider: WorldProvider) : WorldProvider() {

    /**
     * Gets the Sun Brightness for rendering sky.
     */
    @SideOnly(Side.CLIENT)
    override fun getSunBrightness(par1: Float): Float {
        return provider.getSunBrightness(par1) * (1f - DysonSphereController.sphereAmount)
    }

    /**
     * Returns array with sunrise/sunset colors
     */
    @SideOnly(Side.CLIENT)
    override fun calcSunriseSunsetColors(celestialAngle: Float, partialTicks: Float): FloatArray? {
        return provider.calcSunriseSunsetColors(celestialAngle, partialTicks)
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    @SideOnly(Side.CLIENT)
    override fun getFogColor(p_76562_1_: Float, p_76562_2_: Float): Vec3d {
        return provider.getFogColor(p_76562_1_, p_76562_2_).scale((1.0 - DysonSphereController.sphereAmount))
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyColor(cameraEntity: net.minecraft.entity.Entity, partialTicks: Float): Vec3d {
        return provider.getSkyColor(cameraEntity, partialTicks).scale((1.0 - DysonSphereController.sphereAmount))
    }

    @SideOnly(Side.CLIENT)
    override fun getCloudColor(partialTicks: Float): Vec3d {
        return provider.getCloudColor(partialTicks).scale((1.0 - DysonSphereController.sphereAmount))
    }

    /**
     * Gets the Star Brightness for rendering sky.
     */
    @SideOnly(Side.CLIENT)
    override fun getStarBrightness(par1: Float): Float {
        return provider.getStarBrightness(par1) + (Math.max(DysonSphereController.sphereAmount - 0.3f, 0f))
    }

    /**
     * The current sun brightness factor for this dimension.
     * 0.0f means no light at all, and 1.0f means maximum sunlight.
     * This will be used for the "calculateSkylightSubtracted"
     * which is for Sky light value calculation.
     *
     * @return The current brightness factor
     */
    override fun getSunBrightnessFactor(par1: Float): Float {
        return provider.getSunBrightnessFactor(par1) * (1f - DysonSphereController.sphereAmount)
    }

    override fun getDimensionType(): DimensionType {
        return provider.dimensionType
    }

    /*/**
     * Creates a new [BiomeProvider] for the WorldProvider, and also sets the values of [.hasSkylight] and
     * [.hasNoSky] appropriately.
     *
     * Note that subclasses generally override this method without calling the parent version.
     */
    override protected fun init() {
        provider.init()
    }*/

    override fun createChunkGenerator(): IChunkGenerator {
        return provider.createChunkGenerator()
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    override fun canCoordinateBeSpawn(x: Int, z: Int): Boolean {
        return provider.canCoordinateBeSpawn(x, z)
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    override fun calculateCelestialAngle(worldTime: Long, partialTicks: Float): Float {
        return provider.calculateCelestialAngle(worldTime, partialTicks)
    }

    override fun getMoonPhase(worldTime: Long): Int {
        return provider.getMoonPhase(worldTime)
    }

    /**
     * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
     */
    override fun isSurfaceWorld(): Boolean {
        return provider.isSurfaceWorld
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    override fun canRespawnHere(): Boolean {
        return provider.canRespawnHere()
    }

    /**
     * the y level at which clouds are rendered.
     */
    @SideOnly(Side.CLIENT)
    override fun getCloudHeight(): Float {
        return provider.cloudHeight
    }

    @SideOnly(Side.CLIENT)
    override fun isSkyColored(): Boolean {
        return provider.isSkyColored
    }

    override fun getSpawnCoordinate(): BlockPos? {
        return provider.spawnCoordinate
    }

    override fun getAverageGroundLevel(): Int {
        return provider.averageGroundLevel
    }

    /**
     * Returns a double value representing the Y value relative to the top of the map at which void fog is at its
     * maximum. The default factor of 0.03125 relative to 256, for example, means the void fog will be at its maximum at
     * (256*0.03125), or 8.
     */
    @SideOnly(Side.CLIENT)
    override fun getVoidFogYFactor(): Double {
        return provider.voidFogYFactor
    }

    /**
     * Returns true if the given X,Z coordinate should show environmental fog.
     */
    @SideOnly(Side.CLIENT)
    override fun doesXZShowFog(x: Int, z: Int): Boolean {
        return provider.doesXZShowFog(x, z)
    }

    override fun getBiomeProvider(): BiomeProvider {
        return provider.biomeProvider
    }

    override fun doesWaterVaporize(): Boolean {
        return provider.doesWaterVaporize()
    }

    override fun hasSkyLight(): Boolean {
        return provider.hasSkyLight()
    }

    override fun isNether(): Boolean {
        return provider.isNether
    }

    override fun getLightBrightnessTable(): FloatArray {
        return provider.lightBrightnessTable
    }

    override fun createWorldBorder(): WorldBorder {
        return provider.createWorldBorder()
    }

    /**
     * Sets the providers current dimension ID, used in default getSaveFolder()
     * Added to allow default providers to be registered for multiple dimensions.
     * This is to denote the exact dimension ID opposed to the 'type' in WorldType
     *
     * @param dim Dimension ID
     */
    override fun setDimension(dim: Int) {
        provider.dimension = dim
    }

    override fun getDimension(): Int {
        return provider.dimension
    }

    /**
     * Returns the sub-folder of the world folder that this WorldProvider saves to.
     * EXA: DIM1, DIM-1
     * @return The sub-folder name to save this world's chunks to.
     */
    override fun getSaveFolder(): String? {
        return provider.saveFolder
    }

    /**
     * The dimension's movement factor.
     * Whenever a player or entity changes dimension from world A to world B, their coordinates are multiplied by
     * worldA.provider.getMovementFactor() / worldB.provider.getMovementFactor()
     * Example: Overworld factor is 1, nether factor is 8. Traveling from overworld to nether multiplies coordinates by 1/8.
     * @return The movement factor
     */
    override fun getMovementFactor(): Double {
        return provider.movementFactor
    }

    /**
     * If this method returns true, then chunks received by the client will
     * have [net.minecraft.world.chunk.Chunk.resetRelightChecks] called
     * on them, queuing lighting checks for all air blocks in the chunk (and
     * any adjacent light-emitting blocks).
     *
     * Returning true here is recommended if the chunk generator used also
     * does this for newly generated chunks.
     *
     * @return true if lighting checks should be performed
     */
    override fun shouldClientCheckLighting(): Boolean {
        return provider.shouldClientCheckLighting()
    }

    @SideOnly(Side.CLIENT)
    override fun getSkyRenderer(): net.minecraftforge.client.IRenderHandler? {
        return provider.skyRenderer
    }

    @SideOnly(Side.CLIENT)
    override fun setSkyRenderer(skyRenderer: net.minecraftforge.client.IRenderHandler) {
        provider.skyRenderer = skyRenderer
    }

    @SideOnly(Side.CLIENT)
    override fun getCloudRenderer(): net.minecraftforge.client.IRenderHandler? {
        return provider.cloudRenderer
    }

    @SideOnly(Side.CLIENT)
    override fun setCloudRenderer(renderer: net.minecraftforge.client.IRenderHandler) {
        provider.cloudRenderer = renderer
    }

    @SideOnly(Side.CLIENT)
    override fun getWeatherRenderer(): net.minecraftforge.client.IRenderHandler? {
        return provider.weatherRenderer
    }

    @SideOnly(Side.CLIENT)
    override fun setWeatherRenderer(renderer: net.minecraftforge.client.IRenderHandler) {
        provider.weatherRenderer = renderer
    }

    /**
     * Allows for manipulating the coloring of the lightmap texture.
     * Will be called for each 16*16 combination of sky/block light values.
     *
     * @param partialTicks Progress between ticks.
     * @param sunBrightness Current sun brightness.
     * @param skyLight Sky light brightness factor.
     * @param blockLight Block light brightness factor.
     * @param colors The color values that will be used: [r, g, b].
     *
     * @see net.minecraft.client.renderer.EntityRenderer.updateLightmap
     */
    override fun getLightmapColors(partialTicks: Float, sunBrightness: Float, skyLight: Float, blockLight: Float,
                                   colors: FloatArray) {
        provider.getLightmapColors(partialTicks, sunBrightness, skyLight, blockLight, colors)
    }

    override fun getRandomizedSpawnPoint(): BlockPos {
        return provider.randomizedSpawnPoint
    }

    /**
     * Determine if the cursor on the map should 'spin' when rendered, like it does for the player in the nether.
     *
     * @param entity The entity holding the map, playername, or frame-ENTITYID
     * @param x X Position
     * @param z Z Position
     * @param rotation the regular rotation of the marker
     * @return True to 'spin' the cursor
     */
    override fun shouldMapSpin(entity: String, x: Double, z: Double, rotation: Double): Boolean {
        return provider.shouldMapSpin(entity, x, z, rotation)
    }

    /**
     * Determines the dimension the player will be respawned in, typically this brings them back to the overworld.
     *
     * @param player The player that is respawning
     * @return The dimension to respawn the player in
     */
    override fun getRespawnDimension(player: net.minecraft.entity.player.EntityPlayerMP): Int {
        return provider.getRespawnDimension(player)
    }

    /**
     * Called from [World.initCapabilities], to gather capabilities for this world.
     * It's safe to access world here since this is called after world is registered.
     *
     * On server, called directly after mapStorage and world data such as Scoreboard and VillageCollection are initialized.
     * On client, called when world is constructed, just before world load event is called.
     * Note that this method is always called before the world load event.
     * @return initial holder for capabilities on the world
     */
    override fun initCapabilities(): net.minecraftforge.common.capabilities.ICapabilityProvider? {
        return provider.initCapabilities()
    }

    /**
     * Called on the client to get the music type to play when in this world type.
     * At the time of calling, the client player and world are guaranteed to be non-null
     * @return null to use vanilla logic, otherwise a MusicType to play in this world
     */
    @SideOnly(Side.CLIENT)
    override fun getMusicType(): net.minecraft.client.audio.MusicTicker.MusicType? {
        return provider.musicType
    }

    /**
     * Determines if the player can sleep in this world (or if the bed should explode for example).
     *
     * @param player The player that is attempting to sleep
     * @param pos The location where the player tries to sleep at (the position of the clicked on bed for example)
     * @return the result of a player trying to sleep at the given location
     */
    override fun canSleepAt(player: net.minecraft.entity.player.EntityPlayer, pos: BlockPos): WorldProvider.WorldSleepResult {
        return provider.canSleepAt(player, pos)
    }

    /*======================================= Start Moved From World =========================================*/

    override fun getBiomeForCoords(pos: BlockPos): Biome {
        return provider.getBiomeForCoords(pos)
    }

    override fun isDaytime(): Boolean {
        return provider.isDaytime
    }

    /**
     * Calculates the current moon phase factor.
     * This factor is effective for slimes.
     * (This method do not affect the moon rendering)
     */
    override fun getCurrentMoonPhaseFactor(): Float {
        return provider.currentMoonPhaseFactor
    }

    override fun setAllowedSpawnTypes(allowHostile: Boolean, allowPeaceful: Boolean) {
        provider.setAllowedSpawnTypes(allowHostile, allowPeaceful)
    }

    override fun calculateInitialWeather() {
        provider.calculateInitialWeather()
    }

    override fun updateWeather() {
        provider.updateWeather()
    }

    override fun canBlockFreeze(pos: BlockPos, byWater: Boolean): Boolean {
        return provider.canBlockFreeze(pos, byWater)
    }

    override fun canSnowAt(pos: BlockPos, checkLight: Boolean): Boolean {
        return provider.canSnowAt(pos, checkLight)
    }

    override fun setWorldTime(time: Long) {
        provider.worldTime = time
    }

    override fun getSeed(): Long {
        return provider.seed
    }

    override fun getWorldTime(): Long {
        return provider.worldTime
    }

    override fun getSpawnPoint(): BlockPos {
        return provider.spawnPoint
    }

    override fun setSpawnPoint(pos: BlockPos) {
        provider.spawnPoint = pos
    }

    override fun canMineBlock(player: net.minecraft.entity.player.EntityPlayer, pos: BlockPos): Boolean {
        return provider.canMineBlock(player, pos)
    }

    override fun isBlockHighHumidity(pos: BlockPos): Boolean {
        return provider.isBlockHighHumidity(pos)
    }

    override fun getHeight(): Int {
        return provider.height
    }

    override fun getActualHeight(): Int {
        return provider.actualHeight
    }

    override fun getHorizon(): Double {
        return provider.horizon
    }

    override fun resetRainAndThunder() {
        provider.resetRainAndThunder()
    }

    override fun canDoLightning(chunk: net.minecraft.world.chunk.Chunk): Boolean {
        return provider.canDoLightning(chunk)
    }

    override fun canDoRainSnowIce(chunk: net.minecraft.world.chunk.Chunk): Boolean {
        return provider.canDoRainSnowIce(chunk)
    }

    /**
     * Called when a Player is added to the provider's world.
     */
    override fun onPlayerAdded(player: EntityPlayerMP) {
        provider.onPlayerAdded(player)
    }

    /**
     * Called when a Player is removed from the provider's world.
     */
    override fun onPlayerRemoved(player: EntityPlayerMP) {
        provider.onPlayerRemoved(player)
    }

    /**
     * Called when the world is performing a save. Only used to save the state of the Dragon Boss fight in
     * WorldProviderEnd in Vanilla.
     */
    override fun onWorldSave() {
        provider.onWorldSave()
    }

    /**
     * Called when the world is updating entities. Only used in WorldProviderEnd to update the DragonFightManager in
     * Vanilla.
     */
    override fun onWorldUpdateEntities() {
        provider.onWorldUpdateEntities()
    }

    /**
     * Called to determine if the chunk at the given chunk coordinates within the provider's world can be dropped. Used
     * in WorldProviderSurface to prevent spawn chunks from being unloaded.
     */
    override fun canDropChunk(x: Int, z: Int): Boolean {
        return provider.canDropChunk(x, z)
    }
}