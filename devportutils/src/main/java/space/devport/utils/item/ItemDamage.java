package space.devport.utils.item;

import com.google.common.base.Strings;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public class ItemDamage {

    @Getter
    private int damage;

    private boolean hasDamage;

    public ItemDamage(int damage) {
        setDamage(damage);
    }

    public ItemDamage(ItemDamage damage) {
        setDamage(damage.getDamage());
    }

    @Nullable
    public static ItemDamage from(ItemStack item) {
        if (item == null)
            return null;

        ItemMeta meta = item.getItemMeta();

        if (!(meta instanceof Damageable))
            return null;

        Damageable damageable = (Damageable) meta;
        return new ItemDamage(damageable.getDamage());
    }

    @Nullable
    public static ItemDamage fromString(@Nullable String str) {

        if (Strings.isNullOrEmpty(str))
            return null;

        try {
            return new ItemDamage(Integer.parseInt(str.trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean hasDamage() {
        return this.hasDamage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
        this.hasDamage = damage > 0;
    }

    public ItemStack apply(ItemStack item) {

        if (item == null)
            return null;

        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return item;

        item.setItemMeta(apply(meta));
        return item;
    }

    public ItemMeta apply(ItemMeta meta) {
        if (!hasDamage() || !(meta instanceof Damageable))
            return meta;

        Damageable damageable = (Damageable) meta;
        damageable.setDamage(damage);
        return meta;
    }

    @Override
    public String toString() {
        return String.valueOf(damage);
    }
}
