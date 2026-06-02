/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.mixin_interface.assembly_preventer;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface PrimaryAssemblerExtension {
    @Nullable
    public BlockPos simulated$getPrimaryAssembler();

    public void simulated$setPrimaryAssembler(@Nullable BlockPos var1);
}
