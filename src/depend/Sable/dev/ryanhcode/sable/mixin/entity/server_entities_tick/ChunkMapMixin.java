/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.ChunkMap$DistanceManager
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ChunkPos
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.entity.server_entities_tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ChunkMap.class})
public class ChunkMapMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @WrapOperation(method={"*"}, at={@At(value="INVOKE", target="Lnet/minecraft/server/level/ChunkMap$DistanceManager;inEntityTickingRange(J)Z")})
    private boolean sable$wrapEntityTickingRange(ChunkMap.DistanceManager instance, long l, Operation<Boolean> original) {
        ChunkPos chunkPos = new ChunkPos(l);
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        PlotChunkHolder chunkHolder = container.getChunkHolder(chunkPos);
        if (chunkHolder != null) {
            return true;
        }
        return (Boolean)original.call(new Object[]{instance, l});
    }
}
