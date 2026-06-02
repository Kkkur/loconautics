/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.recipe.trie;

import com.simibubi.create.foundation.recipe.trie.AbstractVariant;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public static final class AbstractVariant.AbstractFluid
implements AbstractVariant {
    @NotNull
    private final Fluid fluid;
    private final int hashCode;

    public AbstractVariant.AbstractFluid(@NotNull Fluid fluid) {
        this.fluid = fluid;
        this.hashCode = fluid.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof AbstractVariant.AbstractFluid)) {
            return false;
        }
        AbstractVariant.AbstractFluid that = (AbstractVariant.AbstractFluid)o;
        return this.fluid == that.fluid;
    }

    public int hashCode() {
        return this.hashCode;
    }
}
