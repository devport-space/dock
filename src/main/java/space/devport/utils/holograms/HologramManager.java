package space.devport.utils.holograms;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportPlugin;
import space.devport.utils.DevportUtils;
import space.devport.utils.holograms.provider.CMIHolograms;
import space.devport.utils.holograms.provider.Holograms;
import space.devport.utils.holograms.provider.HologramsProvider;
import space.devport.utils.holograms.provider.HolographicDisplays;

import java.util.List;

public class HologramManager {

    private final DevportPlugin instance;

    @Getter
    private HologramsProvider hologramProvider = null;

    @Getter
    private boolean hooked = false;

    public HologramManager(DevportPlugin instance) {
        this.instance = instance;
    }

    public void attemptHook() {
        if (DevportUtils.getInstance().checkDependency("Holograms")) {
            hologramProvider = new Holograms();
            instance.getConsoleOutput().info("Using &aHolograms &7as the HologramsProvider.");
        } else if (DevportUtils.getInstance().checkDependency("HolographicDisplays")) {
            hologramProvider = new HolographicDisplays();
            instance.getConsoleOutput().info("Using &aHolographicDisplays &7as the HologramsProvider.");
        } else if (DevportUtils.getInstance().checkDependency("CMI")) {
            hologramProvider = new CMIHolograms();
            instance.getConsoleOutput().info("Using &aCMI &7as the HologramsProvider.");
        }

        hooked = hologramProvider != null;
        if (!hooked) instance.getConsoleOutput().info("Found no HologramsProvider installed.");
    }

    public void createHologram(Location loc, List<String> lines) {
        if (checkHooked()) {
            hologramProvider.createHologram(loc, lines);
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

    public void deleteHologram(Location loc) {
        if (checkHooked()) {
            hologramProvider.deleteHologram(loc);
        }
    }

    public void deleteHologram(String id) {
        if (checkHooked()) {
            hologramProvider.deleteHologram(id);
        }
    }

    public void updateHologram(Location loc, List<String> newLines) {
        if (checkHooked()) {
            hologramProvider.updateHologram(loc, newLines);
        }
    }

    public void updateItemHologram(Location loc, ItemStack item) {
        if (checkHooked()) {
            hologramProvider.updateItemHologram(loc, item);
        }
    }

    public void updateAnimatedHologram(Location loc, List<String> lines, int delay) {
        if (checkHooked()) {
            hologramProvider.updateAnimatedHologram(loc, lines, delay);
        }
    }

    public void updateAnimatedItem(Location loc, ItemStack item, int delay) {
        if (checkHooked()) {
            hologramProvider.updateAnimatedItem(loc, item, delay);
        }
    }

    public void removeAll() {
        if (checkHooked()) {
            hologramProvider.removeAll();
        }
    }

    public List<String> getHolograms() {
        return checkHooked() ? hologramProvider.getHolograms() : null;
    }

    private boolean checkHooked() {
        if (!hooked) {
            instance.getConsoleOutput().debug("There was a request for a hologram provider, but it's not registered.");
            return false;
        } else return true;
    }
}
