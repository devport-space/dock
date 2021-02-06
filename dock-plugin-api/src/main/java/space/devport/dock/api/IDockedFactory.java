package space.devport.dock.api;

public interface IDockedFactory {

    /**
     * Destroy the factory.
     * Untie any static references the factory holds.
     */
    void destroy();
}
