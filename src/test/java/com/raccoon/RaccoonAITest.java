package com.raccoon;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.raccoon.ai.RaccoonAITask;
import com.raccoon.entity.RaccoonManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RaccoonAITest {

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
    public void testAITargetsNearbyPrey() {
        World world = server.addSimpleWorld("world");
        Location loc = new Location(world, 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, false);

        Location preyLoc = new Location(world, 2, 64, 2);
        Chicken chicken = (Chicken) world.spawnEntity(preyLoc, EntityType.CHICKEN);

        RaccoonAITask task = new RaccoonAITask(plugin);
        task.run();

        // Target should be set to chicken
        assertEquals(chicken, raccoon.getTarget());
    }

    @Test
    public void testAIClearsPlayerTarget() {
        World world = server.addSimpleWorld("world");
        Location loc = new Location(world, 0, 64, 0);
        Fox raccoon = manager.spawnRaccoon(loc, false);

        PlayerMock player = server.addPlayer();
        raccoon.setTarget(player);

        RaccoonAITask task = new RaccoonAITask(plugin);
        task.run();

        // Target must be cleared because raccoons never target players
        assertNull(raccoon.getTarget());
    }
}
