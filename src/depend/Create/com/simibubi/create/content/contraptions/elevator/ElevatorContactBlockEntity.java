/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions.elevator;

import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlock;
import com.simibubi.create.content.decoration.slidingDoor.DoorControlBehaviour;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ElevatorContactBlockEntity
extends SmartBlockEntity {
    public DoorControlBehaviour doorControls;
    public ElevatorColumn.ColumnCoords columnCoords;
    public boolean activateBlock;
    public String shortName = "";
    public String longName = "";
    public String lastReportedCurrentFloor = "";
    private int yTargetFromNBT = Integer.MIN_VALUE;
    private boolean deferNameGenerator = false;

    public ElevatorContactBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.doorControls = new DoorControlBehaviour(this);
        behaviours.add(this.doorControls);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putString("ShortName", this.shortName);
        tag.putString("LongName", this.longName);
        if (this.lastReportedCurrentFloor != null) {
            tag.putString("LastReportedCurrentFloor", this.lastReportedCurrentFloor);
        }
        if (clientPacket) {
            return;
        }
        tag.putBoolean("Activate", this.activateBlock);
        if (this.columnCoords == null) {
            return;
        }
        ElevatorColumn column = ElevatorColumn.get((LevelAccessor)this.level, this.columnCoords);
        if (column == null) {
            return;
        }
        tag.putInt("ColumnTarget", column.getTargetedYLevel());
        if (column.isActive()) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"ColumnActive");
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.shortName = tag.getString("ShortName");
        this.longName = tag.getString("LongName");
        if (tag.contains("LastReportedCurrentFloor")) {
            this.lastReportedCurrentFloor = tag.getString("LastReportedCurrentFloor");
        }
        if (clientPacket) {
            return;
        }
        this.activateBlock = tag.getBoolean("Activate");
        if (!tag.contains("ColumnTarget")) {
            return;
        }
        int target = tag.getInt("ColumnTarget");
        boolean active = tag.contains("ColumnActive");
        if (this.columnCoords == null) {
            this.yTargetFromNBT = target;
            return;
        }
        ElevatorColumn column = ElevatorColumn.getOrCreate((LevelAccessor)this.level, this.columnCoords);
        column.target(target);
        column.setActive(active);
    }

    public void updateDisplayedFloor(String floor) {
        if (floor.equals(this.lastReportedCurrentFloor)) {
            return;
        }
        this.lastReportedCurrentFloor = floor;
        DisplayLinkBlock.notifyGatherers((LevelAccessor)this.level, this.worldPosition);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (this.level.isClientSide()) {
            return;
        }
        this.columnCoords = ElevatorContactBlock.getColumnCoords((LevelAccessor)this.level, this.worldPosition);
        if (this.columnCoords == null) {
            return;
        }
        ElevatorColumn column = ElevatorColumn.getOrCreate((LevelAccessor)this.level, this.columnCoords);
        column.add(this.worldPosition);
        if (this.shortName.isBlank()) {
            this.deferNameGenerator = true;
        }
        if (this.yTargetFromNBT == Integer.MIN_VALUE) {
            return;
        }
        column.target(this.yTargetFromNBT);
        this.yTargetFromNBT = Integer.MIN_VALUE;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.deferNameGenerator) {
            return;
        }
        if (this.columnCoords != null) {
            ElevatorColumn.getOrCreate((LevelAccessor)this.level, this.columnCoords).initNames(this.level);
        }
        this.deferNameGenerator = false;
    }

    @Override
    public void invalidate() {
        ElevatorColumn column;
        if (this.columnCoords != null && (column = ElevatorColumn.get((LevelAccessor)this.level, this.columnCoords)) != null) {
            column.remove(this.worldPosition);
        }
        super.invalidate();
    }

    public void updateName(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
        this.deferNameGenerator = false;
        this.notifyUpdate();
        ElevatorColumn column = ElevatorColumn.get((LevelAccessor)this.level, this.columnCoords);
        if (column != null) {
            column.namesChanged();
        }
    }

    public Couple<String> getNames() {
        return Couple.create((Object)this.shortName, (Object)this.longName);
    }
}
