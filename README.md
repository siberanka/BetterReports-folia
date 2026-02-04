# BetterReports

A lightweight, optimized reporting plugin for Minecraft, now fully compatible with **Folia** and modern **Paper** servers utilizing Discord webhooks for player and bug reports!

## 🚀 Key Features

*   **⚡ Folia 1.21.11 Ready**: Fully refactored to support Folia's region-based threading model (`EntityScheduler`, async tasks).
*   **🔒 Safe Input Handling**: Uses modern `AsyncChatEvent` listener to capture report reasons safely without blocking threads or crashing the server.
*   **🧹 Optimized**: Bloat-free. Legacy libraries (`XSeries`) removed for maximum performance using native Bukkit APIs.
*   **📢 Discord Integration**: Directly archive reports to your Discord server via webhooks.
*   **🎨 Customisable**: Highly configurable messages and GUIs.
*   **🛡️ Secure**: JSON/Command injection prevention built-in.

## 📋 Requirements

*   **Server**: Folia 1.21.11 or Paper 1.21+
*   **Java**: JDK 21 or higher

## 🛠️ Building

To build the project locally:

```bash
./gradlew clean build
```

The output jar will be located in `build/libs/`.

---

# About

"BetterReports was coded at first as a small project for a Minecraft server. I then thought that I should submit it as an open source resource on Spigot where we can continue to develop the plugin." - Timmy109

> **Note**: This fork/version has been specifically modernized for 1.21.11 and Folia. Legacy support (1.8-1.20.4) has been dropped in favor of performance and modern API usage.

# License

BetterReports is licensed under the MIT license.
You can find the full license text here: [LICENSE](LICENSE.md)
