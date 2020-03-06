package space.devport.utils.itemutil;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.devport.utils.messageutil.MessageBuilder;
import space.devport.utils.messageutil.ParseFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ItemBuilder {

    // TODO Add documentation once redone
    // TODO Hook to ConsoleOutput

    // TODO Implement Mutli-version material support. XSeries: https://github.com/CryptoMorin/XSeries

    private Material type = Material.STONE;
    private short damage = 0;

    private MessageBuilder displayName;

    private int amount = 1;

    private MessageBuilder lore = new MessageBuilder();

    // Apply luck & hide enchants flag?
    private boolean glow = false;

    private HashMap<Enchantment, Integer> enchants = new HashMap<>();

    private List<ItemFlag> flags = new ArrayList<>();

    // Holds item nbt keys & values
    private HashMap<String, String> NBT = new HashMap<>();

    // ParseFormat for placeholders
    private ParseFormat parseFormat = new ParseFormat();

    // Default constructor
    public ItemBuilder() {
    }

    // Constructor with a type.
    public ItemBuilder(Material type) {
        this.type = type;
    }

    public ItemBuilder(ItemBuilder builder) {
        this.displayName = new MessageBuilder(builder.displayName());
        this.type = builder.type();
        this.amount = builder.amount();
        this.damage = builder.damage();
        this.glow = builder.glow();
        this.flags = new ArrayList<>(builder.flags());
        this.enchants = new HashMap<>(builder.enchants());
        this.parseFormat = new ParseFormat(builder.parseFormat());
        this.NBT = new HashMap<>(builder.nbt());
        this.lore = new MessageBuilder(builder.lore());
    }

    // Load ItemStack int o ItemBuilder
    public ItemBuilder(ItemStack item) {
        this.type = item.getType();

        this.damage = (byte) item.getDurability();

        this.amount = item.getAmount();

        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.hasDisplayName())
                this.displayName = new MessageBuilder(itemMeta.getDisplayName());

            if (itemMeta.hasLore())
                this.lore = new MessageBuilder(itemMeta.getLore());

            if (itemMeta.hasEnchants())
                this.enchants = new HashMap<>(itemMeta.getEnchants());

            this.flags = new ArrayList<>(itemMeta.getItemFlags());
        }

        // TODO Add NBT load
    }

    // Parses lore & displayname immediately.
    public ItemBuilder parse(String key, String value) {

        if (displayName != null)
            if (!displayName.isEmpty())
                displayName.parsePlaceholder(key, value);

        if (!lore.isEmpty())
            lore.parsePlaceholder(key, value);

        return this;
    }

    // Parse the item with a different ParseFormat
    public ItemBuilder parseWith(ParseFormat format) {
        for (String key : format.getPlaceholders())
            parse(key, format.getPlaceholderCache().get(key));
        return this;
    }

    // Build the ItemStack
    public ItemStack build() {
        ItemStack item = new ItemStack(type, amount, damage);

        ItemMeta meta = item.getItemMeta();

        // Apply lore
        // Don't edit the lore
        // Parse ParseFormat placeholders
        // Color
        if (!lore.isEmpty()) {
            lore.copyPlaceholders(parseFormat).parsePlaceholders().color();
            meta.setLore(lore.getWorkingMessage());

            lore.pull();
        }

        // Apply display name
        // Parse ParseFormat placeholders
        // Color
        if (displayName != null) {
            meta.setDisplayName(displayName.copyPlaceholders(parseFormat).parsePlaceholders().color().toString());

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
        if (!NBT.isEmpty()) {
            for (String key : NBT.keySet()) {
                // Assertions? Is that a good thing?
                assert item != null : "Item could not have been built.";
                item = ItemNBTEditor.writeNBT(item, key, NBT.get(key));
            }
        }

        return item;
    }

    // ------------------------------ Custom Setters ------------------------------

    public ItemBuilder type(Material mat) {
        this.type = mat;
        return this;
    }

    public ItemBuilder damage(short data) {
        this.damage = data;
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder displayName(String displayName) {
        this.displayName = new MessageBuilder(displayName);
        return this;
    }

    public ItemBuilder displayName(MessageBuilder displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = new MessageBuilder(lore);
        return this;
    }

    public ItemBuilder lore(MessageBuilder lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder lore(String[] lore) {
        this.lore = new MessageBuilder(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLine(String str) {
        lore.addLine(str);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        enchants.put(enchantment, level);
        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        enchants.remove(enchantment);
        return this;
    }

    public ItemBuilder clearEnchants() {
        this.enchants = new HashMap<>();
        return this;
    }

    public ItemBuilder addFlag(ItemFlag flag) {
        flags.add(flag);
        return this;
    }

    public ItemBuilder addFlags(List<ItemFlag> flags) {
        this.flags.addAll(flags);
        return this;
    }

    public ItemBuilder removeFlag(ItemFlag flag) {
        flags.remove(flag);
        return this;
    }

    public ItemBuilder clearFlags() {
        this.flags = new ArrayList<>();
        return this;
    }

    public ItemBuilder addNBT(String key, String value) {
        NBT.put(key, value);
        return this;
    }

    public ItemBuilder removeNBT(String key) {
        NBT.remove(key);
        return this;
    }

    public ItemBuilder clearNBT() {
        NBT.clear();
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        this.glow = glow;
        return this;
    }

    // ParseFormat
    public ItemBuilder parseFormat(ParseFormat format) {
        this.parseFormat = new ParseFormat(format);
        return this;
    }

    @Override
    public String toString() {
        return "(" + type + ":" + damage + ", " + displayName + ")";
    }

    // ------------------------------ Custom Getters ------------------------------

    public Material type() {
        return type;
    }

    public short damage() {
        return damage;
    }

    public MessageBuilder displayName() {
        return displayName;
    }

    public int amount() {
        return amount;
    }

    public MessageBuilder lore() {
        return lore;
    }

    public boolean glow() {
        return glow;
    }

    public HashMap<Enchantment, Integer> enchants() {
        return enchants;
    }

    public List<ItemFlag> flags() {
        return flags;
    }

    public HashMap<String, String> nbt() {
        return NBT;
    }

    public ParseFormat parseFormat() {
        return parseFormat;
    }
}