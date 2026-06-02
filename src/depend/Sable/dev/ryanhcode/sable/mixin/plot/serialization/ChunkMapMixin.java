/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.FullChunkStatus
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.progress.ChunkProgressListener
 *  net.minecraft.util.thread.BlockableEventLoop
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.chunk.ChunkGenerator
 *  net.minecraft.world.level.chunk.LightChunkGetter
 *  net.minecraft.world.level.entity.ChunkStatusUpdateListener
 *  net.minecraft.world.level.entity.Visibility
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager
 *  net.minecraft.world.level.storage.LevelStorageSource$LevelStorageAccess
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.plot.serialization;

import com.mojang.datafixers.DataFixer;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.entity.Visibility;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChunkMap.class})
public class ChunkMapMixin {
    @Mutable
    @Shadow
    @Final
    private Queue<Runnable> unloadQueue;
    @Shadow
    @Final
    private ServerLevel level;

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void sable$init(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor executor, BlockableEventLoop blockableEventLoop, LightChunkGetter lightChunkGetter, ChunkGenerator chunkGenerator, ChunkProgressListener chunkProgressListener, ChunkStatusUpdateListener chunkStatusUpdateListener, Supplier supplier, int i, boolean bl, CallbackInfo ci) {
        this.unloadQueue = new ConcurrentLinkedDeque<Runnable>();
    }

    @Inject(method={"onFullChunkStatusChange"}, at={@At(value="TAIL")})
    private void sable$onStatusChange(ChunkPos chunkPos, FullChunkStatus fullChunkStatus, CallbackInfo ci) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        assert (container != null) : "Sub-level container is null";
        if (container.inBounds(chunkPos)) {
            return;
        }
        SubLevelHoldingChunkMap holdingChunkMap = container.getHoldingChunkMap();
        holdingChunkMap.updateChunkStatus(chunkPos, Visibility.fromFullChunkStatus((FullChunkStatus)fullChunkStatus) != Visibility.HIDDEN);
    }
}
