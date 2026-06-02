/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.levelWrappers.WrappedServerLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.fml.loading.FMLLoader
 *  net.neoforged.neoforge.common.CommonHooks
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.neoforge.platform;

import dev.ryanhcode.sable.platform.SablePlatform;
import net.createmod.catnip.levelWrappers.WrappedServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class SablePlatformImpl
implements SablePlatform {
    @Override
    public boolean isWrappedLevel(@Nullable Level level) {
        if (FMLLoader.getLoadingModList().getModFileById("create") != null) {
            return level instanceof WrappedServerLevel;
        }
        return false;
    }

    @Override
    public boolean isBlockstateLadder(BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return CommonHooks.isLivingOnLadder((BlockState)state, (Level)level, (BlockPos)pos, (LivingEntity)entity).isPresent();
    }
}
