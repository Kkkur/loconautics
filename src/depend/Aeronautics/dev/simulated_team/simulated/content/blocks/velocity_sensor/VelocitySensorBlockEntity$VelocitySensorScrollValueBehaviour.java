/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.BlockHitResult
 */
package dev.simulated_team.simulated.content.blocks.velocity_sensor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.simulated_team.simulated.data.SimLang;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public static class VelocitySensorBlockEntity.VelocitySensorScrollValueBehaviour
extends ScrollValueBehaviour {
    private boolean towards;

    public VelocitySensorBlockEntity.VelocitySensorScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
    }

    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList rows = ImmutableList.of((Object)SimLang.translate("velocity_sensor.selection.away", new Object[0]).component(), (Object)SimLang.translate("velocity_sensor.selection.towards", new Object[0]).component());
        return new ValueSettingsBoard(this.label, this.max, 10, (List)rows, new ValueSettingsFormatter(this::formatValue));
    }

    public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings settings) {
        return SimLang.number(settings.value()).component().append(" m/s");
    }

    public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings valueSetting, boolean ctrlDown) {
        super.setValueSettings(player, valueSetting, ctrlDown);
        this.towards = valueSetting.row() == 1;
    }

    public int getValue() {
        return super.getValue() * (this.towards ? 1 : -1);
    }

    public ValueSettingsBehaviour.ValueSettings getValueSettings() {
        return new ValueSettingsBehaviour.ValueSettings(this.towards ? 1 : 0, this.value);
    }

    public boolean isTowards() {
        return this.towards;
    }

    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        this.towards = nbt.getBoolean("ScrollValueTowards");
        super.read(nbt, registries, clientPacket);
    }

    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putBoolean("ScrollValueTowards", this.towards);
        super.write(nbt, registries, clientPacket);
    }
}
