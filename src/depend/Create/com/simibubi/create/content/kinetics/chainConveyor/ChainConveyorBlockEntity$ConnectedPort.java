/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public record ChainConveyorBlockEntity.ConnectedPort(float chainPosition, @Nullable BlockPos connection, String filter) {
}
