package com.raccoon.entity;

import com.raccoon.RaccoonPlugin;
import com.raccoon.config.RaccoonConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages Raccoon entity creation, state, growth lifecycle, and feeding/eating mechanics.
 */
public class RaccoonManager {

    private final RaccoonPlugin plugin;
    private final NamespacedKey isRaccoonKey;
    private final NamespacedKey isBabyKey;
    private final NamespacedKey growthTicksKey;
    private final NamespacedKey maxGrowthTicksKey;
    private final NamespacedKey animalsEatenKey;
    private final NamespacedKey washCooldownKey;

    public RaccoonManager(RaccoonPlugin plugin) {
        this.plugin = plugin;
        this.isRaccoonKey = new NamespacedKey(plugin, "is_raccoon");
        this.isBabyKey = new NamespacedKey(plugin, "is_baby");
        this.growthTicksKey = new NamespacedKey(plugin, "growth_ticks");
        this.maxGrowthTicksKey = new NamespacedKey(plugin, "max_growth_ticks");
        this.animalsEatenKey = new NamespacedKey(plugin, "animals_eaten");
        this.washCooldownKey = new NamespacedKey(plugin, "wash_cooldown");
    }

    /**
     * Spawns a raccoon at the given location.
     *
     * @param location location to spawn
     * @param isBaby   true if the raccoon should start as a baby
     * @return the spawned Fox entity configured as a raccoon
     */
    public Fox spawnRaccoon(Location location, boolean isBaby) {
        if (location == null || location.getWorld() == null) {
            return null;
        }

        Fox raccoon = (Fox) location.getWorld().spawnEntity(location, EntityType.FOX);
        initRaccoonData(raccoon, isBaby);
        return raccoon;
    }

    /**
     * Initializes the PersistentDataContainer and attributes of a raccoon entity.
     */
    public void initRaccoonData(Fox raccoon, boolean isBaby) {
        RaccoonConfig config = plugin.getRaccoonConfig();
        PersistentDataContainer pdc = raccoon.getPersistentDataContainer();

        pdc.set(isRaccoonKey, PersistentDataType.BYTE, (byte) 1);
        pdc.set(isBabyKey, PersistentDataType.BYTE, (byte) (isBaby ? 1 : 0));
        pdc.set(growthTicksKey, PersistentDataType.INTEGER, 0);
        pdc.set(maxGrowthTicksKey, PersistentDataType.INTEGER, config.getGrowthTimeSeconds() * 20);
        pdc.set(animalsEatenKey, PersistentDataType.INTEGER, 0);
        pdc.set(washCooldownKey, PersistentDataType.LONG, 0L);

        try {
            raccoon.setRemoveWhenFarAway(false);
        } catch (Throwable ignored) {
        }
        try {
            raccoon.setCanPickupItems(config.isPickUpItems());
        } catch (Throwable ignored) {
        }

        if (isBaby) {
            applyBabyState(raccoon);
        } else {
            applyAdultState(raccoon);
        }
    }

    /**
     * Applies visual and attribute modifications for a baby raccoon.
     */
    public void applyBabyState(Fox raccoon) {
        RaccoonConfig config = plugin.getRaccoonConfig();
        raccoon.setBaby();
        raccoon.setAge(-24000);
        try {
            raccoon.setAgeLock(true);
        } catch (Throwable ignored) {
        }

        raccoon.setCustomName(config.getBabyName());
        raccoon.setCustomNameVisible(config.isNameVisible());

        try {
            AttributeInstance healthAttr = raccoon.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (healthAttr != null) {
                healthAttr.setBaseValue(config.getBabyMaxHealth());
                raccoon.setHealth(config.getBabyMaxHealth());
            }
        } catch (Throwable ignored) {
        }

        try {
            AttributeInstance speedAttr = raccoon.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (speedAttr != null) {
                speedAttr.setBaseValue(0.30 * config.getBabySpeed());
            }
        } catch (Throwable ignored) {
        }

        try {
            AttributeInstance attackAttr = raccoon.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            if (attackAttr != null) {
                attackAttr.setBaseValue(config.getBabyDamage());
            }
        } catch (Throwable ignored) {
        }

        raccoon.getPersistentDataContainer().set(isBabyKey, PersistentDataType.BYTE, (byte) 1);
    }

    /**
     * Applies visual and attribute modifications for an adult raccoon.
     */
    public void applyAdultState(Fox raccoon) {
        RaccoonConfig config = plugin.getRaccoonConfig();
        raccoon.setAdult();
        raccoon.setAge(0);
        try {
            raccoon.setAgeLock(false);
        } catch (Throwable ignored) {
        }

        raccoon.setCustomName(config.getAdultName());
        raccoon.setCustomNameVisible(config.isNameVisible());

        try {
            AttributeInstance healthAttr = raccoon.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (healthAttr != null) {
                healthAttr.setBaseValue(config.getAdultMaxHealth());
                raccoon.setHealth(config.getAdultMaxHealth());
            }
        } catch (Throwable ignored) {
        }

        try {
            AttributeInstance speedAttr = raccoon.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (speedAttr != null) {
                speedAttr.setBaseValue(0.30 * config.getAdultSpeed());
            }
        } catch (Throwable ignored) {
        }

        try {
            AttributeInstance attackAttr = raccoon.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            if (attackAttr != null) {
                attackAttr.setBaseValue(config.getAdultDamage());
            }
        } catch (Throwable ignored) {
        }

        raccoon.getPersistentDataContainer().set(isBabyKey, PersistentDataType.BYTE, (byte) 0);
    }

