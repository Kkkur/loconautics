/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.bearing.linearbearing;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LinearCasingBlock
extends Block
implements IWrenchable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final EnumProperty<SliderSide> SIDE = EnumProperty.create((String)"side", SliderSide.class);
    private static final VoxelShape CS_FLOOR_NS = Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)3.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)3.0, (double)0.0, (double)2.0, (double)13.0, (double)16.0), Block.box((double)14.0, (double)3.0, (double)0.0, (double)16.0, (double)13.0, (double)16.0), Block.box((double)2.0, (double)10.0, (double)0.0, (double)5.0, (double)13.0, (double)16.0), Block.box((double)11.0, (double)10.0, (double)0.0, (double)14.0, (double)13.0, (double)16.0)});
    private static final VoxelShape CS_FLOOR_EW = Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)3.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)3.0, (double)0.0, (double)16.0, (double)13.0, (double)2.0), Block.box((double)0.0, (double)3.0, (double)14.0, (double)16.0, (double)13.0, (double)16.0), Block.box((double)0.0, (double)10.0, (double)2.0, (double)16.0, (double)13.0, (double)5.0), Block.box((double)0.0, (double)10.0, (double)11.0, (double)16.0, (double)13.0, (double)14.0)});
    private static final VoxelShape CS_CEILING_NS = Shapes.or((VoxelShape)Block.box((double)0.0, (double)13.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)3.0, (double)0.0, (double)2.0, (double)13.0, (double)16.0), Block.box((double)14.0, (double)3.0, (double)0.0, (double)16.0, (double)13.0, (double)16.0), Block.box((double)2.0, (double)3.0, (double)0.0, (double)5.0, (double)6.0, (double)16.0), Block.box((double)11.0, (double)3.0, (double)0.0, (double)14.0, (double)6.0, (double)16.0)});
    private static final VoxelShape CS_CEILING_EW = Shapes.or((VoxelShape)Block.box((double)0.0, (double)13.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)3.0, (double)0.0, (double)16.0, (double)13.0, (double)2.0), Block.box((double)0.0, (double)3.0, (double)14.0, (double)16.0, (double)13.0, (double)16.0), Block.box((double)0.0, (double)3.0, (double)2.0, (double)16.0, (double)6.0, (double)5.0), Block.box((double)0.0, (double)3.0, (double)11.0, (double)16.0, (double)6.0, (double)14.0)});
    private static final VoxelShape CS_WALL_N = Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)13.0, (double)16.0, (double)16.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)0.0, (double)3.0, (double)2.0, (double)16.0, (double)13.0), Block.box((double)14.0, (double)0.0, (double)3.0, (double)16.0, (double)16.0, (double)13.0), Block.box((double)2.0, (double)0.0, (double)3.0, (double)5.0, (double)16.0, (double)6.0), Block.box((double)11.0, (double)0.0, (double)3.0, (double)14.0, (double)16.0, (double)6.0)});
    private static final VoxelShape CS_WALL_S = Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)3.0), (VoxelShape[])new VoxelShape[]{Block.box((double)14.0, (double)0.0, (double)3.0, (double)16.0, (double)16.0, (double)13.0), Block.box((double)0.0, (double)0.0, (double)3.0, (double)2.0, (double)16.0, (double)13.0), Block.box((double)11.0, (double)0.0, (double)10.0, (double)14.0, (double)16.0, (double)13.0), Block.box((double)2.0, (double)0.0, (double)10.0, (double)5.0, (double)16.0, (double)13.0)});
    private static final VoxelShape CS_WALL_E = Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)3.0, (double)16.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)3.0, (double)0.0, (double)0.0, (double)13.0, (double)16.0, (double)2.0), Block.box((double)3.0, (double)0.0, (double)14.0, (double)13.0, (double)16.0, (double)16.0), Block.box((double)10.0, (double)0.0, (double)2.0, (double)13.0, (double)16.0, (double)5.0), Block.box((double)10.0, (double)0.0, (double)11.0, (double)13.0, (double)16.0, (double)14.0)});
    private static final VoxelShape CS_WALL_W = Shapes.or((VoxelShape)Block.box((double)13.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)3.0, (double)0.0, (double)14.0, (double)13.0, (double)16.0, (double)16.0), Block.box((double)3.0, (double)0.0, (double)0.0, (double)13.0, (double)16.0, (double)2.0), Block.box((double)3.0, (double)0.0, (double)11.0, (double)6.0, (double)16.0, (double)14.0), Block.box((double)3.0, (double)0.0, (double)2.0, (double)6.0, (double)16.0, (double)5.0)});
    private static final VoxelShape CS_ROTATED_WALL_N = Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)13.0, (double)16.0, (double)16.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)2.0, (double)3.0, (double)16.0, (double)5.0, (double)6.0), Block.box((double)0.0, (double)0.0, (double)3.0, (double)16.0, (double)2.0, (double)13.0), Block.box((double)0.0, (double)14.0, (double)3.0, (double)16.0, (double)16.0, (double)13.0), Block.box((double)0.0, (double)11.0, (double)3.0, (double)16.0, (double)14.0, (double)6.0)});
    private static final VoxelShape CS_ROTATED_WALL_S = Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)3.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)2.0, (double)10.0, (double)16.0, (double)5.0, (double)13.0), Block.box((double)0.0, (double)0.0, (double)3.0, (double)16.0, (double)2.0, (double)13.0), Block.box((double)0.0, (double)14.0, (double)3.0, (double)16.0, (double)16.0, (double)13.0), Block.box((double)0.0, (double)11.0, (double)10.0, (double)16.0, (double)14.0, (double)13.0)});
    private static final VoxelShape CS_ROTATED_WALL_E = Shapes.or((VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)3.0, (double)16.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)10.0, (double)2.0, (double)0.0, (double)13.0, (double)5.0, (double)16.0), Block.box((double)3.0, (double)0.0, (double)0.0, (double)13.0, (double)2.0, (double)16.0), Block.box((double)3.0, (double)14.0, (double)0.0, (double)13.0, (double)16.0, (double)16.0), Block.box((double)10.0, (double)11.0, (double)0.0, (double)13.0, (double)14.0, (double)16.0)});
    private static final VoxelShape CS_ROTATED_WALL_W = Shapes.or((VoxelShape)Block.box((double)13.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)3.0, (double)2.0, (double)0.0, (double)6.0, (double)5.0, (double)16.0), Block.box((double)3.0, (double)0.0, (double)0.0, (double)13.0, (double)2.0, (double)16.0), Block.box((double)3.0, (double)14.0, (double)0.0, (double)13.0, (double)16.0, (double)16.0), Block.box((double)3.0, (double)11.0, (double)0.0, (double)6.0, (double)14.0, (double)16.0)});

    public LinearCasingBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH)).setValue(FACE, (Comparable)AttachFace.FLOOR)).setValue(SIDE, (Comparable)((Object)SliderSide.FRONT)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction playerFacing = context.getHorizontalDirection();
        BlockState state = (BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)playerFacing.getOpposite())).setValue(SIDE, (Comparable)((Object)SliderSide.FRONT));
        if (clickedFace == Direction.DOWN) {
            return (BlockState)state.setValue(FACE, (Comparable)AttachFace.CEILING);
        }
        if (clickedFace == Direction.UP) {
            return (BlockState)state.setValue(FACE, (Comparable)AttachFace.FLOOR);
        }
        return (BlockState)((BlockState)state.setValue(FACE, (Comparable)AttachFace.WALL)).setValue((Property)FACING, (Comparable)clickedFace);
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.isClientSide) {
            AttachFace currentFace = (AttachFace)state.getValue(FACE);
            if (currentFace == AttachFace.WALL) {
                SliderSide currentSide = (SliderSide)((Object)state.getValue(SIDE));
                SliderSide nextSide = currentSide == SliderSide.FRONT ? SliderSide.BACK : SliderSide.FRONT;
                level.setBlock(pos, (BlockState)state.setValue(SIDE, (Comparable)((Object)nextSide)), 3);
            } else {
                Direction currentFacing = (Direction)state.getValue((Property)FACING);
                Direction nextFacing = currentFacing.getClockWise();
                level.setBlock(pos, (BlockState)state.setValue((Property)FACING, (Comparable)nextFacing), 3);
            }
        }
        return InteractionResult.SUCCESS;
    }

    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (!level.isClientSide && player != null) {
            if (!player.isCreative()) {
                ItemStack dropStack = new ItemStack((ItemLike)this.asItem());
                if (!player.getInventory().add(dropStack)) {
                    player.drop(dropStack, false);
                }
            }
            level.destroyBlock(pos, false, (Entity)player);
        }
        return InteractionResult.SUCCESS;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, FACE, SIDE});
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        AttachFace face = (AttachFace)state.getValue(FACE);
        Direction facing = (Direction)state.getValue((Property)FACING);
        SliderSide side = (SliderSide)((Object)state.getValue(SIDE));
        if (face == AttachFace.FLOOR) {
            return facing == Direction.EAST || facing == Direction.WEST ? CS_FLOOR_EW : CS_FLOOR_NS;
        }
        if (face == AttachFace.CEILING) {
            return facing == Direction.EAST || facing == Direction.WEST ? CS_CEILING_EW : CS_CEILING_NS;
        }
        if (side == SliderSide.BACK) {
            return switch (facing) {
                case Direction.EAST -> CS_ROTATED_WALL_E;
                case Direction.SOUTH -> CS_ROTATED_WALL_S;
                case Direction.WEST -> CS_ROTATED_WALL_W;
                default -> CS_ROTATED_WALL_N;
            };
        }
        return switch (facing) {
            case Direction.EAST -> CS_WALL_E;
            case Direction.SOUTH -> CS_WALL_S;
            case Direction.WEST -> CS_WALL_W;
            default -> CS_WALL_N;
        };
    }

    public static enum SliderSide implements StringRepresentable
    {
        FRONT("front"),
        BACK("back");

        private final String name;

        private SliderSide(String name) {
            this.name = name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }
}
