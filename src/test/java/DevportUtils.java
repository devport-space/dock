import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.utils.ConsoleOutput;

public class DevportUtils {

    @Getter
    public static ConsoleOutput cO;

    private JavaPlugin plugin;

    // Main plugin class, handles instances.
    public DevportUtils(JavaPlugin plugin) {
        this.plugin = plugin;

        cO = new ConsoleOutput();
        cO.debug("Test");
    }

    // Test method in a console application
    public static void main(String[] args) {
        cO = new ConsoleOutput(false);
        cO.setDebug(true);

        cO.info("Starting the test..");
        TestItAll.doSomething();
        cO.info("Done..");
    }
}
