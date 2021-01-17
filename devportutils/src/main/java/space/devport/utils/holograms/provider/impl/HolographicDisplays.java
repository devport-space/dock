package space.devport.utils.holograms.provider.impl;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportPlugin;
import space.devport.utils.holograms.provider.HologramProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolographicDisplays extends HologramProvider {

    // We need a separate cache, because HoloDisplay API is retarded.
    private final Map<String, Hologram> holograms = new HashMap<>();

    public HolographicDisplays(DevportPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean exists(String id) {
        return holograms.containsKey(id);
    }

    @Override
    public Location getLocation(String id) {
        Hologram hologram = getHologram(id);
        return hologram != null ? hologram.getLocation().clone() : null;
    }

    @Override
    public void addHologram(String id, Location location) {
        Hologram hologram = HologramsAPI.getHolograms(plugin).stream()
                .filter(h -> h.getLocation().equals(location))
                .findAny().orElse(null);

        if (hologram == null)
            return;

        super.addHologram(id, location);
        holograms.put(id, hologram);
    }

    private Hologram getHologram(String id) {
        return holograms.getOrDefault(id, null);
    }

    @Override
    public void createHologram(String id, Location location, List<String> content) {
        Hologram hologram = HologramsAPI.createHologram(plugin, location);

        super.addHologram(id, location);
        holograms.put(id, hologram);

        for (String s : content) {
            hologram.appendTextLine(s);
        }
    }

    @Override
    public void createItemHologram(String id, Location location, ItemStack item) {
        Hologram hologram = HologramsAPI.createHologram(plugin, location);

        super.addHologram(id, location);
        holograms.put(id, hologram);

        hologram.appendItemLine(item);
    }

    @Override
    public void deleteHologram(String id) {
        Hologram hologram = getHologram(id);
        if (hologram != null)
            hologram.delete();
    }

    @Override
    public void updateHologram(String id, List<String> newContent) {
        Hologram hologram = getHologram(id);

        if (hologram == null) return;

        Location location = hologram.getLocation();
        deleteHologram(id);
        createHologram(id, location, newContent);
    }

    @Override
    public void updateItemHologram(String id, ItemStack item) {
        Hologram hologram = getHologram(id);

        if (hologram == null) return;

        Location location = hologram.getLocation();
        deleteHologram(id);
        createItemHologram(id, location, item);
    }

    @Override
    public void updateAnimatedHologram(String id, List<String> newContent, int delay) {
        throw new UnsupportedOperationException("Holographic displays doesn't implement animated holograms");
    }

    @Override
    public void updateAnimatedItem(String id, ItemStack item, int delay) {
        throw new UnsupportedOperationException("Holographic displays doesn't implement animated holograms");
    }

    @Override
    public void createAnimatedHologram(String id, Location location, List<String> lines, int delay) {
        throw new UnsupportedOperationException("Holographic displays doesn't implement animated holograms");
    }

    @Override
    public void createAnimatedItem(String id, Location location, ItemStack item, int delay) {
        throw new UnsupportedOperationException("Holographic displays doesn't implement animated holograms");
    }

    @Override
    public void moveHologram(String id, Location newLocation) {
        Hologram hologram = getHologram(id);
        if (hologram == null) return;
        hologram.teleport(newLocation);
    }
}