package space.devport.dock.version;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.DockedManager;
import space.devport.dock.api.IDockedPlugin;
import space.devport.dock.utility.reflection.Reflection;
import space.devport.dock.utility.reflection.ServerVersion;
import space.devport.dock.version.api.ICompoundFactory;
import space.devport.dock.version.api.IVersionUtility;

import java.util.function.Consumer;

@Slf4j
public class VersionManager extends DockedManager {

    @Getter
    private ICompoundFactory compoundFactory;

    @Getter
    private IVersionUtility versionUtility;

    public VersionManager(IDockedPlugin plugin) {
        super(plugin);
    }

    @Override
    public void preEnable() {

    }

    @Override
    public void afterEnable() {

    }

    @Override
    public void preReload() {

    }

    @Override
    public void afterReload() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void afterDependencyLoad() {

    }

    @Override
    public void onLoad() {
        initialize();
    }

    public void initialize() {
        String version = ServerVersion.getNmsVersion();

        if (load(version))
            log.info("Loaded version dependent modules for {}", version);
        else
            log.error("Could not load version dependent modules for {}. Some features might not work.", version);
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
                log.error("Subclass {} is not an implementation of {}, cannot use it.", subClassName, interfaceClazz.getSimpleName());
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
