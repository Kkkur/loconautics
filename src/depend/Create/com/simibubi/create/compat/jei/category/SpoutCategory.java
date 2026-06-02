/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.constants.VanillaTypes
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.ingredients.IIngredientType
 *  mezz.jei.api.neoforge.NeoForgeTypes
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  mezz.jei.api.runtime.IIngredientManager
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
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
 *  net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedSpout;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

@ParametersAreNonnullByDefault
public class SpoutCategory
extends CreateRecipeCategory<FillingRecipe> {
    private final AnimatedSpout spout = new AnimatedSpout();

    public SpoutCategory(CreateRecipeCategory.Info<FillingRecipe> info) {
        super(info);
    }

    public static void consumeRecipes(Consumer<RecipeHolder<FillingRecipe>> consumer, IIngredientManager ingredientManager) {
        Collection fluidStacks = ingredientManager.getAllIngredients((IIngredientType)NeoForgeTypes.FLUID_STACK);
        for (ItemStack stack : ingredientManager.getAllIngredients((IIngredientType)VanillaTypes.ITEM_STACK)) {
            if (PotionFluidHandler.isPotionItem(stack)) {
                FluidStack fluidFromPotionItem = PotionFluidHandler.getFluidFromPotionItem(stack);
                Ingredient bottle = Ingredient.of((ItemLike[])new ItemLike[]{Items.GLASS_BOTTLE});
                ResourceLocation id = Create.asResource("potions");
                SizedFluidIngredient fluidIngredient = new SizedFluidIngredient(DataComponentFluidIngredient.of((boolean)false, (FluidStack)fluidFromPotionItem), fluidFromPotionItem.getAmount());
                FillingRecipe recipe = (FillingRecipe)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)new StandardProcessingRecipe.Builder<FillingRecipe>(FillingRecipe::new, id).withItemIngredients(bottle)).withFluidIngredients(fluidIngredient)).withSingleItemOutput(stack)).build();
                consumer.accept((RecipeHolder<FillingRecipe>)new RecipeHolder(id, (Recipe)recipe));
                continue;
            }
            IFluidHandlerItem capability = (IFluidHandlerItem)stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (capability == null) continue;
            int numTanks = capability.getTanks();
            FluidStack existingFluid = numTanks == 1 ? capability.getFluidInTank(0) : FluidStack.EMPTY;
            for (FluidStack fluidStack : fluidStacks) {
                ItemStack copy;
                IFluidHandlerItem fhi;
                if (numTanks == 1 && !existingFluid.isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)existingFluid, (FluidStack)fluidStack) || (fhi = (IFluidHandlerItem)(copy = stack.copy()).getCapability(Capabilities.FluidHandler.ITEM)) == null || !GenericItemFilling.isFluidHandlerValid(copy, fhi)) continue;
                FluidStack fluidCopy = fluidStack.copy();
                fluidCopy.setAmount(1000);
                fhi.fill(fluidCopy, IFluidHandler.FluidAction.EXECUTE);
                ItemStack container = fhi.getContainer();
                if (ItemHelper.sameItem(container, copy) || container.isEmpty()) continue;
                Ingredient bucket = Ingredient.of((ItemStack[])new ItemStack[]{stack});
                ResourceLocation itemName = RegisteredObjectsHelper.getKeyOrThrow((Item)stack.getItem());
                ResourceLocation fluidName = RegisteredObjectsHelper.getKeyOrThrow((Fluid)fluidCopy.getFluid());
                ResourceLocation id = Create.asResource("fill_" + itemName.getNamespace() + "_" + itemName.getPath() + "_with_" + fluidName.getNamespace() + "_" + fluidName.getPath());
                SizedFluidIngredient fluidIngredient = new SizedFluidIngredient(DataComponentFluidIngredient.of((boolean)false, (FluidStack)fluidCopy), fluidCopy.getAmount());
                FillingRecipe recipe = (FillingRecipe)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)new StandardProcessingRecipe.Builder<FillingRecipe>(FillingRecipe::new, id).withItemIngredients(bucket)).withFluidIngredients(fluidIngredient)).withSingleItemOutput(container)).build();
                consumer.accept((RecipeHolder<FillingRecipe>)new RecipeHolder(id, (Recipe)recipe));
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FillingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51).setBackground(SpoutCategory.getRenderedSlot(), -1, -1).addIngredients((Ingredient)recipe.getIngredients().get(0));
        SpoutCategory.addFluidSlot(builder, 27, 32, recipe.getRequiredFluid());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 51).setBackground(SpoutCategory.getRenderedSlot(), -1, -1).addItemStack(SpoutCategory.getResultItem(recipe));
    }

    @Override
    public void draw(FillingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 62, 57);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 126, 29);
        this.spout.withFluids(Arrays.asList(recipe.getRequiredFluid().getFluids())).draw(graphics, this.getBackground().getWidth() / 2 - 13, 22);
    }
}
