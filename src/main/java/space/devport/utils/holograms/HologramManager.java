package space.devport.utils.holograms;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportPlugin;
import space.devport.utils.DevportUtils;
import space.devport.utils.holograms.provider.CMIHolograms;
import space.devport.utils.holograms.provider.HologramProvider;
import space.devport.utils.holograms.provider.Holograms;
import space.devport.utils.holograms.provider.HolographicDisplays;

import java.util.List;

public class HologramManager {

    private final DevportPlugin instance;

    @Getter
    private HologramProvider hologramProvider = null;

    @Getter
    private boolean hooked = false;

    public HologramManager(DevportPlugin instance) {
        this.instance = instance;
    }

    public void attemptHook() {

        if (hologramProvider != null) return;

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
        return checkHooked() ? hologramProvider.getHolograms() : null;
    }

    private boolean checkHooked() {
        if (!hooked) {
            instance.getConsoleOutput().debug("There was a request for a hologram provider, but it's not registered.");
            return false;
        } else return true;
    }
}
