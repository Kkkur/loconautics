/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
 *  com.simibubi.create.foundation.placement.PoleHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package dev.simulated_team.simulated.content.blocks.auger_shaft;

import com.google.common.base.Predicates;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.placement.PoleHelper;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlock;
import dev.simulated_team.simulated.index.SimBlocks;
import java.util.function.Predicate;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

@MethodsReturnNonnullByDefault
private static class AugerShaftBlock.PlacementHelper
extends PoleHelper<Direction.Axis> {
    private AugerShaftBlock.PlacementHelper() {
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
