/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.Level
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

public class ClockworkContraption
extends Contraption {
    protected Direction facing;
    public HandType handType;
    public int offset;
    private Set<BlockPos> ignoreBlocks = new HashSet<BlockPos>();

    @Override
    public ContraptionType getType() {
        return (ContraptionType)AllContraptionTypes.CLOCKWORK.value();
    }

    private void ignoreBlocks(Set<BlockPos> blocks, BlockPos anchor) {
        for (BlockPos blockPos : blocks) {
            this.ignoreBlocks.add(anchor.offset((Vec3i)blockPos));
        }
    }

    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return pos.equals((Object)this.anchor.relative(this.facing.getOpposite(), this.offset + 1));
    }

    public static Pair<ClockworkContraption, ClockworkContraption> assembleClockworkAt(Level world, BlockPos pos, Direction direction) throws AssemblyException {
        int hourArmBlocks = 0;
        ClockworkContraption hourArm = new ClockworkContraption();
        ClockworkContraption minuteArm = null;
        hourArm.facing = direction;
        hourArm.handType = HandType.HOUR;
        if (!hourArm.assemble(world, pos)) {
            return null;
        }
        for (int i = 0; i < 16; ++i) {
            BlockPos offsetPos = BlockPos.ZERO.relative(direction, i);
            if (hourArm.getBlocks().containsKey(offsetPos)) continue;
            hourArmBlocks = i;
            break;
        }
        if (hourArmBlocks > 0) {
            minuteArm = new ClockworkContraption();
            minuteArm.facing = direction;
            minuteArm.handType = HandType.MINUTE;
            minuteArm.offset = hourArmBlocks;
            minuteArm.ignoreBlocks(hourArm.getBlocks().keySet(), hourArm.anchor);
            if (!minuteArm.assemble(world, pos)) {
                return null;
            }
            if (minuteArm.getBlocks().isEmpty()) {
                minuteArm = null;
            }
        }
        hourArm.startMoving(world);
        hourArm.expandBoundsAroundAxis(direction.getAxis());
        if (minuteArm != null) {
            minuteArm.startMoving(world);
            minuteArm.expandBoundsAroundAxis(direction.getAxis());
        }
        return Pair.of((Object)hourArm, (Object)minuteArm);
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        return this.searchMovedStructure(world, pos, this.facing);
    }

    @Override
    public boolean searchMovedStructure(Level world, BlockPos pos, Direction direction) throws AssemblyException {
        return super.searchMovedStructure(world, pos.relative(direction, this.offset + 1), null);
    }

    @Override
    protected boolean moveBlock(Level world, Direction direction, Queue<BlockPos> frontier, Set<BlockPos> visited) throws AssemblyException {
        if (this.ignoreBlocks.contains(frontier.peek())) {
            frontier.poll();
            return true;
        }
        return super.moveBlock(world, direction, frontier, visited);
    }

    @Override
    public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
        CompoundTag tag = super.writeNBT(registries, spawnPacket);
        tag.putInt("facing", this.facing.get3DDataValue());
        tag.putInt("offset", this.offset);
        NBTHelper.writeEnum((CompoundTag)tag, (String)"HandType", (Enum)this.handType);
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag tag, boolean spawnData) {
        this.facing = Direction.from3DDataValue((int)tag.getInt("facing"));
        this.handType = (HandType)NBTHelper.readEnum((CompoundTag)tag, (String)"HandType", HandType.class);
        this.offset = tag.getInt("offset");
        super.readNBT(world, tag, spawnData);
    }

    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        if (BlockPos.ZERO.equals((Object)localPos) || BlockPos.ZERO.equals((Object)localPos.relative(facing))) {
            return false;
        }
        return facing.getAxis() == this.facing.getAxis();
    }

    public static enum HandType {
        HOUR,
        MINUTE;

    }
}
