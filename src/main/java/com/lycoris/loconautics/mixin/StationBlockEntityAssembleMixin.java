package com.lycoris.loconautics.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.server.assembly.PhysicsAssemblyContext;
import com.lycoris.loconautics.server.assembly.SubLevelBridge;

import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

@Mixin(StationBlockEntity.class)
public class StationBlockEntityAssembleMixin {

    @Redirect(
            method = "assemble",
            at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/entity/CarriageContraption;" +
                            "removeBlocksFromWorld(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V")
    )
    private void loconautics$captureToSubLevel(CarriageContraption contraption, Level level, BlockPos offset) {
        if (PhysicsAssemblyContext.isPending() && level instanceof ServerLevel serverLevel) {
            try {
                ServerSubLevel subLevel = SubLevelBridge.createFromContraption(serverLevel, contraption);
                if (subLevel != null) {
                    PhysicsAssemblyContext.addSubLevel(subLevel.getUniqueId(), contraption.anchor);
                    return;
                }
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("Failed to divert carriage to Sable sub-level; falling back", t);
            }
        }
        contraption.removeBlocksFromWorld(level, offset);
    }
}