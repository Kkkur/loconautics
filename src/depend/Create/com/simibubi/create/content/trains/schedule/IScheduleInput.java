/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.schedule;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import java.util.List;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public interface IScheduleInput {
    public Pair<ItemStack, Component> getSummary();

    public ResourceLocation getId();

    public CompoundTag getData();

    public void setData(HolderLookup.Provider var1, CompoundTag var2);

    default public int slotsTargeted() {
        return 0;
    }

    default public List<Component> getTitleAs(String type) {
        ResourceLocation id = this.getId();
        return ImmutableList.of((Object)Component.translatable((String)(id.getNamespace() + ".schedule." + type + "." + id.getPath())));
    }

    default public ItemStack getSecondLineIcon() {
        return ItemStack.EMPTY;
    }

    default public void setItem(int slot, ItemStack stack) {
    }

    default public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    @Nullable
    default public List<Component> getSecondLineTooltip(int slot) {
        return null;
    }

    @OnlyIn(value=Dist.CLIENT)
    default public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
    }

    @OnlyIn(value=Dist.CLIENT)
    default public boolean renderSpecialIcon(GuiGraphics graphics, int x, int y) {
        return false;
    }
}
