package space.devport.dock.holograms;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import space.devport.dock.DockedModule;
import space.devport.dock.DockedPlugin;
import space.devport.dock.holograms.provider.HologramProvider;
import space.devport.dock.holograms.provider.impl.CMIHolograms;
import space.devport.dock.holograms.provider.impl.Holograms;
import space.devport.dock.holograms.provider.impl.HolographicDisplays;
import space.devport.dock.utility.DependencyUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class HologramManager extends DockedModule {

    @Getter
    private HologramProvider hologramProvider = null;

    public HologramManager(DockedPlugin plugin) {
        super(plugin);
    }

    @Override
    public void preEnable() {
        attemptHook();
    }

    @Override
    public void afterDependencyLoad() {
        attemptHook();
    }

    public boolean isHooked() {
        return hologramProvider != null;
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
        if (isHooked())
            hologramProvider.save();
    }

    public void attemptHook() {

        if (hologramProvider != null)
            return;

        if (DependencyUtil.isEnabled("Holograms")) {
            hologramProvider = new Holograms(plugin);
            logUsing("Holograms");
        } else if (DependencyUtil.isEnabled("HolographicDisplays")) {
            hologramProvider = new HolographicDisplays(plugin);
            logUsing("HolographicDisplays");
        } else if (DependencyUtil.isEnabled("CMI")) {
            hologramProvider = new CMIHolograms(plugin);
            logUsing("CMI");
        }

        if (!isHooked())
            log.info("Found no holograms provider installed.");
        else hologramProvider.load();
    }

    private void logUsing(String name) {
        log.info(String.format("Using &a%s &7for holograms.", name));
    }

    private boolean checkHooked() {
        if (!isHooked()) {
            log.debug("There was a request for a holograms provider, but there's none registered.");
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

    public void deleteAll() {
        if (checkHooked()) {
            hologramProvider.deleteAll();
        }
    }

    public Set<String> getHolograms() {
        return checkHooked() ? hologramProvider.getHolograms() : new HashSet<>();
    }
}
