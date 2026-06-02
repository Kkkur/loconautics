/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.physics.floating_block;

import dev.ryanhcode.sable.physics.floating_block.FloatingBlockData;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockMaterial;

public class FloatingBlockCluster {
    private final FloatingBlockMaterial material;
    private final FloatingBlockData blockData;

    public FloatingBlockCluster(FloatingBlockMaterial material) {
        this.material = material;
        this.blockData = new FloatingBlockData();
    }

    public FloatingBlockMaterial getMaterial() {
        return this.material;
    }

    public FloatingBlockData getBlockData() {
        return this.blockData;
    }
}
