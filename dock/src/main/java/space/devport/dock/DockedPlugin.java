package space.devport.dock;

import lombok.Getter;
import lombok.extern.java.Log;
import me.clip.placeholderapi.PlaceholderAPI;
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
import space.devport.dock.api.IDockedFactory;
import space.devport.dock.api.IDockedListener;
import space.devport.dock.api.IDockedManager;
import space.devport.dock.api.IDockedPlugin;
import space.devport.dock.commands.CommandManager;
import space.devport.dock.commands.MainCommand;
import space.devport.dock.commands.build.BuildableMainCommand;
import space.devport.dock.commands.build.BuildableSubCommand;
import space.devport.dock.configuration.Configuration;
import space.devport.dock.economy.EconomyManager;
import space.devport.dock.holograms.HologramManager;
import space.devport.dock.logging.DockedLogger;
import space.devport.dock.menu.MenuManager;
import space.devport.dock.text.language.LanguageManager;
import space.devport.dock.text.placeholders.Placeholders;
import space.devport.dock.util.DependencyUtil;
import space.devport.dock.util.ParseUtil;
import space.devport.dock.util.StringUtil;
import space.devport.dock.util.reflection.Reflection;
import space.devport.dock.util.reflection.ServerType;
import space.devport.dock.util.reflection.ServerVersion;
import space.devport.dock.version.VersionManager;
import space.devport.dock.version.compound.CompoundFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Log
public abstract class DockedPlugin extends JavaPlugin implements IDockedPlugin {

    @Getter
    private DockedLogger dockedLogger;

    private final Set<IDockedFactory> factories = new HashSet<>();

    private final Set<IDockedListener> listeners = new HashSet<>();

    private final Map<Class<? extends IDockedManager>, IDockedManager> managers = new LinkedHashMap<>();

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
        dockedLogger.setup(getClass().getPackage().getName());

        // Load version
        ServerVersion.loadServerVersion();
        ServerType.loadServerType();

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

        log.info(() -> "Starting up " + getDescription().getName() + " " + getDescription().getVersion());
        log.info(() -> "Running on " + ServerType.getCurrentServerType().getName() + " " +
                ServerVersion.getCurrentVersion().toString() + " (NMS: " + ServerVersion.getNmsVersion() + ")");
        log.info(() -> getColor() + "~~~~~~~~~~~~ &7" + getDescription().getName() + " " + getColor() + "~~~~~~~~~~~~");

        if (use(UsageFlag.CONFIGURATION)) {
            this.configuration = new Configuration(this, "config");

            configuration.load();

            if (configuration.getFileConfiguration().contains("hex-pattern"))
                StringUtil.HEX_PATTERN = configuration.getFileConfiguration().getString("hex-pattern");
            StringUtil.compileHexPattern();

            configureLogger();

            this.prefix = getColor() + configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");
        }

        // Fill global placeholders with some default parsers and values.
        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName())
                .addParser((str, player) -> str.replaceAll("(?i)%player%", ParseUtil.parseNotNull(player::getName, "null")), OfflinePlayer.class);

        callManagerAction(IDockedManager::preEnable);

        // Call plugin specific actions.
        onPluginEnable();

        callManagerAction(IDockedManager::afterEnable);

        // Register listeners that are set to.
        registerListeners();

        log.info(() -> getColor() + "~~~~~~~~~~~~ &7/////// " + getColor() + "~~~~~~~~~~~~");
        log.info(() -> "Done... startup took &f" + (System.currentTimeMillis() - start) + "&7ms.");

        // Set the prefix as the last thing, startup looks cooler without it.
        dockedLogger.setPrefix(prefix);

        Bukkit.getScheduler().runTask(this, () -> {

            if (DependencyUtil.isEnabled("PlaceholderAPI")) {
                globalPlaceholders.addParser((str, player) -> PlaceholderAPI.setPlaceholders(player, str), OfflinePlayer.class);
            }

            callManagerAction(IDockedManager::afterDependencyLoad);
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

            configureLogger();

            this.prefix = configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");
            dockedLogger.setPrefix(prefix);
        }

        // Reassign placeholder values.
        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName());

        callManagerAction(IDockedManager::preReload);

        onReload();

        callManagerAction(IDockedManager::afterReload);

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
        listeners.clear();

        // Clean managers.
        callManagerAction(IDockedManager::onDisable);
        managers.clear();

        // Destroy factories.
        factories.forEach(IDockedFactory::destroy);
        factories.clear();

        dockedLogger.destroy();
    }

    private void configureLogger() {
        dockedLogger.setLevel(configuration.getFileConfiguration().getString("log-level", "INFO"));
    }

    public void registerManager(@NotNull IDockedManager dockedManager) {
        Objects.requireNonNull(dockedManager, "Cannot register a null DevportManager.");

        managers.put(dockedManager.getClass(), dockedManager);
        dockedManager.onLoad();
    }

    public boolean isRegistered(Class<? extends IDockedManager> clazz) {
        return managers.containsKey(clazz);
    }

    public <T extends IDockedManager> T getManager(Class<T> clazz) {
        IDockedManager manager = managers.get(clazz);

        if (manager == null) {

            T instancedManager = Reflection.obtainInstance(clazz, new Class[]{DockedPlugin.class}, new Object[]{this});

            if (instancedManager == null) {
                log.severe("Tried to access a manager " + clazz.getSimpleName() + " that's not and cannot be registered.");
                return null;
            }

            // log.warning(() ->"Tried to access a manager " + clazz.getSimpleName() + " that was not registered. Registered and loaded it.");
            registerManager(instancedManager);
            return instancedManager;
        }

        if (!clazz.isAssignableFrom(manager.getClass())) {
            log.severe("A different manager that expected was stored. Failing to retrieve it gracefully.");
            return null;
        }

        return clazz.cast(manager);
    }

    private void callManagerAction(Consumer<IDockedManager> action) {
        for (IDockedManager manager : managers.values()) {
            action.accept(manager);
        }
    }

    public void registerListener(Listener listener) {
        getPluginManager().registerEvents(listener, this);
    }

    public void addListener(IDockedListener listener) {
        listeners.add(listener);
        log.fine(() -> "Added listener " + listener.getClass().getName());
    }

    public boolean removeListener(Class<? extends IDockedListener> clazz) {
        return this.listeners.removeIf(l -> l.getClass().equals(clazz));
    }

    public void clearListeners() {
        this.listeners.clear();
    }

    public void registerListeners() {
        AtomicInteger count = new AtomicInteger();
        listeners.forEach(dockedListener -> {
            if (dockedListener.isRegister() && !dockedListener.isRegistered()) {
                dockedListener.register();
                count.incrementAndGet();
            }
        });

        if (count.get() > 0)
            log.info(() -> "Registered " + count.get() + " listener(s)...");
    }

    public void unregisterListeners() {
        listeners.forEach(dockedListener -> {
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

    @Override
    public JavaPlugin getPlugin() {
        return this;
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