    /**
     * Transitions a baby raccoon into a full adult raccoon with visual effects and sounds.
     */
    public void growToAdult(Fox raccoon) {
        if (!isRaccoon(raccoon)) return;

        applyAdultState(raccoon);

        Location loc = raccoon.getLocation();
        if (loc.getWorld() != null) {
            try {
                loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(0, 0.5, 0), 20, 0.4, 0.4, 0.4, 0.1);
                loc.getWorld().spawnParticle(Particle.HEART, loc, 8, 0.3, 0.3, 0.3, 0.05);
            } catch (Throwable ignored) {
            }
            try {
                loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                loc.getWorld().playSound(loc, Sound.ENTITY_FOX_SPIT, 1.0f, 1.0f);
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Accelerates the growth progress of a baby raccoon.
     *
     * @param raccoon  the baby raccoon
     * @param tickBonus ticks to add to growth counter
     */
    public void advanceGrowth(Fox raccoon, int tickBonus) {
        if (!isBabyRaccoon(raccoon)) return;

        PersistentDataContainer pdc = raccoon.getPersistentDataContainer();
        int currentTicks = pdc.getOrDefault(growthTicksKey, PersistentDataType.INTEGER, 0);
        int maxTicks = pdc.getOrDefault(maxGrowthTicksKey, PersistentDataType.INTEGER, plugin.getRaccoonConfig().getGrowthTimeSeconds() * 20);

        currentTicks += tickBonus;
        pdc.set(growthTicksKey, PersistentDataType.INTEGER, currentTicks);

        if (currentTicks >= maxTicks) {
            growToAdult(raccoon);
        } else {
            // Happy particles for feeding/growing
            Location loc = raccoon.getLocation();
            if (loc.getWorld() != null) {
                try {
                    loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(0, 0.4, 0), 6, 0.2, 0.2, 0.2, 0.05);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /**
     * Handles a player feeding the raccoon a food item.
     *
     * @param raccoon  the raccoon
     * @param foodItem the food item offered
     * @param player   the player offering the food
     * @return true if the raccoon accepted and ate the food
     */
    public boolean feedRaccoon(Fox raccoon, ItemStack foodItem, Player player) {
        if (!isRaccoon(raccoon) || foodItem == null) return false;

        RaccoonConfig config = plugin.getRaccoonConfig();
        if (!config.isFavoriteFood(foodItem.getType())) {
            return false;
        }

        // Heal raccoon
        double maxHealth = 20.0;
        try {
            AttributeInstance maxHealthAttr = raccoon.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (maxHealthAttr != null) maxHealth = maxHealthAttr.getValue();
        } catch (Throwable ignored) {
        }
        try {
            raccoon.setHealth(Math.min(maxHealth, raccoon.getHealth() + 3.0));
        } catch (Throwable ignored) {
        }

        // Effects
        Location loc = raccoon.getLocation();
        if (loc.getWorld() != null) {
            try {
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EAT, 1.0f, 1.2f);
                loc.getWorld().playSound(loc, Sound.ENTITY_FOX_EAT, 1.0f, 1.3f);
            } catch (Throwable ignored) {
            }
            try {
                loc.getWorld().spawnParticle(Particle.ITEM_CRACK, loc.clone().add(0, 0.3, 0), 12, 0.2, 0.2, 0.2, 0.05, foodItem);
                loc.getWorld().spawnParticle(Particle.HEART, loc.clone().add(0, 0.2, 0), 4, 0.3, 0.3, 0.3, 0.02);
            } catch (Throwable ignored) {
            }
        }

        // Advance growth if baby
        if (isBabyRaccoon(raccoon)) {
            advanceGrowth(raccoon, config.getGrowthReductionPerFeedSeconds() * 20);
        }

        // Check food washing near water
        if (config.isWashFoodInWater()) {
            checkAndWashFood(raccoon);
        }

        return true;
    }

    /**
     * Handles a raccoon killing and eating an animal prey.
     *
     * @param raccoon the raccoon predator
     * @param animal  the animal victim
     */
    public void eatAnimal(Fox raccoon, LivingEntity animal) {
        if (!isRaccoon(raccoon) || animal == null) return;

        RaccoonConfig config = plugin.getRaccoonConfig();
        PersistentDataContainer pdc = raccoon.getPersistentDataContainer();

        // Increment kill count
        int kills = pdc.getOrDefault(animalsEatenKey, PersistentDataType.INTEGER, 0) + 1;
        pdc.set(animalsEatenKey, PersistentDataType.INTEGER, kills);

        // Fully heal raccoon
        double maxHealth = 20.0;
        try {
            AttributeInstance maxHealthAttr = raccoon.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (maxHealthAttr != null) maxHealth = maxHealthAttr.getValue();
        } catch (Throwable ignored) {
        }
        try {
            raccoon.setHealth(maxHealth);
        } catch (Throwable ignored) {
        }

        // Effects
        Location loc = raccoon.getLocation();
        if (loc.getWorld() != null) {
            try {
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
                loc.getWorld().playSound(loc, Sound.ENTITY_FOX_EAT, 1.0f, 1.1f);
            } catch (Throwable ignored) {
            }
            try {
                loc.getWorld().spawnParticle(Particle.ITEM_CRACK, loc.clone().add(0, 0.3, 0), 15, 0.3, 0.3, 0.3, 0.05, new ItemStack(Material.BEEF));
                loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 8, 0.3, 0.3, 0.3, 0.05);
            } catch (Throwable ignored) {
            }
        }

        // Growth boost if baby
        if (isBabyRaccoon(raccoon)) {
            advanceGrowth(raccoon, config.getGrowthReductionPerKillSeconds() * 20);
        }

        // Washing food behavior
        if (config.isWashFoodInWater()) {
            checkAndWashFood(raccoon);
        }
    }

    /**
     * Simulates the iconic raccoon behavior of washing food if near water blocks.
     */
    public void checkAndWashFood(Fox raccoon) {
        Location loc = raccoon.getLocation();
        boolean nearWater = false;

        for (int dx = -2; dx <= 2 && !nearWater; dx++) {
            for (int dy = -1; dy <= 1 && !nearWater; dy++) {
                for (int dz = -2; dz <= 2 && !nearWater; dz++) {
                    Material mat = loc.clone().add(dx, dy, dz).getBlock().getType();
                    if (mat == Material.WATER || mat == Material.SEAGRASS || mat == Material.KELP) {
                        nearWater = true;
                    }
                }
            }
        }

        if (nearWater && loc.getWorld() != null) {
            try {
                loc.getWorld().spawnParticle(Particle.WATER_SPLASH, loc.clone().add(0, 0.2, 0), 15, 0.3, 0.2, 0.3, 0.1);
                loc.getWorld().spawnParticle(Particle.WATER_BUBBLE, loc.clone().add(0, 0.2, 0), 10, 0.3, 0.2, 0.3, 0.05);
            } catch (Throwable ignored) {
            }
            try {
                loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_SPLASH, 0.8f, 1.4f);
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Checks if an entity is a custom raccoon.
     */
    public boolean isRaccoon(Entity entity) {
        if (!(entity instanceof Fox)) {
            return false;
        }
        Byte tag = entity.getPersistentDataContainer().get(isRaccoonKey, PersistentDataType.BYTE);
        return tag != null && tag == 1;
    }

    /**
     * Checks if an entity is a baby raccoon.
     */
    public boolean isBabyRaccoon(Entity entity) {
        if (!isRaccoon(entity)) {
            return false;
        }
        Byte tag = entity.getPersistentDataContainer().get(isBabyKey, PersistentDataType.BYTE);
        return tag != null && tag == 1;
    }

    /**
     * Returns the growth percentage (0-100) of a baby raccoon.
     */
    public int getGrowthPercentage(Fox raccoon) {
        if (!isBabyRaccoon(raccoon)) return 100;

        PersistentDataContainer pdc = raccoon.getPersistentDataContainer();
        int currentTicks = pdc.getOrDefault(growthTicksKey, PersistentDataType.INTEGER, 0);
        int maxTicks = pdc.getOrDefault(maxGrowthTicksKey, PersistentDataType.INTEGER, plugin.getRaccoonConfig().getGrowthTimeSeconds() * 20);

        if (maxTicks <= 0) return 100;
        return Math.min(100, (int) ((currentTicks / (double) maxTicks) * 100));
    }

    /**
     * Returns the number of animals eaten by this raccoon.
     */
    public int getAnimalsEaten(Fox raccoon) {
        if (!isRaccoon(raccoon)) return 0;
        return raccoon.getPersistentDataContainer().getOrDefault(animalsEatenKey, PersistentDataType.INTEGER, 0);
    }

    /**
     * Finds all active raccoons across loaded worlds.
     */
    public List<Fox> getAllRaccoons() {
        List<Fox> list = new ArrayList<>();
        plugin.getServer().getWorlds().forEach(world -> {
            for (Fox fox : world.getEntitiesByClass(Fox.class)) {
                if (isRaccoon(fox) && fox.isValid()) {
                    list.add(fox);
                }
            }
        });
        return list;
    }

    public NamespacedKey getIsRaccoonKey() {
        return isRaccoonKey;
    }

    public NamespacedKey getIsBabyKey() {
        return isBabyKey;
    }

    public NamespacedKey getGrowthTicksKey() {
        return growthTicksKey;
    }

    public NamespacedKey getMaxGrowthTicksKey() {
        return maxGrowthTicksKey;
    }

    public NamespacedKey getAnimalsEatenKey() {
        return animalsEatenKey;
    }
}
