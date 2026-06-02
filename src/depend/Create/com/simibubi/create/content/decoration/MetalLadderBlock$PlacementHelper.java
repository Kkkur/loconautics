/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.LadderBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.decoration;

import com.simibubi.create.content.decoration.MetalLadderBlock;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.infrastructure.config.AllConfigs;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

@MethodsReturnNonnullByDefault
private static class MetalLadderBlock.PlacementHelper
implements IPlacementHelper {
    private MetalLadderBlock.PlacementHelper() {
    }

    public Predicate<ItemStack> getItemPredicate() {
        return i -> i.getItem() instanceof BlockItem && ((BlockItem)i.getItem()).getBlock() instanceof MetalLadderBlock;
    }

    public Predicate<BlockState> getStatePredicate() {
        return s -> s.getBlock() instanceof LadderBlock;
    }

    public int attachedLadders(Level world, BlockPos pos, Direction direction) {
        BlockPos checkPos = pos.relative(direction);
        BlockState state = world.getBlockState(checkPos);
        int count = 0;
        while (this.getStatePredicate().test(state)) {
            ++count;
            checkPos = checkPos.relative(direction);
            state = world.getBlockState(checkPos);
        }
        return count;
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        int ladders;
        AttributeInstance reach;
        Direction dir = player.getXRot() < 0.0f ? Direction.UP : Direction.DOWN;
        int range = (Integer)AllConfigs.server().equipment.placementAssistRange.get();
        if (player != null && (reach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE)) != null && reach.hasModifier(ExtendoGripItem.singleRangeAttributeModifier.id())) {
            range += 4;
        }
        if ((ladders = this.attachedLadders(world, pos, dir)) >= range) {
            return PlacementOffset.fail();
        }
        BlockPos newPos = pos.relative(dir, ladders + 1);
        BlockState newState = world.getBlockState(newPos);
        if (!state.canSurvive((LevelReader)world, newPos)) {
            return PlacementOffset.fail();
        }
        if (newState.canBeReplaced()) {
            return PlacementOffset.success((Vec3i)newPos, bState -> (BlockState)bState.setValue((Property)LadderBlock.FACING, (Comparable)((Direction)state.getValue((Property)LadderBlock.FACING))));
        }
        return PlacementOffset.fail();
    }
}
