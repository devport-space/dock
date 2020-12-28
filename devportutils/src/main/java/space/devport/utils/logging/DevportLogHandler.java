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
        LogLevel.fromLevel(record.getLevel()).ifPresent(l -> l.forward(consoleOutput, record.getMessage()));
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
