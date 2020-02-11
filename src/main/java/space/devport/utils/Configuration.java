package space.devport.utils;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import space.devport.utils.messageutil.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
     * Deletes file and reloads -> Creates new file and loads default values
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
}