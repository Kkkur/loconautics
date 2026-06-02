/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.waterwheel;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.equipment.goggles.IProxyHoveringInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlock;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaterWheelStructuralBlock
extends DirectionalBlock
implements IWrenchable,
IProxyHoveringInformation {
    public static final MapCodec<WaterWheelStructuralBlock> CODEC = WaterWheelStructuralBlock.simpleCodec(WaterWheelStructuralBlock::new);

    public WaterWheelStructuralBlock(BlockBehaviour.Properties p_52591_) {
        super(p_52591_);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{FACING}));
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.BLOCK;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return AllBlocks.LARGE_WATER_WHEEL.asStack();
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        if (this.stillValid((BlockGetter)level, clickedPos, state, false)) {
            BlockPos masterPos = WaterWheelStructuralBlock.getMaster((BlockGetter)level, clickedPos, state);
            context = new UseOnContext(level, context.getPlayer(), context.getHand(), context.getItemInHand(), new BlockHitResult(context.getClickLocation(), context.getClickedFace(), masterPos, context.isInside()));
            state = level.getBlockState(masterPos);
        }
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!this.stillValid((BlockGetter)level, pos, state, false)) {
            return ItemInteractionResult.FAIL;
        }
        BlockEntity blockEntity = level.getBlockEntity(WaterWheelStructuralBlock.getMaster((BlockGetter)level, pos, state));
        if (!(blockEntity instanceof WaterWheelBlockEntity)) {
            return ItemInteractionResult.FAIL;
        }
        WaterWheelBlockEntity wwt = (WaterWheelBlockEntity)blockEntity;
        return wwt.applyMaterialIfValid(stack);
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (this.stillValid((BlockGetter)pLevel, pPos, pState, false)) {
            pLevel.destroyBlock(WaterWheelStructuralBlock.getMaster((BlockGetter)pLevel, pPos, pState), true);
        }
    }

    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (this.stillValid((BlockGetter)pLevel, pPos, pState, false)) {
            BlockPos masterPos = WaterWheelStructuralBlock.getMaster((BlockGetter)pLevel, pPos, pState);
            pLevel.destroyBlockProgress(masterPos.hashCode(), masterPos, -1);
            if (!pLevel.isClientSide() && pPlayer.isCreative()) {
                pLevel.destroyBlock(masterPos, false);
            }
        }
        return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        Level level;
        if (this.stillValid((BlockGetter)pLevel, pCurrentPos, pState, false)) {
            BlockPos masterPos = WaterWheelStructuralBlock.getMaster((BlockGetter)pLevel, pCurrentPos, pState);
            if (!pLevel.getBlockTicks().hasScheduledTick(masterPos, (Object)((Block)AllBlocks.LARGE_WATER_WHEEL.get()))) {
                pLevel.scheduleTick(masterPos, (Block)AllBlocks.LARGE_WATER_WHEEL.get(), 1);
            }
            return pState;
        }
        if (!(pLevel instanceof Level) || (level = (Level)pLevel).isClientSide()) {
            return pState;
        }
        if (!level.getBlockTicks().hasScheduledTick(pCurrentPos, (Object)this)) {
            level.scheduleTick(pCurrentPos, (Block)this, 1);
        }
        return pState;
    }

    public static BlockPos getMaster(BlockGetter level, BlockPos pos, BlockState state) {
        Direction direction = (Direction)state.getValue((Property)FACING);
        BlockPos targetedPos = pos.relative(direction);
        BlockState targetedState = level.getBlockState(targetedPos);
        if (targetedState.is((Block)AllBlocks.WATER_WHEEL_STRUCTURAL.get())) {
            return WaterWheelStructuralBlock.getMaster(level, targetedPos, targetedState);
        }
        return targetedPos;
    }

    public boolean stillValid(BlockGetter level, BlockPos pos, BlockState state, boolean directlyAdjacent) {
        if (!state.is((Block)this)) {
            return false;
        }
        Direction direction = (Direction)state.getValue((Property)FACING);
        BlockPos targetedPos = pos.relative(direction);
        BlockState targetedState = level.getBlockState(targetedPos);
        if (!directlyAdjacent && this.stillValid(level, targetedPos, targetedState, true)) {
            return true;
        }
        return targetedState.getBlock() instanceof LargeWaterWheelBlock && targetedState.getValue((Property)LargeWaterWheelBlock.AXIS) != direction.getAxis();
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!this.stillValid((BlockGetter)pLevel, pPos, pState, false)) {
            pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
        }
    }

    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public BlockPos getInformationSource(Level level, BlockPos pos, BlockState state) {
        return this.stillValid((BlockGetter)level, pos, state, false) ? WaterWheelStructuralBlock.getMaster((BlockGetter)level, pos, state) : pos;
    }

    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return false;
    }

    @NotNull
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    public static class RenderProperties
    implements IClientBlockExtensions,
    MultiPosDestructionHandler {
        public boolean addDestroyEffects(BlockState state, Level Level2, BlockPos pos, ParticleEngine manager) {
            return true;
        }

        public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
            if (target instanceof BlockHitResult) {
                BlockHitResult bhr = (BlockHitResult)target;
                BlockPos targetPos = bhr.getBlockPos();
                WaterWheelStructuralBlock waterWheelStructuralBlock = (WaterWheelStructuralBlock)AllBlocks.WATER_WHEEL_STRUCTURAL.get();
                if (waterWheelStructuralBlock.stillValid((BlockGetter)level, targetPos, state, false)) {
                    manager.crack(WaterWheelStructuralBlock.getMaster((BlockGetter)level, targetPos, state), bhr.getDirection());
                }
                return true;
            }
            return super.addHitEffects(state, level, target, manager);
        }

        @Override
        @Nullable
        public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
            WaterWheelStructuralBlock waterWheelStructuralBlock = (WaterWheelStructuralBlock)AllBlocks.WATER_WHEEL_STRUCTURAL.get();
            if (!waterWheelStructuralBlock.stillValid((BlockGetter)level, pos, blockState, false)) {
                return null;
            }
            HashSet<BlockPos> set = new HashSet<BlockPos>();
            set.add(WaterWheelStructuralBlock.getMaster((BlockGetter)level, pos, blockState));
            return set;
        }
    }
}
