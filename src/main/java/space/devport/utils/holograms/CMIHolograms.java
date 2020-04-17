package space.devport.utils.holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.utility.LocationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CMIHolograms extends HologramsHook {

    public CMI plugin;
    private List<String> hologramIdList = new ArrayList<>();

    public CMIHolograms() {
        plugin = CMI.getInstance();
    }

    @Override
    public void createHologram(Location loc, List<String> lines) {
        String id = LocationUtil.locationToString(loc);
        CMIHologram hologram = new CMIHologram(id, loc);
        hologram.setLines(lines);
        hologramIdList.add(id);
        plugin.getHologramManager().addHologram(hologram);
        hologram.update();
        plugin.getHologramManager().save();
    }

    @Override
    public void createItemHologram(Location loc, ItemStack item) {
        String id = LocationUtil.locationToString(loc);
        CMIHologram hologram = new CMIHologram(id, loc);
        hologram.setLines(Arrays.asList(String.format("SICON:%s", item.getType().toString())));
        hologramIdList.add(id);
        plugin.getHologramManager().addHologram(hologram);
        hologram.update();
        plugin.getHologramManager().save();
    }

    @Override
    public void createAnimatedHologram(Location loc, List<String> lines, int delay) {
        throw new UnsupportedOperationException("CMI doesn't implement animated holograms");
    }

    @Override
    public void createAnimatedItem(Location loc, ItemStack item, int delay) {
        String id = LocationUtil.locationToString(loc);
        CMIHologram hologram = new CMIHologram(id, loc);
        hologram.setLines(Arrays.asList(String.format("IICON:%s", item.getType().toString())));
        hologramIdList.add(id);
        plugin.getHologramManager().addHologram(hologram);
        hologram.update();
        plugin.getHologramManager().save();
    }

    @Override
    public void deleteHologram(Location loc) {
        deleteHologram(LocationUtil.locationToString(loc));
    }

    @Override
    public void deleteHologram(String id) {
        if(hologramIdList.contains(id)) {
            hologramIdList.remove(id);
            plugin.getHologramManager().removeHolo(plugin.getHologramManager().getByName(id));
            plugin.getHologramManager().save();
        }
    }

    @Override
    public void updateHologram(Location loc, List<String> newLines) {
        deleteHologram(loc);
        createHologram(loc, newLines);
    }

    @Override
    public void updateItemHologram(Location loc, ItemStack item) {
        deleteHologram(loc);
        createItemHologram(loc, item);
    }

    @Override
    public void updateAnimatedHologram(Location loc, List<String> lines, int delay) {
        throw new UnsupportedOperationException("CMI doesn't implement animated holograms");
    }

    @Override
    public void updateAnimatedItem(Location loc, ItemStack item, int delay) {
        deleteHologram(loc);
        createAnimatedItem(loc, item, delay);
    }

    @Override
    public void removeAll() {
        for(String id : hologramIdList) {
            deleteHologram(id);
        }
    }
}
