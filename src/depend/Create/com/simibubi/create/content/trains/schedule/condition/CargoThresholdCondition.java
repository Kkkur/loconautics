/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.schedule.condition;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.LazyTickedScheduleCondition;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class CargoThresholdCondition
extends LazyTickedScheduleCondition {
    public CargoThresholdCondition() {
        super(20);
        this.data.putString("Threshold", "10");
    }

    @Override
    public boolean lazyTickCompletion(Level level, Train train, CompoundTag context) {
        int lastChecked = context.contains("LastChecked") ? context.getInt("LastChecked") : -1;
        int status = 0;
        for (Carriage carriage : train.carriages) {
            status += carriage.storage.getVersion();
        }
        if (status == lastChecked) {
            return false;
        }
        context.putInt("LastChecked", status);
        return this.test(level, train, context);
    }

    protected void requestStatusToUpdate(int amount, CompoundTag context) {
        context.putInt("CurrentDisplay", amount);
        super.requestStatusToUpdate(context);
    }

    protected int getLastDisplaySnapshot(CompoundTag context) {
        if (!context.contains("CurrentDisplay")) {
            return -1;
        }
        return context.getInt("CurrentDisplay");
    }

    protected abstract boolean test(Level var1, Train var2, CompoundTag var3);

    protected abstract Component getUnit();

    protected abstract ItemStack getIcon();

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of((Object)this.getIcon(), (Object)Component.literal((String)(this.getOperator().formatted + " " + this.getThreshold())).append(this.getUnit()));
    }

    @Override
    public int slotsTargeted() {
        return 1;
    }

    public Ops getOperator() {
        return this.enumData("Operator", Ops.class);
    }

    public int getThreshold() {
        try {
            return Integer.valueOf(this.textData("Threshold"));
        }
        catch (NumberFormatException e) {
            this.data.putString("Threshold", "0");
            return 0;
        }
    }

    public int getMeasure() {
        return this.intData("Measure");
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of((Object)CreateLang.translateDirect("schedule.condition.threshold.place_item", new Object[0]), (Object)CreateLang.translateDirect("schedule.condition.threshold.place_item_2", new Object[0]).withStyle(ChatFormatting.GRAY), (Object)CreateLang.translateDirect("schedule.condition.threshold.place_item_3", new Object[0]).withStyle(ChatFormatting.GRAY));
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addSelectionScrollInput(0, 24, (i, l) -> i.forOptions(Ops.translatedOptions()).titled(CreateLang.translateDirect("schedule.condition.threshold.train_holds", "")).format(state -> Component.literal((String)(" " + Ops.values()[state.intValue()].formatted))), "Operator");
        builder.addIntegerTextInput(29, 41, (e, t) -> {}, "Threshold");
    }

    public static enum Ops {
        GREATER(">"),
        LESS("<"),
        EQUAL("=");

        public String formatted;

        private Ops(String formatted) {
            this.formatted = formatted;
        }

        public boolean test(int current, int target) {
            return switch (this.ordinal()) {
                case 0 -> {
                    if (current > target) {
                        yield true;
                    }
                    yield false;
                }
                case 2 -> {
                    if (current == target) {
                        yield true;
                    }
                    yield false;
                }
                case 1 -> {
                    if (current < target) {
                        yield true;
                    }
                    yield false;
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + String.valueOf((Object)this));
            };
        }

        public static List<? extends Component> translatedOptions() {
            return Arrays.stream(Ops.values()).map(op -> CreateLang.translateDirect("schedule.condition.threshold." + Lang.asId((String)op.name()), new Object[0])).toList();
        }
    }
}
