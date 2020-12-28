package space.devport.utils;

import org.bukkit.event.Listener;

public class DevportListener implements Listener {

    protected final DevportPlugin plugin;

    public DevportListener(DevportPlugin plugin) {
        this.plugin = plugin;

        plugin.registerListener(this);
        plugin.getConsoleOutput().debug("Registered listener " + this.getClass().getName());
    }
}