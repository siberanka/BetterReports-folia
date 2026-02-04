package dev.austech.betterreports.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class StackBuilder {
    private final ItemStack item;
    private String type;

    public static StackBuilder create(final Material material) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }

        return new StackBuilder(new ItemStack(material));
    }

    public static StackBuilder from(final ItemStack item) {
        return new StackBuilder(item);
    }

    public void applyMeta(final Consumer<ItemMeta> function) {
        final ItemMeta meta = item.getItemMeta();
        function.accept(meta);
        item.setItemMeta(meta);
    }

    public StackBuilder type(final String type) {
        this.type = type;
        return this;
    }

    public StackBuilder amount(final int amount) {
        item.setAmount(amount);
        return this;
    }

    public StackBuilder name(final String name) {
        if (name == null)
            return this;
        applyMeta(meta -> meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name)));
        return this;
    }

    public StackBuilder lore(final String lore) {
        if (lore == null || lore.isEmpty())
            return this;
        return lore(lore.split("\n"));
    }

    public StackBuilder lore(final String... lore) {
        if (lore == null)
            return this;
        return lore(Arrays.asList(lore));
    }

    public StackBuilder lore(final List<String> lore) {
        if (lore == null)
            return this;
        final List<String> newList = lore.stream().map(str -> ChatColor.translateAlternateColorCodes('&', str))
                .collect(Collectors.toList());
        applyMeta(meta -> meta.setLore(newList));
        return this;
    }

    public StackBuilder enchant(final Enchantment enchantment, final int level) {
        applyMeta(meta -> meta.addEnchant(enchantment, level, true));

        return this;
    }

    public StackBuilder unEnchant(final Enchantment enchantment) {
        applyMeta(meta -> meta.removeEnchant(enchantment));

        return this;
    }

    public StackBuilder glow(final boolean bool) {
        if (bool)
            glow();
        else
            removeGlow();
        return this;
    }

    public StackBuilder glow() {
        Enchantment enc = Enchantment.getByName("unbreaking");
        if (enc != null)
            enchant(enc, 1);
        applyMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        return this;
    }

    public StackBuilder removeGlow() {
        Enchantment enc = Enchantment.getByName("unbreaking");
        if (enc != null)
            unEnchant(enc);
        applyMeta(meta -> meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS));
        return this;
    }

    public static StackBuilder skull(final String owner) {
        final StackBuilder builder = create(Material.PLAYER_HEAD); // 1.13+ Material
        builder.applyMeta(meta -> {
            final SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(owner);
        });

        return builder;
    }

    public ItemStack build() {
        return item;
    }
}
