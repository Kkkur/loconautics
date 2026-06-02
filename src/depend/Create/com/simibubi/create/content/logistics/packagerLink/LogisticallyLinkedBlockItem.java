/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.packagerLink;

import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LogisticallyLinkedBlockItem
extends BlockItem {
    public LogisticallyLinkedBlockItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    public boolean isFoil(@NotNull ItemStack pStack) {
        return LogisticallyLinkedBlockItem.isTuned(pStack);
    }

    public static boolean isTuned(ItemStack pStack) {
        return pStack.has(DataComponents.BLOCK_ENTITY_DATA);
    }

    @Nullable
    public static UUID networkFromStack(ItemStack pStack) {
        CompoundTag tag = ((CustomData)pStack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.EMPTY)).copyTag();
        if (!tag.hasUUID("Freq")) {
            return null;
        }
        return tag.getUUID("Freq");
    }

    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext tooltipContext, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);
        CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.EMPTY)).copyTag();
        if (!tag.hasUUID("Freq")) {
            return;
        }
        CreateLang.translate("logistically_linked.tooltip", new Object[0]).style(ChatFormatting.GOLD).addTo(tooltipComponents);
        CreateLang.translate("logistically_linked.tooltip_clear", new Object[0]).style(ChatFormatting.GRAY).addTo(tooltipComponents);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (LogisticallyLinkedBlockItem.isTuned(stack)) {
            if (level.isClientSide) {
                level.playSound(player, player.blockPosition(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.75f, 1.0f);
            } else {
                player.displayClientMessage((Component)CreateLang.translateDirect("logistically_linked.cleared", new Object[0]), true);
                stack.remove(DataComponents.BLOCK_ENTITY_DATA);
            }
            return InteractionResultHolder.sidedSuccess((Object)stack, (boolean)level.isClientSide);
        }
        return super.use(level, player, usedHand);
    }

    @NotNull
    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        InteractionHand hand = pContext.getHand();
        if (player == null) {
            return InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            return super.useOn(pContext);
        }
        LogisticallyLinkedBehaviour link = BlockEntityBehaviour.get((BlockGetter)level, pos, LogisticallyLinkedBehaviour.TYPE);
        boolean tuned = LogisticallyLinkedBlockItem.isTuned(stack);
        if (link != null) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            if (!link.mayInteractMessage(player)) {
                return InteractionResult.SUCCESS;
            }
            LogisticallyLinkedBlockItem.assignFrequency(stack, player, link.freqId);
            return InteractionResult.SUCCESS;
        }
        InteractionResult useOn = super.useOn(pContext);
        if (level.isClientSide || useOn == InteractionResult.FAIL) {
            return useOn;
        }
        player.displayClientMessage((Component)(tuned ? CreateLang.translateDirect("logistically_linked.connected", new Object[0]) : CreateLang.translateDirect("logistically_linked.new_network_started", new Object[0])), true);
        return useOn;
    }

    public static void assignFrequency(ItemStack stack, Player player, UUID frequency) {
        CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.EMPTY)).copyTag();
        tag.putUUID("Freq", frequency);
        player.displayClientMessage((Component)CreateLang.translateDirect("logistically_linked.tuned", new Object[0]), true);
        BlockEntity.addEntityType((CompoundTag)tag, ((IBE)((BlockItem)stack.getItem()).getBlock()).getBlockEntityType());
        stack.set(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.of((CompoundTag)tag));
    }
}
