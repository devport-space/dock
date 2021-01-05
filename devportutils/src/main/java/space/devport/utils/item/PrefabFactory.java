package space.devport.utils.item;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import space.devport.utils.DevportPlugin;
import space.devport.utils.IFactory;

public class PrefabFactory implements IFactory {

    private static DevportPlugin plugin;

    public PrefabFactory(DevportPlugin plugin) {
        PrefabFactory.plugin = plugin;
    }

    public void destroy() {
        plugin = null;
    }

    @Contract("null -> null")
    public static ItemPrefab createNew(XMaterial material) {
        return material == null ? null : new ItemPrefabImpl(plugin, material);
    }

    @Contract("null -> null")
    public static ItemPrefab createNew(Material material) {
        return material == null ? null : new ItemPrefabImpl(plugin, XMaterial.matchXMaterial(material));
    }

    @Contract("null -> null")
    public static ItemPrefab of(ItemPrefab prefab) {
        return prefab == null ? null : new ItemPrefabImpl(prefab);
    }

    @Contract("null -> null")
    public static ItemPrefab of(ItemStack item) {
        return item == null ? null : new ItemPrefabImpl(plugin, item);
    }
}
