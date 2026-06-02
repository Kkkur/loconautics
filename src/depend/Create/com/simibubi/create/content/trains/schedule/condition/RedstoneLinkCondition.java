/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.schedule.condition;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class RedstoneLinkCondition
extends ScheduleWaitCondition {
    public Couple<RedstoneLinkNetworkHandler.Frequency> freq = Couple.create(() -> RedstoneLinkNetworkHandler.Frequency.EMPTY);

    @Override
    public int slotsTargeted() {
        return 2;
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of((Object)AllBlocks.REDSTONE_LINK.asStack(), (Object)(this.lowActivation() ? CreateLang.translateDirect("schedule.condition.redstone_link_off", new Object[0]) : CreateLang.translateDirect("schedule.condition.redstone_link_on", new Object[0])));
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of((Object)CreateLang.translateDirect(slot == 0 ? "logistics.firstFrequency" : "logistics.secondFrequency", new Object[0]).withStyle(ChatFormatting.RED));
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of((Object)CreateLang.translateDirect("schedule.condition.redstone_link.frequency_" + (this.lowActivation() ? "unpowered" : "powered"), new Object[0]), (Object)Component.literal((String)" #1 ").withStyle(ChatFormatting.GRAY).append((Component)((RedstoneLinkNetworkHandler.Frequency)this.freq.getFirst()).getStack().getHoverName().copy().withStyle(ChatFormatting.DARK_AQUA)), (Object)Component.literal((String)" #2 ").withStyle(ChatFormatting.GRAY).append((Component)((RedstoneLinkNetworkHandler.Frequency)this.freq.getSecond()).getStack().getHoverName().copy().withStyle(ChatFormatting.DARK_AQUA)));
    }

    @Override
    public boolean tickCompletion(Level level, Train train, CompoundTag context) {
        int lastChecked = context.contains("LastChecked") ? context.getInt("LastChecked") : -1;
        int status = Create.REDSTONE_LINK_NETWORK_HANDLER.globalPowerVersion.get();
        if (status == lastChecked) {
            return false;
        }
        context.putInt("LastChecked", status);
        return Create.REDSTONE_LINK_NETWORK_HANDLER.hasAnyLoadedPower(this.freq) != this.lowActivation();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.freq.set(slot == 0, (Object)RedstoneLinkNetworkHandler.Frequency.of(stack));
        super.setItem(slot, stack);
    }

    @Override
    public ItemStack getItem(int slot) {
        return ((RedstoneLinkNetworkHandler.Frequency)this.freq.get(slot == 0)).getStack();
    }

    @Override
    public ResourceLocation getId() {
        return Create.asResource("redstone_link");
    }

    @Override
    protected void writeAdditional(HolderLookup.Provider registries, CompoundTag tag) {
        tag.put("Frequency", (Tag)this.freq.serializeEach(f -> (CompoundTag)f.getStack().saveOptional(registries)));
    }

    public boolean lowActivation() {
        return this.intData("Inverted") == 1;
    }

    @Override
    protected void readAdditional(HolderLookup.Provider registries, CompoundTag tag) {
        if (tag.contains("Frequency")) {
            this.freq = Couple.deserializeEach((ListTag)tag.getList("Frequency", 10), c -> RedstoneLinkNetworkHandler.Frequency.of(ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)c)));
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addSelectionScrollInput(20, 101, (i, l) -> i.forOptions(CreateLang.translatedOptions("schedule.condition.redstone_link", "powered", "unpowered")).titled(CreateLang.translateDirect("schedule.condition.redstone_link.frequency_state", new Object[0])), "Inverted");
    }

    @Override
    public MutableComponent getWaitingStatus(Level level, Train train, CompoundTag tag) {
        return CreateLang.translateDirect("schedule.condition.redstone_link.status", new Object[0]);
    }
}
