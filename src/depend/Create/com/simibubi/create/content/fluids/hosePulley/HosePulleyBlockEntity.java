/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.hosePulley;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlock;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyFluidHandler;
import com.simibubi.create.content.fluids.transfer.FluidDrainingBehaviour;
import com.simibubi.create.content.fluids.transfer.FluidFillingBehaviour;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;

public class HosePulleyBlockEntity
extends KineticBlockEntity {
    LerpedFloat offset = LerpedFloat.linear().startWithValue(0.0);
    boolean isMoving = true;
    private SmartFluidTank internalTank = new SmartFluidTank(1500, this::onTankContentsChanged);
    private FluidDrainingBehaviour drainer;
    private FluidFillingBehaviour filler;
    private HosePulleyFluidHandler handler = new HosePulleyFluidHandler(this.internalTank, this.filler, this.drainer, () -> this.worldPosition.below((int)Math.ceil(this.offset.getValue())), () -> !this.isMoving);
    private boolean infinite;

    public HosePulleyBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.HOSE_PULLEY.get(), (be, context) -> {
            if (context == null || HosePulleyBlock.hasPipeTowards((LevelReader)be.level, be.worldPosition, be.getBlockState(), context)) {
                return be.handler;
            }
            return null;
        });
    }

    @Override
    public void sendData() {
        this.infinite = this.filler.isInfinite() || this.drainer.isInfinite();
        super.sendData();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean addToGoggleTooltip = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (this.infinite) {
            TooltipHelper.addHint(tooltip, "hint.hose_pulley", new Object[0]);
        }
        return addToGoggleTooltip;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.drainer = new FluidDrainingBehaviour(this);
        this.filler = new FluidFillingBehaviour(this);
        behaviours.add(this.drainer);
        behaviours.add(this.filler);
        super.addBehaviours(behaviours);
        this.registerAwardables(behaviours, AllAdvancements.HOSE_PULLEY, AllAdvancements.HOSE_PULLEY_LAVA);
    }

    protected void onTankContentsChanged(FluidStack contents) {
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        this.isMoving = true;
        if (this.getSpeed() == 0.0f) {
            this.offset.forceNextSync();
            this.offset.setValue((double)Math.round(this.offset.getValue()));
            this.isMoving = false;
        }
        if (this.isMoving) {
            float newOffset = this.offset.getValue() + this.getMovementSpeed();
            if (newOffset < 0.0f) {
                this.isMoving = false;
            }
            if (!this.level.getBlockState(this.worldPosition.below((int)Math.ceil(newOffset))).canBeReplaced()) {
                this.isMoving = false;
            }
            if (this.isMoving) {
                this.drainer.reset();
                this.filler.reset();
            }
        }
        super.onSpeedChanged(previousSpeed);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().expandTowards(0.0, (double)(-this.offset.getValue()), 0.0);
    }

    @Override
    public void tick() {
        super.tick();
        float newOffset = this.offset.getValue() + this.getMovementSpeed();
        if (newOffset < 0.0f) {
            newOffset = 0.0f;
            this.isMoving = false;
        }
        if (!this.level.getBlockState(this.worldPosition.below((int)Math.ceil(newOffset))).canBeReplaced()) {
            newOffset = (int)newOffset;
            this.isMoving = false;
        }
        if (this.getSpeed() == 0.0f) {
            this.isMoving = false;
        }
        this.offset.setValue((double)newOffset);
        this.invalidateRenderBoundingBox();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.level.isClientSide) {
            return;
        }
        if (this.isMoving) {
            return;
        }
        int ceil = (int)Math.ceil(this.offset.getValue() + this.getMovementSpeed());
        if (this.getMovementSpeed() > 0.0f && this.level.getBlockState(this.worldPosition.below(ceil)).canBeReplaced()) {
            this.isMoving = true;
            this.drainer.reset();
            this.filler.reset();
            return;
        }
        this.sendData();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (clientPacket) {
            this.offset.forceNextSync();
        }
        compound.put("Offset", (Tag)this.offset.writeNBT());
        compound.put("Tank", (Tag)this.internalTank.writeToNBT(registries, new CompoundTag()));
        super.write(compound, registries, clientPacket);
        if (clientPacket) {
            compound.putBoolean("Infinite", this.infinite);
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.offset.readNBT(compound.getCompound("Offset"), clientPacket);
        this.internalTank.readFromNBT(registries, compound.getCompound("Tank"));
        super.read(compound, registries, clientPacket);
        if (clientPacket) {
            this.infinite = compound.getBoolean("Infinite");
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.invalidateCapabilities();
    }

    public float getMovementSpeed() {
        float movementSpeed = HosePulleyBlockEntity.convertToLinear(this.getSpeed());
        if (this.level.isClientSide) {
            movementSpeed *= ServerSpeedProvider.get();
        }
        return movementSpeed;
    }

    public float getInterpolatedOffset(float pt) {
        return Math.max(this.offset.getValue(pt), 0.1875f);
    }
}
