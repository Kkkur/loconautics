/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.LayeredDraw$Layer
 *  net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.equipment.blueprint;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.blueprint.BlueprintEntity;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.tableCloth.BlueprintOverlayShopContext;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.content.trains.track.TrackPlacement;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BlueprintOverlayRenderer {
    public static final LayeredDraw.Layer OVERLAY = BlueprintOverlayRenderer::renderOverlay;
    static boolean active;
    static boolean empty;
    static boolean noOutput;
    static boolean lastSneakState;
    static BlueprintEntity.BlueprintSection lastTargetedSection;
    static BlueprintOverlayShopContext shopContext;
    static Map<ItemStack, ItemStack[]> cachedRenderedFilters;
    static List<Pair<ItemStack, Boolean>> ingredients;
    static List<ItemStack> results;
    static boolean resultCraftable;

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        BlueprintEntity.BlueprintSection last = lastTargetedSection;
        lastTargetedSection = null;
        active = false;
        noOutput = false;
        shopContext = null;
        if (mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        HitResult mouseOver = mc.hitResult;
        if (mouseOver == null) {
            return;
        }
        if (mouseOver.getType() != HitResult.Type.ENTITY) {
            return;
        }
        EntityHitResult entityRay = (EntityHitResult)mouseOver;
        Entity entity = entityRay.getEntity();
        if (!(entity instanceof BlueprintEntity)) {
            return;
        }
        BlueprintEntity blueprintEntity = (BlueprintEntity)entity;
        BlueprintEntity.BlueprintSection sectionAt = blueprintEntity.getSectionAt(entityRay.getLocation().subtract(blueprintEntity.position()));
        lastTargetedSection = last;
        active = true;
        boolean sneak = mc.player.isShiftKeyDown();
        if (sectionAt != lastTargetedSection || AnimationTickHolder.getTicks() % 10 == 0 || lastSneakState != sneak) {
            BlueprintOverlayRenderer.rebuild(sectionAt, sneak);
        }
        lastTargetedSection = sectionAt;
        lastSneakState = sneak;
    }

    public static void displayTrackRequirements(TrackPlacement.PlacementInfo info, ItemStack pavementItem) {
        if (active) {
            return;
        }
        BlueprintOverlayRenderer.prepareCustomOverlay();
        for (int tracks = info.requiredTracks; tracks > 0; tracks -= 64) {
            ingredients.add((Pair<ItemStack, Boolean>)Pair.of((Object)new ItemStack((ItemLike)info.trackMaterial.getBlock(), Math.min(64, tracks)), (Object)info.hasRequiredTracks));
        }
        for (int pavement = info.requiredPavement; pavement > 0; pavement -= 64) {
            ingredients.add((Pair<ItemStack, Boolean>)Pair.of((Object)pavementItem.copyWithCount(Math.min(64, pavement)), (Object)info.hasRequiredPavement));
        }
    }

    public static void displayChainRequirements(Item chainItem, int count, boolean fulfilled) {
        if (active) {
            return;
        }
        BlueprintOverlayRenderer.prepareCustomOverlay();
        for (int chains = count; chains > 0; chains -= 64) {
            ingredients.add((Pair<ItemStack, Boolean>)Pair.of((Object)new ItemStack((ItemLike)chainItem, Math.min(64, chains)), (Object)fulfilled));
        }
    }

    public static void displayClothShop(TableClothBlockEntity dce, int alreadyPurchased, ShoppingListItem.ShoppingList list) {
        if (active) {
            return;
        }
        BlueprintOverlayRenderer.prepareCustomOverlay();
        noOutput = false;
        shopContext = new BlueprintOverlayShopContext(false, dce.getStockLevelForTrade(list), alreadyPurchased);
        ingredients.add((Pair<ItemStack, Boolean>)Pair.of((Object)dce.getPaymentItem().copyWithCount(dce.getPaymentAmount()), (Object)(!dce.getPaymentItem().isEmpty() && shopContext.stockLevel() > shopContext.purchases() ? 1 : 0)));
        for (BigItemStack entry : dce.requestData.encodedRequest().stacks()) {
            results.add(entry.stack.copyWithCount(entry.count));
        }
    }

    public static void displayShoppingList(Couple<InventorySummary> bakedList) {
        if (active || bakedList == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        BlueprintOverlayRenderer.prepareCustomOverlay();
        noOutput = false;
        shopContext = new BlueprintOverlayShopContext(true, 1, 0);
        for (BigItemStack entry : ((InventorySummary)bakedList.getSecond()).getStacksByCount()) {
            ingredients.add((Pair<ItemStack, Boolean>)Pair.of((Object)entry.stack.copyWithCount(entry.count), (Object)BlueprintOverlayRenderer.canAfford((Player)mc.player, entry)));
        }
        for (BigItemStack entry : ((InventorySummary)bakedList.getFirst()).getStacksByCount()) {
            results.add(entry.stack.copyWithCount(entry.count));
        }
    }

    private static boolean canAfford(Player player, BigItemStack entry) {
        int itemsPresent = 0;
        for (int i = 0; i < player.getInventory().items.size(); ++i) {
            ItemStack item = player.getInventory().getItem(i);
            if (item.isEmpty() || !ItemStack.isSameItemSameComponents((ItemStack)item, (ItemStack)entry.stack)) continue;
            itemsPresent += item.getCount();
        }
        return itemsPresent >= entry.count;
    }

    private static void prepareCustomOverlay() {
        active = true;
        empty = false;
        noOutput = true;
        ingredients.clear();
        results.clear();
        shopContext = null;
    }

    public static void rebuild(BlueprintEntity.BlueprintSection sectionAt, boolean sneak) {
        int i;
        cachedRenderedFilters.clear();
        ItemStackHandler items = sectionAt.getItems();
        boolean empty = true;
        for (int i2 = 0; i2 < 9; ++i2) {
            if (items.getStackInSlot(i2).isEmpty()) continue;
            empty = false;
            break;
        }
        BlueprintOverlayRenderer.empty = empty;
        results.clear();
        if (empty) {
            return;
        }
        boolean firstPass = true;
        boolean success = true;
        Minecraft mc = Minecraft.getInstance();
        ItemStackHandler playerInv = new ItemStackHandler(mc.player.getInventory().getContainerSize());
        for (int i3 = 0; i3 < playerInv.getSlots(); ++i3) {
            playerInv.setStackInSlot(i3, mc.player.getInventory().getItem(i3).copy());
        }
        int amountCrafted = 0;
        Optional recipe = Optional.empty();
        HashMap<Integer, ItemStack> craftingGrid = new HashMap<Integer, ItemStack>();
        ingredients.clear();
        ItemStackHandler missingItems = new ItemStackHandler(64);
        ItemStackHandler availableItems = new ItemStackHandler(64);
        ArrayList<ItemStack> newlyAdded = new ArrayList<ItemStack>();
        ArrayList<ItemStack> newlyMissing = new ArrayList<ItemStack>();
        boolean invalid = false;
        do {
            craftingGrid.clear();
            newlyAdded.clear();
            newlyMissing.clear();
            block3: for (i = 0; i < 9; ++i) {
                FilterItemStack requestedItem = FilterItemStack.of(items.getStackInSlot(i));
                if (requestedItem.isEmpty()) {
                    craftingGrid.put(i, ItemStack.EMPTY);
                    continue;
                }
                for (int slot = 0; slot < playerInv.getSlots(); ++slot) {
                    if (!requestedItem.test((Level)mc.level, playerInv.getStackInSlot(slot))) continue;
                    ItemStack currentItem = playerInv.extractItem(slot, 1, false);
                    craftingGrid.put(i, currentItem);
                    newlyAdded.add(currentItem);
                    continue block3;
                }
                success = false;
                newlyMissing.add(requestedItem.item());
            }
            if (success) {
                ItemStack resultFromRecipe;
                BlueprintEntity.BlueprintCraftingInventory craftingInventory = new BlueprintEntity.BlueprintCraftingInventory(craftingGrid);
                if (!recipe.isPresent()) {
                    recipe = mc.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, (RecipeInput)craftingInventory.asCraftInput(), (Level)mc.level);
                }
                if ((resultFromRecipe = recipe.filter(arg_0 -> BlueprintOverlayRenderer.lambda$rebuild$0((CraftingContainer)craftingInventory, mc, arg_0)).map(arg_0 -> BlueprintOverlayRenderer.lambda$rebuild$1((CraftingContainer)craftingInventory, mc, arg_0)).orElse(ItemStack.EMPTY)).isEmpty()) {
                    if (!recipe.isPresent()) {
                        invalid = true;
                    }
                    success = false;
                } else if (resultFromRecipe.getCount() + amountCrafted > 64) {
                    success = false;
                } else {
                    amountCrafted += resultFromRecipe.getCount();
                    if (results.isEmpty()) {
                        results.add(resultFromRecipe.copy());
                    } else {
                        results.get(0).grow(resultFromRecipe.getCount());
                    }
                    resultCraftable = true;
                    firstPass = false;
                }
            }
            if (success || firstPass) {
                newlyAdded.forEach(s -> ItemHandlerHelper.insertItemStacked((IItemHandler)availableItems, (ItemStack)s, (boolean)false));
                newlyMissing.forEach(s -> ItemHandlerHelper.insertItemStacked((IItemHandler)missingItems, (ItemStack)s, (boolean)false));
            }
            if (success) continue;
            if (!firstPass) break;
            results.clear();
            if (!invalid) {
                results.add(items.getStackInSlot(9));
            }
            resultCraftable = false;
            break;
        } while (sneak && success);
        for (i = 0; i < 9; ++i) {
            ItemStack available = availableItems.getStackInSlot(i);
            if (available.isEmpty()) continue;
            ingredients.add((Pair<ItemStack, Boolean>)Pair.of((Object)available, (Object)true));
        }
        for (i = 0; i < 9; ++i) {
            ItemStack missing = missingItems.getStackInSlot(i);
            if (missing.isEmpty()) continue;
            ingredients.add((Pair<ItemStack, Boolean>)Pair.of((Object)missing, (Object)false));
        }
    }

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.screen != null) {
            return;
        }
        if (!active || empty) {
            return;
        }
        boolean invalidShop = shopContext != null && (ingredients.isEmpty() || ((ItemStack)ingredients.get(0).getFirst()).isEmpty() || shopContext.stockLevel() == 0);
        int w = 21 * ingredients.size();
        if (!noOutput) {
            w += 21 * results.size();
            w += 30;
        }
        int x = (guiGraphics.guiWidth() - w) / 2;
        int y = guiGraphics.guiHeight() - 100;
        if (shopContext != null) {
            TooltipRenderUtil.renderTooltipBackground((GuiGraphics)guiGraphics, (int)(x - 2), (int)(y + 1), (int)(w + 4), (int)19, (int)0, (int)0x55000000, (int)0x55000000, (int)0, (int)0);
            AllGuiTextures.TRADE_OVERLAY.render(guiGraphics, guiGraphics.guiWidth() / 2 - 48, y - 19);
            if (shopContext.purchases() > 0) {
                guiGraphics.renderItem(AllItems.SHOPPING_LIST.asStack(), guiGraphics.guiWidth() / 2 + 20, y - 20);
                guiGraphics.drawString(mc.font, (Component)Component.literal((String)("x" + shopContext.purchases())), guiGraphics.guiWidth() / 2 + 20 + 16, y - 20 + 4, -1118482, true);
            }
        }
        for (Pair<ItemStack, Boolean> pair : ingredients) {
            RenderSystem.enableBlend();
            ((Boolean)pair.getSecond() != false ? AllGuiTextures.HOTSLOT_ACTIVE : AllGuiTextures.HOTSLOT).render(guiGraphics, x, y);
            ItemStack itemStack = (ItemStack)pair.getFirst();
            String count = shopContext != null && !shopContext.checkout() || (Boolean)pair.getSecond() != false ? null : ChatFormatting.GOLD.toString() + itemStack.getCount();
            BlueprintOverlayRenderer.drawItemStack(guiGraphics, mc, x, y, itemStack, count);
            x += 21;
        }
        if (noOutput) {
            return;
        }
        x += 5;
        RenderSystem.enableBlend();
        if (invalidShop) {
            AllGuiTextures.HOTSLOT_ARROW_BAD.render(guiGraphics, x, y + 4);
        } else {
            AllGuiTextures.HOTSLOT_ARROW.render(guiGraphics, x, y + 4);
        }
        x += 25;
        if (results.isEmpty()) {
            AllGuiTextures.HOTSLOT.render(guiGraphics, x, y);
            GuiGameElement.of((ItemLike)Items.BARRIER).at((float)(x + 3), (float)(y + 3)).render(guiGraphics);
        } else {
            for (ItemStack result : results) {
                AllGuiTextures slot;
                AllGuiTextures allGuiTextures = slot = resultCraftable ? AllGuiTextures.HOTSLOT_SUPER_ACTIVE : AllGuiTextures.HOTSLOT;
                if (!invalidShop && shopContext != null && shopContext.stockLevel() > shopContext.purchases()) {
                    slot = AllGuiTextures.HOTSLOT_ACTIVE;
                }
                slot.render(guiGraphics, resultCraftable ? x - 1 : x, resultCraftable ? y - 1 : y);
                BlueprintOverlayRenderer.drawItemStack(guiGraphics, mc, x, y, result, null);
                x += 21;
            }
        }
        if (shopContext != null && !shopContext.checkout()) {
            int cycle = 0;
            for (boolean count : Iterate.trueAndFalse) {
                for (int i = 0; i < results.size(); ++i) {
                    ItemStack result = results.get(i);
                    List tooltipLines = result.getTooltipLines(Item.TooltipContext.of((Level)mc.level), (Player)mc.player, (TooltipFlag)TooltipFlag.NORMAL);
                    if (tooltipLines.size() <= 1) continue;
                    if (count) {
                        ++cycle;
                        continue;
                    }
                    if (mc.gui.getGuiTicks() / 40 % cycle != i) continue;
                    guiGraphics.renderComponentTooltip(mc.gui.getFont(), tooltipLines, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                }
            }
        }
        RenderSystem.disableBlend();
    }

    public static void drawItemStack(GuiGraphics graphics, Minecraft mc, int x, int y, ItemStack itemStack, String count) {
        if (itemStack.getItem() instanceof FilterItem) {
            int step = AnimationTickHolder.getTicks((LevelAccessor)mc.level) / 10;
            ItemStack[] itemsMatchingFilter = BlueprintOverlayRenderer.getItemsMatchingFilter(itemStack);
            if (itemsMatchingFilter.length > 0) {
                itemStack = itemsMatchingFilter[step % itemsMatchingFilter.length];
            }
        }
        GuiGameElement.of((ItemStack)itemStack).at((float)(x + 3), (float)(y + 3)).render(graphics);
        graphics.renderItemDecorations(mc.font, itemStack, x + 3, y + 3, count);
    }

    private static ItemStack[] getItemsMatchingFilter(ItemStack filter) {
        return cachedRenderedFilters.computeIfAbsent(filter, itemStack -> {
            Item patt0$temp = itemStack.getItem();
            if (patt0$temp instanceof FilterItem) {
                FilterItem filterItem = (FilterItem)patt0$temp;
                return filterItem.getFilterItems((ItemStack)itemStack);
            }
            return new ItemStack[0];
        });
    }

    private static /* synthetic */ ItemStack lambda$rebuild$1(CraftingContainer craftingInventory, Minecraft mc, RecipeHolder r) {
        return ((CraftingRecipe)r.value()).assemble((RecipeInput)craftingInventory.asCraftInput(), (HolderLookup.Provider)mc.level.registryAccess());
    }

    private static /* synthetic */ boolean lambda$rebuild$0(CraftingContainer craftingInventory, Minecraft mc, RecipeHolder r) {
        return ((CraftingRecipe)r.value()).matches((RecipeInput)craftingInventory.asCraftInput(), (Level)mc.level);
    }

    static {
        cachedRenderedFilters = new IdentityHashMap<ItemStack, ItemStack[]>();
        ingredients = new ArrayList<Pair<ItemStack, Boolean>>();
        results = new ArrayList<ItemStack>();
        resultCraftable = false;
    }
}
