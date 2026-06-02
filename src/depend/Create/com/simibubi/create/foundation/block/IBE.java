/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.block;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IBE<T extends BlockEntity>
extends EntityBlock {
    public Class<T> getBlockEntityClass();

    public BlockEntityType<? extends T> getBlockEntityType();

    default public void withBlockEntityDo(BlockGetter world, BlockPos pos, Consumer<T> action) {
        this.getBlockEntityOptional(world, pos).ifPresent(action);
    }

    default public InteractionResult onBlockEntityUse(BlockGetter world, BlockPos pos, Function<T, InteractionResult> action) {
        return this.getBlockEntityOptional(world, pos).map(action).orElse(InteractionResult.PASS);
    }

    default public ItemInteractionResult onBlockEntityUseItemOn(BlockGetter world, BlockPos pos, Function<T, ItemInteractionResult> action) {
        return this.getBlockEntityOptional(world, pos).map(action).orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    public static void onRemove(BlockState blockState, Level level, BlockPos pos, BlockState newBlockState) {
        if (!blockState.hasBlockEntity()) {
            return;
        }
        if (blockState.is(newBlockState.getBlock()) && newBlockState.hasBlockEntity()) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SmartBlockEntity) {
            SmartBlockEntity sbe = (SmartBlockEntity)blockEntity;
            sbe.destroy();
        }
        level.removeBlockEntity(pos);
    }

    default public Optional<T> getBlockEntityOptional(BlockGetter world, BlockPos pos) {
        return Optional.ofNullable(this.getBlockEntity(world, pos));
    }

    default public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return this.getBlockEntityType().create(p_153215_, p_153216_);
    }

    default public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        if (SmartBlockEntity.class.isAssignableFrom(this.getBlockEntityClass())) {
            return new SmartBlockEntityTicker();
        }
        return null;
    }

    @Nullable
    default public T getBlockEntity(BlockGetter worldIn, BlockPos pos) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        Class<T> expectedClass = this.getBlockEntityClass();
        if (blockEntity == null) {
            return null;
        }
        if (!expectedClass.isInstance(blockEntity)) {
            return null;
        }
        return (T)blockEntity;
    }
}
