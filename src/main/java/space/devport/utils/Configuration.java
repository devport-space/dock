package space.devport.utils;

import jdk.internal.joptsimple.internal.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;
import space.devport.utils.itemutil.ItemBuilder;
import space.devport.utils.menuutil.MenuBuilder;
import space.devport.utils.menuutil.MenuItem;
import space.devport.utils.messageutil.MessageBuilder;
import space.devport.utils.messageutil.ParseFormat;
import space.devport.utils.messageutil.StringUtil;
import space.devport.utils.regionutil.LocationUtil;
import space.devport.utils.regionutil.Region;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Configuration {

    // TODO Hook to ConsoleOutput
    // TODO Add null checks and annotations?

    @Getter
    private String path;

    @Getter
    private File file;

    @Getter
    private FileConfiguration fileConfiguration;

    private Plugin plugin;

    /**
     * Initializes this class, creates file and loads yaml from path.
     *
     * @param plugin Main plugin instance
     * @param path   Path to config file
     */
    public Configuration(Plugin plugin, String path) {
        this.plugin = plugin;
        this.path = path;

        file = new File(plugin.getDataFolder(), path + ".yml");

        if (!file.exists())
            try {
                plugin.saveResource(path + ".yml", false);
            } catch (Exception e) {
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                    plugin.getLogger().severe("Could not save " + path + ".yml");
                }
            }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Attempts to retrieve data from yaml, saves and returns default to file if null.
     *
     * @param path         Path to object in config file
     * @param defaultValue Default value
     * @return Fetched value or default, if null
     */
    public Object fetchDefault(String path, Object defaultValue) {
        if (fileConfiguration.contains(path))
            return fileConfiguration.get(path);
        else {
            fileConfiguration.set(path, defaultValue);
            save();
            return defaultValue;
        }
    }

    /**
     * Saves configuration file
     */
    public void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save " + file.getName());
        }
    }

    /**
     * Deletes file and reloads, creates new file and loads default values.
     */
    public void clear() {
        if (file.delete()) {
            reload();
        }
    }

    /**
     * Returns colored string retrieved from config.
     *
     * @param path Path to string in config file
     * @return String with Bukkit color codes
     */
    public String getColored(String path) {
        return StringUtil.color(fileConfiguration.getString(path));
    }

    /**
     * Returns colored string list retrieved from config.
     *
     * @param path Path to list of strings in config file
     * @return List of strings with Bukkit color codes
     */
    public List<String> getColoredList(String path) {
        return StringUtil.color(fileConfiguration.getStringList(path));
    }

    /**
     * Returns a colored list of strings joined in a multi-line string.
     *
     * @param path Path to list of strings in config file
     * @return Multi-line colored string
     */
    public String getColoredMessage(String path) {
        return StringUtil.toMultilineString(getColoredList(path));
    }

    /**
     * Returns an array of strings from config file.
     *
     * @param path Path to list of strings
     * @return Array of strings
     */
    public String[] getArray(String path) {
        return fileConfiguration.getStringList(path).toArray(new String[0]);
    }

    // Get a character, if not found, returns default value.
    public char getChar(String path, char defaultValue) {
        String str = fileConfiguration.getString(path);
        return str != null ? str.toCharArray()[0] : defaultValue;
    }

    /**
     * Reloads the yaml, checks if file exists and loads/creates it to yaml again.
     */
    public void reload() {
        plugin.getLogger().info("Reloading " + path + ".yml");
        file = new File(plugin.getDataFolder(), path + ".yml");

        if (!file.exists()) {
            plugin.getLogger().info("Creating new " + path + ".yml");

            try {
                plugin.saveResource(path + ".yml", false);
            } catch (Exception e) {
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                    plugin.getLogger().severe("Could not create " + path + ".yml");
                }
            }
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves current FileConfiguration to a different file.
     *
     * @param file File to save to
     */
    public void saveToFile(File file) {
        if (file.exists()) {
            plugin.getLogger().severe("This file already exists");
            return;
        }

        try {
            this.fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves current FileConfiguration to a different file by path.
     *
     * @param path Path to save to
     */
    public void saveToFile(String path) {
        File f = new File(plugin.getDataFolder(), path + ".yml");
        if (f.exists()) {
            plugin.getLogger().severe("This file already exists");
            return;
        }

        try {
            this.fileConfiguration.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --------------------------------- Advanced Load/Save Methods -----------------------------------

    // Sub paths to different parts of an Object
    public enum SubPath {
        /**
         * REGIONS
         */
        REGION_MIN("min"),
        REGION_MAX("max"),

        /**
         * MENU
         */

        MENU_TITLE("title"),
        MENU_SLOTS("slots"),
        MENU_FILL_ALL("fill-all"),
        MENU_FILL_SLOTS("fill-slots"),
        MENU_MATRIX("matrix"),
        MENU_ITEMS("items"),
        MENU_FILLER("filler"),
        MENU_MATRIX_CHAR("matrix-char"),

        /**
         * MENU ITEM
         */

        MENU_ITEM_CANCEL_CLICK("cancel-click"),
        MENU_ITEM_SLOT("slot"),

        /**
         * ITEM BUILDER
         */

        ITEM_TYPE("type"),
        ITEM_DATA("data"),
        ITEM_NAME("name"),
        ITEM_AMOUNT("amount"),
        ITEM_LORE("lore"),
        ITEM_ENCHANTS("enchants"),
        ITEM_FLAGS("flags"),
        ITEM_NBT("nbt"),
        ITEM_GLOW("glow");

        @Setter
        private String subPath;

        SubPath(String subPath) {
            this.subPath = subPath;
        }

        @Override
        public String toString() {
            return subPath;
        }
    }

    // Some default values for object loading
    public enum DefaultValue {

        /**
         * MENU
         * */
        MENU_TITLE("My Simple GUI"),

        /**
         * ITEM BUILDER
         */
        ITEM_TYPE("STONE"),
        ITEM_NAME("&cCould not load item"),
        ITEM_LINE("&cReason: &7{message}");

        @Setter
        private String defaultValue;

        DefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return defaultValue;
        }
    }

    // Load a message builder either from String or from a StringList
    public MessageBuilder loadMessageBuilder(String path) {

        if (fileConfiguration.isString(path)) {
            // Load as a string
            String msg = fileConfiguration.getString(path);
            return new MessageBuilder(msg);
        } else if (fileConfiguration.isList(path)) {
            // Load as a list
            List<String> msg = fileConfiguration.getStringList(path);
            return new MessageBuilder(msg);
        }

        // Couldn't find anything, return a blank one.
        return new MessageBuilder();
    }

    // Load region from yaml with given paths
    public Region loadRegion(String path) {
        Location min = LocationUtil.locationFromString(fileConfiguration.getString(path + "." + SubPath.REGION_MIN));
        Location max = LocationUtil.locationFromString(fileConfiguration.getString(path + "." + SubPath.REGION_MAX));

        if (min == null) {
            DevportUtils.inst.getConsoleOutput().err("Could not load a region at path " + path + ", minimum location didn't load.");
            return null;
        }

        if (max == null) {
            DevportUtils.inst.getConsoleOutput().err("Could not load a region at path " + path + ", maximum location didn't load.");
            return null;
        }

        return new Region(min, max, false);
    }

    public void saveRegion(String path, Region region) {
        ConfigurationSection section = fileConfiguration.createSection(path);

        section.set(SubPath.REGION_MIN.toString(), LocationUtil.locationToString(region.getMin()));
        section.set(SubPath.REGION_MAX.toString(), LocationUtil.locationToString(region.getMax()));

        save();
    }

    // Load a whole Menu from yaml on a given path
    public MenuBuilder loadMenuBuilder(String path) {
        String name = path.contains(".") ? path.split("\\.")[path.split("\\.").length - 1] : path;

        MenuBuilder menuBuilder = new MenuBuilder(name);

        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

        menuBuilder.setTitle(section.getString(SubPath.MENU_TITLE.toString(), DefaultValue.MENU_TITLE.toString()));
        menuBuilder.setSlots(section.getInt(SubPath.MENU_SLOTS.toString(), 9));

        menuBuilder.setFillAll(section.getBoolean(SubPath.MENU_FILL_ALL.toString(), false));

        // Get fill slots
        if (section.contains(SubPath.MENU_FILL_SLOTS.toString())) {
            List<Integer> ints = Arrays.stream(section.getString(SubPath.MENU_FILL_SLOTS.toString()).split(";")).map(Integer::parseInt).collect(Collectors.toList());
            menuBuilder.setFillSlots(ints);
        }

        // Load inventory matrix
        if (section.contains(SubPath.MENU_MATRIX.toString()))
            menuBuilder.setBuildMatrix(getArray(path + "." + SubPath.MENU_MATRIX));

        // Load items
        if (section.contains(SubPath.MENU_ITEMS.toString())) {
            for (String itemName : section.getConfigurationSection(SubPath.MENU_ITEMS.toString()).getKeys(false)) {
                ConfigurationSection itemSection = section.getConfigurationSection(SubPath.MENU_ITEMS + "." + itemName);

                MenuItem item = loadMenuItem(path + "." + SubPath.MENU_ITEMS + "." + itemName);

                if (itemName.equalsIgnoreCase(SubPath.MENU_FILLER.toString()))
                    menuBuilder.setFiller(item.getItemBuilder());

                // If it contains matrix-char
                if (itemSection.contains(SubPath.MENU_MATRIX_CHAR.toString()))
                    menuBuilder.addMatrixItem(getChar(itemSection.getCurrentPath() + "." + SubPath.MENU_MATRIX_CHAR, ' '), item);
                else
                    menuBuilder.setItem(item);
            }
        }

        return menuBuilder;
    }

    public MenuItem loadMenuItem(String path) {
        ItemBuilder itemBuilder = loadItemBuilder(path);

        String itemName = path.contains(".") ? path.split("\\.")[path.split("\\.").length - 1] : path;

        int slot = fileConfiguration.getInt(path + "." + SubPath.MENU_ITEM_SLOT, -1);

        MenuItem item = new MenuItem(itemBuilder, itemName, slot);

        item.setCancelClick(fileConfiguration.getBoolean(path + "." + SubPath.MENU_ITEM_CANCEL_CLICK, true));

        return item;
    }

    // Load an ItemBuilder from given path, with given sub-paths for separate parts.
    public ItemBuilder loadItemBuilder(String path) {
        try {
            ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

            String type = section.getString(SubPath.ITEM_TYPE.toString());

            Material mat;

            try {
                mat = Strings.isNullOrEmpty(type) ? Material.valueOf(DefaultValue.ITEM_TYPE.toString().toUpperCase()) : Material.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                DevportUtils.inst.getConsoleOutput().err("Invalid item type in default & on path " + path);
                e.printStackTrace();
                return null;
            }

            short data = (short) (section.contains(SubPath.ITEM_DATA.toString()) ? section.getInt(SubPath.ITEM_DATA.toString()) : 0);

            ItemBuilder b = new ItemBuilder(mat).damage(data);

            if (section.contains(SubPath.ITEM_NAME.toString()))
                b.displayName(section.getString(SubPath.ITEM_NAME.toString()));

            if (section.contains(SubPath.ITEM_AMOUNT.toString()))
                b.amount(section.getInt(SubPath.ITEM_AMOUNT.toString()));

            if (section.contains(SubPath.ITEM_GLOW.toString()))
                b.glow(section.getBoolean(SubPath.ITEM_GLOW.toString()));

            if (section.contains(SubPath.ITEM_LORE.toString()))
                b.lore(section.getStringList(SubPath.ITEM_LORE.toString()));

            if (section.contains(SubPath.ITEM_ENCHANTS.toString())) {
                List<String> dataList = section.getStringList(SubPath.ITEM_ENCHANTS.toString());

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

            if (section.contains(SubPath.ITEM_FLAGS.toString()))
                for (String flagName : section.getStringList(SubPath.ITEM_FLAGS.toString())) {
                    ItemFlag flag = ItemFlag.valueOf(flagName);

                    b.addFlag(flag);
                }

            if (section.contains(SubPath.ITEM_NBT.toString()))
                for (String nbtString : section.getStringList(SubPath.ITEM_NBT.toString()))
                    b.addNBT(nbtString.split(";")[0], nbtString.split(";")[1]);

            return b;
        } catch (NullPointerException | IllegalArgumentException e) {
            if (DevportUtils.inst.getConsoleOutput().isDebug())
                e.printStackTrace();

            DevportUtils.inst.getConsoleOutput().warn("Could not load item on path " + path + ", using default.");

            ParseFormat format = new ParseFormat().fill("{message}", e.getMessage());
            return new ItemBuilder(Material.valueOf(DefaultValue.ITEM_TYPE.toString())).parseFormat(format).displayName(DefaultValue.ITEM_NAME.toString()).addLine(DefaultValue.ITEM_LINE.toString());
        }
    }
}