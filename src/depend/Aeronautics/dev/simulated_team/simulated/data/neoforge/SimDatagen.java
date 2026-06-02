/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.neoforged.neoforge.data.event.GatherDataEvent
 */
package dev.simulated_team.simulated.data.neoforge;

import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.data.neoforge.SimProcessingRecipeGen;
import dev.simulated_team.simulated.index.SimTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class SimDatagen {
    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (event.getMods().contains("simulated")) {
            SimTags.addGenerators();
        }
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), (DataProvider)new SimAdvancements(output, lookupProvider));
        generator.addProvider(event.includeServer(), SimProcessingRecipeGen.registerAll(output, lookupProvider));
    }
}
