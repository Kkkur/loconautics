/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
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
package dev.ryanhcode.sable.mixin.plot;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.SableCommonEvents;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
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
    private BlockPos sable$blockSet = null;

    @Inject(method={"setBlockState"}, at={@At(value="HEAD")})
    private void sable$preSetBlockState(BlockPos pPos, BlockState pState, boolean pIsMoving, CallbackInfoReturnable<BlockState> cir) {
        this.sable$blockSet = pPos;
    }

    @Inject(method={"setBlockState"}, at={@At(value="RETURN")})
    private void sable$postSetBlockState(BlockPos pPos, BlockState pState, boolean pIsMoving, CallbackInfoReturnable<BlockState> cir) {
        SubLevel subLevel;
        if (this.sable$blockSet != null && (subLevel = Sable.HELPER.getContaining(this.level, (Vec3i)this.sable$blockSet)) != null) {
            subLevel.getPlot().onBlockChange(this.sable$blockSet, pState);
        }
        this.sable$blockSet = null;
    }

    @WrapOperation(method={"setBlockState"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")})
    private BlockState sable$setBlockState(LevelChunkSection instance, int pX, int pY, int pZ, BlockState newState, Operation<BlockState> original) {
        BlockState oldState = (BlockState)original.call(new Object[]{instance, pX, pY, pZ, newState});
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            if (oldState != newState) {
                pX = this.sable$blockSet.getX();
                pY = this.sable$blockSet.getY();
                pZ = this.sable$blockSet.getZ();
                SableCommonEvents.handleBlockChange(serverLevel, (LevelChunk)this, pX, pY, pZ, oldState, newState);
            }
        }
        return oldState;
    }
}
