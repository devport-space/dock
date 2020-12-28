package space.devport.utils.configuration;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import me.realized.tokenmanager.util.inventory.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.DevportPlugin;
import space.devport.utils.item.Amount;
import space.devport.utils.item.ItemPrefab;
import space.devport.utils.item.SkullData;
import space.devport.utils.menu.MenuBuilder;
import space.devport.utils.menu.item.MenuItem;
import space.devport.utils.region.Region;
import space.devport.utils.struct.Conditions;
import space.devport.utils.struct.Rewards;
import space.devport.utils.text.StringUtil;
import space.devport.utils.text.message.Message;
import space.devport.utils.utility.LocationUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to handle Configuration files and custom object loading.
 * Only supports the Yml format.
 *
 * @author Devport Team
 */
@SuppressWarnings("DuplicatedCode")
public class Configuration {

    @Getter
    private final String path;

    @Getter
    private File file;

    @Getter
    private FileConfiguration fileConfiguration;

    @Getter
    private final DevportPlugin plugin;

    @Getter
    @Setter
    private boolean autoSave = false;
    private final ConsoleOutput console;
    private final LocationUtil locationUtil;

    /**
     * Initializes this class, creates file and loads yaml from path.
     * Yml is assigned automatically at the end.
     *
     * @param plugin Main plugin instance
     * @param path   Path to config file
     */
    public Configuration(@NotNull DevportPlugin plugin, @NotNull String path) {
        this(plugin, new File(path.contains(".yml") ? path : path + ".yml"));
    }

    /**
     * Initializes this class from file and loads yaml.
     *
     * @param plugin Java plugin instance
     * @param file   File to load from
     */
    public Configuration(DevportPlugin plugin, @NotNull File file) {
        this.plugin = plugin;
        this.file = file;
        this.path = file.getPath();
        console = plugin.getConsoleOutput();
        locationUtil = plugin.getLocationUtil();
        load();
    }

    /**
     * Loads the Yaml configuration from a file.
     */
    public void load(boolean... silent) {
        file = new File(plugin.getDataFolder(), path);

        if (!file.exists()) {

            // Ensure folder structure
            if (!file.getParentFile().mkdirs())
                if (silent.length > 0 && !silent[0])
                    console.err("Could not create " + path);

            try {
                plugin.saveResource(path, false);
                if (silent.length > 0 && !silent[0]) console.debug("Created new " + path);
            } catch (Exception e) {
                try {
                    if (!file.createNewFile()) {
                        if (silent.length > 0 && !silent[0])
                            console.err("Could not create file at " + file.getAbsolutePath());
                        return;
                    }
                } catch (IOException e1) {
                    if (silent.length > 0 && !silent[0])
                        console.err("Could not create file at " + file.getAbsolutePath());
                    return;
                }
            }
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        if (silent.length > 0 && !silent[0]) console.info("Loaded " + path + "...");
    }

    /**
     * Saves the configuration to file.
     *
     * @return boolean Whether we were successful or not
     */
    public boolean save() {
        try {
            fileConfiguration.save(file);
            return true;
        } catch (IOException e) {
            console.err("Could not save " + path);
            return false;
        }
    }

    /**
     * Saves current FileConfiguration to a different file.
     *
     * @param file File to save to
     * @param set  Whether to set the file as default or not
     */
    public void saveToFile(@NotNull File file, boolean... set) {
        if (set.length > 0)
            if (set[0]) {
                this.file = file;
                save();
                return;
            }

        try {
            this.fileConfiguration.save(file);
        } catch (IOException e) {
            console.err("Could not save " + path);
            if (console.isDebug())
                e.printStackTrace();
        }
    }

    /**
     * Saves current FileConfiguration to a different file by path.
     *
     * @param path Path to save to
     * @param set  Whether to set the file as default or not
     */
    public void saveToFile(@Nullable String path, boolean... set) {
        if (Strings.isNullOrEmpty(path)) {
            console.warn("Could not save " + this.path + " to another location, other path is null.");
            return;
        }

        File file = new File(path.contains(".yml") ? path : path + ".yml");
        saveToFile(file, set);
    }

    /**
     * Deletes the file.
     *
     * @return boolean Whether we were successful or not
     */
    public boolean delete() {
        if (file.delete())
            return true;
        else {
            console.err("Could not delete file " + path);
            return false;
        }
    }

    /**
     * Deletes file and loads it again.
     */
    public void clear() {
        if (file.delete())
            load();
    }

    @NotNull
    public ConfigurationSection section(String path) {
        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);
        return section != null ? section : fileConfiguration.createSection(path);
    }

