package space.devport.dock.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.DockedPlugin;
import space.devport.dock.item.data.Amount;
import space.devport.dock.item.data.Enchant;
import space.devport.dock.item.data.ItemDamage;
import space.devport.dock.item.data.SkullData;
import space.devport.dock.item.nbt.NBTContainer;
import space.devport.dock.struct.Context;
import space.devport.dock.text.Placeholders;
import space.devport.dock.text.message.CachedMessage;
import space.devport.dock.text.message.Message;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * ItemPrefab is used to effectively build, edit and check items.
 */
public interface ItemPrefab extends Cloneable {

    /**
     * Build the {@link ItemPrefab} into an {@link ItemStack}.
     * <p>
     * {@link ItemPrefab} {@link Placeholders} will be fed given context.
     *
     * @param context {@link Context} to use.
     * @return Built {@link ItemStack}.
     */
    @Nullable ItemStack build(@NotNull Context context);

    /**
     * Build the ItemPrefab into an {@link ItemStack}.
     *
     * @return Built {@link ItemStack}.
     */
    @Nullable ItemStack build();

    /**
     * Set the type of the Prefab.
     *
     * @param material Material to set
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab withType(@NotNull XMaterial material);

    /**
     * Set the amount of the Prefab.
     *
     * @param amount Amount to set
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab withAmount(@NotNull Amount amount);

    /**
     * Set the amount of the Prefab,
     *
     * @param value Fixed amount to set
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab withAmount(double value) {
        return withAmount(new Amount(value));
    }

    /**
     * Set the dynamic amount of the prefab.
     *
     * @param low  Low value to use
     * @param high High value to use
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab withAmount(double low, double high) {
        return withAmount(new Amount(low, high));
    }

    /**
     * Set the name of the Prefab.
     *
     * @param name Message name to set
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab withName(@Nullable Message name);

    /**
     * Set the name of the Prefab.
     *
     * @param name String name to set
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab withName(@Nullable String name) {
        return withName(name == null ? new CachedMessage() : new CachedMessage(name));
    }

    /**
     * Set the lore of the Prefab.
     *
     * @param lore Message lore to set
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab withLore(@Nullable Message lore);

    /**
     * Set the lore of the Prefab.
     *
     * @param lore {@code Collection<String>} to set as lore.
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab withLore(@Nullable Collection<String> lore) {
        return withLore(lore == null ? new CachedMessage() : new CachedMessage(lore));
    }

    /**
     * Set the lore of the Prefab.
     *
     * @param lore String[] to set
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab withLore(@Nullable String... lore) {
        return withLore(lore == null ? new CachedMessage() : new CachedMessage(lore));
    }

    /**
     * Append to the lore of the Prefab.
     *
     * @param lore Message lore to append
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab appendLore(@NotNull Message lore);

    /**
     * Append to the lore of the Prefab.
     *
     * @param lore String[] lore to append
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab appendLore(@NotNull String... lore) {
        return appendLore(new Message(lore));
    }

    /**
     * Add enchant to Prefab.
     *
     * @param enchant Enchant to add
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab addEnchant(@NotNull Enchant enchant);

    /**
     * Add enchant to Prefab.
     *
     * @param enchantment Enchantment to add
     * @param level       Enchantment level
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab addEnchant(@NotNull XEnchantment enchantment, int level) {
        return addEnchant(new Enchant(enchantment, level));
    }

    /**
     * Set enchants to Prefab.
     *
     * @param enchants Enchants to set
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab withEnchants(@NotNull Collection<Enchant> enchants);

    /**
     * Add flags to Prefab.
     *
     * @param flags Flags to add
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab addFlags(@NotNull ItemFlag... flags);

    /**
     * Set flags of the Prefab.
     *
     * @param flags Flags to set
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab withFlags(@NotNull ItemFlag... flags);

    /**
     * Add NBT to Prefab.
     *
     * @param key       String key
     * @param container Contained value to set
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab addNBT(@NotNull String key, @NotNull NBTContainer container);

    /**
     * Add NBT to Prefab.
     *
     * @param <T>   Type signature.
     * @param key   String key
     * @param value Value to set
     * @return This ItemPrefab.
     */
    <T> @NotNull ItemPrefab addNBT(@NotNull String key, @NotNull T value);

