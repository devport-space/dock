package space.devport.utils.text.language;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportManager;
import space.devport.utils.DevportPlugin;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.text.message.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log
public class LanguageManager extends DevportManager {

    @Getter
    private final Map<String, Message> cache = new HashMap<>();

    @Getter
    private final Map<String, Message> defaults = new HashMap<>();

    @Getter
    private final Set<LanguageDefaults> languageDefaults = new HashSet<>();

    @Getter
    private final Configuration language;

    @Getter
    @Setter
    private boolean setInternalDefaults = true;

    public LanguageManager(DevportPlugin plugin) {
        super(plugin);
        this.language = new Configuration(plugin, "language");
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

    public void addDefault(String path, String... message) {
        this.defaults.put(path, new Message(message));
    }

    public void addDefaults(LanguageDefaults defaults) {
        this.languageDefaults.add(defaults);
        log.info("Added language defaults " + defaults.getClass().getSimpleName());
    }

    private void captureDefaults() {
        if (setInternalDefaults) {
            for (CommandResult result : CommandResult.values()) {
                if (result.isDefaultMessage())
                    addDefault(result.getPath(), result.getMessage(plugin).toString());
            }

            addDefault("Commands.Reload", "&7Done... reload took &f%time%&7ms.");

            addDefault("Commands.Help.Header", "&8&m        &r &" + plugin.getColor().getChar() + "%pluginName% &7v&f%version% &8&m        ");
            addDefault("Commands.Help.Sub-Command-Line", "&" + plugin.getColor().getChar() + "%usage% &8- &7%description%");
        }

        languageDefaults.forEach(LanguageDefaults::setDefaults);
    }

    private void load() {

        language.load();

        boolean save = false;
        int added = 0;

        for (Map.Entry<String, Message> entry : defaults.entrySet()) {

            String path = entry.getKey();

            Message message;
            if (!language.getFileConfiguration().contains(path)) {
                language.setMessage(path, entry.getValue());
                message = defaults.get(path);
                added++;
                save = true;
            } else
                message = language.getMessage(path, new Message());

            cache.put(path, message);
        }

        if (save)
            language.save();

        log.info(String.format("Loaded %d%s message(s)...", cache.size(), added == 0 ? "" : String.format(" and added %d", added)));
    }

    /**
     * Get a new {@link Message} instance from path.
     * <p>
     * Sets the message to parse with the global plugin {@link space.devport.utils.text.Placeholders}.
     *
     * @param path Path of the message to get.
     * @return {@link Message} instance, empty if path is invalid.
     * @see DevportPlugin#getGlobalPlaceholders()
     */
    @NotNull
    public Message get(@Nullable String path) {
        Message message = Message.of(cache.get(path));
        message.parseWith(plugin.getGlobalPlaceholders());
        return message;
    }

    /**
     * Get a new {@link Message} instance from path and prefix it with %prefix%.
     * <p>
     * Sets the message to parse with the global plugin {@link space.devport.utils.text.Placeholders}.
     *
     * @param path Path of the message to get.
     * @return Message instance, empty (without prefix) if path is invalid.
     * @see DevportPlugin#getGlobalPlaceholders()
     */
    public Message getPrefixed(@Nullable String path) {
        Message message = get(path);

        if (message.isEmpty())
            return message;

        message.prefix("%prefix%");
        return message;
    }

    /**
     * Send a message from path to sender.
     * <p>
     * Doesn't proceed to send the message if it's empty -- if path is {@code null}.
     *
     * @param sender {@link CommandSender} to send the message to.
     * @param path   Path of the message to send.
     * @see Message#isEmpty()
     * @see Message#send(CommandSender)
     * @see LanguageManager#get(String)
     */
    public void send(@Nullable CommandSender sender, @Nullable String path) {
        if (sender == null)
            return;

        get(path).send(sender);
    }

    /**
     * Get a Message instance with {@link #getPrefixed(String)} and send it to sender.
     * <p>
     * Doesn't proceed to send the message if it's empty -- if path is {@code null}.
     *
     * @param sender CommandSender to send the message to.
     * @param path   Path of the message to send.
     */
    public void sendPrefixed(@Nullable CommandSender sender, @NotNull String path) {
        if (sender == null) return;
        getPrefixed(path).send(sender);
    }
}
