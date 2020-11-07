package space.devport.utils.text.language;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.DevportManager;
import space.devport.utils.DevportPlugin;
import space.devport.utils.text.message.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageManager extends DevportManager {

    @Getter
    private final Map<String, Message> cache = new HashMap<>();

    @Getter
    protected final Map<String, Message> defaults = new HashMap<>();

    @Getter
    private final List<LanguageDefaults> languageDefaults = new ArrayList<>();

    @Getter
    private Configuration language;

    @Getter
    @Setter
    private boolean setInternalDefaults = true;

    public LanguageManager(DevportPlugin plugin) {
        super(plugin);
    }

    @Override
    public void afterEnable() {
        captureDefaults();
        load();
    }

    @Override
    public void afterReload() {
        load();
    }

    public void captureDefaults() {
        if (setInternalDefaults) {
            for (CommandResult result : CommandResult.values()) {
                if (result.isDefaultMessage())
                    addDefault(result.getPath(), result.getMessage().toString());
            }

            addDefault("Commands.Reload", "&7Done... reload took &f%time%&7ms.");

            addDefault("Commands.Help.Header", "&8&m        &r &" + plugin.getColor().getChar() + "%pluginName% &7v&f%version% &8&m        ");
            addDefault("Commands.Help.Sub-Command-Line", "&" + plugin.getColor().getChar() + "%usage% &8- &7%description%");
        }
        languageDefaults.forEach(LanguageDefaults::setDefaults);
    }

    public void addDefault(String path, String... message) {
        this.defaults.put(path, new Message(message));
        plugin.getConsoleOutput().debug("Added default " + path);
    }

    public void addDefaults(LanguageDefaults defaults) {
        this.languageDefaults.add(defaults);
        plugin.getConsoleOutput().info("Added language defaults " + defaults.getClass().getSimpleName());
    }

    public void load() {

        if (this.language == null)
            this.language = new Configuration(plugin, "language");
        else
            this.language.load();

        boolean save = false;
        int added = 0;

        for (Map.Entry<String, Message> entry : defaults.entrySet()) {

            Message message;
            if (!language.getFileConfiguration().contains(entry.getKey())) {
                language.setMessage(entry.getKey(), entry.getValue());
                message = defaults.get(entry.getKey());
                added++;
                save = true;
            } else
                message = language.getMessage(entry.getKey(), new Message());

            cache.put(entry.getKey(), message);
        }

        if (save)
            language.save();

        consoleOutput.info("Loaded " + this.cache.size() + " " + (added != 0 ? "and added " + added + " new " : "") + "message(s)...");
    }

    public Message get(@NotNull String path) {
        Message msg = new Message(cache.get(path));
        msg.parseWith(plugin.getGlobalPlaceholders());
        return msg;
    }

    public Message getPrefixed(@NotNull String path) {
        Message msg = get(path);

        if (msg.isEmpty()) return msg;

        msg.prefix("%prefix%");
        msg.parseWith(plugin.getGlobalPlaceholders());

        return msg;
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