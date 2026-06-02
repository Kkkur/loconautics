/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.redstone.analogLever;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.mixin.accessor.BlockBehaviourAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class AnalogLeverBlock
extends FaceAttachedHorizontalDirectionalBlock
implements IBE<AnalogLeverBlockEntity> {
    public static final MapCodec<AnalogLeverBlock> CODEC = AnalogLeverBlock.simpleCodec(AnalogLeverBlock::new);

    public AnalogLeverBlock(BlockBehaviour.Properties p_i48402_1_) {
        super(p_i48402_1_);
    }

    public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        if (worldIn.isClientSide) {
            AnalogLeverBlock.addParticles(state, (LevelAccessor)worldIn, pos, 1.0f);
            return InteractionResult.SUCCESS;
        }
        return this.onBlockEntityUse((BlockGetter)worldIn, pos, be -> {
            boolean sneak = player.isShiftKeyDown();
            be.changeState(sneak);
            float f = 0.25f + (float)(be.state + 5) / 15.0f * 0.5f;
            worldIn.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.2f, f);
            return InteractionResult.SUCCESS;
        });
    }

    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return this.getBlockEntityOptional(blockAccess, pos).map(al -> al.state).orElse(0);
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return AnalogLeverBlock.getConnectedDirection((BlockState)blockState) == side ? this.getSignal(blockState, blockAccess, pos, side) : 0;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> {
            if (be.state != 0 && rand.nextFloat() < 0.25f) {
                AnalogLeverBlock.addParticles(stateIn, (LevelAccessor)worldIn, pos, 0.5f);
            }
        });
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (isMoving || state.getBlock() == newState.getBlock()) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> {
            if (be.state != 0) {
                AnalogLeverBlock.updateNeighbors(state, worldIn, pos);
            }
            worldIn.removeBlockEntity(pos);
        });
    }

    private static void addParticles(BlockState state, LevelAccessor worldIn, BlockPos pos, float alpha) {
        Direction direction = ((Direction)state.getValue((Property)FACING)).getOpposite();
        Direction direction1 = AnalogLeverBlock.getConnectedDirection((BlockState)state).getOpposite();
        double d0 = (double)pos.getX() + 0.5 + 0.1 * (double)direction.getStepX() + 0.2 * (double)direction1.getStepX();
        double d1 = (double)pos.getY() + 0.5 + 0.1 * (double)direction.getStepY() + 0.2 * (double)direction1.getStepY();
        double d2 = (double)pos.getZ() + 0.5 + 0.1 * (double)direction.getStepZ() + 0.2 * (double)direction1.getStepZ();
        worldIn.addParticle((ParticleOptions)new DustParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f), alpha), d0, d1, d2, 0.0, 0.0, 0.0);
    }

    static void updateNeighbors(BlockState state, Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, state.getBlock());
        world.updateNeighborsAt(pos.relative(AnalogLeverBlock.getConnectedDirection((BlockState)state).getOpposite()), state.getBlock());
    }

    @NotNull
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return ((BlockBehaviourAccessor)Blocks.LEVER).create$getShape(state, worldIn, pos, context);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{FACING, FACE}));
    }

    @Override
    public Class<AnalogLeverBlockEntity> getBlockEntityClass() {
        return AnalogLeverBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AnalogLeverBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.ANALOG_LEVER.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @NotNull
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
