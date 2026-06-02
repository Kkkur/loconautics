/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.contraptions.bearing.ClockworkBearingBlock;
import com.simibubi.create.content.contraptions.bearing.ClockworkContraption;
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import java.util.List;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Pair;

public class ClockworkBearingBlockEntity
extends KineticBlockEntity
implements IBearingBlockEntity,
IDisplayAssemblyExceptions {
    protected ControlledContraptionEntity hourHand;
    protected ControlledContraptionEntity minuteHand;
    protected float hourAngle;
    protected float minuteAngle;
    protected float clientHourAngleDiff;
    protected float clientMinuteAngleDiff;
    protected boolean running;
    protected boolean assembleNextTick;
    protected AssemblyException lastException;
    protected ScrollOptionBehaviour<ClockHands> operationMode;
    private float prevForcedAngle;

    public ClockworkBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(3);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.operationMode = new ScrollOptionBehaviour<ClockHands>(ClockHands.class, (Component)CreateLang.translateDirect("contraptions.clockwork.clock_hands", new Object[0]), this, this.getMovementModeSlot());
        behaviours.add(this.operationMode);
        this.registerAwardables(behaviours, AllAdvancements.CLOCKWORK_BEARING);
    }

    @Override
    public boolean isWoodenTop() {
        return false;
    }

    @Override
    public void tick() {
        float newAngle;
        super.tick();
        if (this.level.isClientSide) {
            this.prevForcedAngle = this.hourAngle;
            this.clientMinuteAngleDiff /= 2.0f;
            this.clientHourAngleDiff /= 2.0f;
        }
        if (!this.level.isClientSide && this.assembleNextTick) {
            this.assembleNextTick = false;
            if (this.running) {
                boolean canDisassemble = true;
                if (this.speed == 0.0f && (canDisassemble || this.hourHand == null || this.hourHand.getContraption().getBlocks().isEmpty())) {
                    if (this.hourHand != null) {
                        this.hourHand.getContraption().stop(this.level);
                    }
                    if (this.minuteHand != null) {
                        this.minuteHand.getContraption().stop(this.level);
                    }
                    this.disassemble();
                }
                return;
            }
            this.assemble();
            return;
        }
        if (!this.running) {
            return;
        }
        if (this.hourHand == null || !this.hourHand.isStalled()) {
            newAngle = this.hourAngle + this.getHourArmSpeed();
            this.hourAngle = newAngle % 360.0f;
        }
        if (this.minuteHand == null || !this.minuteHand.isStalled()) {
            newAngle = this.minuteAngle + this.getMinuteArmSpeed();
            this.minuteAngle = newAngle % 360.0f;
        }
        this.applyRotations();
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return this.lastException;
    }

    protected void applyRotations() {
        BlockState blockState = this.getBlockState();
        Direction.Axis axis = Direction.Axis.X;
        if (blockState.hasProperty((Property)BlockStateProperties.FACING)) {
            axis = ((Direction)blockState.getValue((Property)BlockStateProperties.FACING)).getAxis();
        }
        if (this.hourHand != null) {
            this.hourHand.setAngle(this.hourAngle);
            this.hourHand.setRotationAxis(axis);
        }
        if (this.minuteHand != null) {
            this.minuteHand.setAngle(this.minuteAngle);
            this.minuteHand.setRotationAxis(axis);
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.hourHand != null && !this.level.isClientSide) {
            this.sendData();
        }
    }

    public float getHourArmSpeed() {
        float speed = this.getAngularSpeed() / 2.0f;
        if (speed != 0.0f) {
            ClockHands mode = ClockHands.values()[this.operationMode.getValue()];
            float hourTarget = mode == ClockHands.HOUR_FIRST ? this.getHourTarget(false) : (mode == ClockHands.MINUTE_FIRST ? this.getMinuteTarget() : this.getHourTarget(true));
            float shortestAngleDiff = AngleHelper.getShortestAngleDiff((double)this.hourAngle, (double)hourTarget);
            speed = shortestAngleDiff < 0.0f ? Math.max(speed, shortestAngleDiff) : Math.min(-speed, shortestAngleDiff);
        }
        return speed + this.clientHourAngleDiff / 3.0f;
    }

    public float getMinuteArmSpeed() {
        float speed = this.getAngularSpeed();
        if (speed != 0.0f) {
            ClockHands mode = ClockHands.values()[this.operationMode.getValue()];
            float minuteTarget = mode == ClockHands.MINUTE_FIRST ? this.getHourTarget(false) : this.getMinuteTarget();
            float shortestAngleDiff = AngleHelper.getShortestAngleDiff((double)this.minuteAngle, (double)minuteTarget);
            speed = shortestAngleDiff < 0.0f ? Math.max(speed, shortestAngleDiff) : Math.min(-speed, shortestAngleDiff);
        }
        return speed + this.clientMinuteAngleDiff / 3.0f;
    }

    protected float getHourTarget(boolean cycle24) {
        boolean isNatural = this.level.dimensionType().natural();
        int dayTime = (int)(this.level.getDayTime() * (long)(isNatural ? 1 : 24) % 24000L);
        int hours = (dayTime / 1000 + 6) % 24;
        int offset = ((Direction)this.getBlockState().getValue((Property)ClockworkBearingBlock.FACING)).getAxisDirection().getStep();
        float hourTarget = (float)(offset * -360) / (cycle24 ? 24.0f : 12.0f) * (float)(hours % (cycle24 ? 24 : 12));
        return hourTarget;
    }

    protected float getMinuteTarget() {
        boolean isNatural = this.level.dimensionType().natural();
        int dayTime = (int)(this.level.getDayTime() * (long)(isNatural ? 1 : 24) % 24000L);
        int minutes = dayTime % 1000 * 60 / 1000;
        int offset = ((Direction)this.getBlockState().getValue((Property)ClockworkBearingBlock.FACING)).getAxisDirection().getStep();
        float minuteTarget = (float)(offset * -360) / 60.0f * (float)minutes;
        return minuteTarget;
    }

    public float getAngularSpeed() {
        float speed = -Math.abs(this.getSpeed() * 3.0f / 10.0f);
        if (this.level.isClientSide) {
            speed *= ServerSpeedProvider.get();
        }
        return speed;
    }

    public void assemble() {
        Pair<ClockworkContraption, ClockworkContraption> contraption;
        if (!(this.level.getBlockState(this.worldPosition).getBlock() instanceof ClockworkBearingBlock)) {
            return;
        }
        Direction direction = (Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING);
        try {
            contraption = ClockworkContraption.assembleClockworkAt(this.level, this.worldPosition, direction);
            this.lastException = null;
        }
        catch (AssemblyException e) {
            this.lastException = e;
            this.sendData();
            return;
        }
        if (contraption == null) {
            return;
        }
        if (contraption.getLeft() == null) {
            return;
        }
        if (((ClockworkContraption)contraption.getLeft()).getBlocks().isEmpty()) {
            return;
        }
        BlockPos anchor = this.worldPosition.relative(direction);
        ((ClockworkContraption)contraption.getLeft()).removeBlocksFromWorld(this.level, BlockPos.ZERO);
        this.hourHand = ControlledContraptionEntity.create(this.level, this, (Contraption)contraption.getLeft());
        this.hourHand.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        this.hourHand.setRotationAxis(direction.getAxis());
        this.level.addFreshEntity((Entity)this.hourHand);
        if (((ClockworkContraption)contraption.getLeft()).containsBlockBreakers()) {
            this.award(AllAdvancements.CONTRAPTION_ACTORS);
        }
        if (contraption.getRight() != null) {
            anchor = this.worldPosition.relative(direction, ((ClockworkContraption)contraption.getRight()).offset + 1);
            ((ClockworkContraption)contraption.getRight()).removeBlocksFromWorld(this.level, BlockPos.ZERO);
            this.minuteHand = ControlledContraptionEntity.create(this.level, this, (Contraption)contraption.getRight());
            this.minuteHand.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
            this.minuteHand.setRotationAxis(direction.getAxis());
            this.level.addFreshEntity((Entity)this.minuteHand);
            if (((ClockworkContraption)contraption.getRight()).containsBlockBreakers()) {
                this.award(AllAdvancements.CONTRAPTION_ACTORS);
            }
        }
        this.award(AllAdvancements.CLOCKWORK_BEARING);
        this.running = true;
        this.hourAngle = 0.0f;
        this.minuteAngle = 0.0f;
        this.sendData();
    }

    public void disassemble() {
        if (!this.running && this.hourHand == null && this.minuteHand == null) {
            return;
        }
        this.hourAngle = 0.0f;
        this.minuteAngle = 0.0f;
        this.applyRotations();
        if (this.hourHand != null) {
            this.hourHand.disassemble();
        }
        if (this.minuteHand != null) {
            this.minuteHand.disassemble();
        }
        this.hourHand = null;
        this.minuteHand = null;
        this.running = false;
        this.sendData();
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
        Contraption contraption2 = contraption.getContraption();
        if (!(contraption2 instanceof ClockworkContraption)) {
            return;
        }
        ClockworkContraption cc = (ClockworkContraption)contraption2;
        this.setChanged();
        Direction facing = (Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING);
        BlockPos anchor = this.worldPosition.relative(facing, cc.offset + 1);
        if (cc.handType == ClockworkContraption.HandType.HOUR) {
            this.hourHand = contraption;
            this.hourHand.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        } else {
            this.minuteHand = contraption;
            this.minuteHand.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        }
        if (!this.level.isClientSide) {
            this.running = true;
            this.sendData();
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("Running", this.running);
        compound.putFloat("HourAngle", this.hourAngle);
        compound.putFloat("MinuteAngle", this.minuteAngle);
        AssemblyException.write(compound, registries, this.lastException);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        float hourAngleBefore = this.hourAngle;
        float minuteAngleBefore = this.minuteAngle;
        this.running = compound.getBoolean("Running");
        this.hourAngle = compound.getFloat("HourAngle");
        this.minuteAngle = compound.getFloat("MinuteAngle");
        this.lastException = AssemblyException.read(compound, registries);
        super.read(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        if (this.running) {
            this.clientHourAngleDiff = AngleHelper.getShortestAngleDiff((double)hourAngleBefore, (double)this.hourAngle);
            this.clientMinuteAngleDiff = AngleHelper.getShortestAngleDiff((double)minuteAngleBefore, (double)this.minuteAngle);
            this.hourAngle = hourAngleBefore;
            this.minuteAngle = minuteAngleBefore;
        } else {
            this.hourHand = null;
            this.minuteHand = null;
        }
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        this.assembleNextTick = true;
    }

    @Override
    public boolean isValid() {
        return !this.isRemoved();
    }

    @Override
    public float getInterpolatedAngle(float partialTicks) {
        if (this.isVirtual()) {
            return Mth.lerp((float)partialTicks, (float)this.prevForcedAngle, (float)this.hourAngle);
        }
        if (this.hourHand == null || this.hourHand.isStalled()) {
            partialTicks = 0.0f;
        }
        return Mth.lerp((float)partialTicks, (float)this.hourAngle, (float)(this.hourAngle + this.getHourArmSpeed()));
    }

    @Override
    public void onStall() {
        if (!this.level.isClientSide) {
            this.sendData();
        }
    }

    @Override
    public void remove() {
        if (!this.level.isClientSide) {
            this.disassemble();
        }
        super.remove();
    }

    @Override
    public boolean isAttachedTo(AbstractContraptionEntity contraption) {
        Contraption contraption2 = contraption.getContraption();
        if (!(contraption2 instanceof ClockworkContraption)) {
            return false;
        }
        ClockworkContraption cc = (ClockworkContraption)contraption2;
        if (cc.handType == ClockworkContraption.HandType.HOUR) {
            return this.hourHand == contraption;
        }
        return this.minuteHand == contraption;
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public BlockPos getBlockPosition() {
        return this.worldPosition;
    }

    @Override
    public void setAngle(float forcedAngle) {
        this.hourAngle = forcedAngle;
    }

    static enum ClockHands implements INamedIconOptions
    {
        HOUR_FIRST(AllIcons.I_HOUR_HAND_FIRST),
        MINUTE_FIRST(AllIcons.I_MINUTE_HAND_FIRST),
        HOUR_FIRST_24(AllIcons.I_HOUR_HAND_FIRST_24);

        private String translationKey;
        private AllIcons icon;

        private ClockHands(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "create.contraptions.clockwork." + Lang.asId((String)this.name());
        }

        @Override
        public AllIcons getIcon() {
            return this.icon;
        }

        @Override
        public String getTranslationKey() {
            return this.translationKey;
        }
    }
}
