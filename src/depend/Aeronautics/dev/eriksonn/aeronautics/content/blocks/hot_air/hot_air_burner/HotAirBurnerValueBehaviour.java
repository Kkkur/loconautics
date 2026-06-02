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
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.BlockHitResult
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dev.eriksonn.aeronautics.data.AeroLang;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class HotAirBurnerValueBehaviour
extends ScrollValueBehaviour {
    private static final MutableComponent TITLE = AeroLang.translate("generic.hot_air", new Object[0]).component();
    private int interval = 5;
    private Supplier<Integer> minSupplier;
    private Supplier<Integer> maxSupplier;

    public HotAirBurnerValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
    }

    private void updateInterval() {
        this.interval = (this.maxSupplier.get() - this.minSupplier.get() + 250) / 500;
        this.interval *= 5;
        this.interval = Math.max(1, this.interval);
    }

    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        this.updateInterval();
        return new ValueSettingsBoard(this.label, this.maxSupplier.get() / this.interval, 10, (List)ImmutableList.of((Object)TITLE), new ValueSettingsFormatter(this::format));
    }

    private MutableComponent format(ValueSettingsBehaviour.ValueSettings valueSettings) {
        this.updateInterval();
        int value = Mth.clamp((int)(valueSettings.value() * this.interval), (int)this.minSupplier.get(), (int)this.maxSupplier.get());
        return AeroLang.translate("unit.meter_cubed", value).component();
    }

    public ValueSettingsBehaviour.ValueSettings getValueSettings() {
        return new ValueSettingsBehaviour.ValueSettings(0, this.value / this.interval);
    }

    public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings valueSetting, boolean ctrlDown) {
        if (!valueSetting.equals((Object)this.getValueSettings())) {
            this.playFeedbackSound((BlockEntityBehaviour)this);
        }
        this.updateInterval();
        this.setValue(valueSetting.value() * this.interval);
    }

    public ScrollValueBehaviour between(Supplier<Integer> min, Supplier<Integer> max) {
        this.minSupplier = min;
        this.maxSupplier = max;
        this.between(min.get(), max.get());
        return this;
    }

    public void setValue(int value) {
        if ((value = Mth.clamp((int)value, (int)this.minSupplier.get(), (int)this.maxSupplier.get())) == this.value) {
            return;
        }
        this.value = value;
        this.blockEntity.setChanged();
        this.blockEntity.sendData();
    }

    public String getClipboardKey() {
        return "Hot Air";
    }
}
