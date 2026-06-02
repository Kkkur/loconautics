/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  foundry.veil.api.client.render.VeilRenderBridge
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator.RedstoneAccumulatorBlock;
import dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator.RedstoneAccumulatorBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import foundry.veil.api.client.render.VeilRenderBridge;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstoneAccumulatorRenderer
extends SmartBlockEntityRenderer<RedstoneAccumulatorBlockEntity> {
    public static ResourceLocation SHADER_NAME = Simulated.path("redstone_accumulator/diode");
    public static RenderType DIODE_RENDER_TYPE = RenderType.create((String)"redstone_accumulator_diode", (VertexFormat)DefaultVertexFormat.BLOCK, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)131072, (boolean)true, (boolean)false, (RenderType.CompositeState)RenderType.CompositeState.builder().setLightmapState(RenderStateShard.LIGHTMAP).setShaderState(RenderStateShard.RENDERTYPE_CUTOUT_SHADER).setTextureState((RenderStateShard.EmptyTextureStateShard)RenderStateShard.BLOCK_SHEET_MIPPED).setShaderState(VeilRenderBridge.shaderState((ResourceLocation)SHADER_NAME)).createCompositeState(true));

    public RedstoneAccumulatorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(RedstoneAccumulatorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        SuperByteBuffer render = CachedBuffers.partial((PartialModel)SimPartialModels.REDSTONE_ACCUMULATOR_DIODE, (BlockState)be.getBlockState()).color(255, 255, 255, this.getLitAmount(be, partialTicks));
        Direction facing = (Direction)be.getBlockState().getValue((Property)RedstoneAccumulatorBlock.FACING);
        render.light(light);
        render.translate(0.5, 0.0, 0.5);
        ((SuperByteBuffer)render.rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).pushPose();
        render.renderInto(ms, buffer.getBuffer(DIODE_RENDER_TYPE));
    }

    private int getLitAmount(RedstoneAccumulatorBlockEntity be, float partialTicks) {
        float state = be.lerpedState.getValue(partialTicks);
        state = 1.0f - (float)Math.pow(state / 15.0f, 1.5);
        return (int)Mth.clamp((float)(state * 255.0f), (float)0.0f, (float)255.0f);
    }
}
