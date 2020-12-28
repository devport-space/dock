package space.devport.utils.logging;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;

public enum LogLevel {

    INFO(Level.INFO, ConsoleOutput::info),
    WARN(Level.WARNING, ConsoleOutput::warn),
    ERR(Level.SEVERE, ConsoleOutput::err),
    DEBUG(DebugLevel.DEBUG, ConsoleOutput::debug);

    @Getter
    private final Level level;

    private final LogForwarder forwarder;

    LogLevel(Level level, LogForwarder forwarder) {
        this.level = level;
        this.forwarder = forwarder;
    }

    public static Optional<LogLevel> fromLevel(Level level) {
        return Arrays.stream(values())
                .filter(l -> l.getLevel().equals(level))
                .findAny();
    }

    public void forward(ConsoleOutput consoleOutput, String message) {
        forwarder.forward(consoleOutput, message);
    }
}
