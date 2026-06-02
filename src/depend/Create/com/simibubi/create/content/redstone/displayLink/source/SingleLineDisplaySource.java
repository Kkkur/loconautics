/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class SingleLineDisplaySource
extends DisplaySource {
    protected abstract MutableComponent provideLine(DisplayLinkContext var1, DisplayTargetStats var2);

    protected abstract boolean allowsLabeling(DisplayLinkContext var1);

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        if (isFirstLine && this.allowsLabeling(context)) {
            this.addLabelingTextBox(builder);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    protected void addLabelingTextBox(ModularGuiLineBuilder builder) {
        builder.addTextInput(0, 137, (e, t) -> {
            e.setValue("");
            t.withTooltip((List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("display_source.label", new Object[0]).withStyle(s -> s.withColor(5476833)), (Object)CreateLang.translateDirect("gui.schedule.lmb_edit", new Object[0]).withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})));
        }, "Label");
    }

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        String label;
        MutableComponent line = this.provideLine(context, stats);
        if (line == EMPTY_LINE) {
            return EMPTY;
        }
        if (this.allowsLabeling(context) && !(label = context.sourceConfig().getString("Label")).isEmpty()) {
            line = Component.literal((String)(label + " ")).append((Component)line);
        }
        return ImmutableList.of((Object)line);
    }

    @Override
    public List<List<MutableComponent>> provideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats) {
        String label;
        if (this.allowsLabeling(context) && !(label = context.sourceConfig().getString("Label")).isEmpty()) {
            return ImmutableList.of((Object)ImmutableList.of((Object)Component.literal((String)(label + " ")), (Object)this.provideLine(context, stats)));
        }
        return super.provideFlapDisplayText(context, stats);
    }

    @Override
    public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay, FlapDisplayLayout layout) {
        String layoutKey = this.getFlapDisplayLayoutName(context);
        if (!this.allowsLabeling(context)) {
            if (!layout.isLayout(layoutKey)) {
                layout.configure(layoutKey, (List<FlapDisplaySection>)ImmutableList.of((Object)this.createSectionForValue(context, flapDisplay.getMaxCharCount())));
            }
            return;
        }
        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) {
            if (!layout.isLayout(layoutKey)) {
                layout.configure(layoutKey, (List<FlapDisplaySection>)ImmutableList.of((Object)this.createSectionForValue(context, flapDisplay.getMaxCharCount())));
            }
            return;
        }
        String layoutName = label.length() + "_Labeled_" + layoutKey;
        if (layout.isLayout(layoutName)) {
            return;
        }
        int maxCharCount = flapDisplay.getMaxCharCount();
        FlapDisplaySection labelSection = new FlapDisplaySection((float)Math.min(maxCharCount, label.length() + 1) * 7.0f, "alphabet", false, false);
        if (label.length() + 1 < maxCharCount) {
            layout.configure(layoutName, (List<FlapDisplaySection>)ImmutableList.of((Object)labelSection, (Object)this.createSectionForValue(context, maxCharCount - label.length() - 1)));
        } else {
            layout.configure(layoutName, (List<FlapDisplaySection>)ImmutableList.of((Object)labelSection));
        }
    }

    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        return "Default";
    }

    protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
        return new FlapDisplaySection((float)size * 7.0f, "alphabet", false, false);
    }
}
