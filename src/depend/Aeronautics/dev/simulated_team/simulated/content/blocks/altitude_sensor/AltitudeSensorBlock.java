/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.altitude_sensor;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlockEntity;
import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorScreen;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.multiloader.CommonRedstoneBlock;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AltitudeSensorBlock
extends FaceAttachedHorizontalDirectionalBlock
implements IBE<AltitudeSensorBlockEntity>,
IWrenchable,
CommonRedstoneBlock {
    public static final EnumProperty<FaceType> DIAL = EnumProperty.create((String)"dial", FaceType.class);
    public static final BooleanProperty POWERED = BooleanProperty.create((String)"powered");
    public static final MapCodec<AltitudeSensorBlock> CODEC = AltitudeSensorBlock.simpleCodec(AltitudeSensorBlock::new);

    public AltitudeSensorBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @NotNull
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        AttachFace face = AttachFace.FLOOR;
        if (context.getClickedFace() == Direction.DOWN) {
            face = AttachFace.CEILING;
        } else if (context.getClickedFace().getAxis().isHorizontal()) {
            face = AttachFace.WALL;
            facing = context.getClickedFace();
        }
        return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)facing)).setValue((Property)FACE, (Comparable)face)).setValue(DIAL, (Comparable)((Object)FaceType.LINEAR))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(new Property[]{FACING, FACE, DIAL, POWERED});
        super.createBlockStateDefinition(pBuilder);
    }

    @NotNull
    public VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (state.getValue((Property)FACE) == AttachFace.FLOOR) {
            return SimBlockShapes.ALTITUDE_SENSOR_FLOOR.get((Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING));
        }
        if (state.getValue((Property)FACE) == AttachFace.CEILING) {
            return SimBlockShapes.ALTITUDE_SENSOR_CEILING.get((Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING));
        }
        return SimBlockShapes.ALTITUDE_SENSOR_WALL.get((Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING));
    }

    public Class<AltitudeSensorBlockEntity> getBlockEntityClass() {
        return AltitudeSensorBlockEntity.class;
    }

    public BlockEntityType<? extends AltitudeSensorBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.ALTITUDE_SENSOR.get();
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public int getSignal(@NotNull BlockState state, BlockGetter level, @NotNull BlockPos pos, @NotNull Direction direction) {
        AltitudeSensorBlockEntity be = (AltitudeSensorBlockEntity)level.getBlockEntity(pos);
        return be.signal;
    }

    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (direction != Direction.UP) {
            return 0;
        }
        return this.getSignal(state, level, pos, direction);
    }

    @Override
    public boolean commonConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return direction != null;
    }

    @Override
    public boolean commonCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return true;
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getClickedFace() == state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)) {
            IWrenchable.playRotateSound((Level)context.getLevel(), (BlockPos)context.getClickedPos());
            FaceType faceType = (FaceType)((Object)state.getValue(DIAL));
            faceType = faceType == FaceType.LINEAR ? FaceType.RADIAL : FaceType.LINEAR;
            context.getLevel().setBlock(context.getClickedPos(), (BlockState)state.setValue(DIAL, (Comparable)((Object)faceType)), 3);
            return InteractionResult.SUCCESS;
        }
        return super.onWrenched(state, context);
    }

    @NotNull
    protected ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        return AllItems.WRENCH.isIn(stack) ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION : this.onBlockEntityUseItemOn((BlockGetter)level, pos, be -> {
            if (level.isClientSide) {
                this.withBlockEntityDo((BlockGetter)level, pos, AltitudeSensorScreen::open);
            }
            return ItemInteractionResult.SUCCESS;
        });
    }

    public static enum FaceType implements StringRepresentable
    {
        LINEAR,
        RADIAL;


        @NotNull
        public String getSerializedName() {
            return this.toString().toLowerCase(Locale.ROOT);
        }
    }
}
