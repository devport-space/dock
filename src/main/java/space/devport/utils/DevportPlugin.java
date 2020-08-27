package space.devport.utils;

import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.CommandManager;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.holograms.HologramManager;
import space.devport.utils.menu.MenuManager;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.StringUtil;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.utility.reflection.ServerType;
import space.devport.utils.utility.reflection.ServerVersion;

import java.util.*;

public abstract class DevportPlugin extends JavaPlugin {

    //TODO Hold instance, but replace with dependency injection in most cases.
    @Getter
    @Setter
    private static DevportPlugin instance;

    @Getter
    protected PluginManager pluginManager;

    @Getter
    protected ConsoleOutput consoleOutput;

    @Getter
    private final Set<UsageFlag> usageFlags = new HashSet<>();

    @Getter
    protected CommandManager commandManager;
    @Getter
    protected MenuManager menuManager;
    @Getter
    protected LanguageManager languageManager;
    @Getter
    protected HologramManager hologramManager;
    @Getter
    protected CustomisationManager customisationManager;

    @Getter
    protected Configuration configuration;

    @Getter
    protected String prefix = "";

    @Getter
    private final Random random = new Random();

    @Getter
    private final Placeholders globalPlaceholders = new Placeholders();

    // Temporary.
    @Getter
    private Economy economy;

    public abstract void onPluginEnable();

    public abstract void onPluginDisable();

    public abstract void onReload();

    public abstract UsageFlag[] usageFlags();

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        instance = this;

        // Load version
        ServerVersion.loadServerVersion();
        ServerType.loadServerType();

        pluginManager = getServer().getPluginManager();

        // Setup Console Output
        consoleOutput = new ConsoleOutput(this);
        ConsoleOutput.setInstance(consoleOutput);

        // Print header
        consoleOutput.info("Starting up " + getDescription().getName() + " " + getDescription().getVersion());
        consoleOutput.info("Running on " + ServerType.getCurrentServerType().getName() + " " + ServerVersion.getCurrentVersion().toString());
        consoleOutput.info("&3~~~~~~~~~~~~ &7Devport &3~~~~~~~~~~~~");

        // Load usage flags
        this.usageFlags.addAll(Arrays.asList(usageFlags()));

        if (use(UsageFlag.CONFIGURATION)) {
            configuration = new Configuration(this, "config");

            if (configuration.getFileConfiguration().contains("hex-pattern"))
                StringUtil.HEX_PATTERN = configuration.getFileConfiguration().getString("hex-pattern");
            StringUtil.compilePattern();

            consoleOutput.setDebug(configuration.getFileConfiguration().getBoolean("debug-enabled", false));
            prefix = configuration.getColoredString("plugin-prefix", getDescription().getPrefix() != null ? getDescription().getPrefix() : "");
        }

        consoleOutput.debug("Usage flags: " + usageFlags.toString());

        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName());

        if (use(UsageFlag.COMMANDS))
            commandManager = new CommandManager(this);

        if (use(UsageFlag.CUSTOMISATION)) {
            customisationManager = new CustomisationManager(this);
            this.customisationManager.load();
        }

        if (use(UsageFlag.MENUS))
            menuManager = new MenuManager();

        if (use(UsageFlag.HOLOGRAMS)) {
            hologramManager = new HologramManager(this);
            hologramManager.attemptHook();
        }

        if (use(UsageFlag.LANGUAGE))
            languageManager = new LanguageManager();

        // Call plugin enable
        onPluginEnable();

        if (use(UsageFlag.LANGUAGE)) {
            languageManager.captureDefaults();
            languageManager.load();
            consoleOutput.info("Loaded " + languageManager.getCache().size() + " message(s)...");
        }

        if (use(UsageFlag.COMMANDS))
            commandManager.registerAll();

        consoleOutput.info("&3~~~~~~~~~~~~ &7/////// &3~~~~~~~~~~~~");
        consoleOutput.info("Done... startup took &f" + (System.currentTimeMillis() - start) + "&7ms.");

        // Set the prefix as the last thing, startup looks cooler without it.
        consoleOutput.setPrefix(prefix);

        // Runs after Server finished loading to ensure all possible deps would be loaded.
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (use(UsageFlag.HOLOGRAMS))
                hologramManager.attemptHook();
            if (use(UsageFlag.ECONOMY))
                setupEconomy();
        }, 1L);
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

        if (use(UsageFlag.CUSTOMISATION))
            this.customisationManager.load();

        globalPlaceholders.add("%prefix%", prefix)
                .add("%version%", getDescription().getVersion())
                .add("%pluginName%", getDescription().getName());

        if (use(UsageFlag.LANGUAGE)) {
            if (languageManager == null) languageManager = new LanguageManager();
            languageManager.load();
            consoleOutput.info("Loaded " + languageManager.getCache().size() + " message(s)..");
        }

        onReload();

        if (use(UsageFlag.HOLOGRAMS))
            if (hologramManager.isHooked()) {
                hologramManager.getHologramProvider().save();
                hologramManager.getHologramProvider().load();
            } else {
                hologramManager.attemptHook();
            }

        if (use(UsageFlag.ECONOMY))
            setupEconomy();

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

    public void setupEconomy() {

        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            if (economy != null) economy = null;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            if (economy != null) economy = null;
            consoleOutput.info("Found Vault, but no economy manager.");
            return;
        }

        if (economy != null) return;

        economy = rsp.getProvider();
        consoleOutput.info("Found Vault, using economy.");
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

    public void registerListener(Listener listener) {
        pluginManager.registerEvents(listener, this);
    }

    public MainCommand addMainCommand(MainCommand mainCommand) {
        commandManager.registeredCommands.add(mainCommand);
        return mainCommand;
    }
}