package space.devport.utils;

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

    // Paths to region data
    @Getter
    @Setter
    private String[] regionPaths = new String[]{"min", "max"};

    // Load region from yaml with given paths
    public Region loadRegion(String path, String[] paths) {
        Location min = LocationUtil.locationFromString(fileConfiguration.getString(path + "." + paths[0]));
        Location max = LocationUtil.locationFromString(fileConfiguration.getString(path + "." + paths[1]));

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

    // Load region from yaml
    public Region loadRegion(String path) {
        return loadRegion(path, regionPaths);
    }

    public void saveRegion(String path, Region region) {
        ConfigurationSection section = fileConfiguration.createSection(path);

        section.set(regionPaths[0], LocationUtil.locationToString(region.getMin()));
        section.set(regionPaths[1], LocationUtil.locationToString(region.getMax()));

        save();
    }

    // Paths to menu data
    @Getter
    @Setter
    private String[] menuPaths = new String[]{"title", "slots", "fill-all", "fill-slots", "matrix", "items", "filler", "slot", "cancel-click", "matrix-char"};

    // Load a whole Menu from yaml on a given path
    // paths = "title", "slots", "fill-all", "fill-slots", "matrix", "items", "filler", "slot", "cancel-click", "matrix-char"
    public MenuBuilder loadMenuBuilder(String path, String[] paths) {
        String name = path.contains(".") ? path.split("\\.")[path.split("\\.").length - 1] : path;

        MenuBuilder menuBuilder = new MenuBuilder(name);

        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

        menuBuilder.setTitle(section.getString(paths[0], "My Simple GUI"));
        menuBuilder.setSlots(section.getInt(paths[1], 9));

        menuBuilder.setFillAll(section.getBoolean(paths[2], false));

        // Get fill slots
        if (section.contains(paths[3])) {
            List<Integer> ints = Arrays.stream(section.getString(paths[3]).split(";")).map(Integer::parseInt).collect(Collectors.toList());
            menuBuilder.setFillSlots(ints);
        }

        // Load inventory matrix
        if (section.contains(paths[4]))
            menuBuilder.setBuildMatrix(getArray(path + "." + paths[4]));

        // Load items
        if (section.contains(paths[5])) {
            for (String itemName : section.getConfigurationSection(paths[5]).getKeys(false)) {
                ConfigurationSection itemSection = section.getConfigurationSection(paths[5] + "." + itemName);

                MenuItem item = loadMenuItem(path + "." + paths[5] + "." + itemName, new String[]{paths[7], paths[8]});

                if (itemName.equalsIgnoreCase(paths[6]))
                    menuBuilder.setFiller(item.getItemBuilder());

                // If it contains matrix-char
                if (itemSection.contains(paths[9]))
                    menuBuilder.addMatrixItem(itemSection.getString(paths[9]).charAt(0), item);
                else
                    menuBuilder.setItem(item);
            }
        }

        return menuBuilder;
    }

    public MenuBuilder loadMenuBuilder(String path) {
        return loadMenuBuilder(path, menuPaths);
    }

    // Paths to menu item data
    @Getter
    @Setter
    private String[] menuItemPaths = new String[]{"slot", "cancel-click"};

    public MenuItem loadMenuItem(String path, String[] paths) {
        ItemBuilder itemBuilder = loadItemBuilder(path);

        String itemName = path.contains(".") ? path.split("\\.")[path.split("\\.").length - 1] : path;

        int slot = fileConfiguration.getInt(path + "." + paths[0], -1);

        MenuItem item = new MenuItem(itemBuilder, itemName, slot);

        item.setCancelClick(fileConfiguration.getBoolean(path + "." + paths[1], true));

        return item;
    }

    public MenuItem loadMenuItem(String path) {
        return loadMenuItem(path, menuItemPaths);
    }

    // ItemBuilder data paths
    @Getter
    @Setter
    private String[] itemPaths = new String[]{"type", "damage", "name", "amount", "glow", "lore", "enchants", "flags", "nbt"};

    // Load an ItemBuilder from given path, with given sub-paths for separate parts.
    public ItemBuilder loadItemBuilder(String path, String[] paths) {
        try {
            ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

            String type = section.getString(paths[0]);

            if (type == null) {
                type = "STONE";
                DevportUtils.inst.getConsoleOutput().warn("Missing Item material on " + path + "." + paths[0] + ", using stone.");
            }

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

    public ItemBuilder loadItemBuilder(String path) {
        return loadItemBuilder(path, itemPaths);
    }
}