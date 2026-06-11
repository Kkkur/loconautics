package com.lycoris.loconautics.mixin;

import java.util.Objects;
import java.util.UUID;

import com.lycoris.loconautics.allsable.SableStationParking;
import com.lycoris.loconautics.allsable.SableTrain;

import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Puts the station block into "train present" mode when a SABLE train is parked at it, exactly as Create's own
 * {@code StationBlockEntity.tick} does for a Create train: {@code trainPresent} raises the flag and lights the
 * comparator, {@code trainCanDisassemble} arms the screen's disassemble button, {@code imminentTrain} carries the
 * (sable) train id so the client can tell it is not a Create train.
 *
 * <p>Injected right before Create's server tick reads {@code station.getImminentTrain()} — Create's own logic
 * would always see "no train" (sable trains can't sit in {@link GlobalStation}'s {@code Train} slot) and clear
 * the display fields every tick, fighting any state we set. When a sable train is parked we therefore update the
 * fields ourselves (with the same change-detection + {@code notifyUpdate} Create uses) and cancel the remainder
 * of the tick; when none is parked, Create's logic runs untouched.
 */
@Mixin(value = StationBlockEntity.class, remap = false)
public abstract class StationBlockEntityMixin {

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/station/GlobalStation;getImminentTrain()"
                            + "Lcom/simibubi/create/content/trains/entity/Train;"),
            cancellable = true)
    private void loconautics$sableTrainPresence(CallbackInfo ci) {
        StationBlockEntity self = (StationBlockEntity) (Object) this;
        GlobalStation station = self.getStation();
        if (station == null || station.getImminentTrain() != null) {
            return; // no station, or a real Create train owns the display — let Create handle it
        }
        SableTrain parked = SableStationParking.presentTrain(station);
        if (parked == null) {
            return; // nothing parked — Create's logic clears the fields as usual
        }

        StationBlockEntityAccessor acc = (StationBlockEntityAccessor) self;
        UUID imminentId = parked.id();
        boolean canDisassemble = self.edgePoint.isOrthogonal();

        if (!acc.loconautics$getTrainPresent()
                || acc.loconautics$getTrainCanDisassemble() != canDisassemble
                || !Objects.equals(imminentId, acc.loconautics$getImminentTrain())) {
            acc.loconautics$setImminentTrain(imminentId);
            acc.loconautics$setTrainPresent(true);
            acc.loconautics$setTrainCanDisassemble(canDisassemble);
            acc.loconautics$setTrainBackwards(false);
            acc.loconautics$setTrainHasSchedule(false);
            acc.loconautics$setTrainHasAutoSchedule(false);
            self.notifyUpdate();
        }
        ci.cancel();
    }
}
