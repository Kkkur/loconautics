/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 *  com.tterrag.registrate.util.entry.ItemProviderEntry
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.advancements.AdvancementHolder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$PathProvider
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.data.advancements;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.data.advancements.SimAdvancementTriggers;
import dev.simulated_team.simulated.data.advancements.SimulatedAdvancement;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimItems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class SimAdvancements
implements DataProvider {
    public static final List<SimulatedAdvancement> SIM_ADVANCEMENTS = new ArrayList<SimulatedAdvancement>();
    private final PackOutput output;
    public static final SimulatedAdvancement ROOT = SimAdvancements.create("root", b -> b.icon((ItemProviderEntry<?, ?>)SimItems.CONTRAPTION_DIAGRAM).title("Create Simulated").description("Physics Be Upon Ye").awardedForFree().special(SimulatedAdvancement.TaskType.SILENT));
    public static final SimulatedAdvancement APPLIED_KINEMATICS = SimAdvancements.create("applied_kinematics", b -> b.icon(SimBlocks.PHYSICS_ASSEMBLER.asStack()).title("Applied Kinematics").description("Obtain a Physics Assembler, the heart of every Simulated Contraption").special(SimulatedAdvancement.TaskType.NOISY).after(ROOT).whenIconCollected());
    public static final SimulatedAdvancement OPPOSITES_ATTRACT = SimAdvancements.create("opposite_attract", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.REDSTONE_MAGNET).title("Opposites Attract").description("Place and power a Redstone Magnet").after(APPLIED_KINEMATICS));
    public static final SimulatedAdvancement A_CALCULATED_CONNECTION = SimAdvancements.create("a_calculated_connection", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.DOCKING_CONNECTOR).title("A Calculated Connection").description("Successfully align and connect two Docking Connectors").after(OPPOSITES_ATTRACT).special(SimulatedAdvancement.TaskType.NOISY));
    public static final SimulatedAdvancement YOU_SPIN_ME_RIGHT_ROUND = SimAdvancements.create("you_spin_me_right_round", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.SWIVEL_BEARING).title("You Spin Me Right Round").description("Assemble a Simulated Contraption using a Swivel Bearing").after(APPLIED_KINEMATICS).special(SimulatedAdvancement.TaskType.NOISY));
    public static final SimulatedAdvancement LEARNING_THE_ROPES = SimAdvancements.create("learning_the_ropes", b -> b.icon((ItemProviderEntry<?, ?>)SimItems.ROPE_COUPLING).title("Learning the Ropes").after(APPLIED_KINEMATICS).description("Connect a Rope Connector or Rope Spool with Rope"));
    public static final SimulatedAdvancement STUCK_TOGETHER = SimAdvancements.create("stuck_together", b -> b.icon((ItemProviderEntry<?, ?>)SimItems.PLUNGER_LAUNCHER).title("Stuck Together").after(LEARNING_THE_ROPES).description("Craft a Plunger Launcher").whenIconCollected());
    public static final SimulatedAdvancement NOT_GONNA_SUGARCOAT_IT = SimAdvancements.create("not_gonna_sugarcoat_it", b -> b.icon((ItemProviderEntry<?, ?>)SimItems.HONEY_GLUE).title("Not Gonna Sugarcoat It").description("Use Honey Glue to connect a group of blocks for assembly").after(APPLIED_KINEMATICS));
    public static final SimulatedAdvancement I_DECLARE_THEE = SimAdvancements.create("i_declare_thee", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.NAMEPLATES.get(DyeColor.WHITE)).title("I Declare Thee...").description("Name a Simulated Contraption using a Nameplate").special(SimulatedAdvancement.TaskType.NOISY).after(NOT_GONNA_SUGARCOAT_IT));
    public static final SimulatedAdvancement MEASURE_ONCE_BUILD_TWICE = SimAdvancements.create("measure_once_build_twice", b -> b.icon((ItemProviderEntry<?, ?>)SimItems.CONTRAPTION_DIAGRAM).title("Measure Once, Build Twice").description("Inspect a Contraption Diagram").after(NOT_GONNA_SUGARCOAT_IT));
    public static final SimulatedAdvancement GET_A_GRIP = SimAdvancements.create("get_a_grip", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.IRON_HANDLE).title("Get a Grip!").description("Grab on to a Handle").after(APPLIED_KINEMATICS));
    public static final SimulatedAdvancement GOT_A_GRIP = SimAdvancements.create("got_a_grip", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.IRON_HANDLE).title("Got a Grip!").description("Break a very long fall by grabbing on to a Handle").special(SimulatedAdvancement.TaskType.SECRET).after(GET_A_GRIP));
    public static final SimulatedAdvancement UNPOWERED_STEERING = SimAdvancements.create("unpowered_steering", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.STEERING_WHEEL).title("Unpowered Steering").description("Grab and spin a Steering Wheel").after(GET_A_GRIP));
    public static final SimulatedAdvancement STEAMLESS_ENGINE = SimAdvancements.create("steamless_engine", b -> b.icon(SimBlocks.RED_PORTABLE_ENGINE).title("Steamless Engine").description("Place and power a Portable Engine").after(UNPOWERED_STEERING));
    public static final SimulatedAdvancement THAT_SHOULD_DO_FOR_NOW = SimAdvancements.create("that_should_do_for_now", b -> b.icon(SimBlocks.RED_PORTABLE_ENGINE).title("That Should Do For Now").description("Place over 10 hours of fuel into a Portable Engine").special(SimulatedAdvancement.TaskType.SECRET).after(STEAMLESS_ENGINE));
    public static final SimulatedAdvancement WHAT_GOES_DOWN = SimAdvancements.create("what_goes_down", b -> b.icon((ItemProviderEntry<?, ?>)SimItems.SPRING).title("What Goes Down...").description("Boing! Obtain a Spring item").whenIconCollected().after(GET_A_GRIP));
    public static final SimulatedAdvancement MUST_COME_UP = SimAdvancements.create("must_come_up", b -> b.icon((ItemProviderEntry<?, ?>)SimItems.SPRING).title("...Must Come Up").description("Watch a Spring item bounce a great distance").after(WHAT_GOES_DOWN).special(SimulatedAdvancement.TaskType.SECRET));
    public static final SimulatedAdvancement REWIND_TIME = SimAdvancements.create("rewind_time", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.TORSION_SPRING).title("Rewind Time").description("Watch a Torsion Spring unwind to its original position").after(WHAT_GOES_DOWN));
    public static final SimulatedAdvancement I_PAID_FOR_THE_WHOLE_TYPEWRITER = SimAdvancements.create("i_paid_for_the_whole_typewriter", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.LINKED_TYPEWRITER).title("I Paid for the Whole Typewriter").description("Bind 26 or more keys to frequencies on the Linked Typewriter").after(GET_A_GRIP).special(SimulatedAdvancement.TaskType.SECRET));
    public static final SimulatedAdvancement NO_PRESSURE = SimAdvancements.create("no_pressure", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.ALTITUDE_SENSOR).title("No Pressure").description("Obtain and place an Altitude Sensor").after(APPLIED_KINEMATICS).whenIconPlaced());
    public static final SimulatedAdvancement CAN_WE_GET_MUCH_HIGHER = SimAdvancements.create("can_we_get_much_higher", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.ALTITUDE_SENSOR).title("Can We Get Much Higher?").description("Observe an Altitude Sensor at 0% atmospheric pressure").after(NO_PRESSURE).special(SimulatedAdvancement.TaskType.SECRET));
    public static final SimulatedAdvancement CONVOLUTED_CIRCUMVOLUTIONS = SimAdvancements.create("convoluted_circumvolutions", b -> b.icon((ItemProviderEntry<?, ?>)SimItems.GYRO_MECHANISM).title("Convoluted Circumvolutions").description("Obtain a Gyroscopic Mechanism").after(NO_PRESSURE).special(SimulatedAdvancement.TaskType.NOISY).whenIconCollected());
    public static final SimulatedAdvancement THE_DEFINITION_OF_UP = SimAdvancements.create("the_definition_of_up", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.GIMBAL_SENSOR).title("The Definition of \"Up\"").description("Obtain and place a Gimbal Sensor to help you keep balance").after(CONVOLUTED_CIRCUMVOLUTIONS).whenIconPlaced());
    public static final SimulatedAdvancement THATAWAY = SimAdvancements.create("thataway", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.NAVIGATION_TABLE).title("Thataway!").description("Obtain and place a Navigation Table to point you in the right direction").whenIconPlaced().special(SimulatedAdvancement.TaskType.NOISY).after(NO_PRESSURE));
    public static final SimulatedAdvancement FAR_FROM_HOME = SimAdvancements.create("far_from_home", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.NAVIGATION_TABLE).title("Far From Home").description("Set a Navigation Table's target to a location over 5000 blocks away").special(SimulatedAdvancement.TaskType.SECRET).after(THATAWAY));
    public static final SimulatedAdvancement SPEED_IS_KEY = SimAdvancements.create("speed_is_key", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.VELOCITY_SENSOR).title("Speed is Key").description("Obtain and place a Velocity Sensor to satiate your need for speed").after(NO_PRESSURE).whenIconPlaced());
    public static final SimulatedAdvancement BIG_BEAM = SimAdvancements.create("big_beam", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.LASER_POINTER).title("Big Beam").description("Power a Laser Pointer. Please do not stare directly into the Laser Pointer").after(NO_PRESSURE));
    public static final SimulatedAdvancement NEARSIGHTED = SimAdvancements.create("nearsighted", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.OPTICAL_SENSOR).title("Nearsighted").description("Obtain and place an Optical Sensor to show you what's right there").after(BIG_BEAM).whenIconPlaced());
    public static final SimulatedAdvancement MY_EYE = SimAdvancements.create("my_eye", b -> b.icon((ItemProviderEntry<?, ?>)SimBlocks.LASER_SENSOR).title("My Eye!").description("Shine a laser into a Laser Sensor and activate it").after(BIG_BEAM));
    public static final SimulatedAdvancement CALL_OF_THE_VOID = SimAdvancements.create("call_of_the_void", b -> b.icon((ItemLike)Blocks.END_PORTAL_FRAME).title("Call of the Void").description("Visit a glimmering sea at the end of the world").special(SimulatedAdvancement.TaskType.SECRET).after(APPLIED_KINEMATICS));
    private final CompletableFuture<HolderLookup.Provider> registries;

    public SimAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.output = output;
        this.registries = registries;
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (SimulatedAdvancement advancement : SIM_ADVANCEMENTS) {
            advancement.provideLang(consumer);
        }
    }

    private static SimulatedAdvancement create(String id, UnaryOperator<SimulatedAdvancement.Builder> b) {
        SimulatedAdvancement advancement = new SimulatedAdvancement(id, b, Simulated.path("textures/gui/advancement.png"), "simulated", SimAdvancementTriggers::addSimple);
        SIM_ADVANCEMENTS.add(advancement);
        return advancement;
    }

    public static void register() {
    }

    public List<SimulatedAdvancement> getAdvancementsArray() {
        return SIM_ADVANCEMENTS;
    }

    @NotNull
    public CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return this.registries.thenCompose(provider -> {
            PackOutput.PathProvider pathProvider = this.output.createPathProvider(PackOutput.Target.DATA_PACK, "advancement");
            ArrayList futures = new ArrayList();
            HashSet set = Sets.newHashSet();
            Consumer<AdvancementHolder> consumer = advancement -> {
                ResourceLocation id = advancement.id();
                if (!set.add(id)) {
                    throw new IllegalStateException("Duplicate advancement " + String.valueOf(id));
                }
                Path path = pathProvider.json(id);
                futures.add(DataProvider.saveStable((CachedOutput)cachedOutput, (HolderLookup.Provider)provider, (Codec)Advancement.CODEC, (Object)advancement.value(), (Path)path));
            };
            for (SimulatedAdvancement advancement2 : this.getAdvancementsArray()) {
                advancement2.save(consumer);
            }
            return CompletableFuture.allOf((CompletableFuture[])futures.toArray(CompletableFuture[]::new));
        });
    }

    @NotNull
    public String getName() {
        return "Create Simulated's Advancements";
    }
}
