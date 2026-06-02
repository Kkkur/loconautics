/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
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
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 */
package com.simibubi.create.content.redstone.thresholdSwitch;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlockEntity;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchScreen;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;

public class ThresholdSwitchBlock
extends DirectedDirectionalBlock
implements IBE<ThresholdSwitchBlockEntity> {
    public static final IntegerProperty LEVEL = IntegerProperty.create((String)"level", (int)0, (int)5);

    public ThresholdSwitchBlock(BlockBehaviour.Properties p_i48377_1_) {
        super(p_i48377_1_);
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        this.updateObservedInventory(state, (LevelReader)worldIn, pos);
    }

    private void updateObservedInventory(BlockState state, LevelReader world, BlockPos pos) {
        this.withBlockEntityDo((BlockGetter)world, pos, ThresholdSwitchBlockEntity::updateCurrentLevel);
    }

    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return side != null && side.getOpposite() != ThresholdSwitchBlock.getTargetDirection(state);
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (side == ThresholdSwitchBlock.getTargetDirection(blockState).getOpposite()) {
            return 0;
        }
        return this.getBlockEntityOptional(blockAccess, pos).filter(ThresholdSwitchBlockEntity::isPowered).map($ -> 15).orElse(0);
    }

    public void tick(BlockState blockState, ServerLevel world, BlockPos pos, RandomSource random) {
        this.getBlockEntityOptional((BlockGetter)world, pos).ifPresent(ThresholdSwitchBlockEntity::updatePowerAfterDelay);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{LEVEL}));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player != null && AllItems.WRENCH.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.withBlockEntityDo((BlockGetter)level, pos, be -> this.displayScreen((ThresholdSwitchBlockEntity)be, player)));
        return ItemInteractionResult.SUCCESS;
    }

    @OnlyIn(value=Dist.CLIENT)
    protected void displayScreen(ThresholdSwitchBlockEntity be, Player player) {
        if (player instanceof LocalPlayer) {
            ScreenOpener.open((Screen)new ThresholdSwitchScreen(be));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        Direction preferredFacing = null;
        for (Direction face : context.getNearestLookingDirections()) {
            BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos().relative(face));
            if (be == null || be.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), null) == null && be.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null) == null) continue;
            preferredFacing = face;
            break;
        }
        if (preferredFacing == null) {
            Direction facing = context.getNearestLookingDirection();
            Direction direction = preferredFacing = context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? facing : facing.getOpposite();
        }
        if (preferredFacing.getAxis() == Direction.Axis.Y) {
            state = (BlockState)state.setValue((Property)TARGET, (Comparable)(preferredFacing == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR));
            preferredFacing = context.getHorizontalDirection();
        }
        return (BlockState)state.setValue((Property)FACING, preferredFacing);
    }

    @Override
    public Class<ThresholdSwitchBlockEntity> getBlockEntityClass() {
        return ThresholdSwitchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ThresholdSwitchBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.THRESHOLD_SWITCH.get();
    }
}
