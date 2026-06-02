/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState
 *  dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionContainer
 *  dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionRegion
 *  dev.ryanhcode.sable.util.BoundedBitVolume3i
 *  dev.ryanhcode.sable.util.LevelAccelerator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.absorber;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionContainer;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionRegion;
import dev.ryanhcode.sable.util.BoundedBitVolume3i;
import dev.ryanhcode.sable.util.LevelAccelerator;
import dev.simulated_team.simulated.content.blocks.absorber.AbsorberBlock;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.Set;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class AbsorberBlockEntity
extends SmartBlockEntity {
    private static final Direction[] DIRECTION_PRIORITY = new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.DOWN};
    @Nullable
    private WaterOcclusionRegion currentRegion;
    public LerpedFloat animationTimer = LerpedFloat.linear();

    public AbsorberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.animationTimer.chase(0.0, 0.04, LerpedFloat.Chaser.LINEAR);
    }

    private static boolean dfs(LevelAccelerator accelerator, BlockPos pos, Set<BlockPos> visited, Set<BlockPos> enclosed) {
        boolean isEnclosed;
        boolean safe = pos.getY() <= accelerator.getMaxBuildHeight();
        visited.add(pos);
        BlockState state = accelerator.getBlockState(pos);
        boolean bl = isEnclosed = !VoxelNeighborhoodState.isSolid((BlockGetter)accelerator, (BlockPos)pos, (BlockState)state);
        if (!isEnclosed) {
            return safe;
        }
        enclosed.add(pos);
        for (Direction dir : DIRECTION_PRIORITY) {
            BlockPos relative = pos.relative(dir);
            enclosed.add(relative);
            if (visited.contains(relative)) continue;
            safe = safe && AbsorberBlockEntity.dfs(accelerator, relative, visited, enclosed);
        }
        return safe;
    }

    public void tick() {
        super.tick();
        boolean powered = (Boolean)this.getBlockState().getValue((Property)AbsorberBlock.POWERED);
        if (this.currentRegion != null && this.currentRegion.isDirty()) {
            this.removeRegionIfExists();
        }
        this.animationTimer.tickChaser();
        if (this.animationTimer.settled()) {
            this.animationTimer.updateChaseTarget(powered ? 1.0f : 0.0f);
        }
        if (this.animationTimer.settled() && !this.level.isClientSide) {
            if (powered) {
                if (this.currentRegion == null) {
                    this.buildRegion();
                }
                boolean doWet = false;
                for (Direction dir : Direction.values()) {
                    BlockPos newPos = this.getBlockPos().relative(dir);
                    BlockState blockstate = this.level.getBlockState(newPos);
                    FluidState fluidstate = this.level.getFluidState(newPos);
                    if (!fluidstate.is(FluidTags.WATER) || !(blockstate.getBlock() instanceof LiquidBlock)) continue;
                    this.level.setBlock(newPos, Blocks.AIR.defaultBlockState(), 3);
                    doWet = true;
                }
                if (doWet && !((Boolean)this.getBlockState().getValue((Property)AbsorberBlock.WET)).booleanValue()) {
                    this.level.setBlock(this.getBlockPos(), (BlockState)this.getBlockState().cycle((Property)AbsorberBlock.WET), 2);
                }
            } else {
                this.removeRegionIfExists();
                if (((Boolean)this.getBlockState().getValue((Property)AbsorberBlock.WET)).booleanValue()) {
                    this.level.setBlock(this.getBlockPos(), (BlockState)this.getBlockState().cycle((Property)AbsorberBlock.WET), 2);
                }
            }
        }
        if (this.level.isClientSide && this.animationTimer.getChaseTarget() < this.animationTimer.getValue() && ((Boolean)this.getBlockState().getValue((Property)AbsorberBlock.WET)).booleanValue()) {
            BlockPos pos = this.getBlockPos();
            float t = this.animationTimer.getValue();
            float offset = 0.5f + t * t * 0.5f;
            for (int i = 0; i < 2; ++i) {
                this.level.addParticle((ParticleOptions)ParticleTypes.SPLASH, (double)((float)pos.getX() + this.level.random.nextFloat()), (double)((float)pos.getY() + offset), (double)((float)pos.getZ() + this.level.random.nextFloat()), 0.0, 0.0, 0.0);
            }
        }
    }

    private void buildRegion() {
        if (this.currentRegion != null) {
            throw new IllegalStateException("EvaporatorBlockEntity already has a region assigned.");
        }
        WaterOcclusionContainer container = WaterOcclusionContainer.getContainer((Level)this.level);
        ObjectOpenHashSet visited = new ObjectOpenHashSet();
        ObjectOpenHashSet enclosed = new ObjectOpenHashSet();
        if (AbsorberBlockEntity.dfs(new LevelAccelerator(this.level), this.worldPosition.above(), (Set<BlockPos>)visited, (Set<BlockPos>)enclosed) && !enclosed.isEmpty()) {
            this.currentRegion = container.addRegion(BoundedBitVolume3i.fromBlocks((Iterable)enclosed));
        }
    }

    public void invalidate() {
        super.invalidate();
        this.removeRegionIfExists();
    }

    private void removeRegionIfExists() {
        if (this.currentRegion != null) {
            WaterOcclusionContainer container = WaterOcclusionContainer.getContainer((Level)this.level);
            if (container != null) {
                container.removeRegion(this.currentRegion);
            }
            this.currentRegion = null;
        }
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
