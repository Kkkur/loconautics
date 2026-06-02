/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.AllFluids;
import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.content.fluids.FluidReactions;
import com.simibubi.create.content.fluids.pipes.VanillaFluidTargets;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.mixin.accessor.FlowingFluidAccessor;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class OpenEndedPipe
extends FlowSource {
    private Level world;
    private BlockPos pos;
    private AABB aoe;
    private OpenEndFluidHandler fluidHandler;
    private BlockPos outputPos;
    private boolean wasPulling;
    private final ICapabilityProvider<IFluidHandler> fluidHandlerProvider = ICapabilityProvider.of(() -> this.fluidHandler);

    public OpenEndedPipe(BlockFace face) {
        super(face);
        this.fluidHandler = new OpenEndFluidHandler();
        this.outputPos = face.getConnectedPos();
        this.pos = face.getPos();
        this.aoe = new AABB(this.outputPos).expandTowards(0.0, -1.0, 0.0);
        if (face.getFace() == Direction.DOWN) {
            this.aoe = this.aoe.expandTowards(0.0, -1.0, 0.0);
        }
    }

    public Level getWorld() {
        return this.world;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockPos getOutputPos() {
        return this.outputPos;
    }

    public AABB getAOE() {
        return this.aoe;
    }

    @Override
    public void manageSource(Level world, BlockEntity networkBE) {
        this.world = world;
    }

    @Override
    @Nullable
    public ICapabilityProvider<IFluidHandler> provideHandler() {
        return this.fluidHandlerProvider;
    }

    @Override
    public boolean isEndpoint() {
        return true;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag compound = new CompoundTag();
        this.fluidHandler.writeToNBT(registries, compound);
        compound.putBoolean("Pulling", this.wasPulling);
        compound.put("Location", (Tag)this.location.serializeNBT());
        return compound;
    }

    public static OpenEndedPipe fromNBT(CompoundTag compound, HolderLookup.Provider registries, BlockPos blockEntityPos) {
        BlockFace fromNBT = BlockFace.fromNBT((CompoundTag)compound.getCompound("Location"));
        OpenEndedPipe oep = new OpenEndedPipe(new BlockFace(blockEntityPos, fromNBT.getFace()));
        oep.fluidHandler.readFromNBT(registries, compound);
        oep.wasPulling = compound.getBoolean("Pulling");
        return oep;
    }

    private FluidStack removeFluidFromSpace(boolean simulate) {
        FluidStack empty = FluidStack.EMPTY;
        if (this.world == null) {
            return empty;
        }
        if (!this.world.isLoaded(this.outputPos)) {
            return empty;
        }
        BlockState state = this.world.getBlockState(this.outputPos);
        FluidState fluidState = state.getFluidState();
        boolean waterlog = state.hasProperty((Property)BlockStateProperties.WATERLOGGED);
        FluidStack drainBlock = VanillaFluidTargets.drainBlock(this.world, this.outputPos, state, simulate);
        if (!drainBlock.isEmpty()) {
            if (!simulate && state.hasProperty((Property)BlockStateProperties.LEVEL_HONEY) && AllFluids.HONEY.is((Object)drainBlock.getFluid())) {
                AdvancementBehaviour.tryAward((BlockGetter)this.world, this.pos, AllAdvancements.HONEY_DRAIN);
            }
            return drainBlock;
        }
        if (!waterlog && !state.canBeReplaced()) {
            return empty;
        }
        if (fluidState.isEmpty() || !fluidState.isSource()) {
            return empty;
        }
        FluidStack stack = new FluidStack(fluidState.getType(), 1000);
        if (simulate) {
            return stack;
        }
        if (FluidHelper.isWater(stack.getFluid())) {
            AdvancementBehaviour.tryAward((BlockGetter)this.world, this.pos, AllAdvancements.WATER_SUPPLY);
        }
        if (waterlog) {
            this.world.setBlock(this.outputPos, (BlockState)state.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)), 3);
            this.world.scheduleTick(this.outputPos, (Fluid)Fluids.WATER, 1);
        } else {
            FlowingFluidAccessor flowing;
            FluidState potentiallyFilled;
            BlockState newState = (BlockState)fluidState.createLegacyBlock().setValue((Property)LiquidBlock.LEVEL, (Comparable)Integer.valueOf(14));
            FluidState newFluidState = newState.getFluidState();
            Fluid fluid = newFluidState.getType();
            if (fluid instanceof FlowingFluidAccessor && (potentiallyFilled = (flowing = (FlowingFluidAccessor)fluid).create$getNewLiquid(this.world, this.outputPos, newState)).equals(fluidState)) {
                return stack;
            }
            this.world.setBlock(this.outputPos, newState, 3);
        }
        return stack;
    }

    private boolean provideFluidToSpace(FluidStack fluid, boolean simulate) {
        if (this.world == null) {
            return false;
        }
        if (!this.world.isLoaded(this.outputPos)) {
            return false;
        }
        BlockState state = this.world.getBlockState(this.outputPos);
        FluidState fluidState = state.getFluidState();
        boolean waterlog = state.hasProperty((Property)BlockStateProperties.WATERLOGGED);
        if (!waterlog && !state.canBeReplaced()) {
            return false;
        }
        if (fluid.isEmpty()) {
            return false;
        }
        if (!(fluid.getFluid() instanceof FlowingFluid)) {
            return false;
        }
        if (!FluidHelper.hasBlockState(fluid.getFluid())) {
            return true;
        }
        if (!fluidState.isEmpty() && FluidHelper.convertToStill(fluidState.getType()) != fluid.getFluid()) {
            FluidReactions.handlePipeSpillCollision(this.world, this.outputPos, fluid.getFluid(), fluidState);
            return false;
        }
        if (fluidState.isSource()) {
            return false;
        }
        if (waterlog && fluid.getFluid() != Fluids.WATER) {
            return false;
        }
        if (simulate) {
            return true;
        }
        if (!((Boolean)AllConfigs.server().fluids.pipesPlaceFluidSourceBlocks.get()).booleanValue()) {
            return true;
        }
        if (this.world.dimensionType().ultraWarm() && FluidHelper.isTag(fluid, (TagKey<Fluid>)FluidTags.WATER)) {
            int i = this.outputPos.getX();
            int j = this.outputPos.getY();
            int k = this.outputPos.getZ();
            this.world.playSound(null, (double)i, (double)j, (double)k, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.8f);
            return true;
        }
        if (waterlog) {
            this.world.setBlock(this.outputPos, (BlockState)state.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(true)), 3);
            this.world.scheduleTick(this.outputPos, (Fluid)Fluids.WATER, 1);
            return true;
        }
        this.world.setBlock(this.outputPos, fluid.getFluid().defaultFluidState().createLegacyBlock(), 3);
        return true;
    }

    private class OpenEndFluidHandler
    extends FluidTank {
        public OpenEndFluidHandler() {
            super(1000);
        }

        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            OpenPipeEffectHandler effectHandler;
            if (OpenEndedPipe.this.world == null) {
                return 0;
            }
            if (!OpenEndedPipe.this.world.isLoaded(OpenEndedPipe.this.outputPos)) {
                return 0;
            }
            if (resource.isEmpty()) {
                return 0;
            }
            if (!OpenEndedPipe.this.provideFluidToSpace(resource, true)) {
                return 0;
            }
            FluidStack containedFluidStack = this.getFluid();
            boolean hasBlockState = FluidHelper.hasBlockState(containedFluidStack.getFluid());
            if (!containedFluidStack.isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)containedFluidStack, (FluidStack)resource)) {
                this.setFluid(FluidStack.EMPTY);
            }
            if (OpenEndedPipe.this.wasPulling) {
                OpenEndedPipe.this.wasPulling = false;
            }
            if ((effectHandler = OpenPipeEffectHandler.REGISTRY.get(resource.getFluid())) != null && !hasBlockState) {
                resource = FluidHelper.copyStackWithAmount(resource, 1);
            }
            int fill = super.fill(resource, action);
            if (action.simulate()) {
                return fill;
            }
            if (effectHandler != null && !resource.isEmpty()) {
                FluidStack exposed = hasBlockState ? resource.copy() : resource;
                effectHandler.apply(OpenEndedPipe.this.world, OpenEndedPipe.this.aoe, exposed);
            }
            if ((this.getFluidAmount() == 1000 || !hasBlockState) && OpenEndedPipe.this.provideFluidToSpace(containedFluidStack, false)) {
                this.setFluid(FluidStack.EMPTY);
            }
            return fill;
        }

        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            return this.drainInner(resource.getAmount(), resource, action);
        }

        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            return this.drainInner(maxDrain, null, action);
        }

        private FluidStack drainInner(int amount, @Nullable FluidStack filter, IFluidHandler.FluidAction action) {
            FluidStack drainedFromInternal;
            boolean filterPresent;
            FluidStack empty = FluidStack.EMPTY;
            boolean bl = filterPresent = filter != null;
            if (OpenEndedPipe.this.world == null) {
                return empty;
            }
            if (!OpenEndedPipe.this.world.isLoaded(OpenEndedPipe.this.outputPos)) {
                return empty;
            }
            if (amount == 0) {
                return empty;
            }
            if (amount > 1000) {
                amount = 1000;
                if (filterPresent) {
                    filter = FluidHelper.copyStackWithAmount(filter, amount);
                }
            }
            if (!OpenEndedPipe.this.wasPulling) {
                OpenEndedPipe.this.wasPulling = true;
            }
            FluidStack fluidStack = drainedFromInternal = filterPresent ? super.drain(filter, action) : super.drain(amount, action);
            if (!drainedFromInternal.isEmpty()) {
                return drainedFromInternal;
            }
            FluidStack drainedFromWorld = OpenEndedPipe.this.removeFluidFromSpace(action.simulate());
            if (drainedFromWorld.isEmpty()) {
                return FluidStack.EMPTY;
            }
            if (filterPresent && !FluidStack.isSameFluidSameComponents((FluidStack)drainedFromWorld, (FluidStack)filter)) {
                return FluidStack.EMPTY;
            }
            int remainder = drainedFromWorld.getAmount() - amount;
            drainedFromWorld.setAmount(amount);
            if (!action.simulate() && remainder > 0) {
                if (!this.getFluid().isEmpty() && !FluidStack.isSameFluidSameComponents((FluidStack)this.getFluid(), (FluidStack)drainedFromWorld)) {
                    this.setFluid(FluidStack.EMPTY);
                }
                super.fill(FluidHelper.copyStackWithAmount(drainedFromWorld, remainder), IFluidHandler.FluidAction.EXECUTE);
            }
            return drainedFromWorld;
        }
    }
}
