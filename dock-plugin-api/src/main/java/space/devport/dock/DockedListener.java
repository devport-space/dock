package space.devport.dock;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.event.HandlerList;
import space.devport.dock.api.IDockedListener;
import space.devport.dock.api.IDockedPlugin;

@Log
public class DockedListener implements IDockedListener {

    protected final IDockedPlugin plugin;

    // Whether or not to register the listener on plugin enable.
    @Getter
    @Setter
    private boolean register = true;

    // Whether or not to unregister the listener on plugin disable.
    @Getter
    @Setter
    private boolean unregister = true;

    @Getter
    private boolean registered = false;

    public DockedListener(IDockedPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        this.registered = true;
        plugin.registerListener(this);
        log.fine("Registered listener " + getClass().getName());
    }

    public void unregister() {
        this.registered = false;
        HandlerList.unregisterAll(this);
        log.fine("Unregistered listener " + getClass().getName());
    }
}