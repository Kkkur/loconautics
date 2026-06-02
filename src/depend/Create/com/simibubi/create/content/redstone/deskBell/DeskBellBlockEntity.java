/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.deskBell;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.redstone.deskBell.DeskBellBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class DeskBellBlockEntity
extends SmartBlockEntity {
    public LerpedFloat animation = LerpedFloat.linear().startWithValue(0.0);
    public boolean ding;
    int blockStateTimer = 0;
    float animationOffset;

    public DeskBellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        this.animation.tickChaser();
        if (this.level.isClientSide) {
            return;
        }
        if (this.blockStateTimer == 0) {
            return;
        }
        --this.blockStateTimer;
        if (this.blockStateTimer > 0) {
            return;
        }
        BlockState blockState = this.getBlockState();
        if (((Boolean)blockState.getValue((Property)DeskBellBlock.POWERED)).booleanValue()) {
            ((DeskBellBlock)AllBlocks.DESK_BELL.get()).unPress(blockState, this.level, this.worldPosition);
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (clientPacket && this.ding) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"Ding");
        }
        this.ding = false;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (clientPacket && tag.contains("Ding")) {
            this.ding();
        }
    }

    public void ding() {
        if (!this.level.isClientSide) {
            this.blockStateTimer = 20;
            this.ding = true;
            this.sendData();
            return;
        }
        this.animationOffset = this.level.random.nextFloat() * 2.0f * (float)Math.PI;
        this.animation.startWithValue(1.0).chase(0.0, 0.05, LerpedFloat.Chaser.LINEAR);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
