/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
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
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.ItemContainerContents
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LecternBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.redstone.link.controller;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlock;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItemRenderer;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerMenu;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import java.util.function.Consumer;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.items.ItemStackHandler;

public class LinkedControllerItem
extends Item
implements MenuProvider {
    public LinkedControllerItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState hitState = world.getBlockState(pos);
        if (player.mayBuild()) {
            if (player.isShiftKeyDown()) {
                if (AllBlocks.LECTERN_CONTROLLER.has(hitState)) {
                    if (!world.isClientSide) {
                        ((LecternControllerBlock)AllBlocks.LECTERN_CONTROLLER.get()).withBlockEntityDo((BlockGetter)world, pos, be -> be.swapControllers(stack, player, ctx.getHand(), hitState));
                    }
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (AllBlocks.REDSTONE_LINK.has(hitState)) {
                    if (world.isClientSide) {
                        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.toggleBindMode(ctx.getClickedPos()));
                    }
                    player.getCooldowns().addCooldown((Item)this, 2);
                    return InteractionResult.SUCCESS;
                }
                if (hitState.is(Blocks.LECTERN) && !((Boolean)hitState.getValue((Property)LecternBlock.HAS_BOOK)).booleanValue()) {
                    if (!world.isClientSide) {
                        ItemStack lecternStack = player.isCreative() ? stack.copy() : stack.split(1);
                        ((LecternControllerBlock)AllBlocks.LECTERN_CONTROLLER.get()).replaceLectern(hitState, world, pos, lecternStack);
                    }
                    return InteractionResult.SUCCESS;
                }
                if (AllBlocks.LECTERN_CONTROLLER.has(hitState)) {
                    return InteractionResult.PASS;
                }
            }
        }
        return this.use(world, player, ctx.getHand()).getResult();
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!world.isClientSide && player instanceof ServerPlayer && player.mayBuild()) {
                player.openMenu((MenuProvider)this, buf -> ItemStack.STREAM_CODEC.encode(buf, (Object)heldItem));
            }
            return InteractionResultHolder.success((Object)heldItem);
        }
        if (!player.isShiftKeyDown()) {
            if (world.isClientSide) {
                CatnipServices.PLATFORM.executeOnClientOnly(() -> this::toggleActive);
            }
            player.getCooldowns().addCooldown((Item)this, 2);
        }
        return InteractionResultHolder.pass((Object)heldItem);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void toggleBindMode(BlockPos pos) {
        LinkedControllerClientHandler.toggleBindMode(pos);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void toggleActive() {
        LinkedControllerClientHandler.toggle();
    }

    public static ItemStackHandler getFrequencyItems(ItemStack stack) {
        ItemStackHandler newInv = new ItemStackHandler(12);
        if (AllItems.LINKED_CONTROLLER.get() != stack.getItem()) {
            throw new IllegalArgumentException("Cannot get frequency items from non-controller: " + String.valueOf(stack));
        }
        if (!stack.has(AllDataComponents.LINKED_CONTROLLER_ITEMS)) {
            return newInv;
        }
        ItemHelper.fillItemStackHandler((ItemContainerContents)stack.getOrDefault(AllDataComponents.LINKED_CONTROLLER_ITEMS, (Object)ItemContainerContents.EMPTY), newInv);
        return newInv;
    }

    public static Couple<RedstoneLinkNetworkHandler.Frequency> toFrequency(ItemStack controller, int slot) {
        ItemStackHandler frequencyItems = LinkedControllerItem.getFrequencyItems(controller);
        return Couple.create((Object)RedstoneLinkNetworkHandler.Frequency.of(frequencyItems.getStackInSlot(slot * 2)), (Object)RedstoneLinkNetworkHandler.Frequency.of(frequencyItems.getStackInSlot(slot * 2 + 1)));
    }

    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        ItemStack heldItem = player.getMainHandItem();
        return LinkedControllerMenu.create(id, inv, heldItem);
    }

    public Component getDisplayName() {
        return this.getDescription();
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new LinkedControllerItemRenderer()));
    }
}
