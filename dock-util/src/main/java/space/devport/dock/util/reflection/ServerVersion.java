package space.devport.dock.util.reflection;

import com.google.common.base.Strings;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

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
    public static ServerVersion defaultVersion = v1_16;

    public static String getNmsVersion() {
        if (nmsVersion == null)
            loadServerVersion();
        return nmsVersion;
    }

    public static void loadServerVersion() {
        ServerVersion.nmsVersion = SpigotHelper.extractNMSVersion(false);

        if (Strings.isNullOrEmpty(nmsVersion))
            nmsVersion = "v1_16_R3";

        String nmsVersion = SpigotHelper.extractNMSVersion(true);

        if (Strings.isNullOrEmpty(nmsVersion))
            return;

        try {
            ServerVersion.currentVersion = valueOf(nmsVersion);
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
        return currentVersion.ordinal() >= version.ordinal();
    }

    public static boolean isCurrentBelow(ServerVersion version) {
        return currentVersion.ordinal() <= version.ordinal();
    }
}