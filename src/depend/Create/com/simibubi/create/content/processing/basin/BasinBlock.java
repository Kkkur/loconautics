/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.processing.basin;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BasinBlock
extends Block
implements IBE<BasinBlockEntity>,
IWrenchable {
    public static final DirectionProperty FACING = BlockStateProperties.FACING_HOPPER;

    public BasinBlock(BlockBehaviour.Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)Direction.DOWN));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_.add(new Property[]{FACING}));
    }

    public static boolean isBasin(LevelReader world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof BasinBlockEntity;
    }

    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos.above());
        return !(blockEntity instanceof BasinOperatingBlockEntity);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            this.withBlockEntityDo((BlockGetter)context.getLevel(), context.getClickedPos(), bte -> bte.onWrenched(context.getClickedFace()));
        }
        return InteractionResult.SUCCESS;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return this.onBlockEntityUseItemOn((BlockGetter)level, pos, be -> {
            if (!stack.isEmpty()) {
                FluidStack drained;
                IFluidHandler fluidHandler;
                if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be)) {
                    return ItemInteractionResult.SUCCESS;
                }
                if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be)) {
                    return ItemInteractionResult.SUCCESS;
                }
                if (GenericItemEmptying.canItemBeEmptied(level, stack) || GenericItemFilling.canItemBeFilled(level, stack)) {
                    return ItemInteractionResult.SUCCESS;
                }
                if (stack.getItem().equals(Items.SPONGE) && (fluidHandler = (IFluidHandler)level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null)) != null && !(drained = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE)).isEmpty()) {
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            IItemHandlerModifiable inv = be.itemCapability;
            if (inv == null) {
                inv = new ItemStackHandler(1);
            }
            boolean success = false;
            for (int slot = 0; slot < inv.getSlots(); ++slot) {
                ItemStack stackInSlot = inv.getStackInSlot(slot);
                if (stackInSlot.isEmpty()) continue;
                player.getInventory().placeItemBackInInventory(stackInSlot);
                inv.setStackInSlot(slot, ItemStack.EMPTY);
                success = true;
            }
            if (success) {
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, 1.0f + level.getRandom().nextFloat());
            }
            be.onEmptied();
            return ItemInteractionResult.SUCCESS;
        });
    }

    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        if (!worldIn.getBlockState(entityIn.blockPosition()).is((Block)this)) {
            return;
        }
        if (!(entityIn instanceof ItemEntity)) {
            return;
        }
        ItemEntity itemEntity = (ItemEntity)entityIn;
        if (!entityIn.isAlive()) {
            return;
        }
        this.withBlockEntityDo(worldIn, entityIn.blockPosition(), be -> {
            ItemStack insertItem = ItemHandlerHelper.insertItem((IItemHandler)be.inputInventory, (ItemStack)itemEntity.getItem().copy(), (boolean)false);
            if (insertItem.isEmpty()) {
                itemEntity.discard();
                return;
            }
            itemEntity.setItem(insertItem);
        });
    }

    public VoxelShape getInteractionShape(BlockState p_199600_1_, BlockGetter p_199600_2_, BlockPos p_199600_3_) {
        return AllShapes.BASIN_RAYTRACE_SHAPE;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.BASIN_BLOCK_SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext ctx) {
        if (ctx instanceof EntityCollisionContext && ((EntityCollisionContext)ctx).getEntity() instanceof ItemEntity) {
            return AllShapes.BASIN_COLLISION_SHAPE;
        }
        return this.getShape(state, reader, pos, ctx);
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return this.getBlockEntityOptional((BlockGetter)worldIn, pos).map(BasinBlockEntity::getInputInventory).map(ItemHelper::calcRedstoneFromInventory).orElse(0);
    }

    @Override
    public Class<BasinBlockEntity> getBlockEntityClass() {
        return BasinBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BasinBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.BASIN.get();
    }

    public static boolean canOutputTo(BlockGetter world, BlockPos basinPos, Direction direction) {
        DirectBeltInputBehaviour directBeltInputBehaviour;
        BlockPos neighbour = basinPos.relative(direction);
        BlockPos output = neighbour.below();
        BlockState blockState = world.getBlockState(neighbour);
        if (FunnelBlock.isFunnel(blockState)) {
            if (FunnelBlock.getFunnelFacing(blockState) == direction) {
                return false;
            }
        } else {
            if (!blockState.getCollisionShape(world, neighbour).isEmpty()) {
                return false;
            }
            BlockEntity blockEntity = world.getBlockEntity(output);
            if (blockEntity instanceof BeltBlockEntity) {
                BeltBlockEntity belt = (BeltBlockEntity)blockEntity;
                return belt.getSpeed() == 0.0f || belt.getMovementFacing() != direction.getOpposite();
            }
        }
        if ((directBeltInputBehaviour = BlockEntityBehaviour.get(world, output, DirectBeltInputBehaviour.TYPE)) != null) {
            return directBeltInputBehaviour.canInsertFromSide(direction);
        }
        return false;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
