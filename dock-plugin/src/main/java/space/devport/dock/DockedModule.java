package space.devport.dock;

/**
 * DevportCore manager module.
 */
public abstract class DockedModule {

    protected final DockedPlugin plugin;

    public DockedModule(DockedPlugin plugin) {
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