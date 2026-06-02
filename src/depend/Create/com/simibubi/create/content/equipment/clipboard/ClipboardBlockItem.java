/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.equipment.clipboard;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import com.simibubi.create.content.equipment.clipboard.ClipboardScreen;
import com.simibubi.create.foundation.recipe.ItemCopyingRecipe;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ClipboardBlockItem
extends BlockItem
implements ItemCopyingRecipe.SupportsItemCopying {
    public ClipboardBlockItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    @NotNull
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            return super.useOn(context);
        }
        return this.use(context.getLevel(), player, context.getHand()).getResult();
    }

    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, Player pPlayer, ItemStack pStack, BlockState pState) {
        if (pLevel.isClientSide()) {
            return false;
        }
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof ClipboardBlockEntity)) {
            return false;
        }
        ClipboardBlockEntity cbe = (ClipboardBlockEntity)blockEntity;
        cbe.notifyUpdate();
        return true;
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResultHolder.pass((Object)heldItem);
        }
        player.getCooldowns().addCooldown(heldItem.getItem(), 10);
        if (world.isClientSide) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.openScreen(player, heldItem.getComponents()));
        }
        ClipboardContent content = (ClipboardContent)heldItem.getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, (Object)ClipboardContent.EMPTY);
        heldItem.set(AllDataComponents.CLIPBOARD_CONTENT, (Object)content.setType(ClipboardOverrides.ClipboardType.EDITING));
        return InteractionResultHolder.success((Object)heldItem);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void openScreen(Player player, DataComponentMap components) {
        if (Minecraft.getInstance().player == player) {
            ScreenOpener.open((Screen)new ClipboardScreen(player.getInventory().selected, components, null));
        }
    }

    public void registerModelOverrides() {
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> ClipboardOverrides.registerModelOverridesClient(this));
    }

    @Override
    public DataComponentType<?> getComponentType() {
        return AllDataComponents.CLIPBOARD_CONTENT;
    }
}
