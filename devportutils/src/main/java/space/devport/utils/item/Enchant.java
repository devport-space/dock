package space.devport.utils.item;

import com.cryptomorin.xseries.XEnchantment;
import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Enchantment and level wrapper for simplified enchant manipulation.
 */
public class Enchant {

    @Getter
    public final XEnchantment enchantment;
    @Getter
    public final Amount level;

    public Enchant(XEnchantment enchantment, Amount level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public Enchant(Enchantment enchantment, Amount level) {
        this(XEnchantment.matchXEnchantment(enchantment), level);
    }

    public Enchant(XEnchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = new Amount(level);
    }

    public Enchant(Enchantment enchantment, int level) {
        this(XEnchantment.matchXEnchantment(enchantment), level);
    }

    public static Set<Enchant> from(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return new HashSet<>();

        return from(meta);
    }

    public static Set<Enchant> from(ItemMeta meta) {
        Set<Enchant> enchants = new HashSet<>();
        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            XEnchantment enchantment = XEnchantment.matchXEnchantment(entry.getKey());
            Enchant enchant = new Enchant(enchantment, entry.getValue());
            enchants.add(enchant);
        }
        return enchants;
    }

    public void apply(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return;

        apply(meta);
    }

    public void apply(@NotNull ItemMeta meta) {
        Enchantment enchantment = this.enchantment.parseEnchantment();

        if (enchantment == null)
            return;

        meta.addEnchant(enchantment, level.getInt(), true);
    }

    public boolean compare(Enchantment enchantment) {
        return this.enchantment == XEnchantment.matchXEnchantment(enchantment);
    }

    public boolean compare(XEnchantment xEnchantment) {
        return this.enchantment == xEnchantment;
    }
}
