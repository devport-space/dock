package space.devport.utils;

import lombok.extern.java.Log;
import org.bukkit.event.Listener;
import space.devport.utils.logging.DebugLevel;

@Log
public class DevportListener implements Listener {

    protected final DevportPlugin plugin;

    public DevportListener(DevportPlugin plugin) {
        this.plugin = plugin;

        plugin.registerListener(this);
        log.log(DebugLevel.DEBUG, "Registered listener " + this.getClass().getName());
    }
}