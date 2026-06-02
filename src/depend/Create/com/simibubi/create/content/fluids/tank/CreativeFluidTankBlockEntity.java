/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.simibubi.create.content.fluids.tank;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class CreativeFluidTankBlockEntity
extends FluidTankBlockEntity {
    public CreativeFluidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.CREATIVE_FLUID_TANK.get(), (be, context) -> {
            if (be.fluidCapability == null) {
                be.refreshCapability();
            }
            return be.fluidCapability;
        });
    }

    @Override
    protected SmartFluidTank createInventory() {
        return new CreativeSmartFluidTank(CreativeFluidTankBlockEntity.getCapacityMultiplier(), this::onFluidStackChanged);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return false;
    }

    public static class CreativeSmartFluidTank
    extends SmartFluidTank {
        public static final Codec<CreativeSmartFluidTank> CODEC = RecordCodecBuilder.create(i -> i.group((App)FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(FluidTank::getFluid), (App)ExtraCodecs.NON_NEGATIVE_INT.fieldOf("capacity").forGetter(FluidTank::getCapacity)).apply((Applicative)i, (fluid, capacity) -> {
            CreativeSmartFluidTank tank = new CreativeSmartFluidTank((int)capacity, $ -> {});
            tank.setFluid((FluidStack)fluid);
            return tank;
        }));

        public CreativeSmartFluidTank(int capacity, Consumer<FluidStack> updateCallback) {
            super(capacity, updateCallback);
        }

        public int getFluidAmount() {
            return this.getFluid().isEmpty() ? 0 : this.getTankCapacity(0);
        }

        public void setContainedFluid(FluidStack fluidStack) {
            this.fluid = fluidStack.copy();
            if (!fluidStack.isEmpty()) {
                this.fluid.setAmount(this.getTankCapacity(0));
            }
            this.onContentsChanged();
        }

        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            return resource.getAmount();
        }

        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            return super.drain(resource, IFluidHandler.FluidAction.SIMULATE);
        }

        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            return super.drain(maxDrain, IFluidHandler.FluidAction.SIMULATE);
        }
    }
}
