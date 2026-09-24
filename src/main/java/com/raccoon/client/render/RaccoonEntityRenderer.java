package com.raccoon.client.render;

import com.raccoon.RaccoonMod;
import com.raccoon.client.model.RaccoonEntityModel;
import com.raccoon.client.model.RaccoonModelLayers;
import com.raccoon.entity.RaccoonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RaccoonEntityRenderer extends MobRenderer<RaccoonEntity, RaccoonEntityModel<RaccoonEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RaccoonMod.MOD_ID, "textures/entity/raccoon/raccoon.png");

    public RaccoonEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new RaccoonEntityModel<>(context.bakeLayer(RaccoonModelLayers.RACCOON)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(RaccoonEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(RaccoonEntity entity, com.mojang.blaze3d.vertex.PoseStack poseStack, float partialTick) {
        float size = entity.isBaby() ? 0.55F : 0.85F;
        poseStack.scale(size, size, size);
    }
}
