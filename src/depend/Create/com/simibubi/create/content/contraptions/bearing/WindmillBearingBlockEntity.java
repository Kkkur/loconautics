/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WindmillBearingBlockEntity
extends MechanicalBearingBlockEntity {
    protected ScrollOptionBehaviour<RotationDirection> movementDirection;
    protected float lastGeneratedSpeed;
    protected boolean queuedReassembly;

    public WindmillBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void updateGeneratedRotation() {
        super.updateGeneratedRotation();
        this.lastGeneratedSpeed = this.getGeneratedSpeed();
        this.queuedReassembly = false;
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        boolean cancelAssembly = this.assembleNextTick;
        super.onSpeedChanged(prevSpeed);
        this.assembleNextTick = cancelAssembly;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            return;
        }
        if (!this.queuedReassembly) {
            return;
        }
        this.queuedReassembly = false;
        if (!this.running) {
            this.assembleNextTick = true;
        }
    }

    public void disassembleForMovement() {
        if (!this.running) {
            return;
        }
        this.disassemble();
        this.queuedReassembly = true;
    }

    @Override
    public float getGeneratedSpeed() {
        if (!this.running) {
            return 0.0f;
        }
        if (this.movedContraption == null) {
            return this.lastGeneratedSpeed;
        }
        int sails = ((BearingContraption)this.movedContraption.getContraption()).getSailBlocks() / (Integer)AllConfigs.server().kinetics.windmillSailsPerRPM.get();
        return (float)Mth.clamp((int)sails, (int)1, (int)16) * this.getAngleSpeedDirection();
    }

    @Override
    protected boolean isWindmill() {
        return true;
    }

    protected float getAngleSpeedDirection() {
        RotationDirection rotationDirection = RotationDirection.values()[this.movementDirection.getValue()];
        return rotationDirection == RotationDirection.CLOCKWISE ? 1 : -1;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putFloat("LastGenerated", this.lastGeneratedSpeed);
        compound.putBoolean("QueueAssembly", this.queuedReassembly);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (!this.wasMoved) {
            this.lastGeneratedSpeed = compound.getFloat("LastGenerated");
        }
        this.queuedReassembly = compound.getBoolean("QueueAssembly");
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.remove(this.movementMode);
        this.movementDirection = new ScrollOptionBehaviour<RotationDirection>(RotationDirection.class, (Component)CreateLang.translateDirect("contraptions.windmill.rotation_direction", new Object[0]), this, this.getMovementModeSlot());
        this.movementDirection.withCallback($ -> this.onDirectionChanged());
        behaviours.add(this.movementDirection);
        this.registerAwardables(behaviours, AllAdvancements.WINDMILL, AllAdvancements.WINDMILL_MAXED);
    }

    private void onDirectionChanged() {
        if (!this.running) {
            return;
        }
        if (!this.level.isClientSide) {
            this.updateGeneratedRotation();
        }
    }

    @Override
    public boolean isWoodenTop() {
        return true;
    }

    public static enum RotationDirection implements INamedIconOptions
    {
        CLOCKWISE(AllIcons.I_REFRESH),
        COUNTER_CLOCKWISE(AllIcons.I_ROTATE_CCW);

        private String translationKey;
        private AllIcons icon;

        private RotationDirection(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "create.generic." + Lang.asId((String)this.name());
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
