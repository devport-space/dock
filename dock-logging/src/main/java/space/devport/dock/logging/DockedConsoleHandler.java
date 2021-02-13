package space.devport.dock.logging;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DockedConsoleHandler extends Handler {

    private final static String NORMAL_PATTERN = "[%s - %s] %s%s";

    private final static String DETAILED_PATTERN = "[%s - %s] [%s.%s] (%d) %s%s";

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Setter
    private ConsoleCommandSender console;

    @Getter
    private String prefix = "";

    @Getter
    private final Set<CommandSender> listeners = new HashSet<>();

    @Getter
    private final JavaPlugin plugin;

    protected DockedConsoleHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.console = plugin.getServer().getConsoleSender();
    }

    @Override
    public void publish(LogRecord record) {
        LogLevel level = LogLevel.fromLevel(record.getLevel(), LogLevel.INFO);

        String message;
        if (level.isDetailed()) {
            message = String.format(DETAILED_PATTERN,
                    LocalTime.from(Instant.ofEpochMilli(record.getMillis())).format(dateFormatter),
                    record.getLevel().toString(),
                    record.getSourceClassName(),
                    record.getSourceMethodName(),
                    record.getThreadID(),
                    level.getPrefix(),
                    record.getMessage());
        } else {
            message = String.format(NORMAL_PATTERN,
                    LocalTime.from(Instant.ofEpochMilli(record.getMillis())).format(dateFormatter),
                    record.getLevel().toString(),
                    level.getPrefix(),
                    record.getMessage());
        }

        sendRaw(message);
    }

    private void sendRaw(String msg) {
        if (console == null) {
            Bukkit.getLogger().info(LoggerUtil.stripColor(msg));
            toListeners(LoggerUtil.color(msg));
            return;
        }

        String message = LoggerUtil.color(msg);

        if (message != null) {
            console.sendMessage(message);
            toListeners(message);
        }
    }

    @Override
    public void flush() {
        // NO-OP
    }

    @Override
    public void close() throws SecurityException {
        // NO-OP
    }

    /**
     * Add a listener.
     * CommandSender will receive console output.
     *
     * @param listener CommandSender to add
     */
    public void addListener(@NotNull CommandSender listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener.
     * CommandSender will not receive console output anymore.
     *
     * @param listener CommandSender to remove
     */
    public void removeListener(@NotNull CommandSender listener) {
        listeners.remove(listener);
    }

    public void toListeners(String message) {
        if (message != null)
            listeners.forEach(c -> c.sendMessage(message));
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
    }
}
