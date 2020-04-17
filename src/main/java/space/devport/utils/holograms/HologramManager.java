package space.devport.utils.holograms;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportPlugin;

import java.util.List;

public class HologramManager {

    private DevportPlugin instance;
    @Getter private HologramsHook hookedClass = null;
    @Getter private boolean hooked = false;

    public HologramManager(DevportPlugin instance) {
        this.instance = instance;
        decideHook();
    }

    private void decideHook() {
        if(instance.getPluginManager().isPluginEnabled("Holograms")) {
            hookedClass = new Holograms();
            hooked = true;
        } else if(instance.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            hookedClass = new HolographicDisplays();
            hooked = true;
        } else if(instance.getPluginManager().isPluginEnabled("CMI")) {
            hookedClass = new CMIHolograms();
            hooked = true;
        } else {
            hookedClass = null;
        }
    }

    public void createHologram(Location loc, List<String> lines) {
        if(hooked) {
            hookedClass.createHologram(loc, lines);
        }
    }

    public void createItemHologram(Location loc, ItemStack item) {
        if(hooked) {
            hookedClass.createItemHologram(loc, item);
        }
    }

    public void createAnimatedHologram(Location loc, List<String> lines, int delay) {
        if(hooked) {
            hookedClass.createAnimatedHologram(loc, lines, delay);
        }
    }

    public void createAnimatedItem(Location loc, ItemStack item, int delay) {
        if(hooked) {
            hookedClass.createAnimatedItem(loc, item, delay);
        }
    }

    public void deleteHologram(Location loc) {
        if(hooked) {
            hookedClass.deleteHologram(loc);
        }
    }

    public void deleteHologram(String id) {
        if(hooked) {
            hookedClass.deleteHologram(id);
        }
    }

    public void updateHologram(Location loc, List<String> newLines) {
        if(hooked) {
            hookedClass.updateHologram(loc, newLines);
        }
    }

    public void updateItemHologram(Location loc, ItemStack item) {
        if(hooked) {
            hookedClass.updateItemHologram(loc, item);
        }
    }

    public void updateAnimatedHologram(Location loc, List<String> lines, int delay) {
        if(hooked) {
            hookedClass.updateAnimatedHologram(loc, lines, delay);
        }
    }

    public void updateAnimatedItem(Location loc, ItemStack item, int delay) {
        if(hooked) {
            hookedClass.updateAnimatedItem(loc, item, delay);
        }
    }

    public void removeAll() {
        if(hooked) {
            hookedClass.removeAll();
        }
    }
}
