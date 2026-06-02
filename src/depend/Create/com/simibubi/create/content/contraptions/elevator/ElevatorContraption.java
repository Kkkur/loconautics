/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  org.apache.commons.lang3.tuple.MutablePair
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.contraptions.elevator;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity;
import com.simibubi.create.content.contraptions.elevator.ElevatorFloorListPacket;
import com.simibubi.create.content.contraptions.pulley.PulleyContraption;
import com.simibubi.create.content.redstone.contact.RedstoneContactBlock;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ElevatorContraption
extends PulleyContraption {
    protected ElevatorColumn.ColumnCoords column;
    protected int contactYOffset;
    public boolean arrived;
    private int namesListVersion = -1;
    public List<IntAttached<Couple<String>>> namesList = ImmutableList.of();
    public int clientYTarget;
    public int maxContactY;
    public int minContactY;
    private int contacts;

    public ElevatorContraption() {
    }

    public ElevatorContraption(int initialOffset) {
        super(initialOffset);
    }

    @Override
    public void tickStorage(AbstractContraptionEntity entity) {
        super.tickStorage(entity);
        if (entity.tickCount % 10 != 0) {
            return;
        }
        ElevatorColumn.ColumnCoords coords = this.getGlobalColumn();
        ElevatorColumn column = ElevatorColumn.get((LevelAccessor)entity.level(), coords);
        if (column == null) {
            return;
        }
        if (column.namesListVersion == this.namesListVersion) {
            return;
        }
        this.namesList = column.compileNamesList();
        this.namesListVersion = column.namesListVersion;
        CatnipServices.NETWORK.sendToClientsTrackingEntity((Entity)entity, (CustomPacketPayload)new ElevatorFloorListPacket(entity, this.namesList));
    }

    @Override
    protected void disableActorOnStart(MovementContext context) {
    }

    public ElevatorColumn.ColumnCoords getGlobalColumn() {
        return this.column.relative(this.anchor);
    }

    public Integer getCurrentTargetY(Level level) {
        ElevatorColumn.ColumnCoords coords = this.getGlobalColumn();
        ElevatorColumn column = ElevatorColumn.get((LevelAccessor)level, coords);
        if (column == null) {
            return null;
        }
        if (!column.isTargetAvailable()) {
            return null;
        }
        int targetedYLevel = column.getTargetedYLevel();
        if (this.isTargetUnreachable(targetedYLevel)) {
            return null;
        }
        return targetedYLevel;
    }

    public boolean isTargetUnreachable(int contactY) {
        return contactY < this.minContactY || contactY > this.maxContactY;
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        if (!this.searchMovedStructure(world, pos, null)) {
            return false;
        }
        if (this.blocks.size() <= 0) {
            return false;
        }
        if (this.contacts == 0) {
            throw new AssemblyException((Component)CreateLang.translateDirect("gui.assembly.exception.no_contacts", new Object[0]));
        }
        if (this.contacts > 1) {
            throw new AssemblyException((Component)CreateLang.translateDirect("gui.assembly.exception.too_many_contacts", new Object[0]));
        }
        ElevatorColumn column = ElevatorColumn.get((LevelAccessor)world, this.getGlobalColumn());
        if (column != null && column.isActive()) {
            throw new AssemblyException((Component)CreateLang.translateDirect("gui.assembly.exception.column_conflict", new Object[0]));
        }
        this.startMoving(world);
        return true;
    }

    @Override
    protected Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (!AllBlocks.REDSTONE_CONTACT.has(blockState)) {
            return super.capture(world, pos);
        }
        Direction facing = (Direction)blockState.getValue((Property)RedstoneContactBlock.FACING);
        if (facing.getAxis() == Direction.Axis.Y) {
            return super.capture(world, pos);
        }
        ++this.contacts;
        BlockPos local = this.toLocalPos(pos.relative(facing));
        this.column = new ElevatorColumn.ColumnCoords(local.getX(), local.getZ(), facing.getOpposite());
        this.contactYOffset = local.getY();
        return super.capture(world, pos);
    }

    public int getContactYOffset() {
        return this.contactYOffset;
    }

    public void broadcastFloorData(Level level, BlockPos contactPos) {
        ElevatorColumn column = ElevatorColumn.get((LevelAccessor)level, this.getGlobalColumn());
        BlockEntity blockEntity = level.getBlockEntity(contactPos);
        if (!(blockEntity instanceof ElevatorContactBlockEntity)) {
            return;
        }
        ElevatorContactBlockEntity ecbe = (ElevatorContactBlockEntity)blockEntity;
        if (column != null) {
            column.floorReached((LevelAccessor)level, ecbe.shortName);
        }
    }

    @Override
    public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
        CompoundTag tag = super.writeNBT(registries, spawnPacket);
        tag.putBoolean("Arrived", this.arrived);
        tag.put("Column", (Tag)this.column.write());
        tag.putInt("ContactY", this.contactYOffset);
        tag.putInt("MaxContactY", this.maxContactY);
        tag.putInt("MinContactY", this.minContactY);
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
        this.arrived = nbt.getBoolean("Arrived");
        this.column = ElevatorColumn.ColumnCoords.read(nbt.getCompound("Column"));
        this.contactYOffset = nbt.getInt("ContactY");
        this.maxContactY = nbt.getInt("MaxContactY");
        this.minContactY = nbt.getInt("MinContactY");
        super.readNBT(world, nbt, spawnData);
    }

    @Override
    public ContraptionType getType() {
        return (ContraptionType)AllContraptionTypes.ELEVATOR.value();
    }

    public void setClientYTarget(int clientYTarget) {
        if (this.clientYTarget == clientYTarget) {
            return;
        }
        this.clientYTarget = clientYTarget;
        this.syncControlDisplays();
    }

    public void syncControlDisplays() {
        if (this.namesList.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.namesList.size(); ++i) {
            if ((Integer)this.namesList.get(i).getFirst() != this.clientYTarget) continue;
            this.setAllControlsToFloor(i);
        }
    }

    public void setAllControlsToFloor(int floorIndex) {
        for (MutablePair pair : this.actors) {
            Object object;
            if (pair.right == null || !((object = ((MovementContext)pair.right).temporaryData) instanceof ContraptionControlsMovement.ElevatorFloorSelection)) continue;
            ContraptionControlsMovement.ElevatorFloorSelection efs = (ContraptionControlsMovement.ElevatorFloorSelection)object;
            efs.currentIndex = floorIndex;
        }
    }
}
