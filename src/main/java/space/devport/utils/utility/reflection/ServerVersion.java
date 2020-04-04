package space.devport.utils.utility.reflection;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;

public enum ServerVersion {

    v1_7(0),
    v1_8(1),
    v1_9(2),
    v1_10(3),
    v1_11(4),
    v1_12(5),
    v1_13(6),
    v1_14(7),
    v1_15(8),
    v1_16(9);

    private static ServerVersion currentVersion;

    @Setter
    public static ServerVersion defaultVersion = v1_12;

    public static void loadServerVersion() {
        String nmsVersion = SpigotHelper.extractNMSVersion(true);

        if (Strings.isNullOrEmpty(nmsVersion)) return;

        try {
            currentVersion = valueOf(nmsVersion);
        } catch (IllegalArgumentException ignored) {
        }
    }

    public static ServerVersion getCurrentVersion() {
        return currentVersion != null ? currentVersion : defaultVersion;
    }

    @Getter
    private final int value;

    ServerVersion(int value) {
        this.value = value;
    }

    public boolean isAbove(ServerVersion version) {
        return this.value >= version.getValue();
    }

    public boolean isBelow(ServerVersion version) {
        return this.value <= version.getValue();
    }

    public static boolean isAboveCurrent(ServerVersion version) {
        return currentVersion.getValue() >= version.getValue();
    }

    public static boolean isBelowCurrent(ServerVersion version) {
        return currentVersion.getValue() <= version.getValue();
    }
}