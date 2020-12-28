package space.devport.utils.logging;

import space.devport.utils.DevportPlugin;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DevportLogHandler extends Handler {

    private final DevportPlugin plugin;

    public DevportLogHandler(DevportPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void publish(LogRecord record) {
        LogLevel.fromLevel(record.getLevel()).ifPresent(l -> l.forward(plugin.getConsoleOutput(), record.getMessage()));
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
