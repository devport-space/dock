package space.devport.dock.item.impl;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import space.devport.dock.DockedPlugin;
import space.devport.dock.api.IDockedFactory;
import space.devport.dock.item.ItemPrefab;

public class PrefabFactory implements IDockedFactory {

    private static DockedPlugin plugin;

    /**
     * Initialize a new PrefabFactory.
     *
     * @param plugin DevportPlugin reference.
     * @throws IllegalStateException if it's already initialized.
     */
    public PrefabFactory(DockedPlugin plugin) {
        if (PrefabFactory.plugin != null)
            throw new IllegalStateException("PrefabFactory already initialized.");

        PrefabFactory.plugin = plugin;
    }

    @Override
    public void destroy() {
        plugin = null;
    }

    private static void checkInitialized() throws IllegalStateException {
        if (plugin == null)
            throw new IllegalStateException("PrefabFactory is not initialized.");
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
        checkInitialized();
        return material == null ? null : new ItemPrefabImpl(plugin, material);
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
        checkInitialized();
        return material == null ? null : new ItemPrefabImpl(plugin, XMaterial.matchXMaterial(material));
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
        checkInitialized();
        return prefab == null ? null : new ItemPrefabImpl(prefab);
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
        checkInitialized();
        return item == null ? null : new ItemPrefabImpl(plugin, item);
    }
}
