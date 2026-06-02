/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.Rotate
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.engine_room.flywheel.lib.transform.Translate
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.depot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.simibubi.create.content.logistics.depot.EjectorBlock;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.Rotate;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.engine_room.flywheel.lib.transform.Translate;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EjectorRenderer
extends ShaftRenderer<EjectorBlockEntity> {
    static final Vec3 pivot = VecHelper.voxelSpace((double)0.0, (double)11.25, (double)0.75);

    public EjectorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public boolean shouldRenderOffScreen(EjectorBlockEntity p_188185_1_) {
        return true;
    }

    @Override
    protected void renderSafe(EjectorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        float lidProgress = be.getLidProgress(partialTicks);
        float angle = lidProgress * 70.0f;
        if (!VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            SuperByteBuffer model = CachedBuffers.partial((PartialModel)AllPartialModels.EJECTOR_TOP, (BlockState)be.getBlockState());
            EjectorRenderer.applyLidAngle(be, angle, model);
            model.light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        float maxTime = (float)(be.earlyTarget != null ? (double)be.earlyTargetTime : be.launcher.getTotalFlyingTicks());
        for (IntAttached<ItemStack> intAttached : be.launchedItems) {
            float time = (float)((Integer)intAttached.getFirst()).intValue() + partialTicks;
            if (time > maxTime) continue;
            ms.pushPose();
            Vec3 launchedItemLocation = be.getLaunchedItemLocation(time);
            msr.translate(launchedItemLocation.subtract(Vec3.atLowerCornerOf((Vec3i)be.getBlockPos())));
            Vec3 itemRotOffset = VecHelper.voxelSpace((double)0.0, (double)2.0, (double)-1.0);
            msr.translate(itemRotOffset);
            if (PackageItem.isPackage((ItemStack)intAttached.getValue())) {
                ms.translate(0.0f, 0.25f, 0.0f);
                ms.scale(1.5f, 1.5f, 1.5f);
                msr.rotateYDegrees(time * 20.0f);
            } else {
                ms.scale(0.5f, 0.5f, 0.5f);
                msr.rotateYDegrees(AngleHelper.horizontalAngle((Direction)be.getFacing()));
                msr.rotateXDegrees(time * 40.0f);
            }
            msr.translateBack(itemRotOffset);
            Minecraft.getInstance().getItemRenderer().renderStatic((ItemStack)intAttached.getValue(), ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
            ms.popPose();
        }
        DepotBehaviour behaviour = be.getBehaviour(DepotBehaviour.TYPE);
        if (behaviour == null || behaviour.isEmpty()) {
            return;
        }
        ms.pushPose();
        EjectorRenderer.applyLidAngle(be, angle, msr);
        ((PoseTransformStack)((PoseTransformStack)msr.center()).rotateYDegrees(-180.0f - AngleHelper.horizontalAngle((Direction)((Direction)be.getBlockState().getValue(EjectorBlock.HORIZONTAL_FACING))))).uncenter();
        DepotRenderer.renderItemsOf(be, partialTicks, ms, buffer, light, overlay, behaviour);
        ms.popPose();
    }

    static <T extends Translate<T> & Rotate<T>> void applyLidAngle(KineticBlockEntity be, float angle, T tr) {
        EjectorRenderer.applyLidAngle(be, pivot, angle, tr);
    }

    static <T extends Translate<T> & Rotate<T>> void applyLidAngle(KineticBlockEntity be, Vec3 rotationOffset, float angle, T tr) {
        ((Translate)((Rotate)((Translate)((Rotate)tr.center()).rotateYDegrees(180.0f + AngleHelper.horizontalAngle((Direction)((Direction)be.getBlockState().getValue(EjectorBlock.HORIZONTAL_FACING))))).uncenter().translate(rotationOffset)).rotateXDegrees(-angle)).translateBack(rotationOffset);
    }
}
