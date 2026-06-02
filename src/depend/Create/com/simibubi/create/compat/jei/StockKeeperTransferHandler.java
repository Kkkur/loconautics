/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.helpers.IJeiHelpers
 *  mezz.jei.api.helpers.IStackHelper
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  mezz.jei.api.recipe.transfer.IRecipeTransferError
 *  mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler
 *  mezz.jei.common.transfer.RecipeTransferErrorInternal
 *  mezz.jei.common.transfer.RecipeTransferOperationsResult
 *  mezz.jei.common.transfer.RecipeTransferUtil
 *  mezz.jei.library.transfer.RecipeTransferErrorMissingSlots
 *  mezz.jei.library.transfer.RecipeTransferErrorTooltip
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.compat.jei;

import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.stockTicker.CraftableBigItemStack;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.blockEntity.ItemHandlerContainer;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import mezz.jei.common.transfer.RecipeTransferErrorInternal;
import mezz.jei.common.transfer.RecipeTransferOperationsResult;
import mezz.jei.common.transfer.RecipeTransferUtil;
import mezz.jei.library.transfer.RecipeTransferErrorMissingSlots;
import mezz.jei.library.transfer.RecipeTransferErrorTooltip;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StockKeeperTransferHandler
implements IUniversalRecipeTransferHandler<StockKeeperRequestMenu> {
    private IJeiHelpers helpers;

    public StockKeeperTransferHandler(IJeiHelpers helpers) {
        this.helpers = helpers;
    }

    public Class<? extends StockKeeperRequestMenu> getContainerClass() {
        return StockKeeperRequestMenu.class;
    }

    public Optional<MenuType<StockKeeperRequestMenu>> getMenuType() {
        return Optional.of((MenuType)AllMenuTypes.STOCK_KEEPER_REQUEST.get());
    }

    @Nullable
    public IRecipeTransferError transferRecipe(StockKeeperRequestMenu container, Object object, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        Level level = player.level();
        if (!(object instanceof RecipeHolder)) {
            return null;
        }
        RecipeHolder recipe = (RecipeHolder)object;
        MutableObject result = new MutableObject();
        if (level.isClientSide()) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> result.setValue((Object)this.transferRecipeOnClient(container, recipe, recipeSlots, player, maxTransfer, doTransfer)));
        }
        return (IRecipeTransferError)result.getValue();
    }

    @Nullable
    private IRecipeTransferError transferRecipeOnClient(StockKeeperRequestMenu container, RecipeHolder<Recipe<?>> recipeHolder, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        Object object = container.screenReference;
        if (!(object instanceof StockKeeperRequestScreen)) {
            return RecipeTransferErrorInternal.INSTANCE;
        }
        StockKeeperRequestScreen screen = (StockKeeperRequestScreen)((Object)object);
        Recipe recipe = recipeHolder.value();
        if (recipe.getIngredients().size() > 9) {
            return RecipeTransferErrorInternal.INSTANCE;
        }
        for (CraftableBigItemStack cbis : screen.recipesToOrder) {
            if (cbis.recipe != recipe) continue;
            return new RecipeTransferErrorTooltip((Component)CreateLang.translate("gui.stock_keeper.already_ordering_recipe", new Object[0]).component());
        }
        if (screen.itemsToOrder.size() >= 9) {
            return new RecipeTransferErrorTooltip((Component)CreateLang.translate("gui.stock_keeper.slots_full", new Object[0]).component());
        }
        InventorySummary summary = ((StockTickerBlockEntity)((StockKeeperRequestMenu)screen.getMenu()).contentHolder).getLastClientsideStockSnapshotAsSummary();
        if (summary == null) {
            return RecipeTransferErrorInternal.INSTANCE;
        }
        ItemHandlerContainer outputDummy = new ItemHandlerContainer((IItemHandlerModifiable)new ItemStackHandler(9));
        ArrayList<Slot> craftingSlots = new ArrayList<Slot>();
        for (int i = 0; i < outputDummy.getContainerSize(); ++i) {
            craftingSlots.add(new Slot((Container)outputDummy, i, 0, 0));
        }
        List<BigItemStack> stacksByCount = summary.getStacksByCount();
        ItemHandlerContainer inputDummy = new ItemHandlerContainer((IItemHandlerModifiable)new ItemStackHandler(stacksByCount.size()));
        HashMap<Slot, ItemStack> availableItemStacks = new HashMap<Slot, ItemStack>();
        for (int j = 0; j < stacksByCount.size(); ++j) {
            BigItemStack bigItemStack = stacksByCount.get(j);
            availableItemStacks.put(new Slot((Container)inputDummy, j, 0, 0), bigItemStack.stack.copyWithCount(bigItemStack.count));
        }
        RecipeTransferOperationsResult transferOperations = RecipeTransferUtil.getRecipeTransferOperations((IStackHelper)this.helpers.getStackHelper(), availableItemStacks, (List)recipeSlots.getSlotViews(RecipeIngredientRole.INPUT), craftingSlots);
        if (!transferOperations.missingItems.isEmpty()) {
            return new RecipeTransferErrorMissingSlots((Component)CreateLang.translate("gui.stock_keeper.not_in_stock", new Object[0]).component(), (Collection)transferOperations.missingItems);
        }
        if (!doTransfer) {
            return null;
        }
        ItemStack result = recipe.getResultItem((HolderLookup.Provider)player.level().registryAccess());
        if (result.isEmpty()) {
            return new RecipeTransferErrorTooltip((Component)CreateLang.translate("gui.stock_keeper.recipe_result_empty", new Object[0]).component());
        }
        CraftableBigItemStack cbis = new CraftableBigItemStack(result, recipe);
        screen.recipesToOrder.add(cbis);
        screen.searchBox.setValue("");
        screen.refreshSearchNextTick = true;
        screen.requestCraftable(cbis, maxTransfer ? cbis.stack.getMaxStackSize() : 1);
        return null;
    }
}
