/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.steamEngine;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import java.util.List;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredShaftBlockEntity
extends GeneratingKineticBlockEntity {
    public BlockPos enginePos;
    public float engineEfficiency;
    public int movementDirection = 1;
    public int initialTicks = 3;
    public Block capacityKey;

    public PoweredShaftBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.initialTicks > 0) {
            --this.initialTicks;
        }
    }

    public void update(BlockPos sourcePos, int direction, float efficiency) {
        BlockPos key;
        this.enginePos = key = this.worldPosition.subtract((Vec3i)sourcePos);
        float prev = this.engineEfficiency;
        this.engineEfficiency = efficiency;
        int prevDirection = this.movementDirection;
        if (Mth.equal((float)efficiency, (float)prev) && prevDirection == direction) {
            return;
        }
        this.capacityKey = this.level.getBlockState(sourcePos).getBlock();
        this.movementDirection = direction;
        this.updateGeneratedRotation();
    }

    public void remove(BlockPos sourcePos) {
        if (!this.isPoweredBy(sourcePos)) {
            return;
        }
        this.enginePos = null;
        this.engineEfficiency = 0.0f;
        this.movementDirection = 0;
        this.capacityKey = null;
        this.updateGeneratedRotation();
    }

    public boolean canBePoweredBy(BlockPos globalPos) {
        return this.initialTicks == 0 && (this.enginePos == null || this.isPoweredBy(globalPos));
    }

    public boolean isPoweredBy(BlockPos globalPos) {
        BlockPos key = this.worldPosition.subtract((Vec3i)globalPos);
        return key.equals((Object)this.enginePos);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("Direction", this.movementDirection);
        if (this.initialTicks > 0) {
            compound.putInt("Warmup", this.initialTicks);
        }
        if (this.enginePos != null && this.capacityKey != null) {
            compound.put("EnginePos", NbtUtils.writeBlockPos((BlockPos)this.enginePos));
            compound.putFloat("EnginePower", this.engineEfficiency);
            compound.putString("EngineType", RegisteredObjectsHelper.getKeyOrThrow((Block)this.capacityKey).toString());
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.movementDirection = compound.getInt("Direction");
        this.initialTicks = compound.getInt("Warmup");
        this.enginePos = null;
        this.engineEfficiency = 0.0f;
        if (compound.contains("EnginePos")) {
            this.enginePos = NBTHelper.readBlockPos((CompoundTag)compound, (String)"EnginePos");
            this.engineEfficiency = compound.getFloat("EnginePower");
            this.capacityKey = (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse((String)compound.getString("EngineType")));
        }
    }

    @Override
    public float getGeneratedSpeed() {
        return this.getCombinedCapacity() > 0.0f ? (float)(this.movementDirection * 16 * this.getSpeedModifier()) : 0.0f;
    }

    private float getCombinedCapacity() {
        return this.capacityKey == null ? 0.0f : (float)((double)this.engineEfficiency * BlockStressValues.getCapacity(this.capacityKey));
    }

    private int getSpeedModifier() {
        return (int)(1.0 + (this.engineEfficiency >= 1.0f ? 3.0 : Math.min(2.0, Math.floor(this.engineEfficiency * 4.0f))));
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity;
        this.lastCapacityProvided = capacity = this.getCombinedCapacity() / (float)this.getSpeedModifier();
        return capacity;
    }

    @Override
    public int getRotationAngleOffset(Direction.Axis axis) {
        int combinedCoords = axis.choose(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ());
        return super.getRotationAngleOffset(axis) + (combinedCoords % 2 == 0 ? 180 : 0);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return false;
    }

    public boolean addToEngineTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
