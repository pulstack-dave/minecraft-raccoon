package com.raccoon.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.raccoon.RaccoonMod;
import com.raccoon.client.model.RaccoonEntityModel;
import com.raccoon.client.model.RaccoonModelLayers;
import com.raccoon.entity.RaccoonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RaccoonEntityRenderer extends MobRenderer<RaccoonEntity, RaccoonRenderState, RaccoonEntityModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RaccoonMod.MOD_ID, "textures/entity/raccoon/raccoon.png");

    public RaccoonEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new RaccoonEntityModel(context.bakeLayer(RaccoonModelLayers.RACCOON)), 0.4F);
    }

    @Override
    public RaccoonRenderState createRenderState() {
        return new RaccoonRenderState();
    }

    @Override
    public void extractRenderState(RaccoonEntity entity, RaccoonRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.standing = entity.isStanding();
        state.sitting = entity.isInSittingPose() || entity.isOrderedToSit();
        state.washing = entity.isWashing();
    }

    @Override
    public ResourceLocation getTextureLocation(RaccoonRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void scale(RaccoonRenderState state, PoseStack poseStack) {
        float size = state.isBaby ? 0.55F : 0.85F;
        poseStack.scale(size, size, size);
    }
}
