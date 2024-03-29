package space.devport.dock.version;

import lombok.Getter;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.DockedManager;
import space.devport.dock.api.IDockedPlugin;
import space.devport.dock.api.IIndependentManager;
import space.devport.dock.util.reflection.Reflection;
import space.devport.dock.util.server.ServerVersion;
import space.devport.dock.version.api.ICompoundFactory;
import space.devport.dock.version.api.IVersionUtility;

import java.util.function.Consumer;

@Log
public class VersionManager extends DockedManager implements IIndependentManager {

    @Getter
    private ICompoundFactory compoundFactory;

    @Getter
    private IVersionUtility versionUtility;

    public VersionManager(IDockedPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        initialize();
    }

    @Override
    public void reload() {
        // NO-OP, reloading the modules could be unsafe.
    }

    @Override
    public void shutdown() {
        this.compoundFactory = null;
        this.versionUtility = null;
    }

    /**
     * Initialize endpoint. Call this onLoad ideally.
     */
    @Override
    public void initialize() {
        String version = ServerVersion.getNmsVersion();

        if (load(version))
            log.info(() -> "Loaded version dependent modules for " + version);
        else
            log.severe(() -> "Could not load version dependent modules for " + version + ". Some features might not work.");
    }

    /**
     * Attempt to load a version subclass.
     *
     * @param <T>            Common interface from version.api
     * @param packageVersion NMS package version
     * @param subClassName   Subclass implementation name to look for
     * @param interfaceClazz Class of the common interface
     * @param store          Consume loaded subclass instance
     */
    private <T> boolean load(@NotNull String packageVersion, @NotNull String subClassName, @NotNull Class<T> interfaceClazz, @NotNull Consumer<T> store) {
        try {
            String path = getClass().getPackage().getName() + "." + packageVersion + "." + subClassName;
            Class<?> clazz = Class.forName(path);

            if (!interfaceClazz.isAssignableFrom(clazz)) {
                log.severe(() -> "Subclass " + subClassName + " is not an implementation of " + interfaceClazz.getSimpleName() + ", cannot use it.");
                return false;
            }

            Class<? extends T> factoryClazz = clazz.asSubclass(interfaceClazz);

            T t = Reflection.obtainInstance(factoryClazz, null, null);
            store.accept(t);
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean load(String packageVersion) {
        return load(packageVersion, "CompoundFactory", ICompoundFactory.class, factory -> this.compoundFactory = factory) &&
                load(packageVersion, "VersionUtility", IVersionUtility.class, versionUtility -> this.versionUtility = versionUtility);
    }
}
