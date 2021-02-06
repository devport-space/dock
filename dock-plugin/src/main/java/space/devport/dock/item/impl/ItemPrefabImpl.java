package space.devport.dock.item.impl;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.DockedPlugin;
import space.devport.dock.item.ItemPrefab;

class ItemPrefabImpl extends AbstractPrefab {

    ItemPrefabImpl(DockedPlugin plugin, @NotNull XMaterial material) {
        super(plugin, material);
    }

    ItemPrefabImpl(@NotNull ItemPrefab prefab) {
        super(prefab);
    }

    ItemPrefabImpl(DockedPlugin plugin, @NotNull ItemStack item) {
        super(plugin, item);
    }

    @Override
    public @NotNull AbstractPrefab clone() {
        return new ItemPrefabImpl(this);
    }
}
