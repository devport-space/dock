package space.devport.dock.item.data;

import space.devport.dock.common.Strings;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ItemDamage {

    @Getter
    private int damage;

    private boolean hasDamage;

    public ItemDamage(int damage) {
        setDamage(damage);
    }

    private ItemDamage(@NotNull ItemDamage damage) {
        Objects.requireNonNull(damage);
        setDamage(damage.getDamage());
    }

    @Contract("null -> null;!null -> !null")
    public static ItemDamage of(@Nullable ItemDamage damage) {
        return damage == null ? null : new ItemDamage(damage);
    }

    @Contract("null -> null")
    public static ItemDamage from(@Nullable ItemStack item) {
        if (item == null)
            return null;

        ItemMeta meta = item.getItemMeta();

        if (!(meta instanceof Damageable))
            return null;

        Damageable damageable = (Damageable) meta;
        return new ItemDamage(damageable.getDamage());
    }

    @Contract("null -> null")
    public static ItemDamage fromString(@Nullable String str) {

        if (Strings.isNullOrEmpty(str))
            return null;

        try {
            return new ItemDamage(Integer.parseInt(str.trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public ItemStack apply(@NotNull ItemStack item) {
        Objects.requireNonNull(item);

        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return item;

        item.setItemMeta(apply(meta));
        return item;
    }

    //TODO: Support legacy damage. There is probably no Damageable interface down there.
    @Contract("null -> null")
    public ItemMeta apply(@Nullable ItemMeta meta) {
        if (!hasDamage() || !(meta instanceof Damageable))
            return meta;

        Damageable damageable = (Damageable) meta;
        damageable.setDamage(damage);
        return meta;
    }

    public boolean hasDamage() {
        return this.hasDamage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
        this.hasDamage = damage > 0;
    }

    @Override
    public String toString() {
        return String.valueOf(damage);
    }
}
