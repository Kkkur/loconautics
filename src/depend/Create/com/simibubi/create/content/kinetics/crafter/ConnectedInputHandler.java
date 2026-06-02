/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 */
package com.simibubi.create.content.kinetics.crafter;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.crafter.CrafterHelper;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlock;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

public class ConnectedInputHandler {
    public static boolean shouldConnect(Level world, BlockPos pos, Direction face, Direction direction) {
        BlockState refState = world.getBlockState(pos);
        if (!refState.hasProperty(HorizontalKineticBlock.HORIZONTAL_FACING)) {
            return false;
        }
        Direction refDirection = (Direction)refState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING);
        if (direction.getAxis() == refDirection.getAxis()) {
            return false;
        }
        if (face == refDirection) {
            return false;
        }
        BlockState neighbour = world.getBlockState(pos.relative(direction));
        if (!AllBlocks.MECHANICAL_CRAFTER.has(neighbour)) {
            return false;
        }
        return refDirection == neighbour.getValue(HorizontalKineticBlock.HORIZONTAL_FACING);
    }

    public static void toggleConnection(Level world, BlockPos pos, BlockPos pos2) {
        BlockPos controllerPos2;
        MechanicalCrafterBlockEntity crafter1 = CrafterHelper.getCrafter((BlockAndTintGetter)world, pos);
        MechanicalCrafterBlockEntity crafter2 = CrafterHelper.getCrafter((BlockAndTintGetter)world, pos2);
        if (crafter1 == null || crafter2 == null) {
            return;
        }
        BlockPos controllerPos1 = crafter1.getBlockPos().offset((Vec3i)crafter1.input.data.get(0));
        if (controllerPos1.equals((Object)(controllerPos2 = crafter2.getBlockPos().offset((Vec3i)crafter2.input.data.get(0))))) {
            MechanicalCrafterBlockEntity controller = CrafterHelper.getCrafter((BlockAndTintGetter)world, controllerPos1);
            Set<BlockPos> positions = controller.input.data.stream().map(arg_0 -> ((BlockPos)controllerPos1).offset(arg_0)).collect(Collectors.toSet());
            LinkedList<BlockPos> frontier = new LinkedList<BlockPos>();
            ArrayList<BlockPos> splitGroup = new ArrayList<BlockPos>();
            frontier.add(pos2);
            positions.remove(pos2);
            positions.remove(pos);
            while (!frontier.isEmpty()) {
                BlockPos current = (BlockPos)frontier.remove(0);
                for (Direction direction : Iterate.directions) {
                    BlockPos next = current.relative(direction);
                    if (!positions.remove(next)) continue;
                    splitGroup.add(next);
                    frontier.add(next);
                }
            }
            ConnectedInputHandler.initAndAddAll(world, crafter1, positions);
            ConnectedInputHandler.initAndAddAll(world, crafter2, splitGroup);
            crafter1.setChanged();
            crafter1.connectivityChanged();
            crafter2.setChanged();
            crafter2.connectivityChanged();
            return;
        }
        if (!crafter1.input.isController) {
            crafter1 = CrafterHelper.getCrafter((BlockAndTintGetter)world, controllerPos1);
        }
        if (!crafter2.input.isController) {
            crafter2 = CrafterHelper.getCrafter((BlockAndTintGetter)world, controllerPos2);
        }
        if (crafter1 == null || crafter2 == null) {
            return;
        }
        ConnectedInputHandler.connectControllers(world, crafter1, crafter2);
        world.setBlock(crafter1.getBlockPos(), crafter1.getBlockState(), 3);
        crafter1.setChanged();
        crafter1.connectivityChanged();
        crafter2.setChanged();
        crafter2.connectivityChanged();
    }

    public static void initAndAddAll(Level world, MechanicalCrafterBlockEntity crafter, Collection<BlockPos> positions) {
        crafter.input = new ConnectedInput();
        positions.forEach(splitPos -> ConnectedInputHandler.modifyAndUpdate(world, splitPos, input -> {
            input.attachTo(crafter.getBlockPos(), (BlockPos)splitPos);
            crafter.input.data.add(splitPos.subtract((Vec3i)crafter.getBlockPos()));
        }));
    }

    public static void connectControllers(Level world, MechanicalCrafterBlockEntity crafter1, MechanicalCrafterBlockEntity crafter2) {
        crafter1.input.data.forEach(offset -> {
            BlockPos connectedPos = crafter1.getBlockPos().offset((Vec3i)offset);
            ConnectedInputHandler.modifyAndUpdate(world, connectedPos, input -> {});
        });
        crafter2.input.data.forEach(offset -> {
            if (offset.equals((Object)BlockPos.ZERO)) {
                return;
            }
            BlockPos connectedPos = crafter2.getBlockPos().offset((Vec3i)offset);
            ConnectedInputHandler.modifyAndUpdate(world, connectedPos, input -> {
                input.attachTo(crafter1.getBlockPos(), connectedPos);
                crafter1.input.data.add(BlockPos.ZERO.subtract((Vec3i)input.data.get(0)));
            });
        });
        crafter2.input.attachTo(crafter1.getBlockPos(), crafter2.getBlockPos());
        crafter1.input.data.add(BlockPos.ZERO.subtract((Vec3i)crafter2.input.data.get(0)));
    }

    private static void modifyAndUpdate(Level world, BlockPos pos, Consumer<ConnectedInput> callback) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof MechanicalCrafterBlockEntity)) {
            return;
        }
        MechanicalCrafterBlockEntity crafter = (MechanicalCrafterBlockEntity)blockEntity;
        callback.accept(crafter.input);
        crafter.setChanged();
        crafter.connectivityChanged();
    }

    public static class ConnectedInput {
        boolean isController = true;
        List<BlockPos> data = Collections.synchronizedList(new ArrayList());

        public ConnectedInput() {
            this.data.add(BlockPos.ZERO);
        }

        public void attachTo(BlockPos controllerPos, BlockPos myPos) {
            this.isController = false;
            this.data.clear();
            this.data.add(controllerPos.subtract((Vec3i)myPos));
        }

        public IItemHandler getItemHandler(Level world, BlockPos pos) {
            List<MechanicalCrafterBlockEntity.Inventory> inventories = this.getInventories(world, pos);
            return new CombinedInvWrapper((IItemHandlerModifiable[])inventories.toArray(IItemHandlerModifiable[]::new));
        }

        public List<MechanicalCrafterBlockEntity.Inventory> getInventories(Level world, BlockPos pos) {
            if (!this.isController) {
                BlockPos controllerPos = pos.offset((Vec3i)this.data.get(0));
                ConnectedInput input = CrafterHelper.getInput((BlockAndTintGetter)world, controllerPos);
                if (input == this || input == null || !input.isController) {
                    return List.of();
                }
                return input.getInventories(world, controllerPos);
            }
            Direction facing = Direction.SOUTH;
            BlockState blockState = world.getBlockState(pos);
            if (blockState.hasProperty(MechanicalCrafterBlock.HORIZONTAL_FACING)) {
                facing = (Direction)blockState.getValue(MechanicalCrafterBlock.HORIZONTAL_FACING);
            }
            Direction.AxisDirection axisDirection = facing.getAxisDirection();
            Direction.Axis compareAxis = facing.getClockWise().getAxis();
            Comparator invOrdering = (p1, p2) -> {
                int compareY = -Integer.compare(p1.getY(), p2.getY());
                int modifier = axisDirection.getStep() * (compareAxis == Direction.Axis.Z ? -1 : 1);
                int c1 = compareAxis.choose(p1.getX(), p1.getY(), p1.getZ());
                int c2 = compareAxis.choose(p2.getX(), p2.getY(), p2.getZ());
                return compareY != 0 ? compareY : modifier * Integer.compare(c1, c2);
            };
            return this.data.stream().sorted(invOrdering).map(l -> CrafterHelper.getCrafter((BlockAndTintGetter)world, pos.offset((Vec3i)l))).filter(Objects::nonNull).map(MechanicalCrafterBlockEntity::getInventory).collect(Collectors.toList());
        }

        public void write(CompoundTag nbt) {
            nbt.putBoolean("Controller", this.isController);
            ListTag list = new ListTag();
            this.data.forEach(pos -> {
                CompoundTag data = new CompoundTag();
                data.putInt("X", pos.getX());
                data.putInt("Y", pos.getY());
                data.putInt("Z", pos.getZ());
                list.add((Object)data);
            });
            nbt.put("Data", (Tag)list);
        }

        public void read(CompoundTag nbt) {
            this.isController = nbt.getBoolean("Controller");
            this.data = NBTHelper.readCompoundList((ListTag)nbt.getList("Data", 10), c -> new BlockPos(c.getInt("X"), c.getInt("Y"), c.getInt("Z")));
            if (this.data.isEmpty()) {
                this.isController = true;
                this.data.add(BlockPos.ZERO);
            }
        }
    }
}
