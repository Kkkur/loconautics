/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 */
package com.simibubi.create.content.contraptions.actors.trainControls;

import net.createmod.catnip.animation.LerpedFloat;

static class ControlsMovementBehaviour.LeverAngles {
    LerpedFloat steering = LerpedFloat.linear();
    LerpedFloat speed = LerpedFloat.linear();
    LerpedFloat equipAnimation = LerpedFloat.linear();

    ControlsMovementBehaviour.LeverAngles() {
    }
}
