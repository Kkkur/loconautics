/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.critereon.SimpleCriterionTrigger$SimpleInstance
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.advancement;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import org.jetbrains.annotations.Nullable;

public static abstract class CriterionTriggerBase.Instance
implements SimpleCriterionTrigger.SimpleInstance {
    protected abstract boolean test(@Nullable List<Supplier<Object>> var1);
}
