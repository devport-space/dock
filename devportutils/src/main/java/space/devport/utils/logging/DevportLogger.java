package space.devport.utils.logging;

import space.devport.utils.DevportPlugin;

import java.util.logging.*;

public class DevportLogger {

    public static void setup(DevportPlugin plugin) {
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        Logger rootLogger = LogManager.getLogManager().getLogger("");
        for (Handler handler : rootLogger.getHandlers())
            if (handler instanceof ConsoleHandler)
                rootLogger.removeHandler(handler);

        logger.setLevel(Level.INFO);
        logger.addHandler(new DevportLogHandler(plugin));
    }
}
