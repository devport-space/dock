import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.utils.ConsoleOutput;

public class DevportUtils {

    @Getter
    public ConsoleOutput cO;

    private JavaPlugin plugin;

    // Main plugin class, handles instances.
    public DevportUtils(JavaPlugin plugin) {
        this.plugin = plugin;

        cO = new ConsoleOutput(plugin);
    }

    // Test method for console only.
    public static void main(String[] args) {
        new TestItAll().doSomething();
    }
}
