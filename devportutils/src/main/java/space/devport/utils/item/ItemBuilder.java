package space.devport.utils.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.StringUtil;
import space.devport.utils.text.message.CachedMessage;
import space.devport.utils.text.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handle Item construction.
 *
 * Set to be removed and migrated to ItemPrefab in 4.0.0
 *
 * @author Devport Team
 */
@Deprecated
@NoArgsConstructor
public class ItemBuilder {

    @Getter
    public static final List<String> FILTERED_NBT = new ArrayList<>(Arrays.asList("Damage", "Enchantments", "display", "HideFlags"));

    // Item Data information
    @Getter
    private Material material = Material.STONE;
    @Getter
    private short damage = 0;

    @Getter
    private Amount amount = new Amount(1);

    @Getter
    private CachedMessage displayName = new CachedMessage();

    @Getter
    private CachedMessage lore = new CachedMessage();

    @Getter
    private Map<XEnchantment, Integer> enchants = new HashMap<>();

    @Getter
    private List<ItemFlag> flags = new ArrayList<>();

    // Holds item nbt keys & values
    @Getter
    private Map<String, String> NBT = new HashMap<>();

    @Getter
    private SkullData skullData;

    // Apply luck & hide enchants flag?
    @Getter
    private boolean glow = false;

    // ParseFormat for placeholders
    @Getter
    private transient Placeholders placeholders = new Placeholders();

    /**
     * Constructor with a material.
     *
     * @param material Material to use
     */
    public ItemBuilder(@NotNull Material material) {
        this.material = material;
    }

    /**
     * Copy constructor.
     *
     * @param builder ItemBuilder to copy
     */
    public ItemBuilder(@NotNull ItemBuilder builder) {
        this.displayName = builder.getDisplayName();
        this.material = builder.getMaterial();
        this.amount = builder.getAmount();
        this.damage = builder.getDamage();
        this.glow = builder.isGlow();
        this.flags = new ArrayList<>(builder.getFlags());
        this.enchants = new HashMap<>(builder.getEnchants());
        this.placeholders = new Placeholders(builder.getPlaceholders());
        this.NBT = new HashMap<>(builder.getNBT());
        this.lore = builder.getLore();
        this.skullData = builder.getSkullData() == null ? null : new SkullData(builder.getSkullData());
    }

    /**
     * To-builder constructor.
     *
     * @param item Item to convert
     */
    public ItemBuilder(@NotNull ItemStack item) {
        this.material = item.getType();
        this.damage = (byte) item.getDurability();

        this.amount = new Amount(item.getAmount());

        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            // Display name
            if (itemMeta.hasDisplayName())
                this.displayName = new CachedMessage(itemMeta.getDisplayName());

            // Lore
            if (itemMeta.hasLore())
                this.lore = new CachedMessage(itemMeta.getLore());

            // Enchants
            if (itemMeta.hasEnchants())
                this.enchants = translateEnchants(itemMeta.getEnchants());

            // Flags
            this.flags = new ArrayList<>(itemMeta.getItemFlags());

            // Skull data
            if (XMaterial.matchXMaterial(item) == XMaterial.PLAYER_HEAD)
                this.skullData = SkullData.readSkullTexture(item);
        }

        Map<String, String> map = ItemNBTEditor.getNBTTagMap(item);

