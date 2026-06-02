/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.Transform
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.logistics.packagePort.postbox;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlock;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Transform;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class PostboxRenderer
extends SmartBlockEntityRenderer<PostboxBlockEntity> {
    public PostboxRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(PostboxBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (blockEntity.addressFilter != null && !blockEntity.addressFilter.isBlank()) {
            this.renderNameplateOnHover(blockEntity, (Component)Component.literal((String)blockEntity.addressFilter), 1.0f, ms, buffer, light);
        }
        SuperByteBuffer sbb = CachedBuffers.partial((PartialModel)AllPartialModels.POSTBOX_FLAG, (BlockState)blockEntity.getBlockState());
        sbb.light(light).overlay(overlay).rotateCentered((float)Math.PI / 180 * (180.0f - ((Direction)blockEntity.getBlockState().getValue((Property)PostboxBlock.FACING)).toYRot()), Axis.YP);
        PostboxRenderer.transformFlag(sbb, blockEntity, partialTicks);
        sbb.renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }

    public static void transformFlag(Transform<?> flag, PostboxBlockEntity be, float partialTicks) {
        float value = be.flag.getValue(partialTicks);
        float progress = (float)Math.pow(Math.min(value * 5.0f, 1.0f), 2.0);
        if (be.flag.getChaseTarget() > 0.0f && !be.flag.settled() && progress == 1.0f) {
            float wiggleProgress = (value - 0.2f) / 0.8f;
            progress = (float)((double)progress + Math.sin(wiggleProgress * ((float)Math.PI * 2) * 4.0f) / 8.0 / (double)Math.max(1.0f, 8.0f * wiggleProgress));
        }
        flag.translate(0.0f, 0.625f, 0.125f);
        flag.rotateXDegrees(-progress * 90.0f);
        flag.translateBack(0.0f, 0.625f, 0.125f);
    }
}
