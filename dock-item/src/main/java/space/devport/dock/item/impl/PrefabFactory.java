package space.devport.dock.item.impl;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import space.devport.dock.item.ItemPrefab;

public class PrefabFactory {

    /**
     * Ensure no one initializes the factory.
     */
    private PrefabFactory() {
    }

    /**
     * Create a new {@link ItemPrefab} object with {@link XMaterial}.
     *
     * @param material {@link XMaterial} to create the prefab with.
     * @return {@link ItemPrefab} object with given material.
     * @throws IllegalStateException if the PrefabFactory is not initialized.
     */
    @Contract("null -> null")
    public static ItemPrefab createNew(XMaterial material) {
        return material == null ? null : new ItemPrefabBaseImpl(material);
    }

    /**
     * Create a new {@link ItemPrefab} object with {@link Material}.
     *
     * @param material {@link Material} to create the item with.
     * @return {@link ItemPrefab} object with given material.
     * @throws IllegalStateException if the PrefabFactory is not initialized.
     */
    @Contract("null -> null")
    public static ItemPrefab createNew(Material material) {
        return material == null ? null : new ItemPrefabBaseImpl(XMaterial.matchXMaterial(material));
    }

    /**
     * Creates a copy of {@link ItemPrefab}.
     *
     * @param prefab {@link ItemPrefab} to copy.
     * @return Copied {@link ItemPrefab} object.
     * @throws IllegalStateException if the PrefabFactory is not initialized.
     */
    @Contract("null -> null")
    public static ItemPrefab of(ItemPrefab prefab) {
        return prefab == null ? null : new ItemPrefabBaseImpl(prefab);
    }

    /**
     * Create a new {@link ItemPrefab} from given {@link ItemStack}.
     *
     * @param item {@link ItemStack} to create the prefab from.
     * @return {@link ItemPrefab} object from given {@link ItemStack}.
     * @throws IllegalStateException if the PrefabFactory is not initialized.
     */
    @Contract("null -> null")
    public static ItemPrefab of(ItemStack item) {
        return item == null ? null : new ItemPrefabBaseImpl(item);
    }
}
