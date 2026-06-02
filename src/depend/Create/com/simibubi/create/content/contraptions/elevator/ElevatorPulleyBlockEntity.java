/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.elevator;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlock;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.contraptions.elevator.ElevatorFloorListPacket;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ElevatorPulleyBlockEntity
extends PulleyBlockEntity {
    private float prevSpeed = 0.0f;
    private boolean arrived = true;
    private int clientOffsetTarget;
    private boolean initialOffsetReceived = false;

    public ElevatorPulleyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private int getTargetOffset() {
        Contraption contraption;
        if (this.level.isClientSide) {
            return this.clientOffsetTarget;
        }
        if (this.movedContraption == null || !((contraption = this.movedContraption.getContraption()) instanceof ElevatorContraption)) {
            return (int)this.offset;
        }
        ElevatorContraption ec = (ElevatorContraption)contraption;
        Integer target = ec.getCurrentTargetY(this.level);
        if (target == null) {
            return (int)this.offset;
        }
        return this.worldPosition.getY() - target + ec.contactYOffset - 1;
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
        super.attach(contraption);
        if (this.offset >= 0.0f) {
            this.resetContraptionToOffset();
        }
        if (this.level.isClientSide) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ElevatorFloorListPacket.RequestFloorList(contraption));
            return;
        }
        Contraption contraption2 = contraption.getContraption();
        if (contraption2 instanceof ElevatorContraption) {
            ElevatorContraption ec = (ElevatorContraption)contraption2;
            ElevatorColumn.getOrCreate((LevelAccessor)this.level, ec.getGlobalColumn()).setActive(true);
        }
    }

    @Override
    public void tick() {
        double diff;
        boolean wasArrived = this.arrived;
        super.tick();
        if (this.movedContraption == null) {
            return;
        }
        Contraption contraption = this.movedContraption.getContraption();
        if (!(contraption instanceof ElevatorContraption)) {
            return;
        }
        ElevatorContraption ec = (ElevatorContraption)contraption;
        if (this.level.isClientSide()) {
            ec.setClientYTarget(this.worldPosition.getY() - this.clientOffsetTarget + ec.contactYOffset - 1);
        }
        this.waitingForSpeedChange = false;
        ec.arrived = wasArrived;
        if (!this.arrived) {
            return;
        }
        double y = this.movedContraption.getY();
        int targetLevel = Mth.floor((double)(0.5 + y)) + ec.contactYOffset;
        Integer ecCurrentTargetY = ec.getCurrentTargetY(this.level);
        if (ecCurrentTargetY != null) {
            targetLevel = ecCurrentTargetY;
        }
        if (this.level.isClientSide()) {
            targetLevel = ec.clientYTarget;
        }
        if (!wasArrived && !this.level.isClientSide()) {
            this.triggerContact(ec, targetLevel - ec.contactYOffset);
            AllSoundEvents.CONTRAPTION_DISASSEMBLE.play(this.level, null, (Vec3i)this.worldPosition.below((int)this.offset), 0.75f, 0.8f);
        }
        if (Math.abs(diff = (double)targetLevel - y - (double)ec.contactYOffset) > 0.0078125) {
            diff *= 0.25;
        }
        this.movedContraption.setPos(this.movedContraption.position().add(0.0, diff, 0.0));
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.level.isClientSide() || !this.arrived) {
            return;
        }
        if (this.movedContraption == null || !this.movedContraption.isAlive()) {
            return;
        }
        Contraption contraption = this.movedContraption.getContraption();
        if (!(contraption instanceof ElevatorContraption)) {
            return;
        }
        ElevatorContraption ec = (ElevatorContraption)contraption;
        if (this.getTargetOffset() != (int)this.offset) {
            return;
        }
        double y = this.movedContraption.getY();
        int targetLevel = Mth.floor((double)(0.5 + y));
        this.triggerContact(ec, targetLevel);
    }

    private void triggerContact(ElevatorContraption ec, int targetLevel) {
        ElevatorColumn.ColumnCoords coords = ec.getGlobalColumn();
        ElevatorColumn column = ElevatorColumn.get((LevelAccessor)this.level, coords);
        if (column == null) {
            return;
        }
        BlockPos contactPos = column.contactAt(targetLevel + ec.contactYOffset);
        if (!this.level.isLoaded(contactPos)) {
            return;
        }
        BlockState contactState = this.level.getBlockState(contactPos);
        if (!AllBlocks.ELEVATOR_CONTACT.has(contactState)) {
            return;
        }
        if (((Boolean)contactState.getValue((Property)ElevatorContactBlock.POWERING)).booleanValue()) {
            return;
        }
        ElevatorContactBlock ecb = (ElevatorContactBlock)AllBlocks.ELEVATOR_CONTACT.get();
        ecb.withBlockEntityDo((BlockGetter)this.level, contactPos, be -> {
            be.activateBlock = true;
        });
        ecb.scheduleActivation((LevelAccessor)this.level, contactPos);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if (clientPacket) {
            compound.putInt("ClientTarget", this.clientOffsetTarget);
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        this.clientOffsetTarget = compound.getInt("ClientTarget");
        if (this.initialOffsetReceived) {
            return;
        }
        this.offset = compound.getFloat("Offset");
        this.initialOffsetReceived = true;
        this.resetContraptionToOffset();
    }

    @Override
    public float getMovementSpeed() {
        int currentTarget = this.getTargetOffset();
        if (!this.level.isClientSide() && currentTarget != this.clientOffsetTarget) {
            this.clientOffsetTarget = currentTarget;
            this.sendData();
        }
        float diff = (float)currentTarget - this.offset;
        float movementSpeed = Mth.clamp((float)ElevatorPulleyBlockEntity.convertToLinear(this.getSpeed() * 2.0f), (float)-1.99f, (float)1.99f);
        float rpmLimit = Math.abs(movementSpeed);
        float configacc = Mth.lerp((float)Math.abs(movementSpeed), (float)0.0075f, (float)0.0175f);
        float decelleration = (float)Math.sqrt(2.0f * Math.abs(diff) * configacc);
        float speed = diff;
        speed = Mth.clamp((float)speed, (float)(-rpmLimit), (float)rpmLimit);
        speed = Mth.clamp((float)speed, (float)(this.prevSpeed - configacc), (float)(this.prevSpeed + configacc));
        speed = Mth.clamp((float)speed, (float)(-decelleration), (float)decelleration);
        boolean bl = this.arrived = Math.abs(diff) < 0.5f;
        if (speed > 9.765625E-4f && !this.level.isClientSide()) {
            this.setChanged();
        }
        this.prevSpeed = speed;
        return this.prevSpeed;
    }

    @Override
    protected boolean shouldCreateRopes() {
        return false;
    }

    @Override
    public void disassemble() {
        ElevatorContraption ec;
        ElevatorColumn column;
        Contraption contraption;
        if (this.movedContraption != null && (contraption = this.movedContraption.getContraption()) instanceof ElevatorContraption && (column = ElevatorColumn.get((LevelAccessor)this.level, (ec = (ElevatorContraption)contraption).getGlobalColumn())) != null) {
            column.setActive(false);
        }
        super.disassemble();
        this.offset = -1.0f;
        this.sendData();
    }

    public void clicked() {
        BlockEntity blockEntity;
        if (this.isPassive() && (blockEntity = this.level.getBlockEntity(this.mirrorParent)) instanceof ElevatorPulleyBlockEntity) {
            ElevatorPulleyBlockEntity parent = (ElevatorPulleyBlockEntity)blockEntity;
            parent.clicked();
            return;
        }
        if (this.running) {
            this.disassemble();
        } else {
            this.assembleNextTick = true;
        }
    }

    @Override
    protected boolean moveAndCollideContraption() {
        if (this.arrived) {
            return false;
        }
        super.moveAndCollideContraption();
        return false;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS);
    }

    @Override
    protected void assemble() throws AssemblyException {
        BlockPos ropePos;
        BlockState ropeState;
        int i;
        if (!(this.level.getBlockState(this.worldPosition).getBlock() instanceof ElevatorPulleyBlock)) {
            return;
        }
        if (this.getSpeed() == 0.0f) {
            return;
        }
        int maxLength = (Integer)AllConfigs.server().kinetics.maxRopeLength.get();
        for (i = 1; i <= maxLength && ((ropeState = this.level.getBlockState(ropePos = this.worldPosition.below(i))).getCollisionShape((BlockGetter)this.level, ropePos).isEmpty() || ropeState.canBeReplaced()); ++i) {
        }
        this.offset = i - 1;
        this.forceMove = true;
        if (!this.level.isClientSide && this.mirrorParent == null) {
            this.needsContraption = false;
            BlockPos anchor = this.worldPosition.below(Mth.floor((float)(this.offset + 1.0f)));
            this.offset = Mth.floor((float)this.offset);
            ElevatorContraption contraption = new ElevatorContraption((int)this.offset);
            float offsetOnSucess = this.offset;
            this.offset = 0.0f;
            boolean canAssembleStructure = contraption.assemble(this.level, anchor);
            if (!canAssembleStructure && this.getSpeed() > 0.0f) {
                return;
            }
            if (!contraption.getBlocks().isEmpty()) {
                this.offset = offsetOnSucess;
                contraption.removeBlocksFromWorld(this.level, BlockPos.ZERO);
                this.movedContraption = ControlledContraptionEntity.create(this.level, this, contraption);
                this.movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
                contraption.maxContactY = this.worldPosition.getY() + contraption.contactYOffset - 1;
                contraption.minContactY = contraption.maxContactY - maxLength;
                this.level.addFreshEntity((Entity)this.movedContraption);
                this.forceMove = true;
                this.needsContraption = true;
                if (contraption.containsBlockBreakers()) {
                    this.award(AllAdvancements.CONTRAPTION_ACTORS);
                }
                for (BlockPos pos : contraption.createColliders(this.level, Direction.UP)) {
                    BlockEntity blockEntity;
                    if (pos.getY() != 0 || !((blockEntity = this.level.getBlockEntity(new BlockPos((pos = pos.offset((Vec3i)anchor)).getX(), this.worldPosition.getY(), pos.getZ()))) instanceof ElevatorPulleyBlockEntity)) continue;
                    ElevatorPulleyBlockEntity pbe = (ElevatorPulleyBlockEntity)blockEntity;
                    pbe.startMirroringOther(this.worldPosition);
                }
                ElevatorColumn column = ElevatorColumn.getOrCreate((LevelAccessor)this.level, contraption.getGlobalColumn());
                int target = (int)((float)(this.worldPosition.getY() + contraption.contactYOffset - 1) - this.offset);
                column.target(target);
                column.gatherAll();
                column.setActive(true);
                column.markDirty();
                contraption.broadcastFloorData(this.level, column.contactAt(target));
                this.clientOffsetTarget = column.getTargetedYLevel();
                this.arrived = true;
            }
        }
        this.clientOffsetDiff = 0.0f;
        this.running = true;
        this.sendData();
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        this.setChanged();
    }

    @Override
    protected IControlContraption.MovementMode getMovementMode() {
        return IControlContraption.MovementMode.MOVE_NEVER_PLACE;
    }
}
