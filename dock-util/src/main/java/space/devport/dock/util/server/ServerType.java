package space.devport.dock.util.server;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

public enum ServerType {

    BUKKIT("bukkit"),
    SPIGOT("spigot"),
    iSPIGOT("ispigot"),
    PAPER("paper"),
    TACO("taco"),
    qSPIGOT("qspigot");

    private static ServerType currentType;

    @Setter
    private static ServerType defaultType = SPIGOT;

    public static void loadServerType() {
        for (ServerType val : values()) {
            if (Bukkit.getServer().getName().equalsIgnoreCase(val.getName()))
                currentType = val;
        }
    }

    public static ServerType getCurrentServerType() {
        return currentType != null ? currentType : defaultType;
    }

    @Getter
    private final String name;

    ServerType(String name) {
        this.name = name;
    }
}
