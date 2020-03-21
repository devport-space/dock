package space.devport.utils;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.utils.commands.CommandManager;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.text.Message;

public abstract class DevportPlugin extends JavaPlugin {

    @Getter
    private PluginManager pluginManager;

    @Getter
    private Configuration configuration;

    @Getter
    private ConsoleOutput consoleOutput;

    @Getter
    private CommandManager commandManager;

    // Just a shortcut
    public void registerListener(Listener listener) {
        pluginManager.registerEvents(listener, this);
    }

    public abstract void onPluginEnable();

    public abstract void onPluginDisable();

    public abstract void onReload();

    @Override
    public void onEnable() {
        pluginManager = getServer().getPluginManager();

        configuration = new Configuration(this, "config");

        // Setup Console Output
        consoleOutput = new ConsoleOutput();
        consoleOutput.setPrefix(getDescription().getPrefix());
        consoleOutput.setDebug(configuration.getFileConfiguration().getBoolean("debug"));

        // TODO: Print some fancy intro

        onPluginEnable();
    }

    public void reload(CommandSender sender) {
        long start = System.currentTimeMillis();

        consoleOutput.addListener(sender);

        configuration.reload();

        onReload();

        consoleOutput.removeListener(sender);

        new Message("&7Done... reload took &f" + (System.currentTimeMillis() - start) + "&7ms.").send(sender);
    }

    @Override
    public void onDisable() {
        onPluginDisable();
    }

    @Override
    public void reloadConfig() {
        configuration.reload();
    }

    @Override
    public void saveConfig() {
        configuration.save();
    }
}