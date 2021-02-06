package space.devport.dock.item.impl;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.item.ItemPrefab;

class ItemPrefabBaseImpl extends ItemPrefabBase {

    ItemPrefabBaseImpl(@NotNull XMaterial material) {
        super(material);
    }

    ItemPrefabBaseImpl(@NotNull ItemPrefab prefab) {
        super(prefab);
    }

    ItemPrefabBaseImpl(@NotNull ItemStack item) {
        super(item);
    }

    @Override
    public @NotNull ItemPrefabBase clone() {
        return new ItemPrefabBaseImpl(this);
    }
}
