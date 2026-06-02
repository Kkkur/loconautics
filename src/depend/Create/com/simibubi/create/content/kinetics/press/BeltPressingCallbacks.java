/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.kinetics.press;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.item.ItemStack;

public class BeltPressingCallbacks {
    static BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, PressingBehaviour behaviour) {
        if (behaviour.specifics.getKineticSpeed() == 0.0f) {
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }
        if (behaviour.running) {
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        if (!behaviour.specifics.tryProcessOnBelt(transported, null, true)) {
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }
        behaviour.start(PressingBehaviour.Mode.BELT);
        return BeltProcessingBehaviour.ProcessingResult.HOLD;
    }

    static BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, PressingBehaviour behaviour) {
        if (behaviour.specifics.getKineticSpeed() == 0.0f) {
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }
        if (!behaviour.running) {
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }
        if (behaviour.runningTicks != 120) {
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        behaviour.particleItems.clear();
        ArrayList<ItemStack> results = new ArrayList<ItemStack>();
        if (!behaviour.specifics.tryProcessOnBelt(transported, results, false)) {
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }
        boolean bulk = behaviour.specifics.canProcessInBulk() || transported.stack.getCount() == 1;
        transported.clearFanProcessingData();
        List<TransportedItemStack> collect = results.stream().map(stack -> {
            TransportedItemStack copy = transported.copy();
            boolean centered = BeltHelper.isItemUpright(stack);
            copy.stack = stack;
            copy.locked = true;
            copy.angle = centered ? 180 : Create.RANDOM.nextInt(360);
            return copy;
        }).collect(Collectors.toList());
        if (bulk) {
            if (collect.isEmpty()) {
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.removeItem());
            } else {
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(collect));
            }
        } else {
            TransportedItemStack left = transported.copy();
            left.stack.shrink(1);
            if (collect.isEmpty()) {
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(left));
            } else {
                handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(collect, left));
            }
        }
        behaviour.blockEntity.sendData();
        return BeltProcessingBehaviour.ProcessingResult.HOLD;
    }
}
