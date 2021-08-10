package util;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Test;
import space.devport.dock.util.LocationUtil;
import util.mock.MockWorld;

import static org.junit.Assert.assertEquals;

public class LocationUtilTest {

    @Test
    public void locationShouldSerializeToStringProperly() {
        World mockWorld = new MockWorld("world");
        Location location = new Location(mockWorld, 10.0, 20.0, 30.0);

        String locationString = LocationUtil.composeString(location).orNull();

        assertEquals("world;10.0;20.0;30.0", locationString);
    }
}
