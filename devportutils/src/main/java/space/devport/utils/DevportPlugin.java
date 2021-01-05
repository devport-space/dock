package space.devport.utils;

import lombok.Getter;
import lombok.extern.java.Log;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.build.BuildableMainCommand;
import space.devport.utils.commands.CommandManager;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.commands.build.BuildableSubCommand;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.economy.EconomyManager;
import space.devport.utils.holograms.HologramManager;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.logging.DevportLogger;
import space.devport.utils.menu.MenuManager;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.StringUtil;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.utility.DependencyUtil;
import space.devport.utils.utility.ParseUtil;
import space.devport.utils.utility.reflection.Reflection;
import space.devport.utils.utility.reflection.ServerType;
import space.devport.utils.utility.reflection.ServerVersion;
import space.devport.utils.version.VersionManager;

import java.util.*;
import java.util.function.Consumer;

@Log
public abstract class DevportPlugin extends JavaPlugin {

    @Getter
    private DevportLogger devportLogger;

    @Getter
    private final Map<Class<? extends DevportManager>, DevportManager> managers = new LinkedHashMap<>();

    @Getter
    private final Set<UsageFlag> usageFlags = new HashSet<>();

    @Getter
    protected Configuration configuration;

    @Getter
    protected String prefix = "";

    @Getter
    private final Random random = new Random();

    @Getter
    private final Placeholders globalPlaceholders = new Placeholders();

    @Getter
    private final ChatColor color = getPluginColor();

    public abstract void onPluginEnable();

    public abstract void onPluginDisable();

    public abstract void onReload();

    public abstract UsageFlag[] usageFlags();

    @Override
    public void onLoad() {

        this.devportLogger = new DevportLogger(this);
        devportLogger.setup();

        // Load usage flags
        this.usageFlags.addAll(Arrays.asList(usageFlags()));

        if (use(UsageFlag.NMS)) {
            VersionManager versionManager = new VersionManager(this);
            registerManager(versionManager);
        }

        if (use(UsageFlag.COMMANDS)) {
            CommandManager commandManager = new CommandManager(this);
            registerManager(commandManager);
        }

        if (use(UsageFlag.CUSTOMISATION)) {
            CustomisationManager customisationManager = new CustomisationManager(this);
            registerManager(customisationManager);
        }

        if (use(UsageFlag.MENUS)) {
            MenuManager menuManager = new MenuManager(this);
            registerManager(menuManager);
        }

        if (use(UsageFlag.HOLOGRAMS)) {
            HologramManager hologramManager = new HologramManager(this);
            registerManager(hologramManager);
        }

        if (use(UsageFlag.ECONOMY)) {
            EconomyManager economyManager = new EconomyManager(this);
            registerManager(economyManager);
        }

        // Register economy manager as last to catch language added elsewhere.
        if (use(UsageFlag.LANGUAGE)) {
            LanguageManager languageManager = new LanguageManager(this);
            registerManager(languageManager);
        }
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        // Load version
        ServerVersion.loadServerVersion();
        ServerType.loadServerType();

        // Print header
        log.info(String.format("Starting up %s %s", getDescription().getName(), getDescription().getVersion()));
        log.info(String.format("Running on %s %s", ServerType.getCurrentServerType().getName(), ServerVersion.getCurrentVersion().toString()));
        log.info(String.format("%s~~~~~~~~~~~~ &7%s %s~~~~~~~~~~~~", getColor(), getDescription().getName(), getColor()));

        if (use(UsageFlag.CONFIGURATION)) {
            this.configuration = new Configuration(this, "config");

            configuration.load();

            if (configuration.getFileConfiguration().contains("hex-pattern"))
                StringUtil.HEX_PATTERN = configuration.getFileConfiguration().getString("hex-pattern");
            StringUtil.compilePattern();

            if (configuration.getFileConfiguration().getBoolean("debug-enabled", false))
                devportLogger.setLevel(DebugLevel.DEBUG);
            else
                devportLogger.setLevel(configuration.getFileConfiguration().getString("debug-level", "INFO"));

            this.prefix = getColor() + configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");
        }

        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName())
                .addParser((str, player) -> str.replaceAll("(?i)%player%", ParseUtil.getOrDefault(player::getName, "null")), OfflinePlayer.class);

        callManagerAction(DevportManager::preEnable);

        // Call plugin enable
        onPluginEnable();

        callManagerAction(DevportManager::afterEnable);

        log.info(String.format("%s~~~~~~~~~~~~ &7/////// %s~~~~~~~~~~~~", getColor(), getColor()));
        log.info(String.format("Done... startup took &f%s&7ms.", (System.currentTimeMillis() - start)));

