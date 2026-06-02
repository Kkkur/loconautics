/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.data.WorldAttached
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.elevator;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlock;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.data.WorldAttached;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class ElevatorColumn {
    public static WorldAttached<Map<ColumnCoords, ElevatorColumn>> LOADED_COLUMNS = new WorldAttached($ -> new HashMap());
    protected LevelAccessor level;
    protected ColumnCoords coords;
    protected List<Integer> contacts;
    protected int targetedYLevel;
    protected boolean isActive;
    protected boolean targetAvailable;
    public int namesListVersion;

    @Nullable
    public static ElevatorColumn get(LevelAccessor level, ColumnCoords coords) {
        return (ElevatorColumn)((Map)LOADED_COLUMNS.get(level)).get(coords);
    }

    public static ElevatorColumn getOrCreate(LevelAccessor level, ColumnCoords coords) {
        return ((Map)LOADED_COLUMNS.get(level)).computeIfAbsent(coords, c -> new ElevatorColumn(level, (ColumnCoords)c));
    }

    public ElevatorColumn(LevelAccessor level, ColumnCoords coords) {
        this.level = level;
        this.coords = coords;
        this.contacts = new ArrayList<Integer>();
        this.targetAvailable = false;
    }

    public void markDirty() {
        for (BlockPos pos : this.getContacts()) {
            BlockEntity blockEntity = this.level.getBlockEntity(pos);
            if (!(blockEntity instanceof ElevatorContactBlockEntity)) continue;
            ElevatorContactBlockEntity ecbe = (ElevatorContactBlockEntity)blockEntity;
            ecbe.setChanged();
        }
    }

    public void floorReached(LevelAccessor level, String name) {
        this.getContacts().forEach(p -> {
            BlockEntity patt0$temp = level.getBlockEntity(p);
            if (patt0$temp instanceof ElevatorContactBlockEntity) {
                ElevatorContactBlockEntity ecbe = (ElevatorContactBlockEntity)patt0$temp;
                ecbe.updateDisplayedFloor(name);
            }
        });
    }

    public List<IntAttached<Couple<String>>> compileNamesList() {
        return this.getContacts().stream().map(p -> {
            BlockEntity patt0$temp = this.level.getBlockEntity(p);
            if (patt0$temp instanceof ElevatorContactBlockEntity) {
                ElevatorContactBlockEntity ecbe = (ElevatorContactBlockEntity)patt0$temp;
                return IntAttached.with((int)p.getY(), ecbe.getNames());
            }
            return null;
        }).filter(Objects::nonNull).toList();
    }

    public void namesChanged() {
        ++this.namesListVersion;
    }

    public Collection<BlockPos> getContacts() {
        return this.contacts.stream().map(this::contactAt).toList();
    }

    public void gatherAll() {
        BlockPos.betweenClosedStream((BlockPos)this.contactAt(this.level.getMinBuildHeight()), (BlockPos)this.contactAt(this.level.getMaxBuildHeight())).filter(p -> this.coords.equals(ElevatorContactBlock.getColumnCoords(this.level, p))).forEach(p -> this.level.setBlock(p, BlockHelper.copyProperties(this.level.getBlockState(p), AllBlocks.ELEVATOR_CONTACT.getDefaultState()), 3));
    }

    public BlockPos contactAt(int y) {
        return new BlockPos(this.coords.x, y, this.coords.z);
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
        this.markDirty();
        this.checkEmpty();
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void target(int yLevel) {
        this.targetedYLevel = yLevel;
        this.targetAvailable = true;
    }

    public boolean isTargetAvailable() {
        return this.targetAvailable;
    }

    public int getTargetedYLevel() {
        return this.targetedYLevel;
    }

    public void initNames(Level level) {
        Integer prevLevel = null;
        for (int i = 0; i < this.contacts.size(); ++i) {
            Integer y = this.contacts.get(i);
            BlockPos pos = this.contactAt(y);
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof ElevatorContactBlockEntity)) continue;
            ElevatorContactBlockEntity ecbe = (ElevatorContactBlockEntity)blockEntity;
            Integer currentLevel = null;
            if (!ecbe.shortName.isBlank()) {
                Integer tryValueOf = ElevatorColumn.tryValueOf(ecbe.shortName);
                if (tryValueOf != null) {
                    currentLevel = tryValueOf;
                }
                if (currentLevel == null) continue;
            }
            if (prevLevel != null) {
                currentLevel = prevLevel + 1;
            }
            Integer nextLevel = null;
            for (int peekI = i + 1; peekI < this.contacts.size(); ++peekI) {
                BlockPos peekPos = this.contactAt(this.contacts.get(peekI));
                BlockEntity blockEntity2 = level.getBlockEntity(peekPos);
                if (!(blockEntity2 instanceof ElevatorContactBlockEntity)) continue;
                ElevatorContactBlockEntity peekEcbe = (ElevatorContactBlockEntity)blockEntity2;
                Integer tryValueOf = ElevatorColumn.tryValueOf(peekEcbe.shortName);
                if (tryValueOf == null) continue;
                if (currentLevel != null && currentLevel >= tryValueOf) {
                    peekEcbe.shortName = "";
                    break;
                }
                nextLevel = tryValueOf;
                break;
            }
            if (currentLevel == null) {
                currentLevel = nextLevel != null ? nextLevel - 1 : 0;
            }
            ecbe.updateName(String.valueOf(currentLevel), ecbe.longName);
            prevLevel = currentLevel;
        }
    }

    private static Integer tryValueOf(String floorName) {
        try {
            return Integer.valueOf(floorName, 10);
        }
        catch (NumberFormatException nfe) {
            return null;
        }
    }

    public void add(BlockPos contactPos) {
        int index;
        int coord = contactPos.getY();
        if (this.contacts.contains(coord)) {
            return;
        }
        for (index = 0; index < this.contacts.size() && this.contacts.get(index) <= coord; ++index) {
        }
        this.contacts.add(index, coord);
        this.namesChanged();
    }

    public void remove(BlockPos contactPos) {
        this.contacts.remove((Object)contactPos.getY());
        this.checkEmpty();
        this.namesChanged();
    }

    private void checkEmpty() {
        if (this.contacts.isEmpty() && !this.isActive()) {
            ((Map)LOADED_COLUMNS.get(this.level)).remove(this.coords);
        }
    }

    public record ColumnCoords(int x, int z, Direction side) {
        public ColumnCoords relative(BlockPos anchor) {
            return new ColumnCoords(this.x + anchor.getX(), this.z + anchor.getZ(), this.side);
        }

        public CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("X", this.x);
            tag.putInt("Z", this.z);
            NBTHelper.writeEnum((CompoundTag)tag, (String)"Side", (Enum)this.side);
            return tag;
        }

        public static ColumnCoords read(CompoundTag tag) {
            int x = tag.getInt("X");
            int z = tag.getInt("Z");
            Direction side = (Direction)NBTHelper.readEnum((CompoundTag)tag, (String)"Side", Direction.class);
            return new ColumnCoords(x, z, side);
        }
    }
}
