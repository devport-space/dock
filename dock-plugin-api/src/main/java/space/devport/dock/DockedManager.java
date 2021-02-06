package space.devport.dock;

import space.devport.dock.api.IDockedManager;
import space.devport.dock.api.IDockedPlugin;

/**
 * DevportCore manager module.
 */
public abstract class DockedManager implements IDockedManager {

    protected final IDockedPlugin plugin;

    public DockedManager(IDockedPlugin plugin) {
        this.plugin = plugin;
    }

    public void onLoad() {
    }

    public void preEnable() {
    }

    public void afterEnable() {
    }

    public void preReload() {
    }

    public void afterReload() {
    }

    public void onDisable() {
    }

    /**
     * Fired after the server is completely loaded.
     */
    public void afterDependencyLoad() {
    }
}