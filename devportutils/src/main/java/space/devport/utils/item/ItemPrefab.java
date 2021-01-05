package space.devport.utils.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.item.nbt.NBTContainer;
import space.devport.utils.struct.Context;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.message.CachedMessage;
import space.devport.utils.text.message.Message;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ItemPrefab extends Cloneable {

    @Nullable ItemStack build(@NotNull Context context);

    @Nullable ItemStack build();

    @NotNull ItemPrefab withType(@NotNull XMaterial material);

    @NotNull ItemPrefab withAmount(@NotNull Amount amount);

    default @NotNull ItemPrefab withAmount(double value) {
        return withAmount(new Amount(value));
    }

    default @NotNull ItemPrefab withAmount(double low, double high) {
        return withAmount(new Amount(low, high));
    }

    @NotNull ItemPrefab withName(@Nullable Message name);

    default @NotNull ItemPrefab withName(@Nullable String name) {
        return withName(name == null ? new CachedMessage() : new CachedMessage(name));
    }

    @NotNull ItemPrefab withLore(@Nullable Message lore);

    default @NotNull ItemPrefab withLore(@Nullable Collection<String> lore) {
        return withLore(lore == null ? new CachedMessage() : new CachedMessage(lore));
    }

    default @NotNull ItemPrefab withLore(@Nullable String... lore) {
        return withLore(lore == null ? new CachedMessage() : new CachedMessage(lore));
    }

    @NotNull ItemPrefab appendLore(@NotNull Message lore);

    default @NotNull ItemPrefab appendLore(@NotNull String... lore) {
        return appendLore(new Message(lore));
    }

    @NotNull ItemPrefab addEnchant(@NotNull Enchant enchant);

    default @NotNull ItemPrefab addEnchant(@NotNull XEnchantment enchantment, int level) {
        return addEnchant(new Enchant(enchantment, level));
    }

    @NotNull ItemPrefab withEnchants(@NotNull Collection<Enchant> enchants);

    @NotNull ItemPrefab addFlags(@NotNull ItemFlag... flags);

    @NotNull ItemPrefab withFlags(@NotNull ItemFlag... flags);

    @NotNull ItemPrefab addNBT(@NotNull String key, @NotNull NBTContainer container);

    <T> @NotNull ItemPrefab addNBT(@NotNull String key, @NotNull T value);

    @NotNull ItemPrefab addNBT(@NotNull Map<String, NBTContainer> nbt);

    @NotNull ItemPrefab withNBT(@NotNull Map<String, NBTContainer> nbt);

    @NotNull ItemPrefab withSkullData(@Nullable SkullData skullData);

    default @NotNull ItemPrefab withSkullData(@NotNull String identifier) {
        return withSkullData(SkullData.of(identifier));
    }

    @NotNull ItemPrefab withDamage(@Nullable ItemDamage damage);

    default @NotNull ItemPrefab withDamage(int damage) {
        return withDamage(new ItemDamage(damage));
    }

    default @NotNull ItemPrefab withGlow() {
        return withGlow(true);
    }

    @NotNull ItemPrefab withGlow(boolean b);

    @NotNull ItemPrefab parseWith(@NotNull Placeholders placeholders);

    @NotNull ItemPrefab addBuilder(@NotNull PrefabBuilder builder);

    @NotNull ItemPrefab removeEnchant(@NotNull XEnchantment enchantment);

    @NotNull ItemPrefab clearEnchants();

    @NotNull ItemPrefab removeFlag(ItemFlag flag);

    @NotNull ItemPrefab clearFlags();

    @NotNull ItemPrefab removeNBT(String key);

    @NotNull ItemPrefab clearNBT();

    @NotNull ItemPrefab clearGlow();

    boolean hasEnchantment(XEnchantment xEnchantment);

    boolean hasFlag(ItemFlag flag);

    boolean hasNBT();

    boolean hasNBT(String key);

    <T> boolean hasNBTValue(String key, T value);

    <T> T getNBTValue(String key, @NotNull Class<T> clazz);

    boolean hasDamage();

    @NotNull Map<String, NBTContainer> getNBT();

    @NotNull XMaterial getMaterial();

    @NotNull Amount getAmount();

    @NotNull CachedMessage getName();

    @NotNull CachedMessage getLore();

    @NotNull Set<Enchant> getEnchants();

    @NotNull Set<ItemFlag> getFlags();

    @Nullable SkullData getSkullData();

    @Nullable ItemDamage getDamage();

    boolean isGlow();

    @NotNull DevportPlugin getPlugin();

    @NotNull Placeholders getPlaceholders();
}
