package com.lycoris.loconautics.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.lycoris.loconautics.server.assembly.PhysicsTrainDisassembler;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/**
 * Removes a physics train's Sable sub-levels the moment the player disassembles it, so the
 * physics object can't drift off the rails or linger as a duplicate when the train is re-assembled.
 *
 * <p>Create's own disassembly returns the carriage blocks to the world, so we only delete the
 * (now redundant) sub-levels — handled by {@link PhysicsTrainDisassembler}.
 */
@Mixin(Train.class)
public class TrainDisassembleMixin {

    @Inject(method = "disassemble", at = @At("HEAD"))
    private void loconautics$removePhysicsSubLevels(Direction assemblyDirection, BlockPos pos,
                                                    CallbackInfoReturnable<Boolean> cir) {
        Train self = (Train) (Object) this;
        for (Carriage carriage : self.carriages) {
            CarriageContraptionEntity entity = carriage.anyAvailableEntity();
            if (entity != null && entity.level() instanceof ServerLevel serverLevel) {
                PhysicsTrainDisassembler.disassemble(serverLevel.getServer(), self.id);
                return;
            }
        }
    }
}
