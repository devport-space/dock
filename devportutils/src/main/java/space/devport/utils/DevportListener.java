package space.devport.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import space.devport.utils.logging.DebugLevel;

@Log
public class DevportListener implements Listener {

    protected final DevportPlugin plugin;

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

    public DevportListener(DevportPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        this.registered = true;
        plugin.addListener(this);
        log.log(DebugLevel.DEBUG, "Registered listener " + getClass().getName());
    }

    public void unregister() {
        this.registered = false;
        HandlerList.unregisterAll(this);
        log.log(DebugLevel.DEBUG, "Unregistered listener " + getClass().getName());
    }
}