/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.TypedDataComponent
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.filter.AttributeFilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.filter.ListFilterItem;
import com.simibubi.create.content.logistics.filter.PackageFilterItem;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.ItemCopyingRecipe;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public abstract class FilterItem
extends Item
implements MenuProvider,
ItemCopyingRecipe.SupportsItemCopying {
    public static ListFilterItem regular(Item.Properties properties) {
        return new ListFilterItem(properties);
    }

    public static AttributeFilterItem attribute(Item.Properties properties) {
        return new AttributeFilterItem(properties);
    }

    public static PackageFilterItem address(Item.Properties properties) {
        return new PackageFilterItem(properties);
    }

    protected FilterItem(Item.Properties properties) {
        super(properties);
    }

    @NotNull
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) {
            return InteractionResult.PASS;
        }
        return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    @OnlyIn(value=Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        if (AllKeys.shiftDown()) {
            return;
        }
        List<Component> makeSummary = this.makeSummary(stack);
        if (makeSummary.isEmpty()) {
            return;
        }
        tooltip.add(CommonComponents.SPACE);
        tooltip.addAll(makeSummary);
    }

    public abstract List<Component> makeSummary(ItemStack var1);

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!world.isClientSide && player instanceof ServerPlayer) {
                player.openMenu((MenuProvider)this, buf -> ItemStack.STREAM_CODEC.encode(buf, (Object)heldItem));
            }
            return InteractionResultHolder.success((Object)heldItem);
        }
        return InteractionResultHolder.pass((Object)heldItem);
    }

    public abstract AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3);

    public Component getDisplayName() {
        return this.getDescription();
    }

    public static boolean testDirect(ItemStack filter, ItemStack stack, boolean matchNBT) {
        if (matchNBT) {
            if (PackageItem.isPackage(filter) && PackageItem.isPackage(stack)) {
                return FilterItem.doPackagesHaveSameData(filter, stack);
            }
            return ItemStack.isSameItemSameComponents((ItemStack)filter, (ItemStack)stack);
        }
        if (PackageItem.isPackage(filter) && PackageItem.isPackage(stack)) {
            return true;
        }
        return ItemHelper.sameItem(filter, stack);
    }

    public static boolean doPackagesHaveSameData(@NotNull ItemStack a, @NotNull ItemStack b) {
        if (a.isEmpty()) {
            return false;
        }
        if (!ItemStack.isSameItemSameComponents((ItemStack)a, (ItemStack)b)) {
            return false;
        }
        for (TypedDataComponent component : a.getComponents()) {
            DataComponentType type = component.type();
            if (type.equals(AllDataComponents.PACKAGE_ORDER_DATA) || type.equals(AllDataComponents.PACKAGE_ORDER_CONTEXT) || Objects.equals(a.get(type), b.get(type))) continue;
            return false;
        }
        return true;
    }

    @Override
    public abstract DataComponentType<?> getComponentType();

    public abstract FilterItemStack makeStackWrapper(ItemStack var1);

    public abstract ItemStack[] getFilterItems(ItemStack var1);
}
