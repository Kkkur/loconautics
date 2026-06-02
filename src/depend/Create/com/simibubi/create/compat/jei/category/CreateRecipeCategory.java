/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.builder.IRecipeSlotBuilder
 *  mezz.jei.api.gui.drawable.IDrawable
 *  mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback
 *  mezz.jei.api.gui.ingredient.IRecipeSlotView
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.ingredients.IIngredientType
 *  mezz.jei.api.neoforge.NeoForgeTypes
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  mezz.jei.api.recipe.RecipeType
 *  mezz.jei.api.recipe.category.IRecipeCategory
 *  mezz.jei.api.registration.IRecipeCatalystRegistration
 *  mezz.jei.api.registration.IRecipeRegistration
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 *  net.minecraft.ChatFormatting
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CreateRecipeCategory<T extends Recipe<?>>
implements IRecipeCategory<RecipeHolder<T>> {
    private static final IDrawable BASIC_SLOT = CreateRecipeCategory.asDrawable(AllGuiTextures.JEI_SLOT);
    private static final IDrawable CHANCE_SLOT = CreateRecipeCategory.asDrawable(AllGuiTextures.JEI_CHANCE_SLOT);
    protected final RecipeType<RecipeHolder<T>> type;
    protected final Component title;
    protected final IDrawable background;
    protected final IDrawable icon;
    private final Supplier<List<RecipeHolder<T>>> recipes;
    private final List<Supplier<? extends ItemStack>> catalysts;

    public CreateRecipeCategory(Info<T> info) {
        this.type = info.recipeType();
        this.title = info.title();
        this.background = info.background();
        this.icon = info.icon();
        this.recipes = info.recipes();
        this.catalysts = info.catalysts();
    }

    @NotNull
    public RecipeType<RecipeHolder<T>> getRecipeType() {
        return this.type;
    }

    public Component getTitle() {
        return this.title;
    }

    public IDrawable getBackground() {
        return this.background;
    }

    public IDrawable getIcon() {
        return this.icon;
    }

    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<T> holder, IFocusGroup focuses) {
        this.setRecipe(builder, (T)holder.value(), focuses);
    }

    public void draw(RecipeHolder<T> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        this.draw((T)holder.value(), recipeSlotsView, gui, mouseX, mouseY);
    }

    public List<Component> getTooltipStrings(RecipeHolder<T> holder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        return this.getTooltipStrings((T)holder.value(), recipeSlotsView, mouseX, mouseY);
    }

    protected abstract void setRecipe(IRecipeLayoutBuilder var1, T var2, IFocusGroup var3);

    protected abstract void draw(T var1, IRecipeSlotsView var2, GuiGraphics var3, double var4, double var6);

    protected List<Component> getTooltipStrings(T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        return List.of();
    }

    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(this.type, this.recipes.get());
    }

    public void registerCatalysts(IRecipeCatalystRegistration registration) {
        this.catalysts.forEach(s -> registration.addRecipeCatalyst((ItemStack)s.get(), new RecipeType[]{this.type}));
    }

    public static IDrawable getRenderedSlot() {
        return BASIC_SLOT;
    }

    public static IDrawable getRenderedSlot(ProcessingOutput output) {
        return CreateRecipeCategory.getRenderedSlot(output.getChance());
    }

    public static IDrawable getRenderedSlot(float chance) {
        if (chance == 1.0f) {
            return BASIC_SLOT;
        }
        return CHANCE_SLOT;
    }

    public static ItemStack getResultItem(Recipe<?> recipe) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return ItemStack.EMPTY;
        }
        return recipe.getResultItem((HolderLookup.Provider)level.registryAccess());
    }

    public static IRecipeSlotRichTooltipCallback addStochasticTooltip(ProcessingOutput output) {
        return (view, tooltip) -> {
            float chance = output.getChance();
            if (chance != 1.0f) {
                tooltip.add((FormattedText)CreateLang.translateDirect("recipe.processing.chance", (double)chance < 0.01 ? "<1" : Integer.valueOf((int)(chance * 100.0f))).withStyle(ChatFormatting.GOLD));
            }
        };
    }

    public static IRecipeSlotBuilder addFluidSlot(IRecipeLayoutBuilder builder, int x, int y, SizedFluidIngredient ingredient) {
        int amount = ingredient.amount();
        return ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, x, y).setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1).addIngredients((IIngredientType)NeoForgeTypes.FLUID_STACK, Arrays.asList(ingredient.getFluids()))).setFluidRenderer((long)amount, false, 16, 16).addTooltipCallback(CreateRecipeCategory::addPotionTooltip);
    }

    public static IRecipeSlotBuilder addFluidSlot(IRecipeLayoutBuilder builder, int x, int y, FluidStack stack) {
        return ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1).addIngredient((IIngredientType)NeoForgeTypes.FLUID_STACK, (Object)stack)).setFluidRenderer((long)stack.getAmount(), false, 16, 16).addTooltipCallback(CreateRecipeCategory::addPotionTooltip);
    }

    private static void addPotionTooltip(IRecipeSlotView view, List<Component> tooltip) {
        Optional displayed = view.getDisplayedIngredient((IIngredientType)NeoForgeTypes.FLUID_STACK);
        if (displayed.isEmpty()) {
            return;
        }
        FluidStack fluidStack = (FluidStack)displayed.get();
        if (fluidStack.getFluid().isSame((Fluid)AllFluids.POTION.get())) {
            ArrayList potionTooltip = new ArrayList();
            PotionFluidHandler.addPotionTooltip(fluidStack, potionTooltip::add, 1.0f);
            tooltip.addAll(1, potionTooltip.stream().toList());
        }
    }

    protected static IDrawable asDrawable(final AllGuiTextures texture) {
        return new IDrawable(){

            public int getWidth() {
                return texture.getWidth();
            }

            public int getHeight() {
                return texture.getHeight();
            }

            public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
                texture.render(graphics, xOffset, yOffset);
            }
        };
    }

    public record Info<T extends Recipe<?>>(RecipeType<RecipeHolder<T>> recipeType, Component title, IDrawable background, IDrawable icon, Supplier<List<RecipeHolder<T>>> recipes, List<Supplier<? extends ItemStack>> catalysts) {
    }

    public static class Builder<T extends Recipe<? extends RecipeInput>> {
        private final Class<? extends T> recipeClass;
        private Supplier<Boolean> config = () -> true;
        private IDrawable background;
        private IDrawable icon;
        private final List<Consumer<List<RecipeHolder<T>>>> recipeListConsumers = new ArrayList<Consumer<List<RecipeHolder<T>>>>();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<Supplier<? extends ItemStack>>();

        public Builder(Class<? extends T> recipeClass) {
            this.recipeClass = recipeClass;
        }

        public Builder<T> enableWhen(Supplier<Boolean> predicate) {
            this.config = predicate;
            return this;
        }

        public Builder<T> enableWhen(ConfigBase.ConfigBool configValue) {
            this.config = () -> ((ConfigBase.ConfigBool)configValue).get();
            return this;
        }

        public Builder<T> addRecipeListConsumer(Consumer<List<RecipeHolder<T>>> consumer) {
            this.recipeListConsumers.add(consumer);
            return this;
        }

        public Builder<T> addRecipes(Supplier<Collection<? extends RecipeHolder<T>>> collection) {
            return this.addRecipeListConsumer(recipes -> recipes.addAll((Collection)collection.get()));
        }

        public Builder<T> addAllRecipesIf(Predicate<RecipeHolder<T>> pred) {
            return this.addRecipeListConsumer(recipes -> this.consumeAllRecipesOfType(recipe -> {
                if (pred.test((RecipeHolder)recipe)) {
                    recipes.add(recipe);
                }
            }));
        }

        public Builder<T> addAllRecipesIf(Predicate<RecipeHolder<?>> pred, Function<RecipeHolder<?>, RecipeHolder<T>> converter) {
            return this.addRecipeListConsumer(recipes -> CreateJEI.consumeAllRecipes(recipe -> {
                if (pred.test((RecipeHolder<?>)recipe)) {
                    recipes.add((RecipeHolder)converter.apply((RecipeHolder<?>)recipe));
                }
            }));
        }

        public Builder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
            return this.addTypedRecipes(recipeTypeEntry::getType);
        }

        public <I extends RecipeInput, R extends Recipe<I>> Builder<T> addTypedRecipes(Supplier<net.minecraft.world.item.crafting.RecipeType<R>> recipeType) {
            return this.addRecipeListConsumer(recipes -> CreateJEI.consumeTypedRecipes(recipe -> {
                if (this.recipeClass.isInstance(recipe.value())) {
                    recipes.add(recipe);
                }
            }, (net.minecraft.world.item.crafting.RecipeType)recipeType.get()));
        }

        public Builder<T> addTypedRecipes(Supplier<net.minecraft.world.item.crafting.RecipeType<T>> recipeType, Function<RecipeHolder<?>, RecipeHolder<T>> converter) {
            return this.addRecipeListConsumer(recipes -> CreateJEI.consumeTypedRecipes(recipe -> recipes.add((RecipeHolder)converter.apply((RecipeHolder<?>)recipe)), (net.minecraft.world.item.crafting.RecipeType)recipeType.get()));
        }

        public Builder<T> addTypedRecipesIf(Supplier<net.minecraft.world.item.crafting.RecipeType<? extends T>> recipeType, Predicate<RecipeHolder<?>> pred) {
            return this.addRecipeListConsumer(recipes -> this.consumeTypedRecipesTyped(recipe -> {
                if (pred.test((RecipeHolder<?>)recipe)) {
                    recipes.add(recipe);
                }
            }, (net.minecraft.world.item.crafting.RecipeType)recipeType.get()));
        }

        public Builder<T> addTypedRecipesExcluding(Supplier<net.minecraft.world.item.crafting.RecipeType<? extends T>> recipeType, Supplier<net.minecraft.world.item.crafting.RecipeType<? extends T>> excluded) {
            return this.addRecipeListConsumer(recipes -> {
                List<RecipeHolder<?>> excludedRecipes = CreateJEI.getTypedRecipes((net.minecraft.world.item.crafting.RecipeType)excluded.get());
                this.consumeTypedRecipesTyped(recipe -> {
                    for (RecipeHolder excludedRecipe : excludedRecipes) {
                        if (!CreateJEI.doInputsMatch(recipe.value(), excludedRecipe.value())) continue;
                        return;
                    }
                    recipes.add(recipe);
                }, (net.minecraft.world.item.crafting.RecipeType)recipeType.get());
            });
        }

        public Builder<T> removeRecipes(Supplier<net.minecraft.world.item.crafting.RecipeType<? extends T>> recipeType) {
            return this.addRecipeListConsumer(recipes -> {
                List<RecipeHolder<?>> excludedRecipes = CreateJEI.getTypedRecipes((net.minecraft.world.item.crafting.RecipeType)recipeType.get());
                recipes.removeIf(recipe -> {
                    for (RecipeHolder excludedRecipe : excludedRecipes) {
                        if (!CreateJEI.doInputsMatch(recipe.value(), excludedRecipe.value()) || !CreateJEI.doOutputsMatch(recipe.value(), excludedRecipe.value())) continue;
                        return true;
                    }
                    return false;
                });
            });
        }

        public Builder<T> removeNonAutomation() {
            return this.addRecipeListConsumer(recipes -> recipes.removeIf(AllRecipeTypes.CAN_BE_AUTOMATED.negate()));
        }

        public Builder<T> catalystStack(Supplier<ItemStack> supplier) {
            this.catalysts.add(supplier);
            return this;
        }

        public Builder<T> catalyst(Supplier<ItemLike> supplier) {
            return this.catalystStack(() -> new ItemStack((ItemLike)((ItemLike)supplier.get()).asItem()));
        }

        public Builder<T> icon(IDrawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder<T> itemIcon(ItemLike item) {
            this.icon(new ItemIcon(() -> new ItemStack(item)));
            return this;
        }

        public Builder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
            this.icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
            return this;
        }

        public Builder<T> background(IDrawable background) {
            this.background = background;
            return this;
        }

        public Builder<T> emptyBackground(int width, int height) {
            this.background(new EmptyBackground(width, height));
            return this;
        }

        public CreateRecipeCategory<T> build(String name, Factory<T> factory) {
            return this.build(Create.asResource(name), factory);
        }

        public CreateRecipeCategory<T> build(ResourceLocation id, Factory<T> factory) {
            Supplier<List<Object>> recipesSupplier = this.config.get() != false ? () -> {
                ArrayList recipes = new ArrayList();
                for (Consumer consumer : this.recipeListConsumers) {
                    consumer.accept(recipes);
                }
                return recipes;
            } : Collections::emptyList;
            Info info = new Info(RecipeType.createRecipeHolderType((ResourceLocation)id), (Component)Component.translatable((String)(id.getNamespace() + ".recipe." + id.getPath())), this.background, this.icon, recipesSupplier, this.catalysts);
            return factory.create(info);
        }

        private void consumeAllRecipesOfType(Consumer<RecipeHolder<T>> consumer) {
            CreateJEI.consumeAllRecipes(recipeHolder -> {
                if (this.recipeClass.isInstance(recipeHolder.value())) {
                    consumer.accept((RecipeHolder<T>)recipeHolder);
                }
            });
        }

        private void consumeTypedRecipesTyped(Consumer<RecipeHolder<T>> consumer, net.minecraft.world.item.crafting.RecipeType<?> type) {
            CreateJEI.consumeTypedRecipes(recipeHolder -> {
                if (this.recipeClass.isInstance(recipeHolder.value())) {
                    consumer.accept((RecipeHolder<T>)recipeHolder);
                }
            }, type);
        }
    }

    public static interface Factory<T extends Recipe<?>> {
        public CreateRecipeCategory<T> create(Info<T> var1);
    }
}
