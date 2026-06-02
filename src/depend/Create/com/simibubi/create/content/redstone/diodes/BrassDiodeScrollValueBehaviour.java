/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.redstone.diodes;

import com.simibubi.create.content.redstone.diodes.BrassDiodeBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BrassDiodeScrollValueBehaviour
extends ScrollValueBehaviour {
    public BrassDiodeScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return new ValueSettingsBoard(this.label, 60, 10, CreateLang.translatedOptions("generic.unit", "ticks", "seconds", "minutes"), new ValueSettingsFormatter(this::formatSettings));
    }

    @Override
    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
        if (this.getWorld().isClientSide) {
            return;
        }
        BlockState blockState = this.blockEntity.getBlockState();
        Block block = blockState.getBlock();
        if (block instanceof BrassDiodeBlock) {
            BrassDiodeBlock bdb = (BrassDiodeBlock)block;
            bdb.toggle(this.getWorld(), this.getPos(), blockState, player, hand);
        }
    }

    @Override
    public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings valueSetting, boolean ctrlHeld) {
        int multiplier;
        int value = valueSetting.value();
        switch (valueSetting.row()) {
            case 0: {
                int n = 1;
                break;
            }
            case 1: {
                int n = 20;
                break;
            }
            default: {
                int n = multiplier = 1200;
            }
        }
        if (!valueSetting.equals(this.getValueSettings())) {
            this.playFeedbackSound(this);
        }
        this.setValue(Math.max(2, Math.max(1, value) * multiplier));
    }

    @Override
    public ValueSettingsBehaviour.ValueSettings getValueSettings() {
        int row = 0;
        int value = this.value;
        if (value > 1200) {
            value /= 1200;
            row = 2;
        } else if (value > 60) {
            value /= 20;
            row = 1;
        }
        return new ValueSettingsBehaviour.ValueSettings(row, value);
    }

    public MutableComponent formatSettings(ValueSettingsBehaviour.ValueSettings settings) {
        int value = Math.max(1, settings.value());
        return Component.literal((String)(switch (settings.row()) {
            case 0 -> Math.max(2, value) + "t";
            case 1 -> "0:" + (value < 10 ? "0" : "") + value;
            default -> value + ":00";
        }));
    }

    @Override
    public String getClipboardKey() {
        return "Timings";
    }
}
