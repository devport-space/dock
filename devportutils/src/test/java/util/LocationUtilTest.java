package util;

import mock.MockWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Test;
import space.devport.utils.callbacks.exception.CallbackException;
import space.devport.utils.utility.LocationUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class LocationUtilTest {

    @Test
    public void locationShouldSerializeToStringProperly() {
        World mockWorld = new MockWorld("world");
        Location location = new Location(mockWorld, 10.0, 20.0, 30.0);

        String locationString = LocationUtil.composeString(location);

        assertEquals("world;10.0;20.0;30.0", locationString);
    }

    @Test
    public void locationUtilShouldThrowCorrectly() {
        World mockWorld = new MockWorld("world");
        Location location = new Location(mockWorld, 10.0, 20.0, 30.0);

        assertThrows(CallbackException.class, () -> LocationUtil.composeString(location, null, null));
    }
}
