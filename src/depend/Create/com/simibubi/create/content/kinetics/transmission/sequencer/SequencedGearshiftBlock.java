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
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.transmission.sequencer;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftScreen;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SequencedGearshiftBlock
extends HorizontalAxisKineticBlock
implements IBE<SequencedGearshiftBlockEntity>,
TransformableBlock {
    public static final BooleanProperty VERTICAL = BooleanProperty.create((String)"vertical");
    public static final IntegerProperty STATE = IntegerProperty.create((String)"state", (int)0, (int)5);

    public SequencedGearshiftBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{STATE, VERTICAL}));
    }

    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        if (!level.getBlockTicks().willTickThisTick(pos, (Object)this)) {
            level.scheduleTick(pos, (Block)this, 1);
        }
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource r) {
        boolean previouslyPowered = (Integer)state.getValue((Property)STATE) != 0;
        boolean isPowered = worldIn.hasNeighborSignal(pos);
        this.withBlockEntityDo((BlockGetter)worldIn, pos, sgte -> sgte.onRedstoneUpdate(isPowered, previouslyPowered));
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        return false;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        if (((Boolean)state.getValue((Property)VERTICAL)).booleanValue()) {
            return face.getAxis().isVertical();
        }
        return super.hasShaftTowards(world, pos, state, face);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockItem blockItem;
        if (AllItems.WRENCH.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        Item item = stack.getItem();
        if (item instanceof BlockItem && (blockItem = (BlockItem)item).getBlock() instanceof KineticBlock && this.hasShaftTowards((LevelReader)level, pos, state, hitResult.getDirection())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.withBlockEntityDo((BlockGetter)level, pos, be -> this.displayScreen((SequencedGearshiftBlockEntity)be, player)));
        return ItemInteractionResult.SUCCESS;
    }

    @OnlyIn(value=Dist.CLIENT)
    protected void displayScreen(SequencedGearshiftBlockEntity be, Player player) {
        if (player instanceof LocalPlayer) {
            ScreenOpener.open((Screen)new SequencedGearshiftScreen(be));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis preferredAxis = RotatedPillarKineticBlock.getPreferredAxis(context);
        if (!(preferredAxis == null || context.getPlayer() != null && context.getPlayer().isShiftKeyDown())) {
            return this.withAxis(preferredAxis, context);
        }
        return this.withAxis(context.getNearestLookingDirection().getAxis(), context);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        BlockState newState = state;
        if (context.getClickedFace().getAxis() != Direction.Axis.Y && newState.getValue(HORIZONTAL_AXIS) != context.getClickedFace().getAxis()) {
            newState = (BlockState)newState.cycle((Property)VERTICAL);
        }
        return super.onWrenched(newState, context);
    }

    private BlockState withAxis(Direction.Axis axis, BlockPlaceContext context) {
        BlockState state = (BlockState)this.defaultBlockState().setValue((Property)VERTICAL, (Comparable)Boolean.valueOf(axis.isVertical()));
        if (axis.isVertical()) {
            return (BlockState)state.setValue(HORIZONTAL_AXIS, (Comparable)context.getHorizontalDirection().getAxis());
        }
        return (BlockState)state.setValue(HORIZONTAL_AXIS, (Comparable)axis);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        if (((Boolean)state.getValue((Property)VERTICAL)).booleanValue()) {
            return Direction.Axis.Y;
        }
        return super.getRotationAxis(state);
    }

    @Override
    public Class<SequencedGearshiftBlockEntity> getBlockEntityClass() {
        return SequencedGearshiftBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SequencedGearshiftBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.SEQUENCED_GEARSHIFT.get();
    }

    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return (Integer)state.getValue((Property)STATE);
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null) {
            state = this.mirror(state, transform.mirror);
        }
        if (transform.rotationAxis == Direction.Axis.Y) {
            return this.rotate(state, transform.rotation);
        }
        if (transform.rotation.ordinal() % 2 == 1) {
            if (transform.rotationAxis != state.getValue(HORIZONTAL_AXIS)) {
                return (BlockState)state.cycle((Property)VERTICAL);
            }
            if (((Boolean)state.getValue((Property)VERTICAL)).booleanValue()) {
                return (BlockState)((BlockState)state.cycle((Property)VERTICAL)).cycle(HORIZONTAL_AXIS);
            }
        }
        return state;
    }
}
