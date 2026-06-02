/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.data.advancements.SimAdvancements
 *  dev.simulated_team.simulated.data.advancements.SimulatedAdvancement
 *  dev.simulated_team.simulated.data.advancements.SimulatedAdvancement$Builder
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  org.jetbrains.annotations.NotNull
 */
package dev.ryanhcode.offroad.index;

import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.data.OffroadAdvancementTriggers;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.data.advancements.SimulatedAdvancement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

public class OffroadAdvancements
extends SimAdvancements {
    public static final List<SimulatedAdvancement> OFFROAD_ADVANCEMENTS = new ArrayList<SimulatedAdvancement>();

    public OffroadAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public List<SimulatedAdvancement> getAdvancementsArray() {
        return OFFROAD_ADVANCEMENTS;
    }

    @NotNull
    public String getName() {
        return "Create Offroad Advancements";
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (SimulatedAdvancement advancement : OFFROAD_ADVANCEMENTS) {
            advancement.provideLang(consumer);
        }
    }

    private static SimulatedAdvancement create(String id, UnaryOperator<SimulatedAdvancement.Builder> b) {
        SimulatedAdvancement advancement = new SimulatedAdvancement(id, b, Offroad.path("textures/gui/advancement.png"), "offroad", OffroadAdvancementTriggers::addSimple);
        OFFROAD_ADVANCEMENTS.add(advancement);
        return advancement;
    }

    public static void init() {
    }
}
