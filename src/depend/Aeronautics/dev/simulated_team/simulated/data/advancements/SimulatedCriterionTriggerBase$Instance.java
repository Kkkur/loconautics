/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.CriterionTriggerInstance
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.data.advancements;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public static abstract class SimulatedCriterionTriggerBase.Instance
implements CriterionTriggerInstance {
    private final ResourceLocation id;

    public SimulatedCriterionTriggerBase.Instance(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    protected abstract boolean test(@Nullable List<Supplier<Object>> var1);
}
