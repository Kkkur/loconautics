package com.lycoris.loconautics.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.core.PhysicsTrainTag;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;
import com.lycoris.loconautics.server.assembly.SubLevelDisassembler;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;

/**
 * Disassembles physics trains via Sable instead of Create.
 *
 * <p>Create's {@code Train.disassemble} positions each carriage entity (so we learn exactly where
 * it should land) and then calls {@code entity.disassemble()} to stamp the assembly-time blocks
 * back into the world. We redirect that call: for a physics train we instead disassemble the live
 * Sable sub-level at the carriage's landing spot (preserving broken blocks and chest contents) and
 * discard the phantom entity. The rest of {@code Train.disassemble} (removing the train, etc.) runs
 * normally. Registry/sub-level cleanup is handled by the tick-time orphan sweep once the train is
 * gone from Create.
 */
@Mixin(Train.class)
public class TrainDisassembleMixin {

    @Redirect(
            method = "disassemble",
            at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/entity/CarriageContraptionEntity;disassemble()V")
    )
    private void loconautics$disassembleViaSable(CarriageContraptionEntity entity) {
        Train self = (Train) (Object) this;

        if (entity.level() instanceof ServerLevel level) {
            MinecraftServer server = level.getServer();
            PhysicsTrainTag tag = PhysicsTrainRegistry.get(server).get(self.id);
            if (tag != null) {
                int index = entity.carriageIndex;
                if (index >= 0 && index < tag.carriages().size()) {
                    java.util.UUID subLevelId = tag.carriages().get(index).subLevelId();
                    BlockPos goal = BlockPos.containing(entity.position());
                    try {
                        if (SubLevelDisassembler.disassembleCarriage(server, subLevelId, goal, Rotation.NONE)) {
                            entity.discard(); // skip Create's stale block placement
                            return;
                        }
                    } catch (Throwable t) {
                        LoconauticsConstants.LOGGER.error(
                                "Sable disassembly failed for carriage {}; falling back to Create", index, t);
                    }
                }
            }
        }

        // Normal (non-physics) path, or fallback on error.
        entity.disassemble();
    }
}
