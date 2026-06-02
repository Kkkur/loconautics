/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.content.fluids.tank.FluidTankBlock
 *  com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock
 *  com.simibubi.create.foundation.block.IBE
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent.SteamVentBlockEntity;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import dev.eriksonn.aeronautics.index.AeroBlockShapes;
import dev.eriksonn.aeronautics.index.AeroTags;
import java.util.Locale;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SteamVentBlock
extends Block
implements IBE<SteamVentBlockEntity>,
SimpleWaterloggedBlock,
IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Variant> VARIANT = EnumProperty.create((String)"variant", Variant.class);

    public SteamVentBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{POWERED, BlockStateProperties.WATERLOGGED, VARIANT, FACING}));
    }

    @NotNull
    protected ItemInteractionResult useItemOn(ItemStack itemStack, @NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        Variant current;
        Variant conversion = Variant.getConversionFromItem(itemStack.getItem());
        if (conversion != null && conversion != (current = (Variant)((Object)blockState.getValue(VARIANT)))) {
            level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(VARIANT, (Comparable)((Object)conversion)));
            level.playLocalSound((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.COPPER_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void onPlace(@NotNull BlockState pState, @NotNull Level pLevel, BlockPos pPos, @NotNull BlockState pOldState, boolean pIsMoving) {
        FluidTankBlock.updateBoilerState((BlockState)pState, (Level)pLevel, (BlockPos)pPos.relative(Direction.DOWN));
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, SteamVentBlockEntity::getAndCacheTank);
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, x -> {
            if (!x.updateRawSignal()) {
                x.signalSync();
            }
        });
    }

    public void onRemove(BlockState pState, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean pIsMoving) {
        this.withBlockEntityDo((BlockGetter)level, pos, x -> {
            x.rawSignalStrength = 0;
        });
        if (!(!pState.hasBlockEntity() || pState.is(newState.getBlock()) && newState.hasBlockEntity())) {
            level.removeBlockEntity(pos);
        }
        for (Direction dir : Iterate.directions) {
            BlockEntity blockEntity = level.getBlockEntity(pos.relative(dir));
            if (!(blockEntity instanceof SteamVentBlockEntity)) continue;
            SteamVentBlockEntity vent = (SteamVentBlockEntity)blockEntity;
            vent.signalSync();
        }
        FluidTankBlock.updateBoilerState((BlockState)pState, (Level)level, (BlockPos)pos.relative(Direction.DOWN));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return (BlockState)((BlockState)((BlockState)super.getStateForPlacement(context).setValue((Property)POWERED, (Comparable)Boolean.valueOf(level.hasNeighborSignal(pos)))).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(level.getFluidState(pos).getType() == Fluids.WATER))).setValue((Property)FACING, (Comparable)(context.getPlayer().isShiftKeyDown() ? context.getHorizontalDirection().getOpposite() : context.getHorizontalDirection()));
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)level, pos, SteamVentBlockEntity::updateRawSignal);
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return SteamEngineBlock.canAttach((LevelReader)pLevel, (BlockPos)pPos, (Direction)Direction.DOWN);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AeroBlockShapes.STEAM_VENT.get(Direction.Axis.Y);
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    public Class<SteamVentBlockEntity> getBlockEntityClass() {
        return SteamVentBlockEntity.class;
    }

    public BlockEntityType<? extends SteamVentBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AeroBlockEntityTypes.STEAM_VENT.get();
    }

    public static enum Variant implements StringRepresentable
    {
        GOLD,
        IRON;


        public static Variant getConversionFromItem(Item item) {
            if (item.builtInRegistryHolder().is(AeroTags.ItemTags.GOLD_SHEET)) {
                return GOLD;
            }
            if (item.builtInRegistryHolder().is(AeroTags.ItemTags.IRON_SHEET)) {
                return IRON;
            }
            return null;
        }

        public String getSerializedName() {
            return this.toString().toLowerCase(Locale.ROOT);
        }
    }
}
