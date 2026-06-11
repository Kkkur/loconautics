package com.lycoris.loconautics.mixin;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Client-side access to Create's package-private {@code virtualAnimation} {@link LerpedFloat} on
 * {@link AbstractBogeyBlockEntity}. {@code animate()} only calls {@code setValue()} on this float, which sets
 * its current value without snapshotting a previous one — so {@link AbstractBogeyBlockEntity#getVirtualAngle}
 * has nothing to interpolate against. {@code BogeyWheelAnimator} uses this accessor to tick the chaser each
 * frame after animating, keeping the previous/current pair valid so the wheels actually move.
 */
@Mixin(value = AbstractBogeyBlockEntity.class, remap = false)
public interface AbstractBogeyBlockEntityAccessor {

    /** Create's per-bogey wheel-angle {@code LerpedFloat} (degrees), driven by {@code animate()}. */
    @Accessor("virtualAnimation")
    LerpedFloat loconautics$getVirtualAnimation();
}
