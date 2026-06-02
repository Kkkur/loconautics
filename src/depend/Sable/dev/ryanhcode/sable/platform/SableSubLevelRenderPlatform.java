/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ryanhcode.sable.platform.SablePlatformUtil;
import dev.ryanhcode.sable.sublevel.render.vanilla.SingleBlockSubLevelWrapper;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface SableSubLevelRenderPlatform {
    public static final SableSubLevelRenderPlatform INSTANCE = SablePlatformUtil.load(SableSubLevelRenderPlatform.class);

    public void tesselateBlock(SingleBlockSubLevelWrapper var1, BakedModel var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, RandomSource var7, long var8, int var10, @Nullable RenderType var11);

    public List<RenderType> getRenderLayers(SingleBlockSubLevelWrapper var1, BakedModel var2, BlockState var3, BlockPos var4, RandomSource var5);

    public void tryAddFlywheelVisual(BlockEntity var1);
}
