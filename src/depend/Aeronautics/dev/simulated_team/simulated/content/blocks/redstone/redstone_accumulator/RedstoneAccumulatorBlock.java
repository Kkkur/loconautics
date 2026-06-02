/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.content.redstone.diodes.AbstractDiodeBlock
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DiodeBlock
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
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.redstone.diodes.AbstractDiodeBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.content.blocks.redstone.redstone_accumulator.RedstoneAccumulatorBlockEntity;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.multiloader.CommonRedstoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class RedstoneAccumulatorBlock
extends AbstractDiodeBlock
implements IBE<RedstoneAccumulatorBlockEntity>,
CommonRedstoneBlock {
    public static final MapCodec<RedstoneAccumulatorBlock> CODEC = RedstoneAccumulatorBlock.simpleCodec(RedstoneAccumulatorBlock::new);
    public static BooleanProperty POWERING = BooleanProperty.create((String)"powering");
    public static BooleanProperty SIDE_POWERED = BooleanProperty.create((String)"side_powered");
    public static BooleanProperty INVERTED = BlockStateProperties.INVERTED;

    public RedstoneAccumulatorBlock(BlockBehaviour.Properties builder) {
        super(builder);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)SIDE_POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERING, (Comparable)Boolean.valueOf(false))).setValue((Property)INVERTED, (Comparable)Boolean.valueOf(false)));
    }

    protected MapCodec<? extends DiodeBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, POWERED, SIDE_POWERED, POWERING, INVERTED});
    }

    protected void checkTickOnNeighbor(Level level, BlockPos pos, BlockState state) {
        super.checkTickOnNeighbor(level, pos, state);
        level.setBlock(pos, this.getUpdatedBlockstate(pos, state, level), 2);
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    }

    public Class<RedstoneAccumulatorBlockEntity> getBlockEntityClass() {
        return RedstoneAccumulatorBlockEntity.class;
    }

    public BlockEntityType<? extends RedstoneAccumulatorBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.REDSTONE_ACCUMULATOR.get();
    }

    public BlockState getUpdatedBlockstate(BlockPos pos, BlockState state, Level level) {
        Direction facing = ((Direction)state.getValue((Property)FACING)).getOpposite();
        BlockPos offset = pos.relative(facing.getOpposite());
        BlockPos leftSide = pos.offset(facing.getCounterClockWise().getNormal());
        BlockPos rightSide = pos.offset(facing.getClockWise().getNormal());
        boolean leftSignal = level.getSignal(leftSide, facing.getClockWise().getOpposite()) > 0;
        boolean rightSignal = level.getSignal(rightSide, facing.getCounterClockWise().getOpposite()) > 0;
        boolean backSignal = level.getSignal(offset, facing.getOpposite()) > 0;
        boolean sideSignal = leftSignal || rightSignal;
        boolean frontSignal = this.getOutputSignal((BlockGetter)level, pos, state) > 0;
        return (BlockState)((BlockState)((BlockState)state.setValue((Property)SIDE_POWERED, (Comparable)Boolean.valueOf(sideSignal))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(backSignal))).setValue((Property)POWERING, (Comparable)Boolean.valueOf(frontSignal));
    }

    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return state.getValue((Property)FACING) == dir ? this.getOutputSignal(level, pos, state) : 0;
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        level.setBlock(blockPos, this.getUpdatedBlockstate(blockPos, (BlockState)blockState.cycle((Property)INVERTED), level), 2);
        level.updateNeighborsAt(blockPos, blockState.getBlock());
        float f = (Boolean)blockState.getValue((Property)INVERTED) == false ? 0.6f : 0.5f;
        level.playSound(null, blockPos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3f, f);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public boolean commonConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return direction != null;
    }

    protected int getOutputSignal(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        RedstoneAccumulatorBlockEntity be = (RedstoneAccumulatorBlockEntity)pLevel.getBlockEntity(pPos);
        if (be != null) {
            return (Boolean)pState.getValue((Property)INVERTED) != false ? 15 - be.outputSignal : be.outputSignal;
        }
        return 0;
    }

    protected int getDelay(BlockState state) {
        return 0;
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, be -> {
            if ((((Boolean)pState.getValue((Property)POWERED)).booleanValue() || be.outputSignal > 0) && pRandom.nextFloat() < 0.25f) {
                RedstoneAccumulatorBlock.addParticles(pState, (LevelAccessor)pLevel, pPos, 1.0f);
            }
        });
    }

    private static void addParticles(BlockState state, LevelAccessor level, BlockPos pos, float alpha) {
        level.addParticle((ParticleOptions)new DustParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f), alpha), (double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() + 0.5f), (double)((float)pos.getZ() + 0.5f), 0.0, 0.0, 0.0);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SimBlockShapes.REDSTONE_ACCUMULATOR;
    }
}
