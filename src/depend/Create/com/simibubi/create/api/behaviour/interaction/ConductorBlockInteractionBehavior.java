/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 */
package com.simibubi.create.api.behaviour.interaction;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleItem;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.function.Consumer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public abstract class ConductorBlockInteractionBehavior
extends MovingInteractionBehaviour {
    public abstract boolean isValidConductor(BlockState var1);

    protected void onScheduleUpdate(boolean hasSchedule, BlockState currentBlockState, Consumer<BlockState> blockStateSetter) {
    }

    @Override
    public final boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        ItemStack itemInHand = player.getItemInHand(activeHand);
        if (!(contraptionEntity instanceof CarriageContraptionEntity)) {
            return false;
        }
        CarriageContraptionEntity carriageEntity = (CarriageContraptionEntity)contraptionEntity;
        if (activeHand == InteractionHand.OFF_HAND) {
            return false;
        }
        Contraption contraption = carriageEntity.getContraption();
        if (!(contraption instanceof CarriageContraption)) {
            return false;
        }
        CarriageContraption carriageContraption = (CarriageContraption)contraption;
        StructureTemplate.StructureBlockInfo info = carriageContraption.getBlocks().get(localPos);
        if (info == null || !this.isValidConductor(info.state())) {
            return false;
        }
        Direction assemblyDirection = carriageContraption.getAssemblyDirection();
        for (Direction direction : Iterate.directionsInAxis((Direction.Axis)assemblyDirection.getAxis())) {
            if (!carriageContraption.inControl(localPos, direction)) continue;
            Train train = carriageEntity.getCarriage().train;
            if (train == null) {
                return false;
            }
            if (player.level().isClientSide) {
                return true;
            }
            if (train.runtime.getSchedule() != null) {
                if (train.runtime.paused && !train.runtime.completed) {
                    train.runtime.paused = false;
                    AllSoundEvents.CONFIRM.playOnServer(player.level(), (Vec3i)player.blockPosition(), 1.0f, 1.0f);
                    player.displayClientMessage((Component)CreateLang.translateDirect("schedule.continued", new Object[0]), true);
                    return true;
                }
                if (!itemInHand.isEmpty()) {
                    AllSoundEvents.DENY.playOnServer(player.level(), (Vec3i)player.blockPosition(), 1.0f, 1.0f);
                    player.displayClientMessage((Component)CreateLang.translateDirect("schedule.remove_with_empty_hand", new Object[0]), true);
                    return true;
                }
                AllSoundEvents.playItemPickup(player);
                player.displayClientMessage((Component)CreateLang.translateDirect(train.runtime.isAutoSchedule ? "schedule.auto_removed_from_train" : "schedule.removed_from_train", new Object[0]), true);
                player.setItemInHand(activeHand, train.runtime.returnSchedule((HolderLookup.Provider)player.registryAccess()));
                this.onScheduleUpdate(false, info.state(), newBlockState -> this.setBlockState(localPos, contraptionEntity, (BlockState)newBlockState));
                return true;
            }
            if (!AllItems.SCHEDULE.isIn(itemInHand)) {
                return true;
            }
            Schedule schedule = ScheduleItem.getSchedule((HolderLookup.Provider)player.registryAccess(), itemInHand);
            if (schedule == null) {
                return false;
            }
            if (schedule.entries.isEmpty()) {
                AllSoundEvents.DENY.playOnServer(player.level(), (Vec3i)player.blockPosition(), 1.0f, 1.0f);
                player.displayClientMessage((Component)CreateLang.translateDirect("schedule.no_stops", new Object[0]), true);
                return true;
            }
            this.onScheduleUpdate(true, info.state(), newBlockState -> this.setBlockState(localPos, contraptionEntity, (BlockState)newBlockState));
            train.runtime.setSchedule(schedule, false);
            AllAdvancements.CONDUCTOR.awardTo(player);
            AllSoundEvents.CONFIRM.playOnServer(player.level(), (Vec3i)player.blockPosition(), 1.0f, 1.0f);
            player.displayClientMessage((Component)CreateLang.translateDirect("schedule.applied_to_train", new Object[0]).withStyle(ChatFormatting.GREEN), true);
            itemInHand.shrink(1);
            player.setItemInHand(activeHand, itemInHand.isEmpty() ? ItemStack.EMPTY : itemInHand);
            return true;
        }
        player.displayClientMessage((Component)CreateLang.translateDirect("schedule.non_controlling_seat", new Object[0]), true);
        AllSoundEvents.DENY.playOnServer(player.level(), (Vec3i)player.blockPosition(), 1.0f, 1.0f);
        return true;
    }

    private void setBlockState(BlockPos localPos, AbstractContraptionEntity contraption, BlockState newState) {
        StructureTemplate.StructureBlockInfo info = contraption.getContraption().getBlocks().get(localPos);
        if (info != null) {
            this.setContraptionBlockData(contraption, localPos, new StructureTemplate.StructureBlockInfo(info.pos(), newState, info.nbt()));
        }
    }

    public static class BlazeBurner
    extends ConductorBlockInteractionBehavior {
        @Override
        public boolean isValidConductor(BlockState state) {
            return state.getValue(BlazeBurnerBlock.HEAT_LEVEL) != BlazeBurnerBlock.HeatLevel.NONE;
        }
    }
}
