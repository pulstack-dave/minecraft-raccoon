package com.raccoon.item;

import com.raccoon.RaccoonMod;
import com.raccoon.entity.RaccoonEntities;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RaccoonItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RaccoonMod.MOD_ID);

    public static final DeferredItem<SpawnEggItem> RACCOON_SPAWN_EGG = ITEMS.registerItem("raccoon_spawn_egg",
            properties -> new SpawnEggItem(RaccoonEntities.RACCOON.get(), properties));

    public static final DeferredItem<Item> TRASH = ITEMS.registerItem("trash",
            properties -> new Item(properties.food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.2F)
                    .build())));
}
