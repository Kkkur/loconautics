/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.data.advancements.SimpleSimulatedTrigger
 *  dev.simulated_team.simulated.data.advancements.SimulatedCriterionTriggerBase
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 */
package dev.ryanhcode.offroad.data;

import dev.simulated_team.simulated.data.advancements.SimpleSimulatedTrigger;
import dev.simulated_team.simulated.data.advancements.SimulatedCriterionTriggerBase;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class OffroadAdvancementTriggers {
    private static final List<SimulatedCriterionTriggerBase<?>> TRIGGERS = new LinkedList();

    public static SimpleSimulatedTrigger addSimple(String modid, String id) {
        return OffroadAdvancementTriggers.add(new SimpleSimulatedTrigger(ResourceLocation.fromNamespaceAndPath((String)modid, (String)id)));
    }

    private static <T extends SimulatedCriterionTriggerBase<?>> T add(T instance) {
        TRIGGERS.add(instance);
        return instance;
    }

    public static void register() {
        TRIGGERS.forEach(trigger -> Registry.register((Registry)BuiltInRegistries.TRIGGER_TYPES, (ResourceLocation)trigger.getId(), (Object)trigger));
    }
}
