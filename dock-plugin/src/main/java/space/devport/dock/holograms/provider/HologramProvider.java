package space.devport.dock.holograms.provider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.dock.DockedPlugin;
import space.devport.dock.api.IDockedPlugin;
import space.devport.dock.configuration.Configuration;
import space.devport.dock.utility.FastUUID;
import space.devport.dock.utility.LocationUtil;

import java.util.*;

@Slf4j
public abstract class HologramProvider {

    protected final IDockedPlugin plugin;

    // Holograms that we registered. Saved into holograms.yml
    private final Set<String> registeredHolograms = new HashSet<>();

    @Getter
    private final Configuration storage;

    public HologramProvider(IDockedPlugin plugin) {
        this.plugin = plugin;
        this.storage = new Configuration(plugin, "holograms");
    }

    public void load() {
        registeredHolograms.clear();

        storage.load();

        for (String id : storage.getFileConfiguration().getKeys(false)) {
            Location location = LocationUtil.parseLocation(storage.getFileConfiguration().getString(id),
                    e -> log.error("Could not parse location from " + e.getInput() + ": " + e.getThrowable().getMessage()));

            if (location != null)
                addHologram(id, location);
        }

        log.info(String.format("Loaded %d hologram(s)...", registeredHolograms.size()));

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin.getPlugin(), this::purgeNonexistent, 40);
    }

    public void save() {
        storage.clear();
        purgeNonexistent();

        for (String id : registeredHolograms) {
            String locationString = LocationUtil.composeString(getLocation(id),
                    e -> log.warn("Failed to parse location from " + e.getInput()));

            if (locationString == null) {
                continue;
            }

            if (locationString.isEmpty()) {
                log.warn("Could not save hologram " + id + ", it's location is invalid.");
                continue;
            }

            storage.getFileConfiguration().set(id, locationString);
        }

        log.info(String.format("Saved %s hologram(s)...", registeredHolograms.size()));
        storage.save();
    }

    public void addHologram(String id, Location location) {
        if (exists(id))
            this.registeredHolograms.add(id);
    }

    public void removeHologram(String id) {
        registeredHolograms.remove(id);
    }

    public boolean hasHologram(String id) {
        return registeredHolograms.contains(id);
    }

    public void deleteAll() {
        for (String id : getHolograms()) {
            deleteHologram(id);
        }
    }

    private String nextId() {
        return FastUUID.toString(UUID.randomUUID()).split("-")[0];
    }

    private void purgeNonexistent() {
        this.registeredHolograms.removeIf(id -> !exists(id));
    }

    public Set<String> getHolograms() {
        purgeNonexistent();
        return Collections.unmodifiableSet(registeredHolograms);
    }

    public abstract Location getLocation(String id);

    public abstract boolean exists(String id);

    public abstract void createHologram(String id, Location loc, List<String> content);

    public void createHologram(Location location, List<String> content) {
        createHologram(nextId(), location, content);
    }

    public abstract void createItemHologram(String id, Location location, ItemStack item);

    public void createItemHologram(Location location, ItemStack item) {
        createItemHologram(nextId(), location, item);
    }

    public abstract void createAnimatedHologram(String id, Location location, List<String> lines, int delay);

    public void createAnimatedHologram(Location location, List<String> lines, int delay) {
        createAnimatedHologram(nextId(), location, lines, delay);
    }

    public abstract void createAnimatedItem(String id, Location location, ItemStack item, int delay);

    public void createAnimatedItem(Location location, ItemStack item, int delay) {
        createAnimatedItem(nextId(), location, item, delay);
    }

    public abstract void deleteHologram(String id);

    public abstract void updateHologram(String id, List<String> newContent);

    public abstract void moveHologram(String id, Location newLocation);

    public abstract void updateItemHologram(String id, ItemStack item);

    public abstract void updateAnimatedHologram(String id, List<String> newContent, int delay);

    public abstract void updateAnimatedItem(String id, ItemStack item, int delay);
}