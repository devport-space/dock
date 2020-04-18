package space.devport.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.CommandManager;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.holograms.HologramManager;
import space.devport.utils.menu.MenuManager;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.utility.reflection.ServerType;
import space.devport.utils.utility.reflection.ServerVersion;

import java.util.Random;

public abstract class DevportPlugin extends JavaPlugin {

    @Setter
    private static DevportPlugin instance;

    // TODO: Modules

    @Getter
    protected DevportUtils utils;

    @Getter
    protected PluginManager pluginManager;

    @Getter
    @Setter
    protected ConsoleOutput consoleOutput;

    @Getter
    protected CommandManager commandManager;

    @Getter
    protected MenuManager menuManager;

    @Getter
    protected LanguageManager languageManager;

    @Getter
    protected Configuration configuration;

    @Getter
    protected HologramManager hologramManager;

    @Getter
    protected String prefix = "";

    @Getter
    private ChatColor color = ChatColor.WHITE;

    @Getter
    private final Random random = new Random();

    @Getter
    private final ChatColor[] colors = new ChatColor[]{ChatColor.AQUA, ChatColor.YELLOW, ChatColor.RED, ChatColor.GREEN,
            ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.BLUE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE,
            ChatColor.WHITE, ChatColor.DARK_PURPLE};

    @Getter
    private final Placeholders globalPlaceholders = new Placeholders();

    public static DevportPlugin getInstance() {
        return instance;
    }

    public abstract void onPluginEnable();

    public abstract void onPluginDisable();

    public abstract void onReload();

    public abstract boolean useLanguage();

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        this.color = colors[random.nextInt(colors.length)];

        instance = this;

        ServerVersion.loadServerVersion();
        ServerType.loadServerType();

        pluginManager = getServer().getPluginManager();

        utils = new DevportUtils(this);

        // Setup Console Output
        consoleOutput = utils.getConsoleOutput();
        consoleOutput.setColors(true);

        consoleOutput.info("Starting up " + getDescription().getName() + " v" + getDescription().getVersion());
        consoleOutput.info("Running on " + ServerType.getCurrentServerType().getName() + " " + ServerVersion.getCurrentVersion().toString());
        consoleOutput.info("&3~~~~~~~~~~~~ &7Devport &3~~~~~~~~~~~~");

        configuration = new Configuration(this, "config");

        consoleOutput.setDebug(configuration.getFileConfiguration().getBoolean("debug-enabled"));

        prefix = configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");

        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName());

        commandManager = new CommandManager(this);
        menuManager = new MenuManager();
        hologramManager = new HologramManager(this);
        hologramManager.attemptHook();

        if (useLanguage()) languageManager = new LanguageManager();

        onPluginEnable();

        if (useLanguage()) {
            languageManager.captureDefaults();
            languageManager.load();
            consoleOutput.info("Loaded " + languageManager.getCache().size() + " message(s)..");
        }

        commandManager.registerAll();

        consoleOutput.info("&3~~~~~~~~~~~~ &7/////// &3~~~~~~~~~~~~");
        consoleOutput.info("Done... startup took &f" + (System.currentTimeMillis() - start) + "&7ms.");

        // Set the prefix as the last thing, startup looks cooler without it.
        consoleOutput.setPrefix(prefix);
    }

    public void reload(CommandSender sender) {
        long start = System.currentTimeMillis();

        this.color = colors[random.nextInt(colors.length)];

        if (!(sender instanceof ConsoleCommandSender))
            consoleOutput.addListener(sender);

        consoleOutput.setDebug(configuration.getFileConfiguration().getBoolean("debug-enabled"));

        prefix = configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");

        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName());

        configuration.load();

        if (useLanguage()) {
            if (languageManager == null) languageManager = new LanguageManager();
            languageManager.load();
            consoleOutput.info("Loaded " + languageManager.getCache().size() + " message(s)..");
        }

        onReload();

        if (hologramManager.isHooked()) {
            hologramManager.getHologramProvider().save();
            hologramManager.getHologramProvider().load();
        } else {
            hologramManager.attemptHook();
        }

        consoleOutput.removeListener(sender);

        getLanguageManager().getPrefixed("Commands.Reload")
                .replace("%time%", (System.currentTimeMillis() - start))
                .send(sender);
    }

    @Override
    public void onDisable() {
        if (hologramManager != null && hologramManager.isHooked())
            hologramManager.getHologramProvider().save();

        onPluginDisable();
    }

    @Override
    public void reloadConfig() {
        configuration.load();
    }

    @Override
    public void saveConfig() {
        configuration.save();
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        return configuration.getFileConfiguration();
    }

    public void registerListener(Listener listener) {
        pluginManager.registerEvents(listener, this);
    }

    public MainCommand addMainCommand(MainCommand mainCommand) {
        commandManager.registeredCommands.add(mainCommand);
        return mainCommand;
    }
}