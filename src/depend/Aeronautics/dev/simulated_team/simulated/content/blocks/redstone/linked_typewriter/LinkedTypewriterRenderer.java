/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterInteractionHandler;
import dev.simulated_team.simulated.index.SimPartialModels;
import java.util.Vector;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class LinkedTypewriterRenderer
extends SmartBlockEntityRenderer<LinkedTypewriterBlockEntity> {
    static Vector<LerpedFloat> keys = new Vector(14);

    public LinkedTypewriterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public static void tick() {
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        if (LinkedTypewriterInteractionHandler.getMode() == LinkedTypewriterInteractionHandler.Mode.IDLE) {
            return;
        }
        for (int i = 0; i < keys.size(); ++i) {
            LerpedFloat lerpedFloat = keys.get(i);
            lerpedFloat.chase(LinkedTypewriterInteractionHandler.getPressedKeys().contains(i) ? 1.0 : 0.0, (double)0.4f, LerpedFloat.Chaser.EXP);
            lerpedFloat.tickChaser();
        }
    }

    public static void resetKeys() {
        for (LerpedFloat key : keys) {
            key.startWithValue(0.0);
        }
    }

    protected void renderSafe(LinkedTypewriterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        int i;
        super.renderSafe((SmartBlockEntity)be, partialTicks, ms, buffer, light, overlay);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
        BlockState blockState = be.getBlockState();
        Direction facing = (Direction)blockState.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
        PoseTransformStack ps = TransformStack.of((PoseStack)ms);
        float pt = AnimationTickHolder.getPartialTicks();
        float s = 0.0625f;
        float b = -0.046875f;
        int index = 0;
        ps.translate(0.5, 0.25, 0.5);
        ps.rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing));
        ps.pushPose();
        if (LinkedTypewriterInteractionHandler.getMode() == LinkedTypewriterInteractionHandler.Mode.BIND) {
            i = (int)Mth.lerp((float)((Mth.sin((float)(AnimationTickHolder.getRenderTime() / 4.0f)) + 1.0f) / 2.0f), (float)5.0f, (float)15.0f);
            light = i << 20;
        }
        ps.translate(-0.4375f, 0.0625f, 0.125f);
        ps.pushPose();
        for (i = 0; i < 6; ++i) {
            ps.translate(0.125, 0.0, 0.0);
            LinkedTypewriterRenderer.renderKey(ms, vb, light, pt, blockState, be, -0.046875f, index++, false);
        }
        ms.popPose();
        ps.translate(-0.0625f, -0.0625f, 0.125f);
        ps.pushPose();
        for (i = 0; i < 7; ++i) {
            ps.translate(0.125, 0.0, 0.0);
            LinkedTypewriterRenderer.renderKey(ms, vb, light, pt, blockState, be, -0.046875f, index++, false);
        }
        ms.popPose();
        ps.translate(0.5f, -0.0625f, 0.125f);
        ps.pushPose();
        LinkedTypewriterRenderer.renderKey(ms, vb, light, pt, blockState, be, -0.046875f, index, true);
        ms.popPose();
        ms.popPose();
    }

    protected static void renderKey(PoseStack ms, VertexConsumer vb, int light, float pt, BlockState blockState, LinkedTypewriterBlockEntity be, float b, int index, boolean isSpacebar) {
        ms.pushPose();
        float depression = 0.0f;
        if (be.checkUser(Minecraft.getInstance().player.getUUID())) {
            depression = b * keys.get(index).getValue(pt);
        }
        ms.translate(0.0f, depression, 0.0f);
        if (!isSpacebar) {
            CachedBuffers.partial((PartialModel)SimPartialModels.LINKED_TYPEWRITER_KEY, (BlockState)blockState).light(light).renderInto(ms, vb);
        } else {
            CachedBuffers.partial((PartialModel)SimPartialModels.LINKED_TYPEWRITER_KEY_SPACEBAR, (BlockState)blockState).light(light).renderInto(ms, vb);
        }
        ms.popPose();
    }

    static {
        for (int i = 0; i < 14; ++i) {
            keys.add(LerpedFloat.linear().startWithValue(0.0));
        }
    }
}
