package util.mock;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.*;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings({"ConstantConditions", "Contract"})
public class MockWorld implements World {

    private final String name;

    private final UUID uuid;

    public MockWorld(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    @NotNull
    @Override
    public Block getBlockAt(int i, int i1, int i2) {
        return null;
    }

    @NotNull
    @Override
    public Block getBlockAt(@NotNull Location location) {
        return null;
    }

    @Override
    public int getHighestBlockYAt(int i, int i1) {
        return 0;
    }

    @Override
    public int getHighestBlockYAt(@NotNull Location location) {
        return 0;
    }

    @NotNull
    @Override
    public Block getHighestBlockAt(int i, int i1) {
        return null;
    }

    @NotNull
    @Override
    public Block getHighestBlockAt(@NotNull Location location) {
        return null;
    }

    @Override
    public int getHighestBlockYAt(int i, int i1, @NotNull HeightMap heightMap) {
        return 0;
    }

    @Override
    public int getHighestBlockYAt(@NotNull Location location, @NotNull HeightMap heightMap) {
        return 0;
    }

    @NotNull
    @Override
    public Block getHighestBlockAt(int i, int i1, @NotNull HeightMap heightMap) {
        return null;
    }

    @NotNull
    @Override
    public Block getHighestBlockAt(@NotNull Location location, @NotNull HeightMap heightMap) {
        return null;
    }

    @NotNull
    @Override
    public Chunk getChunkAt(int i, int i1) {
        return null;
    }

    @NotNull
    @Override
    public Chunk getChunkAt(@NotNull Location location) {
        return null;
    }

    @NotNull
    @Override
    public Chunk getChunkAt(@NotNull Block block) {
        return null;
    }

    @Override
    public boolean isChunkLoaded(@NotNull Chunk chunk) {
        return false;
    }

    @NotNull
    @Override
    public Chunk[] getLoadedChunks() {
        return new Chunk[0];
    }

    @Override
    public void loadChunk(@NotNull Chunk chunk) {

    }

    @Override
    public boolean isChunkLoaded(int i, int i1) {
        return false;
    }

    @Override
    public boolean isChunkGenerated(int i, int i1) {
        return false;
    }

    @Override
    public boolean isChunkInUse(int i, int i1) {
        return false;
    }

    @Override
    public void loadChunk(int i, int i1) {

    }

    @Override
    public boolean loadChunk(int i, int i1, boolean b) {
        return false;
    }

    @Override
    public boolean unloadChunk(@NotNull Chunk chunk) {
        return false;
    }

    @Override
    public boolean unloadChunk(int i, int i1) {
        return false;
    }

    @Override
    public boolean unloadChunk(int i, int i1, boolean b) {
        return false;
    }

    @Override
    public boolean unloadChunkRequest(int i, int i1) {
        return false;
    }

    @Override
    public boolean regenerateChunk(int i, int i1) {
        return false;
    }

    @Override
    public boolean refreshChunk(int i, int i1) {
        return false;
    }

    @Override
    public boolean isChunkForceLoaded(int i, int i1) {
        return false;
    }

    @Override
    public void setChunkForceLoaded(int i, int i1, boolean b) {

    }

    @NotNull
    @Override
    public java.util.Collection<Chunk> getForceLoadedChunks() {
        return null;
    }

    @Override
    public boolean addPluginChunkTicket(int i, int i1, @NotNull Plugin plugin) {
        return false;
    }

    @Override
    public boolean removePluginChunkTicket(int i, int i1, @NotNull Plugin plugin) {
        return false;
    }

    @Override
    public void removePluginChunkTickets(@NotNull Plugin plugin) {

    }

    @NotNull
    @Override
    public java.util.Collection<Plugin> getPluginChunkTickets(int i, int i1) {
        return null;
    }

    @NotNull
    @Override
    public java.util.Map<Plugin, java.util.Collection<Chunk>> getPluginChunkTickets() {
        return null;
    }

    @NotNull
    @Override
    public Item dropItem(@NotNull Location location, @NotNull ItemStack itemStack) {
        return null;
    }

    @NotNull
    @Override
    public Item dropItem(@NotNull Location location, @NotNull ItemStack itemStack, @Nullable Consumer<Item> consumer) {
        return null;
    }

    @NotNull
    @Override
    public Item dropItemNaturally(@NotNull Location location, @NotNull ItemStack itemStack) {
        return null;
    }

    @NotNull
    @Override
    public Item dropItemNaturally(@NotNull Location location, @NotNull ItemStack itemStack, @Nullable Consumer<Item> consumer) {
        return null;
    }

    @NotNull
    @Override
    public Arrow spawnArrow(@NotNull Location location, @NotNull Vector vector, float v, float v1) {
        return null;
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull TreeType treeType) {
        return false;
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull TreeType treeType, @NotNull BlockChangeDelegate blockChangeDelegate) {
        return false;
    }

    @NotNull
    @Override
    public LightningStrike strikeLightning(@NotNull Location location) {
        return null;
    }

    @NotNull
    @Override
    public LightningStrike strikeLightningEffect(@NotNull Location location) {
        return null;
    }

    @NotNull
    @Override
    public Biome getBiome(@NotNull Location location) {
        return null;
    }

    @NotNull
    @Override
    public Biome getBiome(int i, int i1, int i2) {
        return null;
    }

    @Override
    public void setBiome(@NotNull Location location, @NotNull Biome biome) {

    }

    @Override
    public void setBiome(int i, int i1, int i2, @NotNull Biome biome) {

    }

    @NotNull
    @Override
    public BlockState getBlockState(@NotNull Location location) {
        return null;
    }

    @NotNull
    @Override
    public BlockState getBlockState(int i, int i1, int i2) {
        return null;
    }

    @NotNull
    @Override
    public BlockData getBlockData(@NotNull Location location) {
        return null;
    }

    @NotNull
    @Override
    public BlockData getBlockData(int i, int i1, int i2) {
        return null;
    }

    @NotNull
    @Override
    public Material getType(@NotNull Location location) {
        return null;
    }

    @NotNull
    @Override
    public Material getType(int i, int i1, int i2) {
        return null;
    }

    @Override
    public void setBlockData(@NotNull Location location, @NotNull BlockData blockData) {

    }

    @Override
    public void setBlockData(int i, int i1, int i2, @NotNull BlockData blockData) {

    }

    @Override
    public void setType(@NotNull Location location, @NotNull Material material) {

    }

    @Override
    public void setType(int i, int i1, int i2, @NotNull Material material) {

    }

    @NotNull
    @Override
    public Entity spawnEntity(@NotNull Location location, @NotNull EntityType entityType) {
        return null;
    }

    @NotNull
    @Override
    public Entity spawnEntity(@NotNull Location location, @NotNull EntityType entityType, boolean b) {
        return null;
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull java.util.Random random, @NotNull TreeType treeType, @Nullable java.util.function.Predicate<BlockState> predicate) {
        return false;
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull java.util.Random random, @NotNull TreeType treeType, @Nullable Consumer<BlockState> consumer) {
        return false;
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull java.util.Random random, @NotNull TreeType treeType) {
        return false;
    }

    @NotNull
    @Override
    public java.util.List<Entity> getEntities() {
        return null;
    }

    @NotNull
    @Override
    public java.util.List<LivingEntity> getLivingEntities() {
        return null;
    }

    @NotNull
    @Override
    public <T extends Entity> T spawn(@NotNull Location location, @NotNull Class<T> aClass, boolean b, @Nullable Consumer<T> consumer) throws IllegalArgumentException {
        return null;
    }

    @NotNull
    @Override
    public <T extends Entity> T spawn(@NotNull Location location, @NotNull Class<T> aClass, @Nullable Consumer<T> consumer) throws IllegalArgumentException {
        return null;
    }

    @NotNull
    @Override
    public <T extends Entity> T spawn(@NotNull Location location, @NotNull Class<T> aClass) throws IllegalArgumentException {
        return null;
    }

    @NotNull
    @Override
    public java.util.List<Player> getPlayers() {
        return null;
    }

    @NotNull
    @Override
    public java.util.Collection<Entity> getNearbyEntities(@NotNull Location location, double v, double v1, double v2) {
        return null;
    }

    @NotNull
    @Override
    public java.util.Collection<Entity> getNearbyEntities(@NotNull BoundingBox boundingBox) {
        return null;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceEntities(@NotNull Location location, @NotNull Vector vector, double v) {
        return null;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceEntities(@NotNull Location location, @NotNull Vector vector, double v, double v1) {
        return null;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(@NotNull Location location, @NotNull Vector vector, double v) {
        return null;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(@NotNull Location location, @NotNull Vector vector, double v, @NotNull FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(@NotNull Location location, @NotNull Vector vector, double v, @NotNull FluidCollisionMode fluidCollisionMode, boolean b) {
        return null;
    }

    @NotNull
    @Override
    public Location getSpawnLocation() {
        return null;
    }

    @Override
    public boolean setSpawnLocation(@NotNull Location location) {
        return false;
    }

    @Override
    public boolean setSpawnLocation(int i, int i1, int i2, float v) {
        return false;
    }

    @Override
    public boolean setSpawnLocation(int i, int i1, int i2) {
        return false;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public void setTime(long l) {

    }

    @Override
    public long getFullTime() {
        return 0;
    }

    @Override
    public void setFullTime(long l) {

    }

    @Override
    public long getGameTime() {
        return 0;
    }

    @Override
    public boolean hasStorm() {
        return false;
    }

    @Override
    public void setStorm(boolean b) {

    }

    @Override
    public int getWeatherDuration() {
        return 0;
    }

    @Override
    public void setWeatherDuration(int i) {

    }

    @Override
    public boolean isThundering() {
        return false;
    }

    @Override
    public void setThundering(boolean b) {

    }

    @Override
    public int getThunderDuration() {
        return 0;
    }

    @Override
    public void setThunderDuration(int i) {

    }

    @Override
    public boolean isClearWeather() {
        return false;
    }

    @Override
    public void setClearWeatherDuration(int i) {

    }

    @Override
    public int getClearWeatherDuration() {
        return 0;
    }

    @Override
    public boolean createExplosion(double v, double v1, double v2, float v3) {
        return false;
    }

    @Override
    public boolean createExplosion(double v, double v1, double v2, float v3, boolean b) {
        return false;
    }

    @Override
    public boolean createExplosion(double v, double v1, double v2, float v3, boolean b, boolean b1) {
        return false;
    }

    @Override
    public boolean createExplosion(double v, double v1, double v2, float v3, boolean b, boolean b1, @Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean createExplosion(@NotNull Location location, float v) {
        return false;
    }

    @Override
    public boolean createExplosion(@NotNull Location location, float v, boolean b) {
        return false;
    }

    @Override
    public boolean createExplosion(@NotNull Location location, float v, boolean b, boolean b1) {
        return false;
    }

    @Override
    public boolean createExplosion(@NotNull Location location, float v, boolean b, boolean b1, @Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean getPVP() {
        return false;
    }

    @Override
    public void setPVP(boolean b) {

    }

    @Nullable
    @Override
    public ChunkGenerator getGenerator() {
        return null;
    }

    @Nullable
    @Override
    public BiomeProvider getBiomeProvider() {
        return null;
    }

    @Override
    public void save() {

    }

    @NotNull
    @Override
    public java.util.List<BlockPopulator> getPopulators() {
        return null;
    }

    @NotNull
    @Override
    @Deprecated
    public FallingBlock spawnFallingBlock(@NotNull Location location, @NotNull MaterialData materialData) throws IllegalArgumentException {
        return null;
    }

    @NotNull
    @Override
    public FallingBlock spawnFallingBlock(@NotNull Location location, @NotNull BlockData blockData) throws IllegalArgumentException {
        return null;
    }

    @NotNull
    @Override
    public FallingBlock spawnFallingBlock(@NotNull Location location, @NotNull Material material, byte b) throws IllegalArgumentException {
        return null;
    }

    @Override
    public void playEffect(@NotNull Location location, @NotNull Effect effect, int i) {

    }

    @Override
    public void playEffect(@NotNull Location location, @NotNull Effect effect, int i, int i1) {

    }

    @NotNull
    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(int i, int i1, boolean b, boolean b1) {
        return null;
    }

    @Override
    public void setSpawnFlags(boolean b, boolean b1) {

    }

    @Override
    public boolean getAllowAnimals() {
        return false;
    }

    @Override
    public boolean getAllowMonsters() {
        return false;
    }

    @NotNull
    @Override
    public Biome getBiome(int i, int i1) {
        return null;
    }

    @Override
    public void setBiome(int i, int i1, @NotNull Biome biome) {

    }

    @Override
    public double getTemperature(int i, int i1) {
        return 0;
    }

    @Override
    public double getTemperature(int i, int i1, int i2) {
        return 0;
    }

    @Override
    public double getHumidity(int i, int i1) {
        return 0;
    }

    @Override
    public double getHumidity(int i, int i1, int i2) {
        return 0;
    }

    @Override
    public int getLogicalHeight() {
        return 0;
    }

    @Override
    public boolean isNatural() {
        return false;
    }

    @Override
    public boolean isBedWorks() {
        return false;
    }

    @Override
    public boolean hasSkyLight() {
        return false;
    }

    @Override
    public boolean hasCeiling() {
        return false;
    }

    @Override
    public boolean isPiglinSafe() {
        return false;
    }

    @Override
    public boolean isRespawnAnchorWorks() {
        return false;
    }

    @Override
    public boolean hasRaids() {
        return false;
    }

    @Override
    public boolean isUltraWarm() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public boolean getKeepSpawnInMemory() {
        return false;
    }

    @Override
    public void setKeepSpawnInMemory(boolean b) {

    }

    @Override
    public boolean isAutoSave() {
        return false;
    }

    @Override
    public void setAutoSave(boolean b) {

    }

    @Override
    public void setDifficulty(@NotNull Difficulty difficulty) {

    }

    @NotNull
    @Override
    public Difficulty getDifficulty() {
        return null;
    }

    @NotNull
    @Override
    public java.io.File getWorldFolder() {
        return null;
    }

    @Nullable
    @Override
    public WorldType getWorldType() {
        return null;
    }

    @Override
    public boolean canGenerateStructures() {
        return false;
    }

    @Override
    public boolean isHardcore() {
        return false;
    }

    @Override
    public void setHardcore(boolean b) {

    }

    @Override
    public long getTicksPerAnimalSpawns() {
        return 0;
    }

    @Override
    public void setTicksPerAnimalSpawns(int i) {

    }

    @Override
    public long getTicksPerMonsterSpawns() {
        return 0;
    }

    @Override
    public void setTicksPerMonsterSpawns(int i) {

    }

    @Override
    public long getTicksPerWaterSpawns() {
        return 0;
    }

    @Override
    public void setTicksPerWaterSpawns(int i) {

    }

    @Override
    public long getTicksPerWaterAmbientSpawns() {
        return 0;
    }

    @Override
    public void setTicksPerWaterAmbientSpawns(int i) {

    }

    @Override
    public long getTicksPerWaterUndergroundCreatureSpawns() {
        return 0;
    }

    @Override
    public void setTicksPerWaterUndergroundCreatureSpawns(int i) {

    }

    @Override
    public long getTicksPerAmbientSpawns() {
        return 0;
    }

    @Override
    public void setTicksPerAmbientSpawns(int i) {

    }

    @Override
    public long getTicksPerSpawns(@NotNull SpawnCategory spawnCategory) {
        return 0;
    }

    @Override
    public void setTicksPerSpawns(@NotNull SpawnCategory spawnCategory, int i) {

    }

    @Override
    public int getMonsterSpawnLimit() {
        return 0;
    }

    @Override
    public void setMonsterSpawnLimit(int i) {

    }

    @Override
    public int getAnimalSpawnLimit() {
        return 0;
    }

    @Override
    public void setAnimalSpawnLimit(int i) {

    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        return 0;
    }

    @Override
    public void setWaterAnimalSpawnLimit(int i) {

    }

    @Override
    public int getWaterUndergroundCreatureSpawnLimit() {
        return 0;
    }

    @Override
    public void setWaterUndergroundCreatureSpawnLimit(int i) {

    }

    @Override
    public int getWaterAmbientSpawnLimit() {
        return 0;
    }

    @Override
    public void setWaterAmbientSpawnLimit(int i) {

    }

    @Override
    public int getAmbientSpawnLimit() {
        return 0;
    }

    @Override
    public void setAmbientSpawnLimit(int i) {

    }

    @Override
    public int getSpawnLimit(@NotNull SpawnCategory spawnCategory) {
        return 0;
    }

    @Override
    public void setSpawnLimit(@NotNull SpawnCategory spawnCategory, int i) {

    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, float v, float v1) {

    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, @NotNull SoundCategory soundCategory, float v, float v1) {

    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, float v, float v1) {

    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, @NotNull SoundCategory soundCategory, float v, float v1) {

    }

    @NotNull
    @Override
    public String[] getGameRules() {
        return new String[0];
    }

    @Nullable
    @Override
    public <T> T getGameRuleValue(@NotNull GameRule<T> gameRule) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getGameRuleDefault(@NotNull GameRule<T> gameRule) {
        return null;
    }

    @NotNull
    @Override
    public WorldBorder getWorldBorder() {
        return null;
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i) {

    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i) {

    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2) {

    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5) {

    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, double v3) {

    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6) {

    }

    @Nullable
    @Override
    public Location locateNearestStructure(@NotNull Location location, @NotNull StructureType structureType, int i, boolean b) {
        return null;
    }

    @Override
    public int getViewDistance() {
        return 0;
    }

    @Override
    public int getSimulationDistance() {
        return 0;
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return null;
    }

    @Nullable
    @Override
    public Raid locateNearestRaid(@NotNull Location location, int i) {
        return null;
    }

    @NotNull
    @Override
    public java.util.List<Raid> getRaids() {
        return null;
    }

    @Nullable
    @Override
    public DragonBattle getEnderDragonBattle() {
        return null;
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, @Nullable T t, boolean b) {

    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, double v3, @Nullable T t, boolean b) {

    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, @Nullable T t) {

    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, double v3, @Nullable T t) {

    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, @Nullable T t) {

    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, double v, double v1, double v2, @Nullable T t) {

    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i, @Nullable T t) {

    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i, @Nullable T t) {

    }

    @Override
    public <T> boolean setGameRule(@NotNull GameRule<T> gameRule, @NotNull T t) {
        return false;
    }

    @Override
    public boolean isGameRule(@NotNull String s) {
        return false;
    }

    @Override
    public boolean setGameRuleValue(@NotNull String s, @NotNull String s1) {
        return false;
    }

    @Nullable
    @Override
    public String getGameRuleValue(@Nullable String s) {
        return null;
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String s, @NotNull SoundCategory soundCategory, float v, float v1) {

    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String s, float v, float v1) {

    }

    @Override
    public <T> void playEffect(@NotNull Location location, @NotNull Effect effect, @Nullable T t, int i) {

    }

    @Override
    public <T> void playEffect(@NotNull Location location, @NotNull Effect effect, @Nullable T t) {

    }

    @Nullable
    @Override
    public RayTraceResult rayTrace(@NotNull Location location, @NotNull Vector vector, double v, @NotNull FluidCollisionMode fluidCollisionMode, boolean b, double v1, @Nullable java.util.function.Predicate<Entity> predicate) {
        return null;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceEntities(@NotNull Location location, @NotNull Vector vector, double v, double v1, @Nullable java.util.function.Predicate<Entity> predicate) {
        return null;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceEntities(@NotNull Location location, @NotNull Vector vector, double v, @Nullable java.util.function.Predicate<Entity> predicate) {
        return null;
    }

    @NotNull
    @Override
    public java.util.Collection<Entity> getNearbyEntities(@NotNull BoundingBox boundingBox, @Nullable java.util.function.Predicate<Entity> predicate) {
        return null;
    }

    @NotNull
    @Override
    public java.util.Collection<Entity> getNearbyEntities(@NotNull Location location, double v, double v1, double v2, @Nullable java.util.function.Predicate<Entity> predicate) {
        return null;
    }

    @NotNull
    @Override
    public java.util.Collection<Entity> getEntitiesByClasses(@NotNull Class<?>... classes) {
        return null;
    }

    @NotNull
    @Override
    public <T extends Entity> java.util.Collection<T> getEntitiesByClass(@NotNull Class<T> aClass) {
        return null;
    }

    @SafeVarargs
    @NotNull
    @Override
    public final <T extends Entity> java.util.Collection<T> getEntitiesByClass(@NotNull Class<T>... classes) {
        return null;
    }

    @NotNull
    @Override
    public <T extends AbstractArrow> T spawnArrow(@NotNull Location location, @NotNull Vector vector, float v, float v1, @NotNull Class<T> aClass) {
        return null;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @NotNull
    @Override
    public java.util.UUID getUID() {
        return this.uuid;
    }

    @NotNull
    @Override
    public Environment getEnvironment() {
        return null;
    }

    @Override
    public long getSeed() {
        return 0;
    }

    @Override
    public int getMinHeight() {
        return 0;
    }

    @Override
    public int getMaxHeight() {
        return 0;
    }

    @Override
    public void setMetadata(@NotNull String s, @NotNull MetadataValue metadataValue) {

    }

    @NotNull
    @Override
    public java.util.List<MetadataValue> getMetadata(@NotNull String s) {
        return null;
    }

    @Override
    public boolean hasMetadata(@NotNull String s) {
        return false;
    }

    @Override
    public void removeMetadata(@NotNull String s, @NotNull Plugin plugin) {

    }

    @NotNull
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return null;
    }

    @Override
    public void sendPluginMessage(@NotNull Plugin plugin, @NotNull String s, byte @NotNull [] bytes) {

    }

    @NotNull
    @Override
    public java.util.Set<String> getListeningPluginChannels() {
        return null;
    }
}
