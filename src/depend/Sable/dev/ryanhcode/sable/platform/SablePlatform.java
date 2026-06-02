/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.platform;

import dev.ryanhcode.sable.platform.SablePlatformUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface SablePlatform {
    public static final SablePlatform INSTANCE = SablePlatformUtil.load(SablePlatform.class);

    public boolean isWrappedLevel(@Nullable Level var1);

    public boolean isBlockstateLadder(BlockState var1, Level var2, BlockPos var3, LivingEntity var4);
}
