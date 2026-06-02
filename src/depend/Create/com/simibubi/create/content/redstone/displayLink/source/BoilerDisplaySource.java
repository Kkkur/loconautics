/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  joptsimple.internal.Strings
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.LecternBlockEntity
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class BoilerDisplaySource
extends DisplaySource {
    public static final List<MutableComponent> notEnoughSpaceSingle = List.of(CreateLang.translateDirect("display_source.boiler.not_enough_space", new Object[0]).append((Component)CreateLang.translateDirect("display_source.boiler.for_boiler_status", new Object[0])));
    public static final List<MutableComponent> notEnoughSpaceDouble = List.of(CreateLang.translateDirect("display_source.boiler.not_enough_space", new Object[0]), CreateLang.translateDirect("display_source.boiler.for_boiler_status", new Object[0]));
    public static final List<List<MutableComponent>> notEnoughSpaceFlap = List.of(List.of(CreateLang.translateDirect("display_source.boiler.not_enough_space", new Object[0])), List.of(CreateLang.translateDirect("display_source.boiler.for_boiler_status", new Object[0])));

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        if (stats.maxRows() < 2) {
            return notEnoughSpaceSingle;
        }
        if (stats.maxRows() < 4) {
            return notEnoughSpaceDouble;
        }
        boolean isBook = context.getTargetBlockEntity() instanceof LecternBlockEntity;
        if (isBook) {
            Stream<MutableComponent> componentList = this.getComponents(context, false).map(components -> {
                Optional reduce = components.stream().reduce(MutableComponent::append);
                return reduce.orElse(EMPTY_LINE);
            });
            return List.of(componentList.reduce((comp1, comp2) -> comp1.append((Component)Component.literal((String)"\n")).append((Component)comp2)).orElse(EMPTY_LINE));
        }
        return this.getComponents(context, false).map(components -> {
            Optional reduce = components.stream().reduce(MutableComponent::append);
            return reduce.orElse(EMPTY_LINE);
        }).toList();
    }

    @Override
    public List<List<MutableComponent>> provideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats) {
        if (stats.maxRows() < 4) {
            context.flapDisplayContext = Boolean.FALSE;
            return notEnoughSpaceFlap;
        }
        List<List<MutableComponent>> components = this.getComponents(context, true).toList();
        if ((float)stats.maxColumns() * 7.0f < 42.0f + (float)components.get(1).get(1).getString().length() * 9.0f) {
            context.flapDisplayContext = Boolean.FALSE;
            return notEnoughSpaceFlap;
        }
        return components;
    }

    @Override
    public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay, FlapDisplayLayout layout, int lineIndex) {
        Boolean b;
        Object object;
        if (lineIndex == 0 || (object = context.flapDisplayContext) instanceof Boolean && !(b = (Boolean)object).booleanValue()) {
            if (layout.isLayout("Default")) {
                return;
            }
            layout.loadDefault(flapDisplay.getMaxCharCount());
            return;
        }
        String layoutKey = "Boiler";
        if (layout.isLayout(layoutKey)) {
            return;
        }
        int labelLength = (int)((float)this.labelWidth() * 7.0f);
        float maxSpace = (float)flapDisplay.getMaxCharCount(1) * 7.0f;
        FlapDisplaySection label = new FlapDisplaySection(labelLength, "alphabet", false, true);
        FlapDisplaySection symbols = new FlapDisplaySection(maxSpace - (float)labelLength, "pixel", false, false).wideFlaps();
        layout.configure(layoutKey, List.of(label, symbols));
    }

    private Stream<List<MutableComponent>> getComponents(DisplayLinkContext context, boolean forFlapDisplay) {
        BlockEntity sourceBE = context.getSourceBlockEntity();
        if (!(sourceBE instanceof FluidTankBlockEntity)) {
            return Stream.of(EMPTY);
        }
        FluidTankBlockEntity tankBlockEntity = (FluidTankBlockEntity)sourceBE;
        tankBlockEntity = tankBlockEntity.getControllerBE();
        if (tankBlockEntity == null) {
            return Stream.of(EMPTY);
        }
        BoilerData boiler = tankBlockEntity.boiler;
        int totalTankSize = tankBlockEntity.getTotalTankSize();
        boiler.calcMinMaxForSize(totalTankSize);
        String label = forFlapDisplay ? "boiler.status" : "boiler.status_short";
        MutableComponent size = this.labelOf(forFlapDisplay ? "size" : "");
        MutableComponent water = this.labelOf(forFlapDisplay ? "water" : "");
        MutableComponent heat = this.labelOf(forFlapDisplay ? "heat" : "");
        int lw = this.labelWidth();
        if (forFlapDisplay) {
            size = Component.literal((String)Strings.repeat((char)' ', (int)(lw - this.labelWidthOf("size")))).append((Component)size);
            water = Component.literal((String)Strings.repeat((char)' ', (int)(lw - this.labelWidthOf("water")))).append((Component)water);
            heat = Component.literal((String)Strings.repeat((char)' ', (int)(lw - this.labelWidthOf("heat")))).append((Component)heat);
        }
        return Stream.of(List.of(CreateLang.translateDirect(label, boiler.getHeatLevelTextComponent())), List.of(size, boiler.getSizeComponent(!forFlapDisplay, forFlapDisplay, ChatFormatting.RESET)), List.of(water, boiler.getWaterComponent(!forFlapDisplay, forFlapDisplay, ChatFormatting.RESET)), List.of(heat, boiler.getHeatComponent(!forFlapDisplay, forFlapDisplay, ChatFormatting.RESET)));
    }

    private int labelWidth() {
        return Math.max(this.labelWidthOf("water"), Math.max(this.labelWidthOf("size"), this.labelWidthOf("heat")));
    }

    private int labelWidthOf(String label) {
        return this.labelOf(label).getString().length();
    }

    private MutableComponent labelOf(String label) {
        if (label.isBlank()) {
            return Component.empty();
        }
        return CreateLang.translateDirect("boiler." + label, new Object[0]);
    }

    @Override
    protected String getTranslationKey() {
        return "boiler_status";
    }
}
