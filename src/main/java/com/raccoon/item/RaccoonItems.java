package com.raccoon.item;

import com.raccoon.RaccoonMod;
import com.raccoon.entity.RaccoonEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RaccoonItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RaccoonMod.MOD_ID);

    public static final DeferredItem<SpawnEggItem> RACCOON_SPAWN_EGG = ITEMS.register("raccoon_spawn_egg",
            () -> new SpawnEggItem(RaccoonEntities.RACCOON.get(), 0x5C5856, 0x1E1E1E, new Item.Properties()));
}
