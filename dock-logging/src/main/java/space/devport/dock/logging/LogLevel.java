package space.devport.dock.logging;

import lombok.Getter;
import org.apache.log4j.Level;

import java.util.Arrays;

public enum LogLevel {

    INFO(Level.INFO, "&7"),
    WARN(Level.WARN, "&c"),
    ERR(Level.ERROR, "&4"),
    DEBUG(Level.DEBUG, "&e"),
    TRACE(Level.TRACE, "&e");

    @Getter
    private final Level level;

    @Getter
    private final String prefix;

    LogLevel(Level level, String prefix) {
        this.level = level;
        this.prefix = prefix;
    }

    public static LogLevel fromLevel(Level level, LogLevel defaultLevel) {
        return Arrays.stream(values())
                .filter(l -> l.getLevel().equals(level))
                .findAny().orElse(defaultLevel);
    }
}
