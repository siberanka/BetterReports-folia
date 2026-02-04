/*
 * BetterReports - Menu.java
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

package dev.austech.betterreports.util.menu;

import dev.austech.betterreports.BetterReports;
import dev.austech.betterreports.util.Common;
import dev.austech.betterreports.util.menu.layout.MenuButton;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

@Getter
@Setter
public abstract class Menu {
    private Menu toReturn;
    private boolean allowEditing = false;
    private boolean autoUpdate = false;

    public boolean canOpen(Player player) {
        return true;
    }

    public Menu setReturn(Menu menu) {
        this.toReturn = menu;
        return this;
    }

    public abstract String getPlayerTitle(Player player);

    public abstract Map<Integer, MenuButton> getButtons(Player player);

    public int getSize() {
        return 54;
    }

    public void open(final Player player) {
        Inventory inventory = create(player);
        player.openInventory(inventory);
        startUpdate(player);
    }

    public Inventory create(final Player player) {
        Map<Integer, MenuButton> buttons = getButtons(player);
        String title = Common.color(getPlayerTitle(player));

        Inventory inv = Bukkit.createInventory(null, getSize(), title);

        buttons.forEach((slot, button) -> {
            if (slot >= 0 && slot < getSize()) {
                inv.setItem(slot, button.getItem(player));
            }
        });

        return inv;
    }

    private void startUpdate(final Player player) {
        MenuManager.cancelTask(player);
        MenuManager.OPENED_MENUS.put(player.getUniqueId(), this);
        onOpen(player);

        ScheduledTask task = player.getScheduler().runAtFixedRate(BetterReports.getInstance(), (st) -> {
            if (!player.isOnline()) {
                st.cancel();
                MenuManager.OPENED_MENUS.remove(player.getUniqueId());
                return;
            }

            if (isAutoUpdate()) {
                update(player);
            }
        }, null, 10L, 10L);

        MenuManager.CHECK_TASKS.put(player.getUniqueId(), task);
    }

    public final void update(Player player) {
        player.getOpenInventory().getTopInventory().setContents(create(player).getContents());
    }

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }
}
