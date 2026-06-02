/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 */
package com.simibubi.create.content.kinetics.crafter;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.crafter.ConnectedInputHandler;
import com.simibubi.create.content.kinetics.crafter.CrafterHelper;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class MechanicalCrafterBlock
extends HorizontalKineticBlock
implements IBE<MechanicalCrafterBlockEntity>,
ICogWheel {
    public static final EnumProperty<Pointing> POINTING = EnumProperty.create((String)"pointing", Pointing.class);

    public MechanicalCrafterBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(POINTING, (Comparable)Pointing.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{POINTING}));
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getAxis();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockPos placedOnPos = context.getClickedPos().relative(face.getOpposite());
        BlockState blockState = context.getLevel().getBlockState(placedOnPos);
        if (blockState.getBlock() != this || context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            BlockState stateForPlacement = super.getStateForPlacement(context);
            Direction direction = (Direction)stateForPlacement.getValue(HORIZONTAL_FACING);
            if (direction != face) {
                stateForPlacement = (BlockState)stateForPlacement.setValue(POINTING, (Comparable)MechanicalCrafterBlock.pointingFromFacing(face, direction));
            }
            return stateForPlacement;
        }
        Direction otherFacing = (Direction)blockState.getValue(HORIZONTAL_FACING);
        Pointing pointing = MechanicalCrafterBlock.pointingFromFacing(face, otherFacing);
        return (BlockState)((BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, (Comparable)otherFacing)).setValue(POINTING, (Comparable)pointing);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        MechanicalCrafterBlockEntity crafter;
        if (state.getBlock() == newState.getBlock() && MechanicalCrafterBlock.getTargetDirection(state) != MechanicalCrafterBlock.getTargetDirection(newState) && (crafter = CrafterHelper.getCrafter((BlockAndTintGetter)worldIn, pos)) != null) {
            crafter.blockChanged();
        }
        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            crafter = CrafterHelper.getCrafter((BlockAndTintGetter)worldIn, pos);
            if (crafter != null) {
                if (crafter.covered) {
                    Block.popResource((Level)worldIn, (BlockPos)pos, (ItemStack)AllItems.CRAFTER_SLOT_COVER.asStack());
                }
                if (!isMoving) {
                    crafter.ejectWholeGrid();
                }
            }
            for (Direction direction : Iterate.directions) {
                if (direction.getAxis() == ((Direction)state.getValue(HORIZONTAL_FACING)).getAxis()) continue;
                BlockPos otherPos = pos.relative(direction);
                ConnectedInputHandler.ConnectedInput thisInput = CrafterHelper.getInput((BlockAndTintGetter)worldIn, pos);
                ConnectedInputHandler.ConnectedInput otherInput = CrafterHelper.getInput((BlockAndTintGetter)worldIn, otherPos);
                if (thisInput == null || otherInput == null || !pos.offset((Vec3i)thisInput.data.get(0)).equals((Object)otherPos.offset((Vec3i)otherInput.data.get(0)))) continue;
                ConnectedInputHandler.toggleConnection(worldIn, pos, otherPos);
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public static Pointing pointingFromFacing(Direction pointingFace, Direction blockFacing) {
        Pointing pointing;
        boolean positive = blockFacing.getAxisDirection() == Direction.AxisDirection.POSITIVE;
        Pointing pointing2 = pointing = pointingFace == Direction.DOWN ? Pointing.UP : Pointing.DOWN;
        if (pointingFace == Direction.EAST) {
            Pointing pointing3 = pointing = positive ? Pointing.LEFT : Pointing.RIGHT;
        }
        if (pointingFace == Direction.WEST) {
            Pointing pointing4 = pointing = positive ? Pointing.RIGHT : Pointing.LEFT;
        }
        if (pointingFace == Direction.NORTH) {
            Pointing pointing5 = pointing = positive ? Pointing.LEFT : Pointing.RIGHT;
        }
        if (pointingFace == Direction.SOUTH) {
            pointing = positive ? Pointing.RIGHT : Pointing.LEFT;
        }
        return pointing;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getClickedFace() == state.getValue(HORIZONTAL_FACING)) {
            if (!context.getLevel().isClientSide) {
                KineticBlockEntity.switchToBlockState(context.getLevel(), context.getClickedPos(), (BlockState)state.cycle(POINTING));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MechanicalCrafterBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        MechanicalCrafterBlockEntity crafter = (MechanicalCrafterBlockEntity)blockEntity;
        if (AllBlocks.MECHANICAL_ARM.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        boolean isHand = stack.isEmpty() && hand == InteractionHand.MAIN_HAND;
        boolean wrenched = AllItems.WRENCH.isIn(stack);
        if (hitResult.getDirection() == state.getValue(HORIZONTAL_FACING)) {
            if (crafter.phase != MechanicalCrafterBlockEntity.Phase.IDLE && !wrenched) {
                crafter.ejectWholeGrid();
                return ItemInteractionResult.SUCCESS;
            }
            if (crafter.phase == MechanicalCrafterBlockEntity.Phase.IDLE && !isHand && !wrenched) {
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }
                if (AllItems.CRAFTER_SLOT_COVER.isIn(stack)) {
                    if (crafter.covered) {
                        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    }
                    if (!crafter.inventory.isEmpty()) {
                        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    }
                    crafter.covered = true;
                    crafter.setChanged();
                    crafter.sendData();
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    return ItemInteractionResult.SUCCESS;
                }
                IItemHandler capability = (IItemHandler)level.getCapability(Capabilities.ItemHandler.BLOCK, crafter.getBlockPos(), null);
                if (capability == null) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                ItemStack remainder = ItemHandlerHelper.insertItem((IItemHandler)capability, (ItemStack)stack.copy(), (boolean)false);
                if (remainder.getCount() != stack.getCount()) {
                    player.setItemInHand(hand, remainder);
                }
                return ItemInteractionResult.SUCCESS;
            }
            ItemStack inSlot = crafter.getInventory().getItem(0);
            if (inSlot.isEmpty()) {
                if (crafter.covered && !wrenched) {
                    if (level.isClientSide) {
                        return ItemInteractionResult.SUCCESS;
                    }
                    crafter.covered = false;
                    crafter.setChanged();
                    crafter.sendData();
                    if (!player.isCreative()) {
                        player.getInventory().placeItemBackInInventory(AllItems.CRAFTER_SLOT_COVER.asStack());
                    }
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!isHand && !ItemStack.isSameItemSameComponents((ItemStack)stack, (ItemStack)inSlot)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            player.getInventory().placeItemBackInInventory(inSlot);
            crafter.getInventory().setStackInSlot(0, ItemStack.EMPTY);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        InvManipulationBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)worldIn, pos, InvManipulationBehaviour.TYPE);
        if (behaviour != null) {
            behaviour.onNeighborChanged(fromPos);
        }
    }

    @Override
    public float getParticleTargetRadius() {
        return 0.85f;
    }

    @Override
    public float getParticleInitialRadius() {
        return 0.75f;
    }

    public static Direction getTargetDirection(BlockState state) {
        if (!AllBlocks.MECHANICAL_CRAFTER.has(state)) {
            return Direction.UP;
        }
        Direction facing = (Direction)state.getValue(HORIZONTAL_FACING);
        Pointing point = (Pointing)state.getValue(POINTING);
        Vec3 targetVec = new Vec3(0.0, 1.0, 0.0);
        targetVec = VecHelper.rotate((Vec3)targetVec, (double)(-point.getXRotation()), (Direction.Axis)Direction.Axis.Z);
        targetVec = VecHelper.rotate((Vec3)targetVec, (double)AngleHelper.horizontalAngle((Direction)facing), (Direction.Axis)Direction.Axis.Y);
        return Direction.getNearest((double)targetVec.x, (double)targetVec.y, (double)targetVec.z);
    }

    public static boolean isValidTarget(Level world, BlockPos targetPos, BlockState crafterState) {
        BlockState targetState = world.getBlockState(targetPos);
        if (!world.isLoaded(targetPos)) {
            return false;
        }
        if (!AllBlocks.MECHANICAL_CRAFTER.has(targetState)) {
            return false;
        }
        if (crafterState.getValue(HORIZONTAL_FACING) != targetState.getValue(HORIZONTAL_FACING)) {
            return false;
        }
        return Math.abs(((Pointing)crafterState.getValue(POINTING)).getXRotation() - ((Pointing)targetState.getValue(POINTING)).getXRotation()) != 180;
    }

    @Override
    public Class<MechanicalCrafterBlockEntity> getBlockEntityClass() {
        return MechanicalCrafterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalCrafterBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.MECHANICAL_CRAFTER.get();
    }
}
