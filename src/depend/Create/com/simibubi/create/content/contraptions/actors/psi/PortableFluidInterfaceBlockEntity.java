/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 */
package com.simibubi.create.content.contraptions.actors.psi;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class PortableFluidInterfaceBlockEntity
extends PortableStorageInterfaceBlockEntity {
    protected IFluidHandler capability = this.createEmptyHandler();

    public PortableFluidInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.PORTABLE_FLUID_INTERFACE.get(), (be, context) -> be.capability);
    }

    @Override
    public void startTransferringTo(Contraption contraption, float distance) {
        this.capability = new InterfaceFluidHandler(contraption.getStorage().getFluids());
        this.invalidateCapability();
        super.startTransferringTo(contraption, distance);
    }

    @Override
    protected void invalidateCapability() {
        this.invalidateCapabilities();
    }

    @Override
    protected void stopTransferring() {
        this.capability = this.createEmptyHandler();
        this.invalidateCapability();
        super.stopTransferring();
    }

    private IFluidHandler createEmptyHandler() {
        return new InterfaceFluidHandler((IFluidHandler)new FluidTank(0));
    }

    public class InterfaceFluidHandler
    implements IFluidHandler {
        private IFluidHandler wrapped;

        public InterfaceFluidHandler(IFluidHandler wrapped) {
            this.wrapped = wrapped;
        }

        public int getTanks() {
            return this.wrapped.getTanks();
        }

        public FluidStack getFluidInTank(int tank) {
            return this.wrapped.getFluidInTank(tank);
        }

        public int getTankCapacity(int tank) {
            return this.wrapped.getTankCapacity(tank);
        }

        public boolean isFluidValid(int tank, FluidStack stack) {
            return this.wrapped.isFluidValid(tank, stack);
        }

        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!PortableFluidInterfaceBlockEntity.this.isConnected()) {
                return 0;
            }
            int fill = this.wrapped.fill(resource, action);
            if (fill > 0 && action.execute()) {
                this.keepAlive();
            }
            return fill;
        }

        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!PortableFluidInterfaceBlockEntity.this.canTransfer()) {
                return FluidStack.EMPTY;
            }
            FluidStack drain = this.wrapped.drain(resource, action);
            if (!drain.isEmpty() && action.execute()) {
                this.keepAlive();
            }
            return drain;
        }

        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            if (!PortableFluidInterfaceBlockEntity.this.canTransfer()) {
                return FluidStack.EMPTY;
            }
            FluidStack drain = this.wrapped.drain(maxDrain, action);
            if (!drain.isEmpty() && action.execute()) {
                this.keepAlive();
            }
            return drain;
        }

        public void keepAlive() {
            PortableFluidInterfaceBlockEntity.this.onContentTransferred();
        }
    }
}
