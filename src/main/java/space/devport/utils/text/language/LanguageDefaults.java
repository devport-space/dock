package space.devport.utils.text.language;

import space.devport.utils.DevportPlugin;

public abstract class LanguageDefaults {

    private final LanguageManager languageManager;

    public LanguageDefaults() {
        this.languageManager = DevportPlugin.getInstance().getLanguageManager();
        this.languageManager.addDefaults(this);
    }

    public void addDefault(String path, String... message) {
        languageManager.addDefault(path, message);
    }

    public abstract void setDefaults();
}