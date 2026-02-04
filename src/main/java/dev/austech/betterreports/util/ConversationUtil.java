/*
 * BetterReports - ConversationUtil.java
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
import lombok.experimental.UtilityClass;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class ConversationUtil {
    // Legacy ConversationFactory removed due to Folia incompatibilities (cause of
    // UnsupportedOperationException in broadcastChatMessage)

    public void run(final Player player, final Supplier<String> message, final Function<String, Prompt> function) {
        // Send the prompt message
        Common.send(player, message.get());

        // Register listener
        InputListener.listen(player.getUniqueId(), (input) -> {
            Prompt prompt = function.apply(input);
            // If the prompt returns null or END_OF_CONVERSATION, we end.
            // If it recursively calls run() (which the original code seemingly didn't do
            // directly but via RerunPrompt logic), we need to handle that.
            // Looking at usage in ReportMenu, it handles re-prompting or saving.
            // The original 'function' returned a Prompt.
            // In ReportMenu:
            // return
            // MainConfig.Values.LANG_QUESTION_BUG_MESSAGE.getPlaceholderString(creator);
            // }, s -> { ... return Prompt.END_OF_CONVERSATION; });

            // The original code had a recursive 'RerunPrompt' check.
            // Since we are simplifying, we assume the callback handles what to do next.
            // However, the original 'function' interface expects to return a Prompt.
            // We should respect that if we want minimal actionable changes in the caller.

            if (prompt instanceof RerunPrompt) {
                // Rerun - technically we should just re-register listner?
                // But the logic in original was: if RerunPrompt, return 'this' (the string
                // prompt).
                // effectively restarting the cycle.
                run(player, message, function);
            }
        });
    }

    // Keep RerunPrompt class to avoid breaking external imports (if any), though it
    // might be unused now or handled differently.
    public static class RerunPrompt extends StringPrompt {
        public RerunPrompt() {
        }

        @Override
        public String getPromptText(final ConversationContext context) {
            return null;
        }

        @Override
        public Prompt acceptInput(final ConversationContext context, final String input) {
            return null;
        }
    }
}
