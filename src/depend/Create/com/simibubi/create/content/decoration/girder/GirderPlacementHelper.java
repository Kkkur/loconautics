/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.decoration.girder;

import com.google.common.base.Predicates;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.decoration.girder.GirderEncasedShaftBlock;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class GirderPlacementHelper
implements IPlacementHelper {
    public Predicate<ItemStack> getItemPredicate() {
        return arg_0 -> AllBlocks.METAL_GIRDER.isIn(arg_0);
    }

    public Predicate<BlockState> getStatePredicate() {
        return Predicates.or(arg_0 -> AllBlocks.METAL_GIRDER.has(arg_0), arg_0 -> AllBlocks.METAL_GIRDER_ENCASED_SHAFT.has(arg_0));
    }

    private boolean canExtendToward(BlockState state, Direction side) {
        Direction.Axis axis = side.getAxis();
        if (state.getBlock() instanceof GirderBlock) {
            boolean x = (Boolean)state.getValue((Property)GirderBlock.X);
            boolean z = (Boolean)state.getValue((Property)GirderBlock.Z);
            if (!x && !z) {
                return axis == Direction.Axis.Y;
            }
            if (x && z) {
                return true;
            }
            return axis == (x ? Direction.Axis.X : Direction.Axis.Z);
        }
        if (state.getBlock() instanceof GirderEncasedShaftBlock) {
            return axis != Direction.Axis.Y && axis != state.getValue(GirderEncasedShaftBlock.HORIZONTAL_AXIS);
        }
        return false;
    }

    private int attachedPoles(Level world, BlockPos pos, Direction direction) {
        BlockPos checkPos = pos.relative(direction);
        BlockState state = world.getBlockState(checkPos);
        int count = 0;
        while (this.canExtendToward(state, direction)) {
            ++count;
            checkPos = checkPos.relative(direction);
            state = world.getBlockState(checkPos);
        }
        return count;
    }

    private BlockState withAxis(BlockState state, Direction.Axis axis) {
        if (state.getBlock() instanceof GirderBlock) {
            return (BlockState)((BlockState)((BlockState)state.setValue((Property)GirderBlock.X, (Comparable)Boolean.valueOf(axis == Direction.Axis.X))).setValue((Property)GirderBlock.Z, (Comparable)Boolean.valueOf(axis == Direction.Axis.Z))).setValue(GirderBlock.AXIS, (Comparable)axis);
        }
        if (state.getBlock() instanceof GirderEncasedShaftBlock && axis.isHorizontal()) {
            return (BlockState)state.setValue(GirderEncasedShaftBlock.HORIZONTAL_AXIS, (Comparable)(axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X));
        }
        return state;
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        List directions = IPlacementHelper.orderedByDistance((BlockPos)pos, (Vec3)ray.getLocation(), dir -> this.canExtendToward(state, (Direction)dir));
        for (Direction dir2 : directions) {
            BlockPos newPos;
            BlockState newState;
            int poles;
            AttributeInstance reach;
            int range = (Integer)AllConfigs.server().equipment.placementAssistRange.get();
            if (player != null && (reach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE)) != null && reach.hasModifier(ExtendoGripItem.singleRangeAttributeModifier.id())) {
                range += 4;
            }
            if ((poles = this.attachedPoles(world, pos, dir2)) >= range || !(newState = world.getBlockState(newPos = pos.relative(dir2, poles + 1))).canBeReplaced()) continue;
            return PlacementOffset.success((Vec3i)newPos, bState -> Block.updateFromNeighbourShapes((BlockState)this.withAxis((BlockState)bState, dir2.getAxis()), (LevelAccessor)world, (BlockPos)newPos));
        }
        return PlacementOffset.fail();
    }
}
