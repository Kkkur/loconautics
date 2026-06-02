/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.server.level.ChunkHolder
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.ChunkResult
 *  net.minecraft.server.level.ServerChunkCache
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.progress.ChunkProgressListener
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.biome.Biomes
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkGenerator
 *  net.minecraft.world.level.chunk.EmptyLevelChunk
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.LightChunk
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  net.minecraft.world.level.entity.ChunkStatusUpdateListener
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager
 *  net.minecraft.world.level.storage.LevelStorageSource$LevelStorageAccess
 *  org.jetbrains.annotations.NotNull
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.plot;

import com.mojang.datafixers.DataFixer;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ServerChunkCache.class})
public class ServerChunkCacheMixin {
    @Shadow
    @Final
    public ChunkMap chunkMap;
    @Shadow
    @Final
    private ServerLevel level;
    @Unique
    private EmptyLevelChunk sable$emptyChunk;

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    public void init(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor executor, ChunkGenerator chunkGenerator, int i, int j, boolean bl, ChunkProgressListener chunkProgressListener, ChunkStatusUpdateListener chunkStatusUpdateListener, Supplier supplier, CallbackInfo ci) {
        this.sable$emptyChunk = new EmptyLevelChunk((Level)serverLevel, new ChunkPos(0, 0), (Holder)serverLevel.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS));
    }

    @Unique
    @NotNull
    private SubLevelContainer sable$getPlotContainer() {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        if (container == null) {
            throw new IllegalStateException("Plot container not found in level");
        }
        return container;
    }

    @Inject(method={"getChunkNow"}, at={@At(value="HEAD")}, cancellable=true)
    private void getChunkNow(int x, int z, CallbackInfoReturnable<LevelChunk> cir) {
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(x, z)) {
            LevelChunk chunk = container.getChunk(new ChunkPos(x, z));
            cir.setReturnValue((Object)chunk);
        }
    }

    @Inject(method={"getChunkFutureMainThread"}, at={@At(value="HEAD")}, cancellable=true)
    private void getChunkFutureMainThread(int x, int z, ChunkStatus chunkStatus, boolean bl, CallbackInfoReturnable<CompletableFuture<ChunkResult<ChunkAccess>>> cir) {
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(x, z)) {
            ChunkPos chunkPos = new ChunkPos(x, z);
            LevelChunk chunk = container.getChunk(chunkPos);
            if (chunk != null) {
                cir.setReturnValue(CompletableFuture.completedFuture(ChunkResult.of((Object)chunk)));
            } else {
                cir.setReturnValue(CompletableFuture.completedFuture(ChunkResult.of((Object)this.sable$emptyChunk)));
            }
        }
    }

    @Inject(method={"hasChunk"}, at={@At(value="HEAD")}, cancellable=true)
    private void hasChunk(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(x, z)) {
            LevelChunk chunk = container.getChunk(new ChunkPos(x, z));
            cir.setReturnValue((Object)(chunk != null ? 1 : 0));
        }
    }

    @Inject(method={"getChunkForLighting"}, at={@At(value="HEAD")}, cancellable=true)
    private void getChunkForLighting(int x, int z, CallbackInfoReturnable<LightChunk> cir) {
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(x, z)) {
            LevelChunk chunk = container.getChunk(new ChunkPos(x, z));
            cir.setReturnValue((Object)chunk);
        }
    }

    @Inject(method={"isPositionTicking"}, at={@At(value="HEAD")}, cancellable=true)
    private void isPositionTicking(long pos, CallbackInfoReturnable<Boolean> cir) {
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(ChunkPos.getX((long)pos), ChunkPos.getZ((long)pos))) {
            ChunkPos chunkPos = new ChunkPos(pos);
            LevelChunk chunk = container.getChunk(chunkPos);
            cir.setReturnValue((Object)(chunk != null ? 1 : 0));
        }
    }

    @Inject(method={"getFullChunk"}, at={@At(value="HEAD")}, cancellable=true)
    private void getFullChunk(long pos, Consumer<LevelChunk> consumer, CallbackInfo ci) {
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(ChunkPos.getX((long)pos), ChunkPos.getZ((long)pos))) {
            ChunkPos chunkPos = new ChunkPos(pos);
            LevelChunk chunk = container.getChunk(chunkPos);
            if (chunk != null) {
                consumer.accept(chunk);
            }
            ci.cancel();
        }
    }

    @Inject(method={"blockChanged"}, at={@At(value="HEAD")}, cancellable=true)
    private void blockChanged(BlockPos blockPos, CallbackInfo ci) {
        ChunkPos pos;
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(pos = new ChunkPos(blockPos))) {
            PlotChunkHolder holder = container.getChunkHolder(pos);
            if (holder == null) {
                throw new UnsupportedOperationException("Cannot change blocks in nonexistent plot holder");
            }
            holder.blockChanged(blockPos);
            ci.cancel();
        }
    }

    @Inject(method={"getVisibleChunkIfPresent"}, at={@At(value="HEAD")}, cancellable=true)
    private void getVisibleChunkIfPresent(long l, CallbackInfoReturnable<ChunkHolder> cir) {
        int x = ChunkPos.getX((long)l);
        int z = ChunkPos.getZ((long)l);
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(x, z)) {
            ChunkPos chunkPos = new ChunkPos(x, z);
            PlotChunkHolder holder = container.getChunkHolder(chunkPos);
            cir.setReturnValue((Object)holder);
        }
    }
}
