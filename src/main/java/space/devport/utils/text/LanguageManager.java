package space.devport.utils.text;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.configuration.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageManager {

    private final DevportPlugin plugin;

    @Getter
    private final Map<String, Message> cache = new HashMap<>();

    @Getter
    protected final Map<String, Message> defaults = new HashMap<>();

    @Getter
    private final List<LanguageDefaults> languageDefaults = new ArrayList<>();

    @Getter
    private Configuration language;

    public LanguageManager() {
        this.plugin = DevportPlugin.getInstance();
        captureDefaults();
    }

    public void captureDefaults() {
        addDefault("Commands.Not-Enough-Args", "%prefix%&cNot enough arguments.", "%prefix%&cUsage: &7%usage%");
        addDefault("Commands.Too-Many-Args", "%prefix%&cToo many arguments.", "%prefix0&cUsage: &7%usage%");
        addDefault("Commands.Only-Players", "%prefix%&cOnly players can do this.");
        addDefault("Commands.Only-Console", "%prefix%&cOnly the console can do this.");

        addDefault("Commands.Reload", "&7Done... reload took &f%time%&7ms.");

        languageDefaults.forEach(LanguageDefaults::setDefaults);
    }

    public void addDefault(String path, String... message) {
        this.defaults.put(path, new Message(message));
    }

    public void addDefaults(LanguageDefaults defaults) {
        this.languageDefaults.add(defaults);
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
        return new Message(cache.getOrDefault(path, null));
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
