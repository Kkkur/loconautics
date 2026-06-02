/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.api.contraption.BlockMovementChecks$CheckResult
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.impl.contraption.BlockMovementChecksImpl
 *  dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener
 *  dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.content.blocks.rope.rope_connector;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.impl.contraption.BlockMovementChecksImpl;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import dev.simulated_team.simulated.content.blocks.rope.RopeHolderBlock;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorBlockEntity;
import dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.util.DirectionalAxisShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RopeConnectorBlock
extends AbstractDirectionalAxisBlock
implements IBE<RopeConnectorBlockEntity>,
RopeHolderBlock<RopeConnectorBlockEntity>,
BlockSubLevelAssemblyListener,
BlockSubLevelCollisionShape {
    public static final MapCodec<RopeConnectorBlock> CODEC = RopeConnectorBlock.simpleCodec(RopeConnectorBlock::new);
    private static final DirectionalAxisShaper SHAPE = DirectionalAxisShaper.make(SimBlockShapes.ROPE_CONNECTOR);
    private static final DirectionalAxisShaper PHYSICS_COLLIDER = DirectionalAxisShaper.make(SimBlockShapes.ROPE_CONNECTOR_COLLIDER);

    public RopeConnectorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
        return PHYSICS_COLLIDER.get((Direction)state.getValue((Property)FACING), (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE));
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if (worldIn.isClientSide) {
            return;
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove((BlockState)pState, (Level)pLevel, (BlockPos)pPos, (BlockState)pNewState);
    }

    public Class<RopeConnectorBlockEntity> getBlockEntityClass() {
        return RopeConnectorBlockEntity.class;
    }

    public BlockEntityType<? extends RopeConnectorBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.ROPE_CONNECTOR.get();
    }

    @Override
    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        return context.getClickedFace();
    }

    @Override
    protected boolean getAxisAlignmentForPlacement(BlockPlaceContext context) {
        return context.getHorizontalDirection().getAxis() != Direction.Axis.X;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE.get((Direction)state.getValue((Property)FACING), (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide() && stack.is(SimTags.Items.DESTROYS_ROPE)) {
            return RopeHolderBlock.shearRope(this, level, pos, (ServerPlayer)player);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    static {
        BlockMovementChecksImpl.registerAttachedCheck((state, world, pos, direction) -> {
            BlockState relativeState = world.getBlockState(pos.relative(direction));
            if (state.getBlock() instanceof RopeConnectorBlock && state.getValue((Property)FACING) == direction.getOpposite()) {
                return BlockMovementChecks.CheckResult.SUCCESS;
            }
            if (relativeState.getBlock() instanceof RopeConnectorBlock && relativeState.getValue((Property)FACING) == direction) {
                return BlockMovementChecks.CheckResult.SUCCESS;
            }
            return BlockMovementChecks.CheckResult.PASS;
        });
    }
}
