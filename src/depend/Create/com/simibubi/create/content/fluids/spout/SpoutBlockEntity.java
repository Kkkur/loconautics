/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 */
package com.simibubi.create.content.fluids.spout;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class SpoutBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation {
    public static final int FILLING_TIME = 20;
    protected BeltProcessingBehaviour beltProcessing;
    public int processingTicks = -1;
    public boolean sendSplash;
    public BlockSpoutingBehaviour customProcess;
    SmartFluidTankBehaviour tank;
    private boolean createdSweetRoll;
    private boolean createdHoneyApple;
    private boolean createdChocolateBerries;
    protected static int SPLASH_PARTICLE_COUNT = 20;

    public SpoutBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.SPOUT.get(), (be, context) -> {
            if (context != Direction.DOWN) {
                return be.tank.getCapability();
            }
            return null;
        });
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().expandTowards(0.0, -2.0, 0.0);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.tank = SmartFluidTankBehaviour.single(this, 1000);
        behaviours.add(this.tank);
        this.beltProcessing = new BeltProcessingBehaviour(this).whenItemEnters(this::onItemReceived).whileItemHeld(this::whenItemHeld);
        behaviours.add(this.beltProcessing);
        this.registerAwardables(behaviours, AllAdvancements.SPOUT, AllAdvancements.FOODS);
    }

    protected BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if (handler.blockEntity.isVirtual()) {
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }
        if (!FillingBySpout.canItemBeFilled(this.level, transported.stack)) {
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }
        if (this.tank.isEmpty()) {
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        if (FillingBySpout.getRequiredAmountForItem(this.level, transported.stack, this.getCurrentFluidInTank()) == -1) {
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }
        return BeltProcessingBehaviour.ProcessingResult.HOLD;
    }

    protected BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if (this.processingTicks != -1 && this.processingTicks != 5) {
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        if (!FillingBySpout.canItemBeFilled(this.level, transported.stack)) {
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }
        if (this.tank.isEmpty()) {
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        FluidStack fluid = this.getCurrentFluidInTank();
        int requiredAmountForItem = FillingBySpout.getRequiredAmountForItem(this.level, transported.stack, fluid.copy());
        if (requiredAmountForItem == -1) {
            return BeltProcessingBehaviour.ProcessingResult.PASS;
        }
        if (requiredAmountForItem > fluid.getAmount()) {
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        if (this.processingTicks == -1) {
            this.processingTicks = 20;
            this.notifyUpdate();
            AllSoundEvents.SPOUTING.playOnServer(this.level, (Vec3i)this.worldPosition, 0.75f, 0.9f + 0.2f * (float)Math.random());
            return BeltProcessingBehaviour.ProcessingResult.HOLD;
        }
        ItemStack out = FillingBySpout.fillItem(this.level, requiredAmountForItem, transported.stack, fluid);
        if (!out.isEmpty()) {
            transported.clearFanProcessingData();
            ArrayList<TransportedItemStack> outList = new ArrayList<TransportedItemStack>();
            TransportedItemStack held = null;
            TransportedItemStack result = transported.copy();
            result.stack = out;
            if (!transported.stack.isEmpty()) {
                held = transported.copy();
            }
            outList.add(result);
            handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(outList, held));
        }
        this.award(AllAdvancements.SPOUT);
        if (this.trackFoods()) {
            this.createdChocolateBerries |= AllItems.CHOCOLATE_BERRIES.isIn(out);
            this.createdHoneyApple |= AllItems.HONEYED_APPLE.isIn(out);
            this.createdSweetRoll |= AllItems.SWEET_ROLL.isIn(out);
            if (this.createdChocolateBerries && this.createdHoneyApple && this.createdSweetRoll) {
                this.award(AllAdvancements.FOODS);
            }
        }
        this.tank.getPrimaryHandler().setFluid(fluid);
        this.sendSplash = true;
        this.notifyUpdate();
        return BeltProcessingBehaviour.ProcessingResult.HOLD;
    }

    private FluidStack getCurrentFluidInTank() {
        return this.tank.getPrimaryHandler().getFluid();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("ProcessingTicks", this.processingTicks);
        if (this.sendSplash && clientPacket) {
            compound.putBoolean("Splash", true);
            this.sendSplash = false;
        }
        if (!this.trackFoods()) {
            return;
        }
        if (this.createdChocolateBerries) {
            NBTHelper.putMarker((CompoundTag)compound, (String)"ChocolateBerries");
        }
        if (this.createdHoneyApple) {
            NBTHelper.putMarker((CompoundTag)compound, (String)"HoneyApple");
        }
        if (this.createdSweetRoll) {
            NBTHelper.putMarker((CompoundTag)compound, (String)"SweetRoll");
        }
    }

    private boolean trackFoods() {
        return this.getBehaviour(AdvancementBehaviour.TYPE).isOwnerPresent();
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.processingTicks = compound.getInt("ProcessingTicks");
        this.createdChocolateBerries = compound.contains("ChocolateBerries");
        this.createdHoneyApple = compound.contains("HoneyApple");
        this.createdSweetRoll = compound.contains("SweetRoll");
        if (!clientPacket) {
            return;
        }
        if (compound.contains("Splash")) {
            this.spawnSplash(this.tank.getPrimaryTank().getRenderedFluid());
        }
    }

    @Override
    public void tick() {
        BlockPos filling;
        BlockSpoutingBehaviour behavior;
        super.tick();
        FluidStack currentFluidInTank = this.getCurrentFluidInTank();
        if (!(this.processingTicks != -1 || !this.isVirtual() && this.level.isClientSide() || currentFluidInTank.isEmpty() || (behavior = BlockSpoutingBehaviour.get(this.level, filling = this.worldPosition.below(2))) == null || behavior.fillBlock(this.level, filling, this, currentFluidInTank.copy(), true) <= 0)) {
            this.processingTicks = 20;
            this.customProcess = behavior;
            this.notifyUpdate();
        }
        if (this.processingTicks >= 0) {
            --this.processingTicks;
            if (this.processingTicks == 5 && this.customProcess != null) {
                int fillBlock = this.customProcess.fillBlock(this.level, this.worldPosition.below(2), this, currentFluidInTank.copy(), false);
                this.customProcess = null;
                if (fillBlock > 0) {
                    this.tank.getPrimaryHandler().setFluid(FluidHelper.copyStackWithAmount(currentFluidInTank, currentFluidInTank.getAmount() - fillBlock));
                    this.sendSplash = true;
                    this.notifyUpdate();
                }
            }
        }
        if (this.processingTicks >= 8 && this.level.isClientSide) {
            this.spawnProcessingParticles(this.tank.getPrimaryTank().getRenderedFluid());
        }
    }

    protected void spawnProcessingParticles(FluidStack fluid) {
        if (this.isVirtual() || fluid.isEmpty()) {
            return;
        }
        Vec3 vec = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        vec = vec.subtract(0.0, 0.5, 0.0);
        ParticleOptions particle = FluidFX.getFluidParticle(fluid);
        this.level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, 0.0, (double)-0.1f, 0.0);
    }

    protected void spawnSplash(FluidStack fluid) {
        if (this.isVirtual() || fluid.isEmpty()) {
            return;
        }
        Vec3 vec = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        vec = vec.subtract(0.0, 1.6875, 0.0);
        ParticleOptions particle = FluidFX.getFluidParticle(fluid);
        for (int i = 0; i < SPLASH_PARTICLE_COUNT; ++i) {
            Vec3 m = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)this.level.random, (float)0.125f);
            m = new Vec3(m.x, Math.abs(m.y), m.z);
            this.level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, m.x, m.y, m.z);
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return this.containedFluidTooltip(tooltip, isPlayerSneaking, (IFluidHandler)this.level.getCapability(Capabilities.FluidHandler.BLOCK, this.worldPosition, null));
    }
}
