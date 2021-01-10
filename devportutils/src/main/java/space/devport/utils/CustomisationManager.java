package space.devport.utils;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.item.ItemPrefab;
import space.devport.utils.item.impl.PrefabFactory;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.menu.MenuBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Log
public class CustomisationManager extends DevportManager {

    @Getter
    private final Configuration customisation = new Configuration(plugin, "customisation");

    private final Map<String, MenuBuilder> loadedMenus = new HashMap<>();
    private final Map<String, ItemPrefab> loadedItems = new HashMap<>();

    public CustomisationManager(DevportPlugin plugin) {
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

        log.log(DebugLevel.DEBUG, "Loading menus...");
        for (String name : customisation.section("menus").getKeys(false)) {
            MenuBuilder menuBuilder = customisation.getMenuBuilder("menus.".concat(name));

            if (menuBuilder == null) {
                log.warning("Could not load menu preset " + name);
                continue;
            }

            this.loadedMenus.put(name, menuBuilder);
        }

        if (!this.loadedMenus.isEmpty())
            log.info("Loaded " + this.loadedMenus.size() + " menu preset(s)...");

        log.log(DebugLevel.DEBUG, "Loading items...");
        for (String name : customisation.section("items").getKeys(false)) {
            ItemPrefab itemBuilder = customisation.getItem("items.".concat(name));

            if (itemBuilder == null) {
                log.warning("Could not load item preset " + name);
                continue;
            }

            this.loadedItems.put(name, itemBuilder);
        }

        if (!this.loadedItems.isEmpty())
            log.info("Loaded " + this.loadedItems.size() + " item preset(s)...");
    }

    @Nullable
    public MenuBuilder getMenu(String name) {
        return loadedMenus.get(name);
    }

    @Contract("null,_ -> param2;_,!null -> !null")
    public MenuBuilder getMenu(String name, MenuBuilder defaultValue) {
        return loadedMenus.getOrDefault(name, defaultValue);
    }

    public MenuBuilder getMenu(String name, @NotNull Supplier<MenuBuilder> defaultSupplier) {
        return loadedMenus.containsKey(name) ? loadedMenus.get(name) : defaultSupplier.get();
    }

    @Nullable
    public ItemPrefab getItem(String name) {
        return loadedItems.get(name);
    }

    @Contract("null,null -> null;_,!null -> !null")
    public ItemPrefab getItem(String name, ItemPrefab defaultValue) {
        return loadedItems.getOrDefault(name, defaultValue);
    }

    public ItemPrefab getItem(String name, @NotNull Supplier<ItemPrefab> defaultSupplier) {
        return loadedItems.containsKey(name) ? loadedItems.get(name) : defaultSupplier.get();
    }

    public Map<String, MenuBuilder> getMenus() {
        return Collections.unmodifiableMap(loadedMenus);
    }

    public Map<String, ItemPrefab> getItems() {
        return Collections.unmodifiableMap(loadedItems);
    }
}