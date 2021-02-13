package space.devport.dock.api;

/**
 * Interface for common endpoints of Manager implementations.
 */
public interface IIndependentManager {

    void initialize();

    void reload();

    void shutdown();
}
