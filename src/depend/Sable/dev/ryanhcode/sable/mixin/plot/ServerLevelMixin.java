/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.ProgressListener
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.TickRateManager
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.ChunkSource
 *  net.minecraft.world.level.dimension.DimensionType
 *  net.minecraft.world.level.storage.WritableLevelData
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.plot;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.platform.SablePlatform;
import dev.ryanhcode.sable.sublevel.storage.SubLevelOccupancySavedData;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ServerLevel.class})
public abstract class ServerLevelMixin
extends Level {
    protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Shadow
    public abstract TickRateManager tickRateManager();

    @Shadow
    public abstract ChunkSource getChunkSource();

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void sable$init(CallbackInfo ci) {
        ServerSubLevelContainer container;
        if (!SablePlatform.INSTANCE.isWrappedLevel((Level)((ServerLevel)this))) {
            SubLevelOccupancySavedData.getOrLoad((ServerLevel)this);
        }
        if ((container = (ServerSubLevelContainer)SubLevelContainer.getContainer(this)) != null) {
            container.initialize();
        }
    }

    @Inject(method={"close"}, at={@At(value="TAIL")})
    private void sable$close(CallbackInfo ci) {
        ServerSubLevelContainer container = (ServerSubLevelContainer)SubLevelContainer.getContainer(this);
        if (container != null) {
            container.close();
        }
    }

    @Inject(method={"save"}, at={@At(value="INVOKE", target="Lnet/minecraft/server/level/ServerLevel;saveLevelData()V", shift=At.Shift.BEFORE)})
    public void sable$saveSubLevels(ProgressListener progressListener, boolean bl, boolean bl2, CallbackInfo ci) {
        Sable.LOGGER.info("Saving sub-levels for level '{}'/{}", (Object)this, (Object)this.dimension().location());
        ServerLevel self = (ServerLevel)this;
        if (progressListener != null) {
            progressListener.progressStartNoAbort((Component)Component.translatable((String)"menu.savingSubLevels"));
        }
        ServerSubLevelContainer container = SubLevelContainer.getContainer(self);
        assert (container != null) : "No sub-level container";
        SubLevelHoldingChunkMap holdingChunkMap = container.getHoldingChunkMap();
        holdingChunkMap.saveAll();
    }

    @Inject(method={"tick(Ljava/util/function/BooleanSupplier;)V"}, at={@At(value="HEAD")})
    private void sable$tickPlotContainer(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        TickRateManager tickRateManager = this.tickRateManager();
        boolean runNormally = tickRateManager.runsNormally();
        ServerSubLevelContainer plotContainer = SubLevelContainer.getContainer((ServerLevel)this);
        assert (plotContainer != null) : "SubLevelContainer is null when ticking";
        if (runNormally) {
            plotContainer.tick();
        }
    }

    @Inject(method={"shouldTickBlocksAt"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$shouldTickBlocksAt(long l, CallbackInfoReturnable<Boolean> cir) {
        ServerSubLevelContainer plotContainer = SubLevelContainer.getContainer((ServerLevel)this);
        assert (plotContainer != null);
        if (plotContainer.getPlot(new ChunkPos(l)) != null) {
            cir.setReturnValue((Object)true);
        }
    }

    @Inject(method={"isNaturalSpawningAllowed(Lnet/minecraft/world/level/ChunkPos;)Z"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$isNaturalSpawningAllowed(ChunkPos chunkPos, CallbackInfoReturnable<Boolean> cir) {
        ServerSubLevelContainer plotContainer = SubLevelContainer.getContainer((ServerLevel)this);
        assert (plotContainer != null);
        if (plotContainer.getPlot(chunkPos) != null) {
            cir.setReturnValue((Object)true);
        }
    }
}
