/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.content.contraptions.chassis;

import com.simibubi.create.content.contraptions.chassis.ChassisBlockEntity;
import com.simibubi.create.content.contraptions.chassis.ChassisRangeDisplay;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;

private static class ChassisRangeDisplay.GroupEntry
extends ChassisRangeDisplay.Entry {
    List<ChassisBlockEntity> includedBEs;

    public ChassisRangeDisplay.GroupEntry(ChassisBlockEntity be) {
        super(be);
    }

    @Override
    protected Object getOutlineKey() {
        return this;
    }

    @Override
    protected Set<BlockPos> createSelection(ChassisBlockEntity chassis) {
        HashSet<BlockPos> list = new HashSet<BlockPos>();
        this.includedBEs = this.be.collectChassisGroup();
        if (this.includedBEs == null) {
            return list;
        }
        for (ChassisBlockEntity chassisBlockEntity : this.includedBEs) {
            list.addAll(super.createSelection(chassisBlockEntity));
        }
        return list;
    }
}
