package space.devport.utils.holograms.provider;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class CMIHolograms extends HologramProvider {

    public final HologramManager hologramManager;

    public CMIHolograms() {
        this.hologramManager = CMI.getInstance().getHologramManager();
    }

    @Override
    public void createHologram(String id, Location location, List<String> content) {
        CMIHologram hologram = new CMIHologram(id, location);
        hologram.setLines(content);
        hologramIdList.add(id);
        hologramManager.addHologram(hologram);
        hologram.update();
        hologramManager.save();
    }

    @Override
    public void createItemHologram(String id, Location location, ItemStack item) {
        CMIHologram hologram = new CMIHologram(id, location);
        hologram.setLines(Collections.singletonList(String.format("SICON:%s", item.getType().toString())));
        hologramIdList.add(id);
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
        CMIHologram hologram = new CMIHologram(id, location);
        hologram.setLines(Collections.singletonList(String.format("ICON:%s", item.getType().toString())));
        hologramIdList.add(id);
        hologramManager.addHologram(hologram);
        hologram.update();
        hologramManager.save();
    }

    @Override
    public void deleteHologram(String id) {
        if (!hologramIdList.contains(id)) return;

        hologramIdList.remove(id);
        hologramManager.removeHolo(hologramManager.getByName(id));
        hologramManager.save();
    }

    @Override
    public void updateHologram(String id, List<String> newContent) {
        Location location = hologramManager.getByName(id).getLoc();
        deleteHologram(id);
        createHologram(id, location, newContent);
    }

    @Override
    public void updateItemHologram(String id, ItemStack item) {
        Location location = hologramManager.getByName(id).getLoc();
        deleteHologram(id);
        createItemHologram(location, item);
    }

    @Override
    public void updateAnimatedHologram(String id, List<String> newContent, int delay) {
        throw new UnsupportedOperationException("CMI doesn't implement animated holograms");
    }

    @Override
    public void moveHologram(String id, Location newLocation) {
        CMIHologram cmiHologram = hologramManager.getByName(id);
        cmiHologram.setLoc(newLocation);
    }

    @Override
    public void updateAnimatedItem(String id, ItemStack item, int delay) {
        Location location = hologramManager.getByName(id).getLoc();
        deleteHologram(id);
        createAnimatedItem(location, item, delay);
    }
}
