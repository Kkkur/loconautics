/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.recipe.trie;

import com.simibubi.create.foundation.recipe.trie.AbstractIngredient;
import java.util.Set;

public static class AbstractIngredient.Universal
extends AbstractIngredient {
    public static final AbstractIngredient.Universal INSTANCE = new AbstractIngredient.Universal();
    private static final int hashCode = AbstractIngredient.Universal.class.hashCode();

    private AbstractIngredient.Universal() {
        super(Set.of());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractIngredient.Universal;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
