/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.recipe.trie;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public sealed interface AbstractVariant {

    public static final class AbstractFluid
    implements AbstractVariant {
        @NotNull
        private final Fluid fluid;
        private final int hashCode;

        public AbstractFluid(@NotNull Fluid fluid) {
            this.fluid = fluid;
            this.hashCode = fluid.hashCode();
        }

        public boolean equals(Object o) {
            if (!(o instanceof AbstractFluid)) {
                return false;
            }
            AbstractFluid that = (AbstractFluid)o;
            return this.fluid == that.fluid;
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    public static final class AbstractItem
    implements AbstractVariant {
        @NotNull
        private final Item item;
        private final int hashCode;

        public AbstractItem(@NotNull Item item) {
            this.item = item;
            this.hashCode = item.hashCode();
        }

        public boolean equals(Object o) {
            if (!(o instanceof AbstractItem)) {
                return false;
            }
            AbstractItem that = (AbstractItem)o;
            return this.item == that.item;
        }

        public int hashCode() {
            return this.hashCode;
        }
    }
}
