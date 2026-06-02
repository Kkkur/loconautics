/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.gimbal_sensor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlock;
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import java.util.List;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public static class GimbalSensorBlockEntity.GimbalSensorScrollValueBehaviour
extends ScrollValueBehaviour {
    protected Direction lastSide = Direction.NORTH;
    protected int primaryValue;
    protected int secondaryValue;
    protected Function<Integer, String> formatter = v -> Math.abs(v) + Component.translatable((String)"create.generic.unit.degrees").getString();
    protected int min;
    protected int max;

    public GimbalSensorBlockEntity.GimbalSensorScrollValueBehaviour(GimbalSensorBlockEntity be) {
        super((Component)Component.translatable((String)"create.kinetics.valve_handle.rotated_angle"), (SmartBlockEntity)be, (ValueBoxTransform)new GimbalSensorBlockEntity.GimbalSensorValueBox(be));
        this.withFormatter(this.formatter);
        this.primaryValue = 0;
        this.secondaryValue = 0;
    }

    public ScrollValueBehaviour between(int min, int max) {
        this.min = min;
        this.max = max;
        return super.between(min, max);
    }

    public boolean isPrimaryAxis() {
        Direction.Axis blockAxis = (Direction.Axis)this.blockEntity.getBlockState().getValue(GimbalSensorBlock.HORIZONTAL_AXIS);
        return this.lastSide.getAxis().isHorizontal() && this.lastSide.getAxis() != blockAxis;
    }

    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList rows = ImmutableList.of((Object)Component.literal((String)"\u27f3").withStyle(ChatFormatting.BOLD), (Object)Component.literal((String)"\u27f2").withStyle(ChatFormatting.BOLD));
        return new ValueSettingsBoard(this.label, 90, 15, (List)rows, new ValueSettingsFormatter(this::formatValue));
    }

    public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings valueSetting, boolean ctrlHeld) {
        int value = Math.max(0, valueSetting.value());
        if (!valueSetting.equals((Object)this.getValueSettings())) {
            this.playFeedbackSound((BlockEntityBehaviour)this);
        }
        this.setValue(valueSetting.row() == 0 ? -value : value);
    }

    public ValueSettingsBehaviour.ValueSettings getValueSettings() {
        return new ValueSettingsBehaviour.ValueSettings(this.getValue() < 0 ? 0 : 1, Math.abs(this.getValue()));
    }

    public MutableComponent formatValue(ValueSettingsBehaviour.ValueSettings settings) {
        return SimLang.number(Math.max(0, Math.abs(settings.value()))).add(Component.translatable((String)"create.generic.unit.degrees")).component();
    }

    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        nbt.putInt("ScrollValue1", this.primaryValue);
        nbt.putInt("ScrollValue2", this.secondaryValue);
        super.write(nbt, registries, clientPacket);
    }

    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        this.primaryValue = nbt.getInt("ScrollValue1");
        this.secondaryValue = nbt.getInt("ScrollValue2");
        super.read(nbt, registries, clientPacket);
    }

    public boolean writeToClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        if (!this.acceptsValueSettings()) {
            return false;
        }
        tag.putInt("ScrollValue1", this.primaryValue);
        tag.putInt("ScrollValue2", this.secondaryValue);
        return true;
    }

    public boolean readFromClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (!this.acceptsValueSettings()) {
            return false;
        }
        if (!tag.contains("ScrollValue1") || !tag.contains("ScrollValue2")) {
            return true;
        }
        if (simulate) {
            return true;
        }
        this.primaryValue = tag.getInt("ScrollValue1");
        this.secondaryValue = tag.getInt("ScrollValue2");
        this.blockEntity.setChanged();
        this.blockEntity.sendData();
        return true;
    }

    public int getValue() {
        return this.isPrimaryAxis() ? this.primaryValue : this.secondaryValue;
    }

    public void setValue(int value) {
        if ((value = Mth.clamp((int)value, (int)this.min, (int)this.max)) == this.getValue()) {
            return;
        }
        if (this.isPrimaryAxis()) {
            this.primaryValue = value;
        } else {
            this.secondaryValue = value;
        }
        this.blockEntity.setChanged();
        this.blockEntity.sendData();
    }

    public String formatValue() {
        return this.formatter.apply(this.getValue());
    }
}
