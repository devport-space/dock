package space.devport.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.messageutil.StringUtil;

@Slf4j
public class ConsoleOutput {

    @Setter @Getter private boolean debug = false;
    @Setter @Getter @NotNull private String prefix = "";
    @Setter @Getter @Nullable private CommandSender cmdSender;

    /**
     * Sends a debug message to console and if not null to cmdSender
     * @param msg Message to show
     */
    public void debug(String msg) {
        if (debug) {
            log.debug(StringUtil.color(prefix + "&7DEBUG: " + msg));

            if (cmdSender != null)
                cmdSender.sendMessage(StringUtil.color("&eDEBUG: " + msg));
        }
    }

    /**
     * Sets cmdSender and see debug(String msg)
     * @param msg Message to show
     * @param origin CommanderSender to show message to
     */
    public void debug(String msg, CommandSender origin) {
        setCmdSender(origin);
        debug(msg);
    }

    /**
     * Sends error message to console and if not null to reload sender
     * @param msg Message to show
     */
    public void err(String msg) {
        log.error(StringUtil.color(prefix + "&4ERROR: " + msg));

        if (cmdSender != null)
            cmdSender.sendMessage(StringUtil.color("&4" + msg));
    }

    /**
     * Sends error message to console and if not null to reload sender
     * @param msg Message to show
     */
    public void info(String msg) {
        log.info(StringUtil.color(prefix + "&7INFO: " + msg));

        if (cmdSender != null)
            cmdSender.sendMessage(StringUtil.color("&7" + msg));
    }

    /**
     * Sends error message to console and if not null to reload sender
     * @param msg Message to show
     */
    public void warn(String msg) {
        log.warn(StringUtil.color(prefix + "&cWARNING: " + msg));
        if (cmdSender != null)
            cmdSender.sendMessage(StringUtil.color("&c" + msg));
    }

}
