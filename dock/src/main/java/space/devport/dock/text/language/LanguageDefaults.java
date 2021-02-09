package space.devport.dock.text.language;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.api.IDockedPlugin;

@Slf4j
public abstract class LanguageDefaults {

    private final IDockedPlugin plugin;
    private LanguageManager languageManager;

    public LanguageDefaults(IDockedPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Add all the defaults to LanguageManager using {@link #addDefault(String, String...)} when called.
     */
    public abstract void setDefaults();

    public void register() {
        if (!plugin.isRegistered(LanguageManager.class)) {
            log.warn("Attempted to register LanguageDefaults when Language is not used.");
            this.languageManager = null;
            return;
        }

        this.languageManager = plugin.getManager(LanguageManager.class);
        this.languageManager.addDefaults(this);
    }

    public void addDefault(@NotNull String path, @NotNull String... message) {
        // Register in case someone forgot.
        if (languageManager == null)
            register();

        languageManager.addDefault(path, message);
    }
}