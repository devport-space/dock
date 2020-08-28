package space.devport.utils.holograms.provider;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.HologramPlugin;
import com.sainttx.holograms.api.animation.ItemAnimation;
import com.sainttx.holograms.api.animation.TextAnimation;
import com.sainttx.holograms.api.line.AnimatedItemLine;
import com.sainttx.holograms.api.line.AnimatedTextLine;
import com.sainttx.holograms.api.line.ItemLine;
import com.sainttx.holograms.api.line.TextLine;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportPlugin;

import java.util.List;

public class Holograms extends HologramProvider {

    private final HologramManager hologramManager;

    public Holograms() {
        this.hologramManager = ((HologramPlugin) DevportPlugin.getInstance().getPluginManager().getPlugin("Holograms")).getHologramManager();
    }

    @Override
    public boolean exists(String id) {
        return hologramManager.getHologram(id) != null;
    }

    @Override
    public Location getLocation(String id) {
        if (!registeredHolograms.contains(id)) return null;
        Hologram hologram = hologramManager.getHologram(id);
        return hologram != null ? hologram.getLocation().clone() : null;
    }

    @Override
    public void createHologram(String id, Location location, List<String> content) {
        Hologram hologram = new Hologram(id, location, true);
        registeredHolograms.add(id);
        for (String s : content) {
            hologram.addLine(new TextLine(hologram, s));
        }
        hologramManager.addActiveHologram(hologram);
        hologram.spawn();
    }

    @Override
    public void createItemHologram(String id, Location location, ItemStack item) {
        Hologram hologram = new Hologram(id, location, true);
        registeredHolograms.add(id);
        hologram.addLine(new ItemLine(hologram, item));
        hologramManager.addActiveHologram(hologram);
        hologram.spawn();
    }

    @Override
    public void createAnimatedHologram(String id, Location location, List<String> content, int delay) {
        Hologram hologram = new Hologram(id, location, true);
        registeredHolograms.add(id);
        hologram.addLine(new AnimatedTextLine(hologram, new TextAnimation(content), delay));
        hologramManager.addActiveHologram(hologram);
        hologram.spawn();
    }

    @Override
    public void createAnimatedItem(String id, Location location, ItemStack item, int delay) {
        Hologram hologram = new Hologram(id, location, true);
        registeredHolograms.add(id);
        hologram.addLine(new AnimatedItemLine(hologram, new ItemAnimation(item), delay));
        hologramManager.addActiveHologram(hologram);
        hologram.spawn();
    }

    @Override
    public void deleteHologram(String id) {
        if (!registeredHolograms.contains(id) || hologramManager.getHologram(id) == null) return;

        registeredHolograms.remove(id);
        hologramManager.deleteHologram(hologramManager.getHologram(id));
    }

    @Override
    public void updateHologram(String id, List<String> newContent) {
        if (!registeredHolograms.contains(id)) return;

        Location location = hologramManager.getHologram(id).getLocation();
        deleteHologram(id);
        createHologram(id, location, newContent);
    }

    @Override
    public void moveHologram(String id, Location newLocation) {
        if (!registeredHolograms.contains(id)) return;

        hologramManager.getHologram(id).teleport(newLocation);
    }

    @Override
    public void updateItemHologram(String id, ItemStack item) {
        if (!registeredHolograms.contains(id)) return;

        Location location = hologramManager.getHologram(id).getLocation();
        deleteHologram(id);
        createItemHologram(id, location, item);
    }

    @Override
    public void updateAnimatedHologram(String id, List<String> newContent, int delay) {
        if (!registeredHolograms.contains(id)) return;

        Location location = hologramManager.getHologram(id).getLocation();
        deleteHologram(id);
        createAnimatedHologram(id, location, newContent, delay);
    }

    @Override
    public void updateAnimatedItem(String id, ItemStack item, int delay) {
        if (!registeredHolograms.contains(id)) return;

        Location location = hologramManager.getHologram(id).getLocation();
        deleteHologram(id);
        createAnimatedItem(id, location, item, delay);
    }
}