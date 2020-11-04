package space.devport.utils.version;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.DevportManager;
import space.devport.utils.utility.reflection.Reflection;
import space.devport.utils.utility.reflection.ServerVersion;
import space.devport.utils.version.api.ICompoundFactory;

import java.util.function.Consumer;

public class VersionManager extends DevportManager {

    private static VersionManager instance;

    @Getter
    private ICompoundFactory compoundFactory;

    private VersionManager() {
        super();
    }

    public static VersionManager getInstance() {
        return instance == null ? new VersionManager() : instance;
    }

    public static ICompoundFactory fetchCompoundFactory() {
        return getInstance().getCompoundFactory();
    }

    @Override
    public void onLoad() {
        load();
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
            Class<?> clazz = Class.forName(getClass().getPackage().getName() + "." + packageVersion + "." + subClassName);

            if (!interfaceClazz.isAssignableFrom(clazz)) {
                ConsoleOutput.getInstance().err("Subclass " + subClassName + " is not an implementation of " + interfaceClazz.getSimpleName() + ", cannot use it.");
                return false;
            }

            Class<? extends T> factoryClazz = clazz.asSubclass(interfaceClazz);

            T t = Reflection.obtainInstance(factoryClazz, null, null);
            store.accept(t);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean load(String packageVersion) {
        return load(packageVersion, "CompoundClass", ICompoundFactory.class, factory -> this.compoundFactory = factory);
    }

    private void load() {
        String version = ServerVersion.getNmsVersion();

        if (!load(version) && !ServerVersion.getNmsVersion().equals(ServerVersion.getCurrentVersion().getNmsFallbackVersion())) {
            ConsoleOutput.getInstance().warn("Could not load version dependent modules for version " + ServerVersion.getNmsVersion() + ", falling back to " + ServerVersion.getCurrentVersion().getNmsFallbackVersion());
            load(ServerVersion.getCurrentVersion().getNmsFallbackVersion());
        }

        if (this.compoundFactory != null)
            ConsoleOutput.getInstance().info("Loaded version dependent modules for " + ServerVersion.getNmsVersion());
        else
            ConsoleOutput.getInstance().err("Could not load version dependent modules for this version. Some features might not work.");
    }
}
