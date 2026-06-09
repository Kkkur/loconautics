package com.lycoris.loconautics.content.analogcontroller;

import com.mojang.serialization.MapCodec;
import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.AllShapes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger("AnalogControllerBlock");

    /** Current redstone output power (0-15). Stored in block state so neighbours update cheaply. */
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    /** Whether the block has an active user mounted on it. */
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    /** Whether the block is transmitting a non-zero signal (power > 0, no user required). */
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public AnalogControllerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(POWER, 0)
                        .setValue(ACTIVE, false)
                        .setValue(POWERED, false)
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
        super.createBlockStateDefinition(builder.add(FACING, POWER, ACTIVE, POWERED));
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

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AllShapes.CONTROLS.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AllShapes.CONTROLS_COLLISION.get(state.getValue(FACING));
    }

    // ------------------------------------------------------------------ interaction

    /**
     * Shift right-click → open frequency GUI.
     * Normal right-click → mount / dismount.
     *
     * Uses useItemOn (fires for both empty and held hand) instead of useWithoutItem
     * so the interaction registers reliably in all contexts including Sable sub-levels.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        LOGGER.info("[analog-block] useItemOn fired: hand={} side={} pos={} player={} shift={}",
                hand, level.isClientSide ? "CLIENT" : "SERVER", pos, player.getName().getString(), player.isShiftKeyDown());

        if (hand != InteractionHand.MAIN_HAND) {
            LOGGER.info("[analog-block] ignoring off-hand click");
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (player.isShiftKeyDown()) {
            // Open frequency screen (server side opens menu)
            if (!level.isClientSide && player instanceof ServerPlayer sp) {
                AnalogControllerBlockEntity ace = resolveBlockEntity(level, pos);
                LOGGER.info("[analog-block] shift+click: resolved BE={}", ace);
                if (ace != null) {
                    sp.openMenu(ace, ace::sendToMenu);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        // Normal click — toggle mount
        if (!level.isClientSide) {
            AnalogControllerBlockEntity ace = resolveBlockEntity(level, pos);
            LOGGER.info("[analog-block] normal click: resolved BE={}", ace);
            if (ace != null) {
                ace.toggleUser(player);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * Resolves the AnalogControllerBlockEntity at {@code pos} in {@code level},
     * falling back to the Sable sub-level's own level when the block lives inside one.
     */
    @Nullable
    private static AnalogControllerBlockEntity resolveBlockEntity(Level level, BlockPos pos) {
        // Fast path: block is in the real world
        BlockEntity be = level.getBlockEntity(pos);
        LOGGER.info("[analog-block] resolveBlockEntity pos={} directBE={}", pos, be);
        if (be instanceof AnalogControllerBlockEntity ace) return ace;

        // Sub-level path: find the sub-level that owns this block pos and query its level
        dev.ryanhcode.sable.sublevel.SubLevel subLevel =
                dev.ryanhcode.sable.Sable.HELPER.getContaining(level, pos);
        LOGGER.info("[analog-block] subLevel lookup result={}", subLevel);
        if (subLevel != null) {
            be = subLevel.getLevel().getBlockEntity(pos);
            LOGGER.info("[analog-block] subLevel BE={}", be);
            if (be instanceof AnalogControllerBlockEntity ace) return ace;
        }
        LOGGER.warn("[analog-block] resolveBlockEntity returned null for pos={}", pos);
        return null;
    }

    // ------------------------------------------------------------------ block entity

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnalogControllerBlockEntity(LoconauticsRegistries.ANALOG_CONTROLLER_BE.get(), pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return type == LoconauticsRegistries.ANALOG_CONTROLLER_BE.get()
                ? (BlockEntityTicker<T>) (BlockEntityTicker<AnalogControllerBlockEntity>) AnalogControllerBlockEntity::tick
                : null;
    }

    // ------------------------------------------------------------------ misc

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos,
                            BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            AnalogControllerBlockEntity ace = resolveBlockEntity(level, pos);
            if (ace != null) {
                ace.onRemoved();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}