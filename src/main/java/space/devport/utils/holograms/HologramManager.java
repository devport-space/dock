package space.devport.utils.holograms;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportManager;
import space.devport.utils.DevportPlugin;
import space.devport.utils.holograms.provider.HologramProvider;
import space.devport.utils.holograms.provider.impl.CMIHolograms;
import space.devport.utils.holograms.provider.impl.Holograms;
import space.devport.utils.holograms.provider.impl.HolographicDisplays;
import space.devport.utils.utility.DependencyUtil;

import java.util.ArrayList;
import java.util.List;

public class HologramManager extends DevportManager {

    @Getter
    private HologramProvider hologramProvider = null;

    @Getter
    private boolean hooked = false;

    public HologramManager(DevportPlugin plugin) {
        super(plugin);
    }

    @Override
    public void afterDependencyLoad() {
        attemptHook();
    }

    @Override
    public void afterReload() {
        if (isHooked()) {
            hologramProvider.save();
            hologramProvider.load();
        } else
            attemptHook();
    }

    @Override
    public void onDisable() {
        hologramProvider.save();
    }

    public void attemptHook() {

        if (hologramProvider != null) return;

        if (DependencyUtil.isEnabled("Holograms")) {
            hologramProvider = new Holograms(plugin);
            plugin.getConsoleOutput().info("Using &aHolograms &7as the HologramsProvider.");
        } else if (DependencyUtil.isEnabled("HolographicDisplays")) {
            hologramProvider = new HolographicDisplays(plugin);
            plugin.getConsoleOutput().info("Using &aHolographicDisplays &7as the HologramsProvider.");
        } else if (DependencyUtil.isEnabled("CMI")) {
            hologramProvider = new CMIHolograms(plugin);
            plugin.getConsoleOutput().info("Using &aCMI &7as the HologramsProvider.");
        }

        hooked = hologramProvider != null;

        if (!hooked)
            plugin.getConsoleOutput().info("Found no HologramsProvider installed.");
    }

    private boolean checkHooked() {
        if (!hooked) {
            plugin.getConsoleOutput().debug("There was a request for a hologram provider, but it's not registered.");
            return false;
        } else return true;
    }

    public void createHologram(Location loc, List<String> lines) {
        if (checkHooked()) {
            hologramProvider.createHologram(loc, lines);
        }
    }

    public void createHologram(String id, Location location, List<String> content) {
        if (checkHooked()) {
            hologramProvider.createHologram(id, location, content);
        }
    }

    public void createItemHologram(Location loc, ItemStack item) {
        if (checkHooked()) {
            hologramProvider.createItemHologram(loc, item);
        }
    }

    public void createAnimatedHologram(Location loc, List<String> lines, int delay) {
        if (checkHooked()) {
            hologramProvider.createAnimatedHologram(loc, lines, delay);
        }
    }

    public void createAnimatedItem(Location loc, ItemStack item, int delay) {
        if (checkHooked()) {
            hologramProvider.createAnimatedItem(loc, item, delay);
        }
    }

    public void deleteHologram(String id) {
        if (checkHooked()) {
            hologramProvider.deleteHologram(id);
        }
    }

    public void updateHologram(String id, List<String> newContent) {
        if (checkHooked()) {
            hologramProvider.updateHologram(id, newContent);
        }
    }

    public void moveHologram(String id, Location newLocation) {
        if (checkHooked()) {
            hologramProvider.moveHologram(id, newLocation);
        }
    }

    public void updateItemHologram(String id, ItemStack item) {
        if (checkHooked()) {
            hologramProvider.updateItemHologram(id, item);
        }
    }

    public void updateAnimatedHologram(String id, List<String> lines, int delay) {
        if (checkHooked()) {
            hologramProvider.updateAnimatedHologram(id, lines, delay);
        }
    }

    public void updateAnimatedItem(String id, ItemStack item, int delay) {
        if (checkHooked()) {
            hologramProvider.updateAnimatedItem(id, item, delay);
        }
    }

    public void removeAll() {
        if (checkHooked()) {
            hologramProvider.removeAll();
        }
    }

    public List<String> getHolograms() {
        return checkHooked() ? hologramProvider.getHolograms() : new ArrayList<>();
    }
}
