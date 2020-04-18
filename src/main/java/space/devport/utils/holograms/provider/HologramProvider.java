package space.devport.utils.holograms.provider;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportPlugin;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.utility.LocationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In default holograms will not persist and only exist as long as they're registered here.
 */
public abstract class HologramProvider {

    private final DevportPlugin plugin;

    private final Configuration storage;

    protected final List<String> registeredHolograms = new ArrayList<>();

    public HologramProvider() {
        this.plugin = DevportPlugin.getInstance();

        storage = new Configuration(plugin, "holograms");

        for (String id : storage.getFileConfiguration().getKeys(false)) {
            Location location = LocationUtil.locationFromString(storage.getFileConfiguration().getString(id));
            addHologram(id, location);
        }

        plugin.getConsoleOutput().info("Loaded " + registeredHolograms.size() + " hologram(s)...");
    }

    public void addHologram(String id, Location location) {
        this.registeredHolograms.add(id);
    }

    public void save() {
        storage.clear();

        for (String id : registeredHolograms) {
            storage.getFileConfiguration().set(id, LocationUtil.locationToString(getLocation(id)));
        }

        storage.save();
    }

    private String nextId() {
        String id = "devport_holo_" + getHolograms().size();
        if (getHolograms().contains(id)) return nextId();
        return id;
    }

    public abstract Location getLocation(String id);

    public List<String> getHolograms() {
        return Collections.unmodifiableList(registeredHolograms);
    }

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