/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.kinetics.motor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class KineticScrollValueBehaviour
extends ScrollValueBehaviour {
    public KineticScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
        this.withFormatter(v -> String.valueOf(Math.abs(v)));
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList rows = ImmutableList.of((Object)Component.literal((String)"\u27f3").withStyle(ChatFormatting.BOLD), (Object)Component.literal((String)"\u27f2").withStyle(ChatFormatting.BOLD));
        ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
        return new ValueSettingsBoard(this.label, 256, 32, (List<Component>)rows, formatter);
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

    public MutableComponent formatSettings(ValueSettingsBehaviour.ValueSettings settings) {
        return CreateLang.number(Math.max(1, Math.abs(settings.value()))).add(CreateLang.text(settings.row() == 0 ? "\u27f3" : "\u27f2").style(ChatFormatting.BOLD)).component();
    }

    @Override
    public String getClipboardKey() {
        return "Speed";
    }
}