    /**
     * Add NBT to Prefab.
     *
     * @param nbt Nbt map to add
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab addNBT(@NotNull Map<String, NBTContainer> nbt);

    /**
     * Set NBT to Prefab.
     *
     * @param nbt NBT map to set
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab withNBT(@NotNull Map<String, NBTContainer> nbt);

    /**
     * Set SkullData to Prefab.
     *
     * @param skullData SkullData to set
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab withSkullData(@Nullable SkullData skullData);

    /**
     * Set SkullData to Prefab.
     * Equivalent to #withSkullData(SkullData.fromString(identifier))
     *
     * @param identifier Identifier to use
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab withSkullData(@NotNull String identifier) {
        return withSkullData(SkullData.of(identifier));
    }

    /**
     * Set Damage to Prefab.
     *
     * @param damage ItemDamage to set
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab withDamage(@Nullable ItemDamage damage);

    /**
     * Set Damage to Prefab.
     * Equivalent to #withDamage(new ItemDamage(damage))
     *
     * @param damage Integer damage to set
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab withDamage(int damage) {
        return withDamage(new ItemDamage(damage));
    }

    /**
     * Set glow to Prefab.
     * Equivalent to #withGlow(true)
     *
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab withGlow() {
        return withGlow(true);
    }

    /**
     * Set glow to Prefab.
     *
     * @param b Boolean, true to glow, false to not
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab withGlow(boolean b);

    /**
     * Set Placeholders to parse with.
     *
     * @param placeholders Placeholders to use
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab parseWith(@NotNull Placeholders placeholders);

    /**
     * Add extra builder to Prefab.
     * PrefabBuilders are applied after the ItemStack is built.
     *
     * @param builder PrefabBuilder to add
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab addBuilder(@NotNull PrefabBuilder builder);

    /**
     * Remove enchant from Prefab.
     *
     * @param enchantment Enchantment to remove
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab removeEnchant(@NotNull XEnchantment enchantment);

    /**
     * Clear enchants from Prefab.
     *
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab clearEnchants();

    /**
     * Remove flag from Prefab.
     *
     * @param flag Flag to remove
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab removeFlag(ItemFlag flag);

    /**
     * Clear flags from Prefab.
     *
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab clearFlags();

    /**
     * Remove NBT from Prefab.
     *
     * @param key NBT key of the entry to remove
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab removeNBT(String key);

    /**
     * Clear NBT from Prefab.
     *
     * @return This ItemPrefab.
     */
    @NotNull ItemPrefab clearNBT();

    /**
     * Remove glow from Prefab.
     * Equivalent to #withGlow(false)
     *
     * @return This ItemPrefab.
     */
    default @NotNull ItemPrefab clearGlow() {
        return withGlow(false);
    }

    /**
     * Check for an enchantment on this Prefab.
     *
     * @param enchantment {@link XEnchantment} to look for
     * @return true if prefab has this enchantment,
     * false if not or enchantment is null.
     */
    boolean hasEnchantment(XEnchantment enchantment);

    /**
     * Check for an {@link ItemFlag} on this Prefab.
     *
     * @param flag {@link ItemFlag} to look for
     * @return true if prefab has this flag,
     * false if not or flag is null.
     */
    boolean hasFlag(ItemFlag flag);

    /**
     * Check for NBT on this Prefab.
     *
     * @return true if prefab has NBT, false if not
     */
    boolean hasNBT();

    /**
     * Check for an NBT key on this Prefab.
     *
     * @param key NBT key to look for
     * @return true if prefab has an NBT entry with this key,
     * false if not or key is null
     */
    boolean hasNBT(String key);

    /**
     * Check for an NBT entry on this Prefab.
     *
     * @param <T>   Type signature.
     * @param key   NBT key to look for
     * @param value Value to check
     * @return true if prefab has this NBT key and it's value is equal to value,
     * false if not or key is null
     */
    <T> boolean hasNBTValue(String key, T value);

    /**
     * Get an NBT value from Prefab.
     *
     * @param key   NBT key to query
     * @param clazz Value class to parse
     * @param <T>   Type signature
     * @return value of the key cast to {@code <T>} or null if absent or
     * value under key has value of a different type
     */
    <T> T getNBTValue(String key, @NotNull Class<T> clazz);

    /**
     * Check for damage on this Prefab.
     *
     * @return true if the prefab has any damage applied to it
     */
    boolean hasDamage();

    /**
     * Get NBT of the Prefab.
     *
     * @return NBT map
     */
    @NotNull Map<String, NBTContainer> getNBT();

    /**
     * Get material of the Prefab.
     *
     * @return XMaterial material
     */
    @NotNull XMaterial getMaterial();

    /**
     * Get amount of the Prefab.
     *
     * @return Amount instance
     */
    @NotNull Amount getAmount();

    /**
     * Get name of the Prefab.
     *
     * @return CachedMessage name
     */
    @NotNull CachedMessage getName();

    /**
     * Get lore of the Prefab.
     *
     * @return CachedMessage lore
     */
    @NotNull CachedMessage getLore();

    /**
     * Get enchants of the Prefab
     *
     * @return Enchants set
     */
    @NotNull Set<Enchant> getEnchants();

    /**
     * Get flags of the Prefab.
     *
     * @return Flags set
     */
    @NotNull Set<ItemFlag> getFlags();

    /**
     * Get SkullData of the Prefab.
     *
     * @return SkullData instance
     */
    @Nullable SkullData getSkullData();

    /**
     * Get damage of the Prefab.
     *
     * @return ItemDamage instance or
     * {@code null} if the prefab has no damage applied
     */
    @Nullable ItemDamage getDamage();

    /**
     * Get glow value.
     *
     * @return Glow value
     */
    boolean isGlow();

    /**
     * Get plugin instance.
     *
     * @return DevportPlugin instance
     */
    @NotNull DockedPlugin getPlugin();

    /**
     * Get placeholders used for parsing.
     *
     * @return Placeholders instance
     */
    @NotNull Placeholders getPlaceholders();

    @NotNull ItemPrefab clone();
}
