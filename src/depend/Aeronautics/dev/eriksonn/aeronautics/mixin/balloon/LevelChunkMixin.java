/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.eriksonn.aeronautics.mixin.balloon;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.BalloonMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LevelChunk.class})
public class LevelChunkMixin {
    @Shadow
    @Final
    private Level level;
    @Unique
    private BlockPos simulated$blockSet = null;

    @Inject(method={"setBlockState"}, at={@At(value="HEAD")})
    private void simulated$preSetBlockState(BlockPos pPos, BlockState pState, boolean pIsMoving, CallbackInfoReturnable<BlockState> cir) {
        this.simulated$blockSet = pPos;
    }

    @WrapOperation(method={"setBlockState"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")})
    private BlockState simulated$setBlockState(LevelChunkSection instance, int pX, int pY, int pZ, BlockState newState, Operation<BlockState> original) {
        BlockState oldState = (BlockState)original.call(new Object[]{instance, pX, pY, pZ, newState});
        if (this.level.isClientSide() && oldState != newState) {
            ((BalloonMap)BalloonMap.MAP.get((LevelAccessor)this.level)).updateNearbyBalloons(this.simulated$blockSet, oldState, newState);
        }
        return oldState;
    }
}
