package space.devport.dock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.commands.build.BuildableMainCommand;
import space.devport.dock.commands.CommandManager;
import space.devport.dock.commands.MainCommand;
import space.devport.dock.commands.build.BuildableSubCommand;
import space.devport.dock.configuration.Configuration;
import space.devport.dock.economy.EconomyManager;
import space.devport.dock.factory.IFactory;
import space.devport.dock.holograms.HologramManager;
import space.devport.dock.item.impl.PrefabFactory;
import space.devport.dock.logging.DockedLogger;
import space.devport.dock.menu.MenuManager;
import space.devport.dock.text.Placeholders;
import space.devport.dock.text.StringUtil;
import space.devport.dock.text.language.LanguageManager;
import space.devport.dock.utility.DependencyUtil;
import space.devport.dock.utility.ParseUtil;
import space.devport.dock.utility.reflection.Reflection;
import space.devport.dock.utility.reflection.ServerType;
import space.devport.dock.utility.reflection.ServerVersion;
import space.devport.dock.version.CompoundFactory;
import space.devport.dock.version.VersionManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
public abstract class DockedPlugin extends JavaPlugin {

    @Getter
    private DockedLogger dockedLogger;

    private final Set<IFactory> factories = new HashSet<>();

    private final Set<DockedListener> listeners = new HashSet<>();

    @Getter
    private final Map<Class<? extends DockedModule>, DockedModule> managers = new LinkedHashMap<>();

    @Getter
    private final Set<UsageFlag> usageFlags = new HashSet<>();

    @Getter
    protected Configuration configuration;

    @Getter
    protected String prefix = "";

    @Getter
    private final Random random = new Random();

    private final Placeholders globalPlaceholders = new Placeholders();

    @Getter
    private final ChatColor color = getPluginColor();

    public abstract void onPluginEnable();

    public abstract void onPluginDisable();

    public abstract void onReload();

    public abstract UsageFlag[] usageFlags();

    @Override
    public void onLoad() {

        // Load usage flags
        usageFlags.addAll(Arrays.asList(usageFlags()));

        // Setup logger
        this.dockedLogger = new DockedLogger(this);
        factories.add(dockedLogger);

        dockedLogger.setup();

        // Load version
        ServerVersion.loadServerVersion();
        ServerType.loadServerType();

        factories.add(new PrefabFactory(this));

        if (use(UsageFlag.NMS)) {
            VersionManager versionManager = new VersionManager(this);
            registerManager(versionManager);

            factories.add(new CompoundFactory(versionManager.getCompoundFactory()));
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

        log.info(String.format("Starting up %s %s", getDescription().getName(), getDescription().getVersion()));
        log.info(String.format("Running on %s %s (NMS: %s)",
                ServerType.getCurrentServerType().getName(),
                ServerVersion.getCurrentVersion().toString(),
                ServerVersion.getNmsVersion()));
        log.info(String.format("%s~~~~~~~~~~~~ &7%s %s~~~~~~~~~~~~", getColor(), getDescription().getName(), getColor()));

        if (use(UsageFlag.CONFIGURATION)) {
            this.configuration = new Configuration(this, "config");

            configuration.load();

            if (configuration.getFileConfiguration().contains("hex-pattern"))
                StringUtil.HEX_PATTERN = configuration.getFileConfiguration().getString("hex-pattern");
            StringUtil.compileHexPattern();

            if (configuration.getFileConfiguration().getBoolean("debug-enabled", false))
                dockedLogger.setLevel(Level.DEBUG);
            else
                dockedLogger.setLevel(configuration.getFileConfiguration().getString("debug-level", "INFO"));

            this.prefix = getColor() + configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");
        }

        // Fill global placeholders with some default parsers and values.
        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName())
                .addParser((str, player) -> str.replaceAll("(?i)%player%", ParseUtil.parseNotNull(player::getName, "null")), OfflinePlayer.class);

        callManagerAction(DockedModule::preEnable);

        // Call plugin specific actions.
        onPluginEnable();

        callManagerAction(DockedModule::afterEnable);

        // Register listeners that are set to.
        registerListeners();

        log.info(String.format("%s~~~~~~~~~~~~ &7/////// %s~~~~~~~~~~~~", getColor(), getColor()));
        log.info(String.format("Done... startup took &f%s&7ms.", (System.currentTimeMillis() - start)));

        // Set the prefix as the last thing, startup looks cooler without it.
        dockedLogger.setPrefix(prefix);

        Bukkit.getScheduler().runTask(this, () -> {

            if (DependencyUtil.isEnabled("PlaceholderAPI")) {
                globalPlaceholders.addParser((str, player) -> PlaceholderAPI.setPlaceholders(player, str), OfflinePlayer.class);
            }

            callManagerAction(DockedModule::afterDependencyLoad);
        });
    }

