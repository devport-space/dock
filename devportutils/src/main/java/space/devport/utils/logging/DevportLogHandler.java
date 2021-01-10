package space.devport.utils.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DevportLogHandler extends Handler {

    private final ConsoleOutput consoleOutput;

    public DevportLogHandler(ConsoleOutput consoleOutput) {
        this.consoleOutput = consoleOutput;
    }

    @Override
    public void publish(LogRecord record) {
        String message = record.getLevel().intValue() <= DebugLevel.DEBUG.intValue() ? String.format("%s@%s", record.getSourceMethodName(), record.getSourceClassName()) : record.getMessage();
        LogLevel.forward(record.getLevel(), consoleOutput, message);
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
