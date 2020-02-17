package space.devport.utils.itemutil;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import space.devport.utils.DevportUtils;
import space.devport.utils.messageutil.ParseFormat;
import space.devport.utils.messageutil.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {

    // TODO Add documentation once redone
    // TODO Hook to ConsoleOutput

    private Material type = Material.STONE;
    private short damage = 0;

    private String displayName;

    private int amount = 1;

    private List<String> lore = new ArrayList<>();

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

    // Load ItemStack int o ItemBuilder
    public ItemBuilder(ItemStack item) {
        this.type = item.getType();

        this.damage = (byte) item.getDurability();

        this.amount = item.getAmount();

        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.hasDisplayName())
                this.displayName = itemMeta.getDisplayName();

            if (itemMeta.hasLore())
                this.lore = itemMeta.getLore();

            if (itemMeta.hasEnchants())
                this.enchants = new HashMap<>(itemMeta.getEnchants());

            this.flags = new ArrayList<>(itemMeta.getItemFlags());
        }

        // TODO Add NBT load
    }

    public static ItemBuilder loadBuilder(FileConfiguration yaml, String path, String[] paths) {
        try {
            ConfigurationSection section = yaml.getConfigurationSection(path);

            String type = section.getString(paths[0]);
            Material mat = Material.valueOf(type);

            short data = (short) section.getInt(paths[1]);

            ItemBuilder b = new ItemBuilder(mat).damage(data);

            if (section.contains(paths[2]))
                b.displayName(section.getString(paths[2]));

            if (section.contains(paths[3]))
                b.amount(section.getInt(paths[3]));

            if (section.contains(paths[4]))
                b.glow(section.getBoolean(paths[4]));

            if (section.contains(paths[5]))
                b.lore(section.getStringList(paths[5]));

            if (section.contains(paths[6])) {
                List<String> dataList = section.getStringList(paths[6]);

                for (String dataString : dataList) {
                    int level = 1;

                    if (dataString.contains(";")) {
                        level = Integer.parseInt(dataString.split(";")[1]);
                        dataString = dataString.split(";")[0];
                    }

                    Enchantment enchantment = Enchantment.getByName(dataString);

                    b.addEnchant(enchantment, level);
                }
            }

            if (section.contains(paths[7]))
                for (String flagName : section.getStringList(paths[7])) {
                    ItemFlag flag = ItemFlag.valueOf(flagName);

                    b.addFlag(flag);
                }

            if (section.contains(paths[8]))
                for (String nbtString : section.getStringList(paths[8]))
                    b.addNBT(nbtString.split(";")[0], nbtString.split(";")[1]);

            return b;
        } catch (NullPointerException | IllegalArgumentException e) {
            if (DevportUtils.inst.getConsoleOutput().isDebug())
                e.printStackTrace();
            return new ItemBuilder(Material.STONE).displayName("&cCould not load item").addLine("&7Reason: &c" + e.getMessage());
        }
    }

    public static ItemBuilder loadBuilder(FileConfiguration yaml, String path) {
        String[] paths = new String[]{"type", "damage", "name", "amount", "glow", "lore", "enchants", "flags", "nbt"};
        return loadBuilder(yaml, path, paths);
    }

    // Parses lore & displayname immediately.
    // TODO Change so it keeps the original somewhere, implementing MessageBuilder would be the best option i guess.
    public ItemBuilder parse(String key, String value) {

        if (displayName != null)
            displayName = displayName.replace(key, value);

        if (lore != null)
            if (!lore.isEmpty()) {
                List<String> newLore = new ArrayList<>();

                for (String line : lore)
                    newLore.add(line.replace(key, value));

                lore = newLore;
            }

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
            List<String> list = lore.stream().map(line -> StringUtil.color(parseFormat.parse(line))).collect(Collectors.toList());
            meta.setLore(list);
        }

        // Apply display name
        // Parse ParseFormat placeholders
        // Color
        if (displayName != null)
            meta.setDisplayName(StringUtil.color(parseFormat.parse(displayName)));

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
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder lore(String[] lore) {
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLine(String str) {
        lore.add(str);
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
        this.parseFormat = format;
        return this;
    }

    // ------------------------------ Custom Getters ------------------------------

    public Material type() {
        return type;
    }

    public short damage() {
        return damage;
    }

    public String displayName() {
        return displayName;
    }

    public int amount() {
        return amount;
    }

    public List<String> lore() {
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