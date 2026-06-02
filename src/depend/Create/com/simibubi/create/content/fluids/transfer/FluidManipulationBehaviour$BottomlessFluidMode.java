/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  net.minecraft.world.level.material.Fluid
 */
package com.simibubi.create.content.fluids.transfer;

import com.google.common.base.Predicates;
import com.simibubi.create.AllTags;
import java.util.function.Predicate;
import net.minecraft.world.level.material.Fluid;

public static enum FluidManipulationBehaviour.BottomlessFluidMode implements Predicate<Fluid>
{
    ALLOW_ALL((Predicate<Fluid>)Predicates.alwaysTrue()),
    DENY_ALL((Predicate<Fluid>)Predicates.alwaysFalse()),
    ALLOW_BY_TAG(AllTags.AllFluidTags.BOTTOMLESS_ALLOW::matches),
    DENY_BY_TAG((Predicate<Fluid>)Predicates.not(AllTags.AllFluidTags.BOTTOMLESS_DENY::matches));

    private final Predicate<Fluid> predicate;

    private FluidManipulationBehaviour.BottomlessFluidMode(Predicate<Fluid> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(Fluid fluid) {
        return this.predicate.test(fluid);
    }
}