    /**
     * Returns colored string retrieved from config.
     * Returns null if string is null or empty.
     *
     * @param path Path to string in config file
     * @return String with Bukkit color codes
     */
    @Nullable
    public String getColoredString(@Nullable String path) {
        if (Strings.isNullOrEmpty(path)) return null;
        return StringUtil.color(Strings.isNullOrEmpty(fileConfiguration.getString(path)) ? null : fileConfiguration.getString(path));
    }

    /**
     * Returns colored string retrieved from config.
     * If there's nothing on the path and default is not set, returns null.
     *
     * @param path         Path to string in config file
     * @param defaultValue Optional String, default value to return
     * @return String with Bukkit color codes
     */
    @NotNull
    public String getColoredString(@Nullable String path, @NotNull String defaultValue) {
        return StringUtil.color(getString(path, defaultValue));
    }

    @Nullable
    public String getString(@Nullable String path) {
        return path != null ? fileConfiguration.getString(path) : null;
    }

    /**
     * A replacement for FileConfiguration.getString(String, String), as it doesn't have the functionality we want.
     */
    @NotNull
    public String getString(@Nullable String path, String defaultValue) {
        if (path == null) return defaultValue;
        String str = fileConfiguration.getString(path);
        return str != null ? str : defaultValue;
    }

    @Nullable
    public List<String> getStringList(@Nullable String path) {
        return path != null ? fileConfiguration.getStringList(path) : null;
    }

    /**
     * Returns a list of strings.
     *
     * @param path        Path to list of strings in config file
     * @param defaultList Default value
     * @return List of strings
     */
    @NotNull
    public List<String> getStringList(@Nullable String path, @NotNull List<String> defaultList) {
        if (Strings.isNullOrEmpty(path)) return defaultList;
        return !fileConfiguration.getStringList(path).isEmpty() ? fileConfiguration.getStringList(path) : defaultList;
    }

    /**
     * Returns colored string list retrieved from config.
     *
     * @param path Path to list of strings in config file
     * @return List of strings with Bukkit color codes
     */
    @Nullable
    public final List<String> getColoredList(@Nullable String path) {
        Message msg = getMessage(path);
        return msg != null ? msg.color().getMessage() : null;
    }

    /**
     * Returns colored string list retrieved from config.
     *
     * @param path        Path to list of strings in config file
     * @param defaultList Default list to return when there's nothing on path
     * @return List of strings with Bukkit color codes
     */
    @NotNull
    public final List<String> getColoredList(@Nullable String path, @NotNull List<String> defaultList) {
        return getMessage(path, new Message(defaultList)).color().getMessage();
    }

    /**
     * Returns a colored message from either a String of a List.
     *
     * @param path Path to list of strings in config file
     * @return Multi-line colored string
     */
    @NotNull
    public String getColoredMessage(@Nullable String path) {
        return getMessage(path, new Message()).color().toString();
    }

    /**
     * Returns an array from yaml parsed form a String List.
     *
     * @param path Path to list of strings
     * @return Array of strings
     */
    @NotNull
    public String[] getArrayList(@Nullable String path) {
        return getStringList(path, new ArrayList<>()).toArray(new String[0]);
    }

    /**
     * Returns an array from yaml parsed from String using given delimiter.
     *
     * @param path      Path to look at
     * @param delimiter Delimiter to use
     * @return String array
     */
    @NotNull
    public String[] getArray(@Nullable String path, @NotNull String delimiter) {
        String str = getString(path);
        return str != null ? str.split(delimiter) : new String[0];
    }

    /**
     * Returns a character from the yaml.
     * Returns given default when null.
     *
     * @param path         Path to look at
     * @param defaultValue Default to use
     * @return char
     */
    public char getChar(@Nullable String path, char defaultValue) {
        String str = getString(path);
        return str != null ? str.toCharArray()[0] : defaultValue;
    }

