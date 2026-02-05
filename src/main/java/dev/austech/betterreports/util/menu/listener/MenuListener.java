/*
 * BetterReports - MenuListener.java
 *
 * Copyright (c) 2023 AusTech Development
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.austech.betterreports.util.menu.listener;

import dev.austech.betterreports.util.menu.Menu;
import dev.austech.betterreports.util.menu.MenuManager;
import dev.austech.betterreports.util.menu.layout.MenuButton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Menu menu = MenuManager.OPENED_MENUS.get(player.getUniqueId());

        if (menu == null) {
            return;
        }

        // Fix: Prevent interacting with the menu if it's not allowed
        // Fix: Prevent interacting with the menu if it's not allowed
        if (!menu.isAllowEditing()) {
            // Strict check: Any interaction with the top inventory (menu) slots must be
            // canceled
            if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                event.setCancelled(true);
                return; // Stop processing further to prevent "Ghost Items" from logic below
            }

            // Block Shift-Click from Bottom Inventory into Top Inventory
            if (event.getAction() == org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
            }

            // Block Double Click (Collect to Cursor) if it touches the menu
            // (Note: Double click collects from all inventories, risky to allow)
            if (event.getAction() == org.bukkit.event.inventory.InventoryAction.COLLECT_TO_CURSOR) {
                event.setCancelled(true);
            }

            // Block "Hotbar Swap" (Number Keys) if targeting the menu is implicitly handled
            // by rawSlot check above
            // BUT: If targeting bottom inventory, we must ensure it doesn't pull FROM menu?
            // "Hotbar Swap" in bottom inv swaps with hotbar (also bottom inv). Safe.
            // "Hotbar Swap" in top inv is caught by rawSlot check. Safe.
        }

        if (menu.getButtons(player).containsKey(event.getSlot())
                && event.getClickedInventory() == event.getView().getTopInventory()) {
            final MenuButton clickedButton = menu.getButtons(player).get(event.getSlot());

            // Double check cancellation for safe measure
            if (!clickedButton.isAllowEditing())
                event.setCancelled(true);

            if (clickedButton.getAction() != null)
                clickedButton.getAction().accept(event, player);

            if (clickedButton.isCloseMenu()) {
                player.closeInventory();
                return;
            }

            if (event.isCancelled()) {
                if (MenuManager.OPENED_MENUS.get(player.getUniqueId()) == menu) {
                    menu.update(player);
                }
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(final org.bukkit.event.inventory.InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Menu menu = MenuManager.OPENED_MENUS.get(player.getUniqueId());

        if (menu == null) {
            return;
        }

        // Fix: Block dragging items into/inside the menu if editing is disabled
        if (!menu.isAllowEditing()) {
            for (int slot : event.getRawSlots()) {
                if (slot < event.getView().getTopInventory().getSize()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer(); // Bukkit being stupid, so we have to cast it.
        final Menu menu = MenuManager.OPENED_MENUS.get(player.getUniqueId());
        if (menu != null) {
            menu.onClose(player);
            MenuManager.cancelTask(player);
            MenuManager.OPENED_MENUS.remove(player.getUniqueId());
        }
    }
}
