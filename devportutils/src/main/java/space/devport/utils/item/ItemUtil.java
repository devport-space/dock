package space.devport.utils.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.item.nbt.TypeUtil;
import space.devport.utils.version.VersionManager;
import space.devport.utils.version.api.ICompound;

import java.util.Set;

@UtilityClass
public class ItemUtil {

    /**
     * Get ItemStack NBT compound.
     */
    public ICompound getCompound(ItemStack item) {
        return VersionManager.fetchCompoundFactory().of(item);
    }

    /**
     * Return the NBT keys saved to item.
     */
    public Set<String> getNBTKeys(ItemStack item) {
        return getCompound(item).getKeys();
    }

    /**
     * Check if item has a NBT key.
     */
    public boolean hasKey(ItemStack item, String key) {
        return getCompound(item).has(key);
    }

    public <T> boolean hasNBTValue(ICompound compound, String key, T value) {
        Object result = TypeUtil.getValue(compound, key, TypeUtil.BASE_CLASS_MAP.get(compound.getId(key)));
        return result.equals(value);
    }

    /**
     * Check if the NBT stored on the item equals {@param value}
     */
    public <T> boolean hasNBTValue(ItemStack item, String key, T value) {
        ICompound compound = getCompound(item);
        return hasNBTValue(compound, key, value);
    }

    /**
     * Add NBT to item.
     */
    public <T> ItemStack setNBT(ItemStack item, String key, T value) {
        ICompound compound = getCompound(item);
        TypeUtil.setValue(compound, key, value);
        return compound.finish();
    }

    /**
     * Remove NBT from item.
     */
    public ItemStack removeNBT(ItemStack item, String key) {
        ICompound compound = getCompound(item);
        compound.remove(key);
        return compound.finish();
    }


    // ItemPrefab example usage kept here for now.
    static {
        ItemPrefab prefab = ItemPrefab.createNew(XMaterial.STICK)
                .withAmount(1, 5)
                .withName("&6A cool stick!")
                .withLore("&7Even cooler with lore.", "&7And more lore!")
                .addEnchant(XEnchantment.ARROW_FIRE, 1)
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addNBT("my_key", 20)
                .addBuilder(item -> ItemUtil.setNBT(item, "my_second_key", "A cute message hidden in my ItemStack."));

        ItemStack item = prefab.build();

        ItemPrefab copiedPrefab = ItemPrefab.of(item)
                .addNBT("my_int_array", new int[]{10, 15});
    }
}
