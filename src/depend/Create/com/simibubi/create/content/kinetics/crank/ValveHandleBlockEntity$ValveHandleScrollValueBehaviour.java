/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.kinetics.crank;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public static class ValveHandleBlockEntity.ValveHandleScrollValueBehaviour
extends ScrollValueBehaviour {
    public ValveHandleBlockEntity.ValveHandleScrollValueBehaviour(SmartBlockEntity be) {
        super((Component)CreateLang.translateDirect("kinetics.valve_handle.rotated_angle", new Object[0]), be, new ValveHandleBlockEntity.ValveHandleValueBox());
        this.withFormatter(v -> String.valueOf(Math.abs(v)) + CreateLang.translateDirect("generic.unit.degrees", new Object[0]).getString());
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList rows = ImmutableList.of((Object)Component.literal((String)"\u27f3").withStyle(ChatFormatting.BOLD), (Object)Component.literal((String)"\u27f2").withStyle(ChatFormatting.BOLD));
        return new ValueSettingsBoard(this.label, 180, 45, (List<Component>)rows, new ValueSettingsFormatter(this::formatValue));
    }

    @Override
    public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings valueSetting, boolean ctrlHeld) {
        int value = Math.max(1, valueSetting.value());
        if (!valueSetting.equals(this.getValueSettings())) {
            this.playFeedbackSound(this);
        }
        this.setValue(valueSetting.row() == 0 ? -value : value);
    }

    @Override
    public ValueSettingsBehaviour.ValueSettings getValueSettings() {
        return new ValueSettingsBehaviour.ValueSettings(this.value < 0 ? 0 : 1, Math.abs(this.value));
    }

    public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings settings) {
        return CreateLang.number(Math.max(1, Math.abs(settings.value()))).add(CreateLang.translateDirect("generic.unit.degrees", new Object[0])).component();
    }

    @Override
    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
        if (this.getWorld().isClientSide) {
            return;
        }
        BlockState blockState = this.blockEntity.getBlockState();
        Block block = blockState.getBlock();
        if (block instanceof ValveHandleBlock) {
            ValveHandleBlock vhb = (ValveHandleBlock)block;
            vhb.clicked(this.getWorld(), this.getPos(), blockState, player, hand);
        }
    }
}
