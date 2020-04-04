package space.devport.utils.text;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import space.devport.utils.DevportPlugin;
import space.devport.utils.configuration.Configuration;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final DevportPlugin plugin;

    @Getter
    private final Map<String, Message> cache = new HashMap<>();

    @Getter
    private final Map<String, Message> defaults = new HashMap<>();

    @Getter
    private final Configuration language;

    public LanguageManager() {
        this.plugin = DevportPlugin.getInstance();
        this.language = new Configuration(plugin, "language");
    }

    // Override this method to add defaults when needed.
    public void setDefaults() {
        defaults.put("Commands.Not-Enough-Args", new Message("%prefix%&cNot enough arguments.", "&cUsage: &7%usage%"));
        defaults.put("Commands.Too-Many-Args", new Message("%prefix%&cToo many arguments.", "&cUsage: &7%usage%"));
    }

    public void load() {
        language.reload();

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

    public Message get(String path) {
        return cache.getOrDefault(path, new Message());
    }

    public Message getPrefixed(String path) {
        return get(path).insert(plugin.getPrefix());
    }

    public void send(CommandSender sender, String path) {
        get(path).send(sender);
    }

    public void sendPrefixed(CommandSender sender, String path) {
        getPrefixed(path).send(sender);
    }
}
