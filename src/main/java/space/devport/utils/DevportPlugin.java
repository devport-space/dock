package space.devport.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
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
import space.devport.utils.economy.EconomyManager;
import space.devport.utils.holograms.HologramManager;
import space.devport.utils.menu.MenuManager;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.StringUtil;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.utility.reflection.ServerType;
import space.devport.utils.utility.reflection.ServerVersion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

public abstract class DevportPlugin extends JavaPlugin {

    @Getter
    private static DevportPlugin instance;

    @Getter
    private final Map<Class<? extends DevportManager>, DevportManager> managers = new HashMap<>();

    @Getter
    private final Set<UsageFlag> usageFlags = new HashSet<>();

    @Getter
    protected ConsoleOutput consoleOutput;

    @Getter
    protected Configuration configuration;

    @Getter
    protected String prefix = "";

    @Getter
    private final Random random = new Random();

    @Getter
    private final Placeholders globalPlaceholders = new Placeholders();

    public abstract void onPluginEnable();

    public abstract void onPluginDisable();

    public abstract void onReload();

    public abstract UsageFlag[] usageFlags();

    @Override
    public void onLoad() {
        instance = getPlugin(this.getClass());

        // Setup Console Output
        consoleOutput = ConsoleOutput.getInstance(this);

        // Load usage flags
        this.usageFlags.addAll(Arrays.asList(usageFlags()));

        consoleOutput.debug("Usage flags: " + usageFlags.toString());

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

        if (use(UsageFlag.LANGUAGE)) {
            LanguageManager languageManager = new LanguageManager(this);
            registerManager(languageManager);
        }

        if (use(UsageFlag.ECONOMY)) {
            EconomyManager economyManager = new EconomyManager(this);
            registerManager(economyManager);
        }
    }

    public void callManagerAction(Consumer<DevportManager> action) {
        for (DevportManager manager : this.managers.values()) {
            action.accept(manager);
        }
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        // Load version
        ServerVersion.loadServerVersion();
        ServerType.loadServerType();

        // Print header
        consoleOutput.info("Starting up " + getDescription().getName() + " " + getDescription().getVersion());
        consoleOutput.info("Running on " + ServerType.getCurrentServerType().getName() + " " + ServerVersion.getCurrentVersion().toString());
        consoleOutput.info("&" + getColor().getChar() + "~~~~~~~~~~~~ &7Devport &" + getColor().getChar() + "~~~~~~~~~~~~");

        //TODO Maybe move to load to allow debugging in #onLoad().
        if (use(UsageFlag.CONFIGURATION)) {
            configuration = new Configuration(this, "config");

            if (configuration.getFileConfiguration().contains("hex-pattern"))
                StringUtil.HEX_PATTERN = configuration.getFileConfiguration().getString("hex-pattern");
            StringUtil.compilePattern();

            consoleOutput.setDebug(configuration.getFileConfiguration().getBoolean("debug-enabled", false));
            prefix = configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");
        }

        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName());

        callManagerAction(DevportManager::preEnable);

        // Call plugin enable
        onPluginEnable();

        callManagerAction(DevportManager::afterEnable);

        consoleOutput.info("&" + getColor().getChar() + "~~~~~~~~~~~~ &7/////// &" + getColor().getChar() + "~~~~~~~~~~~~");
        consoleOutput.info("Done... startup took &f" + (System.currentTimeMillis() - start) + "&7ms.");

        // Set the prefix as the last thing, startup looks cooler without it.
        consoleOutput.setPrefix(prefix);

        Bukkit.getScheduler().runTask(this, () -> callManagerAction(DevportManager::afterDependencyLoad));
    }

    public void reload(CommandSender sender) {
        long start = System.currentTimeMillis();

        if (!(sender instanceof ConsoleCommandSender))
            consoleOutput.addListener(sender);

        if (use(UsageFlag.CONFIGURATION)) {
            configuration.load();

            if (configuration.getFileConfiguration().contains("hex-pattern")) {
                StringUtil.HEX_PATTERN = configuration.getFileConfiguration().getString("hex-pattern");
                StringUtil.compilePattern();
            }

            consoleOutput.setDebug(configuration.getFileConfiguration().getBoolean("debug-enabled", false));
            prefix = configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");
        }

        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName());

        callManagerAction(DevportManager::preReload);

        onReload();

        callManagerAction(DevportManager::afterReload);

        consoleOutput.removeListener(sender);

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

    public <T extends DevportManager> T getManager(Class<T> clazz) {
        DevportManager manager = this.managers.get(clazz);

        if (manager == null) {
            consoleOutput.err("Tried to access a manager with class " + clazz.getSimpleName() + " that's not registered.");
            return null;
        }

        return (T) manager;
    }

    public boolean isRegistered(Class<? extends DevportManager> clazz) {
        return this.managers.containsKey(clazz);
    }

    public boolean use(UsageFlag usageFlag) {
        return this.usageFlags.contains(usageFlag);
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

    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    public PluginManager getPluginManager() {
        return getServer().getPluginManager();
    }

    public void registerListener(Listener listener) {
        getPluginManager().registerEvents(listener, this);
    }

    public void registerManager(DevportManager devportManager) {
        this.managers.put(devportManager.getClass(), devportManager);
    }

    public MainCommand addMainCommand(MainCommand mainCommand) {
        if (use(UsageFlag.COMMANDS))
            getManager(CommandManager.class).registeredCommands.add(mainCommand);
        else consoleOutput.err("Attempted to register a command when command manager is not registered.");
        return mainCommand;
    }
}