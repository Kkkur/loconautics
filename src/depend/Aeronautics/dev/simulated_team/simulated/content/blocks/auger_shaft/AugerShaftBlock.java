/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
 *  com.simibubi.create.content.logistics.chute.ChuteBlock
 *  com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.foundation.placement.PoleHelper
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.auger_shaft;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.placement.PoleHelper;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerCogBlock;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlockEntity;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimSoundEvents;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AugerShaftBlock
extends RotatedPillarKineticBlock
implements IBE<AugerShaftBlockEntity> {
    public static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());
    public static final EnumProperty<BarrelSection> SECTION = EnumProperty.create((String)"section", BarrelSection.class);
    public static final BooleanProperty COG = BooleanProperty.create((String)"cog");
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty ENCASED = BooleanProperty.create((String)"encased");
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf((Map)((Map)Util.make((Object)Maps.newEnumMap(Direction.class), map -> {
        map.put(Direction.NORTH, NORTH);
        map.put(Direction.EAST, EAST);
        map.put(Direction.SOUTH, SOUTH);
        map.put(Direction.WEST, WEST);
        map.put(Direction.UP, UP);
        map.put(Direction.DOWN, DOWN);
    })));

    public AugerShaftBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.defaultBlockState().setValue(SECTION, (Comparable)((Object)BarrelSection.SINGLE))).setValue((Property)COG, (Comparable)Boolean.valueOf(false))).setValue((Property)NORTH, (Comparable)Boolean.valueOf(false))).setValue((Property)EAST, (Comparable)Boolean.valueOf(false))).setValue((Property)SOUTH, (Comparable)Boolean.valueOf(false))).setValue((Property)WEST, (Comparable)Boolean.valueOf(false))).setValue((Property)UP, (Comparable)Boolean.valueOf(false))).setValue((Property)DOWN, (Comparable)Boolean.valueOf(false))).setValue((Property)ENCASED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{SECTION, COG, NORTH, EAST, SOUTH, WEST, UP, DOWN, ENCASED}));
    }

    private boolean connects(BlockPos pos, BlockState state, BlockPos otherPos, BlockState otherState) {
        return otherState.getBlock() == this && otherState.getValue((Property)AXIS) == state.getValue((Property)AXIS);
    }

    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        IPlacementHelper helper = PlacementHelpers.get((int)placementHelperId);
        if (helper.matchesItem(heldItem)) {
            return helper.getOffset(player, level, blockState, blockPos, blockHitResult).placeInWorld(level, (BlockItem)heldItem.getItem(), player, interactionHand, blockHitResult);
        }
        if (!(blockState.getBlock() instanceof AugerCogBlock)) {
            Boolean encased = (Boolean)blockState.getValue((Property)ENCASED);
            if (encased.booleanValue() && player.getItemInHand(interactionHand).is((Item)AllItems.WRENCH.get())) {
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }
                level.setBlockAndUpdate(blockPos, (BlockState)blockState.cycle((Property)ENCASED));
                level.levelEvent(2001, blockPos, Block.getId((BlockState)AllBlocks.INDUSTRIAL_IRON_BLOCK.getDefaultState()));
                return ItemInteractionResult.SUCCESS;
            }
            if (!encased.booleanValue() && player.getItemInHand(interactionHand).is(AllBlocks.INDUSTRIAL_IRON_BLOCK.asItem())) {
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }
                level.setBlockAndUpdate(blockPos, (BlockState)blockState.cycle((Property)ENCASED));
                level.playSound(null, blockPos, SimSoundEvents.AUGER_SHAFT_ENCASING.event(), SoundSource.BLOCKS, 0.5f, 1.05f);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(heldItem, blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        return this.transformAuger(state, SimBlocks.AUGER_COG.getDefaultState(), context, level);
    }

    @Nullable
    protected InteractionResult transformAuger(BlockState state, BlockState newState, UseOnContext context, Level level) {
        RegistryAccess reg = level.registryAccess();
        AugerShaftBlockEntity abe = (AugerShaftBlockEntity)this.getBlockEntity((BlockGetter)level, context.getClickedPos());
        if (abe != null) {
            CompoundTag tag = new CompoundTag();
            abe.write(tag, (HolderLookup.Provider)reg, false);
            abe.beingWrenched = true;
            KineticBlockEntity.switchToBlockState((Level)level, (BlockPos)context.getClickedPos(), (BlockState)((BlockState)newState.setValue((Property)AXIS, (Comparable)((Direction.Axis)state.getValue((Property)AXIS)))));
            AugerShaftBlockEntity newBE = (AugerShaftBlockEntity)this.getBlockEntity((BlockGetter)level, context.getClickedPos());
            if (newBE != null) {
                newBE.read(tag, (HolderLookup.Provider)reg, false);
                newBE.notifyUpdate();
                IWrenchable.playRotateSound((Level)level, (BlockPos)context.getClickedPos());
                return InteractionResult.SUCCESS;
            }
        } else {
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AXIS);
        Direction directionPos = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        Direction directionNegative = Direction.get((Direction.AxisDirection)Direction.AxisDirection.NEGATIVE, (Direction.Axis)axis);
        BlockPos posPos = pos.relative(directionPos);
        BlockPos posNeg = pos.relative(directionNegative);
        BlockState statePos = level.getBlockState(posPos);
        BlockState stateNeg = level.getBlockState(posNeg);
        BarrelSection section = BarrelSection.SINGLE;
        if (this.connects(pos, state, posPos, statePos) && !this.connects(pos, state, posNeg, stateNeg)) {
            section = BarrelSection.END;
        } else if (!this.connects(pos, state, posPos, statePos) && this.connects(pos, state, posNeg, stateNeg)) {
            section = BarrelSection.FRONT;
        } else if (this.connects(pos, state, posPos, statePos) && this.connects(pos, state, posNeg, stateNeg)) {
            section = BarrelSection.MIDDLE;
        }
        BlockState mutState = (BlockState)state.setValue(SECTION, (Comparable)((Object)section));
        boolean isFunnel = neighborState.getBlock() instanceof AbstractFunnelBlock;
        boolean hasHorizontalFacing = neighborState.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING);
        boolean hasFacing = neighborState.hasProperty((Property)BlockStateProperties.FACING);
        mutState = isFunnel && (hasHorizontalFacing && neighborState.getValue((Property)BlockStateProperties.HORIZONTAL_FACING) == dir || hasFacing && neighborState.getValue((Property)BlockStateProperties.FACING) == dir) || dir.getAxis().isVertical() && neighborState.getBlock() instanceof ChuteBlock ? (BlockState)mutState.setValue((Property)PROPERTY_BY_DIRECTION.get(dir), (Comparable)Boolean.valueOf(true)) : (BlockState)mutState.setValue((Property)PROPERTY_BY_DIRECTION.get(dir), (Comparable)Boolean.valueOf(false));
        return super.updateShape(mutState, dir, neighborState, level, pos, neighborPos);
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        BarrelSection section = (BarrelSection)((Object)state.getValue(SECTION));
        if (((Boolean)state.getValue((Property)ENCASED)).booleanValue() || section.equals((Object)BarrelSection.SINGLE)) {
            return Shapes.block();
        }
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AXIS);
        if (!section.equals((Object)BarrelSection.MIDDLE)) {
            return SimBlockShapes.AUGER_END_SHAPE.get(section.equals((Object)BarrelSection.FRONT) ? Direction.get((Direction.AxisDirection)Direction.AxisDirection.NEGATIVE, (Direction.Axis)axis) : Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis));
        }
        return SimBlockShapes.FOURTEEN_VOXEL_POLE.get(axis);
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return (Direction.Axis)state.getValue((Property)AXIS);
    }

    public Class<AugerShaftBlockEntity> getBlockEntityClass() {
        return AugerShaftBlockEntity.class;
    }

    public BlockEntityType<? extends AugerShaftBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.AUGER_SHAFT.get();
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == this.getRotationAxis(state);
    }

    public static enum BarrelSection implements StringRepresentable
    {
        FRONT,
        MIDDLE,
        END,
        SINGLE;


        public String getSerializedName() {
            return this.toString().toLowerCase(Locale.ROOT);
        }
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper
    extends PoleHelper<Direction.Axis> {
        private PlacementHelper() {
            super(state -> state.getBlock() instanceof AugerShaftBlock, state -> (Direction.Axis)state.getValue((Property)RotatedPillarKineticBlock.AXIS), (Property)RotatedPillarKineticBlock.AXIS);
        }

        public Predicate<ItemStack> getItemPredicate() {
            return i -> {
                BlockItem bi;
                Item patt0$temp = i.getItem();
                return patt0$temp instanceof BlockItem && (bi = (BlockItem)patt0$temp).getBlock() instanceof AugerShaftBlock;
            };
        }

        public Predicate<BlockState> getStatePredicate() {
            return Predicates.or(arg_0 -> SimBlocks.AUGER_SHAFT.has(arg_0), arg_0 -> SimBlocks.AUGER_COG.has(arg_0));
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            return super.getOffset(player, world, state, pos, ray);
        }
    }
}
