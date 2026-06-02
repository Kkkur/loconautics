/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.logistics.funnel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.funnel.AbstractHorizontalFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeltFunnelBlock
extends AbstractHorizontalFunnelBlock
implements SpecialBlockItemRequirement {
    private BlockEntry<? extends FunnelBlock> parent;
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create((String)"shape", Shape.class);

    public BeltFunnelBlock(BlockEntry<? extends FunnelBlock> parent, BlockBehaviour.Properties p_i48377_1_) {
        super(p_i48377_1_);
        this.parent = parent;
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(SHAPE, (Comparable)((Object)Shape.RETRACTED)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)p_206840_1_.add(new Property[]{SHAPE}));
    }

    public boolean isOfSameType(FunnelBlock otherFunnel) {
        return this.parent.get() == otherFunnel;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        FunnelBlock fb;
        Block block = newState.getBlock();
        if (block instanceof FunnelBlock && this.isOfSameType(fb = (FunnelBlock)block)) {
            return;
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return ((Shape)((Object)state.getValue(BeltFunnelBlock.SHAPE))).shaper.get((Direction)state.getValue((Property)HORIZONTAL_FACING));
    }

    public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
        if (p_220071_4_ instanceof EntityCollisionContext && ((EntityCollisionContext)p_220071_4_).getEntity() instanceof ItemEntity && (p_220071_1_.getValue(SHAPE) == Shape.PULLING || p_220071_1_.getValue(SHAPE) == Shape.PUSHING)) {
            return AllShapes.FUNNEL_COLLISION.get(this.getFacing(p_220071_1_));
        }
        return this.getShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState stateForPlacement = super.getStateForPlacement(ctx);
        BlockPos pos = ctx.getClickedPos();
        Level world = ctx.getLevel();
        Direction facing = ctx.getClickedFace().getAxis().isHorizontal() ? ctx.getClickedFace() : ctx.getHorizontalDirection();
        BlockState state = (BlockState)stateForPlacement.setValue((Property)HORIZONTAL_FACING, (Comparable)facing);
        boolean sneaking = ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown();
        return (BlockState)state.setValue(SHAPE, (Comparable)((Object)BeltFunnelBlock.getShapeForPosition((BlockGetter)world, pos, facing, !sneaking)));
    }

    public static Shape getShapeForPosition(BlockGetter world, BlockPos pos, Direction facing, boolean extracting) {
        Shape perpendicularState;
        BlockPos posBelow = pos.below();
        BlockState stateBelow = world.getBlockState(posBelow);
        Shape shape = perpendicularState = extracting ? Shape.PUSHING : Shape.PULLING;
        if (!AllBlocks.BELT.has(stateBelow)) {
            return perpendicularState;
        }
        Direction movementFacing = (Direction)stateBelow.getValue(BeltBlock.HORIZONTAL_FACING);
        return movementFacing.getAxis() != facing.getAxis() ? perpendicularState : Shape.RETRACTED;
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return this.parent.asStack();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor world, BlockPos pos, BlockPos p_196271_6_) {
        Shape currentShape;
        this.updateWater(world, state, pos);
        if (!BeltFunnelBlock.isOnValidBelt(state, (LevelReader)world, pos)) {
            BlockState parentState = ProperWaterloggedBlock.withWater(world, this.parent.getDefaultState(), pos);
            if (state.getOptionalValue((Property)POWERED).orElse(false).booleanValue()) {
                parentState = (BlockState)parentState.setValue((Property)POWERED, (Comparable)Boolean.valueOf(true));
            }
            if (state.getValue(SHAPE) == Shape.PUSHING) {
                parentState = (BlockState)parentState.setValue((Property)FunnelBlock.EXTRACTING, (Comparable)Boolean.valueOf(true));
            }
            return (BlockState)parentState.setValue((Property)FunnelBlock.FACING, (Comparable)((Direction)state.getValue((Property)HORIZONTAL_FACING)));
        }
        Shape updatedShape = BeltFunnelBlock.getShapeForPosition((BlockGetter)world, pos, (Direction)state.getValue((Property)HORIZONTAL_FACING), state.getValue(SHAPE) == Shape.PUSHING);
        if (updatedShape == (currentShape = (Shape)((Object)state.getValue(SHAPE)))) {
            return state;
        }
        if (updatedShape == Shape.PUSHING && currentShape == Shape.PULLING) {
            return state;
        }
        if (updatedShape == Shape.RETRACTED && currentShape == Shape.EXTENDED) {
            return state;
        }
        return (BlockState)state.setValue(SHAPE, (Comparable)((Object)updatedShape));
    }

    public static boolean isOnValidBelt(BlockState state, LevelReader world, BlockPos pos) {
        BlockState stateBelow = world.getBlockState(pos.below());
        if (stateBelow.getBlock() instanceof BeltBlock) {
            return BeltBlock.canTransportObjects(stateBelow);
        }
        DirectBeltInputBehaviour directBeltInputBehaviour = BlockEntityBehaviour.get((BlockGetter)world, pos.below(), DirectBeltInputBehaviour.TYPE);
        if (directBeltInputBehaviour == null) {
            return false;
        }
        return directBeltInputBehaviour.canSupportBeltFunnels();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Shape shape;
        Level world = context.getLevel();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Shape newShape = shape = (Shape)((Object)state.getValue(SHAPE));
        if (shape == Shape.PULLING) {
            newShape = Shape.PUSHING;
        } else if (shape == Shape.PUSHING) {
            newShape = Shape.PULLING;
        } else if (shape == Shape.EXTENDED) {
            newShape = Shape.RETRACTED;
        } else if (shape == Shape.RETRACTED) {
            BlockState belt = world.getBlockState(context.getClickedPos().below());
            newShape = belt.getBlock() instanceof BeltBlock && belt.getValue(BeltBlock.SLOPE) != BeltSlope.HORIZONTAL ? Shape.RETRACTED : Shape.EXTENDED;
        }
        if (newShape == shape) {
            return InteractionResult.SUCCESS;
        }
        world.setBlockAndUpdate(context.getClickedPos(), (BlockState)state.setValue(SHAPE, (Comparable)((Object)newShape)));
        if (newShape == Shape.EXTENDED) {
            Direction facing = (Direction)state.getValue((Property)HORIZONTAL_FACING);
            BlockState opposite = world.getBlockState(context.getClickedPos().relative(facing));
            if (opposite.getBlock() instanceof BeltFunnelBlock && opposite.getValue(SHAPE) == Shape.EXTENDED && opposite.getValue((Property)HORIZONTAL_FACING) == facing.getOpposite()) {
                AllAdvancements.FUNNEL_KISS.awardTo(context.getPlayer());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, this.parent.asItem());
    }

    public static enum Shape implements StringRepresentable
    {
        RETRACTED(AllShapes.BELT_FUNNEL_RETRACTED),
        EXTENDED(AllShapes.BELT_FUNNEL_EXTENDED),
        PUSHING(AllShapes.BELT_FUNNEL_PERPENDICULAR),
        PULLING(AllShapes.BELT_FUNNEL_PERPENDICULAR);

        VoxelShaper shaper;

        private Shape(VoxelShaper shaper) {
            this.shaper = shaper;
        }

        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
