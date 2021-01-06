package space.devport.utils.text.language;

import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.DevportPlugin;

@Log
public abstract class LanguageDefaults {

    private final DevportPlugin plugin;
    private LanguageManager languageManager;

    public LanguageDefaults(DevportPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Add all the defaults to LanguageManager using {@link #addDefault(String, String...)} when called.
     */
    public abstract void setDefaults();

    public void register() {
        if (!plugin.isRegistered(LanguageManager.class)) {
            log.warning("Attempted to register LanguageDefaults when Language is not used.");
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