/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.Holder
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.TagKey
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 *  net.neoforged.neoforge.fluids.crafting.TagFluidIngredient
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 */
package com.simibubi.create.foundation.fluid;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;
import org.jetbrains.annotations.ApiStatus;

@Deprecated(since="6.0.7", forRemoval=true)
@ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
public class FluidIngredientOld {
    private static final Codec<SizedFluidIngredient> FLUID_STACK = RecordCodecBuilder.create(i -> i.group(FluidIngredientOld.validatedType("fluid_stack"), (App)FluidStack.FLUID_NON_EMPTY_CODEC.fieldOf("fluid").forGetter(s -> null), (App)DataComponentPatch.CODEC.optionalFieldOf("components", (Object)DataComponentPatch.EMPTY).forGetter(s -> null), (App)Codec.INT.fieldOf("amount").forGetter(s -> null)).apply((Applicative)i, (type, fluid, components, amount) -> new SizedFluidIngredient(DataComponentFluidIngredient.of((boolean)false, (DataComponentMap)components.split().added(), (Holder[])new Holder[]{fluid}), amount.intValue())));
    private static final Codec<SizedFluidIngredient> FLUID_TAG = RecordCodecBuilder.create(i -> i.group(FluidIngredientOld.validatedType("fluid_tag"), (App)TagKey.codec((ResourceKey)Registries.FLUID).fieldOf("fluid_tag").forGetter(s -> null), (App)Codec.INT.fieldOf("amount").forGetter(s -> null)).apply((Applicative)i, (type, tag, amount) -> new SizedFluidIngredient(TagFluidIngredient.tag((TagKey)tag), amount.intValue())));
    public static final Codec<SizedFluidIngredient> CODEC = Codec.withAlternative(FLUID_STACK, FLUID_TAG);

    private static <T> RecordCodecBuilder<T, String> validatedType(String requiredType) {
        return Codec.STRING.validate(s -> s.equals(requiredType) ? DataResult.success((Object)s) : DataResult.error(() -> "Invalid Type: " + s)).fieldOf("type").forGetter(s -> requiredType);
    }
}
