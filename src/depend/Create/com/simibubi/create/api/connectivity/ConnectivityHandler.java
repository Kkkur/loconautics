/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.IFluidTank
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.connectivity;

import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class ConnectivityHandler {
    public static <T extends BlockEntity> void formMulti(T be) {
        SearchCache cache = new SearchCache();
        ArrayList<T> frontier = new ArrayList<T>();
        frontier.add(be);
        ConnectivityHandler.formMulti(be.getType(), (BlockGetter)be.getLevel(), cache, frontier);
    }

    private static <T extends BlockEntity> void formMulti(BlockEntityType<?> type, BlockGetter level, SearchCache<T> cache, List<T> frontier) {
        PriorityQueue<Pair<Integer, T>> creationQueue = ConnectivityHandler.makeCreationQueue();
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        Direction.Axis mainAxis = ((IMultiBlockEntityContainer)((BlockEntity)frontier.get(0))).getMainConnectionAxis();
        int minX = mainAxis == Direction.Axis.Y ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        int minY = mainAxis != Direction.Axis.Y ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        int minZ = mainAxis == Direction.Axis.Y ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (BlockEntity be : frontier) {
            BlockPos pos = be.getBlockPos();
            minX = Math.min(pos.getX(), minX);
            minY = Math.min(pos.getY(), minY);
            minZ = Math.min(pos.getZ(), minZ);
        }
        if (mainAxis == Direction.Axis.Y) {
            minX -= ((IMultiBlockEntityContainer)((BlockEntity)frontier.get(0))).getMaxWidth();
        }
        if (mainAxis != Direction.Axis.Y) {
            minY -= ((IMultiBlockEntityContainer)((BlockEntity)frontier.get(0))).getMaxWidth();
        }
        if (mainAxis == Direction.Axis.Y) {
            minZ -= ((IMultiBlockEntityContainer)((BlockEntity)frontier.get(0))).getMaxWidth();
        }
        while (!frontier.isEmpty()) {
            BlockEntity part = (BlockEntity)frontier.remove(0);
            BlockPos partPos = part.getBlockPos();
            if (visited.contains(partPos)) continue;
            visited.add(partPos);
            int amount = ConnectivityHandler.tryToFormNewMulti(part, cache, true);
            if (amount > 1) {
                creationQueue.add(Pair.of((Object)amount, (Object)part));
            }
            for (Direction.Axis axis : Iterate.axes) {
                T nextBe;
                Direction dir = Direction.get((Direction.AxisDirection)Direction.AxisDirection.NEGATIVE, (Direction.Axis)axis);
                BlockPos next = partPos.relative(dir);
                if (next.getX() <= minX || next.getY() <= minY || next.getZ() <= minZ || visited.contains(next) || (nextBe = ConnectivityHandler.partAt(type, level, next)) == null || nextBe.isRemoved()) continue;
                frontier.add(nextBe);
            }
        }
        visited.clear();
        while (!creationQueue.isEmpty()) {
            Pair<Integer, T> next = creationQueue.poll();
            BlockEntity toCreate = (BlockEntity)next.getValue();
            if (visited.contains(toCreate.getBlockPos())) continue;
            visited.add(toCreate.getBlockPos());
            ConnectivityHandler.tryToFormNewMulti(toCreate, cache, false);
        }
    }

    private static <T extends BlockEntity> int tryToFormNewMulti(T be, SearchCache<T> cache, boolean simulate) {
        int bestWidth = 1;
        int bestAmount = -1;
        if (!((IMultiBlockEntityContainer)be).isController()) {
            return 0;
        }
        int radius = ((IMultiBlockEntityContainer)be).getMaxWidth();
        for (int w = 1; w <= radius; ++w) {
            int amount = ConnectivityHandler.tryToFormNewMultiOfWidth(be, w, cache, true);
            if (amount < bestAmount) continue;
            bestWidth = w;
            bestAmount = amount;
        }
        if (!simulate) {
            IMultiBlockEntityContainer.Fluid ifluid;
            int beWidth = ((IMultiBlockEntityContainer)be).getWidth();
            if (beWidth == bestWidth && beWidth * beWidth * ((IMultiBlockEntityContainer)be).getHeight() == bestAmount) {
                return bestAmount;
            }
            ConnectivityHandler.splitMultiAndInvalidate(be, cache, false);
            if (be instanceof IMultiBlockEntityContainer.Fluid && (ifluid = (IMultiBlockEntityContainer.Fluid)be).hasTank()) {
                ifluid.setTankSize(0, bestAmount);
            }
            ConnectivityHandler.tryToFormNewMultiOfWidth(be, bestWidth, cache, false);
            ((IMultiBlockEntityContainer)be).preventConnectivityUpdate();
            ((IMultiBlockEntityContainer)be).setWidth(bestWidth);
            ((IMultiBlockEntityContainer)be).setHeight(bestAmount / bestWidth / bestWidth);
            ((IMultiBlockEntityContainer)be).notifyMultiUpdated();
        }
        return bestAmount;
    }

    private static <T extends BlockEntity> int tryToFormNewMultiOfWidth(T be, int width, SearchCache<T> cache, boolean simulate) {
        IMultiBlockEntityContainer.Fluid ifluid;
        int amount = 0;
        int height = 0;
        BlockEntityType type = be.getType();
        Level level = be.getLevel();
        if (level == null) {
            return 0;
        }
        BlockPos origin = be.getBlockPos();
        IFluidTank beTank = null;
        FluidStack fluid = FluidStack.EMPTY;
        if (be instanceof IMultiBlockEntityContainer.Fluid && (ifluid = (IMultiBlockEntityContainer.Fluid)be).hasTank()) {
            beTank = ifluid.getTank(0);
            fluid = beTank.getFluid();
        }
        Direction.Axis axis = ((IMultiBlockEntityContainer)be).getMainConnectionAxis();
        block10: for (int yOffset = 0; yOffset < ((IMultiBlockEntityContainer)be).getMaxLength(axis, width); ++yOffset) {
            for (int xOffset = 0; xOffset < width; ++xOffset) {
                for (int zOffset = 0; zOffset < width; ++zOffset) {
                    IMultiBlockEntityContainer.Fluid ifluidCon;
                    BlockPos conPos;
                    Direction.Axis conAxis;
                    BlockEntity controller;
                    int otherWidth;
                    BlockPos pos = switch (axis) {
                        default -> throw new MatchException(null, null);
                        case Direction.Axis.X -> origin.offset(yOffset, xOffset, zOffset);
                        case Direction.Axis.Y -> origin.offset(xOffset, yOffset, zOffset);
                        case Direction.Axis.Z -> origin.offset(xOffset, zOffset, yOffset);
                    };
                    Optional<T> part = cache.getOrCache(type, (BlockGetter)level, pos);
                    if (part.isEmpty() || (otherWidth = ((IMultiBlockEntityContainer)(controller = (BlockEntity)part.get())).getWidth()) > width || otherWidth == width && ((IMultiBlockEntityContainer)controller).getHeight() == ((IMultiBlockEntityContainer)be).getMaxLength(axis, width) || axis != (conAxis = ((IMultiBlockEntityContainer)controller).getMainConnectionAxis()) || !(conPos = controller.getBlockPos()).equals((Object)origin) && (axis != Direction.Axis.Y ? axis == Direction.Axis.Z && conPos.getX() < origin.getX() || conPos.getY() < origin.getY() || axis == Direction.Axis.X && conPos.getZ() < origin.getZ() || axis == Direction.Axis.Z && conPos.getX() + otherWidth > origin.getX() + width || conPos.getY() + otherWidth > origin.getY() + width || axis == Direction.Axis.X && conPos.getZ() + otherWidth > origin.getZ() + width : conPos.getX() < origin.getX() || conPos.getZ() < origin.getZ() || conPos.getX() + otherWidth > origin.getX() + width || conPos.getZ() + otherWidth > origin.getZ() + width)) break block10;
                    if (!(controller instanceof IMultiBlockEntityContainer.Fluid) || !(ifluidCon = (IMultiBlockEntityContainer.Fluid)controller).hasTank()) continue;
                    FluidStack otherFluid = ifluidCon.getFluid(0);
                    if (!fluid.isEmpty() && !otherFluid.isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)fluid, (FluidStack)otherFluid)) break block10;
                }
            }
            amount += width * width;
            ++height;
        }
        if (simulate) {
            return amount;
        }
        Object extraData = ((IMultiBlockEntityContainer)be).getExtraData();
        for (int yOffset = 0; yOffset < height; ++yOffset) {
            for (int xOffset = 0; xOffset < width; ++xOffset) {
                for (int zOffset = 0; zOffset < width; ++zOffset) {
                    IMultiBlockEntityContainer.Fluid ifluidPart;
                    BlockPos pos = switch (axis) {
                        default -> throw new MatchException(null, null);
                        case Direction.Axis.X -> origin.offset(yOffset, xOffset, zOffset);
                        case Direction.Axis.Y -> origin.offset(xOffset, yOffset, zOffset);
                        case Direction.Axis.Z -> origin.offset(xOffset, zOffset, yOffset);
                    };
                    T part = ConnectivityHandler.partAt(type, (BlockGetter)level, pos);
                    if (part == null || part == be) continue;
                    extraData = ((IMultiBlockEntityContainer)be).modifyExtraData(extraData);
                    if (part instanceof IMultiBlockEntityContainer.Fluid && (ifluidPart = (IMultiBlockEntityContainer.Fluid)part).hasTank()) {
                        IFluidTank tankAt = ifluidPart.getTank(0);
                        FluidStack fluidAt = tankAt.getFluid();
                        if (!fluidAt.isEmpty()) {
                            IMultiBlockEntityContainer.Fluid ifluidBE;
                            if (beTank != null && fluid.isEmpty() && beTank instanceof CreativeFluidTankBlockEntity.CreativeSmartFluidTank) {
                                ((CreativeFluidTankBlockEntity.CreativeSmartFluidTank)beTank).setContainedFluid(fluidAt);
                            }
                            if (be instanceof IMultiBlockEntityContainer.Fluid && (ifluidBE = (IMultiBlockEntityContainer.Fluid)be).hasTank() && beTank != null) {
                                beTank.fill(fluidAt, IFluidHandler.FluidAction.EXECUTE);
                            }
                        }
                        tankAt.drain(tankAt.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
                    }
                    ConnectivityHandler.splitMultiAndInvalidate(part, cache, false);
                    ((IMultiBlockEntityContainer)part).setController(origin);
                    ((IMultiBlockEntityContainer)part).preventConnectivityUpdate();
                    cache.put(pos, be);
                    ((IMultiBlockEntityContainer)part).setHeight(height);
                    ((IMultiBlockEntityContainer)part).setWidth(width);
                    ((IMultiBlockEntityContainer)part).notifyMultiUpdated();
                }
            }
        }
        ((IMultiBlockEntityContainer)be).setExtraData(extraData);
        return amount;
    }

    public static <T extends BlockEntity> void splitMulti(T be) {
        ConnectivityHandler.splitMultiAndInvalidate(be, null, false);
    }

    private static <T extends BlockEntity> void splitMultiAndInvalidate(T be, @Nullable SearchCache<T> cache, boolean tryReconnect) {
        IMultiBlockEntityContainer.Fluid fluid;
        IMultiBlockEntityContainer.Inventory inv;
        IMultiBlockEntityContainer.Fluid ifluidBE;
        Level level = be.getLevel();
        if (level == null) {
            return;
        }
        if ((be = ((IMultiBlockEntityContainer)be).getControllerBE()) == null) {
            return;
        }
        int height = ((IMultiBlockEntityContainer)be).getHeight();
        int width = ((IMultiBlockEntityContainer)be).getWidth();
        if (width == 1 && height == 1) {
            return;
        }
        BlockPos origin = be.getBlockPos();
        ArrayList<T> frontier = new ArrayList<T>();
        Direction.Axis axis = ((IMultiBlockEntityContainer)be).getMainConnectionAxis();
        FluidStack toDistribute = FluidStack.EMPTY;
        int maxCapacity = 0;
        if (be instanceof IMultiBlockEntityContainer.Fluid && (ifluidBE = (IMultiBlockEntityContainer.Fluid)be).hasTank()) {
            toDistribute = ifluidBE.getFluid(0);
            maxCapacity = ifluidBE.getTankSize(0);
            if (!toDistribute.isEmpty() && !be.isRemoved()) {
                toDistribute.shrink(maxCapacity);
            }
            ifluidBE.setTankSize(0, 1);
        }
        for (int yOffset = 0; yOffset < height; ++yOffset) {
            for (int xOffset = 0; xOffset < width; ++xOffset) {
                for (int zOffset = 0; zOffset < width; ++zOffset) {
                    BlockPos pos = switch (axis) {
                        default -> throw new MatchException(null, null);
                        case Direction.Axis.X -> origin.offset(yOffset, xOffset, zOffset);
                        case Direction.Axis.Y -> origin.offset(xOffset, yOffset, zOffset);
                        case Direction.Axis.Z -> origin.offset(xOffset, zOffset, yOffset);
                    };
                    T partAt = ConnectivityHandler.partAt(be.getType(), (BlockGetter)level, pos);
                    if (partAt == null || !((IMultiBlockEntityContainer)partAt).getController().equals((Object)origin)) continue;
                    Object controllerBE = ((IMultiBlockEntityContainer)partAt).getControllerBE();
                    ((IMultiBlockEntityContainer)partAt).setExtraData(controllerBE == null ? null : ((IMultiBlockEntityContainer)controllerBE).getExtraData());
                    ((IMultiBlockEntityContainer)partAt).removeController(true);
                    if (!toDistribute.isEmpty() && partAt != be) {
                        IFluidTank tank;
                        FluidStack copy = toDistribute.copy();
                        if (partAt instanceof IMultiBlockEntityContainer.Fluid) {
                            IMultiBlockEntityContainer.Fluid ifluidPart = (IMultiBlockEntityContainer.Fluid)partAt;
                            v1 = ifluidPart.getTank(0);
                        } else {
                            v1 = tank = null;
                        }
                        if (tank instanceof CreativeFluidTankBlockEntity.CreativeSmartFluidTank) {
                            CreativeFluidTankBlockEntity.CreativeSmartFluidTank creativeTank = (CreativeFluidTankBlockEntity.CreativeSmartFluidTank)tank;
                            if (creativeTank.isEmpty()) {
                                creativeTank.setContainedFluid(toDistribute);
                            }
                        } else {
                            int split = Math.min(maxCapacity, toDistribute.getAmount());
                            copy.setAmount(split);
                            toDistribute.shrink(split);
                            if (tank != null) {
                                tank.fill(copy, IFluidHandler.FluidAction.EXECUTE);
                            }
                        }
                    }
                    if (tryReconnect) {
                        frontier.add(partAt);
                        ((IMultiBlockEntityContainer)partAt).preventConnectivityUpdate();
                    }
                    if (cache == null) continue;
                    cache.put(pos, partAt);
                }
            }
        }
        if (be instanceof IMultiBlockEntityContainer.Inventory && (inv = (IMultiBlockEntityContainer.Inventory)be).hasInventory()) {
            be.getLevel().invalidateCapabilities(be.getBlockPos());
        }
        if (be instanceof IMultiBlockEntityContainer.Fluid && (fluid = (IMultiBlockEntityContainer.Fluid)be).hasTank()) {
            be.getLevel().invalidateCapabilities(be.getBlockPos());
        }
        if (tryReconnect) {
            ConnectivityHandler.formMulti(be.getType(), (BlockGetter)level, cache == null ? new SearchCache<T>() : cache, frontier);
        }
    }

    private static <T extends BlockEntity> PriorityQueue<Pair<Integer, T>> makeCreationQueue() {
        return new PriorityQueue<Pair<Integer, T>>((one, two) -> (Integer)two.getKey() - (Integer)one.getKey());
    }

    @Nullable
    public static <T extends BlockEntity> T partAt(BlockEntityType<?> type, BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && be.getType() == type && !be.isRemoved()) {
            return ConnectivityHandler.checked(be);
        }
        return null;
    }

    public static <T extends BlockEntity> boolean isConnected(BlockGetter level, BlockPos pos, BlockPos other) {
        T one = ConnectivityHandler.checked(level.getBlockEntity(pos));
        T two = ConnectivityHandler.checked(level.getBlockEntity(other));
        if (one == null || two == null) {
            return false;
        }
        return ((IMultiBlockEntityContainer)one).getController().equals((Object)((IMultiBlockEntityContainer)two).getController());
    }

    @Nullable
    private static <T extends BlockEntity> T checked(BlockEntity be) {
        if (be instanceof IMultiBlockEntityContainer) {
            return (T)be;
        }
        return null;
    }

    private static class SearchCache<T extends BlockEntity> {
        Map<BlockPos, Optional<T>> controllerMap = new HashMap<BlockPos, Optional<T>>();

        void put(BlockPos pos, T target) {
            this.controllerMap.put(pos, Optional.of(target));
        }

        void putEmpty(BlockPos pos) {
            this.controllerMap.put(pos, Optional.empty());
        }

        boolean hasVisited(BlockPos pos) {
            return this.controllerMap.containsKey(pos);
        }

        Optional<T> getOrCache(BlockEntityType<?> type, BlockGetter level, BlockPos pos) {
            if (this.hasVisited(pos)) {
                return this.controllerMap.get(pos);
            }
            Object partAt = ConnectivityHandler.partAt(type, level, pos);
            if (partAt == null) {
                this.putEmpty(pos);
                return Optional.empty();
            }
            Object controller = ConnectivityHandler.checked(level.getBlockEntity(((IMultiBlockEntityContainer)partAt).getController()));
            if (controller == null) {
                this.putEmpty(pos);
                return Optional.empty();
            }
            this.put(pos, controller);
            return Optional.of(controller);
        }
    }
}
