package space.devport.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.text.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle logging and plugin console output.
 *
 * @author Devport Team
 */
public class ConsoleOutput {

    private static ConsoleOutput instance;

    @NotNull
    public static ConsoleOutput getInstance() {
        return instance == null ? new ConsoleOutput() : instance;
    }

    @NotNull
    public static ConsoleOutput getInstance(JavaPlugin plugin) {
        if (instance != null) {
            instance.setPlugin(plugin);
            return instance;
        } else return new ConsoleOutput(plugin);
    }

    @Getter
    @Setter
    private boolean debug = false;

    @Getter
    @Setter
    private boolean colors = false;

    @Setter
    private ConsoleCommandSender console = null;

    @Getter
    private String prefix = "";

    @Getter
    private final List<CommandSender> listeners = new ArrayList<>();

    private ConsoleOutput() {
        instance = this;
    }

    private ConsoleOutput(JavaPlugin plugin) {
        this();
        setPlugin(plugin);
    }

    public void setPlugin(JavaPlugin plugin) {
        this.console = plugin.getServer().getConsoleSender();
        this.colors = true;
    }

    public void colored(String msg) {
        if (!colors || console == null) return;

        console.sendMessage(StringUtil.color(msg));
    }

    /**
     * Sends a debug message to console, also to cmdSender if not null.
     *
     * @param msg Message to send, can contain color codes.
     */
    public void debug(String msg) {
        if (debug) {
            final String finalMsg = prefix + "&eDEBUG: " + msg;

            if (colors) colored(finalMsg);
            else Bukkit.getLogger().info(StringUtil.stripColor(finalMsg));

            toListeners(finalMsg);
        }
    }

    /**
     * Sends a message to debug and command sender.
     *
     * @param msg    Message to show
     * @param origin CommanderSender who caused the message
     */
    public void debug(String msg, CommandSender origin) {
        if (debug) {
            addListener(origin);
            debug(msg);
            removeListener(origin);
        }
    }

    /**
     * Sends error message to console and if not null to reload sender.
     *
     * @param msg Message to show
     */
    public void err(String msg) {
        final String finalMsg = prefix + "&4ERROR: " + msg;

        if (colors) colored(finalMsg);
        else Bukkit.getLogger().severe(StringUtil.stripColor(finalMsg));

        toListeners(finalMsg);
    }

    /**
     * Sends error message to console and if not null to reload sender
     *
     * @param msg Message to show
     */
    public void info(String msg) {
        final String finalMsg = prefix + "&7INFO: " + msg;

        if (colors) colored(finalMsg);
        else Bukkit.getLogger().info(StringUtil.stripColor(finalMsg));

        toListeners(finalMsg);
    }

    /**
     * Sends a warning message to console.
     * Warnings are sent when an error occurs, but does not affect the functionality - a default is used.
     *
     * @param msg Message to show
     */
    public void warn(String msg) {
        final String finalMsg = prefix + "&cWARN: " + msg;

        if (colors) colored(finalMsg);
        else Bukkit.getLogger().warning(StringUtil.stripColor(finalMsg));

        toListeners(finalMsg);
    }

    /**
     * Add a listener.
     * CommandSender will receive console output.
     *
     * @param listener CommandSender to add
     */
    public void addListener(@NotNull CommandSender listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener.
     * CommandSender will not receive console output anymore.
     *
     * @param listener CommandSender to remove
     */
    public void removeListener(@NotNull CommandSender listener) {
        listeners.remove(listener);
    }

    public void toListeners(String message) {
        final String finalMessage = StringUtil.color(message);
        if (finalMessage != null)
            new ArrayList<>(listeners).forEach(c -> c.sendMessage(finalMessage));
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
    }
}