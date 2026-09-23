package com.raccoon.entity;

import com.raccoon.RaccoonMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RaccoonEntities {
    public static final EntityType<RaccoonEntity> RACCOON = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(RaccoonMod.MOD_ID, "raccoon"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RaccoonEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6F, 0.7F))
                    .build()
    );

    public static void register() {
        // Ensures static initializer runs
    }
}
