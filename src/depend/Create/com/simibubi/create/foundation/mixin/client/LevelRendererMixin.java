/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.BlockDestructionProgress
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package com.simibubi.create.foundation.mixin.client;

import com.google.common.collect.Sets;
import com.simibubi.create.foundation.block.render.BlockDestructionProgressExtension;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Set;
import java.util.SortedSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={LevelRenderer.class})
public class LevelRendererMixin {
    @Shadow
    private ClientLevel level;
    @Shadow
    @Final
    private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;

    @Inject(method={"destroyBlockProgress(ILnet/minecraft/core/BlockPos;I)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/server/level/BlockDestructionProgress;updateTick(I)V", shift=At.Shift.AFTER)}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void create$onDestroyBlockProgress(int breakerId, BlockPos pos, int progress, CallbackInfo ci, BlockDestructionProgress progressObj) {
        MultiPosDestructionHandler handler;
        Set<BlockPos> extraPositions;
        BlockState state = this.level.getBlockState(pos);
        IClientBlockExtensions properties = IClientBlockExtensions.of((BlockState)state);
        if (properties instanceof MultiPosDestructionHandler && (extraPositions = (handler = (MultiPosDestructionHandler)properties).getExtraPositions(this.level, pos, state, progress)) != null) {
            extraPositions.remove(pos);
            ((BlockDestructionProgressExtension)progressObj).create$setExtraPositions(extraPositions);
            for (BlockPos extraPos : extraPositions) {
                ((SortedSet)this.destructionProgress.computeIfAbsent(extraPos.asLong(), l -> Sets.newTreeSet())).add(progressObj);
            }
        }
    }

    @Inject(method={"removeProgress(Lnet/minecraft/server/level/BlockDestructionProgress;)V"}, at={@At(value="RETURN")})
    private void create$onRemoveProgress(BlockDestructionProgress progress, CallbackInfo ci) {
        Set<BlockPos> extraPositions = ((BlockDestructionProgressExtension)progress).create$getExtraPositions();
        if (extraPositions != null) {
            for (BlockPos extraPos : extraPositions) {
                long l = extraPos.asLong();
                Set set = (Set)this.destructionProgress.get(l);
                if (set == null) continue;
                set.remove(progress);
                if (!set.isEmpty()) continue;
                this.destructionProgress.remove(l);
            }
        }
    }
}
