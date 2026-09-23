package com.raccoon;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.raccoon.entity.RaccoonManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RaccoonManagerTest {

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
    public void testSpawnBabyRaccoon() {
        Location loc = new Location(server.addSimpleWorld("world"), 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, true);

        assertNotNull(raccoon);
        assertTrue(manager.isRaccoon(raccoon));
        assertTrue(manager.isBabyRaccoon(raccoon));
        assertFalse(raccoon.isAdult());
        assertEquals(plugin.getRaccoonConfig().getBabyName(), raccoon.getCustomName());
        assertEquals(plugin.getRaccoonConfig().getBabyMaxHealth(), raccoon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }

    @Test
    public void testSpawnAdultRaccoon() {
        Location loc = new Location(server.addSimpleWorld("world"), 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, false);

        assertNotNull(raccoon);
        assertTrue(manager.isRaccoon(raccoon));
        assertFalse(manager.isBabyRaccoon(raccoon));
        assertTrue(raccoon.isAdult());
        assertEquals(plugin.getRaccoonConfig().getAdultName(), raccoon.getCustomName());
        assertEquals(plugin.getRaccoonConfig().getAdultMaxHealth(), raccoon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }

    @Test
    public void testFeedAndAccelerateGrowth() {
        Location loc = new Location(server.addSimpleWorld("world"), 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, true);
        PlayerMock player = server.addPlayer();

        ItemStack berries = new ItemStack(Material.SWEET_BERRIES, 1);
        boolean fed = manager.feedRaccoon(raccoon, berries, player);

        assertTrue(fed);
        assertTrue(manager.getGrowthPercentage(raccoon) > 0);
    }

    @Test
    public void testGrowToAdult() {
        Location loc = new Location(server.addSimpleWorld("world"), 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, true);

        manager.advanceGrowth(raccoon, plugin.getRaccoonConfig().getGrowthTimeSeconds() * 20);

        assertFalse(manager.isBabyRaccoon(raccoon));
        assertTrue(raccoon.isAdult());
        assertEquals(plugin.getRaccoonConfig().getAdultName(), raccoon.getCustomName());
    }

    @Test
    public void testEatAnimalPrey() {
        Location loc = new Location(server.addSimpleWorld("world"), 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, true);
        Chicken chicken = (Chicken) loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);

        assertEquals(0, manager.getAnimalsEaten(raccoon));
        manager.eatAnimal(raccoon, chicken);

        assertEquals(1, manager.getAnimalsEaten(raccoon));
        assertTrue(manager.getGrowthPercentage(raccoon) > 0);
    }
}
