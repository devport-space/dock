package space.devport.dock.item.data;

import com.cryptomorin.xseries.XEnchantment;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.callbacks.ExceptionCallback;
import space.devport.dock.util.ParseUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Enchantment and level wrapper for simplified enchant manipulation.
 */
@Slf4j
public class Enchant {

    @Getter
    @NonNull
    public final XEnchantment enchantment;
    @Getter
    @NonNull
    public final Amount level;

    public Enchant(@NotNull XEnchantment enchantment, @NotNull Amount level) {
        Objects.requireNonNull(enchantment, "Enchant enchantment cannot be null.");
        Objects.requireNonNull(level, "Enchant level cannot be null.");

        this.enchantment = enchantment;
        this.level = level;
    }

    public Enchant(@NotNull XEnchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = new Amount(level);
    }

    @NotNull
    public static Set<Enchant> from(@Nullable ItemMeta meta) {
        Set<Enchant> enchants = new HashSet<>();

        if (meta == null)
            return enchants;

        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {

            if (entry.getKey() == null || entry.getValue() == null)
                continue;

            XEnchantment enchantment = parseEnchantment(entry.getKey());

            if (enchantment == null)
                continue;

            Enchant enchant = new Enchant(enchantment, entry.getValue());
            enchants.add(enchant);
        }
        return enchants;
    }

    @NotNull
    public static Set<Enchant> from(@Nullable ItemStack item) {
        if (item == null)
            return new HashSet<>();

        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return new HashSet<>();

        return from(meta);
    }

    private static XEnchantment parseEnchantment(Enchantment enchantment) {
        return enchantment == null ? null : ParseUtil.parseHandled(() -> XEnchantment.matchXEnchantment(enchantment), ExceptionCallback.IGNORE);
    }

    public static Enchant of(XEnchantment xEnchantment, int level) {
        return xEnchantment == null ? null : new Enchant(xEnchantment, level);
    }

    public static Enchant of(XEnchantment xEnchantment, Amount amount) {
        return xEnchantment == null ? null : new Enchant(xEnchantment, amount);
    }

    @Contract("null,_ -> null")
    public static Enchant of(Enchantment enchantment, int level) {
        return of(parseEnchantment(enchantment), level);
    }

    @Contract("null,_ -> null")
    public static Enchant of(Enchantment enchantment, Amount level) {
        return of(parseEnchantment(enchantment), level);
    }

    public void apply(@NotNull ItemStack item) {
        Objects.requireNonNull(item, "ItemStack cannot be null.");

        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return;

        apply(meta);
    }

    public void apply(@Nullable ItemMeta meta) {
        if (meta == null)
            return;

        Enchantment enchantment = this.enchantment.parseEnchantment();

        if (enchantment == null)
            return;

        meta.addEnchant(enchantment, level.getInt(), true);
    }

    public boolean compare(@Nullable Enchantment enchantment) {
        return enchantment == null || this.enchantment == XEnchantment.matchXEnchantment(enchantment);
    }

    public boolean compare(@Nullable XEnchantment xEnchantment) {
        return this.enchantment == xEnchantment;
    }
}
