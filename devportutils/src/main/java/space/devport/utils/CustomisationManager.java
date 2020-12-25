package space.devport.utils;

import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.item.ItemBuilder;
import space.devport.utils.menu.MenuBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomisationManager extends DevportManager {

    @Getter
    private Configuration customisation;

    private final Map<String, MenuBuilder> loadedMenus = new HashMap<>();
    private final Map<String, ItemBuilder> loadedItems = new HashMap<>();

    public CustomisationManager(DevportPlugin plugin) {
        super(plugin);
    }

    @NotNull
    public MenuBuilder getMenuBuilder(String name) {
        return this.loadedMenus.getOrDefault(name, new MenuBuilder(plugin));
    }

    @NotNull
    public ItemBuilder getItemBuilder(String name) {
        return this.loadedItems.getOrDefault(name, new ItemBuilder(Material.AIR, plugin));
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

        this.customisation = new Configuration(plugin, "customisation");

        plugin.getConsoleOutput().debug("Loading menus...");
        for (String name : customisation.section("menus").getKeys(false)) {
            MenuBuilder menuBuilder = customisation.getMenuBuilder("menus.".concat(name));

            if (menuBuilder == null) {
                consoleOutput.warn("Could not load menu preset " + name);
                continue;
            }

            this.loadedMenus.put(name, menuBuilder);
        }

        if (!this.loadedMenus.isEmpty())
            plugin.getConsoleOutput().info("Loaded " + this.loadedMenus.size() + " menu preset(s)...");

        plugin.getConsoleOutput().debug("Loading items...");
        for (String name : customisation.section("items").getKeys(false)) {
            ItemBuilder itemBuilder = customisation.getItemBuilder("items.".concat(name));

            if (itemBuilder == null) {
                consoleOutput.warn("Could not load item preset " + name);
                continue;
            }

            this.loadedItems.put(name, itemBuilder);
        }

        if (!this.loadedItems.isEmpty())
            plugin.getConsoleOutput().info("Loaded " + this.loadedItems.size() + " item preset(s)...");
    }

    public Map<String, MenuBuilder> getMenus() {
        return Collections.unmodifiableMap(loadedMenus);
    }

    public Map<String, ItemBuilder> getItems() {
        return Collections.unmodifiableMap(loadedItems);
    }
}