package space.devport.utils.logging;

import space.devport.utils.DevportPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DevportLogger {

    public static String LOGGER_KEY = "space.devport";

    public static void setup(DevportPlugin plugin) {
        Logger logger = Logger.getLogger(LOGGER_KEY);

        logger.setUseParentHandlers(false);

        logger.setLevel(Level.INFO);
        logger.addHandler(new DevportLogHandler(plugin));
    }
}
