/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BambooStalkBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CactusBlock
 *  net.minecraft.world.level.block.ChorusFlowerBlock
 *  net.minecraft.world.level.block.ChorusPlantBlock
 *  net.minecraft.world.level.block.KelpBlock
 *  net.minecraft.world.level.block.KelpPlantBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.SugarCaneBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.saw;

import com.simibubi.create.AllTags;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.dynamictrees.DynamicTree;
import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.KelpPlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TreeCutter {
    public static final Tree NO_TREE = new Tree(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    public static boolean canDynamicTreeCutFrom(Block startBlock) {
        return Mods.DYNAMICTREES.runIfInstalled(() -> () -> DynamicTree.isDynamicBranch(startBlock)).orElse(false);
    }

    @NotNull
    public static Optional<AbstractBlockBreakQueue> findDynamicTree(Block startBlock, BlockPos pos) {
        if (TreeCutter.canDynamicTreeCutFrom(startBlock)) {
            return Mods.DYNAMICTREES.runIfInstalled(() -> () -> new DynamicTree(pos));
        }
        return Optional.empty();
    }

    @NotNull
    public static Tree findTree(@Nullable BlockGetter reader, BlockPos pos, BlockState brokenState) {
        if (reader == null) {
            return NO_TREE;
        }
        ArrayList<BlockPos> logs = new ArrayList<BlockPos>();
        ArrayList<BlockPos> leaves = new ArrayList<BlockPos>();
        ArrayList<BlockPos> attachments = new ArrayList<BlockPos>();
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        LinkedList<BlockPos> frontier = new LinkedList<BlockPos>();
        BlockState stateAbove = reader.getBlockState(pos.above());
        if (TreeCutter.isVerticalPlant(brokenState)) {
            BlockPos current;
            if (!TreeCutter.isVerticalPlant(stateAbove)) {
                return NO_TREE;
            }
            logs.add(pos.above());
            for (int i = 1; i < reader.getHeight() && TreeCutter.isVerticalPlant(reader.getBlockState(current = pos.above(i))); ++i) {
                logs.add(current);
            }
            Collections.reverse(logs);
            return new Tree(logs, leaves, attachments);
        }
        if (TreeCutter.isChorus(brokenState)) {
            if (!TreeCutter.isChorus(stateAbove)) {
                return NO_TREE;
            }
            frontier.add(pos.above());
            while (!frontier.isEmpty()) {
                BlockPos current = (BlockPos)frontier.remove(0);
                visited.add(current);
                logs.add(current);
                for (Direction direction : Iterate.directions) {
                    BlockPos offset = current.relative(direction);
                    if (visited.contains(offset) || !TreeCutter.isChorus(reader.getBlockState(offset))) continue;
                    frontier.add(offset);
                }
            }
            Collections.reverse(logs);
            return new Tree(logs, leaves, attachments);
        }
        if (!TreeCutter.validateCut(reader, pos)) {
            return NO_TREE;
        }
        visited.add(pos);
        BlockPos.betweenClosedStream((BlockPos)pos.offset(-1, 0, -1), (BlockPos)pos.offset(1, 1, 1)).forEach(p -> frontier.add(new BlockPos((Vec3i)p)));
        boolean hasRoots = false;
        while (!frontier.isEmpty()) {
            BlockPos currentPos2 = (BlockPos)frontier.remove(0);
            if (!visited.add(currentPos2)) continue;
            BlockState currentState = reader.getBlockState(currentPos2);
            if (TreeCutter.isRoot(currentState)) {
                hasRoots = true;
            } else if (!TreeCutter.isLog(currentState)) continue;
            logs.add(currentPos2);
            TreeCutter.forNeighbours(currentPos2, visited, SearchDirection.UP, p -> frontier.add(new BlockPos((Vec3i)p)));
        }
        visited.clear();
        visited.addAll(logs);
        frontier.addAll(logs);
        if (hasRoots) {
            HashSet<BlockPos> oldLogs = new HashSet<BlockPos>(logs);
            while (!frontier.isEmpty()) {
                BlockPos currentPos3 = (BlockPos)frontier.remove(0);
                BlockState currentState = reader.getBlockState(currentPos3);
                if (!TreeCutter.isRoot(currentState)) continue;
                if (!oldLogs.contains(currentPos3)) {
                    logs.add(currentPos3);
                }
                TreeCutter.forNeighbours(currentPos3, visited, SearchDirection.DOWN, p -> {
                    BlockPos neighbourPos = p.immutable();
                    if (visited.add(neighbourPos)) {
                        frontier.add(neighbourPos);
                    }
                });
            }
            visited.clear();
            visited.addAll(logs);
            frontier.addAll(logs);
        }
        while (!frontier.isEmpty()) {
            BlockPos prevPos = (BlockPos)frontier.remove(0);
            BlockState prevState = reader.getBlockState(prevPos);
            int prevLeafDistance = TreeCutter.isLeaf(prevState) ? TreeCutter.getLeafDistance(prevState) : 0;
            TreeCutter.forNeighbours(prevPos, visited, SearchDirection.BOTH, currentPos -> {
                BlockState state = reader.getBlockState(currentPos);
                BlockPos subtract = currentPos.subtract((Vec3i)pos);
                BlockPos currentPosImmutable = currentPos.immutable();
                if (AllTags.AllBlockTags.TREE_ATTACHMENTS.matches(state)) {
                    attachments.add(currentPosImmutable);
                    visited.add(currentPosImmutable);
                    return;
                }
                int horizontalDistance = Math.max(Math.abs(subtract.getX()), Math.abs(subtract.getZ()));
                if (horizontalDistance <= TreeCutter.nonDecayingLeafDistance(state) && visited.add(currentPosImmutable)) {
                    leaves.add(currentPosImmutable);
                    frontier.add(currentPosImmutable);
                    return;
                }
                if (TreeCutter.isLeaf(state) && TreeCutter.getLeafDistance(state) > prevLeafDistance && visited.add(currentPosImmutable)) {
                    leaves.add(currentPosImmutable);
                    frontier.add(currentPosImmutable);
                    return;
                }
            });
        }
        return new Tree(logs, leaves, attachments);
    }

    private static int getLeafDistance(BlockState state) {
        IntegerProperty distanceProperty = LeavesBlock.DISTANCE;
        for (Property property : state.getValues().keySet()) {
            if (!(property instanceof IntegerProperty)) continue;
            IntegerProperty ip = (IntegerProperty)property;
            if (!property.getName().equals("distance")) continue;
            distanceProperty = ip;
        }
        return (Integer)state.getValue((Property)distanceProperty);
    }

    public static boolean isChorus(BlockState stateAbove) {
        return stateAbove.getBlock() instanceof ChorusPlantBlock || stateAbove.getBlock() instanceof ChorusFlowerBlock;
    }

    public static boolean isVerticalPlant(BlockState stateAbove) {
        Block block = stateAbove.getBlock();
        if (block instanceof BambooStalkBlock) {
            return true;
        }
        if (block instanceof CactusBlock) {
            return true;
        }
        if (block instanceof SugarCaneBlock) {
            return true;
        }
        if (block instanceof KelpPlantBlock) {
            return true;
        }
        return block instanceof KelpBlock;
    }

    private static boolean validateCut(BlockGetter reader, BlockPos pos) {
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        LinkedList<BlockPos> frontier = new LinkedList<BlockPos>();
        frontier.add(pos);
        frontier.add(pos.above());
        int posY = pos.getY();
        while (!frontier.isEmpty()) {
            BlockPos currentPos = (BlockPos)frontier.remove(0);
            BlockPos belowPos = currentPos.below();
            visited.add(currentPos);
            boolean lowerLayer = currentPos.getY() == posY;
            BlockState currentState = reader.getBlockState(currentPos);
            BlockState belowState = reader.getBlockState(belowPos);
            if (!TreeCutter.isLog(currentState) && !TreeCutter.isRoot(currentState)) continue;
            if (!lowerLayer && !pos.equals((Object)belowPos) && (TreeCutter.isLog(belowState) || TreeCutter.isRoot(belowState))) {
                return false;
            }
            for (Direction direction : Iterate.directions) {
                BlockPos offset;
                if (direction == Direction.DOWN || direction == Direction.UP && !lowerLayer || visited.contains(offset = currentPos.relative(direction))) continue;
                frontier.add(offset);
            }
        }
        return true;
    }

    private static void forNeighbours(BlockPos pos, Set<BlockPos> visited, SearchDirection direction, Consumer<BlockPos> acceptor) {
        BlockPos.betweenClosedStream((BlockPos)pos.offset(-1, direction.minY, -1), (BlockPos)pos.offset(1, direction.maxY, 1)).filter(((Predicate<BlockPos>)visited::contains).negate()).forEach(acceptor);
    }

    public static boolean isRoot(BlockState state) {
        return AllTags.AllBlockTags.ROOTS.matches(state);
    }

    public static boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS) || AllTags.AllBlockTags.SLIMY_LOGS.matches(state) || state.is(Blocks.MUSHROOM_STEM);
    }

    private static int nonDecayingLeafDistance(BlockState state) {
        if (state.is(Blocks.RED_MUSHROOM_BLOCK)) {
            return 2;
        }
        if (state.is(Blocks.BROWN_MUSHROOM_BLOCK)) {
            return 3;
        }
        if (state.is(BlockTags.WART_BLOCKS) || state.is(Blocks.WEEPING_VINES) || state.is(Blocks.WEEPING_VINES_PLANT)) {
            return 3;
        }
        return -1;
    }

    private static boolean isLeaf(BlockState state) {
        for (Property property : state.getValues().keySet()) {
            if (!(property instanceof IntegerProperty) || !property.getName().equals("distance") || property == BlockStateProperties.STABILITY_DISTANCE) continue;
            return true;
        }
        return false;
    }

    public static class Tree
    extends AbstractBlockBreakQueue {
        private final List<BlockPos> logs;
        private final List<BlockPos> leaves;
        private final List<BlockPos> attachments;

        public Tree(List<BlockPos> logs, List<BlockPos> leaves, List<BlockPos> attachments) {
            this.logs = logs;
            this.leaves = leaves;
            this.attachments = attachments;
        }

        @Override
        public void destroyBlocks(Level world, ItemStack toDamage, @Nullable Player playerEntity, BiConsumer<BlockPos, ItemStack> drop) {
            this.attachments.forEach(this.makeCallbackFor(world, 0.03125f, toDamage, playerEntity, drop));
            this.logs.forEach(this.makeCallbackFor(world, 0.5f, toDamage, playerEntity, drop));
            this.leaves.forEach(this.makeCallbackFor(world, 0.125f, toDamage, playerEntity, drop));
        }
    }

    private static enum SearchDirection {
        UP(0, 1),
        DOWN(-1, 0),
        BOTH(-1, 1);

        int minY;
        int maxY;

        private SearchDirection(int minY, int maxY) {
            this.minY = minY;
            this.maxY = maxY;
        }
    }
}
