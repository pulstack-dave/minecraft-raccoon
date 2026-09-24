package com.raccoon.entity;

import com.raccoon.RaccoonMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = RaccoonMod.MOD_ID)
public class RaccoonEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, RaccoonMod.MOD_ID);

    public static final EntityType<RaccoonEntity> RACCOON_TYPE = EntityType.Builder.of(RaccoonEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 0.7F)
            .clientTrackingRange(10)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(RaccoonMod.MOD_ID, "raccoon")));

    public static final DeferredHolder<EntityType<?>, EntityType<RaccoonEntity>> RACCOON = ENTITY_TYPES.register("raccoon", () -> RACCOON_TYPE);

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(RACCOON_TYPE, RaccoonEntity.createAttributes().build());
    }
}
