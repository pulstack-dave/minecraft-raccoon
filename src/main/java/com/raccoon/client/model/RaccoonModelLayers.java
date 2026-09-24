package com.raccoon.client.model;

import com.raccoon.RaccoonMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class RaccoonModelLayers {
    public static final ModelLayerLocation RACCOON = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(RaccoonMod.MOD_ID, "raccoon"), "main");
}
