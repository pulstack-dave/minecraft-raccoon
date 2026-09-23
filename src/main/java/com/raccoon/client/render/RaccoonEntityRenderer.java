package com.raccoon.client.render;

import com.raccoon.RaccoonMod;
import com.raccoon.client.model.RaccoonEntityModel;
import com.raccoon.client.model.RaccoonModelLayers;
import com.raccoon.entity.RaccoonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RaccoonEntityRenderer extends MobEntityRenderer<RaccoonEntity, RaccoonEntityModel<RaccoonEntity>> {
    private static final Identifier TEXTURE = new Identifier(RaccoonMod.MOD_ID, "textures/entity/raccoon/raccoon.png");

    public RaccoonEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new RaccoonEntityModel<>(context.getPart(RaccoonModelLayers.RACCOON)), 0.4F);
    }

    @Override
    public Identifier getTexture(RaccoonEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(RaccoonEntity entity, MatrixStack matrices, float amount) {
        if (entity.isBaby()) {
            matrices.scale(0.55F, 0.55F, 0.55F);
        } else {
            matrices.scale(0.85F, 0.85F, 0.85F);
        }
        super.scale(entity, matrices, amount);
    }
}
