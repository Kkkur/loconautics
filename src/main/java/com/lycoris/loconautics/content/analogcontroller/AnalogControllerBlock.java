package com.lycoris.loconautics.content.analogcontroller;

import com.mojang.serialization.MapCodec;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Analog Controller — a stationary block that behaves like the Create train controls,
 * but drives the Create Redstone Link network instead of a train.
 *
 * Interaction summary:
 *   • Normal right-click  → "mount" the controller: W increases signal, S decreases,
 *                           Shift locks/unlocks decay, Escape dismounts.
 *   • Shift right-click   → opens the frequency-selection GUI (like the Linked Typewriter).
 *   • Signal range        → 0 (off) … 15 (max), mapping 1:1 to Minecraft redstone power.
 *   • Decay               → when unlocked and no key is held, signal decays by 1 per tick.
 *   • Lock                → holding Shift while mounted freezes the current value in place.
 */
public class AnalogControllerBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final MapCodec<AnalogControllerBlock> CODEC =
            AnalogControllerBlock.simpleCodec(AnalogControllerBlock::new);

    /** Current redstone output power (0-15). Stored in block state so neighbours update cheaply. */
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    /** Whether the block has an active user mounted on it. */
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public AnalogControllerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(POWER, 0)
                        .setValue(ACTIVE, false)
        );
    }

    // ------------------------------------------------------------------ codec

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    // ------------------------------------------------------------------ state

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, POWER, ACTIVE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        Player player = ctx.getPlayer();
        if (player != null && player.isShiftKeyDown()) {
            dir = dir.getOpposite();
        }
        return this.defaultBlockState().setValue(FACING, dir);
    }

    // ------------------------------------------------------------------ redstone

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(POWER);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWER);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWER);
    }

    // ------------------------------------------------------------------ interaction

    /**
     * Shift right-click → open frequency GUI.
     * Normal right-click → mount / dismount.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            // Open frequency screen (server side opens menu)
            if (!level.isClientSide && player instanceof ServerPlayer sp) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof AnalogControllerBlockEntity ace) {
                    sp.openMenu(ace, buf -> {
                        buf.writeBlockPos(pos);
                        ItemStack.OPTIONAL_STREAM_CODEC.encode(
                                (net.minecraft.network.RegistryFriendlyByteBuf) buf,
                                ace.getFrequencyFirst());
                        ItemStack.OPTIONAL_STREAM_CODEC.encode(
                                (net.minecraft.network.RegistryFriendlyByteBuf) buf,
                                ace.getFrequencySecond());
                    });
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // Normal click — toggle mount
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AnalogControllerBlockEntity ace) {
                ace.toggleUser(player);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // ------------------------------------------------------------------ block entity

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnalogControllerBlockEntity(LoconauticsRegistries.ANALOG_CONTROLLER_BE.get(), pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (type == LoconauticsRegistries.ANALOG_CONTROLLER_BE.get()) {
            //noinspection unchecked
            return (BlockEntityTicker<T>) (BlockEntityTicker<AnalogControllerBlockEntity>)
                    AnalogControllerBlockEntity::tick;
        }
        return null;
    }

    // ------------------------------------------------------------------ misc

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos,
                            BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AnalogControllerBlockEntity ace) {
                ace.onRemoved();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}