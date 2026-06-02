/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.advancements.critereon.ContextAwarePredicate
 *  net.minecraft.advancements.critereon.EntityPredicate
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.advancement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.advancement.CriterionTriggerBase;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import org.jetbrains.annotations.Nullable;

public static class SimpleCreateTrigger.Instance
extends CriterionTriggerBase.Instance {
    private static final Codec<SimpleCreateTrigger.Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(SimpleCreateTrigger.Instance::player)).apply((Applicative)instance, SimpleCreateTrigger.Instance::new));
    private final Optional<ContextAwarePredicate> player;

    public SimpleCreateTrigger.Instance() {
        this.player = Optional.empty();
    }

    public SimpleCreateTrigger.Instance(Optional<ContextAwarePredicate> player) {
        this.player = player;
    }

    @Override
    protected boolean test(@Nullable List<Supplier<Object>> suppliers) {
        return true;
    }

    public Optional<ContextAwarePredicate> player() {
        return this.player;
    }
}
