/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 */
package com.simibubi.create.content.kinetics.crafter;

import com.simibubi.create.content.kinetics.crafter.CrafterHelper;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlock;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

public static class ConnectedInputHandler.ConnectedInput {
    boolean isController = true;
    List<BlockPos> data = Collections.synchronizedList(new ArrayList());

    public ConnectedInputHandler.ConnectedInput() {
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
            ConnectedInputHandler.ConnectedInput input = CrafterHelper.getInput((BlockAndTintGetter)world, controllerPos);
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
