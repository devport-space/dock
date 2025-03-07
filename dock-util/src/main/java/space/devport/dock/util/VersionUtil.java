package space.devport.dock.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class VersionUtil {

    public static int compareVersions(@NotNull String version1, @NotNull String version2) {
        return compareVersions(version1, version2, -1);
    }

    // Compare simple semver
    public static int compareVersions(@NotNull String version1, @NotNull String version2, int depth) {
        // Compare major
        String[] arr1 = version1.split("-")[0].split("\\.");
        String[] arr2 = version2.split("-")[0].split("\\.");

        int len = depth == -1 ? Math.max(arr1.length, arr2.length) : depth;

        for (int i = 0; i < len; i++) {

            if (arr1.length < i) {
                return -1;
            } else if (arr2.length < i) {
                return 1;
            }

            int num1;
            try {
                num1 = Integer.parseInt(arr1[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid version string '" + version1 + "'.");
            }

            int num2;
            try {
                num2 = Integer.parseInt(arr2[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid version string '" + version1 + "'.");
            }

            if (num1 > num2) {
                return 1;
            } else if (num2 > num1) {
                return -1;
            }
        }
        return 0;
    }
}