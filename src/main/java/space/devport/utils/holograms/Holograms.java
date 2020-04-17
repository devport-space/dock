package space.devport.utils.holograms;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramPlugin;
import com.sainttx.holograms.api.animation.ItemAnimation;
import com.sainttx.holograms.api.animation.TextAnimation;
import com.sainttx.holograms.api.line.AnimatedItemLine;
import com.sainttx.holograms.api.line.AnimatedTextLine;
import com.sainttx.holograms.api.line.ItemLine;
import com.sainttx.holograms.api.line.TextLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.utility.LocationUtil;

import java.util.ArrayList;
import java.util.List;

public class Holograms extends HologramsProvider {

    private final HologramPlugin holograms;
    private final List<String> hologramIdList = new ArrayList<>();

    public Holograms() {
        holograms = (HologramPlugin) Bukkit.getPluginManager().getPlugin("Holograms");
    }

    @Override
    public void createHologram(Location loc, List<String> lines) {
        String id = LocationUtil.locationToString(loc);
        Hologram hologram = new Hologram(id, loc);
        hologramIdList.add(id);
        for (String s : lines) {
            hologram.addLine(new TextLine(hologram, s));
        }
        holograms.getHologramManager().addActiveHologram(hologram);
    }

    @Override
    public void createItemHologram(Location loc, ItemStack item) {
        String id = LocationUtil.locationToString(loc);
        Hologram hologram = new Hologram(id, loc);
        hologramIdList.add(id);
        hologram.addLine(new ItemLine(hologram, item));
        holograms.getHologramManager().addActiveHologram(hologram);
    }

    @Override
    public void createAnimatedHologram(Location loc, List<String> lines, int delay) {
        String id = LocationUtil.locationToString(loc);
        Hologram hologram = new Hologram(id, loc);
        hologramIdList.add(id);
        hologram.addLine(new AnimatedTextLine(hologram, new TextAnimation(lines), delay));
        holograms.getHologramManager().addActiveHologram(hologram);
    }

    @Override
    public void createAnimatedItem(Location loc, ItemStack item, int delay) {
        String id = LocationUtil.locationToString(loc);
        Hologram hologram = new Hologram(id, loc);
        hologramIdList.add(id);
        hologram.addLine(new AnimatedItemLine(hologram, new ItemAnimation(item), delay));
        holograms.getHologramManager().addActiveHologram(hologram);
    }

    @Override
    public void deleteHologram(Location loc) {
        String id = LocationUtil.locationToString(loc);
        deleteHologram(id);
    }

    @Override
    public void deleteHologram(String id) {
        if (hologramIdList.contains(id)) {
            hologramIdList.remove(id);
            holograms.getHologramManager().deleteHologram(holograms.getHologramManager().getHologram(id));
        }
    }

    @Override
    public void updateHologram(Location loc, List<String> newLines) {
        String id = LocationUtil.locationToString(loc);
        if (!hologramIdList.contains(id)) return;
        deleteHologram(id);
        createHologram(loc, newLines);
    }

    @Override
    public void updateItemHologram(Location loc, ItemStack item) {
        String id = LocationUtil.locationToString(loc);
        if (!hologramIdList.contains(id)) return;
        deleteHologram(id);
        createItemHologram(loc, item);
    }

    @Override
    public void updateAnimatedHologram(Location loc, List<String> lines, int delay) {
        String id = LocationUtil.locationToString(loc);
        if (!hologramIdList.contains(id)) return;
        deleteHologram(id);
        createAnimatedHologram(loc, lines, delay);
    }

    @Override
    public void updateAnimatedItem(Location loc, ItemStack item, int delay) {
        String id = LocationUtil.locationToString(loc);
        if (!hologramIdList.contains(id)) return;
        deleteHologram(id);
        createAnimatedItem(loc, item, delay);
    }

    @Override
    public void removeAll() {
        for (String id : hologramIdList) {
            deleteHologram(id);
        }
    }


}
