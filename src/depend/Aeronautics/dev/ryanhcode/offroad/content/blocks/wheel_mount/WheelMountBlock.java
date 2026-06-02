/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement
 *  com.simibubi.create.content.kinetics.base.HorizontalKineticBlock
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$ItemUseType
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$StackRequirement
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$StrictNbtStackRequirement
 *  com.simibubi.create.foundation.block.IBE
 *  dev.simulated_team.simulated.multiloader.inventory.ContainerSlot
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.offroad.content.blocks.wheel_mount;

import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity;
import dev.ryanhcode.offroad.content.components.TireLike;
import dev.ryanhcode.offroad.index.OffroadBlockEntityTypes;
import dev.ryanhcode.offroad.index.OffroadBlocks;
import dev.ryanhcode.offroad.index.OffroadDataComponents;
import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WheelMountBlock
extends HorizontalKineticBlock
implements IBE<WheelMountBlockEntity>,
SpecialBlockItemRequirement {
    public WheelMountBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            WheelMountBlockEntity be = (WheelMountBlockEntity)level.getBlockEntity(pos);
            if (be != null && !be.getHeldItem().isEmpty()) {
                Direction facing = (Direction)state.getValue(HORIZONTAL_FACING);
                BlockPos dropPos = pos;
                if (facing != null) {
                    dropPos = dropPos.relative(facing);
                }
                Containers.dropItemStack((Level)level, (double)dropPos.getX(), (double)dropPos.getY(), (double)dropPos.getZ(), (ItemStack)be.getHeldItem());
            }
            level.removeBlockEntity(pos);
        }
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == ((Direction)state.getValue(HORIZONTAL_FACING)).getOpposite();
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getAxis();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean crouching;
        Direction preferred = this.getPreferredHorizontalFacing(context);
        boolean bl = crouching = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        if (preferred == null || crouching) {
            Direction horizontalDirection = context.getHorizontalDirection();
            return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, (Comparable)(crouching ? horizontalDirection : horizontalDirection.getOpposite()));
        }
        return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, (Comparable)preferred.getOpposite());
    }

    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        Direction hitDirection = blockHitResult.getDirection();
        if (!hitDirection.equals((Object)blockState.getValue(HORIZONTAL_FACING)) && hitDirection != Direction.DOWN) {
            return super.useItemOn(heldItem, blockState, level, blockPos, player, interactionHand, blockHitResult);
        }
        if (level.isClientSide()) {
            return this.onBlockEntityUseItemOn((BlockGetter)level, blockPos, mount -> {
                ItemStack potentialTire = mount.getHeldItem();
                if (heldItem.isEmpty() && potentialTire.has(OffroadDataComponents.TIRE) || heldItem.has(OffroadDataComponents.TIRE) && potentialTire.has(OffroadDataComponents.TIRE) || heldItem.has(OffroadDataComponents.TIRE) && potentialTire.isEmpty()) {
                    return ItemInteractionResult.SUCCESS;
                }
                return super.useItemOn(heldItem, blockState, level, blockPos, player, interactionHand, blockHitResult);
            });
        }
        if (this.switchStacks(level, blockPos, player, interactionHand)) {
            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(heldItem, blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    private boolean switchStacks(Level level, BlockPos pos, Player player, InteractionHand hand) {
        boolean[] passed = new boolean[]{false};
        ItemStack heldItem = player.getItemInHand(hand);
        TireLike tireLike = (TireLike)heldItem.get(OffroadDataComponents.TIRE);
        if (heldItem.isEmpty() || tireLike != null) {
            this.withBlockEntityDo((BlockGetter)level, pos, mount -> {
                ContainerSlot slot = mount.getInventory().slot;
                ItemStack save = slot.getStack().copy();
                ItemStack oldSlotItem = save.copy();
                slot.setStack(heldItem.copyWithCount(1));
                if (!player.hasInfiniteMaterials()) {
                    heldItem.shrink(1);
                }
                player.getInventory().placeItemBackInInventory(save);
                ItemStack newSlotItem = slot.getStack();
                mount.setChanged();
                mount.sendData();
                passed[0] = true;
                float pitch = 0.8f + level.random.nextFloat() * 0.4f;
                float volume = 0.75f;
                if (oldSlotItem.isEmpty() && !newSlotItem.isEmpty()) {
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 0.75f, pitch);
                } else if (!oldSlotItem.isEmpty() && newSlotItem.isEmpty()) {
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.75f, pitch);
                } else if (!oldSlotItem.isEmpty()) {
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 0.75f, pitch);
                }
            });
        }
        return passed[0];
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext ctx) {
        return Shapes.block();
    }

    public Class<WheelMountBlockEntity> getBlockEntityClass() {
        return WheelMountBlockEntity.class;
    }

    public BlockEntityType<? extends WheelMountBlockEntity> getBlockEntityType() {
        return (BlockEntityType)OffroadBlockEntityTypes.WHEEL_MOUNT.get();
    }

    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity blockEntity) {
        WheelMountBlockEntity wmbe;
        ItemStack heldItem;
        ItemStack mountStack = OffroadBlocks.WHEEL_MOUNT.asStack();
        if (blockEntity instanceof WheelMountBlockEntity && !(heldItem = (wmbe = (WheelMountBlockEntity)blockEntity).getHeldItem()).isEmpty()) {
            return new ItemRequirement(List.of(new ItemRequirement.StackRequirement(mountStack, ItemRequirement.ItemUseType.CONSUME), new ItemRequirement.StrictNbtStackRequirement(heldItem, ItemRequirement.ItemUseType.CONSUME)));
        }
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, mountStack);
    }
}
