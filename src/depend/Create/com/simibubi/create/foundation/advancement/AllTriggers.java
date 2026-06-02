/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.foundation.advancement;

import com.simibubi.create.foundation.advancement.CriterionTriggerBase;
import com.simibubi.create.foundation.advancement.SimpleCreateTrigger;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class AllTriggers {
    private static final List<CriterionTriggerBase<?>> triggers = new LinkedList();

    public static SimpleCreateTrigger addSimple(String id) {
        return AllTriggers.add(new SimpleCreateTrigger(id));
    }

    private static <T extends CriterionTriggerBase<?>> T add(T instance) {
        triggers.add(instance);
        return instance;
    }

    public static void register() {
        triggers.forEach(trigger -> Registry.register((Registry)BuiltInRegistries.TRIGGER_TYPES, (ResourceLocation)trigger.getId(), (Object)trigger));
    }
}
