/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FactoryPanelBlockItem
extends LogisticallyLinkedBlockItem {
    public FactoryPanelBlockItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    public InteractionResult place(BlockPlaceContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        if (!FactoryPanelBlockItem.isTuned(stack)) {
            AllSoundEvents.DENY.playOnServer(pContext.getLevel(), (Vec3i)pContext.getClickedPos());
            pContext.getPlayer().displayClientMessage((Component)CreateLang.translate("factory_panel.tune_before_placing", new Object[0]).component(), true);
            return InteractionResult.FAIL;
        }
        return super.place(pContext);
    }

    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state) {
        return super.updateCustomBlockEntityTag(pos, level, player, FactoryPanelBlockItem.fixCtrlCopiedStack(stack), state);
    }

    public static ItemStack fixCtrlCopiedStack(ItemStack stack) {
        if (FactoryPanelBlockItem.isTuned(stack) && FactoryPanelBlockItem.networkFromStack(stack) == null) {
            CompoundTag bet = ((CustomData)stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.EMPTY)).copyTag();
            UUID frequency = UUID.randomUUID();
            for (FactoryPanelBlock.PanelSlot slot : FactoryPanelBlock.PanelSlot.values()) {
                CompoundTag panelTag = bet.getCompound(CreateLang.asId((String)slot.name()));
                if (!panelTag.hasUUID("Freq")) continue;
                frequency = panelTag.getUUID("Freq");
            }
            bet = new CompoundTag();
            bet.putUUID("Freq", frequency);
            BlockEntity.addEntityType((CompoundTag)bet, ((IBE)((BlockItem)stack.getItem()).getBlock()).getBlockEntityType());
            stack.set(DataComponents.BLOCK_ENTITY_DATA, (Object)CustomData.of((CompoundTag)bet));
        }
        return stack;
    }
}
