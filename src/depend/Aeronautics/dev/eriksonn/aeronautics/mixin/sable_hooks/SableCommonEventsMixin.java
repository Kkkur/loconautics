/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.SableCommonEvents
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.eriksonn.aeronautics.mixin.sable_hooks;

import dev.eriksonn.aeronautics.events.AeronauticsCommonEvents;
import dev.ryanhcode.sable.SableCommonEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SableCommonEvents.class})
public class SableCommonEventsMixin {
    @Inject(method={"handleBlockChange"}, at={@At(value="HEAD")})
    private static void onBlockChange(ServerLevel level, LevelChunk chunk, int x, int y, int z, BlockState oldState, BlockState newState, CallbackInfo ci) {
        AeronauticsCommonEvents.onBlockModifiedEvent((LevelAccessor)level, new BlockPos(x, y, z), oldState, newState);
    }
}
