/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.ValueListDisplaySource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class FactoryGaugeDisplaySource
extends ValueListDisplaySource {
    @Override
    protected Stream<IntAttached<MutableComponent>> provideEntries(DisplayLinkContext context, int maxRows) {
        List<FactoryPanelPosition> panels = context.blockEntity().factoryPanelSupport.getLinkedPanels();
        if (panels.isEmpty()) {
            return Stream.empty();
        }
        return panels.stream().map(fpp -> this.createEntry(context.level(), (FactoryPanelPosition)fpp)).filter(Objects::nonNull).limit(maxRows);
    }

    @Nullable
    public IntAttached<MutableComponent> createEntry(Level level, FactoryPanelPosition pos) {
        FactoryPanelBehaviour panel = FactoryPanelBehaviour.at((BlockAndTintGetter)level, pos);
        if (panel == null) {
            return null;
        }
        ItemStack filter = panel.getFilter();
        int demand = panel.getAmount() * (panel.upTo ? 1 : filter.getMaxStackSize());
        String s = " ";
        if (demand != 0) {
            int promised = panel.getPromised();
            s = panel.satisfied ? "\u2714" : (promised != 0 ? "\u2191" : "\u25aa");
        }
        return IntAttached.with((int)panel.getLevelInStorage(), (Object)Component.literal((String)(s + " ")).withStyle(style -> style.withColor(panel.getIngredientStatusColor())).append((Component)filter.getHoverName().plainCopy().withStyle(ChatFormatting.RESET)));
    }

    @Override
    protected String getTranslationKey() {
        return "gauge_status";
    }

    @Override
    protected boolean valueFirst() {
        return true;
    }
}
