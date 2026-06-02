/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.data.advancements.SimAdvancements
 *  dev.simulated_team.simulated.data.advancements.SimulatedAdvancement
 *  dev.simulated_team.simulated.data.advancements.SimulatedAdvancement$Builder
 *  dev.simulated_team.simulated.data.advancements.SimulatedAdvancement$TaskType
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.NotNull
 */
package dev.eriksonn.aeronautics.index;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.data.AeroAdvancementTriggers;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.index.AeroItems;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.data.advancements.SimulatedAdvancement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class AeroAdvancements
extends SimAdvancements {
    public static final List<SimulatedAdvancement> AERO_ADVANCEMENTS = new ArrayList<SimulatedAdvancement>();
    public static final SimulatedAdvancement ROOT = AeroAdvancements.create("root", b -> b.icon(AeroBlocks.WHITE_ENVELOPE_BLOCK).title("Create Aeronautics").description("Up Up and Away").awardedForFree().special(SimulatedAdvancement.TaskType.SILENT));
    public static final SimulatedAdvancement HIGH_FASHION = AeroAdvancements.create("high_fashion", b -> b.icon(AeroItems.AVIATORS_GOGGLES).title("High Fashion").description("Obtain a pair of Aviator's Goggles").after(ROOT).whenIconCollected());
    public static final SimulatedAdvancement HEAD_IN_THE_CLOUDS = AeroAdvancements.create("head_in_the_clouds", b -> b.icon(AeroBlocks.HOT_AIR_BURNER).title("Head in the Clouds").description("Fill an airtight Envelope structure with hot air").special(SimulatedAdvancement.TaskType.NOISY).after(ROOT));
    public static final SimulatedAdvancement SONG_OF_THE_SKY = AeroAdvancements.create("song_of_the_sky", b -> b.icon(AeroItems.MUSIC_DISC_CLOUD_SKIPPER).title("Song of the Sky").description("Toss a music disc into the clouds to create something new").special(SimulatedAdvancement.TaskType.NOISY).after(HEAD_IN_THE_CLOUDS).whenIconCollected());
    public static final SimulatedAdvancement FOR_EVERY_ACTION = AeroAdvancements.create("for_every_action", b -> b.icon(AeroBlocks.WOODEN_PROPELLER).title("For Every Action...").description("Place and power a Propeller to generate Thrust").after(HEAD_IN_THE_CLOUDS));
    public static final SimulatedAdvancement IN_THRUST_WE_TRUST = AeroAdvancements.create("in_thrust_we_trust", b -> b.icon(AeroBlocks.PROPELLER_BEARING).title("In Thrust We Trust").description("Assemble a Propeller Bearing to generate more Thrust").special(SimulatedAdvancement.TaskType.NOISY).after(FOR_EVERY_ACTION));
    public static final SimulatedAdvancement HEAVIER_ARTILLERY = AeroAdvancements.create("heavier_artillery", b -> b.icon(AeroBlocks.MOUNTED_POTATO_CANNON).title("Heavier Artillery").description("Fire a vegetable from a Mounted Potato Cannon").after(ROOT));
    public static final SimulatedAdvancement GHOSTBUSTER = AeroAdvancements.create("ghostbuster", b -> b.icon((ItemLike)Items.PHANTOM_MEMBRANE).title("Ghostbuster").description("Kill a Phantom using a Mounted Potato Cannon").special(SimulatedAdvancement.TaskType.EXPERT).after(HEAVIER_ARTILLERY));
    public static final SimulatedAdvancement UNIDENTIFIED_FLOATING_OBJECT = AeroAdvancements.create("unidentified_floating_object", b -> b.icon(AeroBlocks.LEVITITE).title("Unidentified Floating Object").description("Crystallize Levitite Blend into Levitite").special(SimulatedAdvancement.TaskType.NOISY).after(HEAD_IN_THE_CLOUDS));
    public static final SimulatedAdvancement NOW_AVAILABLE_IN_PINK = AeroAdvancements.create("now_available_in_pink", b -> b.icon(AeroBlocks.PEARLESCENT_LEVITITE).title("Now Available in Pink!").description("Crystallize Levitite Blend into Pearlescent Levitite").special(SimulatedAdvancement.TaskType.SECRET).after(UNIDENTIFIED_FLOATING_OBJECT));

    public AeroAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public List<SimulatedAdvancement> getAdvancementsArray() {
        return AERO_ADVANCEMENTS;
    }

    @NotNull
    public String getName() {
        return "Create Aeronautics Advancements";
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (SimulatedAdvancement advancement : AERO_ADVANCEMENTS) {
            advancement.provideLang(consumer);
        }
    }

    private static SimulatedAdvancement create(String id, UnaryOperator<SimulatedAdvancement.Builder> b) {
        SimulatedAdvancement advancement = new SimulatedAdvancement(id, b, Aeronautics.path("textures/gui/advancement.png"), "aeronautics", AeroAdvancementTriggers::addSimple);
        AERO_ADVANCEMENTS.add(advancement);
        return advancement;
    }

    public static void init() {
    }
}
