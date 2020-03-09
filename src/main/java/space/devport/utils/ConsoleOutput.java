package space.devport.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.messageutil.StringUtil;

/**
 * Class to handle logging & plugin console output.
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

    @Setter
    @Getter
    @Nullable
    // Will be replaced with a list of player who have debug turned on.
    @Deprecated
    private CommandSender cmdSender;

    // Whether or not are we using Bukkit Logging
    @Getter
    @Setter
    private boolean useBukkit = false;

    /**
     * Constructor with a useBukkit parameter.
     *
     * @param useBukkit Whether to use bukkit logger or not.
     * */
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
            if (useBukkit)
                Bukkit.getLogger().info(StringUtil.color(prefix + "&7DEBUG: " + msg));
            else log.info(prefix + " DEBUG: " + msg);

            if (cmdSender != null)
                cmdSender.sendMessage(StringUtil.color("&eDEBUG: " + msg));
        }
    }

    /**
     * Sends a message to debug & command sender.
     *
     * @param msg    Message to show
     * @param origin CommanderSender who caused the message
     */
    public void debug(String msg, CommandSender origin) {
        if (debug) {
            setCmdSender(origin);
            debug(msg);
            setCmdSender(null);
        }
    }

    /**
     * Sends error message to console and if not null to reload sender.
     *
     * @param msg Message to show
     */
    public void err(String msg) {
        if (useBukkit)
            Bukkit.getLogger().severe(StringUtil.color(prefix + "&4ERROR: " + msg));
        else log.severe(prefix + msg);

        if (cmdSender != null)
            cmdSender.sendMessage(StringUtil.color("&4" + msg));
    }

    /**
     * Sends error message to console and if not null to reload sender
     *
     * @param msg Message to show
     */
    public void info(String msg) {
        if (useBukkit)
            Bukkit.getLogger().info(StringUtil.color(prefix + "&7INFO: " + msg));
        else log.info(prefix + msg);

        if (cmdSender != null)
            cmdSender.sendMessage(StringUtil.color("&7" + msg));
    }

    /**
     * Sends a warning message to console.
     * Warnings are sent when an error occurs, but does not affect the functionality - a default is used.
     *
     * @param msg Message to show
     */
    public void warn(String msg) {
        if (useBukkit)
            Bukkit.getLogger().warning(StringUtil.color(prefix + "&cWARNING: " + msg));
        else log.warning(prefix + msg);

        if (cmdSender != null)
            cmdSender.sendMessage(StringUtil.color("&c" + msg));
    }
}