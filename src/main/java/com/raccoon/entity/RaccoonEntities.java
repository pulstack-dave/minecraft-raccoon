package com.raccoon.entity;

import com.raccoon.RaccoonMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = RaccoonMod.MOD_ID)
public class RaccoonEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(RaccoonMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<RaccoonEntity>> RACCOON = ENTITY_TYPES.registerEntityType(
            "raccoon",
            RaccoonEntity::new,
            MobCategory.CREATURE,
            builder -> builder.sized(0.6F, 0.7F).clientTrackingRange(10));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(RACCOON.get(), RaccoonEntity.createAttributes().build());
    }
}
