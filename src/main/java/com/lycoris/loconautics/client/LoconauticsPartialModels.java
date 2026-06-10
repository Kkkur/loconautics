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

    // Steel Cable rope strand models — textures provided by the artist under
    // assets/loconautics/textures/block/steel_cable/
    public static final PartialModel STEEL_CABLE_ROPE =
            PartialModel.of(LoconauticsConstants.id("block/steel_cable/rope"));
    public static final PartialModel STEEL_CABLE_KNOT =
            PartialModel.of(LoconauticsConstants.id("block/steel_cable/knot"));

    /** Call from LoconauticsClient during FMLClientSetupEvent to trigger static init. */
    public static void init() {}
}