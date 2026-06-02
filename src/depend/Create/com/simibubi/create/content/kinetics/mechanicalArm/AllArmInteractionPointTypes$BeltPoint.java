/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableBoolean;

public static class AllArmInteractionPointTypes.BeltPoint
extends AllArmInteractionPointTypes.DepotPoint {
    public AllArmInteractionPointTypes.BeltPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public void keepAlive() {
        super.keepAlive();
        BeltBlockEntity beltBE = BeltHelper.getSegmentBE((LevelAccessor)this.level, this.pos);
        if (beltBE == null) {
            return;
        }
        TransportedItemStackHandlerBehaviour transport = beltBE.getBehaviour(TransportedItemStackHandlerBehaviour.TYPE);
        if (transport == null) {
            return;
        }
        MutableBoolean found = new MutableBoolean(false);
        transport.handleProcessingOnAllItems(tis -> {
            if (found.isTrue()) {
                return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
            }
            tis.lockedExternally = true;
            found.setTrue();
            return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
        });
    }
}
