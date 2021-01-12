package space.devport.utils.item.impl;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.DevportPlugin;
import space.devport.utils.item.ItemPrefab;

class ItemPrefabImpl extends AbstractPrefab {

    ItemPrefabImpl(DevportPlugin plugin, @NotNull XMaterial material) {
        super(plugin, material);
    }

    ItemPrefabImpl(@NotNull ItemPrefab prefab) {
        super(prefab);
    }

    ItemPrefabImpl(DevportPlugin plugin, @NotNull ItemStack item) {
        super(plugin, item);
    }

    @Override
    public @NotNull AbstractPrefab clone() {
        return new ItemPrefabImpl(this);
    }
}
