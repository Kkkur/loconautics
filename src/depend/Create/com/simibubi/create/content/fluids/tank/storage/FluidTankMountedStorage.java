/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids.tank.storage;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.SyncedMountedStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.fluid.WrapperMountedFluidStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class FluidTankMountedStorage
extends WrapperMountedFluidStorage<Handler>
implements SyncedMountedStorage {
    public static final MapCodec<FluidTankMountedStorage> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("capacity").forGetter(FluidTankMountedStorage::getCapacity), (App)FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(FluidTankMountedStorage::getFluid)).apply((Applicative)i, FluidTankMountedStorage::new));
    private boolean dirty;

    protected FluidTankMountedStorage(MountedFluidStorageType<?> type, int capacity, FluidStack stack) {
        super(type, new Handler(capacity, stack));
        ((Handler)this.wrapped).onChange = () -> {
            this.dirty = true;
        };
    }

    protected FluidTankMountedStorage(int capacity, FluidStack stack) {
        this((MountedFluidStorageType)AllMountedStorageTypes.FLUID_TANK.get(), capacity, stack);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        FluidTankBlockEntity tank;
        if (be instanceof FluidTankBlockEntity && (tank = (FluidTankBlockEntity)be).isController()) {
            FluidTank inventory = tank.getTankInventory();
            inventory.setFluid(((Handler)this.wrapped).getFluid());
        }
    }

    public FluidStack getFluid() {
        return ((Handler)this.wrapped).getFluid();
    }

    public int getCapacity() {
        return ((Handler)this.wrapped).getCapacity();
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void markClean() {
        this.dirty = false;
    }

    @Override
    public void afterSync(Contraption contraption, BlockPos localPos) {
        BlockEntity be = contraption.getBlockEntityClientSide(localPos);
        if (!(be instanceof FluidTankBlockEntity)) {
            return;
        }
        FluidTankBlockEntity tank = (FluidTankBlockEntity)be;
        FluidTank inv = tank.getTankInventory();
        inv.setFluid(this.getFluid());
        float fillLevel = (float)inv.getFluidAmount() / (float)inv.getCapacity();
        if (tank.getFluidLevel() == null) {
            tank.setFluidLevel(LerpedFloat.linear().startWithValue((double)fillLevel));
        }
        tank.getFluidLevel().chase((double)fillLevel, 0.5, LerpedFloat.Chaser.EXP);
    }

    public static FluidTankMountedStorage fromTank(FluidTankBlockEntity tank) {
        FluidTank inventory = tank.getTankInventory();
        return new FluidTankMountedStorage(inventory.getCapacity(), inventory.getFluid().copy());
    }

    public static FluidTankMountedStorage fromLegacy(HolderLookup.Provider registries, CompoundTag nbt) {
        int capacity = nbt.getInt("Capacity");
        FluidStack fluid = FluidStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)nbt);
        return new FluidTankMountedStorage(capacity, fluid);
    }

    public static final class Handler
    extends FluidTank {
        private Runnable onChange = () -> {};

        public Handler(int capacity, FluidStack stack) {
            super(capacity);
            this.setFluid(stack);
        }

        protected void onContentsChanged() {
            this.onChange.run();
        }
    }
}
