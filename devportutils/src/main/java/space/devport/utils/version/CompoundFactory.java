package space.devport.utils.version;

import org.bukkit.inventory.ItemStack;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.factory.IFactory;
import space.devport.utils.version.api.ICompound;
import space.devport.utils.version.api.ICompoundFactory;

public class CompoundFactory implements IFactory {

    private static ICompoundFactory compoundFactory;

    /**
     * Initialize a new CompoundFactory.
     *
     * @param plugin DevportPlugin reference
     * @throws IllegalStateException when it's already initialized.
     */
    public CompoundFactory(DevportPlugin plugin) {
        if (plugin.use(UsageFlag.NMS)) {
            if (compoundFactory != null)
                throw new IllegalStateException("CompoundFactory already initialized.");

            CompoundFactory.compoundFactory = plugin.getManager(VersionManager.class).getCompoundFactory();
        }
    }

    @Override
    public void destroy() {
        compoundFactory = null;
    }

    private static void checkInitialized() throws IllegalStateException {
        if (compoundFactory == null)
            throw new IllegalStateException("CompoundFactory is not initialized");
    }

    /**
     * Create a new Compound.
     *
     * @return New Compound object
     * @throws IllegalStateException when the CompoundFactory is not initialized.
     */
    public static ICompound create() {
        checkInitialized();
        return compoundFactory.create();
    }

    /**
     * Create compound from {@param item}
     *
     * @param item ItemStack to get Compound from
     * @return Compound object
     * @throws IllegalStateException when the CompoundFactory is not initialized.
     */
    public static ICompound of(ItemStack item) {
        checkInitialized();
        return compoundFactory.of(item);
    }
}
