/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.Glob
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.apache.commons.lang3.StringUtils
 */
package com.simibubi.create.content.trains.schedule.destination;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.TextScheduleInstruction;
import com.simibubi.create.content.trains.station.GlobalPackagePort;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import net.createmod.catnip.data.Glob;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

public class FetchPackagesInstruction
extends TextScheduleInstruction {
    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of((Object)this.getSecondLineIcon(), (Object)CreateLang.translateDirect("schedule.instruction.package_retrieval", new Object[0]));
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of((Object)CreateLang.translate("schedule.instruction.package_retrieval.summary", new Object[0]).style(ChatFormatting.GOLD).component(), (Object)CreateLang.translateDirect("generic.in_quotes", Component.literal((String)this.getLabelText())), (Object)CreateLang.translateDirect("schedule.instruction.package_retrieval.summary_1", new Object[0]).withStyle(ChatFormatting.GRAY), (Object)CreateLang.translateDirect("schedule.instruction.package_retrieval.summary_2", new Object[0]).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return PackageStyles.getDefaultBox();
    }

    public String getFilter() {
        return this.getLabelText();
    }

    public String getFilterForRegex() {
        if (this.getFilter().isBlank()) {
            return Glob.toRegexPattern((String)"*", (String)"");
        }
        return Glob.toRegexPattern((String)this.getFilter(), (String)"");
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of((Object)CreateLang.translateDirect("schedule.instruction.address_filter_edit_box", new Object[0]), (Object)CreateLang.translateDirect("schedule.instruction.address_filter_edit_box_1", new Object[0]).withStyle(ChatFormatting.GRAY), (Object)CreateLang.translateDirect("schedule.instruction.address_filter_edit_box_2", new Object[0]).withStyle(ChatFormatting.DARK_GRAY), (Object)CreateLang.translateDirect("schedule.instruction.address_filter_edit_box_3", new Object[0]).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    protected void modifyEditBox(EditBox box) {
        box.setFilter(s -> StringUtils.countMatches((CharSequence)s, (char)'*') <= 3);
    }

    @Override
    public ResourceLocation getId() {
        return Create.asResource("package_retrieval");
    }

    @Override
    public boolean supportsConditions() {
        return true;
    }

    @Override
    public DiscoveredPath start(ScheduleRuntime runtime, Level level) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return null;
        }
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
            ServerLevel dimLevel = server.getLevel(globalStation.blockEntityDimension);
            if (dimLevel == null) continue;
            for (Map.Entry<BlockPos, GlobalPackagePort> entry : globalStation.connectedPorts.entrySet()) {
                BlockEntity blockEntity;
                GlobalPackagePort port = entry.getValue();
                BlockPos pos = entry.getKey();
                Object postboxInventory = port.offlineBuffer;
                if (dimLevel.isLoaded(pos) && (blockEntity = dimLevel.getBlockEntity(pos)) instanceof PostboxBlockEntity) {
                    PostboxBlockEntity ppbe = (PostboxBlockEntity)blockEntity;
                    postboxInventory = ppbe.inventory;
                }
                for (int slot = 0; slot < postboxInventory.getSlots(); ++slot) {
                    ItemStack stack = postboxInventory.getStackInSlot(slot);
                    if (!PackageItem.isPackage(stack) || PackageItem.matchAddress(stack, port.address)) continue;
                    try {
                        if (!PackageItem.getAddress(stack).matches(regex)) continue;
                        anyMatch = true;
                        validStations.add(globalStation);
                        continue;
                    }
                    catch (PatternSyntaxException patternSyntaxException) {
                        // empty catch block
                    }
                }
            }
        }
        if (validStations.isEmpty()) {
            runtime.startCooldown();
            runtime.state = ScheduleRuntime.State.PRE_TRANSIT;
            ++runtime.currentEntry;
            return null;
        }
        DiscoveredPath best = train.navigation.findPathTo(validStations, Double.MAX_VALUE);
        if (best == null) {
            if (anyMatch) {
                train.status.failedNavigation();
            }
            runtime.startCooldown();
            return null;
        }
        return best;
    }
}
