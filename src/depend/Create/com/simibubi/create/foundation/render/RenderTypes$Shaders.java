/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.RegisterShadersEvent
 */
package com.simibubi.create.foundation.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.simibubi.create.Create;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(value={Dist.CLIENT})
private static class RenderTypes.Shaders {
    private static ShaderInstance glowingShader;

    private RenderTypes.Shaders() {
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        ResourceProvider resourceProvider = event.getResourceProvider();
        event.registerShader(new ShaderInstance(resourceProvider, Create.asResource("glowing_shader"), DefaultVertexFormat.NEW_ENTITY), shader -> {
            glowingShader = shader;
        });
    }
}
