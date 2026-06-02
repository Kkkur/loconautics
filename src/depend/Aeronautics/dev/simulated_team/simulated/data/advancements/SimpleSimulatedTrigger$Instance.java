/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.critereon.CriterionValidator
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.data.advancements;

import dev.simulated_team.simulated.data.advancements.SimulatedCriterionTriggerBase;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public static class SimpleSimulatedTrigger.Instance
extends SimulatedCriterionTriggerBase.Instance {
    public SimpleSimulatedTrigger.Instance(ResourceLocation id) {
        super(id);
    }

    @Override
    protected boolean test(@Nullable List<Supplier<Object>> suppliers) {
        return true;
    }

    public void validate(@NotNull CriterionValidator criterionValidator) {
    }
}