    /**
     * Returns an array of integers parsed from string.
     *
     * @param path String path to an Array.toString output
     * @return Array of integers.
     */
    @Nullable
    public int[] getInts(@Nullable String path) {
        String str = getString(path);

        if (str == null) return new int[]{};

        str = str.replace("[", "").replace("]", "")
                .replace(" ", "");

        if (Strings.isNullOrEmpty(str)) return new int[]{};

        return Arrays.stream(str.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    /**
     * Returns an array of integers parsed from string.
     *
     * @param path         String path to an Array.toString output
     * @param defaultValue Default value to return if there's nothing on path
     * @return Array of integers.
     */
    @Nullable
    public int[] getInts(@Nullable String path, int[] defaultValue) {
        String str = getString(path);

        if (str == null) return defaultValue;

        str = str.replace("[", "").replace("]", "")
                .replace(" ", "");

        if (Strings.isNullOrEmpty(str)) return new int[]{};

        return Arrays.stream(str.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    // --------------------------------- Advanced Load/Save Methods -----------------------------------

    /**
     * Get a Message from the Configuration.
     *
     * @param path Path to the Message
     * @return null if the message is completely absent. Return a blank one otherwise.
     */
    @Nullable
    public Message getMessage(@Nullable String path) {
        if (Strings.isNullOrEmpty(path)) return null;

        if (fileConfiguration.isString(path)) {
            String msg = fileConfiguration.getString(path);
            if (Strings.isNullOrEmpty(msg)) return new Message();
            return new Message(msg);
        } else if (fileConfiguration.isList(path)) {
            List<String> msg = fileConfiguration.getStringList(path);
            return new Message(msg);
        }

        return null;
    }

    /**
     * Loads a message builder either from String or from a list of strings.
     * Returns a default from Default.java when missing.
     *
     * @param path         Path to the Message
     * @param defaultValue Default Message to return.
     * @return Message on the path.
     */
    @NotNull
    public Message getMessage(@Nullable String path, @NotNull Message defaultValue) {

        if (Strings.isNullOrEmpty(path)) return defaultValue;

        if (fileConfiguration.isString(path)) {
            String msg = fileConfiguration.getString(path);
            if (Strings.isNullOrEmpty(msg)) return new Message();
            return new Message(msg);
        } else if (fileConfiguration.isList(path)) {
            List<String> msg = fileConfiguration.getStringList(path);
            return new Message(msg);
        }

        return defaultValue;
    }

    /**
     * Loads a region from given path.
     *
     * @param path Path to the Region
     * @return Region object
     */
    @Nullable
    public Region getRegion(@Nullable String path) {
        Location min = locationUtil.locationFromString(fileConfiguration.getString(path + "." + SubPath.REGION_MIN));
        Location max = locationUtil.locationFromString(fileConfiguration.getString(path + "." + SubPath.REGION_MAX));

        if (min == null) {
            console.err("Could not load a region at path " + path + ", minimum location didn't load.");
            return null;
        }

        if (max == null) {
            console.err("Could not load a region at path " + path + ", maximum location didn't load.");
            return null;
        }

        return new Region(min, max);
    }

    /**
     * Saves a region to given path.
     *
     * @param path   String path to save to
     * @param region Region to save
     */
    public void setRegion(@Nullable String path, @Nullable Region region) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            console.err("Could not save region to path " + path + ", path is invalid.");
            return;
        }

        // Check region
        if (region == null) {
            console.err("Could not save region to path " + path + ", region is null.");
            return;
        }

        ConfigurationSection section = fileConfiguration.createSection(path);

        section.set(SubPath.REGION_MIN.toString(), locationUtil.locationToString(region.getMin()));
        section.set(SubPath.REGION_MAX.toString(), locationUtil.locationToString(region.getMax()));

        if (autoSave)
            save();
    }

    /**
     * Loads a MenuBuilder from given path.
     *
     * @param path String path to load from
     * @return MenuBuilder object
     */
    @Nullable
    public MenuBuilder getMenuBuilder(@Nullable String path) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            console.err("Could not load MenuBuilder at path " + path + ", path is invalid.");
            return null;
        }

        MenuBuilder menuBuilder = new MenuBuilder(plugin);

        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

        if (section == null) {
            console.err("Could not load MenuBuilder at path " + path + ", path is invalid.");
            return null;
        }

        menuBuilder.title(section.getString(SubPath.MENU_TITLE.toString(), "My nice menu"));

        menuBuilder.slots(section.getInt(SubPath.MENU_SLOTS.toString(), 9));

        // Load inventory matrix
        if (section.contains(SubPath.MENU_MATRIX.toString()))
            menuBuilder.buildMatrix(getArrayList(path + "." + SubPath.MENU_MATRIX));

        ConfigurationSection itemsSection = section.getConfigurationSection(SubPath.MENU_ITEMS.toString());

        // Load items
        if (itemsSection != null) {
            for (String itemName : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = section.getConfigurationSection(SubPath.MENU_ITEMS + "." + itemName);

                if (itemSection == null) continue;

                MenuItem item = getMenuItem(path + "." + SubPath.MENU_ITEMS + "." + itemName);

                if (item == null) continue;

                // If it contains matrix-char
                if (itemSection.contains(SubPath.MENU_MATRIX_CHAR.toString()))
                    menuBuilder.addMatrixItem(getChar(itemSection.getCurrentPath() + "." + SubPath.MENU_MATRIX_CHAR, ' '), item);
                else
                    menuBuilder.setItem(item);
            }
        }

        return menuBuilder;
    }

