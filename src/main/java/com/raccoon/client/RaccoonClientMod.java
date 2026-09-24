package com.raccoon.client;

import com.raccoon.RaccoonMod;
import com.raccoon.client.model.RaccoonEntityModel;
import com.raccoon.client.model.RaccoonModelLayers;
import com.raccoon.client.render.RaccoonEntityRenderer;
import com.raccoon.entity.RaccoonEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = RaccoonMod.MOD_ID, value = Dist.CLIENT)
public class RaccoonClientMod {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RaccoonModelLayers.RACCOON, RaccoonEntityModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RaccoonEntities.RACCOON.get(), RaccoonEntityRenderer::new);
    }
}
