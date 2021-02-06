package space.devport.dock.api;

import org.bukkit.event.Listener;

public interface IDockedListener extends Listener {

    boolean isRegistered();

    boolean isRegister();

    boolean isUnregister();

    void register();

    void unregister();
}
