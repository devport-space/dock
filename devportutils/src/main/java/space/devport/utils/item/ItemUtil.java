package space.devport.utils.item;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import space.devport.utils.item.nbt.TypeUtil;
import space.devport.utils.version.CompoundFactory;
import space.devport.utils.version.api.ICompound;

import java.util.Set;

@UtilityClass
public class ItemUtil {

    /**
     * Get ICompound from {@link ItemStack}.
     *
     * @param item {@link ItemStack} to get {@link ICompound} from.
     * @return {@link ICompound} from item.
     * @see ItemStack
     */
    @Contract("null -> null")
    public ICompound getCompound(ItemStack item) {
        return item == null ? null : CompoundFactory.of(item);
    }

    /**
     * Get NBT keys stored on {@link ItemStack}.
     *
     * @param item {@link ItemStack} to load {@link ICompound} from.
     * @return Set of NBT keys.
     * @see ItemStack
     * @see ICompound
     */
    public Set<String> getNBTKeys(ItemStack item) {
        return getCompound(item).getKeys();
    }

    /**
     * Check if item has a NBT key.
     *
     * @param item {@link ItemStack} to look at.
     * @param key  String key to look for.
     * @return True if {@link ICompound} of item contains key.
     */
    public boolean hasKey(ItemStack item, String key) {
        return getCompound(item).has(key);
    }

    /**
     * Check if NBT value stored on {@link ICompound} under key equals value.
     *
     * @param <T>      Type signature.
     * @param compound {@link ICompound} to look at.
     * @param key      Key to look under.
     * @param value    Value to compare to.
     * @return True if {@link ICompound} contains key
     * and value saved under key is equal to value.
     * @see ICompound
     */
    public <T> boolean hasNBTValue(ICompound compound, String key, T value) {
        Object result = TypeUtil.getValue(compound, key, TypeUtil.BASE_CLASS_MAP.get(compound.getId(key)));
        return result.equals(value);
    }

    /**
     * Check if the NBT stored on {@link ItemStack} equals value.
     * <p>
     * Uses {@link #hasNBTValue(ICompound, String, Object)}.
     *
     * @param <T>   Type signature.
     * @param item  {@link ItemStack} to look at.
     * @param key   Key to look under.
     * @param value Value to compare to.
     * @return True if {@link ICompound} of item contains key
     * and value saved under key is equal to value.
     * @see ICompound
     * @see ItemStack
     */
    public <T> boolean hasNBTValue(ItemStack item, String key, T value) {
        ICompound compound = getCompound(item);
        return hasNBTValue(compound, key, value);
    }

    /*
     * Add NBT to item.
     */
    public <T> ItemStack setNBT(ItemStack item, String key, T value) {
        ICompound compound = getCompound(item);
        TypeUtil.setValue(compound, key, value);
        return compound.finish();
    }

    /*
     * Remove NBT from item.
     */
    public ItemStack removeNBT(ItemStack item, String key) {
        ICompound compound = getCompound(item);
        compound.remove(key);
        return compound.finish();
    }
}
