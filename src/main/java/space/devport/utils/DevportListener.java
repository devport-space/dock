package space.devport.utils;

import org.bukkit.event.Listener;

public class DevportListener implements Listener {

    public DevportListener() {
        DevportPlugin.getInstance().registerListener(this);
        DevportPlugin.getInstance().getConsoleOutput().debug("Registered listener " + this.getClass().getName());
    }
}