/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.tunnel;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.world.phys.Vec3;

public class BrassTunnelFilterSlot
extends ValueBoxTransform.Sided {
    @Override
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)13.0, (double)15.5);
    }
}
