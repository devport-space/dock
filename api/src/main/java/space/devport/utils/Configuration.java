package space.devport.utils;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Configuration {

    // Path to the file
    @Getter
    private String path;

    @Getter
    private File file;

    @Getter
    private FileConfiguration fileConfiguration;

    private Plugin plugin;

    /**
     * Initializes this class, creates file and loads yaml for another work
     * @param plugin Main plugin instance
     * @param path Path to config file
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

    public Object fetchDefault(String path, Object o) {
        if (fileConfiguration.contains(path))
            return fileConfiguration.get(path);
        else {
            fileConfiguration.set(path, o);
            save();
            return o;
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
        if(file.delete()) {
            reload();
        }
    }

    /**
     * Returns colored string from config
     * @param path path to string in config file
     * @return String with minecraft understandable color characters
     */
    public String getColored(String path) {
        return StringUtil.color(fileConfiguration.getString(path));
    }

    /**
     * Returns colored string list from config
     * @param path path to list of strings in config file
     * @return List of strings with minecraft understandable color characters
     */
    public List<String> getColoredList(String path) {
        return StringUtil.color(fileConfiguration.getStringList(path));
    }

    /**
     * Returns colored message splitted over lines
     * @param path path to list of strings in config file
     * @return Multi row colored string
     */
    public String getColoredMessage(String path) {
        return String.join("\n", getColoredList(path));
    }

    /**
     * Return array of strings from config file
     * @param path Path to list of strings
     * @return Array of strings
     */
    public String[] getMatrix(String path) {
        return fileConfiguration.getStringList(path).stream().toArray(String[]::new);
    }

    /**
     * Checks if file exists and loads/creates config + loads yaml
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
     * Save current FileConfiguration to a different file.
     *
     * @param file File to save to.
     */
    public void saveToFile(File file) {
        if(file.exists()) {
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
     * Save current FileConfiguration to a different file by path.
     *
     * @param path Path to save to.
     */
    public void saveToFile(String path) {
        File f = new File(plugin.getDataFolder(), path + ".yml");
        if(f.exists()) {
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
