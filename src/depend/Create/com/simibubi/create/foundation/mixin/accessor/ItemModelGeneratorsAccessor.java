/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.models.ItemModelGenerators
 *  net.minecraft.data.models.ItemModelGenerators$TrimModelData
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.simibubi.create.foundation.mixin.accessor;

import java.util.List;
import net.minecraft.data.models.ItemModelGenerators;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ItemModelGenerators.class})
public interface ItemModelGeneratorsAccessor {
    @Accessor(value="GENERATED_TRIM_MODELS")
    public static List<ItemModelGenerators.TrimModelData> create$getGENERATED_TRIM_MODELS() {
        throw new AssertionError();
    }
}
