package space.devport.dock.common;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class Strings {

    public boolean isNullOrEmpty(@Nullable String value) {
        return value == null || value.isEmpty();
    }
}
