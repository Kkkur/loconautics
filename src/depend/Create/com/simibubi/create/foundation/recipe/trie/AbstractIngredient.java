/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.simibubi.create.foundation.recipe.trie;

import com.google.common.collect.ImmutableSet;
import com.simibubi.create.foundation.recipe.trie.AbstractVariant;
import java.util.Set;

public class AbstractIngredient {
    final Set<AbstractVariant> variants;
    final int hashCode;

    public AbstractIngredient(Set<AbstractVariant> variants) {
        this.variants = ImmutableSet.copyOf(variants);
        this.hashCode = variants.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractIngredient)) {
            return false;
        }
        AbstractIngredient that = (AbstractIngredient)obj;
        if (this == that) {
            return true;
        }
        return this.hashCode == that.hashCode && this.variants.equals(that.variants);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public static class Universal
    extends AbstractIngredient {
        public static final Universal INSTANCE = new Universal();
        private static final int hashCode = Universal.class.hashCode();

        private Universal() {
            super(Set.of());
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Universal;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
