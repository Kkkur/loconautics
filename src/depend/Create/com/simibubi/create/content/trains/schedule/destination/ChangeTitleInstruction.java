/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.schedule.destination;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.TextScheduleInstruction;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ChangeTitleInstruction
extends TextScheduleInstruction {
    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of((Object)this.icon(), (Object)Component.literal((String)this.getLabelText()));
    }

    @Override
    public ResourceLocation getId() {
        return Create.asResource("rename");
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return this.icon();
    }

    @Override
    public boolean supportsConditions() {
        return false;
    }

    public String getScheduleTitle() {
        return this.getLabelText();
    }

    private ItemStack icon() {
        return new ItemStack((ItemLike)Items.NAME_TAG);
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of((Object)CreateLang.translateDirect("schedule.instruction.name_edit_box", new Object[0]), (Object)CreateLang.translateDirect("schedule.instruction.name_edit_box_1", new Object[0]).withStyle(ChatFormatting.GRAY), (Object)CreateLang.translateDirect("schedule.instruction.name_edit_box_2", new Object[0]).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    @Nullable
    public DiscoveredPath start(ScheduleRuntime runtime, Level level) {
        runtime.currentTitle = this.getScheduleTitle();
        runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
        ++runtime.currentEntry;
        return null;
    }
}
