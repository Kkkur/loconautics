/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.ShapedRecipePattern
 *  net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
 *  net.minecraft.world.level.Level
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.equipment.toolbox.ToolboxDyeingRecipe;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.recipe.ItemCopyingRecipe;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum AllRecipeTypes implements IRecipeTypeInfo,
StringRepresentable
{
    CONVERSION(ConversionRecipe::new),
    CRUSHING(CrushingRecipe::new),
    CUTTING(CuttingRecipe::new),
    MILLING(MillingRecipe::new),
    BASIN(BasinRecipe::new),
    MIXING(MixingRecipe::new),
    COMPACTING(CompactingRecipe::new),
    PRESSING(PressingRecipe::new),
    SANDPAPER_POLISHING(SandPaperPolishingRecipe::new),
    SPLASHING(SplashingRecipe::new),
    HAUNTING(HauntingRecipe::new),
    DEPLOYING(DeployerApplicationRecipe::new),
    FILLING(FillingRecipe::new),
    EMPTYING(EmptyingRecipe::new),
    ITEM_APPLICATION(ManualApplicationRecipe::new),
    MECHANICAL_CRAFTING(MechanicalCraftingRecipe.Serializer::new),
    SEQUENCED_ASSEMBLY(SequencedAssemblyRecipeSerializer::new),
    TOOLBOX_DYEING(() -> new SimpleCraftingRecipeSerializer(ToolboxDyeingRecipe::new), () -> RecipeType.CRAFTING, false),
    ITEM_COPYING(() -> new SimpleCraftingRecipeSerializer(ItemCopyingRecipe::new), () -> RecipeType.CRAFTING, false);

    public static final Predicate<RecipeHolder<?>> CAN_BE_AUTOMATED;
    public final ResourceLocation id;
    public final Supplier<RecipeSerializer<?>> serializerSupplier;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    @Nullable
    private final DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;
    private boolean isProcessingRecipe;
    public static final Codec<AllRecipeTypes> CODEC;

    private AllRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier, Supplier<RecipeType<?>> typeSupplier, boolean registerType) {
        String name = Lang.asId((String)this.name());
        this.id = Create.asResource(name);
        this.serializerSupplier = serializerSupplier;
        this.serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        if (registerType) {
            this.typeObject = Registers.TYPE_REGISTER.register(name, typeSupplier);
            this.type = this.typeObject;
        } else {
            this.typeObject = null;
            this.type = typeSupplier;
        }
        this.isProcessingRecipe = false;
    }

    private AllRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId((String)this.name());
        this.id = Create.asResource(name);
        this.serializerSupplier = serializerSupplier;
        this.serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        this.typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple((ResourceLocation)this.id));
        this.type = this.typeObject;
        this.isProcessingRecipe = false;
    }

    private AllRecipeTypes(StandardProcessingRecipe.Factory<?> processingFactory) {
        this(() -> new StandardProcessingRecipe.Serializer(processingFactory));
        this.isProcessingRecipe = true;
    }

    private AllRecipeTypes(ProcessingRecipe.Factory<ItemApplicationRecipeParams, ? extends ItemApplicationRecipe> itemApplicationFactory) {
        this(() -> new ItemApplicationRecipe.Serializer(itemApplicationFactory));
        this.isProcessingRecipe = true;
    }

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        ShapedRecipePattern.setCraftingSize((int)9, (int)9);
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T)((RecipeSerializer)this.serializerObject.get());
    }

    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return this.type.get();
    }

    public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I inv, Level world) {
        return world.getRecipeManager().getRecipeFor(this.getType(), inv, world);
    }

    public static boolean shouldIgnoreInAutomation(RecipeHolder<?> recipe) {
        RecipeSerializer serializer = recipe.value().getSerializer();
        if (serializer != null && AllTags.AllRecipeSerializerTags.AUTOMATION_IGNORE.matches(serializer)) {
            return true;
        }
        return !CAN_BE_AUTOMATED.test(recipe);
    }

    @NotNull
    public String getSerializedName() {
        return this.id.toString();
    }

    static {
        CAN_BE_AUTOMATED = r -> !r.id().getPath().endsWith("_manual_only");
        CODEC = StringRepresentable.fromEnum(AllRecipeTypes::values);
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.RECIPE_SERIALIZER, (String)"create");
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create((ResourceKey)Registries.RECIPE_TYPE, (String)"create");

        private Registers() {
        }
    }
}
