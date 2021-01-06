package space.devport.utils.item;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import space.devport.utils.DevportPlugin;
import space.devport.utils.factory.IFactory;

public class PrefabFactory implements IFactory {

    private static DevportPlugin plugin;

    /**
     * Initialize a new PrefabFactory.
     *
     * @param plugin DevportPlugin reference
     * @throws IllegalStateException when it's already initialized.
     */
    public PrefabFactory(DevportPlugin plugin) {
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
     * Create a new {@code ItemPrefab} object with {@param material}
     *
     * @param material Material to create the item with
     * @return ItemPrefab object with given material
     * @throws IllegalStateException when the PrefabFactory is not initialized.
     */
    @Contract("null -> null")
    public static ItemPrefab createNew(XMaterial material) {
        checkInitialized();
        return material == null ? null : new ItemPrefabImpl(plugin, material);
    }

    /**
     * Create a new {@code ItemPrefab} object with {@param material}
     *
     * @param material Material to create the item with
     * @return ItemPrefab object with given material
     * @throws IllegalStateException when the PrefabFactory is not initialized.
     */
    @Contract("null -> null")
    public static ItemPrefab createNew(Material material) {
        checkInitialized();
        return material == null ? null : new ItemPrefabImpl(plugin, XMaterial.matchXMaterial(material));
    }

    /**
     * Creates a copy of {@param prefab}
     *
     * @param prefab ItemPrefab to copy
     * @return Copied ItemPrefab object
     * @throws IllegalStateException when the PrefabFactory is not initialized.
     */
    @Contract("null -> null")
    public static ItemPrefab of(ItemPrefab prefab) {
        checkInitialized();
        return prefab == null ? null : new ItemPrefabImpl(prefab);
    }

    /**
     * Create a new {@code ItemPrefab} from given {@param item}
     *
     * @param item ItemStack to create the prefab from
     * @return ItemPrefab object from given ItemStack
     * @throws IllegalStateException when the PrefabFactory is not initialized.
     */
    @Contract("null -> null")
    public static ItemPrefab of(ItemStack item) {
        checkInitialized();
        return item == null ? null : new ItemPrefabImpl(plugin, item);
    }
}
