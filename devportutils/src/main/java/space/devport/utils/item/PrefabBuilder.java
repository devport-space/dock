package space.devport.utils.item;

import org.bukkit.inventory.ItemStack;

public interface PrefabBuilder {
    /**
     * Apply additional actions to the resulting ItemStack.
     */
    ItemStack apply(ItemStack item);
}
