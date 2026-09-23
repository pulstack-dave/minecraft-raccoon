package com.raccoon.config;

import com.raccoon.RaccoonPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages configuration settings for the Raccoon plugin.
 */
public class RaccoonConfig {

    private final RaccoonPlugin plugin;

    // Egg settings
    private String eggDisplayName;
    private List<String> eggLore;
    private boolean eggCraftable;

    // Raccoon Names & Display
    private String babyName;
    private String adultName;
    private boolean nameVisible;

    // Growth & Stats
    private int growthTimeSeconds;
    private int growthReductionPerFeedSeconds;
    private int growthReductionPerKillSeconds;
    private double babyMaxHealth;
    private double adultMaxHealth;
    private double babyDamage;
    private double adultDamage;
    private double babySpeed;
    private double adultSpeed;

    // AI & Hunting
    private double huntRadius;
    private int huntIntervalTicks;
    private boolean neverHarmPlayers;
    private boolean fleeWhenAttackedByPlayer;
    private boolean washFoodInWater;
    private boolean rummageContainers;
    private boolean pickUpItems;

    // Prey & Foods
    private boolean targetAllAnimals;
    private final Set<EntityType> allowedPrey = new HashSet<>();
    private final Set<Material> favoriteFoods = new HashSet<>();

    public RaccoonConfig(RaccoonPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads or reloads all configuration values from config.yml.
     */
    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // Egg
        this.eggDisplayName = color(config.getString("egg.display-name", "&6&lRaccoon Spawn Egg"));
        this.eggLore = config.getStringList("egg.lore").stream().map(this::color).toList();
        this.eggCraftable = config.getBoolean("egg.craftable", true);

        // Names
        this.babyName = color(config.getString("raccoon.name-baby", "&eBaby Raccoon 🦝"));
        this.adultName = color(config.getString("raccoon.name-adult", "&6Raccoon 🦝"));
        this.nameVisible = config.getBoolean("raccoon.custom-name-visible", true);

        // Growth & Stats
        this.growthTimeSeconds = config.getInt("raccoon.growth-time-seconds", 300);
        this.growthReductionPerFeedSeconds = config.getInt("raccoon.growth-reduction-per-feed-seconds", 25);
        this.growthReductionPerKillSeconds = config.getInt("raccoon.growth-reduction-per-kill-seconds", 60);
        this.babyMaxHealth = config.getDouble("raccoon.baby-max-health", 10.0);
        this.adultMaxHealth = config.getDouble("raccoon.adult-max-health", 20.0);
        this.babyDamage = config.getDouble("raccoon.baby-damage", 2.0);
        this.adultDamage = config.getDouble("raccoon.adult-damage", 5.0);
        this.babySpeed = config.getDouble("raccoon.baby-speed", 1.25);
        this.adultSpeed = config.getDouble("raccoon.adult-speed", 1.15);

        // AI & Hunting
        this.huntRadius = config.getDouble("raccoon.hunt-radius", 16.0);
        this.huntIntervalTicks = config.getInt("raccoon.hunt-interval-ticks", 20);
        this.neverHarmPlayers = config.getBoolean("raccoon.never-harm-players", true);
        this.fleeWhenAttackedByPlayer = config.getBoolean("raccoon.flee-from-players-when-attacked", true);
        this.washFoodInWater = config.getBoolean("raccoon.wash-food-in-water", true);
        this.rummageContainers = config.getBoolean("raccoon.rummage-containers", true);
        this.pickUpItems = config.getBoolean("raccoon.pick-up-items", true);

        // Prey
        allowedPrey.clear();
        this.targetAllAnimals = config.getBoolean("hunting.target-all-animals", true);
        List<String> preyList = config.getStringList("hunting.allowed-prey");
        for (String preyName : preyList) {
            try {
                EntityType type = EntityType.valueOf(preyName.toUpperCase());
                allowedPrey.add(type);
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Foods
        favoriteFoods.clear();
        List<String> foodList = config.getStringList("raccoon.favorite-foods");
        for (String foodName : foodList) {
            try {
                Material mat = Material.valueOf(foodName.toUpperCase());
                favoriteFoods.add(mat);
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Add default foods if empty
        if (favoriteFoods.isEmpty()) {
            favoriteFoods.add(Material.SWEET_BERRIES);
            favoriteFoods.add(Material.GLOW_BERRIES);
            favoriteFoods.add(Material.APPLE);
            favoriteFoods.add(Material.BREAD);
            favoriteFoods.add(Material.COOKIE);
            favoriteFoods.add(Material.COD);
            favoriteFoods.add(Material.SALMON);
            favoriteFoods.add(Material.COOKED_COD);
            favoriteFoods.add(Material.COOKED_SALMON);
            favoriteFoods.add(Material.CHICKEN);
            favoriteFoods.add(Material.COOKED_CHICKEN);
            favoriteFoods.add(Material.RABBIT);
            favoriteFoods.add(Material.COOKED_RABBIT);
            favoriteFoods.add(Material.GOLDEN_CARROT);
        }
    }

    private String color(String text) {
        if (text == null) return "";
        return text.replace('&', '§');
    }

    public String getEggDisplayName() {
        return eggDisplayName;
    }

    public List<String> getEggLore() {
        return eggLore;
    }

    public boolean isEggCraftable() {
        return eggCraftable;
    }

    public String getBabyName() {
        return babyName;
    }

    public String getAdultName() {
        return adultName;
    }

    public boolean isNameVisible() {
        return nameVisible;
    }

    public int getGrowthTimeSeconds() {
        return growthTimeSeconds;
    }

    public int getGrowthReductionPerFeedSeconds() {
        return growthReductionPerFeedSeconds;
    }

    public int getGrowthReductionPerKillSeconds() {
        return growthReductionPerKillSeconds;
    }

    public double getBabyMaxHealth() {
        return babyMaxHealth;
    }

    public double getAdultMaxHealth() {
        return adultMaxHealth;
    }

    public double getBabyDamage() {
        return babyDamage;
    }

    public double getAdultDamage() {
        return adultDamage;
    }

    public double getBabySpeed() {
        return babySpeed;
    }

    public double getAdultSpeed() {
        return adultSpeed;
    }

    public double getHuntRadius() {
        return huntRadius;
    }

    public int getHuntIntervalTicks() {
        return huntIntervalTicks;
    }

    public boolean isNeverHarmPlayers() {
        return neverHarmPlayers;
    }

    public boolean isFleeWhenAttackedByPlayer() {
        return fleeWhenAttackedByPlayer;
    }

    public boolean isWashFoodInWater() {
        return washFoodInWater;
    }

    public boolean isRummageContainers() {
        return rummageContainers;
    }

    public boolean isPickUpItems() {
        return pickUpItems;
    }

    public boolean isTargetAllAnimals() {
        return targetAllAnimals;
    }

    public Set<EntityType> getAllowedPrey() {
        return allowedPrey;
    }

    public Set<Material> getFavoriteFoods() {
        return favoriteFoods;
    }

    public boolean isFavoriteFood(Material material) {
        return favoriteFoods.contains(material);
    }
}
