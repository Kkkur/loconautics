/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.ColorResolver
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.sublevel.render.vanilla;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SingleBlockSubLevelWrapper
implements BlockAndTintGetter {
    private ClientLevel level;
    private final BlockPos.MutableBlockPos globalPos = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos localPos = new BlockPos.MutableBlockPos();
    private BlockState state;

    public void setup(ClientLevel level, double x, double y, double z, BlockPos localPos, BlockState state) {
        this.level = level;
        this.globalPos.set(x, y, z);
        this.localPos.set((Vec3i)localPos);
        this.state = state;
    }

    public void clear() {
        this.level = null;
    }

    public float getShade(Direction direction, boolean bl) {
        return this.level.getShade(direction, bl);
    }

    @NotNull
    public LevelLightEngine getLightEngine() {
        return this.level.getLightEngine();
    }

    public int getBrightness(LightLayer lightLayer, BlockPos pos) {
        return this.getLightEngine().getLayerListener(lightLayer).getLightValue((BlockPos)this.globalPos);
    }

    public int getRawBrightness(BlockPos pos, int i) {
        return this.getLightEngine().getRawBrightness((BlockPos)this.globalPos, i);
    }

    public boolean canSeeSky(BlockPos pos) {
        return this.getBrightness(LightLayer.SKY, (BlockPos)this.globalPos) >= this.getMaxLightLevel();
    }

    public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
        return this.level.getBlockTint(pos, colorResolver);
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.level.getBlockEntity(pos);
    }

    @NotNull
    public BlockState getBlockState(BlockPos pos) {
        if (pos.equals((Object)this.localPos)) {
            return this.state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @NotNull
    public FluidState getFluidState(BlockPos pos) {
        if (pos.equals((Object)this.localPos)) {
            return this.state.getFluidState();
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    public int getHeight() {
        return this.level.getHeight();
    }

    public int getMinBuildHeight() {
        return this.level.getMinBuildHeight();
    }

    public ClientLevel getLevel() {
        return this.level;
    }
}
