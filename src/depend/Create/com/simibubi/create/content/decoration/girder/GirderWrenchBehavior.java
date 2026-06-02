/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.decoration.girder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class GirderWrenchBehavior {
    @OnlyIn(value=Dist.CLIENT)
    public static void tick() {
        HitResult hitResult;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || !((hitResult = mc.hitResult) instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult result = (BlockHitResult)hitResult;
        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        LocalPlayer player = mc.player;
        ItemStack heldItem = player.getMainHandItem();
        if (player.isShiftKeyDown()) {
            return;
        }
        if (!AllBlocks.METAL_GIRDER.has(world.getBlockState(pos))) {
            return;
        }
        if (!AllItems.WRENCH.isIn(heldItem)) {
            return;
        }
        Pair<Direction, Action> dirPair = GirderWrenchBehavior.getDirectionAndAction(result, (Level)world, pos);
        if (dirPair == null) {
            return;
        }
        Vec3 center = VecHelper.getCenterOf((Vec3i)pos);
        Vec3 edge = center.add(Vec3.atLowerCornerOf((Vec3i)((Direction)dirPair.getFirst()).getNormal()).scale(0.4));
        Direction.Axis[] axes = (Direction.Axis[])Arrays.stream(Iterate.axes).filter(axis -> axis != ((Direction)dirPair.getFirst()).getAxis()).toArray(Direction.Axis[]::new);
        double normalMultiplier = dirPair.getSecond() == Action.PAIR ? 4.0 : 1.0;
        Vec3 corner1 = edge.add(Vec3.atLowerCornerOf((Vec3i)Direction.fromAxisAndDirection((Direction.Axis)axes[0], (Direction.AxisDirection)Direction.AxisDirection.POSITIVE).getNormal()).scale(0.3)).add(Vec3.atLowerCornerOf((Vec3i)Direction.fromAxisAndDirection((Direction.Axis)axes[1], (Direction.AxisDirection)Direction.AxisDirection.POSITIVE).getNormal()).scale(0.3)).add(Vec3.atLowerCornerOf((Vec3i)((Direction)dirPair.getFirst()).getNormal()).scale(0.1 * normalMultiplier));
        normalMultiplier = dirPair.getSecond() == Action.HORIZONTAL ? 9.0 : 2.0;
        Vec3 corner2 = edge.add(Vec3.atLowerCornerOf((Vec3i)Direction.fromAxisAndDirection((Direction.Axis)axes[0], (Direction.AxisDirection)Direction.AxisDirection.NEGATIVE).getNormal()).scale(0.3)).add(Vec3.atLowerCornerOf((Vec3i)Direction.fromAxisAndDirection((Direction.Axis)axes[1], (Direction.AxisDirection)Direction.AxisDirection.NEGATIVE).getNormal()).scale(0.3)).add(Vec3.atLowerCornerOf((Vec3i)((Direction)dirPair.getFirst()).getOpposite().getNormal()).scale(0.1 * normalMultiplier));
        Outliner.getInstance().showAABB((Object)"girderWrench", new AABB(corner1, corner2)).lineWidth(0.03125f).colored(new Color(127, 127, 127));
    }

    @Nullable
    private static Pair<Direction, Action> getDirectionAndAction(BlockHitResult result, Level world, BlockPos pos) {
        List<Pair<Direction, Action>> validDirections = GirderWrenchBehavior.getValidDirections((BlockGetter)world, pos);
        if (validDirections.isEmpty()) {
            return null;
        }
        List directions = IPlacementHelper.orderedByDistance((BlockPos)pos, (Vec3)result.getLocation(), validDirections.stream().map(Pair::getFirst).toList());
        if (directions.isEmpty()) {
            return null;
        }
        Direction dir = (Direction)directions.get(0);
        return validDirections.stream().filter(pair -> pair.getFirst() == dir).findFirst().orElseGet(() -> Pair.of((Object)dir, (Object)((Object)Action.SINGLE)));
    }

    public static List<Pair<Direction, Action>> getValidDirections(BlockGetter level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        if (!AllBlocks.METAL_GIRDER.has(blockState)) {
            return Collections.emptyList();
        }
        return Arrays.stream(Iterate.directions).mapMulti((direction, consumer) -> {
            BlockState other = level.getBlockState(pos.relative(direction));
            if (!((Boolean)blockState.getValue((Property)GirderBlock.X)).booleanValue() && !((Boolean)blockState.getValue((Property)GirderBlock.Z)).booleanValue()) {
                return;
            }
            if (direction.getAxis() == Direction.Axis.Y) {
                if (!AllBlocks.METAL_GIRDER.has(other)) {
                    if ((Boolean)blockState.getValue((Property)GirderBlock.X) == false ^ (Boolean)blockState.getValue((Property)GirderBlock.Z) == false) {
                        consumer.accept(Pair.of((Object)direction, (Object)((Object)Action.SINGLE)));
                    }
                    return;
                }
                if (blockState.getValue((Property)GirderBlock.X) == blockState.getValue((Property)GirderBlock.Z)) {
                    return;
                }
                if (other.getValue((Property)GirderBlock.X) == other.getValue((Property)GirderBlock.Z)) {
                    return;
                }
                consumer.accept(Pair.of((Object)direction, (Object)((Object)Action.PAIR)));
                return;
            }
        }).toList();
    }

    public static boolean handleClick(Level level, BlockPos pos, BlockState state, BlockHitResult result) {
        Pair<Direction, Action> dirPair = GirderWrenchBehavior.getDirectionAndAction(result, level, pos);
        if (dirPair == null) {
            return false;
        }
        if (level.isClientSide) {
            return true;
        }
        if (!((Boolean)state.getValue((Property)GirderBlock.X)).booleanValue() && !((Boolean)state.getValue((Property)GirderBlock.Z)).booleanValue()) {
            return false;
        }
        Direction dir = (Direction)dirPair.getFirst();
        BlockPos otherPos = pos.relative(dir);
        BlockState other = level.getBlockState(otherPos);
        if (dir == Direction.UP) {
            level.setBlock(pos, GirderWrenchBehavior.postProcess((BlockState)state.cycle((Property)GirderBlock.TOP)), 18);
            if (dirPair.getSecond() == Action.PAIR && AllBlocks.METAL_GIRDER.has(other)) {
                level.setBlock(otherPos, GirderWrenchBehavior.postProcess((BlockState)other.cycle((Property)GirderBlock.BOTTOM)), 18);
            }
            return true;
        }
        if (dir == Direction.DOWN) {
            level.setBlock(pos, GirderWrenchBehavior.postProcess((BlockState)state.cycle((Property)GirderBlock.BOTTOM)), 18);
            if (dirPair.getSecond() == Action.PAIR && AllBlocks.METAL_GIRDER.has(other)) {
                level.setBlock(otherPos, GirderWrenchBehavior.postProcess((BlockState)other.cycle((Property)GirderBlock.TOP)), 18);
            }
            return true;
        }
        return true;
    }

    private static BlockState postProcess(BlockState newState) {
        if (((Boolean)newState.getValue((Property)GirderBlock.TOP)).booleanValue() && ((Boolean)newState.getValue((Property)GirderBlock.BOTTOM)).booleanValue()) {
            return newState;
        }
        if (newState.getValue(GirderBlock.AXIS) != Direction.Axis.Y) {
            return newState;
        }
        return (BlockState)newState.setValue(GirderBlock.AXIS, (Comparable)((Boolean)newState.getValue((Property)GirderBlock.X) != false ? Direction.Axis.X : Direction.Axis.Z));
    }

    private static enum Action {
        SINGLE,
        PAIR,
        HORIZONTAL;

    }
}
