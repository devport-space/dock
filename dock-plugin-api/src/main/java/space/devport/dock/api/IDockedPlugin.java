package space.devport.dock.api;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.dock.UsageFlag;
import space.devport.dock.logging.DockedLogger;
import space.devport.dock.text.placeholders.Placeholders;

import java.io.File;
import java.util.List;

public interface IDockedPlugin {

    DockedLogger getDockedLogger();

    void reload();

    void reload(CommandSender sender);

    void registerManager(IDockedManager dockedManager);

    boolean isRegistered(Class<? extends IDockedManager> clazz);

    <T extends IDockedManager> T getManager(Class<T> clazz);

    void registerListener(Listener listener);

    void addListener(IDockedListener listener);

    boolean removeListener(Class<? extends IDockedListener> clazz);

    void clearListeners();

    void registerListeners();

    void unregisterListeners();

    // MainCommand registerMainCommand(MainCommand mainCommand);

    // BuildableMainCommand buildMainCommand(String name);

    // BuildableSubCommand buildSubCommand(String name);

    boolean use(UsageFlag flag);

    void reloadConfig();

    void saveConfig();

    FileConfiguration getConfig();

    List<String> getDependencies();

    ChatColor getColor();

    ChatColor getPluginColor();

    PluginManager getPluginManager();

    Placeholders obtainPlaceholders();

    Server getServer();

    PluginDescriptionFile getDescription();

    File getDataFolder();

    JavaPlugin getPlugin();
}
