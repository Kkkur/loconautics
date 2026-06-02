/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientChunkCache
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData$BlockEntityTagOutput
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.chunk.status.ChunkStatus
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
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

import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.platform.SableChunkEventPlatform;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientChunkCache.class})
public abstract class ClientChunkCacheMixin {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    private ClientLevel level;
    @Shadow
    @Final
    private LevelChunk emptyChunk;

    @Shadow
    private static boolean isValidChunk(@Nullable LevelChunk levelChunk, int i, int j) {
        return false;
    }

    @Unique
    @NotNull
    private SubLevelContainer sable$getPlotContainer() {
        ClientSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        if (container == null) {
            throw new IllegalStateException("Plot container not found in level");
        }
        return container;
    }

    @Inject(method={"getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/LevelChunk;"}, at={@At(value="HEAD")}, cancellable=true)
    private void getChunk(int x, int z, ChunkStatus status, boolean create, CallbackInfoReturnable<LevelChunk> cir) {
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(x, z)) {
            ChunkPos chunkPos = new ChunkPos(x, z);
            LevelChunk chunk = container.getChunk(chunkPos);
            if (chunk != null) {
                cir.setReturnValue((Object)chunk);
            } else {
                cir.setReturnValue((Object)this.emptyChunk);
            }
        }
    }

    @Inject(method={"drop"}, at={@At(value="HEAD")}, cancellable=true)
    private void drop(ChunkPos chunkPos, CallbackInfo ci) {
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(chunkPos)) {
            ci.cancel();
            throw new UnsupportedOperationException("Cannot drop chunks in plot");
        }
    }

    @Inject(method={"replaceBiomes"}, at={@At(value="HEAD")}, cancellable=true)
    private void replaceBiomes(int x, int z, FriendlyByteBuf friendlyByteBuf, CallbackInfo ci) {
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(x, z)) {
            ChunkPos chunkPos = new ChunkPos(x, z);
            LevelChunk levelChunk = container.getChunk(chunkPos);
            if (levelChunk == null || !ClientChunkCacheMixin.isValidChunk(levelChunk, x, z)) {
                LOGGER.warn("Ignoring chunk since it's not present: {}, {}", (Object)x, (Object)z);
            } else {
                levelChunk.replaceBiomes(friendlyByteBuf);
            }
        }
    }

    @Inject(method={"replaceWithPacketData"}, at={@At(value="HEAD")}, cancellable=true)
    private void replaceWithPacketData(int x, int z, FriendlyByteBuf friendlyByteBuf, CompoundTag compoundTag, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> cir) {
        SubLevelContainer container = this.sable$getPlotContainer();
        if (container.inBounds(x, z)) {
            ChunkPos chunkPos = new ChunkPos(x, z);
            LevelChunk levelChunk = container.getChunk(chunkPos);
            if (!ClientChunkCacheMixin.isValidChunk(levelChunk, x, z)) {
                if (levelChunk != null) {
                    SableChunkEventPlatform.INSTANCE.onOldChunkInvalid(levelChunk);
                    this.level.unload(levelChunk);
                }
                levelChunk = new LevelChunk((Level)this.level, chunkPos);
                levelChunk.replaceWithPacketData(friendlyByteBuf, compoundTag, consumer);
                container.newPopulatedChunk(chunkPos, levelChunk);
            } else {
                levelChunk.replaceWithPacketData(friendlyByteBuf, compoundTag, consumer);
            }
            this.level.onChunkLoaded(chunkPos);
            this.level.getLightEngine().setLightEnabled(chunkPos, true);
            SableChunkEventPlatform.INSTANCE.onChunkPacketReplaced(levelChunk);
            cir.setReturnValue((Object)levelChunk);
        }
    }
}
