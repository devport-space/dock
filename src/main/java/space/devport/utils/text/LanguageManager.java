package space.devport.utils.text;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.configuration.Configuration;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final DevportPlugin plugin;

    @Getter
    private final Map<String, Message> cache = new HashMap<>();

    @Getter
    protected final Map<String, Message> defaults = new HashMap<>();

    @Getter
    private Configuration language;

    public LanguageManager() {
        this.plugin = DevportPlugin.getInstance();
        setDefaults();
    }

    // Override this method to add defaults when needed.
    public void setDefaults() {
        addDefault("Commands.Not-Enough-Args", "%prefix%&cNot enough arguments.", "&cUsage: &7%usage%");
        addDefault("Commands.Too-Many-Args", "%prefix%&cToo many arguments.", "&cUsage: &7%usage%");
    }

    public void addDefault(String path, String... message) {
        this.defaults.put(path, new Message(message));
    }

    public void load() {
        this.language = new Configuration(plugin, "language");

        boolean save = false;

        for (String path : defaults.keySet()) {

            Message message;
            if (!language.getFileConfiguration().contains(path)) {
                language.setMessageBuilder(path, defaults.get(path));
                message = defaults.get(path);
                save = true;
            } else {
                message = language.getMessage(path, new Message());
            }
            cache.put(path, message);
        }

        if (save)
            language.save();
    }

    public Message get(@NotNull String path) {
        return cache.getOrDefault(path, new Message());
    }

    public Message getPrefixed(@NotNull String path) {
        Message msg = get(path);

        if (msg.isEmpty()) return msg;
        else return msg.prefix(plugin.getPrefix());
    }

    public void send(@Nullable CommandSender sender, @NotNull String path) {
        if (sender == null) return;
        get(path).send(sender);
    }

    public void sendPrefixed(@Nullable CommandSender sender, @NotNull String path) {
        if (sender == null) return;
        getPrefixed(path).send(sender);
    }
}
