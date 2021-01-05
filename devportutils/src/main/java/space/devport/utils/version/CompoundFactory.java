package space.devport.utils.version;

import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportPlugin;
import space.devport.utils.IFactory;
import space.devport.utils.version.api.ICompound;
import space.devport.utils.version.api.ICompoundFactory;

public class CompoundFactory implements IFactory {

    private static ICompoundFactory compoundFactory;

    public CompoundFactory(DevportPlugin plugin) {
        CompoundFactory.compoundFactory = plugin.getManager(VersionManager.class).getCompoundFactory();
    }

    public void destroy() {
        compoundFactory = null;
    }

    public static ICompound create() {
        if (compoundFactory == null)
            throw new IllegalStateException("CompoundFactory is not initialized");

        return compoundFactory.create();
    }

    public static ICompound of(ItemStack item) {
        if (compoundFactory == null)
            throw new IllegalStateException("CompoundFactory is not initialized");

        return compoundFactory.of(item);
    }
}
