package com.lycoris.loconautics.client;

import com.lycoris.loconautics.core.LoconauticsConstants;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;

public class LoconauticsSpriteShifts {

    /**
     * Drives the winch coil scroll animation.
     * Original sprite: block/steel_cable/winch_coil  (the static disc — 16×16)
     * Scroll target:   block/steel_cable/winch_coil_scroll (the taller sheet — 16×32)
     */
    public static final SpriteShiftEntry STEEL_CABLE_WINCH_COIL = SpriteShifter.get(
            LoconauticsConstants.id("block/steel_cable/winch_coil"),
            LoconauticsConstants.id("block/steel_cable/winch_coil_scroll")
    );

    /** Called from LoconauticsClient to trigger static init. */
    public static void init() {}
}