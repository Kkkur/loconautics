/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.infrastructure.ponder.AllCreatePonderTags
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.createmod.ponder.api.registration.PonderTagRegistrationHelper
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package dev.simulated_team.simulated.index;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimItems;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class SimPonderTags {
    public static final ResourceLocation NAVIGATION_ITEMS = Simulated.path("navigation_items");
    public static final ResourceLocation PHYSICS_BEHAVIOR = Simulated.path("physics_behavior");
    public static final ResourceLocation THRUST_PRODUCING_BLOCKS = Simulated.path("thrust_blocks");
    public static final ResourceLocation PHYSICS_SENSORS = Simulated.path("physics_sensors");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper itemHelper = helper.withKeyFunction(RegisteredObjectsHelper::getKeyOrThrow);
        helper.registerTag(NAVIGATION_ITEMS).addToIndex().item((ItemLike)SimBlocks.NAVIGATION_TABLE.asItem()).title("Navigation Items").description("Components which offer a destination to a Navigation Table").register();
        itemHelper.addToTag(NAVIGATION_ITEMS).add((Object)SimBlocks.NAVIGATION_TABLE.asItem()).add((Object)Items.COMPASS).add((Object)Blocks.LODESTONE).add((Object)Items.RECOVERY_COMPASS).add((Object)Items.FILLED_MAP).add((Object)SimBlocks.REDSTONE_MAGNET.asItem());
        helper.registerTag(PHYSICS_BEHAVIOR).addToIndex().item((ItemLike)SimBlocks.PHYSICS_ASSEMBLER.asItem()).title("Physics Behavior").description("Components which have unique physics behavior or interactions").register();
        itemHelper.addToTag(PHYSICS_BEHAVIOR).add((Object)SimBlocks.PHYSICS_ASSEMBLER.asItem()).add((Object)SimBlocks.SWIVEL_BEARING.asItem()).add((Object)AllBlocks.STICKER.asItem()).add((Object)AllBlocks.WEIGHTED_EJECTOR.asItem()).add((Object)SimBlocks.DOCKING_CONNECTOR.asItem()).add((Object)SimBlocks.REDSTONE_MAGNET.asItem()).add((Object)AllBlocks.SAIL.asItem()).add((Object)SimBlocks.WHITE_SYMMETRIC_SAIL.asItem()).add((Object)AllItems.BELT_CONNECTOR.asItem()).add((Object)SimItems.SPRING.asItem()).add((Object)SimItems.ROPE_COUPLING.asItem());
        helper.registerTag(THRUST_PRODUCING_BLOCKS).addToIndex().item((ItemLike)AllBlocks.ENCASED_FAN.asItem()).title("Thrust Producing Blocks").description("Components which produce thrust on Simulated Contraptions").register();
        itemHelper.addToTag(THRUST_PRODUCING_BLOCKS).add((Object)AllBlocks.ENCASED_FAN.asItem()).add((Object)AllBlocks.NOZZLE.asItem());
        helper.registerTag(PHYSICS_SENSORS).addToIndex().item((ItemLike)SimBlocks.OPTICAL_SENSOR.asItem()).title("Physics Sensor Blocks").description("Components which provide dynamic information about the world around them").register();
        itemHelper.addToTag(PHYSICS_SENSORS).add((Object)SimBlocks.ALTITUDE_SENSOR.asItem()).add((Object)SimBlocks.VELOCITY_SENSOR.asItem()).add((Object)SimBlocks.GIMBAL_SENSOR.asItem()).add((Object)SimBlocks.OPTICAL_SENSOR.asItem()).add((Object)SimBlocks.NAVIGATION_TABLE.asItem()).add((Object)SimBlocks.LASER_SENSOR.asItem());
        itemHelper.addToTag(AllCreatePonderTags.KINETIC_RELAYS).add((Object)SimBlocks.DIRECTIONAL_GEARSHIFT.asItem()).add((Object)SimBlocks.TORSION_SPRING.asItem()).add((Object)SimBlocks.ANALOG_TRANSMISSION.asItem());
        itemHelper.addToTag(AllCreatePonderTags.KINETIC_SOURCES).add((Object)SimBlocks.STEERING_WHEEL.asItem()).add((Object)SimBlocks.RED_PORTABLE_ENGINE.asItem());
        itemHelper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).add((Object)SimBlocks.SWIVEL_BEARING.asItem()).add((Object)SimBlocks.ROPE_WINCH.asItem());
        itemHelper.addToTag(AllCreatePonderTags.FLUIDS).add((Object)SimBlocks.DOCKING_CONNECTOR.asItem());
        itemHelper.addToTag(AllCreatePonderTags.LOGISTICS).add((Object)SimBlocks.AUGER_SHAFT.asItem()).add((Object)SimBlocks.AUGER_COG.asItem()).add((Object)SimBlocks.DOCKING_CONNECTOR.asItem());
        itemHelper.addToTag(AllCreatePonderTags.REDSTONE).add((Object)SimBlocks.THROTTLE_LEVER.asItem()).add((Object)SimBlocks.LINKED_TYPEWRITER.asItem()).add((Object)SimBlocks.DIRECTIONAL_LINKED_RECEIVER.asItem()).add((Object)SimBlocks.MODULATING_LINKED_RECEIVER.asItem()).add((Object)SimBlocks.REDSTONE_ACCUMULATOR.asItem()).add((Object)SimBlocks.REDSTONE_INDUCTOR.asItem());
        itemHelper.addToTag(AllCreatePonderTags.MOVEMENT_ANCHOR).add((Object)SimBlocks.PHYSICS_ASSEMBLER.asItem()).add((Object)SimBlocks.SWIVEL_BEARING.asItem());
        itemHelper.addToTag(AllCreatePonderTags.SAILS).add((Object)SimBlocks.WHITE_SYMMETRIC_SAIL.asItem());
        itemHelper.addToTag(AllCreatePonderTags.ARM_TARGETS).add((Object)SimBlocks.RED_PORTABLE_ENGINE.asItem()).add((Object)SimBlocks.NAVIGATION_TABLE.asItem());
        itemHelper.addToTag(AllCreatePonderTags.DISPLAY_SOURCES).add((Object)SimBlocks.AUGER_SHAFT.asItem()).add((Object)SimBlocks.AUGER_COG.asItem()).add((Object)SimBlocks.RED_PORTABLE_ENGINE.asItem()).add((Object)SimBlocks.ALTITUDE_SENSOR.asItem()).add((Object)SimBlocks.VELOCITY_SENSOR.asItem()).add((Object)SimBlocks.GIMBAL_SENSOR.asItem()).add((Object)SimBlocks.OPTICAL_SENSOR.asItem()).add((Object)SimBlocks.NAVIGATION_TABLE.asItem()).add((Object)SimBlocks.DOCKING_CONNECTOR.asItem()).add((Object)SimBlocks.LINKED_TYPEWRITER.asItem());
        itemHelper.addToTag(AllCreatePonderTags.DISPLAY_TARGETS).add((Object)SimBlocks.NAMEPLATES.get(DyeColor.WHITE).asItem());
        itemHelper.addToTag(AllCreatePonderTags.THRESHOLD_SWITCH_TARGETS).add((Object)SimBlocks.ROPE_WINCH.asItem());
    }
}
