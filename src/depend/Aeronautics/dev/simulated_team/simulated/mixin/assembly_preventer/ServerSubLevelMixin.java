/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.simulated_team.simulated.mixin.assembly_preventer;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.simulated_team.simulated.mixin_interface.assembly_preventer.PrimaryAssemblerExtension;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={ServerSubLevel.class})
public class ServerSubLevelMixin
implements PrimaryAssemblerExtension {
    @Unique
    private BlockPos simulated$primaryAssembler = null;

    @Override
    @Nullable
    public BlockPos simulated$getPrimaryAssembler() {
        return this.simulated$primaryAssembler;
    }

    @Override
    public void simulated$setPrimaryAssembler(BlockPos pos) {
        this.simulated$primaryAssembler = pos;
    }
}
