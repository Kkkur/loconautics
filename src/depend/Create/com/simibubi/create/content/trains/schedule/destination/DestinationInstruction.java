/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.Glob
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.schedule.destination;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.TextScheduleInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.data.Glob;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class DestinationInstruction
extends TextScheduleInstruction {
    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of((Object)AllBlocks.TRACK_STATION.asStack(), (Object)Component.literal((String)this.getLabelText()));
    }

    @Override
    public boolean supportsConditions() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return Create.asResource("destination");
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return AllBlocks.TRACK_STATION.asStack();
    }

    public String getFilter() {
        return this.getLabelText();
    }

    public String getFilterForRegex() {
        return Glob.toRegexPattern((String)this.getFilter(), (String)"");
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of((Object)CreateLang.translateDirect("schedule.instruction.filter_edit_box", new Object[0]), (Object)CreateLang.translateDirect("schedule.instruction.filter_edit_box_1", new Object[0]).withStyle(ChatFormatting.GRAY), (Object)CreateLang.translateDirect("schedule.instruction.filter_edit_box_2", new Object[0]).withStyle(ChatFormatting.DARK_GRAY), (Object)CreateLang.translateDirect("schedule.instruction.filter_edit_box_3", new Object[0]).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    protected void modifyEditBox(EditBox box) {
        box.setFilter(s -> StringUtils.countMatches((CharSequence)s, (char)'*') <= 3);
    }

    @Override
    @Nullable
    public DiscoveredPath start(ScheduleRuntime runtime, Level level) {
        String regex = this.getFilterForRegex();
        boolean anyMatch = false;
        ArrayList<GlobalStation> validStations = new ArrayList<GlobalStation>();
        Train train = runtime.train;
        if (!train.hasForwardConductor() && !train.hasBackwardConductor()) {
            train.status.missingConductor();
            runtime.startCooldown();
            return null;
        }
        for (GlobalStation globalStation : train.graph.getPoints(EdgePointType.STATION)) {
            if (!globalStation.name.matches(regex)) continue;
            anyMatch = true;
            validStations.add(globalStation);
        }
        DiscoveredPath best = train.navigation.findPathTo(validStations, Double.MAX_VALUE);
        if (best == null) {
            if (anyMatch) {
                train.status.failedNavigation();
            } else {
                train.status.failedNavigationNoTarget(this.getFilter());
            }
            runtime.startCooldown();
            return null;
        }
        return best;
    }
}
