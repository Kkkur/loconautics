/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class EntityNameDisplaySource
extends SingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        List seats = context.level().getEntitiesOfClass(SeatEntity.class, new AABB(context.getSourcePos()));
        if (seats.isEmpty()) {
            return EMPTY_LINE;
        }
        SeatEntity seatEntity = (SeatEntity)((Object)seats.get(0));
        List passengers = seatEntity.getPassengers();
        if (passengers.isEmpty()) {
            return EMPTY_LINE;
        }
        return Component.literal((String)((Entity)passengers.get(0)).getDisplayName().getString());
    }

    @Override
    protected String getTranslationKey() {
        return "entity_name";
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
