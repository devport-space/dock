package space.devport.utils.logging;

import lombok.Getter;
import space.devport.utils.DevportPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DevportLogger {

    public static String LOGGER_KEY = "space.devport";

    @Getter
    private final ConsoleOutput consoleOutput;

    public DevportLogger(DevportPlugin plugin) {
        this.consoleOutput = new ConsoleOutput(plugin);
    }

    public void setup() {
        Logger logger = Logger.getLogger(LOGGER_KEY);

        logger.setUseParentHandlers(false);

        logger.setLevel(Level.INFO);
        logger.addHandler(new DevportLogHandler(consoleOutput));
    }
}
