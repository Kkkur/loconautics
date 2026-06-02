/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
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
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.lasers.laser_pointer;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.content.blocks.lasers.laser_pointer.LaserPointerBlockEntity;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.util.SimColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
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
import org.jetbrains.annotations.NotNull;

public class LaserPointerBlock
extends DirectionalBlock
implements IBE<LaserPointerBlockEntity>,
IWrenchable {
    public static final MapCodec<LaserPointerBlock> CODEC = LaserPointerBlock.simpleCodec(LaserPointerBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

    public LaserPointerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)INVERTED, (Comparable)Boolean.valueOf(false)));
    }

    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{POWERED}).add(new Property[]{INVERTED}).add(new Property[]{FACING});
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction nearestLookingDirection = context.getNearestLookingDirection();
        if (!context.getPlayer().isShiftKeyDown()) {
            nearestLookingDirection = nearestLookingDirection.getOpposite();
        }
        return (BlockState)((BlockState)((BlockState)super.getStateForPlacement(context).setValue((Property)FACING, (Comparable)nearestLookingDirection)).setValue((Property)POWERED, (Comparable)Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())))).setValue((Property)INVERTED, (Comparable)Boolean.valueOf(false));
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, world, pos, neighborBlock, fromPos, moving);
        if (!world.isClientSide) {
            boolean powered = world.hasNeighborSignal(pos);
            world.setBlock(pos, (BlockState)state.setValue((Property)POWERED, (Comparable)Boolean.valueOf(powered)), 7);
        }
        if (((Boolean)state.getValue((Property)POWERED)).booleanValue()) {
            SimAdvancements.BIG_BEAM.awardToNearby(pos, world);
        }
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SimBlockShapes.LASER_POINTER.get((Direction)pState.getValue((Property)FACING));
    }

    @NotNull
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.setValue((Property)FACING, (Comparable)rot.rotate((Direction)state.getValue((Property)FACING)));
    }

    @NotNull
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation((Direction)state.getValue((Property)FACING)));
    }

    public Class<LaserPointerBlockEntity> getBlockEntityClass() {
        return LaserPointerBlockEntity.class;
    }

    public BlockEntityType<? extends LaserPointerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.LASER_POINTER.get();
    }

    protected ItemInteractionResult useItemOn(@NotNull ItemStack itemStack, @NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        boolean updatedColor;
        LaserPointerBlockEntity be = (LaserPointerBlockEntity)level.getBlockEntity(blockPos);
        assert (be != null);
        int newColor = -1;
        boolean newRainbow = be.isRainbow();
        Item item = itemStack.getItem();
        if (item instanceof DyeItem) {
            DyeItem dyeItem = (DyeItem)item;
            DyeColor gatheredColor = dyeItem.getDyeColor();
            newColor = gatheredColor.getTextColor();
        } else if (itemStack.is(SimTags.Items.LASER_POINTER_LENS)) {
            newColor = SimColors.MEDIA_OURPLE;
            newRainbow = false;
        } else if (itemStack.is(SimTags.Items.LASER_POINTER_RAINBOW)) {
            newRainbow = true;
        }
        boolean bl = updatedColor = newColor != -1 && be.laserColor != newColor;
        if (updatedColor || newRainbow != be.isRainbow()) {
            if (updatedColor) {
                be.setLaserColor(newColor);
            }
            be.setRainbow(newRainbow);
            level.playLocalSound((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.DYE_USE, SoundSource.PLAYERS, 0.3f, 1.0f, false);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getClickedFace().getAxis() == ((Direction)state.getValue((Property)FACING)).getAxis()) {
            KineticBlockEntity.switchToBlockState((Level)context.getLevel(), (BlockPos)context.getClickedPos(), (BlockState)((BlockState)state.cycle((Property)INVERTED)));
            return InteractionResult.SUCCESS;
        }
        return super.onWrenched(state, context);
    }
}
