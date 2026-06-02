/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.content.fluids.FlowSource;
import net.createmod.catnip.math.BlockFace;

public static class FlowSource.Blocked
extends FlowSource {
    public FlowSource.Blocked(BlockFace location) {
        super(location);
    }

    @Override
    public boolean isEndpoint() {
        return false;
    }
}
