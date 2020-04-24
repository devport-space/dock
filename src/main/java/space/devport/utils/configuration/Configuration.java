package space.devport.utils.configuration;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportUtils;
import space.devport.utils.item.Amount;
import space.devport.utils.item.ItemBuilder;
import space.devport.utils.menu.MenuBuilder;
import space.devport.utils.menu.item.MenuItem;
import space.devport.utils.region.Region;
import space.devport.utils.struct.Conditions;
import space.devport.utils.struct.Rewards;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.StringUtil;
import space.devport.utils.text.message.Message;
import space.devport.utils.utility.Default;
import space.devport.utils.utility.LocationUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class to handle Configuration files and custom object loading.
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
    private final JavaPlugin plugin;

    @Getter
    @Setter
    private boolean autoSave = false;

    /**
     * Initializes this class, creates file and loads yaml from path.
     * Yml is assigned automatically at the end.
     *
     * @param plugin Main plugin instance
     * @param path   Path to config file
     */
    public Configuration(@NotNull JavaPlugin plugin, @NotNull String path) {
        this(plugin, new File(path.contains(".yml") ? path : path + ".yml"));
    }

    /**
     * Initializes this class from file and loads yaml.
     *
     * @param plugin Java plugin instance
     * @param file   File to load from
     */
    public Configuration(@NotNull JavaPlugin plugin, @NotNull File file) {
        this.plugin = plugin;
        this.file = file;
        this.path = file.getPath();

        if (DevportUtils.getInstance() == null) {
            new DevportUtils(plugin);
            return;
        }

        load();
    }

    /**
     * Loads the Yaml configuration from a file.
     */
    public void load() {
        file = new File(plugin.getDataFolder(), path);

        if (!file.exists())
            try {
                plugin.saveResource(path, false);
            } catch (Exception e) {
                try {
                    if (!file.createNewFile()) {
                        DevportUtils.getInstance().getConsoleOutput().err("Could not create " + path);
                        return;
                    }
                } catch (IOException e1) {
                    DevportUtils.getInstance().getConsoleOutput().err("Could not create " + path);
                    return;
                }
            }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        DevportUtils.getInstance().getConsoleOutput().info("Loaded " + path + "...");
    }

    /**
     * Reloads the yaml, checks if file exists and loads/creates it again.
     */
    @Deprecated
    public void reload() {
        load();
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
            DevportUtils.getInstance().getConsoleOutput().err("Could not save " + path);
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
            DevportUtils.getInstance().getConsoleOutput().err("Could not save " + path);
            if (DevportUtils.getInstance().getConsoleOutput().isDebug())
                e.printStackTrace();
        }
    }

    /**
     * Saves current FileConfiguration to a different file by path.
     *
     * @param path Path to save to
     * @param set  Whether to set the file as default or not
     */
    public void saveToFile(@NotNull String path, boolean... set) {
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
            DevportUtils.getInstance().getConsoleOutput().err("Could not delete file " + path);
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

    /**
     * Returns colored string retrieved from config.
     * Returns null if string is null or empty.
     *
     * @param path Path to string in config file
     * @return String with Bukkit color codes
     */
    @Nullable
    public String getColoredString(@NotNull String path) {
        return StringUtil.color(Strings.isNullOrEmpty(fileConfiguration.getString(path)) ?
                null : fileConfiguration.getString(path));
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
        if (Strings.isNullOrEmpty(path)) return defaultValue;
        return StringUtil.color(Strings.isNullOrEmpty(fileConfiguration.getString(path)) ? defaultValue : fileConfiguration.getString(path));
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
    public final List<String> getColoredList(@NotNull String path) {
        return StringUtil.color(fileConfiguration.getStringList(path));
    }

    /**
     * Returns colored string list retrieved from config.
     *
     * @param path        Path to list of strings in config file
     * @param defaultList Default list to return when there's nothing on path
     * @return List of strings with Bukkit color codes
     */
    @NotNull
    public final List<String> getColoredList(@NotNull String path, @NotNull List<String> defaultList) {
        return Objects.requireNonNull(StringUtil.color(getStringList(path, defaultList)));
    }

    /**
     * Returns a colored message from either a String of a List.
     *
     * @param path Path to list of strings in config file
     * @return Multi-line colored string
     */
    @NotNull
    public String getColoredMessage(@NotNull String path) {
        return getMessage(path).color().toString();
    }

    /**
     * Returns an array from yaml parsed form a String List.
     *
     * @param path Path to list of strings
     * @return Array of strings
     */
    @NotNull
    public String[] getArrayList(@NotNull String path) {
        return fileConfiguration.getStringList(path).toArray(new String[0]);
    }

    /**
     * Returns an array from yaml parsed from String using given delimiter.
     *
     * @param path      Path to look at
     * @param delimiter Delimiter to use
     * @return String array
     */
    @NotNull
    public String[] getArray(@NotNull String path, @NotNull String delimiter) {
        return fileConfiguration.getString(path).split(delimiter);
    }

    /**
     * Returns a character from the yaml.
     * Returns given default when null.
     *
     * @param path         Path to look at
     * @param defaultValue Default to use
     * @return char
     */
    public char getChar(@NotNull String path, char defaultValue) {
        String str = fileConfiguration.getString(path);
        return str != null ? str.toCharArray()[0] : defaultValue;
    }

    /**
     * Returns an array of integers parsed from string.
     *
     * @param path String path to an Array.toString output
     * @return Array of integers.
     */
    @Nullable
    public int[] getInts(@NotNull String path) {
        String str = fileConfiguration.getString(path);

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
    public int[] getInts(@NotNull String path, int[] defaultValue) {
        String str = fileConfiguration.getString(path);

        if (str == null) return defaultValue;

        str = str.replace("[", "").replace("]", "")
                .replace(" ", "");

        if (Strings.isNullOrEmpty(str)) return new int[]{};

        return Arrays.stream(str.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    // --------------------------------- Advanced Load/Save Methods -----------------------------------

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
     * @param path Path to the MessageBuilder
     * @return MessageBuilder object
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
        Location min = LocationUtil.locationFromString(fileConfiguration.getString(path + "." + SubPath.REGION_MIN));
        Location max = LocationUtil.locationFromString(fileConfiguration.getString(path + "." + SubPath.REGION_MAX));

        if (min == null) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not load a region at path " + path + ", minimum location didn't load.");
            return null;
        }

        if (max == null) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not load a region at path " + path + ", maximum location didn't load.");
            return null;
        }

        return new Region(min, max, (boolean) Default.REGION_IGNORE_HEIGHT.getValue());
    }

    /**
     * Saves a region to given path.
     *
     * @param path   String path to save to
     * @param region Region to save
     */
    public void saveRegion(@Nullable String path, @Nullable Region region) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not save region to path " + path + ", path is invalid.");
            return;
        }

        // Check region
        if (region == null) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not save region to path " + path + ", region is null.");
            return;
        }

        ConfigurationSection section = fileConfiguration.createSection(path);

        section.set(SubPath.REGION_MIN.toString(), LocationUtil.locationToString(region.getMin()));
        section.set(SubPath.REGION_MAX.toString(), LocationUtil.locationToString(region.getMax()));

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
            DevportUtils.getInstance().getConsoleOutput().err("Could not load MenuBuilder at path " + path + ", path is invalid.");
            return null;
        }

        MenuBuilder menuBuilder = new MenuBuilder();

        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

        if (section == null) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not load MenuBuilder at path " + path + ", path is invalid.");
            return null;
        }

        menuBuilder.title(section.getString(SubPath.MENU_TITLE.toString(), String.valueOf(Default.MENU_TITLE.getValue())));

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

                if (itemName.equalsIgnoreCase(SubPath.MENU_FILLER.toString()))
                    menuBuilder.filler(item.getItemBuilder());

                // If it contains matrix-char
                if (itemSection.contains(SubPath.MENU_MATRIX_CHAR.toString()))
                    menuBuilder.addMatrixItem(getChar(itemSection.getCurrentPath() + "." + SubPath.MENU_MATRIX_CHAR, (char) Default.MENU_ITEM_MATRIX_CHAR.getValue()), item);
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
            DevportUtils.getInstance().getConsoleOutput().err("Could not load MenuItem at path " + path + ", path is invalid.");
            return null;
        }

        // Load ItemBuilder
        ItemBuilder itemBuilder = getItemBuilder(path);

        String itemName = path.contains(".") ? path.split("\\.")[path.split("\\.").length - 1] : path;

        int slot = fileConfiguration.getInt(path + "." + SubPath.MENU_ITEM_SLOT, -1);

        if (itemBuilder == null) return null;

        MenuItem item = new MenuItem(itemBuilder, itemName, slot);

        item.setCancelClick(fileConfiguration.getBoolean(path + "." + SubPath.MENU_ITEM_CANCEL_CLICK, true));

        return item;
    }

    /**
     * Loads an ItemBuilder from given path.
     *
     * @param path String path to ItemBuilder
     * @return ItemBuilder object
     */
    @NotNull
    public ItemBuilder getItemBuilder(@Nullable String path, @NotNull ItemBuilder defaultValue) {

        // Parse format for the default
        Placeholders format = new Placeholders()
                .add("{message}", "Invalid path");

        // Check path
        if (!Strings.isNullOrEmpty(path))
            // Try to load
            try {
                ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

                if (section == null) {
                    DevportUtils.getInstance().getConsoleOutput().warn("Could not find section for item on path " + path + ", using default.");
                    return defaultValue;
                }

                String type = section.getString(SubPath.ITEM_TYPE.toString());

                if (Strings.isNullOrEmpty(type)) {
                    DevportUtils.getInstance().getConsoleOutput().err("Invalid material on path " + path + ", returning default.");
                    return defaultValue;
                }

                XMaterial xMaterial = XMaterial.matchXMaterial(type.toUpperCase()).orElse(null);

                if (xMaterial == null || xMaterial.parseMaterial() == null) {
                    DevportUtils.getInstance().getConsoleOutput().err("Invalid material on path " + path + ", returning default.");
                    return defaultValue;
                }

                Material material = xMaterial.parseMaterial();

                if (material == null) {
                    DevportUtils.getInstance().getConsoleOutput().err("Invalid material on path " + path + ", returning default.");
                    return defaultValue;
                }

                // Data
                short data = (short) (section.contains(SubPath.ITEM_DATA.toString()) ? section.getInt(SubPath.ITEM_DATA.toString()) : 0);

                ItemBuilder b = new ItemBuilder(material).damage(data);

                // Display name
                if (section.contains(SubPath.ITEM_NAME.toString()))
                    b.displayName(section.getString(SubPath.ITEM_NAME.toString()));

                // Amount
                if (section.contains(SubPath.ITEM_AMOUNT.toString()))
                    b.amount(section.getInt(SubPath.ITEM_AMOUNT.toString()));

                // Glow
                if (section.contains(SubPath.ITEM_GLOW.toString()))
                    b.glow(section.getBoolean(SubPath.ITEM_GLOW.toString()));

                // Lore
                if (section.contains(SubPath.ITEM_LORE.toString()))
                    b.lore(section.getStringList(SubPath.ITEM_LORE.toString()));

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
                            DevportUtils.getInstance().getConsoleOutput().warn("Could not parse enchantment " + dataString);
                            continue;
                        }

                        Enchantment enchantment = xEnchantment.parseEnchantment();

                        if (enchantment == null) {
                            DevportUtils.getInstance().getConsoleOutput().warn("Enchantment " + xEnchantment.name() + " is not valid on this version, skipping it");
                            continue;
                        }

                        b.addEnchant(enchantment, level);
                    }
                }

                // Item Flags
                if (section.contains(SubPath.ITEM_FLAGS.toString()))
                    for (String flagName : section.getStringList(SubPath.ITEM_FLAGS.toString())) {
                        ItemFlag flag = ItemFlag.valueOf(flagName);

                        b.addFlag(flag);
                    }

                // NBT
                if (section.contains(SubPath.ITEM_NBT.toString()))
                    for (String nbtString : section.getStringList(SubPath.ITEM_NBT.toString()))
                        if (nbtString.contains(SubPath.ITEM_NBT_DELIMITER.toString())) {
                            String[] arr = nbtString.split(SubPath.ITEM_NBT_DELIMITER.toString());
                            b.addNBT(arr[0], arr.length > 1 ? arr[1] : "");
                        }

                return b;
            } catch (Exception e) {
                if (DevportUtils.getInstance().getConsoleOutput().isDebug())
                    e.printStackTrace();
                format.add("{message}", e.getMessage());
            }

        DevportUtils.getInstance().getConsoleOutput().warn("Could not load item on path " + path + ", using default.");
        return defaultValue;
    }

    /**
     * Loads an ItemBuilder from given path.
     *
     * @param path String path to ItemBuilder
     * @return ItemBuilder object
     */
    @Nullable
    public ItemBuilder getItemBuilder(@Nullable String path) {

        // Check path
        if (Strings.isNullOrEmpty(path)) return null;

        // Try to load
        try {
            ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

            if (section == null) {
                DevportUtils.getInstance().getConsoleOutput().debug("Invalid section - path " + path + ", returning null.");
                return null;
            }

            String type = section.getString(SubPath.ITEM_TYPE.toString());

            if (Strings.isNullOrEmpty(type)) {
                DevportUtils.getInstance().getConsoleOutput().debug("Invalid material on path " + path + ", returning null.");
                return null;
            }

            XMaterial xMaterial = XMaterial.matchXMaterial(type.toUpperCase()).orElse(null);

            if (xMaterial == null) {
                DevportUtils.getInstance().getConsoleOutput().debug("Invalid material on path " + path + ", returning null.");
                return null;
            }

            Material material = xMaterial.parseMaterial();

            if (material == null) {
                DevportUtils.getInstance().getConsoleOutput().debug("Invalid material on path " + path + ", returning null.");
                return null;
            }

            // Data
            short data = (short) (section.contains(SubPath.ITEM_DATA.toString()) ? section.getInt(SubPath.ITEM_DATA.toString()) : 0);

            ItemBuilder b = new ItemBuilder(material).damage(data);

            // Display name
            if (section.contains(SubPath.ITEM_NAME.toString()))
                b.displayName(section.getString(SubPath.ITEM_NAME.toString()));

            // Amount
            if (section.contains(SubPath.ITEM_AMOUNT.toString()))
                b.amount(section.getInt(SubPath.ITEM_AMOUNT.toString()));

            // Glow
            if (section.contains(SubPath.ITEM_GLOW.toString()))
                b.glow(section.getBoolean(SubPath.ITEM_GLOW.toString()));

            // Lore
            if (section.contains(SubPath.ITEM_LORE.toString()))
                b.lore(section.getStringList(SubPath.ITEM_LORE.toString()));

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
                        DevportUtils.getInstance().getConsoleOutput().warn("Could not parse enchantment " + dataString);
                        continue;
                    }

                    Enchantment enchantment = xEnchantment.parseEnchantment();

                    if (enchantment == null) {
                        DevportUtils.getInstance().getConsoleOutput().warn("Enchantment " + xEnchantment.name() + " is not valid on this version, skipping it");
                        continue;
                    }

                    b.addEnchant(enchantment, level);
                }
            }

            // Item Flags
            if (section.contains(SubPath.ITEM_FLAGS.toString()))
                for (String flagName : section.getStringList(SubPath.ITEM_FLAGS.toString())) {
                    ItemFlag flag = ItemFlag.valueOf(flagName);

                    b.addFlag(flag);
                }

            // NBT
            if (section.contains(SubPath.ITEM_NBT.toString()))
                for (String nbtString : section.getStringList(SubPath.ITEM_NBT.toString()))
                    if (nbtString.contains(SubPath.ITEM_NBT_DELIMITER.toString())) {
                        String[] arr = nbtString.split(SubPath.ITEM_NBT_DELIMITER.toString());
                        b.addNBT(arr[0], arr.length > 1 ? arr[1] : "");
                    }

            return b;
        } catch (Exception e) {
            if (DevportUtils.getInstance().getConsoleOutput().isDebug())
                e.printStackTrace();
        }

        return null;
    }

    /**
     * Set an ItemBuilder object to a yaml file under a given path.
     *
     * @param path    Path to save it under
     * @param builder ItemBuilder to save.
     */
    public void setItemBuilder(String path, ItemBuilder builder) {
        ConfigurationSection section = fileConfiguration.contains(path) ? fileConfiguration.getConfigurationSection(path) : fileConfiguration.createSection(path);

        if (section == null) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not save ItemBuilder to path " + path);
            return;
        }

        section.set(SubPath.ITEM_TYPE.toString(), builder.getMaterial().toString());
        section.set(SubPath.ITEM_DATA.toString(), builder.getDamage());
        section.set(SubPath.ITEM_AMOUNT.toString(), builder.getAmount().toString());
        section.set(SubPath.ITEM_NAME.toString(), builder.getDisplayName().toString());
        section.set(SubPath.ITEM_LORE.toString(), builder.getLore().getMessage());

        List<String> enchants = new ArrayList<>();
        builder.getEnchants().forEach((e, l) -> enchants.add(e.getKey().getKey() + SubPath.ITEM_ENCHANT_DELIMITER + l));
        section.set(SubPath.ITEM_ENCHANTS.toString(), enchants);

        section.set(SubPath.ITEM_FLAGS.toString(), builder.getFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));

        List<String> nbt = new ArrayList<>();
        builder.getNBT().forEach((k, v) -> nbt.add(k + SubPath.ITEM_NBT_DELIMITER + v));
        section.set(SubPath.ITEM_NBT.toString(), nbt);

        section.set(SubPath.ITEM_GLOW.toString(), builder.isGlow());

        if (autoSave)
            save();
    }

    public void setMessage(String path, Message message) {
        if (message.isEmpty())
            fileConfiguration.set(path, "");
        else {
            if (message.getMessage().size() > 1) {
                fileConfiguration.set(path, message.getMessage());
            } else
                fileConfiguration.set(path, message.getMessage().get(0));
        }
    }

    /**
     * Load an Amount from given path.
     *
     * @param path         String path to Amount
     * @param defaultValue Optional Amount, default to return
     * @return Amount object
     */
    public Amount getAmount(String path, Amount defaultValue) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not load Amount at path " + path + ", path is invalid.");
            return null;
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
    public Amount getAmount(String path) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not load Amount at path " + path + ", path is invalid.");
            return null;
        }

        String dataStr = fileConfiguration.getString(path);

        if (Strings.isNullOrEmpty(dataStr))
            return null;

        dataStr = dataStr.replace(" ", "");

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
            return null;
        }
    }

    @NotNull
    public Conditions getConditions(String path) {
        Conditions conditions = new Conditions();

        conditions.operator(fileConfiguration.getBoolean(path + ".operator", false));
        conditions.permissions(getStringList(path + ".permissions", new ArrayList<>()));

        conditions.worlds(getStringList(path + ".worlds", new ArrayList<>()));

        conditions.health(getAmount(path + ".health", new Amount(0)));

        return conditions;
    }

    @NotNull
    public Rewards getRewards(String path) {
        Rewards rewards = new Rewards();

        rewards.broadcast(getMessage(path + ".broadcast", new Message()));
        rewards.inform(getMessage(path + ".inform", new Message()));

        rewards.commands(getStringList(path + ".commands", new ArrayList<>()));

        rewards.money(getAmount(path + ".money", new Amount(0)));
        rewards.tokens(getAmount(path + ".tokens", new Amount(0)));

        ConfigurationSection itemsSection = fileConfiguration.getConfigurationSection(path + ".items");

        if (itemsSection == null) return rewards;

        for (String name : itemsSection.getKeys(false)) {
            ItemBuilder itemBuilder = getItemBuilder(path + ".items." + name);

            if (itemBuilder != null)
                rewards.addItem(itemBuilder);
        }

        return rewards;
    }
}