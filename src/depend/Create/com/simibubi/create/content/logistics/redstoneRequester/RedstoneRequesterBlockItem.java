/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlock;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class RedstoneRequesterBlockItem
extends LogisticallyLinkedBlockItem {
    public RedstoneRequesterBlockItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!RedstoneRequesterBlockItem.isTuned(stack)) {
            return;
        }
        if (!stack.has(AllDataComponents.AUTO_REQUEST_DATA)) {
            super.appendHoverText(stack, tooltipContext, tooltipComponents, tooltipFlag);
            return;
        }
        CreateLang.translate("logistically_linked.tooltip", new Object[0]).style(ChatFormatting.GOLD).addTo(tooltipComponents);
        RedstoneRequesterBlock.appendRequesterTooltip(stack, tooltipComponents);
    }
}
