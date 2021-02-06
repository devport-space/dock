package space.devport.dock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.configuration.Configuration;
import space.devport.dock.item.ItemPrefab;
import space.devport.dock.menu.MenuBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class CustomisationManager extends DockedModule {

    @Getter
    private final Configuration customisation = new Configuration(plugin, "customisation");

    private final Map<String, MenuBuilder> loadedMenus = new HashMap<>();
    private final Map<String, ItemPrefab> loadedItems = new HashMap<>();

    public CustomisationManager(DockedPlugin plugin) {
        super(plugin);
    }

    @Override
    public void preEnable() {
        load();
    }

    @Override
    public void preReload() {
        load();
    }

    public void load() {
        customisation.load();

        log.debug("Loading menus...");
        for (String name : customisation.section("menus").getKeys(false)) {
            MenuBuilder menuBuilder = customisation.getMenuBuilder("menus.".concat(name));

            if (menuBuilder == null) {
                log.warn("Could not load menu preset " + name);
                continue;
            }

            this.loadedMenus.put(name, menuBuilder);
        }

        if (!this.loadedMenus.isEmpty())
            log.info("Loaded " + this.loadedMenus.size() + " menu preset(s)...");

        log.debug("Loading items...");
        for (String name : customisation.section("items").getKeys(false)) {
            ItemPrefab itemBuilder = customisation.getItem("items.".concat(name));

            if (itemBuilder == null) {
                log.warn("Could not load item preset " + name);
                continue;
            }

            this.loadedItems.put(name, itemBuilder);
        }

        if (!this.loadedItems.isEmpty())
            log.info("Loaded " + this.loadedItems.size() + " item preset(s)...");
    }

    @Nullable
    public MenuBuilder getMenu(String name) {
        return getMenu(name, (MenuBuilder) null);
    }

    @Contract("null,_ -> param2;_,!null -> !null")
    public MenuBuilder getMenu(String name, MenuBuilder defaultValue) {
        if (loadedMenus.containsKey(name))
            return loadedMenus.get(name).clone();
        return defaultValue;
    }

    public MenuBuilder getMenu(String name, @NotNull Supplier<MenuBuilder> defaultSupplier) {
        return loadedMenus.containsKey(name) ? loadedMenus.get(name).clone() : defaultSupplier.get();
    }

    @Nullable
    public ItemPrefab getItem(String name) {
        return getItem(name, (ItemPrefab) null);
    }

    @Contract("null,null -> null;_,!null -> !null")
    public ItemPrefab getItem(String name, ItemPrefab defaultValue) {
        if (loadedItems.containsKey(name))
            return loadedItems.get(name).clone();
        return defaultValue;
    }

    public ItemPrefab getItem(String name, @NotNull Supplier<ItemPrefab> defaultSupplier) {
        return loadedItems.containsKey(name) ? loadedItems.get(name).clone() : defaultSupplier.get();
    }

    public Map<String, MenuBuilder> getMenus() {
        return Collections.unmodifiableMap(loadedMenus);
    }

    public Map<String, ItemPrefab> getItems() {
        return Collections.unmodifiableMap(loadedItems);
    }
}