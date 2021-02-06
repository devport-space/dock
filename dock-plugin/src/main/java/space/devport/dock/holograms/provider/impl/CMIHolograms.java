package space.devport.dock.holograms.provider.impl;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMILocation;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.dock.DockedPlugin;
import space.devport.dock.holograms.provider.HologramProvider;

import java.util.Collections;
import java.util.List;

public class CMIHolograms extends HologramProvider {

    public final HologramManager hologramManager;

    public CMIHolograms(DockedPlugin plugin) {
        super(plugin);
        this.hologramManager = CMI.getInstance().getHologramManager();
    }

    @Override
    public boolean exists(String id) {
        return this.hologramManager.getByName(id) != null;
    }

    @Override
    public Location getLocation(String id) {
        CMIHologram hologram = hologramManager.getByName(id);
        return hologram == null ? null : hologram.getLocation().getBukkitLoc().clone();
    }

    @Override
    public void createHologram(String id, Location location, List<String> content) {
        CMILocation cmiLocation = new CMILocation(location);
        CMIHologram hologram = new CMIHologram(id, cmiLocation);

        super.addHologram(id, location);

        hologram.setSaveToFile(true);
        hologram.setLines(content);
        hologramManager.addHologram(hologram);
        hologram.update();

        hologramManager.save();
    }

    @Override
    public void createItemHologram(String id, Location location, ItemStack item) {
        CMILocation cmiLocation = new CMILocation(location);
        CMIHologram hologram = new CMIHologram(id, cmiLocation);

        super.addHologram(id, location);
        hologram.setLines(Collections.singletonList(String.format("SICON:%s", item.getType().toString())));
        hologramManager.addHologram(hologram);
        hologram.update();
        hologramManager.save();
    }

    @Override
    public void createAnimatedHologram(String id, Location location, List<String> lines, int delay) {
        throw new UnsupportedOperationException("CMI doesn't implement animated holograms");
    }

    @Override
    public void createAnimatedItem(String id, Location location, ItemStack item, int delay) {
        CMILocation cmiLocation = new CMILocation(location);
        CMIHologram hologram = new CMIHologram(id, cmiLocation);
        super.addHologram(id, location);
        hologram.setLines(Collections.singletonList(String.format("ICON:%s", item.getType().toString())));
        hologramManager.addHologram(hologram);
        hologram.update();
        hologramManager.save();
    }

    @Override
    public void deleteHologram(String id) {
        if (!exists(id)) return;

        super.removeHologram(id);
        hologramManager.removeHolo(hologramManager.getByName(id));
        hologramManager.save();
    }

    @Override
    public void updateHologram(String id, List<String> newContent) {
        if (!exists(id)) return;
        CMIHologram hologram = hologramManager.getByName(id);
        hologram.setLines(newContent);
        hologram.update();
    }

    @Override
    public void updateItemHologram(String id, ItemStack item) {
        if (!exists(id)) return;
        Location location = hologramManager.getByName(id).getLocation().getBukkitLoc();
        deleteHologram(id);
        createItemHologram(location, item);
    }

    @Override
    public void updateAnimatedHologram(String id, List<String> newContent, int delay) {
        throw new UnsupportedOperationException("CMI doesn't implement animated holograms");
    }

    @Override
    public void moveHologram(String id, Location newLocation) {
        if (!exists(id)) return;
        CMIHologram cmiHologram = hologramManager.getByName(id);
        cmiHologram.setLoc(newLocation);
    }

    @Override
    public void updateAnimatedItem(String id, ItemStack item, int delay) {
        if (!exists(id)) return;
        Location location = hologramManager.getByName(id).getLocation().getBukkitLoc();
        deleteHologram(id);
        createAnimatedItem(location, item, delay);
    }
}
