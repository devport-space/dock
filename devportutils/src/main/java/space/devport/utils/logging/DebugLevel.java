package space.devport.utils.logging;

import java.util.logging.Level;

public class DebugLevel extends Level {

    public static final DebugLevel DEBUG = new DebugLevel("DEBUG", 200);

    protected DebugLevel(String name, int value) {
        super(name, value);
    }
}
