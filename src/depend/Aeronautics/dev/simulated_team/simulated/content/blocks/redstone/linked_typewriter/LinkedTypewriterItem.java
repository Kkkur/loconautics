/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity
 *  com.simibubi.create.foundation.utility.RaycastHelper
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter;

import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.simulated_team.simulated.content.blocks.redstone.AbstractLinkedReceiverBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterInteractionHandler;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterItemBindHandler;
import dev.simulated_team.simulated.mixin.accessor.RedstoneLinkBlockEntityAccessor;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class LinkedTypewriterItem
extends BlockItem {
    public LinkedTypewriterItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Couple frequency = null;
        BlockEntity be = level.getBlockEntity(clickedPos);
        if (be instanceof AbstractLinkedReceiverBlockEntity) {
            AbstractLinkedReceiverBlockEntity abe = (AbstractLinkedReceiverBlockEntity)be;
            frequency = abe.getFrequency();
        } else if (be instanceof RedstoneLinkBlockEntity) {
            RedstoneLinkBlockEntity lbe = (RedstoneLinkBlockEntity)be;
            frequency = ((RedstoneLinkBlockEntityAccessor)lbe).getLink().getNetworkKey();
        }
        if (frequency != null) {
            if (!level.isClientSide) {
                return InteractionResult.CONSUME;
            }
            if (LinkedTypewriterInteractionHandler.getMode() == LinkedTypewriterInteractionHandler.Mode.BINDING_FROM_ITEM) {
                LinkedTypewriterItemBindHandler.reset();
                return InteractionResult.CONSUME;
            }
            LinkedTypewriterInteractionHandler.setMode(LinkedTypewriterInteractionHandler.Mode.BINDING_FROM_ITEM);
            LinkedTypewriterItemBindHandler.setClickedPos(clickedPos);
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide) {
            LinkedTypewriterItemBindHandler.reset();
        }
        return super.useOn(context);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        BlockHitResult blockHitResult = RaycastHelper.rayTraceRange((Level)level, (Player)player, (double)player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE));
        if (blockHitResult.getType() == HitResult.Type.MISS && level.isClientSide) {
            LinkedTypewriterItemBindHandler.reset();
        }
        return super.use(level, player, usedHand);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        CompoundTag tag;
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA) && (tag = ((CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA)).copyTag()).contains("Keys", 9)) {
            int keyCount = tag.getList("Keys", 10).size();
            tooltipComponents.add((Component)Component.translatable((String)"simulated.linked_typewriter.key_count", (Object[])new Object[]{keyCount}).withStyle(ChatFormatting.GOLD));
        }
    }
}
