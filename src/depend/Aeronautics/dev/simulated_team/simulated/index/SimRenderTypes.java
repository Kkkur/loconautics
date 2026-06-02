/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  foundry.veil.api.client.render.VeilRenderBridge
 *  net.minecraft.Util
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TextureStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.resources.ResourceLocation
 */
package dev.simulated_team.simulated.index;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import dev.simulated_team.simulated.Simulated;
import foundry.veil.api.client.render.VeilRenderBridge;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class SimRenderTypes
extends RenderType {
    private static final RenderType STAFF_OVERLAY = SimRenderTypes.create((String)"simulated:staff_overlay/staff_overlay", (VertexFormat)DefaultVertexFormat.POSITION_COLOR, (VertexFormat.Mode)VertexFormat.Mode.TRIANGLE_STRIP, (int)1536, (boolean)false, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(VeilRenderBridge.shaderState((ResourceLocation)Simulated.path("staff_overlay/staff_overlay"))).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(RenderStateShard.COLOR_WRITE).setDepthTestState(RenderStateShard.NO_DEPTH_TEST).setCullState(CULL).createCompositeState(false));
    private static final RenderType LASER = SimRenderTypes.create((String)"simulated:laser", (VertexFormat)DefaultVertexFormat.POSITION_TEX_COLOR, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)false, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(VeilRenderBridge.shaderState((ResourceLocation)Simulated.path("laser/laser"))).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).createCompositeState(false));
    private static final RenderType LENS = SimRenderTypes.create((String)"simulated:laser_pointer_lens", (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)true, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)BLOCK_SHEET_MIPPED).setShaderState(VeilRenderBridge.shaderState((ResourceLocation)Simulated.path("laser_pointer/lens"))).createCompositeState(true));
    private static final VertexFormat SPRING_FORMAT = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Stress", VertexFormatElement.COLOR).add("UV0", VertexFormatElement.UV0).add("UV2", VertexFormatElement.UV2).add("Normal", VertexFormatElement.NORMAL).padding(1).build();
    private static final RenderType LOCK = SimRenderTypes.create((String)"simulated:lock", (VertexFormat)DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER).setDepthTestState(RenderStateShard.NO_DEPTH_TEST).setCullState(RenderStateShard.NO_CULL).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(Simulated.path("textures/gui/lock.png"), false, false)).createCompositeState(true));
    private static final RenderType ROPE = SimRenderTypes.create((String)"simulated:rope", (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setShaderState(VeilRenderBridge.shaderState((ResourceLocation)Simulated.path("rope/rope"))).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(Simulated.path("textures/block/rope_particle.png"), false, false)).setLightmapState(LIGHTMAP).createCompositeState(false));
    private static final Function<ResourceLocation, RenderType> SPRING = Util.memoize(texture -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder().setShaderState(VeilRenderBridge.shaderState((ResourceLocation)Simulated.path("spring/spring"))).setTextureState((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return SimRenderTypes.create((String)"spring", (VertexFormat)SPRING_FORMAT, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)1536, (boolean)true, (boolean)false, (RenderType.CompositeState)state);
    });

    private SimRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType staffOverlay() {
        return STAFF_OVERLAY;
    }

    public static RenderType laser() {
        return LASER;
    }

    public static RenderType lens() {
        return LENS;
    }

    public static RenderType lock() {
        return LOCK;
    }

    public static RenderType rope() {
        return ROPE;
    }

    public static RenderType spring(ResourceLocation texture) {
        return SPRING.apply(texture);
    }
}
