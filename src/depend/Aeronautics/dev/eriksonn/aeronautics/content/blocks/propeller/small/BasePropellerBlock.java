/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.small;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
import dev.eriksonn.aeronautics.index.AeroBlockShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BasePropellerBlock
extends DirectionalKineticBlock
implements IBE<BasePropellerBlockEntity> {
    public static final BooleanProperty REVERSED = BooleanProperty.create((String)"reversed");

    public BasePropellerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.getStateDefinition().any()).setValue((Property)REVERSED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{REVERSED});
        super.createBlockStateDefinition(builder);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredFacing = this.getPreferredFacing(context);
        if (preferredFacing == null) {
            preferredFacing = context.getClickedFace().getOpposite();
        }
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)(context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? preferredFacing : preferredFacing.getOpposite()));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AeroBlockShapes.PROPELLER.get((Direction)pState.getValue((Property)FACING));
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue((Property)FACING)).getAxis();
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == ((Direction)state.getValue((Property)FACING)).getOpposite();
    }

    public Class<BasePropellerBlockEntity> getBlockEntityClass() {
        return BasePropellerBlockEntity.class;
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        block3: {
            block2: {
                Vec3 diff = context.getClickLocation().subtract(context.getClickedPos().getCenter());
                Direction facing = (Direction)state.getValue((Property)FACING);
                Vec3i normal = facing.getNormal();
                if (context.getClickedFace() == facing) break block2;
                Vec3 vec3 = new Vec3((double)normal.getX(), (double)normal.getY(), (double)normal.getZ());
                if (!(diff.dot(vec3) > 0.0)) break block3;
            }
            state = (BlockState)state.cycle((Property)REVERSED);
            context.getLevel().setBlock(context.getClickedPos(), state, 3);
            IWrenchable.playRotateSound((Level)context.getLevel(), (BlockPos)context.getClickedPos());
            return InteractionResult.SUCCESS;
        }
        return super.onWrenched(state, context);
    }
}
