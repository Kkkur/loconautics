/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.sublevel.KinematicContraption
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.offroad.mixin.borehead_collision;

import dev.ryanhcode.offroad.content.entities.BoreheadContraptionEntity;
import dev.ryanhcode.sable.api.sublevel.KinematicContraption;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={BoreheadContraptionEntity.class})
public abstract class BoreheadContraptionEntityMixin
implements KinematicContraption {
    public boolean sable$shouldCollide() {
        return false;
    }
}
