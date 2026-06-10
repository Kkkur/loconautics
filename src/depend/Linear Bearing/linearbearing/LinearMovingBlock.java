/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.storage.loot.LootParams$Builder
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.bearing.linearbearing;

import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LinearMovingBlock
extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    public static final EnumProperty<SliderSide> SIDE = EnumProperty.create((String)"side", SliderSide.class);

    public LinearMovingBlock(BlockBehaviour.Properties properties) {
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

    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return Collections.emptyList();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, FACE, SIDE});
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        AttachFace face = (AttachFace)state.getValue(FACE);
        Direction facing = (Direction)state.getValue((Property)FACING);
        SliderSide side = (SliderSide)((Object)state.getValue(SIDE));
        VoxelShape floorNS = Shapes.or((VoxelShape)Block.box((double)2.0, (double)3.0, (double)0.0, (double)14.0, (double)10.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)5.0, (double)10.0, (double)0.0, (double)11.0, (double)13.0, (double)16.0), Block.box((double)0.0, (double)13.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0)});
        VoxelShape floorEW = Shapes.or((VoxelShape)Block.box((double)0.0, (double)3.0, (double)2.0, (double)16.0, (double)10.0, (double)14.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)10.0, (double)5.0, (double)16.0, (double)13.0, (double)11.0), Block.box((double)0.0, (double)13.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0)});
        if (face == AttachFace.FLOOR) {
            return facing == Direction.EAST || facing == Direction.WEST ? floorEW : floorNS;
        }
        VoxelShape ceilingNS = Shapes.or((VoxelShape)Block.box((double)2.0, (double)6.0, (double)0.0, (double)14.0, (double)13.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)5.0, (double)3.0, (double)0.0, (double)11.0, (double)13.0, (double)16.0), Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)3.0, (double)16.0)});
        VoxelShape ceilingEW = Shapes.or((VoxelShape)Block.box((double)0.0, (double)6.0, (double)2.0, (double)16.0, (double)13.0, (double)14.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)3.0, (double)5.0, (double)16.0, (double)6.0, (double)11.0), Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)3.0, (double)16.0)});
        if (face == AttachFace.CEILING) {
            return facing == Direction.EAST || facing == Direction.WEST ? ceilingEW : ceilingNS;
        }
        if (side != SliderSide.BACK) {
            return switch (facing) {
                case Direction.EAST -> Shapes.or((VoxelShape)Block.box((double)3.0, (double)0.0, (double)2.0, (double)10.0, (double)16.0, (double)14.0), (VoxelShape[])new VoxelShape[]{Block.box((double)10.0, (double)0.0, (double)5.0, (double)13.0, (double)16.0, (double)11.0), Block.box((double)13.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0)});
                case Direction.SOUTH -> Shapes.or((VoxelShape)Block.box((double)2.0, (double)0.0, (double)3.0, (double)14.0, (double)16.0, (double)10.0), (VoxelShape[])new VoxelShape[]{Block.box((double)5.0, (double)0.0, (double)10.0, (double)11.0, (double)16.0, (double)13.0), Block.box((double)0.0, (double)0.0, (double)13.0, (double)16.0, (double)16.0, (double)16.0)});
                case Direction.WEST -> Shapes.or((VoxelShape)Block.box((double)6.0, (double)0.0, (double)2.0, (double)13.0, (double)16.0, (double)14.0), (VoxelShape[])new VoxelShape[]{Block.box((double)3.0, (double)0.0, (double)5.0, (double)6.0, (double)16.0, (double)11.0), Block.box((double)0.0, (double)0.0, (double)0.0, (double)3.0, (double)16.0, (double)16.0)});
                default -> Shapes.or((VoxelShape)Block.box((double)2.0, (double)0.0, (double)6.0, (double)14.0, (double)16.0, (double)13.0), (VoxelShape[])new VoxelShape[]{Block.box((double)5.0, (double)0.0, (double)3.0, (double)11.0, (double)16.0, (double)6.0), Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)3.0)});
            };
        }
        return switch (facing) {
            case Direction.EAST -> Shapes.or((VoxelShape)Block.box((double)3.0, (double)2.0, (double)0.0, (double)10.0, (double)14.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)10.0, (double)5.0, (double)0.0, (double)13.0, (double)11.0, (double)16.0), Block.box((double)13.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0)});
            case Direction.SOUTH -> Shapes.or((VoxelShape)Block.box((double)0.0, (double)2.0, (double)3.0, (double)16.0, (double)14.0, (double)10.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)5.0, (double)10.0, (double)16.0, (double)11.0, (double)13.0), Block.box((double)0.0, (double)0.0, (double)13.0, (double)16.0, (double)16.0, (double)16.0)});
            case Direction.WEST -> Shapes.or((VoxelShape)Block.box((double)6.0, (double)2.0, (double)0.0, (double)13.0, (double)14.0, (double)16.0), (VoxelShape[])new VoxelShape[]{Block.box((double)3.0, (double)5.0, (double)0.0, (double)6.0, (double)11.0, (double)16.0), Block.box((double)0.0, (double)0.0, (double)0.0, (double)3.0, (double)16.0, (double)16.0)});
            default -> Shapes.or((VoxelShape)Block.box((double)0.0, (double)2.0, (double)6.0, (double)16.0, (double)14.0, (double)13.0), (VoxelShape[])new VoxelShape[]{Block.box((double)0.0, (double)5.0, (double)3.0, (double)16.0, (double)11.0, (double)6.0), Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)16.0, (double)3.0)});
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
