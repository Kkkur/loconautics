/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BellAttachType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.bell.AbstractBellBlock;
import com.simibubi.create.content.equipment.bell.HauntedBellBlock;
import com.simibubi.create.content.equipment.bell.PeculiarBellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class PeculiarBellBlock
extends AbstractBellBlock<PeculiarBellBlockEntity> {
    public PeculiarBellBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends PeculiarBellBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.PECULIAR_BELL.get();
    }

    @Override
    public Class<PeculiarBellBlockEntity> getBlockEntityClass() {
        return PeculiarBellBlockEntity.class;
    }

    @Override
    public void playSound(Level world, BlockPos pos) {
        AllSoundEvents.PECULIAR_BELL_USE.playOnServer(world, (Vec3i)pos, 2.0f, 0.94f);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState newState = super.getStateForPlacement(ctx);
        if (newState == null) {
            return null;
        }
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        return this.tryConvert((LevelAccessor)world, pos, newState, world.getBlockState(pos.relative(Direction.DOWN)));
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        BlockState newState = super.updateShape(state, facing, facingState, world, currentPos, facingPos);
        if (facing != Direction.DOWN) {
            return newState;
        }
        return this.tryConvert(world, currentPos, newState, facingState);
    }

    protected BlockState tryConvert(LevelAccessor world, BlockPos pos, BlockState state, BlockState underState) {
        if (!AllBlocks.PECULIAR_BELL.has(state)) {
            return state;
        }
        Block underBlock = underState.getBlock();
        if (!Blocks.SOUL_FIRE.equals(underBlock) && !Blocks.SOUL_CAMPFIRE.equals(underBlock)) {
            return state;
        }
        if (world.isClientSide()) {
            this.spawnConversionParticles(world, pos);
        } else if (world instanceof Level) {
            AllSoundEvents.HAUNTED_BELL_CONVERT.playOnServer((Level)world, (Vec3i)pos);
        }
        return (BlockState)((BlockState)((BlockState)AllBlocks.HAUNTED_BELL.getDefaultState().setValue((Property)HauntedBellBlock.FACING, (Comparable)((Direction)state.getValue((Property)FACING)))).setValue((Property)HauntedBellBlock.ATTACHMENT, (Comparable)((BellAttachType)state.getValue((Property)ATTACHMENT)))).setValue((Property)HauntedBellBlock.POWERED, (Comparable)((Boolean)state.getValue((Property)POWERED)));
    }

    public void spawnConversionParticles(LevelAccessor world, BlockPos blockPos) {
        RandomSource random = world.getRandom();
        int num = random.nextInt(10) + 15;
        for (int i = 0; i < num; ++i) {
            float pitch = random.nextFloat() * 120.0f - 90.0f;
            float yaw = random.nextFloat() * 360.0f;
            Vec3 vel = Vec3.directionFromRotation((float)pitch, (float)yaw).scale(random.nextDouble() * 0.1 + 0.1);
            Vec3 pos = Vec3.atCenterOf((Vec3i)blockPos);
            world.addParticle((ParticleOptions)ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
        }
    }
}
