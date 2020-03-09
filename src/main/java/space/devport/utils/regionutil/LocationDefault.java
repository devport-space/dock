package space.devport.utils.regionutil;

import org.jetbrains.annotations.NotNull;

/**
 * Default values for LocationUtil.
 */
// Will move to a global Default enum later on.
@Deprecated
public enum LocationDefault {

    LOCATION_DELIMITER(";");

    private String value;

    /**
     * Default enum constructor with a value.
     *
     * @param value Value for the enum
     * */
    LocationDefault(String value) {
        this.value = value;
    }

    /**
     * Set the enum String value.
     *
     * @param value String value to set
     */
    public void set(@NotNull String value) {
        this.value = value;
    }

    /**
     * Returns an enum String value.
     *
     * @return String enum value
     */
    @NotNull
    public String get() {
        return this.value;
    }
}