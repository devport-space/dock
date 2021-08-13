package space.devport.dock.util.server;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.common.Strings;

public enum ServerVersion {

    v1_7,
    v1_8,
    v1_9,
    v1_10,
    v1_11,
    v1_12,
    v1_13,
    v1_14,
    v1_15,
    v1_16,
    v1_17;

    ServerVersion() {
    }

    private static ServerVersion currentVersion;

    private static String nmsVersion;

    @Setter
    @NotNull
    public static ServerVersion defaultVersion = v1_17;

    @Setter
    @NotNull
    public static String defaultNMSVersion = "v1_17_R1";

    public static String getNmsVersion() {
        return Strings.isNullOrEmpty(nmsVersion) ? defaultNMSVersion : nmsVersion;
    }

    public static void loadServerVersion() {
        ServerVersion.nmsVersion = SpigotHelper.extractNMSVersion(false);

        if (Strings.isNullOrEmpty(nmsVersion))
            ServerVersion.nmsVersion = defaultNMSVersion;

        String shortVersion = SpigotHelper.extractNMSVersion(true);

        if (Strings.isNullOrEmpty(shortVersion))
            return;

        try {
            ServerVersion.currentVersion = valueOf(shortVersion);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @NotNull
    public static ServerVersion getCurrentVersion() {
        return currentVersion != null ? currentVersion : defaultVersion;
    }

    public boolean isAbove(ServerVersion version) {
        return this.ordinal() >= version.ordinal();
    }

    public boolean isBelow(ServerVersion version) {
        return this.ordinal() <= version.ordinal();
    }

    public static boolean isCurrentAbove(ServerVersion version) {
        return getCurrentVersion().isAbove(version);
    }

    public static boolean isCurrentBelow(ServerVersion version) {
        return getCurrentVersion().isBelow(version);
    }
}