/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.foundation.blockEntity.SyncedBlockEntity
 *  dev.simulated_team.simulated.multiloader.inventory.ContainerSlot
 *  dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper
 *  dev.simulated_team.simulated.util.DirectionalAxisShaper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon.MountedPotatoCannonBlockEntity;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import dev.eriksonn.aeronautics.index.AeroBlockShapes;
import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import dev.simulated_team.simulated.util.DirectionalAxisShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MountedPotatoCannonBlock
extends DirectionalAxisKineticBlock
implements IBE<MountedPotatoCannonBlockEntity> {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty BLOCKED = BooleanProperty.create((String)"blocked");
    DirectionalAxisShaper MOUNTED_POTATO_CANNON = DirectionalAxisShaper.make((VoxelShape)AeroBlockShapes.MOUNTED_POTATO_CANNON);
    DirectionalAxisShaper MOUNTED_POTATO_CANNON_BLOCKED = DirectionalAxisShaper.make((VoxelShape)AeroBlockShapes.MOUNTED_POTATO_CANNON_BLOCKED);

    public MountedPotatoCannonBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)BLOCKED, (Comparable)Boolean.valueOf(false)));
    }

    public Class<MountedPotatoCannonBlockEntity> getBlockEntityClass() {
        return MountedPotatoCannonBlockEntity.class;
    }

    public BlockEntityType<? extends MountedPotatoCannonBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AeroBlockEntityTypes.MOUNTED_POTATO_CANNON.get();
    }

    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof MountedPotatoCannonBlockEntity) {
            long inserted;
            MountedPotatoCannonBlockEntity be = (MountedPotatoCannonBlockEntity)blockEntity;
            ContainerSlot slot = be.getInventory().slot;
            if (heldItem.isEmpty() && slot.isEmpty()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            ItemInfoWrapper info = ItemInfoWrapper.generateFromStack((ItemStack)heldItem);
            if ((slot.isEmpty() || slot.getType() == heldItem.getItem()) && (inserted = (long)slot.insertStack(info, Math.min(heldItem.getCount(), 16), true)) > 0L) {
                if (!level.isClientSide) {
                    slot.insertStack(info, Math.min(heldItem.getCount(), 16), false);
                }
                if (!player.isCreative()) {
                    heldItem.shrink((int)inserted);
                }
                return ItemInteractionResult.sidedSuccess((boolean)level.isClientSide());
            }
            if (slot.getType() != heldItem.getItem() && slot.canInsert(info)) {
                ItemStack extracted = slot.getStack().copy();
                player.getInventory().placeItemBackInInventory(extracted);
                slot.setStack(ItemStack.EMPTY);
                if (!level.isClientSide) {
                    long inserted2 = slot.insertStack(info, Math.min(heldItem.getCount(), 16), false);
                    heldItem.shrink((int)inserted2);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(heldItem, blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        boolean previouslyPowered = (Boolean)state.getValue((Property)POWERED);
        if (previouslyPowered != level.hasNeighborSignal(pos)) {
            level.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 2);
        }
        this.withBlockEntityDo((BlockGetter)level, pos, SyncedBlockEntity::sendData);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED, BLOCKED});
        super.createBlockStateDefinition(builder);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)super.getStateForPlacement(context).setValue((Property)POWERED, (Comparable)Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return (Boolean)state.getValue((Property)BLOCKED) != false ? this.MOUNTED_POTATO_CANNON_BLOCKED.get((Direction)state.getValue((Property)FACING), ((Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE)).booleanValue()) : this.MOUNTED_POTATO_CANNON.get((Direction)state.getValue((Property)FACING), ((Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE)).booleanValue());
    }
}
