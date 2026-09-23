package com.raccoon;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RaccoonCommandTest {

    private ServerMock server;
    private RaccoonPlugin plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(RaccoonPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testCommandEggGivesEggToPlayer() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        boolean result = player.performCommand("raccoon egg " + player.getName() + " 3");
        assertTrue(result);

        ItemStack item = player.getInventory().getItem(0);
        assertNotNull(item);
        assertEquals(Material.EGG, item.getType());
        assertEquals(3, item.getAmount());
        assertTrue(plugin.getRaccoonEgg().isRaccoonEgg(item));
    }

    @Test
    public void testCommandSpawnSpawnsRaccoon() {
        server.addSimpleWorld("world");
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        boolean result = player.performCommand("raccoon spawn baby");
        assertTrue(result);
        assertEquals(1, plugin.getRaccoonManager().getAllRaccoons().size());
    }

    @Test
    public void testCommandReload() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        boolean result = player.performCommand("raccoon reload");
        assertTrue(result);
        String msg = player.nextMessage();
        assertNotNull(msg);
        assertTrue(msg.contains("reloaded"));
    }

    @Test
    public void testCommandHelpAndList() {
        server.addSimpleWorld("world");
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        assertTrue(player.performCommand("raccoon help"));
        String helpMsg = player.nextMessage();
        assertNotNull(helpMsg);
        assertTrue(helpMsg.contains("Minecraft Raccoon"));

        assertTrue(player.performCommand("raccoon spawn adult"));
        assertTrue(player.performCommand("raccoon list"));
    }

    @Test
    public void testTabCompletion() {
        PlayerMock player = server.addPlayer();
        player.setOp(true);

        java.util.List<String> tabs = server.getCommandTabComplete(player, "raccoon ");
        assertNotNull(tabs);
        assertTrue(tabs.contains("egg"));
        assertTrue(tabs.contains("spawn"));
        assertTrue(tabs.contains("list"));
        assertTrue(tabs.contains("reload"));
    }
}
