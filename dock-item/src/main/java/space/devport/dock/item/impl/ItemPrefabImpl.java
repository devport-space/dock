package space.devport.dock.item.impl;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.item.ItemPrefab;

class ItemPrefabImpl extends ItemPrefabBase {

    ItemPrefabImpl(@NotNull XMaterial material) {
        super(material);
    }

    ItemPrefabImpl(@NotNull ItemPrefab prefab) {
        super(prefab);
    }

    ItemPrefabImpl(@NotNull ItemStack item) {
        super(item);
    }

    @Override
    public @NotNull ItemPrefabBase clone() {
        return new ItemPrefabImpl(this);
    }
}
