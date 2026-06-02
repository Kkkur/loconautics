/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package com.simibubi.create.foundation.blockEntity.behaviour.fluid;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import java.util.function.Consumer;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.mutable.MutableInt;

public class SmartFluidTankBehaviour
extends BlockEntityBehaviour {
    public static final BehaviourType<SmartFluidTankBehaviour> TYPE = new BehaviourType();
    public static final BehaviourType<SmartFluidTankBehaviour> INPUT = new BehaviourType("Input");
    public static final BehaviourType<SmartFluidTankBehaviour> OUTPUT = new BehaviourType("Output");
    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;
    protected TankSegment[] tanks;
    protected IFluidHandler capability;
    protected boolean extractionAllowed = true;
    protected boolean insertionAllowed = true;
    protected Runnable fluidUpdateCallback;
    private BehaviourType<SmartFluidTankBehaviour> behaviourType;

    public static SmartFluidTankBehaviour single(SmartBlockEntity be, int capacity) {
        return new SmartFluidTankBehaviour(TYPE, be, 1, capacity, false);
    }

    public SmartFluidTankBehaviour(BehaviourType<SmartFluidTankBehaviour> type, SmartBlockEntity be, int tanks, int tankCapacity, boolean enforceVariety) {
        super(be);
        this.behaviourType = type;
        this.tanks = new TankSegment[tanks];
        IFluidHandler[] handlers = new IFluidHandler[tanks];
        for (int i = 0; i < tanks; ++i) {
            TankSegment tankSegment;
            this.tanks[i] = tankSegment = new TankSegment(tankCapacity);
            handlers[i] = tankSegment.tank;
        }
        this.capability = new InternalFluidHandler(handlers, enforceVariety);
        this.fluidUpdateCallback = () -> {};
    }

    public SmartFluidTankBehaviour whenFluidUpdates(Runnable fluidUpdateCallback) {
        this.fluidUpdateCallback = fluidUpdateCallback;
        return this;
    }

    public SmartFluidTankBehaviour allowInsertion() {
        this.insertionAllowed = true;
        return this;
    }

    public SmartFluidTankBehaviour allowExtraction() {
        this.extractionAllowed = true;
        return this;
    }

    public SmartFluidTankBehaviour forbidInsertion() {
        this.insertionAllowed = false;
        return this;
    }

    public SmartFluidTankBehaviour forbidExtraction() {
        this.extractionAllowed = false;
        return this;
    }

    @Override
    public void initialize() {
        super.initialize();
        if (this.getWorld().isClientSide) {
            return;
        }
        this.forEach(ts -> {
            ts.fluidLevel.forceNextSync();
            ts.onFluidStackChanged();
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (this.syncCooldown > 0) {
            --this.syncCooldown;
            if (this.syncCooldown == 0 && this.queuedSync) {
                this.updateFluids();
            }
        }
        this.forEach(be -> {
            LerpedFloat fluidLevel = be.getFluidLevel();
            if (fluidLevel != null) {
                fluidLevel.tickChaser();
            }
        });
    }

    public void sendDataImmediately() {
        this.syncCooldown = 0;
        this.queuedSync = false;
        this.updateFluids();
    }

    public void sendDataLazily() {
        if (this.syncCooldown > 0) {
            this.queuedSync = true;
            return;
        }
        this.updateFluids();
        this.queuedSync = false;
        this.syncCooldown = 8;
    }

    protected void updateFluids() {
        this.fluidUpdateCallback.run();
        this.blockEntity.sendData();
        this.blockEntity.setChanged();
    }

    @Override
    public void unload() {
        super.unload();
        this.blockEntity.getLevel().invalidateCapabilities(this.getPos());
    }

    public SmartFluidTank getPrimaryHandler() {
        return this.getPrimaryTank().tank;
    }

    public TankSegment getPrimaryTank() {
        return this.tanks[0];
    }

    public TankSegment[] getTanks() {
        return this.tanks;
    }

    public boolean isEmpty() {
        for (TankSegment tankSegment : this.tanks) {
            if (tankSegment.tank.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public void forEach(Consumer<TankSegment> action) {
        for (TankSegment tankSegment : this.tanks) {
            action.accept(tankSegment);
        }
    }

    public IFluidHandler getCapability() {
        return this.capability;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        ListTag tanksNBT = new ListTag();
        this.forEach(ts -> tanksNBT.add((Object)ts.writeNBT(registries)));
        nbt.put(this.getType().getName() + "Tanks", (Tag)tanksNBT);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        MutableInt index = new MutableInt(0);
        NBTHelper.iterateCompoundList((ListTag)nbt.getList(this.getType().getName() + "Tanks", 10), c -> {
            if (index.intValue() >= this.tanks.length) {
                return;
            }
            this.tanks[index.intValue()].readNBT((CompoundTag)c, registries, clientPacket);
            index.increment();
        });
    }

    @Override
    public BehaviourType<?> getType() {
        return this.behaviourType;
    }

    public class TankSegment {
        protected SmartFluidTank tank;
        protected LerpedFloat fluidLevel;
        protected FluidStack renderedFluid;

        public TankSegment(int capacity) {
            this.tank = new SmartFluidTank(capacity, f -> this.onFluidStackChanged());
            this.fluidLevel = LerpedFloat.linear().startWithValue(0.0).chase(0.0, 0.25, LerpedFloat.Chaser.EXP);
            this.renderedFluid = FluidStack.EMPTY;
        }

        public void onFluidStackChanged() {
            if (!SmartFluidTankBehaviour.this.blockEntity.hasLevel()) {
                return;
            }
            this.fluidLevel.chase((double)((float)this.tank.getFluidAmount() / (float)this.tank.getCapacity()), 0.25, LerpedFloat.Chaser.EXP);
            if (!SmartFluidTankBehaviour.this.getWorld().isClientSide) {
                SmartFluidTankBehaviour.this.sendDataLazily();
            }
            if (SmartFluidTankBehaviour.this.blockEntity.isVirtual() && !this.tank.getFluid().isEmpty()) {
                this.renderedFluid = this.tank.getFluid();
            }
        }

        public FluidStack getRenderedFluid() {
            return this.renderedFluid;
        }

        public LerpedFloat getFluidLevel() {
            return this.fluidLevel;
        }

        public float getTotalUnits(float partialTicks) {
            return this.fluidLevel.getValue(partialTicks) * (float)this.tank.getCapacity();
        }

        public CompoundTag writeNBT(HolderLookup.Provider registries) {
            CompoundTag compound = new CompoundTag();
            compound.put("TankContent", (Tag)this.tank.writeToNBT(registries, new CompoundTag()));
            compound.put("Level", (Tag)this.fluidLevel.writeNBT());
            return compound;
        }

        public void readNBT(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
            this.tank.readFromNBT(registries, compound.getCompound("TankContent"));
            this.fluidLevel.readNBT(compound.getCompound("Level"), clientPacket);
            if (!this.tank.getFluid().isEmpty()) {
                this.renderedFluid = this.tank.getFluid();
            }
        }

        public boolean isEmpty(float partialTicks) {
            FluidStack renderedFluid = this.getRenderedFluid();
            if (renderedFluid.isEmpty()) {
                return true;
            }
            float units = this.getTotalUnits(partialTicks);
            return units < 1.0f;
        }
    }

    public class InternalFluidHandler
    extends CombinedTankWrapper {
        public InternalFluidHandler(IFluidHandler[] handlers, boolean enforceVariety) {
            super(handlers);
            if (enforceVariety) {
                this.enforceVariety();
            }
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!SmartFluidTankBehaviour.this.insertionAllowed) {
                return 0;
            }
            return super.fill(resource, action);
        }

        public int forceFill(FluidStack resource, IFluidHandler.FluidAction action) {
            return super.fill(resource, action);
        }

        @Override
        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!SmartFluidTankBehaviour.this.extractionAllowed) {
                return FluidStack.EMPTY;
            }
            return super.drain(resource, action);
        }

        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            if (!SmartFluidTankBehaviour.this.extractionAllowed) {
                return FluidStack.EMPTY;
            }
            return super.drain(maxDrain, action);
        }
    }
}
