/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.constants.VanillaTypes
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.ingredients.IIngredientType
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  mezz.jei.api.runtime.IIngredientManager
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ItemStackLinkedSet
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedItemDrain;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

@ParametersAreNonnullByDefault
public class ItemDrainCategory
extends CreateRecipeCategory<EmptyingRecipe> {
    private final AnimatedItemDrain drain = new AnimatedItemDrain();

    public ItemDrainCategory(CreateRecipeCategory.Info<EmptyingRecipe> info) {
        super(info);
    }

    public static void consumeRecipes(Consumer<RecipeHolder<EmptyingRecipe>> consumer, IIngredientManager ingredientManager) {
        ObjectOpenCustomHashSet emptiedItems = new ObjectOpenCustomHashSet(ItemStackLinkedSet.TYPE_AND_TAG);
        for (ItemStack stack : ingredientManager.getAllIngredients((IIngredientType)VanillaTypes.ITEM_STACK)) {
            if (PotionFluidHandler.isPotionItem(stack)) {
                FluidStack fluidFromPotionItem = PotionFluidHandler.getFluidFromPotionItem(stack);
                Ingredient potion = Ingredient.of((ItemStack[])new ItemStack[]{stack});
                ResourceLocation id = Create.asResource("potions");
                EmptyingRecipe recipe = (EmptyingRecipe)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)new StandardProcessingRecipe.Builder<EmptyingRecipe>(EmptyingRecipe::new, id).withItemIngredients(potion)).withFluidOutputs(fluidFromPotionItem)).withSingleItemOutput(new ItemStack((ItemLike)Items.GLASS_BOTTLE))).build();
                consumer.accept((RecipeHolder<EmptyingRecipe>)new RecipeHolder(id, (Recipe)recipe));
                continue;
            }
            IFluidHandlerItem capability = (IFluidHandlerItem)stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (capability == null) continue;
            ItemStack copy = stack.copy();
            capability = (IFluidHandlerItem)copy.getCapability(Capabilities.FluidHandler.ITEM);
            FluidStack extracted = capability.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            ItemStack result = capability.getContainer();
            if (extracted.isEmpty() || result.isEmpty()) continue;
            result = ItemHelper.sameItem(stack, result) ? stack : (ItemStack)emptiedItems.addOrGet((Object)result);
            Ingredient ingredient = Ingredient.of((ItemStack[])new ItemStack[]{stack});
            ResourceLocation itemName = RegisteredObjectsHelper.getKeyOrThrow((Item)stack.getItem());
            ResourceLocation fluidName = RegisteredObjectsHelper.getKeyOrThrow((Fluid)extracted.getFluid());
            ResourceLocation id = Create.asResource("empty_" + itemName.getNamespace() + "_" + itemName.getPath() + "_of_" + fluidName.getNamespace() + "_" + fluidName.getPath());
            EmptyingRecipe recipe = (EmptyingRecipe)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)new StandardProcessingRecipe.Builder<EmptyingRecipe>(EmptyingRecipe::new, id).withItemIngredients(ingredient)).withFluidOutputs(extracted)).withSingleItemOutput(result)).build();
            consumer.accept((RecipeHolder<EmptyingRecipe>)new RecipeHolder(id, (Recipe)recipe));
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EmptyingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 8).setBackground(ItemDrainCategory.getRenderedSlot(), -1, -1).addIngredients((Ingredient)recipe.getIngredients().get(0));
        ItemDrainCategory.addFluidSlot(builder, 132, 8, recipe.getResultingFluid());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 27).setBackground(ItemDrainCategory.getRenderedSlot(), -1, -1).addItemStack(ItemDrainCategory.getResultItem(recipe));
    }

    @Override
    public void draw(EmptyingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 62, 37);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 73, 4);
        this.drain.withFluid(recipe.getResultingFluid()).draw(graphics, this.getBackground().getWidth() / 2 - 13, 40);
    }
}
