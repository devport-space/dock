package space.devport.dock.logging;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class DockedAppender extends AppenderSkeleton {

    @Setter
    private ConsoleCommandSender console;

    @Getter
    private String prefix = "";

    @Getter
    private final Set<CommandSender> listeners = new HashSet<>();

    public DockedAppender(JavaPlugin plugin) {
        super();
        setPlugin(plugin);
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {

        LogLevel level = LogLevel.fromLevel(loggingEvent.getLevel(), LogLevel.INFO);

        String message;

        // Add additional info when DEBUG or TRACE is provided.
        // Note: This slows down the logging extremely.
        if (loggingEvent.getLevel() == Level.DEBUG || loggingEvent.getLevel() == Level.TRACE)
            message = String.format("%s[(%s) %s@%s:%s]: %s",
                    level.getPrefix(),
                    loggingEvent.getThreadName(),
                    loggingEvent.getLocationInformation().getMethodName(),
                    loggingEvent.getLocationInformation().getClassName(),
                    loggingEvent.getLocationInformation().getLineNumber(),
                    loggingEvent.getRenderedMessage());
        else
            message = level.getPrefix() + loggingEvent.getRenderedMessage();

        sendRaw(message);
    }

    private void sendRaw(String msg) {
        if (console == null) {
            Bukkit.getLogger().info(LoggerUtil.stripColor(msg));
            return;
        }

        String message = LoggerUtil.color(msg);

        if (message != null) {
            console.sendMessage(message);
            toListeners(message);
        }
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

    public void setPlugin(JavaPlugin plugin) {
        this.console = plugin.getServer().getConsoleSender();
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
