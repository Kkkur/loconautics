/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.simibubi.create.foundation.recipe.IRecipeTypeInfo
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.index.neoforge;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.data.neoforge.PortableEngineDyeingRecipe;
import java.util.function.Supplier;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SimNeoForgeRecipeTypes implements IRecipeTypeInfo,
StringRepresentable
{
    PORTABLE_ENGINE_DYEING(() -> new SimpleCraftingRecipeSerializer(PortableEngineDyeingRecipe::new), () -> RecipeType.CRAFTING, false);

    public static final Codec<SimNeoForgeRecipeTypes> CODEC;
    public final ResourceLocation id;
    public final Supplier<RecipeSerializer<?>> serializerSupplier;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    @Nullable
    private final DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    private SimNeoForgeRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
        String name = Lang.asId((String)this.name());
        this.id = Simulated.path(name);
        this.serializerSupplier = serializerSupplier;
        this.serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        if (registerType) {
            this.typeObject = Registers.TYPE_REGISTER.register(name, typeSupplier);
            this.type = this.typeObject;
        } else {
            this.typeObject = null;
            this.type = typeSupplier;
        }
    }

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T)((RecipeSerializer)this.serializerObject.get());
    }

    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return this.type.get();
    }

    @NotNull
    public String getSerializedName() {
        return this.id.toString();
    }

    static {
        CODEC = StringRepresentable.fromEnum(SimNeoForgeRecipeTypes::values);
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.RECIPE_SERIALIZER, (String)"simulated");
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create((ResourceKey)Registries.RECIPE_TYPE, (String)"simulated");

        private Registers() {
        }
    }
}
