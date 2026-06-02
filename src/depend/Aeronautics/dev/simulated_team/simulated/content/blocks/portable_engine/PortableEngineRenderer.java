/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.AllPartialModels
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.RedstoneTorchBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.simulated_team.simulated.content.blocks.portable_engine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PortableEngineRenderer
extends KineticBlockEntityRenderer<PortableEngineBlockEntity> {
    public PortableEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected static float getHatchOpenProgress(PortableEngineBlockEntity engine, float partialTicks) {
        return Mth.sin((float)(engine.getHatchOpenTime(partialTicks) / 10.0f * 1.5707964f));
    }

    protected void renderSafe(PortableEngineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer translucent;
        BlockState state = this.getRenderedBlockState((KineticBlockEntity)be);
        RenderType type = this.getRenderType((KineticBlockEntity)be, state);
        PortableEngineRenderer.renderRotatingBuffer((KineticBlockEntity)be, (SuperByteBuffer)this.getRotatedModel(be, state), (PoseStack)ms, (VertexConsumer)buffer.getBuffer(type), (int)light);
        FilteringRenderer.renderOnBlockEntity((SmartBlockEntity)be, (float)partialTicks, (PoseStack)ms, (MultiBufferSource)buffer, (int)light, (int)overlay);
        VertexConsumer cutout = buffer.getBuffer(RenderType.cutout());
        Direction direction = (Direction)be.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
        BlockState blockState = be.getBlockState();
        SimPartialModels.EngineParts engineParts = SimPartialModels.ENGINE_PARTS;
        float visualStrength = be.visualStrength.getValue(partialTicks);
        boolean lit = (Boolean)blockState.getValue((Property)RedstoneTorchBlock.LIT);
        this.renderHatch(be, partialTicks, ms, light, blockState, direction, cutout, 255, engineParts, !lit, false);
        this.renderPipes(be, partialTicks, ms, light, blockState, direction, cutout, 255, engineParts, false);
        float hatchOpenProgress = 1.0f - PortableEngineRenderer.getHatchOpenProgress(be, partialTicks);
        if (visualStrength > 0.0f) {
            translucent = buffer.getBuffer(RenderType.translucent());
            engineParts = be.isSuperHeated() ? SimPartialModels.ENGINE_PARTS_SUPERHEATED : SimPartialModels.ENGINE_PARTS_HEATED;
            this.renderPipes(be, partialTicks, ms, 0xF000F0, blockState, direction, translucent, (int)(visualStrength * 255.0f), engineParts, true);
        }
        if (lit) {
            translucent = buffer.getBuffer(RenderType.translucent());
            this.renderHatch(be, partialTicks, ms, 0xF000F0, blockState, direction, translucent, (int)(hatchOpenProgress * 255.0f), engineParts, lit, true);
        }
    }

    private void renderHatch(PortableEngineBlockEntity be, float partialTicks, PoseStack ms, int light, BlockState blockState, Direction direction, VertexConsumer consumer, int alpha, SimPartialModels.EngineParts parts, boolean renderInner, boolean lit) {
        if (be.isVirtual()) {
            lit = false;
        }
        double hatchPivotY = 0.30625f;
        double hatchPivotZ = 0.23125f;
        float hatchOpenAmount = PortableEngineRenderer.getHatchOpenProgress(be, partialTicks) * 0.65f;
        SuperByteBuffer hatchBottom = this.rotateToFacing(CachedBuffers.partial((PartialModel)parts.hatchBottom, (BlockState)blockState), direction);
        if (lit) {
            hatchBottom.disableDiffuse();
        }
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)hatchBottom.translate(0.0, (double)0.30625f, (double)0.23125f)).rotate(-hatchOpenAmount, Direction.EAST)).translate(-0.0, (double)-0.30625f, (double)-0.23125f)).light(light).color(255, 255, 255, alpha).renderInto(ms, consumer);
        SuperByteBuffer hatchTop = this.rotateToFacing(CachedBuffers.partial((PartialModel)parts.hatchTop, (BlockState)blockState), direction);
        if (lit) {
            hatchTop.disableDiffuse();
        }
        hatchTop.light(light).color(255, 255, 255, alpha).renderInto(ms, consumer);
        if (renderInner) {
            SuperByteBuffer mouth = this.rotateToFacing(CachedBuffers.partial((PartialModel)parts.mouth, (BlockState)blockState), direction.getOpposite());
            if (lit) {
                mouth.disableDiffuse();
            }
            mouth.light(light).renderInto(ms, consumer);
        }
    }

    private void renderPipes(PortableEngineBlockEntity be, float partialTicks, PoseStack ms, int light, BlockState blockState, Direction direction, VertexConsumer consumer, int alpha, SimPartialModels.EngineParts parts, boolean lit) {
        float renderTime = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel()) / 20.0f;
        double pulseTime = (double)renderTime * 7.0;
        double clipHeight = 0.65;
        float pulseStrength = 0.03f * be.visualStrength.getValue(partialTicks);
        float pipePulseStrength = pulseStrength * 1.1f;
        float pipeScale = (float)(Math.max(Math.sin(pulseTime) + 0.65, 0.0) - 0.65) * pipePulseStrength + 1.0f;
        float outletScale = (float)(Math.max(Math.sin(pulseTime - 1.15) + 0.65, 0.0) - 0.65) * pulseStrength + 1.0f;
        Vector3f outletRotationPointLeft = new Vector3f(2.2f, 10.2f, 11.0f).div(16.0f);
        Vector3f outletRotationPointRight = new Vector3f(13.6f, 10.2f, 11.0f).div(16.0f);
        float outletRotation = (float)Math.toRadians(7.5);
        Vector3f pipeCenterRight = new Vector3f(14.0f, 10.0f, 8.0f).div(16.0f);
        Vector3f pipeCenterLeft = new Vector3f(2.0f, 10.0f, 8.0f).div(16.0f);
        if (be.isVirtual()) {
            lit = false;
        }
        SuperByteBuffer pipeRight = this.rotateToFacing(CachedBuffers.partial((PartialModel)parts.pipeRight, (BlockState)blockState), direction);
        if (lit) {
            pipeRight.disableDiffuse();
        }
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)pipeRight.translate((Vector3fc)pipeCenterRight)).scale(pipeScale)).translateBack((Vector3fc)pipeCenterRight)).light(light).color(255, 255, 255, alpha).renderInto(ms, consumer);
        SuperByteBuffer outletRight = this.rotateToFacing(CachedBuffers.partial((PartialModel)parts.outletRight, (BlockState)blockState), direction);
        if (lit) {
            outletRight.disableDiffuse();
        }
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)outletRight.translate((Vector3fc)pipeCenterRight)).scale(outletScale)).translateBack((Vector3fc)pipeCenterRight)).translate((Vector3fc)outletRotationPointRight)).rotateY(-outletRotation)).translateBack((Vector3fc)outletRotationPointRight)).light(light).color(255, 255, 255, alpha).renderInto(ms, consumer);
        SuperByteBuffer pipeLeft = this.rotateToFacing(CachedBuffers.partial((PartialModel)parts.pipeLeft, (BlockState)blockState), direction);
        if (lit) {
            pipeLeft.disableDiffuse();
        }
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)pipeLeft.translate((Vector3fc)pipeCenterLeft)).scale(pipeScale)).translateBack((Vector3fc)pipeCenterLeft)).light(light).color(255, 255, 255, alpha).renderInto(ms, consumer);
        SuperByteBuffer outletLeft = this.rotateToFacing(CachedBuffers.partial((PartialModel)parts.outletLeft, (BlockState)blockState), direction);
        if (lit) {
            outletLeft.disableDiffuse();
        }
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)outletLeft.translate((Vector3fc)pipeCenterLeft)).scale(outletScale)).translateBack((Vector3fc)pipeCenterLeft)).translate((Vector3fc)outletRotationPointLeft)).rotateY(outletRotation)).translateBack((Vector3fc)outletRotationPointLeft)).light(light).color(255, 255, 255, alpha).renderInto(ms, consumer);
    }

    protected SuperByteBuffer getRotatedModel(PortableEngineBlockEntity te, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)te.getBlockState(), (Direction)((Direction)te.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING)));
    }

    protected SuperByteBuffer rotateToFacing(SuperByteBuffer buffer, Direction facing) {
        buffer.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing)), Direction.UP);
        return buffer;
    }
}
