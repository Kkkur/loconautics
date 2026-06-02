/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer
 *  com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon.MountedPotatoCannonBlockEntity;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class MountedPotatoCannonRenderer
extends SafeBlockEntityRenderer<MountedPotatoCannonBlockEntity> {
    public MountedPotatoCannonRenderer(BlockEntityRendererProvider.Context context) {
    }

    protected void renderSafe(MountedPotatoCannonBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        FilteringRenderer.renderOnBlockEntity((SmartBlockEntity)be, (float)partialTicks, (PoseStack)ms, (MultiBufferSource)buffer, (int)light, (int)overlay);
        this.renderComponents(be, partialTicks, ms, buffer, light, overlay);
        this.renderItem(be, partialTicks, ms, buffer, light, overlay);
    }

    private void renderComponents(MountedPotatoCannonBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        boolean drawParts;
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        boolean bl = drawParts = !VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel());
        if (drawParts) {
            KineticBlockEntityRenderer.renderRotatingKineticBlock((KineticBlockEntity)be, (BlockState)this.getRenderedBlockState(be), (PoseStack)ms, (VertexConsumer)vb, (int)light);
        }
        BlockState blockState = be.getBlockState();
        float barrelOffset = !be.isBlocked() ? be.getBarrelDistance(partialTicks) : (float)(-(be.getBlockedLength() / 2.0));
        float bellowOffset = -be.getBellowDistance(partialTicks);
        SuperByteBuffer barrel = CachedBuffers.partial((PartialModel)AeroPartialModels.CANNON_BARREL, (BlockState)blockState);
        ((SuperByteBuffer)MountedPotatoCannonRenderer.transform(barrel, blockState, true).translate(0.0f, 0.0f, barrelOffset)).light(light).renderInto(ms, vb);
        SuperByteBuffer bellow = CachedBuffers.partial((PartialModel)AeroPartialModels.CANNON_BELLOW, (BlockState)blockState);
        ((SuperByteBuffer)MountedPotatoCannonRenderer.transform(bellow, blockState, true).translate(0.0f, bellowOffset, 0.0f)).light(light).renderInto(ms, vb);
        ((SuperByteBuffer)((SuperByteBuffer)MountedPotatoCannonRenderer.transform(bellow, blockState, true).rotateCentered((float)Math.PI, Direction.SOUTH)).light(light).translate(0.0f, bellowOffset, 0.0f)).renderInto(ms, vb);
        if (drawParts) {
            SuperByteBuffer cogwheel = CachedBuffers.partial((PartialModel)AeroPartialModels.CANNON_COG, (BlockState)blockState);
            float angle = be.getCogwheelAngle(partialTicks);
            ((SuperByteBuffer)MountedPotatoCannonRenderer.transform(cogwheel, blockState, true).rotateCentered((float)Math.PI / 180 * (angle % 360.0f), Direction.SOUTH)).light(light).renderInto(ms, vb);
        }
    }

    private static SuperByteBuffer transform(SuperByteBuffer buffer, BlockState state, boolean axisDirectionMatters) {
        Direction facing = (Direction)state.getValue((Property)BlockStateProperties.FACING);
        float zRotLast = axisDirectionMatters && (Boolean)state.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE) ^ facing.getAxis() == Direction.Axis.Z ? 90.0f : 0.0f;
        float yRot = AngleHelper.horizontalAngle((Direction)facing);
        float zRot = facing == Direction.UP ? 90.0f : (facing == Direction.DOWN ? 90.0f : 0.0f);
        float zRotSecondLast = facing == Direction.UP ? 180.0f : 0.0f;
        buffer.rotateCentered((float)((double)(zRot / 180.0f) * Math.PI), Direction.SOUTH);
        buffer.rotateCentered((float)((double)(zRot / 180.0f) * Math.PI), Direction.DOWN);
        buffer.rotateCentered((float)((double)(yRot / 180.0f) * Math.PI), Direction.UP);
        buffer.rotateCentered((float)((double)(zRotLast / 180.0f) * Math.PI), Direction.SOUTH);
        buffer.rotateCentered((float)((double)(zRotSecondLast / 180.0f) * Math.PI), Direction.UP);
        return buffer;
    }

    private BlockState getRenderedBlockState(MountedPotatoCannonBlockEntity te) {
        return KineticBlockEntityRenderer.shaft((Direction.Axis)KineticBlockEntityRenderer.getRotationAxisOf((KineticBlockEntity)te));
    }

    public void renderItem(MountedPotatoCannonBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be.getInventory().isEmpty()) {
            return;
        }
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        ms.pushPose();
        msr.center();
        Direction facing = (Direction)be.getBlockState().getValue((Property)BlockStateProperties.FACING);
        Vec3i facingVec = facing.getNormal();
        float itemScale = 0.35f;
        float normalizedTimer = be.getItemTime(partialTicks);
        float itemPosition = !be.isBlocked() ? 1.0f - (float)Math.exp(-0.25f * normalizedTimer) : 0.0f;
        ms.translate((float)facingVec.getX() * (itemPosition *= 0.8f), (float)facing.getStepY() * itemPosition, (float)facingVec.getZ() * itemPosition);
        ms.scale(0.35f, 0.35f, 0.35f);
        int itemRotationId = be.getItemRotationId();
        Quaternionf Q = new Quaternionf((float)Math.sin((float)itemRotationId * 0.4f), (float)Math.cos((float)itemRotationId * 1.4f), (float)Math.sin((float)itemRotationId * 3.0f), (float)Math.cos((float)itemRotationId * 5.0f));
        Q.normalize();
        msr.rotate((Quaternionfc)Q);
        itemRenderer.renderStatic(be.getInventory().slot.getStack(), ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
        ms.popPose();
    }
}
