package space.devport.utils.holograms;

import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@NoArgsConstructor
public abstract class HologramsHook {

    public abstract void createHologram(Location loc, List<String> lines);

    public abstract void createItemHologram(Location loc, ItemStack item);

    public abstract void createAnimatedHologram(Location loc, List<String> lines, int delay);

    public abstract void createAnimatedItem(Location loc, ItemStack item, int delay);

    public abstract void deleteHologram(Location loc);

    public abstract void deleteHologram(String id);

    public abstract void updateHologram(Location loc, List<String> newLines);

    public abstract void updateItemHologram(Location loc, ItemStack item);

    public abstract void updateAnimatedHologram(Location loc, List<String> lines, int delay);

    public abstract void updateAnimatedItem(Location loc, ItemStack item, int delay);

    public abstract void removeAll();
}
