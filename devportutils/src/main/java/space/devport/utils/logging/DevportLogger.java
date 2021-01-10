package space.devport.utils.logging;

import com.google.common.base.Strings;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import space.devport.utils.factory.IFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DevportLogger implements IFactory {

    @Getter
    private final String loggerKey;

    private final Logger parentLogger;

    private ConsoleOutput consoleOutput;

    private DevportLogHandler devportLogHandler;

    public DevportLogger(String loggerKey) {
        this.loggerKey = loggerKey;
        this.parentLogger = Logger.getLogger(loggerKey);
    }

    public void setup(ConsoleOutput consoleOutput) {
        this.consoleOutput = consoleOutput;

        parentLogger.setUseParentHandlers(false);

        setLevel(Level.INFO);
        this.devportLogHandler = new DevportLogHandler(consoleOutput);
        parentLogger.addHandler(devportLogHandler);
    }

    @Override
    public void destroy() {
        parentLogger.removeHandler(devportLogHandler);
        parentLogger.setUseParentHandlers(true);

        this.consoleOutput = null;
        this.devportLogHandler = null;
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

    public void setPrefix(String prefix) {
        if (consoleOutput != null)
            consoleOutput.setPrefix(prefix);
    }

    public void addListener(CommandSender listener) {
        if (consoleOutput != null)
            consoleOutput.addListener(listener);
    }

    public void removeListener(CommandSender listener) {
        if (consoleOutput != null)
            consoleOutput.removeListener(listener);
    }
}
