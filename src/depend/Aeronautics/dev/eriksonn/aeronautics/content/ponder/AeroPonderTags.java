/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.infrastructure.ponder.AllCreatePonderTags
 *  dev.simulated_team.simulated.index.SimPonderTags
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.createmod.ponder.api.registration.PonderTagRegistrationHelper
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package dev.eriksonn.aeronautics.content.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.service.AeroLevititeService;
import dev.simulated_team.simulated.index.SimPonderTags;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class AeroPonderTags {
    public static final ResourceLocation LEVITITE_BREAKABLE = Aeronautics.path("levitite_breakable");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper itemHelper = helper.withKeyFunction(RegisteredObjectsHelper::getKeyOrThrow);
        helper.registerTag(LEVITITE_BREAKABLE).item((ItemLike)AeroLevititeService.INSTANCE.getBucket()).title("Breaks When Crystallizing").description("Blocks that are broken when nearby Levitite Blend crystallizes into Levitite. Useful for making molds for casting").register();
        itemHelper.addToTag(LEVITITE_BREAKABLE).add((Object)AeroLevititeService.INSTANCE.getBucket());
        itemHelper.addToTag(LEVITITE_BREAKABLE).add((Object)Blocks.CLAY).add((Object)Blocks.MUD).add((Object)Blocks.PACKED_MUD).add((Object)Blocks.COARSE_DIRT);
        itemHelper.addToTag(SimPonderTags.PHYSICS_BEHAVIOR).add((Object)AeroBlocks.PROPELLER_BEARING.asItem()).add((Object)AeroBlocks.GYROSCOPIC_PROPELLER_BEARING.asItem()).add((Object)AeroBlocks.SMART_PROPELLER.asItem()).add((Object)AeroBlocks.ANDESITE_PROPELLER.asItem()).add((Object)AeroBlocks.WOODEN_PROPELLER.asItem()).add((Object)AeroBlocks.WHITE_ENVELOPE_BLOCK.asItem()).add((Object)AeroBlocks.HOT_AIR_BURNER.asItem()).add((Object)AeroBlocks.STEAM_VENT.asItem()).add((Object)AeroBlocks.LEVITITE.asItem()).add((Object)AeroBlocks.PEARLESCENT_LEVITITE.asItem());
        itemHelper.addToTag(SimPonderTags.THRUST_PRODUCING_BLOCKS).add((Object)AeroBlocks.PROPELLER_BEARING.asItem()).add((Object)AeroBlocks.GYROSCOPIC_PROPELLER_BEARING.asItem()).add((Object)AeroBlocks.SMART_PROPELLER.asItem()).add((Object)AeroBlocks.ANDESITE_PROPELLER.asItem()).add((Object)AeroBlocks.WOODEN_PROPELLER.asItem());
        itemHelper.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).add((Object)AeroBlocks.PROPELLER_BEARING.asItem()).add((Object)AeroBlocks.GYROSCOPIC_PROPELLER_BEARING.asItem()).add((Object)AeroBlocks.SMART_PROPELLER.asItem()).add((Object)AeroBlocks.ANDESITE_PROPELLER.asItem()).add((Object)AeroBlocks.WOODEN_PROPELLER.asItem()).add((Object)AeroBlocks.MOUNTED_POTATO_CANNON.asItem());
        itemHelper.addToTag(AllCreatePonderTags.ARM_TARGETS).add((Object)AeroBlocks.MOUNTED_POTATO_CANNON.asItem());
        itemHelper.addToTag(AllCreatePonderTags.THRESHOLD_SWITCH_TARGETS).add((Object)AeroBlocks.HOT_AIR_BURNER.asItem()).add((Object)AeroBlocks.STEAM_VENT.asItem());
        itemHelper.addToTag(AllCreatePonderTags.DISPLAY_SOURCES).add((Object)AeroBlocks.HOT_AIR_BURNER.asItem()).add((Object)AeroBlocks.STEAM_VENT.asItem()).add((Object)AeroBlocks.PROPELLER_BEARING.asItem()).add((Object)AeroBlocks.GYROSCOPIC_PROPELLER_BEARING.asItem()).add((Object)AeroBlocks.SMART_PROPELLER.asItem()).add((Object)AeroBlocks.ANDESITE_PROPELLER.asItem()).add((Object)AeroBlocks.WOODEN_PROPELLER.asItem());
    }
}
