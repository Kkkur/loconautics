/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.common.util.TriState
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.content.logistics.itemHatch;

import com.simibubi.create.AllBlocks;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid="create")
public class ItemHatchHandler {
    @SubscribeEvent(priority=EventPriority.LOW)
    public static void useOnItemHatchIgnoresSneak(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseItem() == TriState.DEFAULT && AllBlocks.ITEM_HATCH.has(event.getLevel().getBlockState(event.getPos()))) {
            event.setUseBlock(TriState.TRUE);
        }
    }
}