    public void reload() {
        reload(null);
    }

    public void reload(@Nullable CommandSender sender) {
        long start = System.currentTimeMillis();

        // Add reload requester to receive Log information.
        if (!(sender instanceof ConsoleCommandSender))
            dockedLogger.addListener(sender);

        // Reload configuration if we want to.
        if (use(UsageFlag.CONFIGURATION)) {
            configuration.load();

            if (configuration.getFileConfiguration().contains("hex-pattern")) {
                StringUtil.HEX_PATTERN = configuration.getFileConfiguration().getString("hex-pattern");
                StringUtil.compileHexPattern();
            }

            if (configuration.getFileConfiguration().getBoolean("debug-enabled", false))
                dockedLogger.setLevel(Level.DEBUG);
            else
                dockedLogger.setLevel(configuration.getFileConfiguration().getString("debug-level", "INFO"));

            this.prefix = configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");
        }

        // Reassign placeholder values.
        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName());

        callManagerAction(DockedModule::preReload);

        onReload();

        callManagerAction(DockedModule::afterReload);

        // Remove requester from log output and send him a done message.
        dockedLogger.removeListener(sender);

        if (use(UsageFlag.LANGUAGE))
            getManager(LanguageManager.class).getPrefixed("Commands.Reload")
                    .replace("%time%", (System.currentTimeMillis() - start))
                    .send(sender);
    }

    @Override
    public void onDisable() {
        onPluginDisable();

        // Unregister listeners.
        unregisterListeners();
        this.listeners.clear();

        // Clean managers.
        callManagerAction(DockedModule::onDisable);
        this.managers.clear();

        // Destroy factories.
        this.factories.forEach(IFactory::destroy);
        this.factories.clear();
    }

    public void registerManager(@NotNull DockedModule dockedModule) {
        Objects.requireNonNull(dockedModule, "Cannot register a null DevportManager.");
        this.managers.put(dockedModule.getClass(), dockedModule);
        dockedModule.onLoad();
    }

    public boolean isRegistered(Class<? extends DockedModule> clazz) {
        return this.managers.containsKey(clazz);
    }

    public <T extends DockedModule> T getManager(Class<T> clazz) {
        DockedModule manager = this.managers.get(clazz);

        if (manager == null) {

            T instancedManager = Reflection.obtainInstance(clazz, new Class[]{DockedPlugin.class}, new Object[]{this});

            if (instancedManager == null) {
                log.error("Tried to access a manager " + clazz.getSimpleName() + " that's not and cannot be registered.");
                return null;
            }

            // log.warn("Tried to access a manager " + clazz.getSimpleName() + " that was not registered. Registered and loaded it.");
            registerManager(instancedManager);
            return instancedManager;
        }

        if (!clazz.isAssignableFrom(manager.getClass())) {
            log.error("A different manager that expected was stored. Failing to retrieve it gracefully.");
            return null;
        }

        return clazz.cast(manager);
    }

    public void callManagerAction(Consumer<DockedModule> action) {
        for (DockedModule manager : this.managers.values()) {
            action.accept(manager);
        }
    }

    public void registerListener(Listener listener) {
        getPluginManager().registerEvents(listener, this);
    }

    public void addListener(DockedListener listener) {
        this.listeners.add(listener);
        log.debug("Added listener " + listener.getClass().getName());
    }

    public boolean removeListener(Class<? extends DockedListener> clazz) {
        return this.listeners.removeIf(l -> l.getClass().equals(clazz));
    }

    public void clearListeners() {
        this.listeners.clear();
    }

    public void registerListeners() {
        AtomicInteger count = new AtomicInteger();
        this.listeners.forEach(dockedListener -> {
            if (dockedListener.isRegister() && !dockedListener.isRegistered()) {
                dockedListener.register();
                count.incrementAndGet();
            }
        });
        if (count.get() > 0)
            log.info(String.format("Registered %d listener(s)...", count.get()));
    }

    public void unregisterListeners() {
        this.listeners.forEach(dockedListener -> {
            if (dockedListener.isUnregister())
                dockedListener.unregister();
        });
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

    /*
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

    public Placeholders getGlobalPlaceholders() {
        return globalPlaceholders;
    }

    // Obtain a copy of Global Placeholders.
    public Placeholders obtainPlaceholders() {
        return globalPlaceholders.clone();
    }
}