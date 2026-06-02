/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ArmBlock
extends KineticBlock
implements IBE<ArmBlockEntity>,
ICogWheel {
    public static final BooleanProperty CEILING = BooleanProperty.create((String)"ceiling");

    public ArmBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)CEILING, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_.add(new Property[]{CEILING}));
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return (BlockState)this.defaultBlockState().setValue((Property)CEILING, (Comparable)Boolean.valueOf(ctx.getClickedFace() == Direction.DOWN));
    }

    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return (Boolean)state.getValue((Property)CEILING) != false ? AllShapes.MECHANICAL_ARM_CEILING : AllShapes.MECHANICAL_ARM;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        this.withBlockEntityDo((BlockGetter)world, pos, ArmBlockEntity::redstoneUpdate);
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        this.withBlockEntityDo((BlockGetter)world, pos, ArmBlockEntity::redstoneUpdate);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<ArmBlockEntity> getBlockEntityClass() {
        return ArmBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ArmBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.MECHANICAL_ARM.get();
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemInteractionResult gogglesResult;
        if (AllItems.GOGGLES.isIn(stack) && (gogglesResult = this.onBlockEntityUseItemOn((BlockGetter)level, pos, ate -> {
            if (ate.goggles) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            ate.goggles = true;
            ate.notifyUpdate();
            return ItemInteractionResult.SUCCESS;
        })).consumesAction()) {
            return gogglesResult;
        }
        MutableBoolean success = new MutableBoolean(false);
        this.withBlockEntityDo((BlockGetter)level, pos, be -> {
            if (be.heldItem.isEmpty()) {
                return;
            }
            success.setTrue();
            if (level.isClientSide) {
                return;
            }
            player.getInventory().placeItemBackInInventory(be.heldItem);
            be.heldItem = ItemStack.EMPTY;
            be.phase = ArmBlockEntity.Phase.SEARCH_INPUTS;
            be.setChanged();
            be.sendData();
        });
        return success.booleanValue() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
