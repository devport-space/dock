package space.devport.dock.api;

public interface IDockedManager {

    void onLoad();

    void preEnable();

    void afterEnable();

    void preReload();

    void afterReload();

    void onDisable();

    /**
     * Fired after the server is completely loaded.
     */
    void afterDependencyLoad();
}
