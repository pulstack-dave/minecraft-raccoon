package com.raccoon.listener;

import com.raccoon.RaccoonPlugin;
import com.raccoon.config.RaccoonConfig;
import com.raccoon.entity.RaccoonEgg;
import com.raccoon.entity.RaccoonManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

/**
 * Event listener enforcing Raccoon behaviors, egg hatching, human safety, and hunting mechanics.
 */
public class RaccoonListener implements Listener {

    private final RaccoonPlugin plugin;
    private final RaccoonManager manager;
    private final RaccoonEgg eggHelper;
    private final NamespacedKey projectileEggKey;

    public RaccoonListener(RaccoonPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getRaccoonManager();
        this.eggHelper = plugin.getRaccoonEgg();
        this.projectileEggKey = new NamespacedKey(plugin, "raccoon_projectile_egg");
    }

    /**
     * Handles right-clicking blocks with a Raccoon Spawn Egg.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !eggHelper.isRaccoonEgg(item)) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }

        BlockFace face = event.getBlockFace();
        Location spawnLoc = clicked.getRelative(face).getLocation().add(0.5, 0.0, 0.5);

        event.setCancelled(true);

        // Spawn baby raccoon
        Fox raccoon = manager.spawnRaccoon(spawnLoc, true);
        if (raccoon != null) {
            if (spawnLoc.getWorld() != null) {
                try {
                    spawnLoc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, spawnLoc.clone().add(0, 0.5, 0), 15, 0.3, 0.3, 0.3, 0.1);
                } catch (Throwable ignored) {
                }
                try {
                    spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.2f);
                    spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_FOX_SNIFF, 1.0f, 1.4f);
                } catch (Throwable ignored) {
                }
            }

            // Deduct item in survival/adventure
            Player player = event.getPlayer();
            if (player.getGameMode() != GameMode.CREATIVE) {
                item.setAmount(item.getAmount() - 1);
            }
        }
    }

    /**
     * Tags thrown raccoon egg projectiles.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Egg egg && egg.getShooter() instanceof Player player) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();

            if (eggHelper.isRaccoonEgg(mainHand) || eggHelper.isRaccoonEgg(offHand)) {
                egg.getPersistentDataContainer().set(projectileEggKey, PersistentDataType.BYTE, (byte) 1);
            }
        }
    }

    /**
     * Hatches a baby raccoon when a thrown raccoon egg lands.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Egg egg) {
            Byte tag = egg.getPersistentDataContainer().get(projectileEggKey, PersistentDataType.BYTE);
            if (tag != null && tag == 1) {
                Location hitLoc = projectile.getLocation();
                if (hitLoc.getWorld() != null) {
                    manager.spawnRaccoon(hitLoc, true);
                    try {
                        hitLoc.getWorld().spawnParticle(Particle.ITEM_CRACK, hitLoc, 20, 0.2, 0.2, 0.2, 0.05, new ItemStack(Material.EGG));
                        hitLoc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, hitLoc, 10, 0.3, 0.3, 0.3, 0.05);
                    } catch (Throwable ignored) {
                    }
                    try {
                        hitLoc.getWorld().playSound(hitLoc, Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
                        hitLoc.getWorld().playSound(hitLoc, Sound.ENTITY_FOX_SPIT, 1.0f, 1.2f);
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
    }

    /**
     * Handles interacting with raccoons (inspecting stats or feeding).
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Fox raccoon) || !manager.isRaccoon(raccoon)) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItem(event.getHand());

        // Sneak inspection
        if (player.isSneaking()) {
            event.setCancelled(true);
            boolean isBaby = manager.isBabyRaccoon(raccoon);
            int growthPct = manager.getGrowthPercentage(raccoon);
            int kills = manager.getAnimalsEaten(raccoon);
            double hp = raccoon.getHealth();
            double maxHp = 20.0;
            try {
                if (raccoon.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH) != null) {
                    maxHp = raccoon.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
                }
            } catch (Throwable ignored) {
            }

            String status = isBaby ? String.format("§eBaby (§a%d%% §eto adult)", growthPct) : "§6Adult";
            player.sendMessage(String.format("§8[§6🦝 Raccoon§8] §7Status: %s §7| Animals Eaten: §b%d §7| Health: §c%.1f§7/§c%.1f", status, kills, hp, maxHp));
            if (raccoon.getWorld() != null) {
                try {
                    raccoon.getWorld().playSound(raccoon.getLocation(), Sound.ENTITY_FOX_SNIFF, 0.9f, 1.3f);
                } catch (Throwable ignored) {
                }
            }
            return;
        }

        // Feeding
        if (handItem != null && plugin.getRaccoonConfig().isFavoriteFood(handItem.getType())) {
            event.setCancelled(true);
            if (manager.feedRaccoon(raccoon, handItem, player)) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    handItem.setAmount(handItem.getAmount() - 1);
                }
            }
        }
    }

    /**
     * Prevents raccoons from ever targeting players or other raccoons.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Fox) || !manager.isRaccoon(entity)) {
            return;
        }

        LivingEntity target = event.getTarget();
        if (target == null) return;

        // Strictly cancel targeting players
        if (target instanceof Player || manager.isRaccoon(target)) {
            event.setCancelled(true);
            event.setTarget(null);
        }
    }

    /**
     * Ensures raccoons NEVER harm humans, deal damage to animal prey, and flee if hurt by humans.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        // 1. If raccoon attacks a player -> CANCEL completely!
        if (manager.isRaccoon(damager) && victim instanceof Player) {
            event.setCancelled(true);
            return;
        }

        // 2. If raccoon attacks an animal -> apply attack damage and bite effect
        if (manager.isRaccoon(damager) && victim instanceof LivingEntity livingVictim && !(victim instanceof Player)) {
            Fox raccoon = (Fox) damager;
            double damage = manager.isBabyRaccoon(raccoon)
                    ? plugin.getRaccoonConfig().getBabyDamage()
                    : plugin.getRaccoonConfig().getAdultDamage();
            event.setDamage(damage);

            Location loc = victim.getLocation();
            if (loc.getWorld() != null) {
                try {
                    loc.getWorld().playSound(loc, Sound.ENTITY_FOX_BITE, 1.0f, 1.1f);
                } catch (Throwable ignored) {
                }
                try {
                    loc.getWorld().spawnParticle(Particle.CRIT, loc.add(0, 0.4, 0), 6, 0.2, 0.2, 0.2, 0.1);
                } catch (Throwable ignored) {
                }
            }
        }

        // 3. If player attacks a raccoon -> raccoon flees and never attacks back
        if (victim instanceof Fox raccoon && manager.isRaccoon(raccoon) && damager instanceof Player player) {
            if (plugin.getRaccoonConfig().isFleeWhenAttackedByPlayer()) {
                Location rLoc = raccoon.getLocation();
                Location pLoc = player.getLocation();
                Vector fleeVec = rLoc.toVector().subtract(pLoc.toVector()).normalize().setY(0.2).multiply(1.2);
                raccoon.setVelocity(fleeVec);
                if (rLoc.getWorld() != null) {
                    try {
                        raccoon.getWorld().playSound(rLoc, Sound.ENTITY_FOX_SCREECH, 1.0f, 1.2f);
                    } catch (Throwable ignored) {
                    }
                    try {
                        raccoon.getWorld().spawnParticle(Particle.SMOKE_NORMAL, rLoc.add(0, 0.3, 0), 8, 0.2, 0.2, 0.2, 0.05);
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
    }

    /**
     * Handles raccoons eating their animal prey upon death.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim instanceof Player || manager.isRaccoon(victim)) {
            return;
        }

        Player killer = victim.getKiller();
        // Check if victim died to a raccoon
        if (victim.getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent) {
            Entity damager = damageEvent.getDamager();
            if (damager instanceof Fox raccoon && manager.isRaccoon(raccoon)) {
                manager.eatAnimal(raccoon, victim);
            }
        }
    }

    /**
     * Ensures newly bred baby raccoons are properly marked and configured.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityBreed(EntityBreedEvent event) {
        if (manager.isRaccoon(event.getFather()) || manager.isRaccoon(event.getMother())) {
            if (event.getEntity() instanceof Fox baby) {
                manager.initRaccoonData(baby, true);
            }
        }
    }
}
