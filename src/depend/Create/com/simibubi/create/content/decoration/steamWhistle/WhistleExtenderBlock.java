/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.decoration.steamWhistle;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WhistleExtenderBlock
extends Block
implements IWrenchable {
    public static final EnumProperty<WhistleExtenderShape> SHAPE = EnumProperty.create((String)"shape", WhistleExtenderShape.class);
    public static final EnumProperty<WhistleBlock.WhistleSize> SIZE = WhistleBlock.SIZE;

    public WhistleExtenderBlock(BlockBehaviour.Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(SHAPE, (Comparable)((Object)WhistleExtenderShape.SINGLE))).setValue(SIZE, (Comparable)((Object)WhistleBlock.WhistleSize.MEDIUM)));
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (context.getClickLocation().y < (double)((float)context.getClickedPos().getY() + 0.5f) || state.getValue(SHAPE) == WhistleExtenderShape.SINGLE) {
            return IWrenchable.super.onSneakWrenched(state, context);
        }
        if (!(world instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }
        world.setBlock(pos, (BlockState)state.setValue(SHAPE, (Comparable)((Object)WhistleExtenderShape.SINGLE)), 3);
        IWrenchable.playRemoveSound(world, pos);
        return InteractionResult.SUCCESS;
    }

    protected UseOnContext relocateContext(UseOnContext context, BlockPos target) {
        return new UseOnContext(context.getPlayer(), context.getHand(), new BlockHitResult(context.getClickLocation(), context.getClickedFace(), target, context.isInside()));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player == null || !AllBlocks.STEAM_WHISTLE.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        BlockPos findRoot = WhistleExtenderBlock.findRoot((LevelAccessor)level, pos);
        BlockState blockState = level.getBlockState(findRoot);
        Block block = blockState.getBlock();
        if (block instanceof WhistleBlock) {
            WhistleBlock whistle = (WhistleBlock)block;
            return whistle.useItemOn(stack, blockState, level, findRoot, player, hand, new BlockHitResult(hitResult.getLocation(), hitResult.getDirection(), findRoot, hitResult.isInside()));
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        BlockPos findRoot;
        Level level = context.getLevel();
        BlockState blockState = level.getBlockState(findRoot = WhistleExtenderBlock.findRoot((LevelAccessor)level, context.getClickedPos()));
        Block block = blockState.getBlock();
        if (block instanceof WhistleBlock) {
            WhistleBlock whistle = (WhistleBlock)block;
            return whistle.onWrenched(blockState, this.relocateContext(context, findRoot));
        }
        return IWrenchable.super.onWrenched(state, context);
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return AllBlocks.STEAM_WHISTLE.asStack();
    }

    public static BlockPos findRoot(LevelAccessor pLevel, BlockPos pPos) {
        BlockState blockState;
        BlockPos currentPos = pPos.below();
        while (AllBlocks.STEAM_WHISTLE_EXTENSION.has(blockState = pLevel.getBlockState(currentPos))) {
            currentPos = currentPos.below();
        }
        return currentPos;
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState below = pLevel.getBlockState(pPos.below());
        return below.is((Block)this) && below.getValue(SHAPE) != WhistleExtenderShape.SINGLE || AllBlocks.STEAM_WHISTLE.has(below);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pFacing.getAxis() != Direction.Axis.Y) {
            return pState;
        }
        if (pFacing == Direction.UP) {
            boolean connected = pState.getValue(SHAPE) == WhistleExtenderShape.DOUBLE_CONNECTED;
            boolean shouldConnect = pLevel.getBlockState(pCurrentPos.above()).is((Block)this);
            if (!connected && shouldConnect) {
                return (BlockState)pState.setValue(SHAPE, (Comparable)((Object)WhistleExtenderShape.DOUBLE_CONNECTED));
            }
            if (connected && !shouldConnect) {
                return (BlockState)pState.setValue(SHAPE, (Comparable)((Object)WhistleExtenderShape.DOUBLE));
            }
            return pState;
        }
        return !pState.canSurvive((LevelReader)pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : (BlockState)pState.setValue(SIZE, (Comparable)((Object)((WhistleBlock.WhistleSize)((Object)pLevel.getBlockState(pCurrentPos.below()).getValue(SIZE)))));
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (pOldState.getBlock() != this || pOldState.getValue(SHAPE) != pState.getValue(SHAPE)) {
            WhistleBlock.queuePitchUpdate((LevelAccessor)pLevel, WhistleExtenderBlock.findRoot((LevelAccessor)pLevel, pPos));
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pNewState.getBlock() != this) {
            WhistleBlock.queuePitchUpdate((LevelAccessor)pLevel, WhistleExtenderBlock.findRoot((LevelAccessor)pLevel, pPos));
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{SHAPE, SIZE}));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WhistleBlock.WhistleSize size = (WhistleBlock.WhistleSize)((Object)pState.getValue(SIZE));
        switch (((WhistleExtenderShape)((Object)pState.getValue(SHAPE))).ordinal()) {
            case 1: {
                return size == WhistleBlock.WhistleSize.LARGE ? AllShapes.WHISTLE_EXTENDER_LARGE_DOUBLE : (size == WhistleBlock.WhistleSize.MEDIUM ? AllShapes.WHISTLE_EXTENDER_MEDIUM_DOUBLE : AllShapes.WHISTLE_EXTENDER_SMALL_DOUBLE);
            }
            case 2: {
                return size == WhistleBlock.WhistleSize.LARGE ? AllShapes.WHISTLE_EXTENDER_LARGE_DOUBLE_CONNECTED : (size == WhistleBlock.WhistleSize.MEDIUM ? AllShapes.WHISTLE_EXTENDER_MEDIUM_DOUBLE_CONNECTED : AllShapes.WHISTLE_EXTENDER_SMALL_DOUBLE_CONNECTED);
            }
        }
        return size == WhistleBlock.WhistleSize.LARGE ? AllShapes.WHISTLE_EXTENDER_LARGE : (size == WhistleBlock.WhistleSize.MEDIUM ? AllShapes.WHISTLE_EXTENDER_MEDIUM : AllShapes.WHISTLE_EXTENDER_SMALL);
    }

    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        return AllBlocks.STEAM_WHISTLE.has(neighborState) && dir == Direction.DOWN;
    }

    public static enum WhistleExtenderShape implements StringRepresentable
    {
        SINGLE,
        DOUBLE,
        DOUBLE_CONNECTED;


        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
