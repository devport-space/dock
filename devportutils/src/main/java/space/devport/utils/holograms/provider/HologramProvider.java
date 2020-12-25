package space.devport.utils.holograms.provider;

import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportPlugin;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.utility.FastUUID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * In default holograms will not persist and only exist as long as they're registered here.
 */
public abstract class HologramProvider {

    protected final DevportPlugin plugin;

    protected final List<String> registeredHolograms = new ArrayList<>();

    private Configuration storage;

    public HologramProvider(DevportPlugin plugin) {
        this.plugin = plugin;

        load();
    }

    public void load() {
        registeredHolograms.clear();

        if (storage == null)
            storage = new Configuration(plugin, "holograms");
        else storage.load();

        for (String id : storage.getFileConfiguration().getKeys(false)) {
            Location location = plugin.getLocationUtil().locationFromString(storage.getFileConfiguration().getString(id));
            addHologram(id, location);
        }

        plugin.getConsoleOutput().info("Loaded " + registeredHolograms.size() + " hologram(s)...");

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this::purgeNonexistent, 40);
    }

    public void addHologram(String id, Location location) {
        this.registeredHolograms.add(id);
    }

    public void save() {
        storage.clear();
        purgeNonexistent();

        for (String id : registeredHolograms) {
            String locationString = plugin.getLocationUtil().locationToString(getLocation(id));

            if (Strings.isNullOrEmpty(locationString)) {
                plugin.getConsoleOutput().warn("Could not save hologram " + id + ", it's location is invalid.");
                continue;
            }


            storage.getFileConfiguration().set(id, locationString);
        }

        storage.save();
    }

    private String nextId() {
        return FastUUID.toString(UUID.randomUUID()).split("-")[0];
    }

    public abstract Location getLocation(String id);

    private void purgeNonexistent() {
        this.registeredHolograms.removeIf(id -> !exists(id));
    }

    public List<String> getHolograms() {
        purgeNonexistent();
        return Collections.unmodifiableList(registeredHolograms);
    }

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

    public void removeAll() {
        for (String id : getHolograms()) {
            deleteHologram(id);
        }
    }
}