/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions.chassis;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.contraptions.chassis.ChassisBlockEntity;
import com.simibubi.create.content.contraptions.chassis.ChassisRangeDisplay;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.BulkScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

class ChassisBlockEntity.ChassisScrollValueBehaviour
extends BulkScrollValueBehaviour {
    public ChassisBlockEntity.ChassisScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot, Function<SmartBlockEntity, List<? extends SmartBlockEntity>> groupGetter) {
        super(label, be, slot, groupGetter);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList rows = ImmutableList.of((Object)CreateLang.translateDirect("contraptions.chassis.distance", new Object[0]));
        ValueSettingsFormatter formatter = new ValueSettingsFormatter(vs -> new ValueSettingsBehaviour.ValueSettings(vs.row(), vs.value() + 1).format());
        return new ValueSettingsBoard(this.label, this.max - 1, 1, (List<Component>)rows, formatter);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void newSettingHovered(ValueSettingsBehaviour.ValueSettings valueSetting) {
        if (!((ChassisBlockEntity)ChassisBlockEntity.this).level.isClientSide) {
            return;
        }
        if (!AllKeys.ctrlDown()) {
            ChassisBlockEntity.this.currentlySelectedRange = valueSetting.value() + 1;
        } else {
            for (SmartBlockEntity smartBlockEntity : this.getBulk()) {
                if (!(smartBlockEntity instanceof ChassisBlockEntity)) continue;
                ChassisBlockEntity cbe = (ChassisBlockEntity)smartBlockEntity;
                cbe.currentlySelectedRange = valueSetting.value() + 1;
            }
        }
        ChassisRangeDisplay.display(ChassisBlockEntity.this);
    }

    @Override
    public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings vs, boolean ctrlHeld) {
        super.setValueSettings(player, new ValueSettingsBehaviour.ValueSettings(vs.row(), vs.value() + 1), ctrlHeld);
    }

    @Override
    public ValueSettingsBehaviour.ValueSettings getValueSettings() {
        ValueSettingsBehaviour.ValueSettings vs = super.getValueSettings();
        return new ValueSettingsBehaviour.ValueSettings(vs.row(), vs.value() - 1);
    }

    @Override
    public String getClipboardKey() {
        return "Chassis";
    }
}