        for (String key : map.keySet()) {
            if (!FILTERED_NBT.contains(key))
                this.NBT.put(key, map.get(key));
        }
    }

    @NotNull
    private Map<XEnchantment, Integer> translateEnchants(@Nullable Map<Enchantment, Integer> input) {
        Map<XEnchantment, Integer> output = new HashMap<>();
        if (input != null)
            for (Map.Entry<Enchantment, Integer> entry : input.entrySet()) {
                XEnchantment xEnchantment = XEnchantment.matchXEnchantment(entry.getKey());
                output.put(xEnchantment, entry.getValue());
            }
        return output;
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
        if (!displayName.isEmpty())
            displayName.replace(key, value);

        if (!lore.isEmpty())
            lore.replace(key, value);
        return this;
    }

    /**
     * Parse the item's display name and lore with given parse format.
     *
     * @param placeholders Parse format to parse with
     * @return ItemBuilder object
     */
    public ItemBuilder parseWith(@NotNull Placeholders placeholders) {
        this.placeholders = this.placeholders.copy(placeholders);
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
        ItemStack item = new ItemStack(material, amount.getInt(), damage);
        item.setDurability(damage);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            ConsoleOutput.getInstance().err("Could not build Item, there's not ItemMeta on a fresh ItemStack.");
            return item;
        }

        // Apply lore
        if (!lore.isEmpty()) {
            //TODO Placeholders.parseWith(Placeholders).parse() acted weirdly. Most likely the new context system.
            List<String> lore = StringUtil.color(placeholders.parse(this.lore.getMessage()));

            meta.setLore(lore);
        }

        // Apply display name
        if (!displayName.isEmpty()) {
            meta.setDisplayName(StringUtil.color(placeholders.parse(this.displayName.toString())));

            displayName.pull();
        }

        // Apply enchants
        if (!enchants.isEmpty()) {
            for (Map.Entry<XEnchantment, Integer> entry : enchants.entrySet()) {
                Enchantment enchantment = entry.getKey().parseEnchantment();

                if (enchantment == null) continue;

                meta.addEnchant(enchantment, entry.getValue(), true);
            }
        }

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
                if (FILTERED_NBT.contains(key)) continue;

                ItemStack i = ItemNBTEditor.writeNBT(item, key, NBT.get(key));

                if (i == null)
                    ConsoleOutput.getInstance().warn("Couldn't write NBT to item.");
                else
                    item = i;
            }

        // Skull data
        if (skullData != null)
            item = skullData.apply(item);

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
     * @param amount Amount to set
     * @return ItemBuilder object
     */
    public ItemBuilder amount(@NotNull Amount amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Set the amount.
     *
     * @param amount Amount to set
     * @return ItemBuilder object
     */
    public ItemBuilder amount(int amount) {
        return amount(new Amount(amount));
    }

    /**
     * Set the random amount range.
     *
     * @param low  Low amount to set
     * @param high High amount to set
     * @return ItemBuilder object
     */
    public ItemBuilder amount(int low, int high) {
        return amount(new Amount(low, high));
    }

    /**
     * Set the displayName.
     *
     * @param displayName Material to set
     * @return ItemBuilder object
     */
    public ItemBuilder displayName(@Nullable Message displayName) {
        this.displayName = displayName != null ? new CachedMessage(displayName) : new CachedMessage();
        return this;
    }

    /**
     * Set the display name with a String.
     *
     * @param displayName Display name in String
     * @return ItemBuilder object
     */
    public ItemBuilder displayName(@Nullable String displayName) {
        return displayName(new CachedMessage(displayName));
    }

    /**
     * Set the lore.
     *
     * @param lore Lore to set
     * @return ItemBuilder object
     */
    public ItemBuilder lore(@Nullable Message lore) {
        this.lore = lore != null ? new CachedMessage(lore) : new CachedMessage();
        return this;
    }

    /**
     * Set the lore with a List.
     *
     * @param lore Lore to set in List
     * @return ItemBuilder object
     */
    public ItemBuilder lore(@Nullable List<String> lore) {
        return lore(new CachedMessage(lore));
    }

    /**
     * Set the lore with an Array.
     *
     * @param lore Lore to set in Array
     * @return ItemBuilder object
     */
    public ItemBuilder lore(@NotNull String... lore) {
        return lore(new CachedMessage(new ArrayList<>(Arrays.asList(lore))));
    }

    /**
     * Add a line to lore.
     *
     * @param line Line to add in String
     * @return ItemBuilder object
     */
    public ItemBuilder addLine(@NotNull String line) {
        if (lore == null) this.lore = new CachedMessage();
        lore.append(line);
        return this;
    }

    /**
     * Set the enchants.
     *
     * @param enchants Enchants to set
     * @return ItemBuilder object
     */
    public ItemBuilder enchants(@Nullable Map<Enchantment, Integer> enchants) {
        this.enchants = translateEnchants(enchants);
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
        enchants.put(XEnchantment.matchXEnchantment(enchantment), level.length > 0 ? level[0] : 1);
        return this;
    }

    public ItemBuilder addEnchant(@NotNull XEnchantment xEnchantment, int... level) {
        Enchantment enchantment = xEnchantment.parseEnchantment();
        if (enchantment == null) return this;
        return this.addEnchant(enchantment, level);
    }

    /**
     * Add enchants.
     *
     * @param enchants Enchants to add
     * @return ItemBuilder object
     */
    public ItemBuilder addEnchants(@NotNull Map<Enchantment, Integer> enchants) {
        if (this.enchants == null)
            this.enchants = new HashMap<>();
        this.enchants.putAll(translateEnchants(enchants));
        return this;
    }

    /**
     * Remove an enchant.
     *
     * @param enchantment Enchant to remove
     * @return ItemBuilder object
     */
    public ItemBuilder removeEnchant(@Nullable Enchantment enchantment) {
        if (enchantment == null) return this;
        XEnchantment xEnchantment = XEnchantment.matchXEnchantment(enchantment);
        return removeEnchant(xEnchantment);
    }

    public ItemBuilder removeEnchant(@Nullable XEnchantment enchantment) {
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
     * Add NBT key and value.
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
     * Check if ItemBuilder has a NBT key.
     *
     * @param key The key to look for
     */
    public boolean hasNBT(String key) {
        return this.NBT != null && this.NBT.containsKey(key);
    }

    /**
     * Check if ItemBuilder has NBT key with a value.
     *
     * @param key   The key to look for
     * @param value Required value
     */
    public boolean hasNBT(String key, String value) {
        return this.hasNBT(key) && this.getNBT().get(key).equals(value);
    }

    /**
     * Set the glow.
     * Applies Luck 1 enchantment with a HIDE_ENCHANTS flag on build.
     *
     * @param glow Optional boolean, true default
     * @return ItemBuilder object
     */
    public ItemBuilder glow(boolean... glow) {
        this.glow = glow.length <= 0 || glow[0];
        return this;
    }

    public ItemBuilder skullData(SkullData skullData) {
        this.skullData = skullData;
        return this;
    }

    /**
     * Set the placeholders.
     *
     * @param placeholders Parse format to set
     * @return ItemBuilder object
     */
    public ItemBuilder setPlaceholders(@NotNull Placeholders placeholders) {
        this.placeholders = new Placeholders(placeholders);
        return this;
    }
}