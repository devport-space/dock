package space.devport.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.messageutil.StringUtil;

@Log
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
    private CommandSender cmdSender;

    // Whether or not are we using Bukkit Logging
    @Getter
    @Setter
    private boolean bukkit = false;

    public ConsoleOutput() {
    }

    public ConsoleOutput(boolean bukkit) {
        this.bukkit = bukkit;
    }

    /**
     * Sends a debug message to console and if not null to cmdSender
     *
     * @param msg Message to show
     */
    public void debug(String msg) {
        if (debug) {
            if (bukkit)
                Bukkit.getLogger().info(StringUtil.color(prefix + "&7DEBUG: " + msg));
            else log.info(prefix + " DEBUG: " + msg);

            if (cmdSender != null)
                cmdSender.sendMessage(StringUtil.color("&eDEBUG: " + msg));
        }
    }

    /**
     * Sets cmdSender and see debug(String msg)
     *
     * @param msg    Message to show
     * @param origin CommanderSender to show message to
     */
    public void debug(String msg, CommandSender origin) {
        if (debug) {
            setCmdSender(origin);
            debug(msg);
        }
    }

    /**
     * Sends error message to console and if not null to reload sender
     *
     * @param msg Message to show
     */
    public void err(String msg) {

        if (bukkit)
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
        if (bukkit)
            Bukkit.getLogger().info(StringUtil.color(prefix + "&7INFO: " + msg));
        else log.info(prefix + msg);

        if (cmdSender != null)
            cmdSender.sendMessage(StringUtil.color("&7" + msg));
    }

    /**
     * Sends error message to console and if not null to reload sender
     *
     * @param msg Message to show
     */
    public void warn(String msg) {
        if (bukkit)
            Bukkit.getLogger().warning(StringUtil.color(prefix + "&cWARNING: " + msg));
        else log.warning(prefix + msg);

        if (cmdSender != null)
            cmdSender.sendMessage(StringUtil.color("&c" + msg));
    }
}