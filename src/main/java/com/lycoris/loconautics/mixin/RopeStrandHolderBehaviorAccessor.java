package com.lycoris.loconautics.mixin;

import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

/**
 * Exposes private ownership fields on RopeStrandHolderBehavior so that
 * SteelCableItem can set them after manually constructing a strand,
 * without having to call RopeStrandHolderBehavior.createRope() (which
 * enforces Simulated's maxRopeRange limit).
 */
@Mixin(RopeStrandHolderBehavior.class)
public interface RopeStrandHolderBehaviorAccessor {

    @Accessor("strandOwner")
    void loconautics$setStrandOwner(boolean owner);

    @Accessor("strandOwner")
    boolean loconautics$isStrandOwner();

    @Accessor("attachedRopeID")
    void loconautics$setAttachedRopeID(UUID id);

    @Accessor("attachedRopeID")
    UUID loconautics$getAttachedRopeID();
}