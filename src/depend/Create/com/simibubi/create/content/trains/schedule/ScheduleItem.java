/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.schedule;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleMenu;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.recipe.ItemCopyingRecipe;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ScheduleItem
extends Item
implements MenuProvider,
ItemCopyingRecipe.SupportsItemCopying {
    public ScheduleItem(Item.Properties pProperties) {
        super(pProperties);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) {
            return InteractionResult.PASS;
        }
        return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

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

    public InteractionResult handScheduleTo(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        InteractionResult pass = InteractionResult.PASS;
        Schedule schedule = ScheduleItem.getSchedule((HolderLookup.Provider)pPlayer.registryAccess(), pStack);
        if (schedule == null) {
            return pass;
        }
        if (pInteractionTarget == null) {
            return pass;
        }
        Entity rootVehicle = pInteractionTarget.getRootVehicle();
        if (!(rootVehicle instanceof CarriageContraptionEntity)) {
            return pass;
        }
        CarriageContraptionEntity entity = (CarriageContraptionEntity)rootVehicle;
        if (pPlayer.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Contraption contraption = entity.getContraption();
        if (contraption instanceof CarriageContraption) {
            CarriageContraption cc = (CarriageContraption)contraption;
            Train train = entity.getCarriage().train;
            if (train == null) {
                return InteractionResult.SUCCESS;
            }
            Integer seatIndex = contraption.getSeatMapping().get(pInteractionTarget.getUUID());
            if (seatIndex == null) {
                return InteractionResult.SUCCESS;
            }
            BlockPos seatPos = contraption.getSeats().get(seatIndex);
            Couple<Boolean> directions = cc.conductorSeats.get(seatPos);
            if (directions == null) {
                pPlayer.displayClientMessage((Component)CreateLang.translateDirect("schedule.non_controlling_seat", new Object[0]), true);
                AllSoundEvents.DENY.playOnServer(pPlayer.level(), (Vec3i)pPlayer.blockPosition(), 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
            if (train.runtime.getSchedule() != null) {
                AllSoundEvents.DENY.playOnServer(pPlayer.level(), (Vec3i)pPlayer.blockPosition(), 1.0f, 1.0f);
                pPlayer.displayClientMessage((Component)CreateLang.translateDirect("schedule.remove_with_empty_hand", new Object[0]), true);
                return InteractionResult.SUCCESS;
            }
            if (schedule.entries.isEmpty()) {
                AllSoundEvents.DENY.playOnServer(pPlayer.level(), (Vec3i)pPlayer.blockPosition(), 1.0f, 1.0f);
                pPlayer.displayClientMessage((Component)CreateLang.translateDirect("schedule.no_stops", new Object[0]), true);
                return InteractionResult.SUCCESS;
            }
            train.runtime.setSchedule(schedule, false);
            AllAdvancements.CONDUCTOR.awardTo(pPlayer);
            AllSoundEvents.CONFIRM.playOnServer(pPlayer.level(), (Vec3i)pPlayer.blockPosition(), 1.0f, 1.0f);
            pPlayer.displayClientMessage((Component)CreateLang.translateDirect("schedule.applied_to_train", new Object[0]).withStyle(ChatFormatting.GREEN), true);
            pStack.shrink(1);
            pPlayer.setItemInHand(pUsedHand, pStack.isEmpty() ? ItemStack.EMPTY : pStack);
        }
        return InteractionResult.SUCCESS;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        Schedule schedule = ScheduleItem.getSchedule(context.registries(), stack);
        if (schedule == null || schedule.entries.isEmpty()) {
            return;
        }
        MutableComponent caret = Component.literal((String)"> ").withStyle(ChatFormatting.GRAY);
        MutableComponent arrow = Component.literal((String)"-> ").withStyle(ChatFormatting.GRAY);
        List<ScheduleEntry> entries = schedule.entries;
        for (int i = 0; i < entries.size(); ++i) {
            boolean current = i == schedule.savedProgress && schedule.entries.size() > 1;
            ScheduleEntry entry = entries.get(i);
            ScheduleInstruction scheduleInstruction = entry.instruction;
            if (!(scheduleInstruction instanceof DestinationInstruction)) continue;
            DestinationInstruction destination = (DestinationInstruction)scheduleInstruction;
            ChatFormatting format = current ? ChatFormatting.YELLOW : ChatFormatting.GOLD;
            MutableComponent prefix = current ? arrow : caret;
            tooltip.add((Component)prefix.copy().append((Component)Component.literal((String)destination.getFilter()).withStyle(format)));
        }
    }

    public static Schedule getSchedule(HolderLookup.Provider registries, ItemStack pStack) {
        if (!pStack.has(AllDataComponents.TRAIN_SCHEDULE)) {
            return null;
        }
        return Schedule.fromTag(registries, (CompoundTag)pStack.get(AllDataComponents.TRAIN_SCHEDULE));
    }

    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        ItemStack heldItem = player.getMainHandItem();
        return new ScheduleMenu((MenuType)AllMenuTypes.SCHEDULE.get(), id, inv, heldItem);
    }

    public Component getDisplayName() {
        return this.getDescription();
    }

    @Override
    public DataComponentType<?> getComponentType() {
        return AllDataComponents.TRAIN_SCHEDULE;
    }
}
