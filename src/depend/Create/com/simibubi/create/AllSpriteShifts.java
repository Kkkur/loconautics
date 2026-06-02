/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SpriteShifter
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.block.WeatheringCopper$WeatherState
 *  net.minecraft.world.level.block.state.properties.WoodType
 */
package com.simibubi.create;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.properties.WoodType;

public class AllSpriteShifts {
    private static final Map<WoodType, CTSpriteShiftEntry> WOODEN_WINDOWS = new IdentityHashMap<WoodType, CTSpriteShiftEntry>();
    public static final Map<WeatheringCopper.WeatherState, CTSpriteShiftEntry> COPPER_SHINGLES = new EnumMap<WeatheringCopper.WeatherState, CTSpriteShiftEntry>(WeatheringCopper.WeatherState.class);
    public static final Map<WeatheringCopper.WeatherState, CTSpriteShiftEntry> COPPER_TILES = new EnumMap<WeatheringCopper.WeatherState, CTSpriteShiftEntry>(WeatheringCopper.WeatherState.class);
    public static final Map<DyeColor, SpriteShiftEntry> DYED_BELTS = new EnumMap<DyeColor, SpriteShiftEntry>(DyeColor.class);
    public static final Map<DyeColor, SpriteShiftEntry> DYED_OFFSET_BELTS = new EnumMap<DyeColor, SpriteShiftEntry>(DyeColor.class);
    public static final Map<DyeColor, SpriteShiftEntry> DYED_DIAGONAL_BELTS = new EnumMap<DyeColor, SpriteShiftEntry>(DyeColor.class);
    public static final SpriteShiftEntry BURNER_FLAME = AllSpriteShifts.get("block/blaze_burner_flame", "block/blaze_burner_flame_scroll");
    public static final SpriteShiftEntry SUPER_BURNER_FLAME = AllSpriteShifts.get("block/blaze_burner_flame", "block/blaze_burner_flame_superheated_scroll");
    public static final CTSpriteShiftEntry ANDESITE_SCAFFOLD = AllSpriteShifts.horizontal("scaffold/andesite_scaffold");
    public static final CTSpriteShiftEntry BRASS_SCAFFOLD = AllSpriteShifts.horizontal("scaffold/brass_scaffold");
    public static final CTSpriteShiftEntry COPPER_SCAFFOLD = AllSpriteShifts.horizontal("scaffold/copper_scaffold");
    public static final CTSpriteShiftEntry ANDESITE_SCAFFOLD_INSIDE = AllSpriteShifts.horizontal("scaffold/andesite_scaffold_inside");
    public static final CTSpriteShiftEntry BRASS_SCAFFOLD_INSIDE = AllSpriteShifts.horizontal("scaffold/brass_scaffold_inside");
    public static final CTSpriteShiftEntry COPPER_SCAFFOLD_INSIDE = AllSpriteShifts.horizontal("scaffold/copper_scaffold_inside");
    public static final CTSpriteShiftEntry FRAMED_GLASS = AllSpriteShifts.getCT(AllCTTypes.OMNIDIRECTIONAL, "palettes/framed_glass", "palettes/framed_glass");
    public static final CTSpriteShiftEntry HORIZONTAL_FRAMED_GLASS = AllSpriteShifts.getCT(AllCTTypes.HORIZONTAL_KRYPPERS, "palettes/framed_glass", "palettes/horizontal_framed_glass");
    public static final CTSpriteShiftEntry VERTICAL_FRAMED_GLASS = AllSpriteShifts.getCT(AllCTTypes.VERTICAL, "palettes/framed_glass", "palettes/vertical_framed_glass");
    public static final CTSpriteShiftEntry ORNATE_IRON_WINDOW = AllSpriteShifts.vertical("palettes/ornate_iron_window");
    public static final CTSpriteShiftEntry INDUSTRIAL_IRON_WINDOW = AllSpriteShifts.getCT(AllCTTypes.RECTANGLE, "palettes/industrial_iron_window");
    public static final CTSpriteShiftEntry OLD_FACTORY_WINDOW_1 = AllSpriteShifts.getCT(AllCTTypes.RECTANGLE, "palettes/weathered_iron_window", "palettes/weathered_iron_window_1");
    public static final CTSpriteShiftEntry OLD_FACTORY_WINDOW_2 = AllSpriteShifts.getCT(AllCTTypes.RECTANGLE, "palettes/weathered_iron_window", "palettes/weathered_iron_window_2");
    public static final CTSpriteShiftEntry OLD_FACTORY_WINDOW_3 = AllSpriteShifts.getCT(AllCTTypes.RECTANGLE, "palettes/weathered_iron_window", "palettes/weathered_iron_window_3");
    public static final CTSpriteShiftEntry OLD_FACTORY_WINDOW_4 = AllSpriteShifts.getCT(AllCTTypes.RECTANGLE, "palettes/weathered_iron_window", "palettes/weathered_iron_window_4");
    public static final CTSpriteShiftEntry CRAFTER_SIDE = AllSpriteShifts.vertical("crafter_side");
    public static final CTSpriteShiftEntry CRAFTER_OTHERSIDE = AllSpriteShifts.horizontal("crafter_side");
    public static final CTSpriteShiftEntry ANDESITE_ENCASED_COGWHEEL_SIDE = AllSpriteShifts.vertical("andesite_encased_cogwheel_side");
    public static final CTSpriteShiftEntry ANDESITE_ENCASED_COGWHEEL_OTHERSIDE = AllSpriteShifts.horizontal("andesite_encased_cogwheel_side");
    public static final CTSpriteShiftEntry BRASS_ENCASED_COGWHEEL_SIDE = AllSpriteShifts.vertical("brass_encased_cogwheel_side");
    public static final CTSpriteShiftEntry BRASS_ENCASED_COGWHEEL_OTHERSIDE = AllSpriteShifts.horizontal("brass_encased_cogwheel_side");
    public static final CTSpriteShiftEntry GIRDER_POLE = AllSpriteShifts.vertical("girder_pole_side");
    public static final CTSpriteShiftEntry ANDESITE_CASING = AllSpriteShifts.omni("andesite_casing");
    public static final CTSpriteShiftEntry BRASS_CASING = AllSpriteShifts.omni("brass_casing");
    public static final CTSpriteShiftEntry COPPER_CASING = AllSpriteShifts.omni("copper_casing");
    public static final CTSpriteShiftEntry SHADOW_STEEL_CASING = AllSpriteShifts.omni("shadow_steel_casing");
    public static final CTSpriteShiftEntry REFINED_RADIANCE_CASING = AllSpriteShifts.omni("refined_radiance_casing");
    public static final CTSpriteShiftEntry RAILWAY_CASING = AllSpriteShifts.omni("railway_casing");
    public static final CTSpriteShiftEntry RAILWAY_CASING_SIDE = AllSpriteShifts.omni("railway_casing_side");
    public static final CTSpriteShiftEntry CREATIVE_CASING = AllSpriteShifts.getCT(AllCTTypes.RECTANGLE, "creative_casing");
    public static final CTSpriteShiftEntry CHASSIS_SIDE = AllSpriteShifts.omni("linear_chassis_side");
    public static final CTSpriteShiftEntry SECONDARY_CHASSIS_SIDE = AllSpriteShifts.omni("secondary_linear_chassis_side");
    public static final CTSpriteShiftEntry CHASSIS = AllSpriteShifts.omni("linear_chassis_end");
    public static final CTSpriteShiftEntry CHASSIS_STICKY = AllSpriteShifts.omni("linear_chassis_end_sticky");
    public static final CTSpriteShiftEntry BRASS_TUNNEL_TOP = AllSpriteShifts.vertical("tunnel/brass_tunnel_top");
    public static final CTSpriteShiftEntry FLUID_TANK = AllSpriteShifts.getCT(AllCTTypes.RECTANGLE, "fluid_tank");
    public static final CTSpriteShiftEntry FLUID_TANK_TOP = AllSpriteShifts.getCT(AllCTTypes.RECTANGLE, "fluid_tank_top");
    public static final CTSpriteShiftEntry FLUID_TANK_INNER = AllSpriteShifts.getCT(AllCTTypes.RECTANGLE, "fluid_tank_inner");
    public static final CTSpriteShiftEntry CREATIVE_FLUID_TANK = AllSpriteShifts.getCT(AllCTTypes.RECTANGLE, "creative_fluid_tank");
    public static final Couple<CTSpriteShiftEntry> VAULT_TOP = AllSpriteShifts.vault("top");
    public static final Couple<CTSpriteShiftEntry> VAULT_FRONT = AllSpriteShifts.vault("front");
    public static final Couple<CTSpriteShiftEntry> VAULT_SIDE = AllSpriteShifts.vault("side");
    public static final Couple<CTSpriteShiftEntry> VAULT_BOTTOM = AllSpriteShifts.vault("bottom");
    public static final SpriteShiftEntry ELEVATOR_BELT = AllSpriteShifts.get("block/elevator_pulley_belt", "block/elevator_pulley_belt_scroll");
    public static final SpriteShiftEntry ROPE_PULLEY_COIL = AllSpriteShifts.get("block/rope_pulley_coil", "block/rope_pulley_coil_scroll");
    public static final SpriteShiftEntry ELEVATOR_COIL = AllSpriteShifts.get("block/elevator_pulley_coil", "block/elevator_pulley_coil_scroll");
    public static final SpriteShiftEntry HOSE_PULLEY_COIL = AllSpriteShifts.get("block/hose_pulley_coil", "block/hose_pulley_coil_scroll");
    public static final SpriteShiftEntry FACTORY_PANEL_CONNECTIONS = AllSpriteShifts.get("block/factory_panel_connections", "block/factory_panel_connections_animated");
    public static final SpriteShiftEntry BELT = AllSpriteShifts.get("block/belt", "block/belt_scroll");
    public static final SpriteShiftEntry BELT_OFFSET = AllSpriteShifts.get("block/belt_offset", "block/belt_scroll");
    public static final SpriteShiftEntry BELT_DIAGONAL = AllSpriteShifts.get("block/belt_diagonal", "block/belt_diagonal_scroll");
    public static final SpriteShiftEntry ANDESIDE_BELT_CASING = AllSpriteShifts.get("block/belt/brass_belt_casing", "block/belt/andesite_belt_casing");
    public static final SpriteShiftEntry CRAFTER_THINGIES = AllSpriteShifts.get("block/crafter_thingies", "block/crafter_thingies");
    public static final SpriteShiftEntry BOGEY_BELT = AllSpriteShifts.get("block/bogey/belt", "block/bogey/belt_scroll");

