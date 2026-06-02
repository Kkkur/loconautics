/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import org.apache.commons.lang3.mutable.MutableObject;

public class ItemNameDisplaySource
extends SingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        DisplayLinkBlockEntity gatherer = context.blockEntity();
        Direction direction = gatherer.getDirection();
        BlockPos.MutableBlockPos pos = gatherer.getSourcePosition().mutable();
        MutableComponent combined = EMPTY_LINE.copy();
        for (int i = 0; i < 32; ++i) {
            TransportedItemStackHandlerBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)context.level(), (BlockPos)pos, TransportedItemStackHandlerBehaviour.TYPE);
            pos.move(direction);
            if (behaviour == null) break;
            MutableObject stackHolder = new MutableObject();
            behaviour.handleCenteredProcessingOnAllItems(0.25f, tis -> {
                stackHolder.setValue((Object)tis.stack);
                return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
            });
            ItemStack stack = (ItemStack)stackHolder.getValue();
            if (stack == null || stack.isEmpty()) continue;
            combined = combined.append(stack.getHoverName());
        }
        return combined;
    }

    @Override
    protected String getTranslationKey() {
        return "combine_item_names";
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        return "Number";
    }
}
