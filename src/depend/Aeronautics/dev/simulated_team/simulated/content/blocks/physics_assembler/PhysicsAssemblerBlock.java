/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer
 *  com.simibubi.create.foundation.block.IBE
 *  dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.physics_assembler;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimClickInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PhysicsAssemblerBlock
extends FaceAttachedHorizontalDirectionalBlock
implements IBE<PhysicsAssemblerBlockEntity>,
IWrenchable,
BlockSubLevelAssemblyListener {
    public static final MapCodec<PhysicsAssemblerBlock> CODEC = PhysicsAssemblerBlock.simpleCodec(PhysicsAssemblerBlock::new);

    public PhysicsAssemblerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return PhysicsAssemblerBlock.canAttach(level, pos, PhysicsAssemblerBlock.getConnectedDirection((BlockState)state).getOpposite());
    }

    public static boolean canAttach(LevelReader reader, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.relative(direction);
        return !reader.getBlockState(blockpos).getBlockSupportShape((BlockGetter)reader, pos).getFaceShape(direction.getOpposite()).isEmpty();
    }

    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public Class<PhysicsAssemblerBlockEntity> getBlockEntityClass() {
        return PhysicsAssemblerBlockEntity.class;
    }

    public BlockEntityType<? extends PhysicsAssemblerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.PHYSICS_ASSEMBLER.get();
    }

    @NotNull
    public VoxelShape getShape(BlockState state, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        return switch ((AttachFace)state.getValue((Property)FACE)) {
            case AttachFace.CEILING -> SimBlockShapes.PHYSICS_ASSEMBLER_CEILING_OUTLINE.get(facing);
            case AttachFace.FLOOR -> SimBlockShapes.PHYSICS_ASSEMBLER_OUTLINE.get(facing);
            default -> SimBlockShapes.PHYSICS_ASSEMBLER_WALL_OUTLINE.get(facing.getOpposite());
        };
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        return switch ((AttachFace)state.getValue((Property)FACE)) {
            case AttachFace.CEILING -> SimBlockShapes.PHYSICS_ASSEMBLER_CEILING_COLLISION.get(facing);
            case AttachFace.FLOOR -> SimBlockShapes.PHYSICS_ASSEMBLER_COLLISION.get(facing);
            default -> SimBlockShapes.PHYSICS_ASSEMBLER_WALL_COLLISION.get(facing.getOpposite());
        };
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player instanceof DeployerFakePlayer) {
            if (!level.isClientSide) {
                this.withBlockEntityDo((BlockGetter)level, pos, PhysicsAssemblerBlockEntity::assembleOrDisassemble);
            }
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide && player.isLocalPlayer()) {
            return this.onBlockEntityUse((BlockGetter)level, pos, be -> {
                SimClickInteractions.PHYSICS_ASSEMBLER_MANAGER.startHold(level, player, pos);
                return InteractionResult.SUCCESS;
            });
        }
        return InteractionResult.CONSUME;
    }

    public static Direction getStickyFacing(BlockState state) {
        return switch ((AttachFace)state.getValue((Property)FACE)) {
            default -> throw new MatchException(null, null);
            case AttachFace.FLOOR -> Direction.DOWN;
            case AttachFace.CEILING -> Direction.UP;
            case AttachFace.WALL -> ((Direction)state.getValue((Property)FACING)).getOpposite();
        };
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{FACING, FACE}));
    }

    public void afterMove(ServerLevel serverLevel, ServerLevel serverLevel1, BlockState blockState, BlockPos blockPos, BlockPos blockPos1) {
        BlockEntity be = serverLevel1.getBlockEntity(blockPos1);
        if (be instanceof PhysicsAssemblerBlockEntity) {
            PhysicsAssemblerBlockEntity pabe = (PhysicsAssemblerBlockEntity)be;
            pabe.setParent((Level)serverLevel1);
        }
    }
}
