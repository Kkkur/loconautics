/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.ControlledContraptionEntity
 *  com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.simulated_team.simulated.mixin.torsion_spring;

import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={MechanicalBearingBlockEntity.class})
public abstract class MechanicalBearingBlockEntityMixin {
    @Redirect(method={"onSpeedChanged"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/ControlledContraptionEntity;isStalled()Z"))
    private boolean dontRoundTheAngle(ControlledContraptionEntity instance) {
        return true;
    }
}