    /**
     * Loads a MenuItem from given path.
     *
     * @param path String path to the item
     * @return MenuItem object
     */
    @Nullable
    public MenuItem getMenuItem(@Nullable String path) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            console.err("Could not load MenuItem at path " + path + ", path is invalid.");
            return null;
        }

        // Load ItemBuilder
        ItemPrefab itemPrefab = getItem(path);

        String itemName = path.contains(".") ? path.split("\\.")[path.split("\\.").length - 1] : path;

        int slot = fileConfiguration.getInt(path + "." + SubPath.MENU_ITEM_SLOT, -1);

        if (itemPrefab == null) return null;

        MenuItem item = new MenuItem(itemPrefab, itemName, slot);

        item.setCancelClick(fileConfiguration.getBoolean(path + "." + SubPath.MENU_ITEM_CANCEL_CLICK, true));

        // Load rewards
        if (section(path).contains("rewards"))
            item.setRewards(getRewards(path.concat(".rewards")));

        return item;
    }

    /**
     * Loads an ItemPrefab from given path.
     *
     * @param path String path to ItemBuilder
     * @return ItemBuilder object
     */
    public ItemPrefab getItem(@Nullable String path, @Nullable ItemPrefab defaultValue) {

        /// Check path
        if (Strings.isNullOrEmpty(path))
            return defaultValue;

        // Try to load
        try {
            ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

            if (section == null) {
                console.debug("Invalid section - path " + path + ", returning null.");
                return defaultValue;
            }

            String type = section.getString(SubPath.ITEM_TYPE.toString());

            if (Strings.isNullOrEmpty(type)) {
                console.debug("Invalid material on path " + path + ", returning null.");
                return defaultValue;
            }

            XMaterial xMaterial = XMaterial.matchXMaterial(type.toUpperCase()).orElse(null);

            if (xMaterial == null) {
                console.debug("Invalid material on path " + path + ", returning null.");
                return defaultValue;
            }

            Material material = xMaterial.parseMaterial();

            if (material == null) {
                console.debug("Invalid material on path " + path + ", returning null.");
                return defaultValue;
            }

            // Data
            short data = (short) (section.contains(SubPath.ITEM_DATA.toString()) ? section.getInt(SubPath.ITEM_DATA.toString()) : 0);

            ItemPrefab b = ItemPrefab.createNew(material, plugin);

            // Display name
            if (section.contains(SubPath.ITEM_NAME.toString()))
                b.withName(section.getString(SubPath.ITEM_NAME.toString()));

            // Amount
            if (section.contains(SubPath.ITEM_AMOUNT.toString()))
                b.withAmount(getAmount(section.getCurrentPath() + "." + SubPath.ITEM_AMOUNT.toString(), new Amount(1)));

            // TODO: Glow
            // if (section.contains(SubPath.ITEM_GLOW.toString()))
            //    b.glow(section.getBoolean(SubPath.ITEM_GLOW.toString()));

            // Lore
            if (section.contains(SubPath.ITEM_LORE.toString()))
                b.withLore(section.getStringList(SubPath.ITEM_LORE.toString()));

            // Enchants
            if (section.contains(SubPath.ITEM_ENCHANTS.toString())) {
                List<String> dataList = section.getStringList(SubPath.ITEM_ENCHANTS.toString());

                for (String dataString : dataList) {
                    int level = 1;

                    if (dataString.contains(SubPath.ITEM_ENCHANT_DELIMITER.toString())) {
                        level = Integer.parseInt(dataString.split(SubPath.ITEM_ENCHANT_DELIMITER.toString())[1]);
                        dataString = dataString.split(SubPath.ITEM_ENCHANT_DELIMITER.toString())[0];
                    }

                    XEnchantment xEnchantment = XEnchantment.matchXEnchantment(dataString).orElse(null);

                    if (xEnchantment == null) {
                        console.warn("Could not parse enchantment " + dataString);
                        continue;
                    }

                    b.addEnchant(xEnchantment, level);
                }
            }

            // Item Flags
            if (section.contains(SubPath.ITEM_FLAGS.toString()))
                for (String flagName : section.getStringList(SubPath.ITEM_FLAGS.toString())) {
                    ItemFlag flag = ItemFlag.valueOf(flagName);

                    b.withFlags(flag);
                }

            // Skull data
            if (section.contains(SubPath.ITEM_SKULL_DATA.toString()))
                b.withSkullData(SkullData.fromString(section.getString(SubPath.ITEM_SKULL_DATA.toString())));

            // NBT
            if (section.contains(SubPath.ITEM_NBT.toString()))
                for (String nbtString : section.getStringList(SubPath.ITEM_NBT.toString()))
                    if (nbtString.contains(SubPath.ITEM_NBT_DELIMITER.toString())) {
                        String[] arr = nbtString.split(SubPath.ITEM_NBT_DELIMITER.toString());
                        b.addNBT(arr[0], arr.length > 1 ? arr[1] : "");
                    }

            return b;
        } catch (Exception e) {
            if (console.isDebug())
                e.printStackTrace();
        }

        return defaultValue;
    }

    /**
     * Loads an ItemBuilder from given path.
     *
     * @param path String path to ItemBuilder
     * @return ItemBuilder object
     */
    @Nullable
    public ItemPrefab getItem(@Nullable String path) {
        return getItem(path, null);
    }

    /**
     * Set an ItemPrefab object to a yaml file under a given path.
     *
     * @param path    Path to save it under
     * @param builder ItemPrefab to save.
     */
    public void setItemBuilder(@Nullable String path, @NotNull ItemPrefab builder) {

        if (Strings.isNullOrEmpty(path)) {
            console.err("Could not save ItemBuilder, path null.");
            return;
        }

        ConfigurationSection section = section(path);

        section.set(SubPath.ITEM_TYPE.toString(), builder.getMaterial().toString());

        if (builder.hasDamage())
            section.set(SubPath.ITEM_DATA.toString(), builder.getDamage().toString());

        if (!(builder.getAmount().isFixed() && builder.getAmount().getFixedValue() == 1))
            section.set(SubPath.ITEM_AMOUNT.toString(), builder.getAmount().isFixed() ? (int) builder.getAmount().getFixedValue() : builder.getAmount().toString());

        if (!builder.getName().isEmpty())
            section.set(SubPath.ITEM_NAME.toString(), builder.getName().toString());

        if (!builder.getLore().isEmpty())
            section.set(SubPath.ITEM_LORE.toString(), builder.getLore().getMessage());

        if (!builder.getEnchants().isEmpty()) {
            List<String> enchants = new ArrayList<>();
            builder.getEnchants().forEach(e -> enchants.add(e.getEnchantment().name() + SubPath.ITEM_ENCHANT_DELIMITER + e.getLevel().toString()));
            section.set(SubPath.ITEM_ENCHANTS.toString(), enchants);
        }

        if (!builder.getFlags().isEmpty())
            section.set(SubPath.ITEM_FLAGS.toString(), builder.getFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));

        if (!builder.getNbt().isEmpty()) {
            List<String> nbt = new ArrayList<>();
            builder.getNbt().forEach((k, v) -> nbt.add(k + SubPath.ITEM_NBT_DELIMITER + v.toString()));
            section.set(SubPath.ITEM_NBT.toString(), nbt);
        }

        // TODO: Glow
        // if (builder.isGlow())
        //section.set(SubPath.ITEM_GLOW.toString(), builder.isGlow());

        if (builder.getSkullData() != null)
            section.set(SubPath.ITEM_SKULL_DATA.toString(), builder.getSkullData().toString());

        if (autoSave)
            save();
    }

    public void setMessage(@Nullable String path, @NotNull Message message) {
        if (Strings.isNullOrEmpty(path)) {
            console.err("Could not save Message, path null.");
            return;
        }

        if (message.isEmpty())
            fileConfiguration.set(path, "");
        else {
            if (message.getMessage().size() > 1) {
                fileConfiguration.set(path, message.getMessage());
            } else
                fileConfiguration.set(path, message.getMessage().get(0));
        }

        if (autoSave)
            save();
    }

    /**
     * Load an Amount from given path.
     *
     * @param path         String path to Amount
     * @param defaultValue Optional Amount, default to return
     * @return Amount object
     */
    @NotNull
    public Amount getAmount(@Nullable String path, @NotNull Amount defaultValue) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            console.err("Could not load Amount at path " + path + ", path is invalid.");
            return defaultValue;
        }

        String dataStr = fileConfiguration.getString(path);

        if (Strings.isNullOrEmpty(dataStr))
            return defaultValue;

        try {
            if (dataStr.contains("-")) {
                String[] arr = dataStr.split("-");

                double low = Double.parseDouble(arr[0]);
                double high = Double.parseDouble(arr[1]);

                return new Amount(low, high);
            } else {
                int n = Integer.parseInt(dataStr);

                return new Amount(n);
            }
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    /**
     * Load an Amount from given path.
     *
     * @param path String path to Amount
     * @return Amount object
     */
    @Nullable
    public Amount getAmount(@Nullable String path) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            console.err("Could not load Amount at path " + path + ", path is invalid.");
            return null;
        }

        if (fileConfiguration.isDouble(path)) {
            double fixed = fileConfiguration.getDouble(path);
            return new Amount(fixed);
        }

        String dataStr = fileConfiguration.getString(path);

        if (Strings.isNullOrEmpty(dataStr) ||
                !dataStr.contains("-"))
            return null;

        dataStr = dataStr.replace(" ", "");

        try {
            String[] arr = dataStr.split("-");

            double low = Double.parseDouble(arr[0]);
            double high = Double.parseDouble(arr[1]);

            return new Amount(low, high);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @NotNull
    public Conditions getConditions(@Nullable String path) {
        Conditions conditions = new Conditions();

        if (Strings.isNullOrEmpty(path)) return conditions;

        conditions.operator(fileConfiguration.getBoolean(path + ".operator", false));
        conditions.permissions(getStringList(path + ".permissions", new ArrayList<>()));

        conditions.worlds(getStringList(path + ".worlds", new ArrayList<>()));

        conditions.health(getAmount(path + ".health", new Amount(0)));

        return conditions;
    }

    @NotNull
    public Rewards getRewards(@Nullable String path) {
        Rewards rewards = new Rewards(plugin);

        if (Strings.isNullOrEmpty(path)) return rewards;

        rewards.broadcast(getMessage(path + ".broadcast", new Message()));
        rewards.inform(getMessage(path + ".inform", new Message()));

        rewards.commands(getStringList(path + ".commands", new ArrayList<>()));

        rewards.money(getAmount(path + ".money", new Amount(0)));
        rewards.tokens(getAmount(path + ".tokens", new Amount(0)));

        ConfigurationSection itemsSection = fileConfiguration.getConfigurationSection(path + ".items");

        if (itemsSection == null) return rewards;

        for (String name : itemsSection.getKeys(false)) {
            ItemPrefab itemBuilder = getItem(path + ".items." + name);

            if (itemBuilder != null)
                rewards.addItem(itemBuilder);
        }

        return rewards;
    }

    public void setRewards(String path, Rewards rewards) {
        if (Strings.isNullOrEmpty(path)) return;

        ConfigurationSection section = section(path);

        if (!rewards.getTokens().isEmpty())
            section.set("tokens", rewards.getTokens().toString());
        if (!rewards.getMoney().isEmpty())
            section.set("money", rewards.getMoney().toString());
        if (!rewards.getInform().isEmpty())
            section.set("inform", rewards.getInform().getMessage());
        if (!rewards.getBroadcast().isEmpty())
            section.set("broadcast", rewards.getBroadcast().getMessage());
        if (!rewards.getCommands().isEmpty())
            section.set("commands", rewards.getCommands());

        for (int i = 0; i < rewards.getItems().size(); i++) {
            setItemBuilder(path + ".items." + i, rewards.getItems().get(i));
        }

        if (autoSave)
            save();
    }
}