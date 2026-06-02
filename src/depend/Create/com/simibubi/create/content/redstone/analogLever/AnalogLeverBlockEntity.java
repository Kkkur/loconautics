/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.redstone.analogLever;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AnalogLeverBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation {
    int state = 0;
    int lastChange;
    LerpedFloat clientState = LerpedFloat.linear();

    public AnalogLeverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("State", this.state);
        compound.putInt("ChangeTimer", this.lastChange);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.state = compound.getInt("State");
        this.lastChange = compound.getInt("ChangeTimer");
        this.clientState.chase((double)this.state, (double)0.2f, LerpedFloat.Chaser.EXP);
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lastChange > 0) {
            --this.lastChange;
            if (this.lastChange == 0) {
                this.updateOutput();
            }
        }
        if (this.level.isClientSide) {
            this.clientState.tickChaser();
        }
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    private void updateOutput() {
        AnalogLeverBlock.updateNeighbors(this.getBlockState(), this.level, this.worldPosition);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public void changeState(boolean back) {
        int prevState = this.state;
        this.state += back ? -1 : 1;
        this.state = Mth.clamp((int)this.state, (int)0, (int)15);
        if (prevState != this.state) {
            this.lastChange = 15;
        }
        this.sendData();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("tooltip.analogStrength", this.state).forGoggles(tooltip);
        return true;
    }

    public int getState() {
        return this.state;
    }
}
