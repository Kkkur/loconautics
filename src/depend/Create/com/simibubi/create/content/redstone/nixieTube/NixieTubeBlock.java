/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.redstone.nixieTube;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.redstone.nixieTube.DoubleFaceAttachedBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import java.util.List;
import java.util.function.BiConsumer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NixieTubeBlock
extends DoubleFaceAttachedBlock
implements IBE<NixieTubeBlockEntity>,
IWrenchable,
SimpleWaterloggedBlock,
SpecialBlockItemRequirement {
    protected final DyeColor color;

    public NixieTubeBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACE, (Comparable)((Object)DoubleFaceAttachedBlock.DoubleAttachFace.FLOOR))).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        List<ClipboardEntry> entries;
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        NixieTubeBlockEntity nixie = (NixieTubeBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        if (nixie == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (NixieTubeBlock.isInComputerControlledRow((LevelAccessor)level, pos)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.isEmpty()) {
            if (nixie.reactsToRedstone()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            nixie.clearCustomText();
            this.updateDisplayedRedstoneValue(state, level, pos);
            return ItemInteractionResult.SUCCESS;
        }
        boolean display = stack.getItem() == Items.NAME_TAG && stack.has(DataComponents.CUSTOM_NAME) || AllBlocks.CLIPBOARD.isIn(stack);
        DyeColor dye = DyeColor.getColor((ItemStack)stack);
        if (!display && dye == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        Component component = (Component)stack.getOrDefault(DataComponents.CUSTOM_NAME, (Object)Component.empty());
        if (AllBlocks.CLIPBOARD.isIn(stack) && !(entries = ClipboardEntry.getLastViewedEntries(stack)).isEmpty()) {
            component = entries.getFirst().text;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        String tagUsed = Component.Serializer.toJson((Component)component, (HolderLookup.Provider)level.registryAccess());
        NixieTubeBlock.walkNixies((LevelAccessor)level, pos, true, (currentPos, rowPosition) -> {
            if (display) {
                this.withBlockEntityDo((BlockGetter)level, (BlockPos)currentPos, be -> be.displayCustomText(tagUsed, (int)rowPosition));
            }
            if (dye != null) {
                level.setBlockAndUpdate(currentPos, NixieTubeBlock.withColor(state, dye));
            }
        });
        return ItemInteractionResult.SUCCESS;
    }

    public static Direction getLeftNixieDirection(@NotNull BlockState state) {
        Direction left = ((Direction)state.getValue((Property)FACING)).getOpposite();
        if (state.getValue((Property)FACE) == DoubleFaceAttachedBlock.DoubleAttachFace.WALL) {
            left = Direction.UP;
        }
        if (state.getValue((Property)FACE) == DoubleFaceAttachedBlock.DoubleAttachFace.WALL_REVERSED) {
            left = Direction.DOWN;
        }
        return left;
    }

    public static Direction getRightNixieDirection(@NotNull BlockState state) {
        return NixieTubeBlock.getLeftNixieDirection(state).getOpposite();
    }

    public static boolean isInComputerControlledRow(@NotNull LevelAccessor world, @NotNull BlockPos pos) {
        return Mods.COMPUTERCRAFT.isLoaded() && !NixieTubeBlock.walkNixies(world, pos, false, null);
    }

    public static boolean walkNixies(@NotNull LevelAccessor world, @NotNull BlockPos start, boolean allowComputerControlled, @Nullable BiConsumer<BlockPos, Integer> callback) {
        BlockEntity ntbe;
        BlockPos nextPos;
        BlockState state = world.getBlockState(start);
        if (!(state.getBlock() instanceof NixieTubeBlock)) {
            return false;
        }
        if (!Mods.COMPUTERCRAFT.isLoaded()) {
            allowComputerControlled = true;
        }
        BlockPos currentPos = start;
        Direction left = NixieTubeBlock.getLeftNixieDirection(state);
        Direction right = left.getOpposite();
        while (NixieTubeBlock.areNixieBlocksEqual(world.getBlockState(nextPos = currentPos.relative(left)), state)) {
            BlockEntity blockEntity;
            if (!allowComputerControlled && (blockEntity = world.getBlockEntity(nextPos)) instanceof NixieTubeBlockEntity) {
                ntbe = (NixieTubeBlockEntity)blockEntity;
                if (ntbe.computerBehaviour.hasAttachedComputer()) {
                    return false;
                }
            }
            currentPos = nextPos;
        }
        if (!allowComputerControlled) {
            BlockPos nextPos2;
            ntbe = world.getBlockEntity(start);
            if (ntbe instanceof NixieTubeBlockEntity) {
                NixieTubeBlockEntity ntbe2 = (NixieTubeBlockEntity)ntbe;
                if (ntbe2.computerBehaviour.hasAttachedComputer()) {
                    return false;
                }
            }
            BlockPos leftmostPos = currentPos;
            currentPos = start;
            while (NixieTubeBlock.areNixieBlocksEqual(world.getBlockState(nextPos2 = currentPos.relative(right)), state)) {
                BlockEntity blockEntity = world.getBlockEntity(nextPos2);
                if (blockEntity instanceof NixieTubeBlockEntity) {
                    NixieTubeBlockEntity ntbe3 = (NixieTubeBlockEntity)blockEntity;
                    if (ntbe3.computerBehaviour.hasAttachedComputer()) {
                        return false;
                    }
                }
                currentPos = nextPos2;
            }
            currentPos = leftmostPos;
        }
        int index = 0;
        while (true) {
            BlockPos nextPos3;
            int rowPosition = ++index;
            if (callback != null) {
                callback.accept(currentPos, rowPosition);
            }
            if (!NixieTubeBlock.areNixieBlocksEqual(world.getBlockState(nextPos3 = currentPos.relative(right)), state)) break;
            currentPos = nextPos3;
        }
        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{FACE, FACING, BlockStateProperties.WATERLOGGED}));
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() instanceof NixieTubeBlock) {
            return;
        }
        world.removeBlockEntity(pos);
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            Direction right;
            BlockPos rightPos;
            Direction left = NixieTubeBlock.getLeftNixieDirection(state);
            BlockPos leftPos = pos.relative(left);
            if (NixieTubeBlock.areNixieBlocksEqual(world.getBlockState(leftPos), state)) {
                boolean leftRowComputerControlled = NixieTubeBlock.isInComputerControlledRow((LevelAccessor)world, leftPos);
                NixieTubeBlock.walkNixies((LevelAccessor)world, leftPos, true, leftRowComputerControlled ? (currentPos, rowPosition) -> {
                    BlockEntity patt0$temp = world.getBlockEntity(currentPos);
                    if (patt0$temp instanceof NixieTubeBlockEntity) {
                        NixieTubeBlockEntity ntbe = (NixieTubeBlockEntity)patt0$temp;
                        ntbe.displayEmptyText((int)rowPosition);
                    }
                } : (currentPos, rowPosition) -> {
                    BlockEntity patt0$temp = world.getBlockEntity(currentPos);
                    if (patt0$temp instanceof NixieTubeBlockEntity) {
                        NixieTubeBlockEntity ntbe = (NixieTubeBlockEntity)patt0$temp;
                        NixieTubeBlock.updateDisplayedRedstoneValue(ntbe, state, true);
                    }
                });
            }
            if (NixieTubeBlock.areNixieBlocksEqual(world.getBlockState(rightPos = pos.relative(right = left.getOpposite())), state)) {
                boolean rightRowComputerControlled = NixieTubeBlock.isInComputerControlledRow((LevelAccessor)world, rightPos);
                NixieTubeBlock.walkNixies((LevelAccessor)world, rightPos, true, rightRowComputerControlled ? (currentPos, rowPosition) -> {
                    BlockEntity patt0$temp = world.getBlockEntity(currentPos);
                    if (patt0$temp instanceof NixieTubeBlockEntity) {
                        NixieTubeBlockEntity ntbe = (NixieTubeBlockEntity)patt0$temp;
                        ntbe.displayEmptyText((int)rowPosition);
                    }
                } : (currentPos, rowPosition) -> {
                    BlockEntity patt0$temp = world.getBlockEntity(currentPos);
                    if (patt0$temp instanceof NixieTubeBlockEntity) {
                        NixieTubeBlockEntity ntbe = (NixieTubeBlockEntity)patt0$temp;
                        NixieTubeBlock.updateDisplayedRedstoneValue(ntbe, state, true);
                    }
                });
            }
        }
    }

    public ItemStack getCloneItemStack(LevelReader pLevel, BlockPos pPos, BlockState pState) {
        return AllBlocks.ORANGE_NIXIE_TUBE.asStack();
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, ((NixieTubeBlock)AllBlocks.ORANGE_NIXIE_TUBE.get()).asItem());
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = (Direction)pState.getValue((Property)FACING);
        return switch ((DoubleFaceAttachedBlock.DoubleAttachFace)((Object)pState.getValue((Property)FACE))) {
            case DoubleFaceAttachedBlock.DoubleAttachFace.CEILING -> AllShapes.NIXIE_TUBE_CEILING.get(facing.getClockWise().getAxis());
            case DoubleFaceAttachedBlock.DoubleAttachFace.FLOOR -> AllShapes.NIXIE_TUBE.get(facing.getClockWise().getAxis());
            default -> AllShapes.NIXIE_TUBE_WALL.get(facing);
        };
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        if (this.color != DyeColor.ORANGE) {
            return ((NixieTubeBlock)AllBlocks.ORANGE_NIXIE_TUBE.get()).getCloneItemStack(state, target, level, pos, player);
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            world.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)world));
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        if (state.getValue((Property)FACE) != DoubleFaceAttachedBlock.DoubleAttachFace.WALL && state.getValue((Property)FACE) != DoubleFaceAttachedBlock.DoubleAttachFace.WALL_REVERSED) {
            state = (BlockState)state.setValue((Property)FACING, (Comparable)((Direction)state.getValue((Property)FACING)).getClockWise());
        }
        return (BlockState)state.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER));
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
        this.updateDisplayedRedstoneValue(state, (Level)worldIn, pos);
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getBlock() == oldState.getBlock() || isMoving || oldState.getBlock() instanceof NixieTubeBlock) {
            return;
        }
        if (Mods.COMPUTERCRAFT.isLoaded() && NixieTubeBlock.isInComputerControlledRow((LevelAccessor)worldIn, pos)) {
            NixieTubeBlock.walkNixies((LevelAccessor)worldIn, pos, true, (currentPos, rowPosition) -> {
                BlockEntity patt0$temp = worldIn.getBlockEntity(currentPos);
                if (patt0$temp instanceof NixieTubeBlockEntity) {
                    NixieTubeBlockEntity ntbe = (NixieTubeBlockEntity)patt0$temp;
                    ntbe.displayEmptyText((int)rowPosition);
                }
            });
            return;
        }
        this.updateDisplayedRedstoneValue(state, worldIn, pos);
    }

    public static void updateDisplayedRedstoneValue(NixieTubeBlockEntity be, BlockState state, boolean force) {
        if (be.getLevel() == null || be.getLevel().isClientSide) {
            return;
        }
        if (be.reactsToRedstone() || force) {
            be.updateRedstoneStrength(NixieTubeBlock.getPower(be.getLevel(), state, be.getBlockPos()));
        }
    }

    private void updateDisplayedRedstoneValue(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)level, pos, be -> NixieTubeBlock.updateDisplayedRedstoneValue(be, state, false));
    }

    static boolean isValidBlock(BlockGetter world, BlockPos pos, boolean above) {
        BlockState state = world.getBlockState(pos.above(above ? 1 : -1));
        return !state.getShape(world, pos).isEmpty();
    }

    private static int getPower(Level level, BlockState state, BlockPos pos) {
        int power = 0;
        for (Direction direction : Iterate.directions) {
            power = Math.max(level.getSignal(pos.relative(direction), direction), power);
        }
        for (Direction direction : Iterate.directions) {
            if (((Direction)state.getValue((Property)FACING)).getOpposite() == direction) continue;
            power = Math.max(level.getSignal(pos.relative(direction), Direction.UP), power);
        }
        return power;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return side != null;
    }

    @Override
    public Class<NixieTubeBlockEntity> getBlockEntityClass() {
        return NixieTubeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends NixieTubeBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.NIXIE_TUBE.get();
    }

    public DyeColor getColor() {
        return this.color;
    }

    public static boolean areNixieBlocksEqual(BlockState blockState, BlockState otherState) {
        if (!(blockState.getBlock() instanceof NixieTubeBlock)) {
            return false;
        }
        if (!(otherState.getBlock() instanceof NixieTubeBlock)) {
            return false;
        }
        return NixieTubeBlock.withColor(blockState, DyeColor.WHITE) == NixieTubeBlock.withColor(otherState, DyeColor.WHITE);
    }

    public static BlockState withColor(BlockState state, DyeColor color) {
        return (BlockState)((BlockState)((BlockState)(color == DyeColor.ORANGE ? AllBlocks.ORANGE_NIXIE_TUBE : AllBlocks.NIXIE_TUBES.get(color)).getDefaultState().setValue((Property)FACING, (Comparable)((Direction)state.getValue((Property)FACING)))).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)))).setValue((Property)FACE, (Comparable)((Object)((DoubleFaceAttachedBlock.DoubleAttachFace)((Object)state.getValue((Property)FACE)))));
    }

    public static DyeColor colorOf(BlockState blockState) {
        return blockState.getBlock() instanceof NixieTubeBlock ? ((NixieTubeBlock)blockState.getBlock()).color : DyeColor.ORANGE;
    }

    public static Direction getFacing(BlockState sideState) {
        return NixieTubeBlock.getConnectedDirection(sideState);
    }
}
