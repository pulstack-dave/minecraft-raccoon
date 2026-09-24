package com.raccoon;

import com.raccoon.entity.RaccoonEntities;
import com.raccoon.item.RaccoonItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RaccoonMod.MOD_ID)
public class RaccoonMod {
    public static final String MOD_ID = "raccoon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public RaccoonMod(IEventBus modEventBus) {
        LOGGER.info("Initializing Minecraft Raccoon mod");
        RaccoonEntities.ENTITY_TYPES.register(modEventBus);
        RaccoonItems.ITEMS.register(modEventBus);
        modEventBus.addListener(RaccoonMod::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(RaccoonItems.RACCOON_SPAWN_EGG);
        } else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(RaccoonItems.TRASH);
        }
    }
}
