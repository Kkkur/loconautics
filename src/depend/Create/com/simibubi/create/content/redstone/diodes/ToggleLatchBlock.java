/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DiodeBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.redstone.diodes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.diodes.AbstractDiodeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class ToggleLatchBlock
extends AbstractDiodeBlock {
    public static BooleanProperty POWERING = BooleanProperty.create((String)"powering");
    public static final MapCodec<ToggleLatchBlock> CODEC = ToggleLatchBlock.simpleCodec(ToggleLatchBlock::new);

    public ToggleLatchBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERING, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    @NotNull
    protected MapCodec<? extends DiodeBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED, POWERING, FACING});
    }

    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue((Property)FACING) == side ? this.getOutputSignal(blockAccess, pos, blockState) : 0;
    }

    protected int getDelay(BlockState state) {
        return 1;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (AllItems.WRENCH.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return this.activated(level, pos, state);
    }

    protected int getOutputSignal(BlockGetter worldIn, BlockPos pos, BlockState state) {
        return (Boolean)state.getValue((Property)POWERING) != false ? 15 : 0;
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        boolean poweredPreviously = (Boolean)state.getValue((Property)POWERED);
        super.tick(state, worldIn, pos, random);
        BlockState newState = worldIn.getBlockState(pos);
        if (((Boolean)newState.getValue((Property)POWERED)).booleanValue() && !poweredPreviously) {
            worldIn.setBlock(pos, (BlockState)newState.cycle((Property)POWERING), 2);
        }
    }

    protected ItemInteractionResult activated(Level worldIn, BlockPos pos, BlockState state) {
        if (!worldIn.isClientSide) {
            float f = (Boolean)state.getValue((Property)POWERING) == false ? 0.6f : 0.5f;
            worldIn.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3f, f);
            worldIn.setBlock(pos, (BlockState)state.cycle((Property)POWERING), 2);
        }
        return ItemInteractionResult.SUCCESS;
    }

    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        if (side == null) {
            return false;
        }
        return side.getAxis() == ((Direction)state.getValue((Property)FACING)).getAxis();
    }
}
