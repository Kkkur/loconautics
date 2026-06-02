/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 */
package com.simibubi.create.content.trains.schedule.condition;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.CargoThresholdCondition;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

public class ItemThresholdCondition
extends CargoThresholdCondition {
    private FilterItemStack stack = FilterItemStack.empty();

    @Override
    protected Component getUnit() {
        return Component.literal((String)(this.inStacks() ? "\u25a4" : ""));
    }

    @Override
    protected ItemStack getIcon() {
        return this.stack.item();
    }

    @Override
    protected boolean test(Level level, Train train, CompoundTag context) {
        CargoThresholdCondition.Ops operator = this.getOperator();
        int target = this.getThreshold();
        boolean stacks = this.inStacks();
        int foundItems = 0;
        for (Carriage carriage : train.carriages) {
            CombinedInvWrapper items = carriage.storage.getAllItems();
            for (int i = 0; i < items.getSlots(); ++i) {
                ItemStack stackInSlot = items.getStackInSlot(i);
                if (!this.stack.test(level, stackInSlot)) continue;
                if (stacks) {
                    foundItems += stackInSlot.getCount() == stackInSlot.getMaxStackSize() ? 1 : 0;
                    continue;
                }
                foundItems += stackInSlot.getCount();
            }
        }
        this.requestStatusToUpdate(foundItems, context);
        return operator.test(foundItems, target);
    }

    @Override
    protected void writeAdditional(HolderLookup.Provider registries, CompoundTag tag) {
        super.writeAdditional(registries, tag);
        tag.put("Item", (Tag)this.stack.serializeNBT(registries));
    }

    @Override
    protected void readAdditional(HolderLookup.Provider registries, CompoundTag tag) {
        super.readAdditional(registries, tag);
        if (tag.contains("Item")) {
            this.stack = FilterItemStack.of(registries, tag.getCompound("Item"));
        }
    }

    @Override
    public boolean tickCompletion(Level level, Train train, CompoundTag context) {
        return super.tickCompletion(level, train, context);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.stack = FilterItemStack.of(stack);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.stack.item();
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of((Object)CreateLang.translateDirect("schedule.condition.threshold.train_holds", CreateLang.translateDirect("schedule.condition.threshold." + Lang.asId((String)this.getOperator().name()), new Object[0])), (Object)CreateLang.translateDirect("schedule.condition.threshold.x_units_of_item", this.getThreshold(), CreateLang.translateDirect("schedule.condition.threshold." + (this.inStacks() ? "stacks" : "items"), new Object[0]), this.stack.isEmpty() ? CreateLang.translateDirect("schedule.condition.threshold.anything", new Object[0]) : (this.stack.isFilterItem() ? CreateLang.translateDirect("schedule.condition.threshold.matching_content", new Object[0]) : this.stack.item().getHoverName())).withStyle(ChatFormatting.DARK_AQUA));
    }

    private boolean inStacks() {
        return this.intData("Measure") == 1;
    }

    @Override
    public ResourceLocation getId() {
        return Create.asResource("item_threshold");
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        super.initConfigurationWidgets(builder);
        builder.addSelectionScrollInput(71, 50, (i, l) -> i.forOptions((List<? extends Component>)ImmutableList.of((Object)CreateLang.translateDirect("schedule.condition.threshold.items", new Object[0]), (Object)CreateLang.translateDirect("schedule.condition.threshold.stacks", new Object[0]))).titled(CreateLang.translateDirect("schedule.condition.threshold.item_measure", new Object[0])), "Measure");
    }

    @Override
    public MutableComponent getWaitingStatus(Level level, Train train, CompoundTag tag) {
        int lastDisplaySnapshot = this.getLastDisplaySnapshot(tag);
        if (lastDisplaySnapshot == -1) {
            return Component.empty();
        }
        int offset = this.getOperator() == CargoThresholdCondition.Ops.LESS ? -1 : (this.getOperator() == CargoThresholdCondition.Ops.GREATER ? 1 : 0);
        return CreateLang.translateDirect("schedule.condition.threshold.status", lastDisplaySnapshot, Math.max(0, this.getThreshold() + offset), CreateLang.translateDirect("schedule.condition.threshold." + (this.inStacks() ? "stacks" : "items"), new Object[0]));
    }
}
