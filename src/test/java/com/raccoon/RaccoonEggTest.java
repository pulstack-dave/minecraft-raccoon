package com.raccoon;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.raccoon.entity.RaccoonEgg;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RaccoonEggTest {

    private ServerMock server;
    private RaccoonPlugin plugin;
    private RaccoonEgg eggHelper;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(RaccoonPlugin.class);
        eggHelper = plugin.getRaccoonEgg();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testCreateEgg() {
        ItemStack egg = eggHelper.createEgg(4);
        assertNotNull(egg);
        assertEquals(Material.EGG, egg.getType());
        assertEquals(4, egg.getAmount());
        assertTrue(egg.hasItemMeta());
        assertTrue(eggHelper.isRaccoonEgg(egg));
    }

    @Test
    public void testRegularEggIsNotRaccoonEgg() {
        ItemStack regularEgg = new ItemStack(Material.EGG, 1);
        assertFalse(eggHelper.isRaccoonEgg(regularEgg));
    }
}
