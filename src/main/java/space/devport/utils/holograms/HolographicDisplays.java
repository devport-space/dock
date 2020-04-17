package space.devport.utils.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import space.devport.utils.DevportPlugin;
import space.devport.utils.utility.LocationUtil;

import java.util.ArrayList;
import java.util.List;

public class HolographicDisplays extends HologramsProvider {

    private final Plugin plugin;
    private final List<String> hologramIdList = new ArrayList<>();

    public HolographicDisplays() {
        plugin = DevportPlugin.getInstance();
    }

    @Override
    public void createHologram(Location loc, List<String> lines) {
        String id = LocationUtil.locationToString(loc);
        Hologram hologram = HologramsAPI.createHologram(plugin, loc);
        hologramIdList.add(id);
        for(String s : lines) {
            hologram.appendTextLine(s);
        }
    }

    @Override
    public void createItemHologram(Location loc, ItemStack item) {
        String id = LocationUtil.locationToString(loc);
        Hologram hologram = HologramsAPI.createHologram(plugin, loc);
        hologramIdList.add(id);
        hologram.appendItemLine(item);
    }

    @Override
    public void createAnimatedHologram(Location loc, List<String> lines, int delay) {
        throw new UnsupportedOperationException("Holographic displays doesn't implement animated holograms");
    }

    @Override
    public void createAnimatedItem(Location loc, ItemStack item, int delay) {
        throw new UnsupportedOperationException("Holographic displays doesn't implement animated holograms");
    }

    @Override
    public void deleteHologram(Location loc) {
        if(loc == null) return;
        for (Hologram hol : new ArrayList<>(HologramsAPI.getHolograms(plugin))) {
            if(hol.getLocation() == loc) {
                hol.delete();
            }
        }
    }

    @Override
    public void deleteHologram(String id) {
        deleteHologram(LocationUtil.locationFromString(id));
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
        throw new UnsupportedOperationException("Holographic displays doesn't implement animated holograms");
    }

    @Override
    public void updateAnimatedItem(Location loc, ItemStack item, int delay) {
        throw new UnsupportedOperationException("Holographic displays doesn't implement animated holograms");
    }

    @Override
    public void removeAll() {
        for (String id : hologramIdList) {
            deleteHologram(id);
        }
    }
}
