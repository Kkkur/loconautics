/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
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
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.trains.schedule.condition;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.CargoThresholdCondition;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidThresholdCondition
extends CargoThresholdCondition {
    private FilterItemStack compareStack = FilterItemStack.empty();

    @Override
    protected Component getUnit() {
        return Component.literal((String)"b");
    }

    @Override
    protected ItemStack getIcon() {
        return this.compareStack.item();
    }

    @Override
    protected boolean test(Level level, Train train, CompoundTag context) {
        CargoThresholdCondition.Ops operator = this.getOperator();
        int target = this.getThreshold();
        int foundFluid = 0;
        for (Carriage carriage : train.carriages) {
            MountedFluidStorageWrapper fluids = carriage.storage.getFluids();
            for (int i = 0; i < fluids.getTanks(); ++i) {
                FluidStack fluidInTank = fluids.getFluidInTank(i);
                if (!this.compareStack.test(level, fluidInTank)) continue;
                foundFluid += fluidInTank.getAmount();
            }
        }
        this.requestStatusToUpdate(foundFluid / 1000, context);
        return operator.test(foundFluid, target * 1000);
    }

    @Override
    protected void writeAdditional(HolderLookup.Provider registries, CompoundTag tag) {
        super.writeAdditional(registries, tag);
        tag.put("Bucket", (Tag)this.compareStack.serializeNBT(registries));
    }

    @Override
    protected void readAdditional(HolderLookup.Provider registries, CompoundTag tag) {
        super.readAdditional(registries, tag);
        if (tag.contains("Bucket")) {
            this.compareStack = FilterItemStack.of(registries, tag.getCompound("Bucket"));
        }
    }

    @Override
    public boolean tickCompletion(Level level, Train train, CompoundTag context) {
        return super.tickCompletion(level, train, context);
    }

    @OnlyIn(value=Dist.CLIENT)
    private FluidStack loadFluid() {
        return this.compareStack.fluid((Level)Minecraft.getInstance().level);
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of((Object)CreateLang.translateDirect("schedule.condition.threshold.train_holds", CreateLang.translateDirect("schedule.condition.threshold." + Lang.asId((String)this.getOperator().name()), new Object[0])), (Object)CreateLang.translateDirect("schedule.condition.threshold.x_units_of_item", this.getThreshold(), CreateLang.translateDirect("schedule.condition.threshold.buckets", new Object[0]), this.compareStack.isEmpty() ? CreateLang.translateDirect("schedule.condition.threshold.anything", new Object[0]) : (this.compareStack.isFilterItem() ? CreateLang.translateDirect("schedule.condition.threshold.matching_content", new Object[0]) : this.loadFluid().getHoverName())).withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.compareStack = FilterItemStack.of(stack);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.compareStack.item();
    }

    @Override
    public ResourceLocation getId() {
        return Create.asResource("fluid_threshold");
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        super.initConfigurationWidgets(builder);
        builder.addSelectionScrollInput(71, 50, (i, l) -> i.forOptions((List<? extends Component>)ImmutableList.of((Object)CreateLang.translateDirect("schedule.condition.threshold.buckets", new Object[0]))).titled(null), "Measure");
    }

    @Override
    public MutableComponent getWaitingStatus(Level level, Train train, CompoundTag tag) {
        int lastDisplaySnapshot = this.getLastDisplaySnapshot(tag);
        if (lastDisplaySnapshot == -1) {
            return Component.empty();
        }
        int offset = this.getOperator() == CargoThresholdCondition.Ops.LESS ? -1 : (this.getOperator() == CargoThresholdCondition.Ops.GREATER ? 1 : 0);
        return CreateLang.translateDirect("schedule.condition.threshold.status", lastDisplaySnapshot, Math.max(0, this.getThreshold() + offset), CreateLang.translateDirect("schedule.condition.threshold.buckets", new Object[0]));
    }
}
