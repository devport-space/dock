package space.devport.dock.version.compound;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.version.compound.CompoundFactory;
import space.devport.dock.version.api.ICompound;

import java.util.*;

@UtilityClass
public class CompoundUtil {

    // Some ItemStack parameters are already saved inside other variables.
    // These keys will be ignored in "filtered" methods.
    public static final List<String> VANILLA_KEYS = Arrays.asList("Damage", "Enchantments", "display", "HideFlags");

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

    private Collection<String> filterKeys(Collection<String> c) {
        c.removeIf(VANILLA_KEYS::contains);
        return c;
    }

    /**
     * Get NBT keys stored on {@link ItemStack}.
     *
     * @param item {@link ItemStack} to load {@link ICompound} from.
     * @return Set of NBT keys.
     * @see ItemStack
     * @see ICompound
     */
    public Collection<String> getKeys(ItemStack item) {
        return getCompound(item).getKeys();
    }

    public Collection<String> getKeysFiltered(ItemStack item) {
        return filterKeys(getKeys(item));
    }

    /**
     * Check if {@link ItemStack} has any NBT keys.
     *
     * @param item {@link ItemStack} to check.
     * @return True if {@link ItemStack} has any NBT keys.
     */
    public boolean has(ItemStack item) {
        return !getCompound(item).getKeys().isEmpty();
    }

    public boolean hasFiltered(ItemStack item) {
        return !filterKeys(getCompound(item).getKeys()).isEmpty();
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
    public <T> boolean hasValue(ICompound compound, String key, T value) {
        Object result = TypeUtil.extract(compound, key, TypeUtil.BASE_CLASS_MAP.get(compound.getId(key)));
        return result.equals(value);
    }

    /**
     * Check if the NBT stored on {@link ItemStack} equals value.
     * <p>
     * Uses {@link #hasValue(ICompound, String, Object)}.
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
    public <T> boolean hasValue(ItemStack item, String key, T value) {
        ICompound compound = getCompound(item);
        return hasValue(compound, key, value);
    }

    public NBTContainer getValue(ItemStack item, String key) {
        ICompound compound = getCompound(item);
        return compound == null ? null : TypeUtil.contain(compound, key);
    }

    @Contract("null,_,_ -> null")
    public <T> T getValue(@Nullable ItemStack item, @NotNull String key, @NotNull Class<T> clazz) {
        return getValue(getCompound(item), key, clazz);
    }

    @Contract("null,_,_ -> null")
    public <T> T getValue(@Nullable ICompound compound, @NotNull String key, @NotNull Class<T> clazz) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(key);

        return compound == null ? null : TypeUtil.extract(compound, key, clazz);
    }

    /*
     * Add NBT to item.
     */
    public <T> ItemStack setValue(ItemStack item, String key, T value) {
        ICompound compound = getCompound(item);
        TypeUtil.setValue(compound, key, value);
        return compound.finish();
    }

    /*
     * Remove NBT from item.
     */
    public ItemStack remove(ItemStack item, String key) {
        ICompound compound = getCompound(item);
        compound.remove(key);
        return compound.finish();
    }

    public ItemStack clear(ItemStack item) {
        ICompound compound = getCompound(item);
        for (String key : compound.getKeys())
            compound.remove(key);
        return compound.finish();
    }

    public ItemStack clearFiltered(ItemStack item) {
        ICompound compound = getCompound(item);
        for (String key : filterKeys(compound.getKeys()))
            compound.remove(key);
        return compound.finish();
    }

    /**
     * Parse NBT from {@link ICompound} into a {@link Map} of {@link String} and {@link NBTContainer}.
     *
     * @param compound {@link ICompound} to parse from.
     * @return {@code Map<String, NBTContainer>} of parsed values.
     * If {@link ICompound} is null, return an empty map.
     */
    @NotNull
    public Map<String, NBTContainer> getMap(@Nullable ICompound compound) {
        Map<String, NBTContainer> nbt = new HashMap<>();
        if (compound != null)
            for (String key : compound.getKeys()) {
                nbt.put(key, TypeUtil.contain(compound, key));
            }
        return nbt;
    }

    public Map<String, NBTContainer> getMapFiltered(ICompound compound) {
        Map<String, NBTContainer> nbt = new HashMap<>();
        if (compound != null)
            for (String key : compound.getKeys()) {
                nbt.put(key, TypeUtil.contain(compound, key));
            }
        return nbt;
    }

    /**
     * Parse NBT from {@link ItemStack} into a {@link Map} of {@link String} and {@link NBTContainer}.
     *
     * @param item {@link ItemStack} to parse from.
     * @return {@code Map<String, NBTContainer>} of parsed values.
     * If {@link ItemStack} is null, return an empty map.
     * @see #getMap(ICompound)
     */
    @NotNull
    public Map<String, NBTContainer> getMap(@Nullable ItemStack item) {
        return getMap(getCompound(item));
    }

    /**
     * Parse NBT from {@link ItemStack} into a {@link Map} of {@link String} and {@link NBTContainer}.
     *
     * @param item {@link ItemStack} to parse from.
     * @return {@code Map<String, NBTContainer>} of parsed values.
     * If {@link ItemStack} is null, return an empty map.
     * @see #getMapFiltered(ICompound)
     */
    @NotNull
    public Map<String, NBTContainer> getMapFiltered(@Nullable ItemStack item) {
        return getMapFiltered(getCompound(item));
    }
}
