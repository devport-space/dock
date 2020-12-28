package space.devport.utils.text.language;

import lombok.extern.java.Log;
import space.devport.utils.DevportPlugin;

@Log
public abstract class LanguageDefaults {

    private final LanguageManager languageManager;

    public LanguageDefaults(DevportPlugin plugin) {
        if (!plugin.isRegistered(LanguageManager.class)) {
            log.severe("Attempted to register LanguageDefaults when Language is not used.");
            this.languageManager = null;
            return;
        }

        this.languageManager = plugin.getManager(LanguageManager.class);
        this.languageManager.addDefaults(this);
    }

    public void addDefault(String path, String... message) {
        languageManager.addDefault(path, message);
    }

    public abstract void setDefaults();
}