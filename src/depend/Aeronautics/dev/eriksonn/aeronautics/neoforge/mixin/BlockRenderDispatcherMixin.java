/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  org.spongepowered.asm.mixin.Dynamic
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.eriksonn.aeronautics.neoforge.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.eriksonn.aeronautics.index.AeroTags;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={BlockRenderDispatcher.class})
public class BlockRenderDispatcherMixin {
    @WrapMethod(method={"renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/neoforged/neoforge/client/model/data/ModelData;)V"})
    @Dynamic
    public void renderBreakingTexture(BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, ModelData modelData, Operation<Void> original) {
        if (state.is(AeroTags.BlockTags.LEVITITE)) {
            poseStack.pushPose();
            float s = 1.0E-4f;
            poseStack.translate(-s, -s, -s);
            poseStack.scale(1.0f + (s *= 2.0f), 1.0f + s, 1.0f + s);
            original.call(new Object[]{state, pos, level, poseStack, consumer, modelData});
            poseStack.popPose();
        } else {
            original.call(new Object[]{state, pos, level, poseStack, consumer, modelData});
        }
    }
}
