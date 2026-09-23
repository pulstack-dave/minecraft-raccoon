package com.raccoon.item;

import com.raccoon.RaccoonMod;
import com.raccoon.entity.RaccoonEntities;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RaccoonItems {
    public static final Item RACCOON_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            new Identifier(RaccoonMod.MOD_ID, "raccoon_spawn_egg"),
            new SpawnEggItem(RaccoonEntities.RACCOON, 0x5C5856, 0x1E1E1E, new Item.Settings())
    );

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(RACCOON_SPAWN_EGG);
        });
    }
}
