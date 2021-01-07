package space.devport.utils.version;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.factory.IFactory;
import space.devport.utils.version.api.ICompound;
import space.devport.utils.version.api.ICompoundFactory;

import java.util.Objects;

public class CompoundFactory implements IFactory {

    private static ICompoundFactory compoundFactory;

    /**
     * Initialize a new CompoundFactory.
     *
     * @param compoundFactory {@link ICompoundFactory} instance.
     * @throws IllegalStateException when it's already initialized.
     */
    public CompoundFactory(@NotNull ICompoundFactory compoundFactory) {
        if (CompoundFactory.compoundFactory != null)
            throw new IllegalStateException("CompoundFactory already initialized.");

        Objects.requireNonNull(compoundFactory);

        CompoundFactory.compoundFactory = compoundFactory;
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
     * Create a new {@link ICompound}.
     *
     * @return New ICompound object.
     * @throws IllegalStateException when the CompoundFactory is not initialized.
     */
    @NotNull
    public static ICompound create() {
        checkInitialized();
        return compoundFactory.create();
    }

    /**
     * Create an {@link ICompound} from {@link ItemStack}.
     *
     * @param item {@link ItemStack} to get {@link ICompound} from.
     * @return {@link ICompound} object from item.
     * @throws IllegalStateException when the CompoundFactory is not initialized.
     */
    @NotNull
    public static ICompound of(@NotNull ItemStack item) {
        checkInitialized();
        Objects.requireNonNull(item);
        return compoundFactory.of(item);
    }
}
