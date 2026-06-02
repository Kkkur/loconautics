/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  net.minecraft.server.level.ChunkHolder
 *  net.minecraft.server.level.ChunkMap
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.ChunkPos
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.plot;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.List;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ChunkMap.class})
public class ChunkMapMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @Inject(method={"getPlayers"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$getPlayers(ChunkPos chunkPos, boolean bl, CallbackInfoReturnable<List<ServerPlayer>> cir) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        if (container.inBounds(chunkPos)) {
            List<ServerPlayer> players = container.getPlayersTracking(chunkPos);
            cir.setReturnValue(players);
        }
    }

    @Inject(method={"saveChunkIfNeeded"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$saveChunkIfNeeded(ChunkHolder chunkHolder, CallbackInfoReturnable<Boolean> cir) {
        if (chunkHolder instanceof PlotChunkHolder) {
            cir.setReturnValue((Object)false);
        }
    }

    @Redirect(method={"hasWork"}, at=@At(value="INVOKE", target="Lit/unimi/dsi/fastutil/longs/Long2ObjectLinkedOpenHashMap;isEmpty()Z", ordinal=1, remap=false))
    private boolean sable$hasWork(Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap) {
        return !updatingChunkMap.values().stream().anyMatch(chunkHolder -> !(chunkHolder instanceof PlotChunkHolder));
    }

    @Inject(method={"isChunkTracked"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$isChunkTracked(ServerPlayer serverPlayer, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        LevelPlot plot = container.getPlot(new ChunkPos(i, j));
        if (plot != null) {
            ServerSubLevel subLevel = (ServerSubLevel)plot.getSubLevel();
            cir.setReturnValue((Object)subLevel.getTrackingPlayers().contains(serverPlayer.getGameProfile().getId()));
        }
    }

    @Inject(method={"anyPlayerCloseEnoughForSpawning"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$anyPlayerCloseEnoughForSpawning(ChunkPos chunkPos, CallbackInfoReturnable<Boolean> cir) {
        LevelPlot plot;
        ServerSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        assert (container != null);
        if (container.inBounds(chunkPos) && (plot = container.getPlot(chunkPos)) != null) {
            ServerSubLevel subLevel = (ServerSubLevel)plot.getSubLevel();
            cir.setReturnValue((Object)(!subLevel.getTrackingPlayers().isEmpty() ? 1 : 0));
        }
    }
}
