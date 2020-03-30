package space.devport.utils.configutil;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportUtils;
import space.devport.utils.itemutil.ItemBuilder;
import space.devport.utils.menuutil.MenuBuilder;
import space.devport.utils.menuutil.MenuItem;
import space.devport.utils.messageutil.MessageBuilder;
import space.devport.utils.messageutil.ParseFormat;
import space.devport.utils.messageutil.StringUtil;
import space.devport.utils.regionutil.LocationUtil;
import space.devport.utils.regionutil.Region;
import space.devport.utils.utilities.Default;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class to handle Configuration files and custom object loading.
 * Requires DevportUtils to be instanced.
 *
 * @author Devport Team
 */
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
     * Requires a DevportUtils instance.
     *
     * @param plugin Main plugin instance
     * @param path   Path to config file
     */
    public Configuration(@NotNull JavaPlugin plugin, @NotNull String path) {
        this(plugin, new File(path.contains(".yml") ? path : path + ".yml"));
    }

    /**
     * Initializes this class from file and loads yaml.
     * Requires a DevportUtils instance.
     *
     * @param plugin Java plugin instance
     * @param file   File to load from
     */
    public Configuration(@NotNull JavaPlugin plugin, @NotNull File file) {
        this.plugin = plugin;
        this.file = file;
        this.path = file.getPath();

        if (DevportUtils.getInstance() == null) {
            plugin.getLogger().severe("There's no DevportUtils instance, cannot load.");
            return;
        }

        load();
    }

    /**
     * Loads the Yaml configuration from a file.
     */
    public void load() {
        DevportUtils.getInstance().getConsoleOutput().debug("Loading " + path);
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
    }

    /**
     * Reloads the yaml, checks if file exists and loads/creates it again.
     */
    public void reload() {
        DevportUtils.getInstance().getConsoleOutput().debug("Reloading " + path);
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
    public String getColoredString(@NotNull String path, @NotNull String defaultValue) {
        return StringUtil.color(Strings.isNullOrEmpty(fileConfiguration.getString(path)) ?
                defaultValue : fileConfiguration.getString(path));
    }

    /**
     * Returns a list of strings.
     *
     * @param path        Path to list of strings in config file
     * @param defaultList Default value
     * @return List of strings
     */
    @NotNull
    public List<String> getStringList(@NotNull String path, @NotNull List<String> defaultList) {
        return fileConfiguration.getStringList(path) != null ? fileConfiguration.getStringList(path) : defaultList;
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
        return loadMessageBuilder(path).color().toString();
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

    // --------------------------------- Advanced Load/Save Methods -----------------------------------

    /**
     * Loads a message builder either from String or from a list of strings.
     * Returns a default from Default.java when missing.
     *
     * @param path Path to the MessageBuilder
     * @return MessageBuilder object
     */
    @NotNull
    public MessageBuilder loadMessageBuilder(@Nullable String path, @NotNull MessageBuilder... defaultValue) {

        // Check the path
        if (Strings.isNullOrEmpty(path))
            return defaultValue.length > 0 ? defaultValue[0] : (MessageBuilder) Default.MESSAGE_BUILDER.getValue();

        if (fileConfiguration.isString(path)) {
            // Load as a string
            String msg = fileConfiguration.getString(path);
            return new MessageBuilder(msg);
        } else if (fileConfiguration.isList(path)) {
            // Load as a list
            List<String> msg = fileConfiguration.getStringList(path);
            return new MessageBuilder(msg);
        }

        // Couldn't find anything
        return defaultValue.length > 0 ? defaultValue[0] : (MessageBuilder) Default.MESSAGE_BUILDER.getValue();
    }

    /**
     * Loads a region from given path.
     *
     * @param path Path to the Region
     * @return Region object
     */
    @Nullable
    public Region loadRegion(@Nullable String path) {
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
    public MenuBuilder loadMenuBuilder(@Nullable String path) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not load MenuBuilder at path " + path + ", path is invalid.");
            return null;
        }

        MenuBuilder menuBuilder = new MenuBuilder();

        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

        menuBuilder.setTitle(section.getString(SubPath.MENU_TITLE.toString(), String.valueOf(Default.MENU_TITLE.getValue())));
        menuBuilder.setSlots(section.getInt(SubPath.MENU_SLOTS.toString(), (int) Default.MENU_SLOTS.getValue()));

        menuBuilder.setFillAll(section.getBoolean(SubPath.MENU_FILL_ALL.toString(), (boolean) Default.MENU_FILL_ALL.getValue()));

        // Get fill slots
        if (section.contains(SubPath.MENU_FILL_SLOTS.toString())) {
            if (section.isString(SubPath.MENU_FILL_SLOTS.toString())) {

                List<Integer> ints =
                        Arrays.stream(section.getString(SubPath.MENU_FILL_SLOTS.toString())
                                .split(String.valueOf(SubPath.MENU_FILL_SLOTS_DELIMITER.toString())))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());

                menuBuilder.setFillSlots(ints);
            } else
                DevportUtils.getInstance().getConsoleOutput().warn("Could not load fill slots at path " + path + SubPath.MENU_FILL_SLOTS + " is not a string.");
        }

        // Load inventory matrix
        if (section.contains(SubPath.MENU_MATRIX.toString()))
            menuBuilder.setBuildMatrix(getArrayList(path + "." + SubPath.MENU_MATRIX));

        // Load items
        if (section.contains(SubPath.MENU_ITEMS.toString())) {
            for (String itemName : section.getConfigurationSection(SubPath.MENU_ITEMS.toString()).getKeys(false)) {
                ConfigurationSection itemSection = section.getConfigurationSection(SubPath.MENU_ITEMS + "." + itemName);

                MenuItem item = loadMenuItem(path + "." + SubPath.MENU_ITEMS + "." + itemName);

                if (itemName.equalsIgnoreCase(SubPath.MENU_FILLER.toString()))
                    menuBuilder.setFiller(item.getItemBuilder());

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
    public MenuItem loadMenuItem(@Nullable String path) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not load MenuItem at path " + path + ", path is invalid.");
            return null;
        }

        // Load ItemBuilder
        ItemBuilder itemBuilder = loadItemBuilder(path);

        String itemName = path.contains(".") ? path.split("\\.")[path.split("\\.").length - 1] : path;

        int slot = fileConfiguration.getInt(path + "." + SubPath.MENU_ITEM_SLOT, -1);

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
    public ItemBuilder loadItemBuilder(@Nullable String path, @NotNull ItemBuilder... defaultValue) {

        // Parse format for the default
        ParseFormat format = new ParseFormat()
                .fill("{message}", "Invalid path");

        // Check path
        if (!Strings.isNullOrEmpty(path))
            // Try to load
            try {
                ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

                if (section == null) {
                    DevportUtils.getInstance().getConsoleOutput().warn("Could not find section for item on path " + path + ", using default.");
                    return defaultValue.length > 0 ? defaultValue[0] : defaultBuilder(format);
                }

                // Material
                String type = section.getString(SubPath.ITEM_TYPE.toString());

                Material mat;

                try {
                    mat = Strings.isNullOrEmpty(type) ? Material.valueOf(Default.ITEM_TYPE.toString().toUpperCase()) : Material.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    DevportUtils.getInstance().getConsoleOutput().err("Invalid item type in default & on path " + path + ", returning a blank ItemBuilder.");
                    e.printStackTrace();
                    return new ItemBuilder();
                }

                // Data
                short data = (short) (section.contains(SubPath.ITEM_DATA.toString()) ? section.getInt(SubPath.ITEM_DATA.toString()) : 0);

                ItemBuilder b = new ItemBuilder(mat).damage(data);

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

                        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(dataString));

                        if (enchantment == null) {
                            DevportUtils.getInstance().getConsoleOutput().warn("Could not parse enchantment " + dataString);
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
                format.fill("{message}", e.getMessage());
            }

        DevportUtils.getInstance().getConsoleOutput().warn("Could not load item on path " + path + ", using default.");

        return defaultValue.length > 0 ? defaultValue[0] : defaultBuilder(format);
    }

    private ItemBuilder defaultBuilder(ParseFormat format) {
        return new ItemBuilder(Material.valueOf(Default.ITEM_TYPE.toString()))
                .parseWith(format)
                .displayName(Default.ITEM_NAME.toString())
                .addLine(Default.ITEM_LINE.toString());
    }

    public void setItemBuilder(String path, ItemBuilder item) {
        ConfigurationSection section = fileConfiguration.contains(path) ? fileConfiguration.getConfigurationSection(path) : fileConfiguration.createSection(path);

        if (section == null) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not save ItemBuilder to path " + path);
            return;
        }

        section.set(SubPath.ITEM_TYPE.toString(), item.getMaterial().toString());
        section.set(SubPath.ITEM_DATA.toString(), item.getDamage());
        section.set(SubPath.ITEM_AMOUNT.toString(), item.getAmount());
        section.set(SubPath.ITEM_NAME.toString(), item.getDisplayName().toString());
        section.set(SubPath.ITEM_LORE.toString(), item.getLore().getMessage());

        List<String> enchants = new ArrayList<>();
        item.getEnchants().forEach((e, l) -> enchants.add(e.getKey().getKey() + SubPath.ITEM_ENCHANT_DELIMITER + l));
        section.set(SubPath.ITEM_ENCHANTS.toString(), enchants);

        section.set(SubPath.ITEM_FLAGS.toString(), item.getFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));

        List<String> nbt = new ArrayList<>();
        item.getNBT().forEach((k, v) -> nbt.add(k + SubPath.ITEM_NBT_DELIMITER + v));
        section.set(SubPath.ITEM_NBT.toString(), nbt);

        section.set(SubPath.ITEM_GLOW.toString(), item.isGlow());

        if (autoSave)
            save();
    }

    public void setMessageBuilder(String path, MessageBuilder message) {
        if (message.getMessage().isEmpty())
            fileConfiguration.set(path, "");
        else {
            if (message.getMessage().size() > 1) {
                fileConfiguration.set(path, message.getMessage());
            } else
                fileConfiguration.set(path, message.getMessage().get(0));
        }
    }
}