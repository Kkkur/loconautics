/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.DyeColor
 */
package com.simibubi.create;

import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.content.logistics.box.PackageStyles;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class AllPartialModels {
    public static final PartialModel SCHEMATICANNON_CONNECTOR = AllPartialModels.block("schematicannon/connector");
    public static final PartialModel SCHEMATICANNON_PIPE = AllPartialModels.block("schematicannon/pipe");
    public static final PartialModel SHAFTLESS_COGWHEEL = AllPartialModels.block("cogwheel_shaftless");
    public static final PartialModel SHAFTLESS_LARGE_COGWHEEL = AllPartialModels.block("large_cogwheel_shaftless");
    public static final PartialModel COGWHEEL_SHAFT = AllPartialModels.block("cogwheel_shaft");
    public static final PartialModel SHAFT_HALF = AllPartialModels.block("shaft_half");
    public static final PartialModel SHAFT = AllPartialModels.block("shaft");
    public static final PartialModel COGWHEEL = AllPartialModels.block("cogwheel");
    public static final PartialModel BELT_PULLEY = AllPartialModels.block("belt_pulley");
    public static final PartialModel BELT_START = AllPartialModels.block("belt/start");
    public static final PartialModel BELT_MIDDLE = AllPartialModels.block("belt/middle");
    public static final PartialModel BELT_END = AllPartialModels.block("belt/end");
    public static final PartialModel BELT_START_BOTTOM = AllPartialModels.block("belt/start_bottom");
    public static final PartialModel BELT_MIDDLE_BOTTOM = AllPartialModels.block("belt/middle_bottom");
    public static final PartialModel BELT_END_BOTTOM = AllPartialModels.block("belt/end_bottom");
    public static final PartialModel BELT_DIAGONAL_START = AllPartialModels.block("belt/diagonal_start");
    public static final PartialModel BELT_DIAGONAL_MIDDLE = AllPartialModels.block("belt/diagonal_middle");
    public static final PartialModel BELT_DIAGONAL_END = AllPartialModels.block("belt/diagonal_end");
    public static final PartialModel ANDESITE_BELT_COVER_X = AllPartialModels.block("belt_cover/andesite_belt_cover_x");
    public static final PartialModel BRASS_BELT_COVER_X = AllPartialModels.block("belt_cover/brass_belt_cover_x");
    public static final PartialModel ANDESITE_BELT_COVER_Z = AllPartialModels.block("belt_cover/andesite_belt_cover_z");
    public static final PartialModel BRASS_BELT_COVER_Z = AllPartialModels.block("belt_cover/brass_belt_cover_z");
    public static final PartialModel ENCASED_FAN_INNER = AllPartialModels.block("encased_fan/propeller");
    public static final PartialModel HAND_CRANK_HANDLE = AllPartialModels.block("hand_crank/handle");
    public static final PartialModel HAND_CRANK_BASE = AllPartialModels.block("hand_crank/block");
    public static final PartialModel VALVE_HANDLE = AllPartialModels.block("valve_handle");
    public static final PartialModel MECHANICAL_PRESS_HEAD = AllPartialModels.block("mechanical_press/head");
    public static final PartialModel MECHANICAL_MIXER_POLE = AllPartialModels.block("mechanical_mixer/pole");
    public static final PartialModel MECHANICAL_MIXER_HEAD = AllPartialModels.block("mechanical_mixer/head");
    public static final PartialModel MECHANICAL_CRAFTER_LID = AllPartialModels.block("mechanical_crafter/lid");
    public static final PartialModel MECHANICAL_CRAFTER_ARROW = AllPartialModels.block("mechanical_crafter/arrow");
    public static final PartialModel MECHANICAL_CRAFTER_BELT_FRAME = AllPartialModels.block("mechanical_crafter/belt");
    public static final PartialModel MECHANICAL_CRAFTER_BELT = AllPartialModels.block("mechanical_crafter/belt_animated");
    public static final PartialModel SAW_BLADE_HORIZONTAL_ACTIVE = AllPartialModels.block("mechanical_saw/blade_horizontal_active");
    public static final PartialModel SAW_BLADE_HORIZONTAL_INACTIVE = AllPartialModels.block("mechanical_saw/blade_horizontal_inactive");
    public static final PartialModel SAW_BLADE_HORIZONTAL_REVERSED = AllPartialModels.block("mechanical_saw/blade_horizontal_reversed");
    public static final PartialModel SAW_BLADE_VERTICAL_ACTIVE = AllPartialModels.block("mechanical_saw/blade_vertical_active");
    public static final PartialModel SAW_BLADE_VERTICAL_INACTIVE = AllPartialModels.block("mechanical_saw/blade_vertical_inactive");
    public static final PartialModel SAW_BLADE_VERTICAL_REVERSED = AllPartialModels.block("mechanical_saw/blade_vertical_reversed");
    public static final PartialModel GAUGE_DIAL = AllPartialModels.block("gauge/dial");
    public static final PartialModel GAUGE_INDICATOR = AllPartialModels.block("gauge/indicator");
    public static final PartialModel GAUGE_HEAD_SPEED = AllPartialModels.block("gauge/speedometer/head");
    public static final PartialModel GAUGE_HEAD_STRESS = AllPartialModels.block("gauge/stressometer/head");
    public static final PartialModel BEARING_TOP = AllPartialModels.block("bearing/top");
    public static final PartialModel BEARING_TOP_WOODEN = AllPartialModels.block("bearing/top_wooden");
    public static final PartialModel DRILL_HEAD = AllPartialModels.block("mechanical_drill/head");
    public static final PartialModel HARVESTER_BLADE = AllPartialModels.block("mechanical_harvester/blade");
    public static final PartialModel DEPLOYER_POLE = AllPartialModels.block("deployer/pole");
    public static final PartialModel DEPLOYER_HAND_POINTING = AllPartialModels.block("deployer/hand_pointing");
    public static final PartialModel DEPLOYER_HAND_PUNCHING = AllPartialModels.block("deployer/hand_punching");
    public static final PartialModel DEPLOYER_HAND_HOLDING = AllPartialModels.block("deployer/hand_holding");
    public static final PartialModel ANALOG_LEVER_HANDLE = AllPartialModels.block("analog_lever/handle");
    public static final PartialModel ANALOG_LEVER_INDICATOR = AllPartialModels.block("analog_lever/indicator");
    public static final PartialModel FUNNEL_FLAP = AllPartialModels.block("funnel/flap");
    public static final PartialModel BELT_FUNNEL_FLAP = AllPartialModels.block("belt_funnel/flap");
    public static final PartialModel BELT_TUNNEL_FLAP = AllPartialModels.block("belt_tunnel/flap");
    public static final PartialModel FLEXPEATER_INDICATOR = AllPartialModels.block("diodes/indicator");
    public static final PartialModel ROLLER_WHEEL = AllPartialModels.block("mechanical_roller/wheel");
    public static final PartialModel ROLLER_FRAME = AllPartialModels.block("mechanical_roller/frame");
    public static final PartialModel CUCKOO_MINUTE_HAND = AllPartialModels.block("cuckoo_clock/minute_hand");
    public static final PartialModel CUCKOO_HOUR_HAND = AllPartialModels.block("cuckoo_clock/hour_hand");
    public static final PartialModel CUCKOO_LEFT_DOOR = AllPartialModels.block("cuckoo_clock/left_door");
    public static final PartialModel CUCKOO_RIGHT_DOOR = AllPartialModels.block("cuckoo_clock/right_door");
    public static final PartialModel CUCKOO_PIG = AllPartialModels.block("cuckoo_clock/pig");
    public static final PartialModel CUCKOO_CREEPER = AllPartialModels.block("cuckoo_clock/creeper");
    public static final PartialModel GANTRY_COGS = AllPartialModels.block("gantry_carriage/wheels");
    public static final PartialModel ROPE_COIL = AllPartialModels.block("rope_pulley/rope_coil");
    public static final PartialModel ROPE_HALF = AllPartialModels.block("rope_pulley/rope_half");
    public static final PartialModel ROPE_HALF_MAGNET = AllPartialModels.block("rope_pulley/rope_half_magnet");
    public static final PartialModel ROPE = AllPartialModels.block("rope_pulley/rope");
    public static final PartialModel PULLEY_MAGNET = AllPartialModels.block("rope_pulley/pulley_magnet");
    public static final PartialModel HOSE_COIL = AllPartialModels.block("hose_pulley/hose_coil");
    public static final PartialModel HOSE = AllPartialModels.block("hose_pulley/rope");
    public static final PartialModel HOSE_MAGNET = AllPartialModels.block("hose_pulley/pulley_magnet");
    public static final PartialModel HOSE_HALF = AllPartialModels.block("hose_pulley/rope_half");
    public static final PartialModel HOSE_HALF_MAGNET = AllPartialModels.block("hose_pulley/rope_half_magnet");
    public static final PartialModel ELEVATOR_COIL = AllPartialModels.block("elevator_pulley/rope_coil");
    public static final PartialModel ELEVATOR_MAGNET = AllPartialModels.block("elevator_pulley/pulley_magnet");
    public static final PartialModel ELEVATOR_BELT = AllPartialModels.block("elevator_pulley/rope");
    public static final PartialModel ELEVATOR_BELT_HALF = AllPartialModels.block("elevator_pulley/rope_half");
    public static final PartialModel MILLSTONE_COG = AllPartialModels.block("millstone/inner");
    public static final PartialModel SYMMETRY_PLANE = AllPartialModels.block("symmetry_effect/plane");
    public static final PartialModel SYMMETRY_CROSSPLANE = AllPartialModels.block("symmetry_effect/crossplane");
    public static final PartialModel SYMMETRY_TRIPLEPLANE = AllPartialModels.block("symmetry_effect/tripleplane");
    public static final PartialModel STICKER_HEAD = AllPartialModels.block("sticker/head");
    public static final PartialModel DESK_BELL_PLUNGER = AllPartialModels.block("desk_bell/plunger");
    public static final PartialModel DESK_BELL_BELL = AllPartialModels.block("desk_bell/bell");
    public static final PartialModel PORTABLE_STORAGE_INTERFACE_MIDDLE = AllPartialModels.block("portable_storage_interface/block_middle");
    public static final PartialModel PORTABLE_STORAGE_INTERFACE_MIDDLE_POWERED = AllPartialModels.block("portable_storage_interface/block_middle_powered");
    public static final PartialModel PORTABLE_STORAGE_INTERFACE_TOP = AllPartialModels.block("portable_storage_interface/block_top");
    public static final PartialModel PORTABLE_FLUID_INTERFACE_MIDDLE = AllPartialModels.block("portable_fluid_interface/block_middle");
    public static final PartialModel PORTABLE_FLUID_INTERFACE_MIDDLE_POWERED = AllPartialModels.block("portable_fluid_interface/block_middle_powered");
    public static final PartialModel PORTABLE_FLUID_INTERFACE_TOP = AllPartialModels.block("portable_fluid_interface/block_top");
    public static final PartialModel ARM_COG = AllPartialModels.block("mechanical_arm/cog");
    public static final PartialModel ARM_BASE = AllPartialModels.block("mechanical_arm/base");
    public static final PartialModel ARM_LOWER_BODY = AllPartialModels.block("mechanical_arm/lower_body");
    public static final PartialModel ARM_UPPER_BODY = AllPartialModels.block("mechanical_arm/upper_body");
    public static final PartialModel ARM_CLAW_BASE = AllPartialModels.block("mechanical_arm/claw_base");
    public static final PartialModel ARM_CLAW_BASE_GOGGLES = AllPartialModels.block("mechanical_arm/claw_base_goggles");
    public static final PartialModel ARM_CLAW_GRIP_UPPER = AllPartialModels.block("mechanical_arm/upper_claw_grip");
    public static final PartialModel ARM_CLAW_GRIP_LOWER = AllPartialModels.block("mechanical_arm/lower_claw_grip");
    public static final PartialModel MECHANICAL_PUMP_COG = AllPartialModels.block("mechanical_pump/cog");
    public static final PartialModel FLUID_PIPE_CASING = AllPartialModels.block("fluid_pipe/casing");
    public static final PartialModel FLUID_VALVE_POINTER = AllPartialModels.block("fluid_valve/pointer");
    public static final PartialModel SPOUT_TOP = AllPartialModels.block("spout/top");
    public static final PartialModel SPOUT_MIDDLE = AllPartialModels.block("spout/middle");
    public static final PartialModel SPOUT_BOTTOM = AllPartialModels.block("spout/bottom");
    public static final PartialModel PECULIAR_BELL = AllPartialModels.block("peculiar_bell");
    public static final PartialModel HAUNTED_BELL = AllPartialModels.block("haunted_bell");
    public static final PartialModel TOOLBOX_DRAWER = AllPartialModels.block("toolbox/drawer");
    public static final PartialModel SPEED_CONTROLLER_BRACKET = AllPartialModels.block("rotation_speed_controller/bracket");
    public static final PartialModel GOGGLES = AllPartialModels.block("goggles");
    public static final PartialModel EJECTOR_TOP = AllPartialModels.block("weighted_ejector/top");
    public static final PartialModel CHAIN_CONVEYOR_WHEEL = AllPartialModels.block("chain_conveyor/wheel");
    public static final PartialModel CHAIN_CONVEYOR_GUARD = AllPartialModels.block("chain_conveyor/guard");
    public static final PartialModel CHAIN_CONVEYOR_SHAFT = AllPartialModels.block("chain_conveyor/shaft");
    public static final PartialModel FROGPORT_BODY = AllPartialModels.block("package_frogport/body");
    public static final PartialModel FROGPORT_HEAD = AllPartialModels.block("package_frogport/head");
    public static final PartialModel FROGPORT_HEAD_GOGGLES = AllPartialModels.block("package_frogport/head_goggles");
    public static final PartialModel FROGPORT_TONGUE = AllPartialModels.block("package_frogport/tongue");
    public static final PartialModel POSTBOX_FLAG = AllPartialModels.block("package_postbox/flag");
    public static final PartialModel PACKAGER_TRAY_REGULAR = AllPartialModels.block("packager/tray");
    public static final PartialModel PACKAGER_TRAY_DEFRAG = AllPartialModels.block("repackager/tray");
    public static final PartialModel PACKAGER_HATCH_OPEN = AllPartialModels.block("packager/hatch_open");
    public static final PartialModel PACKAGER_HATCH_CLOSED = AllPartialModels.block("packager/hatch_closed");
    public static final PartialModel TABLE_CLOTH_PRICE_SIDE = AllPartialModels.block("table_cloth/price_tag_side");
    public static final PartialModel TABLE_CLOTH_PRICE_TOP = AllPartialModels.block("table_cloth/price_tag_top");
    public static final PartialModel COPPER_BACKTANK_SHAFT = AllPartialModels.block("copper_backtank/block_shaft_input");
    public static final PartialModel COPPER_BACKTANK_COGS = AllPartialModels.block("copper_backtank/block_cogs");
    public static final PartialModel NETHERITE_BACKTANK_SHAFT = AllPartialModels.block("netherite_backtank/block_shaft_input");
    public static final PartialModel NETHERITE_BACKTANK_COGS = AllPartialModels.block("netherite_backtank/block_cogs");
    public static final PartialModel TRACK_SEGMENT_LEFT = AllPartialModels.block("track/segment_left");
    public static final PartialModel TRACK_SEGMENT_RIGHT = AllPartialModels.block("track/segment_right");
    public static final PartialModel TRACK_TIE = AllPartialModels.block("track/tie");
    public static final PartialModel GIRDER_SEGMENT_TOP = AllPartialModels.block("metal_girder/segment_top");
    public static final PartialModel GIRDER_SEGMENT_MIDDLE = AllPartialModels.block("metal_girder/segment_middle");
    public static final PartialModel GIRDER_SEGMENT_BOTTOM = AllPartialModels.block("metal_girder/segment_bottom");
    public static final PartialModel TRACK_STATION_OVERLAY = AllPartialModels.block("track_overlay/station");
    public static final PartialModel TRACK_SIGNAL_OVERLAY = AllPartialModels.block("track_overlay/signal");
    public static final PartialModel TRACK_ASSEMBLING_OVERLAY = AllPartialModels.block("track_overlay/assembling");
    public static final PartialModel TRACK_SIGNAL_DUAL_OVERLAY = AllPartialModels.block("track_overlay/signal_dual");
    public static final PartialModel TRACK_OBSERVER_OVERLAY = AllPartialModels.block("track_overlay/observer");
    public static final PartialModel BOGEY_FRAME = AllPartialModels.block("track/bogey/bogey_frame");
    public static final PartialModel SMALL_BOGEY_WHEELS = AllPartialModels.block("track/bogey/bogey_wheel");
    public static final PartialModel BOGEY_PIN = AllPartialModels.block("track/bogey/bogey_drive_wheel_pin");
    public static final PartialModel BOGEY_PISTON = AllPartialModels.block("track/bogey/bogey_drive_piston");
    public static final PartialModel BOGEY_DRIVE = AllPartialModels.block("track/bogey/bogey_drive");
    public static final PartialModel LARGE_BOGEY_WHEELS = AllPartialModels.block("track/bogey/bogey_drive_wheel");
    public static final PartialModel BOGEY_DRIVE_BELT = AllPartialModels.block("track/bogey/bogey_drive_belt");
    public static final PartialModel TRAIN_COUPLING_HEAD = AllPartialModels.block("track/bogey/coupling_head");
    public static final PartialModel TRAIN_COUPLING_CABLE = AllPartialModels.block("track/bogey/coupling_cable");
    public static final PartialModel TRAIN_CONTROLS_COVER = AllPartialModels.block("controls/train/cover");
    public static final PartialModel TRAIN_CONTROLS_LEVER = AllPartialModels.block("controls/train/lever");
    public static final PartialModel CONTRAPTION_CONTROLS_BUTTON = AllPartialModels.block("contraption_controls/button");
    public static final PartialModel ENGINE_PISTON = AllPartialModels.block("steam_engine/piston");
    public static final PartialModel ENGINE_LINKAGE = AllPartialModels.block("steam_engine/linkage");
    public static final PartialModel ENGINE_CONNECTOR = AllPartialModels.block("steam_engine/shaft_connector");
    public static final PartialModel BOILER_GAUGE = AllPartialModels.block("steam_engine/gauge");
    public static final PartialModel BOILER_GAUGE_DIAL = AllPartialModels.block("steam_engine/gauge_dial");
    public static final PartialModel SIGNAL_ON = AllPartialModels.block("track_signal/indicator_on");
    public static final PartialModel SIGNAL_OFF = AllPartialModels.block("track_signal/indicator_off");
    public static final PartialModel DISPLAY_LINK_TUBE = AllPartialModels.block("display_link/tube");
    public static final PartialModel DISPLAY_LINK_GLOW = AllPartialModels.block("display_link/glow");
    public static final PartialModel STATION_ON = AllPartialModels.block("track_station/flag_on");
    public static final PartialModel STATION_OFF = AllPartialModels.block("track_station/flag_off");
    public static final PartialModel STATION_ASSEMBLE = AllPartialModels.block("track_station/flag_assemble");
    public static final PartialModel SIGNAL_PANEL = AllPartialModels.block("track_signal/panel");
    public static final PartialModel SIGNAL_WHITE_CUBE = AllPartialModels.block("track_signal/white_cube");
    public static final PartialModel SIGNAL_WHITE_GLOW = AllPartialModels.block("track_signal/white_glow");
    public static final PartialModel SIGNAL_WHITE = AllPartialModels.block("track_signal/white_tube");
    public static final PartialModel SIGNAL_RED_CUBE = AllPartialModels.block("track_signal/red_cube");
    public static final PartialModel SIGNAL_RED_GLOW = AllPartialModels.block("track_signal/red_glow");
    public static final PartialModel SIGNAL_RED = AllPartialModels.block("track_signal/red_tube");
    public static final PartialModel SIGNAL_YELLOW_CUBE = AllPartialModels.block("track_signal/yellow_cube");
    public static final PartialModel SIGNAL_YELLOW_GLOW = AllPartialModels.block("track_signal/yellow_glow");
    public static final PartialModel SIGNAL_YELLOW = AllPartialModels.block("track_signal/yellow_tube");
    public static final PartialModel SIGNAL_COMPUTER_WHITE_CUBE = AllPartialModels.block("track_signal/computer_white_cube");
    public static final PartialModel SIGNAL_COMPUTER_WHITE_GLOW = AllPartialModels.block("track_signal/computer_white_glow");
    public static final PartialModel SIGNAL_COMPUTER_WHITE = AllPartialModels.block("track_signal/computer_white_tube");
    public static final PartialModel SIGNAL_COMPUTER_WHITE_BASE = AllPartialModels.block("track_signal/computer_white_tube_base");
    public static final PartialModel BLAZE_CAGE = AllPartialModels.block("blaze_burner/block");
    public static final PartialModel BLAZE_INERT = AllPartialModels.block("blaze_burner/blaze/inert");
    public static final PartialModel BLAZE_SUPER_ACTIVE = AllPartialModels.block("blaze_burner/blaze/super_active");
    public static final PartialModel BLAZE_GOGGLES = AllPartialModels.block("blaze_burner/goggles");
    public static final PartialModel BLAZE_GOGGLES_SMALL = AllPartialModels.block("blaze_burner/goggles_small");
    public static final PartialModel BLAZE_IDLE = AllPartialModels.block("blaze_burner/blaze/idle");
    public static final PartialModel BLAZE_ACTIVE = AllPartialModels.block("blaze_burner/blaze/active");
    public static final PartialModel BLAZE_SUPER = AllPartialModels.block("blaze_burner/blaze/super");
    public static final PartialModel BLAZE_BURNER_FLAME = AllPartialModels.block("blaze_burner/flame");
    public static final PartialModel BLAZE_BURNER_RODS = AllPartialModels.block("blaze_burner/rods_small");
    public static final PartialModel BLAZE_BURNER_RODS_2 = AllPartialModels.block("blaze_burner/rods_large");
    public static final PartialModel BLAZE_BURNER_SUPER_RODS = AllPartialModels.block("blaze_burner/superheated_rods_small");
    public static final PartialModel BLAZE_BURNER_SUPER_RODS_2 = AllPartialModels.block("blaze_burner/superheated_rods_large");
    public static final PartialModel WHISTLE_MOUTH_LARGE = AllPartialModels.block("steam_whistle/large_mouth");
    public static final PartialModel WHISTLE_MOUTH_MEDIUM = AllPartialModels.block("steam_whistle/medium_mouth");
    public static final PartialModel WHISTLE_MOUTH_SMALL = AllPartialModels.block("steam_whistle/small_mouth");
    public static final PartialModel WATER_WHEEL = AllPartialModels.block("water_wheel/wheel");
    public static final PartialModel LARGE_WATER_WHEEL = AllPartialModels.block("large_water_wheel/block");
    public static final PartialModel LARGE_WATER_WHEEL_EXTENSION = AllPartialModels.block("large_water_wheel/block_extension");
    public static final PartialModel FACTORY_PANEL = AllPartialModels.block("factory_gauge/panel");
    public static final PartialModel FACTORY_PANEL_WITH_BULB = AllPartialModels.block("factory_gauge/panel_with_bulb");
    public static final PartialModel FACTORY_PANEL_RESTOCKER = AllPartialModels.block("factory_gauge/panel_restocker");
    public static final PartialModel FACTORY_PANEL_RESTOCKER_WITH_BULB = AllPartialModels.block("factory_gauge/panel_restocker_with_bulb");
    public static final PartialModel FACTORY_PANEL_LIGHT = AllPartialModels.block("factory_gauge/bulb_light");
    public static final PartialModel FACTORY_PANEL_RED_LIGHT = AllPartialModels.block("factory_gauge/bulb_red");
    public static final PartialModel TABLE_CLOTH_NW = AllPartialModels.block("table_cloth/north_west");
    public static final PartialModel TABLE_CLOTH_NE = AllPartialModels.block("table_cloth/north_east");
    public static final PartialModel TABLE_CLOTH_SW = AllPartialModels.block("table_cloth/south_west");
    public static final PartialModel TABLE_CLOTH_SE = AllPartialModels.block("table_cloth/south_east");
    public static final PartialModel FLYWHEEL = AllPartialModels.block("flywheel/block");
    public static final PartialModel CRUSHING_WHEEL = AllPartialModels.block("crushing_wheel/block");
    public static final PartialModel TURNTABLE = AllPartialModels.block("turntable");
    public static final PartialModel GANTRY_SHAFT_START = AllPartialModels.block("gantry_shaft/block_start");
    public static final PartialModel GANTRY_SHAFT_END = AllPartialModels.block("gantry_shaft/block_end");
    public static final PartialModel GANTRY_SHAFT_MIDDLE = AllPartialModels.block("gantry_shaft/block_middle");
    public static final PartialModel GANTRY_SHAFT_SINGLE = AllPartialModels.block("gantry_shaft/block_single");
    public static final PartialModel POWERED_SHAFT = AllPartialModels.block("powered_shaft");
    public static final PartialModel CRAFTING_BLUEPRINT_1x1 = AllPartialModels.entity("crafting_blueprint_small");
    public static final PartialModel CRAFTING_BLUEPRINT_2x2 = AllPartialModels.entity("crafting_blueprint_medium");
    public static final PartialModel CRAFTING_BLUEPRINT_3x3 = AllPartialModels.entity("crafting_blueprint_large");
    public static final PartialModel TRAIN_HAT = AllPartialModels.entity("train_hat");
    public static final PartialModel LOGISTICS_HAT = AllPartialModels.entity("logistics_hat");
    public static final PartialModel COUPLING_ATTACHMENT = AllPartialModels.entity("minecart_coupling/attachment");
    public static final PartialModel COUPLING_RING = AllPartialModels.entity("minecart_coupling/ring");
    public static final PartialModel COUPLING_CONNECTOR = AllPartialModels.entity("minecart_coupling/connector");
    public static final Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>> PIPE_ATTACHMENTS = new EnumMap<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>>(FluidTransportBehaviour.AttachmentTypes.ComponentPartials.class);
    public static final Map<Direction, PartialModel> FACTORY_PANEL_ARROWS = new EnumMap<Direction, PartialModel>(Direction.class);
    public static final Map<Direction, PartialModel> FACTORY_PANEL_LINES = new EnumMap<Direction, PartialModel>(Direction.class);
    public static final Map<Direction, PartialModel> FACTORY_PANEL_DOTTED = new EnumMap<Direction, PartialModel>(Direction.class);
    public static final Map<Direction, PartialModel> METAL_GIRDER_BRACKETS = new EnumMap<Direction, PartialModel>(Direction.class);
    public static final Map<DyeColor, PartialModel> TOOLBOX_LIDS = new EnumMap<DyeColor, PartialModel>(DyeColor.class);
    public static final Map<DyeColor, PartialModel> DYED_VALVE_HANDLES = new EnumMap<DyeColor, PartialModel>(DyeColor.class);
    public static final Map<ResourceLocation, Couple<PartialModel>> FOLDING_DOORS = new HashMap<ResourceLocation, Couple<PartialModel>>();
    public static final List<PartialModel> CONTRAPTION_CONTROLS_INDICATOR = new ArrayList<PartialModel>();
    public static final Map<ResourceLocation, PartialModel> PACKAGES = new HashMap<ResourceLocation, PartialModel>();
    public static final List<PartialModel> PACKAGES_TO_HIDE_AS = new ArrayList<PartialModel>();
    public static final Map<ResourceLocation, PartialModel> PACKAGE_RIGGING = new HashMap<ResourceLocation, PartialModel>();
    public static final Map<GantryShaftKey, PartialModel> GANTRY_SHAFTS = new HashMap<GantryShaftKey, PartialModel>();

    private static void putFoldingDoor(String path) {
        FOLDING_DOORS.put(Create.asResource(path), (Couple<PartialModel>)Couple.create((Object)AllPartialModels.block(path + "/fold_left"), (Object)AllPartialModels.block(path + "/fold_right")));
    }

    private static PartialModel block(String path) {
        return PartialModel.of((ResourceLocation)Create.asResource("block/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of((ResourceLocation)Create.asResource("entity/" + path));
    }

    public static void init() {
    }

    static {
        for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials componentPartials : FluidTransportBehaviour.AttachmentTypes.ComponentPartials.values()) {
            HashMap<Direction, PartialModel> map = new HashMap<Direction, PartialModel>();
            Direction[] directionArray = Iterate.directions;
            int n = directionArray.length;
            for (int i = 0; i < n; ++i) {
                Direction d = directionArray[i];
                String asId = Lang.asId((String)componentPartials.name());
                map.put(d, AllPartialModels.block("fluid_pipe/" + asId + "/" + Lang.asId((String)d.getSerializedName())));
            }
            PIPE_ATTACHMENTS.put(componentPartials, map);
        }
        for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials componentPartials : DyeColor.values()) {
            TOOLBOX_LIDS.put((DyeColor)componentPartials, AllPartialModels.block("toolbox/lid/" + Lang.asId((String)componentPartials.name())));
            DYED_VALVE_HANDLES.put((DyeColor)componentPartials, AllPartialModels.block(Lang.asId((String)componentPartials.name()) + "_valve_handle"));
        }
        for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials componentPartials : Iterate.horizontalDirections) {
            METAL_GIRDER_BRACKETS.put((Direction)componentPartials, AllPartialModels.block("metal_girder/bracket_" + Lang.asId((String)componentPartials.name())));
            FACTORY_PANEL_ARROWS.put((Direction)componentPartials, AllPartialModels.block("factory_gauge/connections/arrow_" + Lang.asId((String)componentPartials.name())));
            FACTORY_PANEL_LINES.put((Direction)componentPartials, AllPartialModels.block("factory_gauge/connections/line_" + Lang.asId((String)componentPartials.name())));
            FACTORY_PANEL_DOTTED.put((Direction)componentPartials, AllPartialModels.block("factory_gauge/connections/dotted_" + Lang.asId((String)componentPartials.name())));
        }
        for (int i = 0; i < 8; ++i) {
            CONTRAPTION_CONTROLS_INDICATOR.add(AllPartialModels.block("contraption_controls/indicator_" + i));
        }
        AllPartialModels.putFoldingDoor("andesite_door");
        AllPartialModels.putFoldingDoor("copper_door");
        for (PackageStyles.PackageStyle style : PackageStyles.STYLES) {
            ResourceLocation key = style.getItemId();
            PartialModel partialModel = PartialModel.of((ResourceLocation)Create.asResource("item/" + key.getPath()));
            PACKAGES.put(key, partialModel);
            if (!style.rare()) {
                PACKAGES_TO_HIDE_AS.add(partialModel);
            }
            PACKAGE_RIGGING.put(key, PartialModel.of((ResourceLocation)style.getRiggingModel()));
        }
        for (Object object : (Object)Iterate.trueAndFalse) {
            for (boolean powered : Iterate.trueAndFalse) {
                for (GantryShaftBlock.Part part : GantryShaftBlock.Part.values()) {
                    GantryShaftKey key = new GantryShaftKey(part, powered, (boolean)object);
                    GANTRY_SHAFTS.put(key, PartialModel.of((ResourceLocation)key.name()));
                }
            }
        }
    }

    public record GantryShaftKey(GantryShaftBlock.Part part, boolean powered, boolean flipped) {
        private ResourceLocation name() {
            String partName = this.part.getSerializedName();
            if (!this.flipped && !this.powered) {
                return Create.asResource("block/gantry_shaft/block_" + partName);
            }
            String flipped = this.flipped ? "_flipped" : "";
            String powered = this.powered ? "_powered" : "";
            return Create.asResource("block/gantry_shaft_" + partName + powered + flipped);
        }
    }
}
