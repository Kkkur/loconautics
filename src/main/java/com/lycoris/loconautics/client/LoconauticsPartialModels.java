package com.lycoris.loconautics.client;

import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.lycoris.loconautics.core.LoconauticsConstants;

public class LoconauticsPartialModels {

    // Reuse Create's cover and lever partial models directly — no need to ship copies.
    public static final PartialModel CONTROLS_COVER = AllPartialModels.TRAIN_CONTROLS_COVER;
    public static final PartialModel CONTROLS_LEVER = AllPartialModels.TRAIN_CONTROLS_LEVER;

    // Your static block base — resolves to loconautics:block/analog_controller
    public static final PartialModel ANALOG_CONTROLLER =
            PartialModel.of(LoconauticsConstants.id("block/analog_controller"));

    /** Call from LoconauticsClient during FMLClientSetupEvent to trigger static init. */
    public static void init() {}
}