    private static void populateMaps() {
        WoodType[] supportedWoodTypes = new WoodType[]{WoodType.OAK, WoodType.SPRUCE, WoodType.BIRCH, WoodType.ACACIA, WoodType.JUNGLE, WoodType.DARK_OAK, WoodType.MANGROVE, WoodType.CRIMSON, WoodType.WARPED, WoodType.CHERRY, WoodType.BAMBOO};
        Arrays.stream(supportedWoodTypes).forEach(woodType -> WOODEN_WINDOWS.put((WoodType)woodType, AllSpriteShifts.vertical("palettes/" + woodType.name() + "_window")));
        for (DyeColor dyeColor : DyeColor.values()) {
            String id = dyeColor.getSerializedName();
            DYED_BELTS.put(dyeColor, AllSpriteShifts.get("block/belt", "block/belt/" + id + "_scroll"));
            DYED_OFFSET_BELTS.put(dyeColor, AllSpriteShifts.get("block/belt_offset", "block/belt/" + id + "_scroll"));
            DYED_DIAGONAL_BELTS.put(dyeColor, AllSpriteShifts.get("block/belt_diagonal", "block/belt/" + id + "_diagonal_scroll"));
        }
        for (DyeColor dyeColor : WeatheringCopper.WeatherState.values()) {
            String pref = "copper/" + (String)(dyeColor == WeatheringCopper.WeatherState.UNAFFECTED ? "" : Lang.asId((String)dyeColor.name()) + "_");
            COPPER_SHINGLES.put((WeatheringCopper.WeatherState)dyeColor, AllSpriteShifts.getCT(AllCTTypes.ROOF, pref + "copper_roof_top", pref + "copper_shingles_top"));
            COPPER_TILES.put((WeatheringCopper.WeatherState)dyeColor, AllSpriteShifts.getCT(AllCTTypes.ROOF, pref + "copper_roof_top", pref + "copper_tiles_top"));
        }
    }

    private static Couple<CTSpriteShiftEntry> vault(String name) {
        String prefixed = "block/vault/vault_" + name;
        return Couple.createWithContext(medium -> CTSpriteShifter.getCT(AllCTTypes.RECTANGLE, Create.asResource(prefixed + "_small"), Create.asResource(medium != false ? prefixed + "_medium" : prefixed + "_large")));
    }

    private static CTSpriteShiftEntry omni(String name) {
        return AllSpriteShifts.getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry horizontal(String name) {
        return AllSpriteShifts.getCT(AllCTTypes.HORIZONTAL, name);
    }

    private static CTSpriteShiftEntry vertical(String name) {
        return AllSpriteShifts.getCT(AllCTTypes.VERTICAL, name);
    }

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get((ResourceLocation)Create.asResource(originalLocation), (ResourceLocation)Create.asResource(targetLocation));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, Create.asResource("block/" + blockTextureName), Create.asResource("block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return AllSpriteShifts.getCT(type, blockTextureName, blockTextureName);
    }

    public static CTSpriteShiftEntry getWoodenWindow(WoodType woodType) {
        return WOODEN_WINDOWS.get(woodType);
    }

    static {
        AllSpriteShifts.populateMaps();
    }
}
