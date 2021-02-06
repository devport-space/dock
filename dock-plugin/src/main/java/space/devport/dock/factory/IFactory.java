package space.devport.dock.factory;

public interface IFactory {

    /**
     * Destroy the factory.
     * Untie any static references the factory holds.
     */
    void destroy();
}
