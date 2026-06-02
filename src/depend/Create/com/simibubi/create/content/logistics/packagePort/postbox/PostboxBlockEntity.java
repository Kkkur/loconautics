/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.item.BoneMealItem
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.simibubi.create.content.logistics.packagePort.postbox;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlock;
import com.simibubi.create.content.trains.station.GlobalPackagePort;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.lang.ref.WeakReference;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class PostboxBlockEntity
extends PackagePortBlockEntity {
    public WeakReference<GlobalStation> trackedGlobalStation = new WeakReference<Object>(null);
    public LerpedFloat flag = LerpedFloat.linear().startWithValue(0.0);
    public boolean forceFlag;
    private boolean sendParticles;
    public AbstractComputerBehaviour computerBehaviour;

    public PostboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.PACKAGE_POSTBOX.get(), (be, context) -> be.itemHandler);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
        super.addBehaviours(behaviours);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && !this.isVirtual()) {
            if (this.sendParticles) {
                this.sendData();
            }
            return;
        }
        float currentTarget = this.flag.getChaseTarget();
        if (currentTarget == 0.0f || this.flag.settled()) {
            boolean target;
            boolean bl = target = !this.inventory.isEmpty() || this.forceFlag;
            if ((float)target != currentTarget) {
                this.flag.chase((double)target, (double)0.1f, LerpedFloat.Chaser.LINEAR);
                if (target) {
                    AllSoundEvents.CONTRAPTION_ASSEMBLE.playAt(this.level, (Vec3i)this.worldPosition, 1.0f, 2.0f, true);
                }
            }
        }
        boolean settled = this.flag.getValue() > 0.15f;
        this.flag.tickChaser();
        if (currentTarget == 0.0f && settled != this.flag.getValue() > 0.15f) {
            AllSoundEvents.CONTRAPTION_DISASSEMBLE.playAt(this.level, (Vec3i)this.worldPosition, 0.75f, 1.5f, true);
        }
        if (this.sendParticles) {
            this.sendParticles = false;
            BoneMealItem.addGrowthParticles((LevelAccessor)this.level, (BlockPos)this.worldPosition, (int)40);
        }
    }

    @Override
    protected void onOpenChange(boolean open) {
        BlockState state = this.level.getBlockState(this.worldPosition);
        if (!(state.getBlock() instanceof PostboxBlock)) {
            return;
        }
        this.level.setBlockAndUpdate(this.worldPosition, (BlockState)state.setValue((Property)PostboxBlock.OPEN, (Comparable)Boolean.valueOf(open)));
        this.level.playSound(null, this.worldPosition, open ? SoundEvents.BARREL_OPEN : SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS);
    }

    public void spawnParticles() {
        this.sendParticles = true;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (clientPacket && this.sendParticles) {
            NBTHelper.putMarker((CompoundTag)tag, (String)"Particles");
        }
        this.sendParticles = false;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.sendParticles = clientPacket && tag.contains("Particles");
    }

    public void setChanged() {
        this.saveOfflineBuffer();
        super.setChanged();
    }

    private void saveOfflineBuffer() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        GlobalStation station = (GlobalStation)this.trackedGlobalStation.get();
        if (station == null) {
            return;
        }
        GlobalPackagePort globalPackagePort = station.connectedPorts.get(this.worldPosition);
        if (globalPackagePort == null) {
            return;
        }
        globalPackagePort.saveOfflineBuffer(this.inventory);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }
}
