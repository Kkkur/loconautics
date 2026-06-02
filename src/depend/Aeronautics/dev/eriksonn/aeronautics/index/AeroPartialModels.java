/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.resources.ResourceLocation
 */
package dev.eriksonn.aeronautics.index;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.eriksonn.aeronautics.Aeronautics;
import net.minecraft.resources.ResourceLocation;

public class AeroPartialModels {
    public static final PartialModel STEAM_VENT_REDSTONE = AeroPartialModels.block("steam_vent/redstone");
    public static final PartialModel STEAM_VENT_BASE = AeroPartialModels.block("steam_vent/steam_base");
    public static final PartialModel STEAM_VENT_JET = AeroPartialModels.block("steam_vent/steam_jet");
    public static final PartialModel BEARING_PLATE = AeroPartialModels.block("propeller_bearing/bearing_plate");
    public static final PartialModel BEARING_PLATE_METAL = AeroPartialModels.block("gyroscopic_propeller_bearing/metal_bearing_plate");
    public static final PartialModel GYRO_BEARING_PISTON_HEAD = AeroPartialModels.block("gyroscopic_propeller_bearing/piston_head");
    public static final PartialModel GYRO_BEARING_PISTON_POLE = AeroPartialModels.block("gyroscopic_propeller_bearing/piston_pole");
    public static final PartialModel HOT_AIR_BURNER_INDICATOR = AeroPartialModels.block("adjustable_burner/redstone_indicator");
    public static final PartialModel CANNON_BARREL = AeroPartialModels.block("mounted_potato_cannon/partials/barrel");
    public static final PartialModel CANNON_BELLOW = AeroPartialModels.block("mounted_potato_cannon/partials/bellow");
    public static final PartialModel CANNON_COG = AeroPartialModels.block("mounted_potato_cannon/partials/cog");
    public static final PartialModel ANDESITE_PROPELLER = AeroPartialModels.block("andesite_propeller/propeller");
    public static final PartialModel WOODEN_PROPELLER = AeroPartialModels.block("wooden_propeller/propeller");
    public static final PartialModel ANDESITE_PROPELLER_REVERSED = AeroPartialModels.block("andesite_propeller/propeller_reversed");
    public static final PartialModel WOODEN_PROPELLER_REVERSED = AeroPartialModels.block("wooden_propeller/propeller_reversed");
    public static final PartialModel SMART_PROPELLER = AeroPartialModels.block("smart_propeller/propeller");
    public static final PartialModel SMART_PROPELLER_REVERSED = AeroPartialModels.block("smart_propeller/propeller_reversed");
    public static final PartialModel SMART_PROPELLER_HINGE = AeroPartialModels.block("smart_propeller/hinge");

    private static PartialModel block(String path) {
        return PartialModel.of((ResourceLocation)Aeronautics.path("block/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of((ResourceLocation)Aeronautics.path("entity/" + path));
    }

    private static PartialModel item(String path) {
        return PartialModel.of((ResourceLocation)Aeronautics.path("item/" + path));
    }

    public static void init() {
    }
}
