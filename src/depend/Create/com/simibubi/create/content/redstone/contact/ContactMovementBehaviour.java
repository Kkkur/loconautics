/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.ticks.TickPriority
 */
package com.simibubi.create.content.redstone.contact;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.redstone.contact.RedstoneContactBlock;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.TickPriority;

public class ContactMovementBehaviour
implements MovementBehaviour {
    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        return Vec3.atLowerCornerOf((Vec3i)((Direction)context.state.getValue((Property)RedstoneContactBlock.FACING)).getNormal()).scale((double)0.65f);
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        Contraption contraption;
        BlockState block = context.state;
        Level world = context.world;
        if (world.isClientSide) {
            return;
        }
        if (context.firstMovement) {
            return;
        }
        this.deactivateLastVisitedContact(context);
        BlockState visitedState = world.getBlockState(pos);
        if (!AllBlocks.REDSTONE_CONTACT.has(visitedState) && !AllBlocks.ELEVATOR_CONTACT.has(visitedState)) {
            return;
        }
        Vec3 contact = Vec3.atLowerCornerOf((Vec3i)((Direction)block.getValue((Property)RedstoneContactBlock.FACING)).getNormal());
        contact = (Vec3)context.rotation.apply(contact);
        Direction direction = Direction.getNearest((double)contact.x, (double)contact.y, (double)contact.z);
        if (visitedState.getValue((Property)RedstoneContactBlock.FACING) != direction.getOpposite()) {
            return;
        }
        if (AllBlocks.REDSTONE_CONTACT.has(visitedState)) {
            world.setBlockAndUpdate(pos, (BlockState)visitedState.setValue((Property)RedstoneContactBlock.POWERED, (Comparable)Boolean.valueOf(true)));
        }
        if (AllBlocks.ELEVATOR_CONTACT.has(visitedState) && (contraption = context.contraption) instanceof ElevatorContraption) {
            ElevatorContraption ec = (ElevatorContraption)contraption;
            ec.broadcastFloorData(world, pos);
        }
        context.data.put("lastContact", NbtUtils.writeBlockPos((BlockPos)pos));
    }

    @Override
    public void stopMoving(MovementContext context) {
        this.deactivateLastVisitedContact(context);
    }

    @Override
    public void cancelStall(MovementContext context) {
        MovementBehaviour.super.cancelStall(context);
        this.deactivateLastVisitedContact(context);
    }

    public void deactivateLastVisitedContact(MovementContext context) {
        if (!context.data.contains("lastContact")) {
            return;
        }
        BlockPos last = NBTHelper.readBlockPos((CompoundTag)context.data, (String)"lastContact");
        context.data.remove("lastContact");
        BlockState blockState = context.world.getBlockState(last);
        if (AllBlocks.REDSTONE_CONTACT.has(blockState)) {
            context.world.scheduleTick(last, (Block)AllBlocks.REDSTONE_CONTACT.get(), 1, TickPriority.NORMAL);
        }
    }
}
