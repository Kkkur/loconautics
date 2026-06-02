/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.Util
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderStateShard$ShaderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TextureStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceProvider
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.RegisterShadersEvent
 */
package com.simibubi.create.foundation.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.Create;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

public class RenderTypes
extends RenderStateShard {
    public static final RenderStateShard.ShaderStateShard GLOWING_SHADER = new RenderStateShard.ShaderStateShard(() -> Shaders.glowingShader);
    private static final RenderType ENTITY_SOLID_BLOCK_MIPPED = RenderType.create((String)RenderTypes.createLayerName("entity_solid_block_mipped"), (VertexFormat)DefaultVertexFormat.NEW_ENTITY, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)BLOCK_SHEET_MIPPED).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
    private static final RenderType ENTITY_CUTOUT_BLOCK_MIPPED = RenderType.create((String)RenderTypes.createLayerName("entity_cutout_block_mipped"), (VertexFormat)DefaultVertexFormat.NEW_ENTITY, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)BLOCK_SHEET_MIPPED).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
    private static final RenderType ENTITY_TRANSLUCENT_BLOCK_MIPPED = RenderType.create((String)RenderTypes.createLayerName("entity_translucent_block_mipped"), (VertexFormat)DefaultVertexFormat.NEW_ENTITY, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
    private static final RenderType ADDITIVE = RenderType.create((String)RenderTypes.createLayerName("additive"), (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RENDERTYPE_SOLID_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)BLOCK_SHEET).setTransparencyState(ADDITIVE_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
    private static final RenderType ITEM_GLOWING_SOLID = RenderType.create((String)RenderTypes.createLayerName("item_glowing_solid"), (VertexFormat)DefaultVertexFormat.NEW_ENTITY, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(GLOWING_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)BLOCK_SHEET).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
    private static final RenderType ITEM_GLOWING_TRANSLUCENT = RenderType.create((String)RenderTypes.createLayerName("item_glowing_translucent"), (VertexFormat)DefaultVertexFormat.NEW_ENTITY, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(GLOWING_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)BLOCK_SHEET).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
    private static final Function<ResourceLocation, RenderType> CHAIN = Util.memoize(p_234330_ -> RenderType.create((String)"chain_conveyor_chain", (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RENDERTYPE_CUTOUT_MIPPED_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(p_234330_, false, true)).setTransparencyState(NO_TRANSPARENCY).setWriteMaskState(COLOR_DEPTH_WRITE).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false)));
    public static BiFunction<ResourceLocation, Boolean, RenderType> TRAIN_MAP = Util.memoize(RenderTypes::getTrainMap);

    public static RenderType entitySolidBlockMipped() {
        return ENTITY_SOLID_BLOCK_MIPPED;
    }

    public static RenderType entityCutoutBlockMipped() {
        return ENTITY_CUTOUT_BLOCK_MIPPED;
    }

    public static RenderType entityTranslucentBlockMipped() {
        return ENTITY_TRANSLUCENT_BLOCK_MIPPED;
    }

    public static RenderType additive() {
        return ADDITIVE;
    }

    private static RenderType getTrainMap(ResourceLocation locationIn, boolean linearFiltering) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TEXT_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(locationIn, linearFiltering, false)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false);
        return RenderType.create((String)"create_train_map", (VertexFormat)DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)rendertype$state);
    }

    public static RenderType itemGlowingSolid() {
        return ITEM_GLOWING_SOLID;
    }

    public static RenderType itemGlowingTranslucent() {
        return ITEM_GLOWING_TRANSLUCENT;
    }

    public static RenderType chain(ResourceLocation pLocation) {
        return CHAIN.apply(pLocation);
    }

    private static String createLayerName(String name) {
        return "create:" + name;
    }

    private RenderTypes() {
        super(null, null, null);
    }

    @EventBusSubscriber(value={Dist.CLIENT})
    private static class Shaders {
        private static ShaderInstance glowingShader;

        private Shaders() {
        }

        @SubscribeEvent
        public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
            ResourceProvider resourceProvider = event.getResourceProvider();
            event.registerShader(new ShaderInstance(resourceProvider, Create.asResource("glowing_shader"), DefaultVertexFormat.NEW_ENTITY), shader -> {
                glowingShader = shader;
            });
        }
    }
}
