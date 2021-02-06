package space.devport.dock.logging;

import com.google.common.base.Strings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DockedLogger {

    private final JavaPlugin plugin;

    private DockedAppender appender;

    public DockedLogger(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        this.appender = new DockedAppender(plugin);

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.addAppender(appender);
        rootLogger.setLevel(Level.INFO);
    }

    public void destroy() {
        Logger.getRootLogger().removeAppender(appender);
        this.appender = null;
    }

    public void setLevel(Level level) {
        Logger.getRootLogger().setLevel(level);
    }

    public void setLevel(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            Level level = Level.toLevel(name, null);

            if (level != null)
                setLevel(level);
        }
    }

    public void setPrefix(String prefix) {
        if (appender != null)
            appender.setPrefix(prefix);
    }

    public void addListener(CommandSender listener) {
        if (appender != null)
            appender.addListener(listener);
    }

    public void removeListener(CommandSender listener) {
        if (appender != null)
            appender.removeListener(listener);
    }
}
