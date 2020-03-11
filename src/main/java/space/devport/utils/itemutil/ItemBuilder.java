package space.devport.utils.itemutil;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportUtils;
import space.devport.utils.messageutil.MessageBuilder;
import space.devport.utils.messageutil.ParseFormat;

import java.util.*;

/**
 * Class to handle Item construction.
 *
 * @author Devport Team
 */
@NoArgsConstructor
public class ItemBuilder {

    // TODO Implement Mutli-version material support. XSeries: https://github.com/CryptoMorin/XSeries

    // Item Data information
    @Getter
    private Material material = Material.STONE;
    @Getter
    private short damage = 0;

    @Getter
    private int amount = 1;

    @Getter
    private MessageBuilder displayName;
    @Getter
    private MessageBuilder lore = new MessageBuilder();

    // Apply luck & hide enchants flag?
    @Getter
    private boolean glow = false;

    @Getter
    private HashMap<Enchantment, Integer> enchants = new HashMap<>();

    @Getter
    private List<ItemFlag> flags = new ArrayList<>();

    // Holds item nbt keys & values
    @Getter
    private HashMap<String, String> NBT = new HashMap<>();

    // ParseFormat for placeholders
    @Getter
    private ParseFormat parseFormat = new ParseFormat();

    /**
     * Constructor with a material.
     */
    public ItemBuilder(@NotNull Material material) {
        this.material = material;
    }

    /**
     * Copy constructor.
     */
    public ItemBuilder(@NotNull ItemBuilder builder) {
        this.displayName = new MessageBuilder(builder.getDisplayName());
        this.material = builder.getMaterial();
        this.amount = builder.getAmount();
        this.damage = builder.getDamage();
        this.glow = builder.isGlow();
        this.flags = new ArrayList<>(builder.getFlags());
        this.enchants = new HashMap<>(builder.getEnchants());
        this.parseFormat = new ParseFormat(builder.getParseFormat());
        this.NBT = new HashMap<>(builder.getNBT());
        this.lore = new MessageBuilder(builder.getLore());
    }

    /**
     * To-builder constructor.
     */
    public ItemBuilder(@NotNull ItemStack item) {
        this.material = item.getType();
        this.damage = (byte) item.getDurability();

        this.amount = item.getAmount();

        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            // Display name
            if (itemMeta.hasDisplayName())
                this.displayName = new MessageBuilder(itemMeta.getDisplayName());

            // Lore
            if (itemMeta.hasLore())
                this.lore = new MessageBuilder(itemMeta.getLore());

            // Enchants
            if (itemMeta.hasEnchants())
                this.enchants = new HashMap<>(itemMeta.getEnchants());

            // Flags
            this.flags = new ArrayList<>(itemMeta.getItemFlags());
        }

        // TODO Find out what is saved into NBT by vanilla, then filter them out.
        Map<String, String> map = ItemNBTEditor.getNBTTagMap(item);

