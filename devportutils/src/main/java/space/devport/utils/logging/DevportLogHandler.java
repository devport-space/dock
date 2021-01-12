package space.devport.utils.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DevportLogHandler extends Handler {

    // Delete the root package of the plugin to make the path shorter.
    // It still shows what we need.
    private final static String PACKAGE = DevportLogHandler.class.getPackage().getName()
            .replace(".utils.logging", "");

    private final ConsoleOutput consoleOutput;

    public DevportLogHandler(ConsoleOutput consoleOutput) {
        this.consoleOutput = consoleOutput;
    }

    @Override
    public void publish(LogRecord record) {
        String message;
        if (record.getLevel().intValue() <= DebugLevel.DEBUG.intValue())
            message = String.format("[%s@%s]: %s", record.getSourceMethodName(), record.getSourceClassName().replace(PACKAGE, ""), record.getMessage());
        else message = record.getMessage();

        LogLevel.forward(record.getLevel(), consoleOutput, message);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
