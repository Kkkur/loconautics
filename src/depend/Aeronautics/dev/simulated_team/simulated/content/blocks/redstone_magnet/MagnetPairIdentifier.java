/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.redstone_magnet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.NotNull;

public final class MagnetPairIdentifier {
    @NotNull
    private final BlockPos posA;
    @NotNull
    private final BlockPos posB;

    public MagnetPairIdentifier(BlockPos posA, BlockPos posB) {
        if (posA.compareTo((Vec3i)posB) > 0) {
            this.posA = posA;
            this.posB = posB;
        } else {
            this.posB = posA;
            this.posA = posB;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MagnetPairIdentifier that = (MagnetPairIdentifier)o;
        return this.posA.equals((Object)that.posA) && this.posB.equals((Object)that.posB);
    }

    public int hashCode() {
        int result = this.posA.hashCode();
        result = 31 * result + this.posB.hashCode();
        return result;
    }

    public String toString() {
        return "MagnetPairIdentifier[posA=" + String.valueOf(this.posA) + ", posB=" + String.valueOf(this.posB) + "]";
    }
}
