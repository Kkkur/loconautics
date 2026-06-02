/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.server.level.DistanceManager
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.entity.server_entities_tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ServerLevel.class})
public class ServerLevelMixin {
    @WrapOperation(method={"*"}, at={@At(value="INVOKE", target="Lnet/minecraft/server/level/DistanceManager;inEntityTickingRange(J)Z")})
    private boolean sable$wrapEntityTickingRange(DistanceManager instance, long l, Operation<Boolean> original) {
        ChunkPos chunkPos = new ChunkPos(l);
        SubLevelContainer container = SubLevelContainer.getContainer((Level)this);
        PlotChunkHolder chunkHolder = container.getChunkHolder(chunkPos);
        if (chunkHolder != null) {
            return true;
        }
        return (Boolean)original.call(new Object[]{instance, l});
    }
}
