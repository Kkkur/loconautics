/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.render.SuperByteBufferCache$Compartment
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.ChunkRenderTypeSet
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.simibubi.create.content.kinetics.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.KineticDebugger;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.apache.commons.lang3.ArrayUtils;

public class KineticBlockEntityRenderer<T extends KineticBlockEntity>
extends SafeBlockEntityRenderer<T> {
    public static final SuperByteBufferCache.Compartment<BlockState> KINETIC_BLOCK = new SuperByteBufferCache.Compartment();
    public static boolean rainbowMode = false;
    protected static final RenderType[] REVERSED_CHUNK_BUFFER_LAYERS = (RenderType[])RenderType.chunkBufferLayers().toArray(RenderType[]::new);

    public KineticBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(T be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState state = this.getRenderedBlockState(be);
        RenderType type = this.getRenderType(be, state);
        KineticBlockEntityRenderer.renderRotatingBuffer(be, this.getRotatedModel(be, state), ms, buffer.getBuffer(type), light);
    }

    protected BlockState getRenderedBlockState(T be) {
        return be.getBlockState();
    }

    protected RenderType getRenderType(T be, BlockState state) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        ChunkRenderTypeSet typeSet = model.getRenderTypes(state, RandomSource.create((long)42L), ModelData.EMPTY);
        for (RenderType type : REVERSED_CHUNK_BUFFER_LAYERS) {
            if (!typeSet.contains(type)) continue;
            return type;
        }
        return RenderType.cutoutMipped();
    }

    protected SuperByteBuffer getRotatedModel(T be, BlockState state) {
        return CachedBuffers.block(KINETIC_BLOCK, (BlockState)state);
    }

    public static void renderRotatingKineticBlock(KineticBlockEntity be, BlockState renderedState, PoseStack ms, VertexConsumer buffer, int light) {
        SuperByteBuffer superByteBuffer = CachedBuffers.block(KINETIC_BLOCK, (BlockState)renderedState);
        KineticBlockEntityRenderer.renderRotatingBuffer(be, superByteBuffer, ms, buffer, light);
    }

    public static void renderRotatingBuffer(KineticBlockEntity be, SuperByteBuffer superBuffer, PoseStack ms, VertexConsumer buffer, int light) {
        KineticBlockEntityRenderer.standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, buffer);
    }

    public static float getAngleForBe(KineticBlockEntity be, BlockPos pos, Direction.Axis axis) {
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        float offset = KineticBlockEntityRenderer.getRotationOffsetForPosition(be, pos, axis);
        float angle = (time * be.getSpeed() * 3.0f / 10.0f + offset) % 360.0f / 180.0f * (float)Math.PI;
        return angle;
    }

    public static SuperByteBuffer standardKineticRotationTransform(SuperByteBuffer buffer, KineticBlockEntity be, int light) {
        BlockPos pos = be.getBlockPos();
        Direction.Axis axis = ((IRotate)be.getBlockState().getBlock()).getRotationAxis(be.getBlockState());
        return KineticBlockEntityRenderer.kineticRotationTransform(buffer, be, axis, KineticBlockEntityRenderer.getAngleForBe(be, pos, axis), light);
    }

    public static SuperByteBuffer kineticRotationTransform(SuperByteBuffer buffer, KineticBlockEntity be, Direction.Axis axis, float angle, int light) {
        buffer.light(light);
        buffer.rotateCentered(angle, Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis));
        if (KineticDebugger.isActive()) {
            rainbowMode = true;
            buffer.color(be.hasNetwork() ? Color.generateFromLong((long)be.network) : Color.WHITE);
        } else {
            float overStressedEffect = be.effects.overStressedEffect;
            if (overStressedEffect != 0.0f) {
                boolean overstressed = overStressedEffect > 0.0f;
                Color color = overstressed ? Color.RED : Color.SPRING_GREEN;
                float weight = overstressed ? overStressedEffect : -overStressedEffect;
                buffer.color(Color.WHITE.mixWith(color, weight));
            } else {
                buffer.color(Color.WHITE);
            }
        }
        return buffer;
    }

    public static float getRotationOffsetForPosition(KineticBlockEntity be, BlockPos pos, Direction.Axis axis) {
        return KineticBlockEntityVisual.rotationOffset(be.getBlockState(), axis, (Vec3i)pos) + (float)be.getRotationAngleOffset(axis);
    }

    public static BlockState shaft(Direction.Axis axis) {
        return (BlockState)AllBlocks.SHAFT.getDefaultState().setValue((Property)BlockStateProperties.AXIS, (Comparable)axis);
    }

    public static Direction.Axis getRotationAxisOf(KineticBlockEntity be) {
        return ((IRotate)be.getBlockState().getBlock()).getRotationAxis(be.getBlockState());
    }

    static {
        ArrayUtils.reverse((Object[])REVERSED_CHUNK_BUFFER_LAYERS);
    }
}
