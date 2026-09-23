package com.raccoon.entity;

import com.raccoon.RaccoonPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Handles Raccoon Spawn Egg generation, identification, and crafting recipes.
 */
public class RaccoonEgg {

    private final RaccoonPlugin plugin;
    private final NamespacedKey eggKey;
    private final NamespacedKey recipeKey;

    public RaccoonEgg(RaccoonPlugin plugin) {
        this.plugin = plugin;
        this.eggKey = new NamespacedKey(plugin, "raccoon_egg_item");
        this.recipeKey = new NamespacedKey(plugin, "raccoon_egg_recipe");
    }

    /**
     * Creates an ItemStack representing the custom Raccoon Spawn Egg.
     *
     * @param amount number of eggs in stack
     * @return the raccoon egg ItemStack
     */
    public ItemStack createEgg(int amount) {
        ItemStack item = new ItemStack(Material.EGG, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getRaccoonConfig().getEggDisplayName());
            List<String> lore = plugin.getRaccoonConfig().getEggLore();
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }
            meta.getPersistentDataContainer().set(eggKey, PersistentDataType.BYTE, (byte) 1);
            meta.setCustomModelData(7222001); // Unique custom model data for resource packs if desired
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Checks whether an ItemStack is a custom Raccoon Spawn Egg.
     *
     * @param item ItemStack to inspect
     * @return true if the item is a raccoon egg
     */
    public boolean isRaccoonEgg(ItemStack item) {
        if (item == null || item.getType() != Material.EGG || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        Byte tag = meta.getPersistentDataContainer().get(eggKey, PersistentDataType.BYTE);
        if (tag != null && tag == 1) {
            return true;
        }

        // Fallback check on display name if PDC is stripped
        String expectedName = plugin.getRaccoonConfig().getEggDisplayName();
        return meta.hasDisplayName() && meta.getDisplayName().equals(expectedName);
    }

    /**
     * Registers the crafting recipe for Raccoon Spawn Egg if enabled in configuration.
     */
    public void registerRecipe() {
        if (!plugin.getRaccoonConfig().isEggCraftable()) {
            return;
        }
        try {
            ShapelessRecipe recipe = new ShapelessRecipe(recipeKey, createEgg(1));
            recipe.addIngredient(Material.EGG);
            recipe.addIngredient(Material.SWEET_BERRIES);
            recipe.addIngredient(Material.BREAD);
            recipe.addIngredient(Material.COD);
            plugin.getServer().addRecipe(recipe);
        } catch (Exception ignored) {
            // Recipe might already be registered on reload
        }
    }

    public NamespacedKey getEggKey() {
        return eggKey;
    }
}