        // Set the prefix as the last thing, startup looks cooler without it.
        devportLogger.getConsoleOutput().setPrefix(prefix);

        Bukkit.getScheduler().runTask(this, () -> {

            if (DependencyUtil.isEnabled("PlaceholderAPI")) {
                globalPlaceholders.addParser((str, player) -> PlaceholderAPI.setPlaceholders(player, str), Player.class);
            }

            callManagerAction(DevportManager::afterDependencyLoad);
        });
    }

    public void reload(CommandSender sender) {
        long start = System.currentTimeMillis();

        if (!(sender instanceof ConsoleCommandSender))
            devportLogger.getConsoleOutput().addListener(sender);

        if (use(UsageFlag.CONFIGURATION)) {
            configuration.load();

            if (configuration.getFileConfiguration().contains("hex-pattern")) {
                StringUtil.HEX_PATTERN = configuration.getFileConfiguration().getString("hex-pattern");
                StringUtil.compilePattern();
            }

            if (configuration.getFileConfiguration().getBoolean("debug-enabled", false))
                devportLogger.setLevel(DebugLevel.DEBUG);
            else
                devportLogger.setLevel(configuration.getFileConfiguration().getString("debug-level", "INFO"));

            this.prefix = configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");
        }

        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName());

        callManagerAction(DevportManager::preReload);

        onReload();

        callManagerAction(DevportManager::afterReload);

        devportLogger.getConsoleOutput().removeListener(sender);

        if (use(UsageFlag.LANGUAGE))
            getManager(LanguageManager.class).getPrefixed("Commands.Reload")
                    .replace("%time%", (System.currentTimeMillis() - start))
                    .send(sender);
    }

    @Override
    public void onDisable() {
        onPluginDisable();
        callManagerAction(DevportManager::onDisable);
    }

    public void registerManager(DevportManager devportManager) {
        this.managers.put(devportManager.getClass(), devportManager);

        devportManager.onLoad();
    }

    public boolean isRegistered(Class<? extends DevportManager> clazz) {
        return this.managers.containsKey(clazz);
    }


    public <T extends DevportManager> T getManager(Class<T> clazz) {
        DevportManager manager = this.managers.get(clazz);

        if (manager == null) {

            T instancedManager = Reflection.obtainInstance(clazz, new Class[]{DevportPlugin.class}, new Object[]{this});

            if (instancedManager == null) {
                log.severe("Tried to access a manager " + clazz.getSimpleName() + " that's not and cannot be registered.");
                return null;
            }

            // log.warning("Tried to access a manager " + clazz.getSimpleName() + " that was not registered. Registered and loaded it.");
            registerManager(instancedManager);
            return instancedManager;
        }

        if (!clazz.isAssignableFrom(manager.getClass())) {
            log.severe("A different manager that expected was stored. Failing to retrieve it gracefully.");
            return null;
        }

        return clazz.cast(manager);
    }

    public void callManagerAction(Consumer<DevportManager> action) {
        for (DevportManager manager : this.managers.values()) {
            action.accept(manager);
        }
    }

    public void registerListener(Listener listener) {
        getPluginManager().registerEvents(listener, this);
    }

    public MainCommand registerMainCommand(MainCommand mainCommand) {
        getManager(CommandManager.class).registerCommand(mainCommand);
        return mainCommand;
    }

    public BuildableMainCommand buildMainCommand(String name) {
        BuildableMainCommand mainCommand = new BuildableMainCommand(this, name);
        registerMainCommand(mainCommand);
        return mainCommand;
    }

    public BuildableSubCommand buildSubCommand(String name) {
        return new BuildableSubCommand(this, name);
    }

    public boolean use(UsageFlag usageFlag) {
        return this.usageFlags.contains(usageFlag);
    }

    @Override
    public void reloadConfig() {
        if (configuration != null) configuration.load();
    }

    @Override
    public void saveConfig() {
        if (configuration != null) configuration.save();
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        return configuration.getFileConfiguration();
    }

    /**
     * Get the whole dependency list.
     */
    public List<String> getDependencies() {
        List<String> dependencies = new ArrayList<>();
        dependencies.addAll(getDescription().getDepend());
        dependencies.addAll(getDescription().getSoftDepend());
        return dependencies;
    }

    public ChatColor getPluginColor() {
        return StringUtil.getRandomColor();
    }

    public PluginManager getPluginManager() {
        return getServer().getPluginManager();
    }
}