/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.packagePort.frogport;

import com.simibubi.create.AllSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FrogportSounds {
    public void open(Level level, BlockPos pos) {
        AllSoundEvents.FROGPORT_OPEN.playAt(level, Vec3.atCenterOf((Vec3i)pos), 0.5f, 1.0f, false);
    }

    public void close(Level level, BlockPos pos) {
        if (!this.isPlayerNear(pos)) {
            return;
        }
        AllSoundEvents.FROGPORT_CLOSE.playAt(level, Vec3.atCenterOf((Vec3i)pos), 1.0f, 1.25f + level.random.nextFloat() * 0.25f, true);
    }

    public void catchPackage(Level level, BlockPos pos) {
        if (!this.isPlayerNear(pos)) {
            return;
        }
        AllSoundEvents.FROGPORT_CATCH.playAt(level, Vec3.atCenterOf((Vec3i)pos), 1.0f, 1.0f, false);
    }

    public void depositPackage(Level level, BlockPos pos) {
        if (!this.isPlayerNear(pos)) {
            return;
        }
        AllSoundEvents.FROGPORT_DEPOSIT.playAt(level, Vec3.atCenterOf((Vec3i)pos), 1.0f, 1.0f, false);
    }

    private boolean isPlayerNear(BlockPos pos) {
        return pos.closerThan((Vec3i)Minecraft.getInstance().player.blockPosition(), 20.0);
    }
}
