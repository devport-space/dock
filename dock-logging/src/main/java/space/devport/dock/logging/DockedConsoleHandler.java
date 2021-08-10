package space.devport.dock.logging;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.common.Strings;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DockedConsoleHandler extends Handler {

    private final static String NORMAL_PATTERN = "%s&r%s%s";

    private final static String DETAILED_PATTERN = "%s&r[%s.%s] %s%s";

    private final ConsoleCommandSender console;

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

        if (record.getLevel().intValue() < getLevel().intValue()) {
            return;
        }

        LogLevel level = LogLevel.fromLevel(record.getLevel(), LogLevel.INFO);

        String message;
        if (level.isDetailed()) {
            message = String.format(DETAILED_PATTERN,
                    prefix,
                    record.getSourceClassName(),
                    record.getSourceMethodName(),
                    level.getPrefix(),
                    record.getMessage());
        } else {
            message = String.format(NORMAL_PATTERN,
                    prefix,
                    level.getPrefix(),
                    record.getMessage());
        }

        sendRaw(record, message);
    }

    private void sendRaw(LogRecord record, String msg) {

        if (Strings.isNullOrEmpty(msg))
            return;

        String message = LoggerUtil.colorBukkit(msg);

        toListeners(message);

        if (console == null)
            Bukkit.getLogger().log(record.getLevel(), LoggerUtil.stripColor(msg));
        else
            console.sendMessage(message);
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
