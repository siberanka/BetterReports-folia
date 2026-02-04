/*
 * BetterReports - InputListener.java
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

package dev.austech.betterreports.util;

import dev.austech.betterreports.BetterReports;
import dev.austech.betterreports.util.config.impl.MainConfig;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class InputListener implements Listener {
    private static final Map<UUID, Consumer<String>> WAITING_INPUT = new ConcurrentHashMap<>();
    private static final String[] ESCAPE_SEQUENCES = { "quit", "exit", "escape", "cancel" };

    public static void listen(final UUID uuid, final Consumer<String> callback) {
        WAITING_INPUT.put(uuid, callback);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(final AsyncChatEvent event) {
        final Player player = event.getPlayer();
        if (!WAITING_INPUT.containsKey(player.getUniqueId()))
            return;

        event.setCancelled(true);

        final String input = PlainTextComponentSerializer.plainText().serialize(event.message());
        final Consumer<String> callback = WAITING_INPUT.remove(player.getUniqueId());

        if (Arrays.stream(ESCAPE_SEQUENCES).anyMatch(input::equalsIgnoreCase)) {
            player.getScheduler().run(BetterReports.getInstance(), (task) -> {
                Common.resetTitle(player);
                Common.send(player, MainConfig.Values.LANG_CONVERSATION_CANCELLED.getString());
            }, null);
            return;
        }

        // Execute callback on the player's thread context to be safe with UI/Bukkit API
        player.getScheduler().run(BetterReports.getInstance(), (task) -> callback.accept(input), null);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        WAITING_INPUT.remove(event.getPlayer().getUniqueId());
    }
}
