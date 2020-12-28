package space.devport.utils;

import space.devport.utils.logging.ConsoleOutput;

/**
 * DevportCore manager module.
 */
public abstract class DevportManager {

    protected DevportPlugin plugin;

    protected ConsoleOutput consoleOutput;

    public DevportManager(DevportPlugin plugin) {
        this.plugin = plugin;
        this.consoleOutput = plugin.getConsoleOutput();
    }

    public void onLoad() {
    }

    public void preEnable() {
    }

    public void afterEnable() {
    }

    public void preReload() {
    }

    public void afterReload() {
    }

    public void onDisable() {
    }

    /**
     * Fired after the server is completely loaded.
     */
    public void afterDependencyLoad() {
    }
}