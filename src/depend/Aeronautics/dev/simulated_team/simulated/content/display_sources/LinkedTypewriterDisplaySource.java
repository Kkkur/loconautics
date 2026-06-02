/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource
 *  com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.display_sources;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LinkedTypewriterDisplaySource
extends SingleLineDisplaySource {
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof LinkedTypewriterBlockEntity)) {
            return EMPTY_LINE.copy();
        }
        LinkedTypewriterBlockEntity be = (LinkedTypewriterBlockEntity)blockEntity;
        return Component.literal((String)be.getTypedEntry());
    }

    protected String getTranslationKey() {
        return "typewriter.typed_text";
    }

    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