        for (String key : map.keySet()) {
            // Filter here
            this.NBT.put(key, map.get(key));
        }
    }

    /**
     * Parses a placeholder with given value.
     *
     * @param key   Placeholder key
     * @param value Placeholder value
     * @return ItemBuilder object
     */
    // Move to parse format
    @Deprecated
    public ItemBuilder parse(@NotNull String key, @NotNull String value) {
        if (displayName != null)
            if (!displayName.isEmpty())
                displayName.parsePlaceholder(key, value);

        if (!lore.isEmpty())
            lore.parsePlaceholder(key, value);
        return this;
    }

    /**
     * Parse the item's display name and lore with given parse format.
     *
     * @param format Parse format to parse with
     * @return ItemBuilder object
     */
    public ItemBuilder parseWith(@NotNull ParseFormat format) {
        for (String key : format.getPlaceholders())
            parse(key, format.getPlaceholderCache().get(key));
        return this;
    }

    /**
     * Builds the ItemStack with Builder parameters.
     * If no material is set, uses STONE as a default.
     *
     * @return Built ItemStack object
     */
    @NotNull
    public ItemStack build() {
        ItemStack item = new ItemStack(material, amount, damage);
        ItemMeta meta = item.getItemMeta();

        // Apply lore
        if (!lore.isEmpty()) {
            lore.copyPlaceholders(parseFormat)
                    .parsePlaceholders()
                    .color();
            meta.setLore(lore.getWorkingMessage());

            lore.pull();
        }

        // Apply display name
        if (displayName != null) {
            meta.setDisplayName(displayName.copyPlaceholders(parseFormat)
                    .parsePlaceholders()
                    .color()
                    .toString());
            displayName.pull();
        }

        // Apply enchants
        if (!enchants.isEmpty())
            enchants.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));

        // Add flags
        if (!flags.isEmpty())
            flags.forEach(meta::addItemFlags);

        if (glow) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);

        // NBT
        if (!NBT.isEmpty())
            for (String key : NBT.keySet()) {
                ItemStack i = ItemNBTEditor.writeNBT(item, key, NBT.get(key));
                if (i == null)
                    DevportUtils.getInstance().getConsoleOutput().warn("Couldn't write NBT to item.");
                else
                    item = i;
            }

        return item;
    }

    // ------------------------------ Builder Setters ------------------------------

    /**
     * Set the material (type).
     *
     * @param material Material to set
     * @return ItemBuilder object
     */
    public ItemBuilder type(@NotNull Material material) {
        this.material = material;
        return this;
    }

    /**
     * Set the damage.
     *
     * @param damage Damage to set
     * @return ItemBuilder object
     */
    public ItemBuilder damage(short damage) {
        this.damage = damage;
        return this;
    }

    /**
     * Set the amount.
     *
     * @param amount Material to set
     * @return ItemBuilder object
     */
    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Set the displayName.
     *
     * @param displayName Material to set
     * @return ItemBuilder object
     */
    public ItemBuilder displayName(@Nullable MessageBuilder displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Set the display name with a String.
     *
     * @param displayName Display name in String
     * @return ItemBuilder object
     */
    public ItemBuilder displayName(@Nullable String displayName) {
        return displayName(new MessageBuilder(displayName));
    }

    /**
     * Set the lore.
     *
     * @param lore Lore to set
     * @return ItemBuilder object
     */
    public ItemBuilder lore(@Nullable MessageBuilder lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Set the lore with a List.
     *
     * @param lore Lore to set in List
     * @return ItemBuilder object
     */
    public ItemBuilder lore(@Nullable List<String> lore) {
        return lore(new MessageBuilder(lore));
    }

    /**
     * Set the lore with an Array.
     *
     * @param lore Lore to set in Array
     * @return ItemBuilder object
     */
    public ItemBuilder lore(@NotNull String[] lore) {
        return lore(new MessageBuilder(Arrays.asList(lore)));
    }

    /**
     * Add a line to lore.
     *
     * @param line Line to add in String
     * @return ItemBuilder object
     */
    public ItemBuilder addLine(@NotNull String line) {
        if (lore == null)
            this.lore = new MessageBuilder();
        lore.addLine(line);
        return this;
    }

    /**
     * Set the enchants.
     *
     * @param enchants Enchants to set
     * @return ItemBuilder object
     */
    public ItemBuilder enchants(@Nullable HashMap<Enchantment, Integer> enchants) {
        this.enchants = enchants;
        return this;
    }

    /**
     * Add an enchant.
     *
     * @param enchantment Enchantment to add
     * @param level       Optional level parameter, uses 1 as default
     * @return ItemBuilder object
     */
    public ItemBuilder addEnchant(@NotNull Enchantment enchantment, int... level) {
        if (this.enchants == null)
            this.enchants = new HashMap<>();
        enchants.put(enchantment, level.length > 0 ? level[0] : 1);
        return this;
    }

    /**
     * Add enchants.
     *
     * @param enchants Enchants to add
     * @return ItemBuilder object
     */
    public ItemBuilder addEnchants(@NotNull HashMap<Enchantment, Integer> enchants) {
        if (this.enchants == null)
            this.enchants = new HashMap<>();
        this.enchants.putAll(enchants);
        return this;
    }

    /**
     * Remove an enchant.
     *
     * @param enchantment Enchant to remove
     * @return ItemBuilder object
     */
    public ItemBuilder removeEnchant(@Nullable Enchantment enchantment) {
        enchants.remove(enchantment);
        return this;
    }

    /**
     * Clear all enchants.
     *
     * @return ItemBuilder object
     */
    public ItemBuilder clearEnchants() {
        if (this.enchants == null)
            this.enchants = new HashMap<>();
        this.enchants.clear();
        return this;
    }

    /**
     * Set flags.
     *
     * @param flags Flags to set
     * @return ItemBuilder object
     */
    public ItemBuilder flags(@Nullable List<ItemFlag> flags) {
        this.flags = flags;
        return this;
    }

    /**
     * Add a flag.
     *
     * @param flag Flag to add
     * @return ItemBuilder object
     */
    public ItemBuilder addFlag(@NotNull ItemFlag flag) {
        if (this.flags == null)
            this.flags = new ArrayList<>();
        flags.add(flag);
        return this;
    }

    /**
     * Add flags.
     *
     * @param flags Flags to add
     * @return ItemBuilder object
     */
    public ItemBuilder addFlags(List<ItemFlag> flags) {
        if (this.flags == null)
            this.flags = new ArrayList<>();
        this.flags.addAll(flags);
        return this;
    }

    /**
     * Remove a flag.
     *
     * @param flag Flag to remove
     * @return ItemBuilder object
     */
    public ItemBuilder removeFlag(@NotNull ItemFlag flag) {
        if (this.flags != null)
            flags.remove(flag);
        return this;
    }

    /**
     * Clear all flags.
     *
     * @return ItemBuilder object
     */
    public ItemBuilder clearFlags() {
        if (this.flags == null)
            this.flags = new ArrayList<>();
        else this.flags.clear();
        return this;
    }

    /**
     * Set the nbt.
     *
     * @param nbt NBT HashMap
     * @return ItemBuilder object
     */
    public ItemBuilder nbt(@Nullable HashMap<String, String> nbt) {
        this.NBT = nbt;
        return this;
    }

    /**
     * Add NBT key & value.
     *
     * @param key   Key to add
     * @param value Value to add
     * @return ItemBuilder object
     */
    public ItemBuilder addNBT(@NotNull String key, @NotNull String value) {
        if (this.NBT == null)
            this.NBT = new HashMap<>();
        NBT.put(key, value);
        return this;
    }

    /**
     * Remove an NBT key.
     *
     * @param key Key to remove
     * @return ItemBuilder object
     */
    public ItemBuilder removeNBT(@NotNull String key) {
        if (NBT != null)
            NBT.remove(key);
        return this;
    }

    /**
     * Clear all NBT.
     *
     * @return ItemBuilder object
     */
    public ItemBuilder clearNBT() {
        if (this.NBT == null)
            this.NBT = new HashMap<>();
        else this.NBT.clear();
        return this;
    }

    /**
     * Set the glow.
     * Applies Luck 1 enchantment with a HIDE_ENCHANTS flag on build.
     *
     * @param glow Optional boolean, uses true as default
     * @return ItemBuilder object
     */
    public ItemBuilder glow(boolean... glow) {
        this.glow = glow.length <= 0 || glow[0];
        return this;
    }

    /**
     * Set the parse format.
     *
     * @param format Parse format to set
     * @return ItemBuilder object
     */
    public ItemBuilder parseFormat(@NotNull ParseFormat format) {
        this.parseFormat = new ParseFormat(format);
        return this;
    }
}