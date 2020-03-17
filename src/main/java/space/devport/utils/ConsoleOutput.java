package space.devport.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.messageutil.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle logging and plugin console output.
 *
 * @author Devport Team
 */
@Log
@NoArgsConstructor
public class ConsoleOutput {

    @Setter
    @Getter
    private boolean debug = false;

    @Setter
    @Getter
    @NotNull
    private String prefix = "";

    @Getter
    private final List<CommandSender> listeners = new ArrayList<>();

    @Getter
    @Setter
    private boolean useBukkit = false;

    /**
     * Constructor with a useBukkit parameter.
     *
     * @param useBukkit Whether to use bukkit logger or not.
     */
    public ConsoleOutput(boolean useBukkit) {
        this.useBukkit = useBukkit;
    }

    /**
     * Sends a debug message to console, also to cmdSender if not null.
     *
     * @param msg Message to send, can contain color codes.
     */
    public void debug(String msg) {
        if (debug) {
            if (useBukkit) {
                Bukkit.getLogger().info(StringUtil.color(prefix + "&7DEBUG: " + msg));
                listeners.forEach(c -> c.sendMessage(StringUtil.color("&eDEBUG: " + msg)));
            } else log.info(prefix + " DEBUG: " + msg);
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
        if (useBukkit) {
            listeners.forEach(c -> c.sendMessage(StringUtil.color("&4" + msg)));
            Bukkit.getLogger().severe(StringUtil.color(prefix + "&4ERROR: " + msg));
        } else log.severe(prefix + msg);
    }

    /**
     * Sends error message to console and if not null to reload sender
     *
     * @param msg Message to show
     */
    public void info(String msg) {
        if (useBukkit) {
            listeners.forEach(c -> c.sendMessage(StringUtil.color("&7" + msg)));
            Bukkit.getLogger().info(StringUtil.color(prefix + "&7INFO: " + msg));
        } else log.info(prefix + msg);
    }

    /**
     * Sends a warning message to console.
     * Warnings are sent when an error occurs, but does not affect the functionality - a default is used.
     *
     * @param msg Message to show
     */
    public void warn(String msg) {
        if (useBukkit) {
            listeners.forEach(c -> c.sendMessage(StringUtil.color("&c" + msg)));
            Bukkit.getLogger().warning(StringUtil.color(prefix + "&cWARNING: " + msg));
        } else log.warning(prefix + msg);
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
}