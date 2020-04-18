package space.devport.utils.holograms.provider;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public abstract class HologramProvider {

    @Getter
    protected final List<String> hologramIdList = new ArrayList<>();

    private String nextId() {
        String id = "devport_holo_" + hologramIdList.size();
        if (hologramIdList.contains(id)) return nextId();
        return id;
    }

    public abstract void createHologram(String id, Location loc, List<String> content);

    public void createHologram(Location location, List<String> content) {
        createHologram(nextId(), location, content);
    }

    public abstract void createItemHologram(String id, Location location, ItemStack item);

    public void createItemHologram(Location location, ItemStack item) {
        createItemHologram(nextId(), location, item);
    }

    public abstract void createAnimatedHologram(String id, Location location, List<String> lines, int delay);

    public void createAnimatedHologram(Location location, List<String> lines, int delay) {
        createAnimatedHologram(nextId(), location, lines, delay);
    }

    public abstract void createAnimatedItem(String id, Location location, ItemStack item, int delay);

    public void createAnimatedItem(Location location, ItemStack item, int delay) {
        createAnimatedItem(nextId(), location, item, delay);
    }

    public abstract void deleteHologram(String id);

    public abstract void updateHologram(String id, List<String> newContent);

    public abstract void moveHologram(String id, Location newLocation);

    public abstract void updateItemHologram(String id, ItemStack item);

    public abstract void updateAnimatedHologram(String id, List<String> newContent, int delay);

    public abstract void updateAnimatedItem(String id, ItemStack item, int delay);

    public void removeAll() {
        for (String id : hologramIdList) {
            deleteHologram(id);
        }
    }
}