/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.placement;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@MethodsReturnNonnullByDefault
public abstract class PoleHelper<T extends Comparable<T>>
implements IPlacementHelper {
    protected final Predicate<BlockState> statePredicate;
    protected final Property<T> property;
    protected final Function<BlockState, Direction.Axis> axisFunction;

    public PoleHelper(Predicate<BlockState> statePredicate, Function<BlockState, Direction.Axis> axisFunction, Property<T> property) {
        this.statePredicate = statePredicate;
        this.axisFunction = axisFunction;
        this.property = property;
    }

    public boolean matchesAxis(BlockState state, Direction.Axis axis) {
        if (!this.statePredicate.test(state)) {
            return false;
        }
        return this.axisFunction.apply(state) == axis;
    }

    public int attachedPoles(Level world, BlockPos pos, Direction direction) {
        BlockPos checkPos = pos.relative(direction);
        BlockState state = world.getBlockState(checkPos);
        int count = 0;
        while (this.matchesAxis(state, direction.getAxis())) {
            ++count;
            checkPos = checkPos.relative(direction);
            state = world.getBlockState(checkPos);
        }
        return count;
    }

    public Predicate<BlockState> getStatePredicate() {
        return this.statePredicate;
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        List directions = IPlacementHelper.orderedByDistance((BlockPos)pos, (Vec3)ray.getLocation(), dir -> dir.getAxis() == this.axisFunction.apply(state));
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
            return PlacementOffset.success((Vec3i)newPos, bState -> (BlockState)bState.setValue(this.property, state.getValue(this.property)));
        }
        return PlacementOffset.fail();
    }
}
