/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.redstone.displayLink.target.NixieTubeDisplayTarget;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NixieTubeDisplaySource
extends SingleLineDisplaySource {
    @Override
    protected String getTranslationKey() {
        return "nixie_tube";
    }

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity sourceBE = context.getSourceBlockEntity();
        if (!(sourceBE instanceof NixieTubeBlockEntity)) {
            return EMPTY_LINE;
        }
        NixieTubeBlockEntity nbe = (NixieTubeBlockEntity)sourceBE;
        MutableComponent text = nbe.getFullText();
        try {
            String line = text.getString();
            Integer.valueOf(line);
            context.flapDisplayContext = Boolean.TRUE;
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return text;
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return !(context.blockEntity().activeTarget instanceof NixieTubeDisplayTarget);
    }

    @Override
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        if (this.isNumeric(context)) {
            return "Number";
        }
        return super.getFlapDisplayLayoutName(context);
    }

    @Override
    protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
        if (this.isNumeric(context)) {
            return new FlapDisplaySection((float)size * 7.0f, "numeric", false, false);
        }
        return super.createSectionForValue(context, size);
    }

    protected boolean isNumeric(DisplayLinkContext context) {
        return context.flapDisplayContext == Boolean.TRUE;
    }
}
