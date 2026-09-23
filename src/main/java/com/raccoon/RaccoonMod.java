package com.raccoon;

import com.raccoon.entity.RaccoonEntities;
import com.raccoon.entity.RaccoonEntity;
import com.raccoon.item.RaccoonItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaccoonMod implements ModInitializer {
    public static final String MOD_ID = "raccoon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Minecraft Raccoon Mod...");

        RaccoonEntities.register();
        RaccoonItems.register();

        FabricDefaultAttributeRegistry.register(RaccoonEntities.RACCOON, RaccoonEntity.createRaccoonAttributes());

        LOGGER.info("Minecraft Raccoon Mod initialized successfully!");
    }
}
