package com.raccoon.ai;

import com.raccoon.RaccoonPlugin;
import com.raccoon.config.RaccoonConfig;
import com.raccoon.entity.RaccoonManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Scheduled background AI task managing Raccoon life cycles, animal hunting, target validation,
 * and environmental interactions.
 */
public class RaccoonAITask extends BukkitRunnable {

    private final RaccoonPlugin plugin;
    private final RaccoonManager manager;

    public RaccoonAITask(RaccoonPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getRaccoonManager();
    }

    @Override
    public void run() {
        RaccoonConfig config = plugin.getRaccoonConfig();
        int intervalTicks = config.getHuntIntervalTicks();
        double radius = config.getHuntRadius();

        List<Fox> raccoons = manager.getAllRaccoons();
        for (Fox raccoon : raccoons) {
            if (!raccoon.isValid() || raccoon.isDead()) {
                continue;
            }

            // 1. Safety check: NEVER target humans / players
            LivingEntity currentTarget = raccoon.getTarget();
            if (currentTarget instanceof Player || (currentTarget != null && manager.isRaccoon(currentTarget))) {
                raccoon.setTarget(null);
                currentTarget = null;
            }

            // 2. Growth loop for baby raccoons
            if (manager.isBabyRaccoon(raccoon)) {
                manager.advanceGrowth(raccoon, intervalTicks);
            }

            // 3. Prey acquisition (Hunt other animals)
            if (currentTarget == null || currentTarget.isDead() || !currentTarget.isValid()) {
                LivingEntity prey = findNearestPrey(raccoon, radius);
                if (prey != null) {
                    raccoon.setTarget(prey);
                    currentTarget = prey;
                }
            }

            // 4. Hunting leap / attack assistance
            if (currentTarget != null && currentTarget.isValid()) {
                double distSq = raccoon.getLocation().distanceSquared(currentTarget.getLocation());
                boolean onGround = true;
                try {
                    onGround = raccoon.isOnGround();
                } catch (Throwable ignored) {
                }

                if (distSq > 4.0 && distSq < 36.0 && onGround) {
                    // Pounce / rush towards prey
                    try {
                        Vector dir = currentTarget.getLocation().toVector().subtract(raccoon.getLocation().toVector()).normalize();
                        dir.setY(0.25);
                        dir.multiply(0.85);
                        raccoon.setVelocity(dir);
                    } catch (Throwable ignored) {
                    }
                    if (raccoon.getWorld() != null) {
                        try {
                            raccoon.getWorld().playSound(raccoon.getLocation(), Sound.ENTITY_FOX_BITE, 0.8f, 1.2f);
                        } catch (Throwable ignored) {
                        }
                    }
                }
            }

            // 5. Environmental interactions: Rummaging and Food Pickup
            handleEnvironmentalQuirks(raccoon, config);
        }
    }

    /**
     * Scans surrounding area for the closest valid animal prey.
     */
    private LivingEntity findNearestPrey(Fox raccoon, double radius) {
        RaccoonConfig config = plugin.getRaccoonConfig();
        LivingEntity nearest = null;
        double nearestDistSq = radius * radius;

        for (Entity entity : raccoon.getNearbyEntities(radius, radius / 2.0, radius)) {
            if (!(entity instanceof LivingEntity living) || !living.isValid() || living.isDead()) {
                continue;
            }

            // Never hunt players or fellow raccoons
            if (living instanceof Player || manager.isRaccoon(living)) {
                continue;
            }

            // Check if entity is valid prey
            boolean isValidPrey = false;
            if (config.isTargetAllAnimals() && living instanceof Animals) {
                isValidPrey = true;
            } else if (config.getAllowedPrey().contains(living.getType())) {
                isValidPrey = true;
            }

            if (isValidPrey) {
                double distSq = raccoon.getLocation().distanceSquared(living.getLocation());
                if (distSq < nearestDistSq) {
                    nearestDistSq = distSq;
                    nearest = living;
                }
            }
        }
        return nearest;
    }

    /**
     * Handles food picking up and container rummaging quirks.
     */
    private void handleEnvironmentalQuirks(Fox raccoon, RaccoonConfig config) {
        Location loc = raccoon.getLocation();

        // Check dropped food items
        if (config.isPickUpItems()) {
            for (Entity entity : raccoon.getNearbyEntities(3.0, 2.0, 3.0)) {
                if (entity instanceof Item item && item.isValid() && !item.isDead()) {
                    ItemStack stack = item.getItemStack();
                    if (config.isFavoriteFood(stack.getType())) {
                        // Raccoon eats the dropped food
                        manager.feedRaccoon(raccoon, stack, null);
                        if (stack.getAmount() > 1) {
                            stack.setAmount(stack.getAmount() - 1);
                            item.setItemStack(stack);
                        } else {
                            item.remove();
                        }
                        break;
                    }
                }
            }
        }

        // Check container sniffing / rummaging
        if (config.isRummageContainers() && loc.getWorld() != null) {
            Block blockBelow = loc.getBlock();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Block nearby = blockBelow.getRelative(dx, 0, dz);
                    String blockName = nearby.getType().name();
                    if (blockName.contains("COMPOSTER") || blockName.contains("BARREL") || blockName.contains("CHEST") || blockName.contains("CAULDRON")) {
                        if (Math.random() < 0.2) {
                            try {
                                loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, nearby.getLocation().add(0.5, 0.8, 0.5), 4, 0.2, 0.2, 0.2, 0.01);
                            } catch (Throwable ignored) {
                            }
                            try {
                                loc.getWorld().playSound(nearby.getLocation(), Sound.ENTITY_FOX_SNIFF, 0.7f, 1.3f);
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                }
            }
        }
    }
}
