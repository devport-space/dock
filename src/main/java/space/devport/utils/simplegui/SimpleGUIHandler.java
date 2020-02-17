package space.devport.utils.simplegui;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportUtils;
import space.devport.utils.simplegui.events.SimpleItemClickEvent;

import java.util.HashMap;

public class SimpleGUIHandler implements Listener {

    // Holding currently created GUIs
    @Getter
    private HashMap<String, SimpleGUI> guiCache = new HashMap<>();

    // Add a gui to the cache
    public void addGUI(SimpleGUI gui) {
        guiCache.put(gui.getName(), gui);
    }

    // Click listener, throws SimpleItemClickEvent
    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCursor() == null || e.getClickedInventory() == null) return;

        // Get which gui the player clicked
        Inventory inventory = e.getClickedInventory();

        SimpleGUI simpleGUI = null;

        for (SimpleGUI gui : DevportUtils.inst.getGuiHandler().getGuiCache().values())
            if (inventory.equals(gui.getInventory()))
                simpleGUI = gui;

        if (simpleGUI == null)
            return;

        DevportUtils.inst.getConsoleOutput().debug("InventoryClickEvent fired on slot " + e.getSlot() + " in inventory " + simpleGUI.getName());
        DevportUtils.inst.getConsoleOutput().debug("Item slots: " + simpleGUI.getItems().keySet());

        if (!simpleGUI.getItems().containsKey(e.getSlot())) {
            DevportUtils.inst.getConsoleOutput().debug("Case 4");
            return;
        }

        // Get clicked SimpleItem
        SimpleItem clickedItem = simpleGUI.getItems().get(e.getSlot());

        // Cancel event if we should.
        if (clickedItem.isCancelClick())
            e.setCancelled(true);

        // Throw new event
        DevportUtils.inst.getConsoleOutput().debug("Calling a SimpleGUI event.");
        SimpleItemClickEvent clickEvent = new SimpleItemClickEvent(e, simpleGUI, clickedItem);
        DevportUtils.inst.getPlugin().getServer().getPluginManager().callEvent(clickEvent);

        // Call method
        simpleGUI.onClick(e, simpleGUI, clickedItem);
    }
}
