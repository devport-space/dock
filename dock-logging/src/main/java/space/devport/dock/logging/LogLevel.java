package space.devport.dock.logging;

import lombok.Getter;

import java.util.Arrays;
import java.util.logging.Level;

public enum LogLevel {

    ERR(Level.SEVERE, "&4"),
    WARN(Level.WARNING, "&c"),
    INFO(Level.INFO, "&7"),
    FINE(Level.FINE, "&e", true),
    FINER(Level.FINER, "&e", true),
    FINEST(Level.FINEST, "&e", true);

    private static final LogLevel[] VALUES = values();

    private final Level level;

    @Getter
    private final String prefix;

    @Getter
    private boolean detailed = false;

    LogLevel(Level level, String prefix) {
        this.level = level;
        this.prefix = prefix;
    }

    LogLevel(Level level, String prefix, boolean detailed) {
        this(level, prefix);
        this.detailed = detailed;
    }

    public Level toLevel() {
        return this.level;
    }

    public static LogLevel fromLevel(Level level, LogLevel defaultLevel) {
        return Arrays.stream(VALUES)
                .filter(l -> l.toLevel().equals(level))
                .findAny().orElse(defaultLevel);
    }

    public static LogLevel fromString(String name, LogLevel defaultLevel) {
        return Arrays.stream(VALUES)
                .filter(l -> l.toString().equalsIgnoreCase(name))
                .findAny().orElse(defaultLevel);
    }
}
