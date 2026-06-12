package com.lycoris.loconautics.mixin;

import com.lycoris.loconautics.allsable.SableTrain;
import com.lycoris.loconautics.allsable.SableTrainSpawner;
import com.lycoris.loconautics.network.packets.StationParkedSyncPacket;

import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Teaches Create's {@link StationBlockEntity} about parked Sable trains so its own disassemble UI can serve them.
 *
 * <p>Each server tick we ask {@link SableTrainSpawner#findParkedTrain} whether a custom Sable train is stopped at
 * this station, and push the result to clients with a dedicated {@link StationParkedSyncPacket} (broadcast on
 * change, and refreshed periodically while parked so a client that starts tracking the station mid-park still
 * learns about it). Detection only — the actual disassembly runs server-side from {@code DisassembleSableTrainPacket}.
 * Create's own train-present logic is left completely untouched.
 *
 * <p>remap=false: targets a Create class; NeoForge runs on Mojang mappings so the inherited vanilla members
 * ({@code getLevel}, {@code getBlockPos}) keep their source names.
 */
@Mixin(value = StationBlockEntity.class, remap = false)
public abstract class StationBlockEntitySableMixin extends SmartBlockEntity {

    /** Dummy constructor — never invoked at runtime; Mixin bypasses constructors entirely. */
    private StationBlockEntitySableMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public abstract GlobalStation getStation();

    @Shadow
    public abstract boolean isAssembling();

    /** Last detected "a Sable train is parked here" state (server-side only). */
    @Unique
    private boolean loconautics$parked;

    /** Last broadcast train name, so a rename re-broadcasts immediately (not only on the 20-tick refresh). */
    @Unique
    private String loconautics$name = "";

    /** Last broadcast consist carriage spans, so a coupling change re-broadcasts immediately. */
    @Unique
    private List<Integer> loconautics$carriages = List.of();

    /** Server-side tick counter for the periodic parked-state re-broadcast. */
    @Unique
    private int loconautics$refreshTick;

    @Inject(method = "tick", at = @At("TAIL"))
    private void loconautics$detectParkedSableTrain(CallbackInfo ci) {
        Level level = this.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        boolean parked = false;
        String name = "";
        List<Integer> carriages = List.of();
        GlobalStation station = this.getStation();
        if (!this.isAssembling() && station != null) {
            SableTrain train = SableTrainSpawner.findParkedTrain(serverLevel, station);
            if (train != null) {
                parked = true;
                name = SableTrainSpawner.trainName(serverLevel, train);
                carriages = SableTrainSpawner.consistCarriageIcons(serverLevel, train);
            }
        }

        if (parked != loconautics$parked || !name.equals(loconautics$name)
                || !carriages.equals(loconautics$carriages)) {
            loconautics$parked = parked;
            loconautics$name = name;
            loconautics$carriages = carriages;
            StationParkedSyncPacket.broadcast(this.getBlockPos(), parked, name, carriages);
        } else if (parked && loconautics$refreshTick % 20 == 0) {
            // Refresh: covers clients that began tracking this station after the flag first went true.
            StationParkedSyncPacket.broadcast(this.getBlockPos(), true, name, carriages);
        }
        loconautics$refreshTick++;
    }
}
