/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
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
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.elevator;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactScreen;
import com.simibubi.create.content.redstone.contact.RedstoneContactBlock;
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.Optional;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElevatorContactBlock
extends WrenchableDirectionalBlock
implements IBE<ElevatorContactBlockEntity>,
SpecialBlockItemRequirement {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty CALLING = BooleanProperty.create((String)"calling");
    public static final BooleanProperty POWERING = BrassDiodeBlock.POWERING;
    public static final MapCodec<ElevatorContactBlock> CODEC = ElevatorContactBlock.simpleCodec(ElevatorContactBlock::new);

    public ElevatorContactBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)CALLING, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERING, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)FACING, (Comparable)Direction.SOUTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{CALLING, POWERING, POWERED}));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        InteractionResult onWrenched = super.onWrenched(state, context);
        if (onWrenched != InteractionResult.SUCCESS) {
            return onWrenched;
        }
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return onWrenched;
        }
        BlockPos pos = context.getClickedPos();
        state = level.getBlockState(pos);
        Direction facing = (Direction)state.getValue((Property)RedstoneContactBlock.FACING);
        if (facing.getAxis() != Direction.Axis.Y && ElevatorColumn.get((LevelAccessor)level, new ElevatorColumn.ColumnCoords(pos.getX(), pos.getZ(), facing)) != null) {
            return onWrenched;
        }
        level.setBlockAndUpdate(pos, BlockHelper.copyProperties(state, AllBlocks.REDSTONE_CONTACT.getDefaultState()));
        return onWrenched;
    }

    @Nullable
    public static ElevatorColumn.ColumnCoords getColumnCoords(LevelAccessor level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        if (!AllBlocks.ELEVATOR_CONTACT.has(blockState) && !AllBlocks.REDSTONE_CONTACT.has(blockState)) {
            return null;
        }
        Direction facing = (Direction)blockState.getValue((Property)FACING);
        BlockPos target = pos;
        return new ElevatorColumn.ColumnCoords(target.getX(), target.getZ(), facing);
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (pLevel.isClientSide) {
            return;
        }
        boolean isPowered = (Boolean)pState.getValue((Property)POWERED);
        if (isPowered == pLevel.hasNeighborSignal(pPos)) {
            return;
        }
        pLevel.setBlock(pPos, (BlockState)pState.cycle((Property)POWERED), 2);
        if (isPowered) {
            return;
        }
        if (((Boolean)pState.getValue((Property)CALLING)).booleanValue()) {
            return;
        }
        ElevatorColumn elevatorColumn = ElevatorColumn.getOrCreate((LevelAccessor)pLevel, ElevatorContactBlock.getColumnCoords((LevelAccessor)pLevel, pPos));
        this.callToContactAndUpdate(elevatorColumn, pState, pLevel, pPos, true);
    }

    public void callToContactAndUpdate(ElevatorColumn elevatorColumn, BlockState pState, Level pLevel, BlockPos pPos, boolean powered) {
        pLevel.setBlock(pPos, (BlockState)pState.cycle((Property)CALLING), 2);
        for (BlockPos otherPos : elevatorColumn.getContacts()) {
            BlockState otherState;
            if (otherPos.equals((Object)pPos) || !AllBlocks.ELEVATOR_CONTACT.has(otherState = pLevel.getBlockState(otherPos))) continue;
            pLevel.setBlock(otherPos, (BlockState)otherState.setValue((Property)CALLING, (Comparable)Boolean.valueOf(false)), 18);
            this.scheduleActivation((LevelAccessor)pLevel, otherPos);
        }
        if (powered) {
            pState = (BlockState)pState.setValue((Property)POWERED, (Comparable)Boolean.valueOf(true));
        }
        pLevel.setBlock(pPos, (BlockState)pState.setValue((Property)CALLING, (Comparable)Boolean.valueOf(true)), 2);
        pLevel.updateNeighborsAt(pPos, (Block)this);
        elevatorColumn.target(pPos.getY());
        elevatorColumn.markDirty();
    }

    public void scheduleActivation(LevelAccessor pLevel, BlockPos pPos) {
        if (!pLevel.getBlockTicks().hasScheduledTick(pPos, (Object)this)) {
            pLevel.scheduleTick(pPos, (Block)this, 1);
        }
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
        boolean wasPowering = (Boolean)pState.getValue((Property)POWERING);
        Optional optionalBE = this.getBlockEntityOptional((BlockGetter)pLevel, pPos);
        boolean shouldBePowering = optionalBE.map(be -> {
            boolean activateBlock = be.activateBlock;
            be.activateBlock = false;
            be.setChanged();
            return activateBlock;
        }).orElse(false);
        if (wasPowering || (shouldBePowering |= RedstoneContactBlock.hasValidContact((LevelAccessor)pLevel, pPos, (Direction)pState.getValue((Property)FACING)))) {
            pLevel.setBlock(pPos, (BlockState)pState.setValue((Property)POWERING, (Comparable)Boolean.valueOf(shouldBePowering)), 18);
        }
        pLevel.updateNeighborsAt(pPos, (Block)this);
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing != stateIn.getValue((Property)FACING)) {
            return stateIn;
        }
        boolean hasValidContact = RedstoneContactBlock.hasValidContact(worldIn, currentPos, facing);
        if ((Boolean)stateIn.getValue((Property)POWERING) != hasValidContact) {
            this.scheduleActivation(worldIn, currentPos);
        }
        return stateIn;
    }

    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    public boolean isSignalSource(BlockState state) {
        return (Boolean)state.getValue((Property)POWERING);
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return AllBlocks.REDSTONE_CONTACT.asStack();
    }

    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        if (side == null) {
            return true;
        }
        return state.getValue((Property)FACING) != side.getOpposite();
    }

    public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (side == null) {
            return 0;
        }
        BlockState toState = blockAccess.getBlockState(pos.relative(side.getOpposite()));
        if (toState.is((Block)this)) {
            return 0;
        }
        return (Boolean)state.getValue((Property)POWERING) != false ? 15 : 0;
    }

    @Override
    public Class<ElevatorContactBlockEntity> getBlockEntityClass() {
        return ElevatorContactBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ElevatorContactBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.ELEVATOR_CONTACT.get();
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        return ItemRequirement.of(AllBlocks.REDSTONE_CONTACT.getDefaultState(), be);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player != null && AllItems.WRENCH.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.withBlockEntityDo((BlockGetter)level, pos, be -> this.displayScreen((ElevatorContactBlockEntity)be, player)));
        return ItemInteractionResult.SUCCESS;
    }

    @OnlyIn(value=Dist.CLIENT)
    protected void displayScreen(ElevatorContactBlockEntity be, Player player) {
        if (player instanceof LocalPlayer) {
            ScreenOpener.open((Screen)new ElevatorContactScreen(be.getBlockPos(), be.shortName, be.longName, be.doorControls.mode));
        }
    }

    public static int getLight(BlockState state) {
        return (Boolean)state.getValue((Property)POWERING) != false ? 10 : 0;
    }

    @Override
    @NotNull
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }
}
