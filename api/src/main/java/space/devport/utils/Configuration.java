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
    private FileConfiguration fileConfiguration;

    private Plugin plugin;

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

    public void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save " + file.getName());
        }
    }

    public void clear() {
        file.delete();

        reload();
    }

    public String getColored(String path) {
        return StringUtil.color(fileConfiguration.getString(path));
    }

    public List<String> getColoredList(String path) {
        return StringUtil.color(fileConfiguration.getStringList(path));
    }

    public String getColoredMessage(String path) {
        StringBuilder strB = new StringBuilder();
        for (String line : getColoredList(path))
            strB.append(line).append("\n");
        return strB.toString();
    }

    public String[] getMatrix(String path) {
        List<String> list = fileConfiguration.getStringList(path);
        StringBuilder str = new StringBuilder();
        for (String str1 : list)
            str.append(str1).append(";");
        return str.toString().split(";");
    }

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

    }

    /**
     * Save current FileConfiguration to a different file by path.
     *
     * @param path Path to save to.
     */
    public void saveToFile(String path) {
    }

    /**
     * Get loaded FileConfiguration.
     *
     * @return Loaded FileConfiguration.
     */
    public FileConfiguration getYaml() {
        return fileConfiguration;
    }
}
