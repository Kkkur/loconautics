/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 */
package dev.simulated_team.simulated.content.blocks.redstone.directional_receiver;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.simulated_team.simulated.content.blocks.redstone.directional_receiver.DirectionalLinkedReceiverBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class DirectionalLinkedReceiverRenderer
extends SmartBlockEntityRenderer<DirectionalLinkedReceiverBlockEntity> {
    public DirectionalLinkedReceiverRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(DirectionalLinkedReceiverBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        super.renderSafe((SmartBlockEntity)be, partialTicks, ms, bufferSource, light, overlay);
    }
}
