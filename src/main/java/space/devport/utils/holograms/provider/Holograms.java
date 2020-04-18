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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Holograms extends HologramProvider {

    private final HologramManager hologramManager;

    public Holograms() {
        this.hologramManager = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
    }

    @Override
    public void createHologram(String id, Location location, List<String> content) {
        Hologram hologram = new Hologram(id, location, true);
        hologramIdList.add(id);
        for (String s : content) {
            hologram.addLine(new TextLine(hologram, s));
        }
        hologramManager.addActiveHologram(hologram);
        hologram.spawn();
    }

    @Override
    public void createItemHologram(String id, Location location, ItemStack item) {
        Hologram hologram = new Hologram(id, location, true);
        hologramIdList.add(id);
        hologram.addLine(new ItemLine(hologram, item));
        hologramManager.addActiveHologram(hologram);
        hologram.spawn();
    }

    @Override
    public void createAnimatedHologram(String id, Location location, List<String> content, int delay) {
        Hologram hologram = new Hologram(id, location, true);
        hologramIdList.add(id);
        hologram.addLine(new AnimatedTextLine(hologram, new TextAnimation(content), delay));
        hologramManager.addActiveHologram(hologram);
        hologram.spawn();
    }

    @Override
    public void createAnimatedItem(String id, Location location, ItemStack item, int delay) {
        Hologram hologram = new Hologram(id, location, true);
        hologramIdList.add(id);
        hologram.addLine(new AnimatedItemLine(hologram, new ItemAnimation(item), delay));
        hologramManager.addActiveHologram(hologram);
        hologram.spawn();
    }

    @Override
    public void deleteHologram(String id) {
        if (!hologramIdList.contains(id)) return;

        hologramIdList.remove(id);
        hologramManager.deleteHologram(hologramManager.getHologram(id));
    }

    @Override
    public void updateHologram(String id, List<String> newContent) {
        if (!hologramIdList.contains(id)) return;

        Location location = hologramManager.getHologram(id).getLocation();
        deleteHologram(id);
        createHologram(id, location, newContent);
    }

    @Override
    public void moveHologram(String id, Location newLocation) {
        if (!hologramIdList.contains(id)) return;

        hologramManager.getHologram(id).teleport(newLocation);
    }

    @Override
    public void updateItemHologram(String id, ItemStack item) {
        if (!hologramIdList.contains(id)) return;

        Location location = hologramManager.getHologram(id).getLocation();
        deleteHologram(id);
        createItemHologram(id, location, item);
    }

    @Override
    public void updateAnimatedHologram(String id, List<String> newContent, int delay) {
        if (!hologramIdList.contains(id)) return;

        Location location = hologramManager.getHologram(id).getLocation();
        deleteHologram(id);
        createAnimatedHologram(id, location, newContent, delay);
    }

    @Override
    public void updateAnimatedItem(String id, ItemStack item, int delay) {
        if (!hologramIdList.contains(id)) return;

        Location location = hologramManager.getHologram(id).getLocation();
        deleteHologram(id);
        createAnimatedItem(id, location, item, delay);
    }
}