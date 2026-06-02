/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.offroad.content.blocks.wheel_mount;

import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

private record WheelMountBlockEntity.TerrainCastResult(double maxExtension, @NotNull Direction normal, @Nullable SubLevel subLevel, @Nullable BlockPos minInteractingBlock) {
}
