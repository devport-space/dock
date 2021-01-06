package space.devport.utils.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface PrefabBuilder {

    /**
     * Apply additional actions to the resulting ItemStack.
     *
     * @param item {@link ItemStack} output from {@link ItemPrefab#build()}.
     * @return Edited {@link ItemStack}.
     * @see ItemStack
     * @see ItemPrefab
     */
    @NotNull ItemStack apply(@NotNull ItemStack item);
}
