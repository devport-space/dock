package space.devport.dock.logging;

import com.google.common.base.Strings;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DockedLogger {

    private final JavaPlugin plugin;

    private DockedConsoleHandler handler;

    private Logger parentLogger;

    public DockedLogger(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup(String loggerKey) {
        this.handler = new DockedConsoleHandler(plugin);
        this.parentLogger = Logger.getLogger(loggerKey);

        setLevel(LogLevel.INFO);
        parentLogger.setUseParentHandlers(false);
        parentLogger.addHandler(handler);
    }

    public void destroy() {
        parentLogger.removeHandler(handler);
        parentLogger.setLevel(Level.INFO);

        this.handler = null;
    }

    public void setLevel(LogLevel level) {
        parentLogger.setLevel(level.toLevel());
    }

    public void setLevel(String name) {
        if (Strings.isNullOrEmpty(name))
            return;

        LogLevel logLevel = LogLevel.fromString(name, null);

        if (logLevel != null)
            setLevel(logLevel);
    }

    public void setPrefix(String prefix) {
        if (handler != null)
            handler.setPrefix(prefix);
    }

    public void addListener(CommandSender listener) {
        if (handler != null)
            handler.addListener(listener);
    }

    public void removeListener(CommandSender listener) {
        if (handler != null)
            handler.removeListener(listener);
    }
}
