package space.devport.utils;

import org.bukkit.event.Listener;

public class DevportListener implements Listener {

    public DevportListener(DevportPlugin plugin) {
        plugin.registerListener(this);
        plugin.getConsoleOutput().debug("Registered listener " + this.getClass().getName());
    }
}