package com.raccoon;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.raccoon.entity.RaccoonManager;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RaccoonListenerTest {

    private ServerMock server;
    private RaccoonPlugin plugin;
    private RaccoonManager manager;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(RaccoonPlugin.class);
        manager = plugin.getRaccoonManager();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testRaccoonNeverTargetsPlayer() {
        Location loc = new Location(server.addSimpleWorld("world"), 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, false);
        PlayerMock player = server.addPlayer();

        EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(raccoon, player, EntityTargetLivingEntityEvent.TargetReason.CUSTOM);
        server.getPluginManager().callEvent(event);

        assertTrue(event.isCancelled());
        assertNull(event.getTarget());
    }

    @Test
    public void testRaccoonNeverHarmsPlayer() {
        Location loc = new Location(server.addSimpleWorld("world"), 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, false);
        PlayerMock player = server.addPlayer();

        @SuppressWarnings("deprecation")
        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(
                raccoon, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 5.0
        );
        server.getPluginManager().callEvent(damageEvent);

        assertTrue(damageEvent.isCancelled());
    }

    @Test
    public void testRaccoonDamagesAnimals() {
        Location loc = new Location(server.addSimpleWorld("world"), 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, false);
        Chicken chicken = (Chicken) loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);

        @SuppressWarnings("deprecation")
        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(
                raccoon, chicken, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0
        );
        server.getPluginManager().callEvent(damageEvent);

        assertFalse(damageEvent.isCancelled());
        assertEquals(plugin.getRaccoonConfig().getAdultDamage(), damageEvent.getDamage());
    }

    @Test
    public void testSneakInspectRaccoon() {
        Location loc = new Location(server.addSimpleWorld("world"), 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, true);
        PlayerMock player = server.addPlayer();
        player.setSneaking(true);

        PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, raccoon, EquipmentSlot.HAND);
        server.getPluginManager().callEvent(event);

        assertTrue(event.isCancelled());
        String msg = player.nextMessage();
        assertNotNull(msg);
        assertTrue(msg.contains("Raccoon"));
        assertTrue(msg.contains("Baby"));
    }

    @Test
    public void testFeedingRaccoonWithItem() {
        Location loc = new Location(server.addSimpleWorld("world"), 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, true);
        PlayerMock player = server.addPlayer();
        ItemStack berries = new ItemStack(org.bukkit.Material.SWEET_BERRIES, 2);
        player.getInventory().setItemInMainHand(berries);

        PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, raccoon, EquipmentSlot.HAND);
        server.getPluginManager().callEvent(event);

        assertTrue(event.isCancelled());
        assertEquals(1, player.getInventory().getItemInMainHand().getAmount());
        assertTrue(manager.getGrowthPercentage(raccoon) > 0);
    }

    @Test
    public void testThrownEggProjectileHitHatchesBaby() {
        Location loc = new Location(server.addSimpleWorld("world"), 10, 64, 10);
        org.bukkit.entity.Egg egg = (org.bukkit.entity.Egg) loc.getWorld().spawnEntity(loc, EntityType.EGG);
        egg.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "raccoon_projectile_egg"), org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);

        org.bukkit.event.entity.ProjectileHitEvent hitEvent = new org.bukkit.event.entity.ProjectileHitEvent(egg);
        server.getPluginManager().callEvent(hitEvent);

        assertEquals(1, manager.getAllRaccoons().size());
        Fox spawned = manager.getAllRaccoons().get(0);
        assertTrue(manager.isBabyRaccoon(spawned));
    }
}
