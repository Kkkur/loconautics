/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.neoforged.neoforge.data.event.GatherDataEvent
 *  net.neoforged.neoforge.registries.RegisterEvent
 */
package dev.ryanhcode.offroad.neoforge.data;

import dev.ryanhcode.offroad.data.OffroadAdvancementTriggers;
import dev.ryanhcode.offroad.index.OffroadAdvancements;
import dev.ryanhcode.offroad.index.OffroadSoundEvents;
import dev.ryanhcode.offroad.index.OffroadTags;
import dev.ryanhcode.offroad.neoforge.index.OffroadSoundEventsNeoForge;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class OffroadDatagen {
    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (event.getMods().contains("offroad")) {
            OffroadTags.addGenerators();
        }
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), (DataProvider)new OffroadAdvancements(output, lookupProvider));
        event.addProvider((DataProvider)OffroadSoundEvents.REGISTRY.getProvider(output));
    }

    public static void registerEvent(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, OffroadSoundEventsNeoForge::register);
        if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            OffroadAdvancements.init();
            OffroadAdvancementTriggers.register();
        }
    }
}
