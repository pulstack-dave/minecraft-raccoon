package com.raccoon.client;

import com.raccoon.client.model.RaccoonEntityModel;
import com.raccoon.client.model.RaccoonModelLayers;
import com.raccoon.client.render.RaccoonEntityRenderer;
import com.raccoon.entity.RaccoonEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class RaccoonClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(RaccoonModelLayers.RACCOON, RaccoonEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(RaccoonEntities.RACCOON, RaccoonEntityRenderer::new);
    }
}
