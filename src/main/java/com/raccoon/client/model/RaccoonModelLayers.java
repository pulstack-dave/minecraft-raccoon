package com.raccoon.client.model;

import com.raccoon.RaccoonMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RaccoonModelLayers {
    public static final EntityModelLayer RACCOON = new EntityModelLayer(new Identifier(RaccoonMod.MOD_ID, "raccoon"), "main");
}
