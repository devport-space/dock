package space.devport.utils.version;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.DevportManager;
import space.devport.utils.DevportPlugin;
import space.devport.utils.utility.reflection.Reflection;
import space.devport.utils.utility.reflection.ServerVersion;
import space.devport.utils.version.api.ICompoundFactory;
import space.devport.utils.version.api.IVersionUtility;

import java.util.function.Consumer;

public class VersionManager extends DevportManager {

    @Getter
    private ICompoundFactory compoundFactory;

    @Getter
    private IVersionUtility versionUtility;

    public VersionManager(DevportPlugin plugin) {
        super(plugin);
    }

    public static ICompoundFactory fetchCompoundFactory() {
        return DevportPlugin.getInstance().getManager(VersionManager.class).getCompoundFactory();
    }

    public static IVersionUtility fetchVersionUtility() {
        return DevportPlugin.getInstance().getManager(VersionManager.class).getVersionUtility();
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
            String path = getClass().getPackage().getName() + "." + packageVersion + "." + subClassName;
            ConsoleOutput.getInstance().debug(path);
            Class<?> clazz = Class.forName(path);

            if (!interfaceClazz.isAssignableFrom(clazz)) {
                ConsoleOutput.getInstance().err("Subclass " + subClassName + " is not an implementation of " + interfaceClazz.getSimpleName() + ", cannot use it.");
                return false;
            }

            Class<? extends T> factoryClazz = clazz.asSubclass(interfaceClazz);

            T t = Reflection.obtainInstance(factoryClazz, null, null);
            store.accept(t);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean load(String packageVersion) {
        return load(packageVersion, "CompoundFactory", ICompoundFactory.class, factory -> this.compoundFactory = factory) &&
                load(packageVersion, "VersionUtility", IVersionUtility.class, versionUtility -> this.versionUtility = versionUtility);
    }

    private void load() {
        String version = ServerVersion.getNmsVersion();

        if (load(version))
            ConsoleOutput.getInstance().info("Loaded version dependent modules for " + version);
        else
            ConsoleOutput.getInstance().err("Could not load version dependent modules for this version. Some features might not work.");
    }
}
