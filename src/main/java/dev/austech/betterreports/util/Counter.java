/*
 * BetterReports - Counter.java
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
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Counter {
    final File file = new File(BetterReports.getInstance().getDataFolder(), "counter.db");

    @Getter
    private final AtomicInteger globalCounter = new AtomicInteger(1);

    @Getter
    private final AtomicInteger bugCounter = new AtomicInteger(1);

    @Getter
    private final AtomicInteger playerCounter = new AtomicInteger(1);

    public void incrementBug() {
        globalCounter.incrementAndGet();
        bugCounter.incrementAndGet();
        save();
    }

    public void incrementPlayer() {
        globalCounter.incrementAndGet();
        playerCounter.incrementAndGet();
        save();
    }

    private void save() {
        Bukkit.getAsyncScheduler().runNow(BetterReports.getInstance(), (task) -> {
            try {
                final String str = "G: " + globalCounter.get() + "\nB: " + bugCounter.get() + "\nP: "
                        + playerCounter.get();
                Files.write(file.toPath(), str.getBytes(StandardCharsets.UTF_8));
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void load() {
        if (!file.exists()) {
            save();
            return;
        }

        try (final Stream<String> lines = Files.lines(file.toPath())) {
            final List<String> data = lines.map(str -> str.substring(3)).collect(Collectors.toList());

            globalCounter.set(Integer.parseInt(data.get(0)));
            bugCounter.set(Integer.parseInt(data.get(1)));
            playerCounter.set(Integer.parseInt(data.get(2)));

        } catch (final Exception exception) {
            globalCounter.set(-1);
            bugCounter.set(-1);
            playerCounter.set(-1);

            exception.printStackTrace();
        }
    }
}
