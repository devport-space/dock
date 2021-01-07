package space.devport.utils.logging;

import com.google.common.base.Strings;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import space.devport.utils.DevportPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DevportLogger {

    @Getter
    private final String loggerKey;

    private final Logger parentLogger;

    @Getter
    private final ConsoleOutput consoleOutput;

    public DevportLogger(DevportPlugin plugin) {
        this.consoleOutput = new ConsoleOutput(plugin);

        this.loggerKey = plugin.getClass().getName();
        this.parentLogger = Logger.getLogger(loggerKey);
    }

    public void setup() {
        parentLogger.setUseParentHandlers(false);

        setLevel(Level.INFO);
        parentLogger.addHandler(new DevportLogHandler(consoleOutput));
    }

    public void setLevel(Level level) {
        parentLogger.setLevel(level);
    }

    public void setLevel(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            Level level = Level.parse(name);
            setLevel(level);
        }
    }

    public void addListener(CommandSender listener) {
        consoleOutput.addListener(listener);
    }

    public void removeListener(CommandSender listener) {
        consoleOutput.removeListener(listener);
    }
}
