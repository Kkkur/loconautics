/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.FluidType
 *  net.neoforged.neoforge.fluids.IFluidTank
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids.tank;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import java.util.Objects;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation,
IMultiBlockEntityContainer.Fluid {
    private static final int MAX_SIZE = 3;
    protected IFluidHandler fluidCapability;
    protected boolean forceFluidLevelUpdate = true;
    protected FluidTank tankInventory = this.createInventory();
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity = false;
    protected boolean updateCapability = false;
    protected boolean window = true;
    protected int luminosity;
    protected int width = 1;
    protected int height = 1;
    public BoilerData boiler = new BoilerData();
    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;
    private LerpedFloat fluidLevel;

    public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.refreshCapability();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.FLUID_TANK.get(), (be, context) -> {
            if (be.fluidCapability == null) {
                be.refreshCapability();
            }
            return be.fluidCapability;
        });
    }

    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(FluidTankBlockEntity.getCapacityMultiplier(), this::onFluidStackChanged);
    }

    protected void updateConnectivity() {
        this.updateConnectivity = false;
        if (this.level.isClientSide) {
            return;
        }
        if (!this.isController()) {
            return;
        }
        ConnectivityHandler.formMulti(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.syncCooldown > 0) {
            --this.syncCooldown;
            if (this.syncCooldown == 0 && this.queuedSync) {
                this.sendData();
            }
        }
        if (this.lastKnownPos == null) {
            this.lastKnownPos = this.getBlockPos();
        } else if (!this.lastKnownPos.equals((Object)this.worldPosition) && this.worldPosition != null) {
            this.onPositionChanged();
            return;
        }
        if (this.updateCapability) {
            this.updateCapability = false;
            this.refreshCapability();
        }
        if (this.updateConnectivity) {
            this.updateConnectivity();
        }
        if (this.fluidLevel != null) {
            this.fluidLevel.tickChaser();
        }
        if (this.isController()) {
            this.boiler.tick(this);
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.isController()) {
            this.boiler.updateOcclusion(this);
        }
    }

    @Override
    public BlockPos getLastKnownPos() {
        return this.lastKnownPos;
    }

    @Override
    public boolean isController() {
        return this.controller == null || this.worldPosition.getX() == this.controller.getX() && this.worldPosition.getY() == this.controller.getY() && this.worldPosition.getZ() == this.controller.getZ();
    }

    @Override
    public void initialize() {
        super.initialize();
        this.sendData();
        if (this.level.isClientSide) {
            this.invalidateRenderBoundingBox();
        }
    }

    private void onPositionChanged() {
        this.removeController(true);
        this.lastKnownPos = this.worldPosition;
    }

    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!this.hasLevel()) {
            return;
        }
        FluidType attributes = newFluidStack.getFluid().getFluidType();
        int luminosity = (int)((float)attributes.getLightLevel(newFluidStack) / 1.2f);
        boolean reversed = attributes.isLighterThanAir();
        int maxY = (int)(this.getFillState() * (float)this.height + 1.0f);
        for (int yOffset = 0; yOffset < this.height; ++yOffset) {
            boolean isBright;
            boolean bl = reversed ? this.height - yOffset <= maxY : (isBright = yOffset < maxY);
            int actualLuminosity = isBright ? luminosity : (luminosity > 0 ? 1 : 0);
            for (int xOffset = 0; xOffset < this.width; ++xOffset) {
                for (int zOffset = 0; zOffset < this.width; ++zOffset) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    FluidTankBlockEntity tankAt = (FluidTankBlockEntity)ConnectivityHandler.partAt(this.getType(), (BlockGetter)this.level, pos);
                    if (tankAt == null) continue;
                    this.level.updateNeighbourForOutputSignal(pos, tankAt.getBlockState().getBlock());
                    if (tankAt.luminosity == actualLuminosity) continue;
                    tankAt.setLuminosity(actualLuminosity);
                }
            }
        }
        if (!this.level.isClientSide) {
            this.setChanged();
            this.sendData();
        }
        if (this.isVirtual()) {
            if (this.fluidLevel == null) {
                this.fluidLevel = LerpedFloat.linear().startWithValue((double)this.getFillState());
            }
            this.fluidLevel.chase((double)this.getFillState(), 0.5, LerpedFloat.Chaser.EXP);
        }
    }

    protected void setLuminosity(int luminosity) {
        if (this.level.isClientSide) {
            return;
        }
        if (this.luminosity == luminosity) {
            return;
        }
        this.luminosity = luminosity;
        this.sendData();
    }

    public FluidTankBlockEntity getControllerBE() {
        if (this.isController() || !this.hasLevel()) {
            return this;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(this.controller);
        if (blockEntity instanceof FluidTankBlockEntity) {
            return (FluidTankBlockEntity)blockEntity;
        }
        return null;
    }

    public void applyFluidTankSize(int blocks) {
        this.tankInventory.setCapacity(blocks * FluidTankBlockEntity.getCapacityMultiplier());
        int overflow = this.tankInventory.getFluidAmount() - this.tankInventory.getCapacity();
        if (overflow > 0) {
            this.tankInventory.drain(overflow, IFluidHandler.FluidAction.EXECUTE);
        }
        this.forceFluidLevelUpdate = true;
    }

    @Override
    public void removeController(boolean keepFluids) {
        if (this.level.isClientSide) {
            return;
        }
        this.updateConnectivity = true;
        if (!keepFluids) {
            this.applyFluidTankSize(1);
        }
        this.controller = null;
        this.width = 1;
        this.height = 1;
        this.boiler.clear();
        this.onFluidStackChanged(this.tankInventory.getFluid());
        BlockState state = this.getBlockState();
        if (FluidTankBlock.isTank(state)) {
            state = (BlockState)state.setValue((Property)FluidTankBlock.BOTTOM, (Comparable)Boolean.valueOf(true));
            state = (BlockState)state.setValue((Property)FluidTankBlock.TOP, (Comparable)Boolean.valueOf(true));
            state = (BlockState)state.setValue(FluidTankBlock.SHAPE, (Comparable)((Object)(this.window ? FluidTankBlock.Shape.WINDOW : FluidTankBlock.Shape.PLAIN)));
            this.getLevel().setBlock(this.worldPosition, state, 22);
        }
        this.refreshCapability();
        this.setChanged();
        this.sendData();
    }

    public void toggleWindows() {
        FluidTankBlockEntity be = this.getControllerBE();
        if (be == null) {
            return;
        }
        if (be.boiler.isActive()) {
            return;
        }
        be.setWindows(!be.window);
    }

    public void updateBoilerTemperature() {
        FluidTankBlockEntity be = this.getControllerBE();
        if (be == null) {
            return;
        }
        if (!be.boiler.isActive()) {
            return;
        }
        be.boiler.needsHeatLevelUpdate = true;
    }

    public void sendDataImmediately() {
        this.syncCooldown = 0;
        this.queuedSync = false;
        this.sendData();
    }

    @Override
    public void sendData() {
        if (this.syncCooldown > 0) {
            this.queuedSync = true;
            return;
        }
        super.sendData();
        this.queuedSync = false;
        this.syncCooldown = 8;
    }

    public void setWindows(boolean window) {
        this.window = window;
        for (int yOffset = 0; yOffset < this.height; ++yOffset) {
            for (int xOffset = 0; xOffset < this.width; ++xOffset) {
                for (int zOffset = 0; zOffset < this.width; ++zOffset) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = this.level.getBlockState(pos);
                    if (!FluidTankBlock.isTank(blockState)) continue;
                    FluidTankBlock.Shape shape = FluidTankBlock.Shape.PLAIN;
                    if (window) {
                        if (this.width == 1) {
                            shape = FluidTankBlock.Shape.WINDOW;
                        }
                        if (this.width == 2) {
                            FluidTankBlock.Shape shape2 = xOffset == 0 ? (zOffset == 0 ? FluidTankBlock.Shape.WINDOW_NW : FluidTankBlock.Shape.WINDOW_SW) : (shape = zOffset == 0 ? FluidTankBlock.Shape.WINDOW_NE : FluidTankBlock.Shape.WINDOW_SE);
                        }
                        if (this.width == 3 && Math.abs(Math.abs(xOffset) - Math.abs(zOffset)) == 1) {
                            shape = FluidTankBlock.Shape.WINDOW;
                        }
                    }
                    this.level.setBlock(pos, (BlockState)blockState.setValue(FluidTankBlock.SHAPE, (Comparable)((Object)shape)), 22);
                    this.level.getChunkSource().getLightEngine().checkBlock(pos);
                }
            }
        }
    }

    public void updateBoilerState() {
        if (!this.isController()) {
            return;
        }
        boolean wasBoiler = this.boiler.isActive();
        boolean changed = this.boiler.evaluate(this);
        if (wasBoiler != this.boiler.isActive()) {
            if (this.boiler.isActive()) {
                this.setWindows(false);
            }
            for (int yOffset = 0; yOffset < this.height; ++yOffset) {
                for (int xOffset = 0; xOffset < this.width; ++xOffset) {
                    for (int zOffset = 0; zOffset < this.width; ++zOffset) {
                        BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.offset(xOffset, yOffset, zOffset));
                        if (!(blockEntity instanceof FluidTankBlockEntity)) continue;
                        FluidTankBlockEntity fbe = (FluidTankBlockEntity)blockEntity;
                        fbe.refreshCapability();
                    }
                }
            }
        }
        if (changed) {
            this.notifyUpdate();
            this.boiler.checkPipeOrganAdvancement(this);
        }
    }

    @Override
    public void setController(BlockPos controller) {
        if (this.level.isClientSide && !this.isVirtual()) {
            return;
        }
        if (controller.equals((Object)this.controller)) {
            return;
        }
        this.controller = controller;
        this.refreshCapability();
        this.setChanged();
        this.sendData();
    }

    void refreshCapability() {
        this.fluidCapability = this.handlerForCapability();
        this.invalidateCapabilities();
    }

    private IFluidHandler handlerForCapability() {
        return this.isController() ? (this.boiler.isActive() ? this.boiler.createHandler() : this.tankInventory) : (this.getControllerBE() != null ? this.getControllerBE().handlerForCapability() : new FluidTank(0));
    }

    @Override
    public BlockPos getController() {
        return this.isController() ? this.worldPosition : this.controller;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (this.isController()) {
            return super.createRenderBoundingBox().expandTowards((double)(this.width - 1), (double)(this.height - 1), (double)(this.width - 1));
        }
        return super.createRenderBoundingBox();
    }

    @Nullable
    public FluidTankBlockEntity getOtherFluidTankBlockEntity(Direction direction) {
        BlockEntity otherBE = this.level.getBlockEntity(this.worldPosition.relative(direction));
        if (otherBE instanceof FluidTankBlockEntity) {
            return (FluidTankBlockEntity)otherBE;
        }
        return null;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        FluidTankBlockEntity controllerBE = this.getControllerBE();
        if (controllerBE == null) {
            return false;
        }
        if (controllerBE.boiler.addToGoggleTooltip(tooltip, isPlayerSneaking, controllerBE.getTotalTankSize())) {
            return true;
        }
        return this.containedFluidTooltip(tooltip, isPlayerSneaking, (IFluidHandler)this.level.getCapability(Capabilities.FluidHandler.BLOCK, controllerBE.getBlockPos(), null));
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        boolean changeOfController;
        super.read(compound, registries, clientPacket);
        BlockPos controllerBefore = this.controller;
        int prevSize = this.width;
        int prevHeight = this.height;
        int prevLum = this.luminosity;
        this.updateConnectivity = compound.contains("Uninitialized");
        this.luminosity = compound.getInt("Luminosity");
        this.lastKnownPos = null;
        if (compound.contains("LastKnownPos")) {
            this.lastKnownPos = NBTHelper.readBlockPos((CompoundTag)compound, (String)"LastKnownPos");
        }
        this.controller = null;
        if (compound.contains("Controller")) {
            this.controller = NBTHelper.readBlockPos((CompoundTag)compound, (String)"Controller");
        }
        if (this.isController()) {
            this.window = compound.getBoolean("Window");
            this.width = compound.getInt("Size");
            this.height = compound.getInt("Height");
            this.tankInventory.setCapacity(this.getTotalTankSize() * FluidTankBlockEntity.getCapacityMultiplier());
            this.tankInventory.readFromNBT(registries, compound.getCompound("TankContent"));
            if (this.tankInventory.getSpace() < 0) {
                this.tankInventory.drain(-this.tankInventory.getSpace(), IFluidHandler.FluidAction.EXECUTE);
            }
        }
        this.boiler.read(compound.getCompound("Boiler"), this.width * this.width * this.height);
        if (compound.contains("ForceFluidLevel") || this.fluidLevel == null) {
            this.fluidLevel = LerpedFloat.linear().startWithValue((double)this.getFillState());
        }
        this.updateCapability = true;
        if (!clientPacket) {
            return;
        }
        boolean bl = changeOfController = !Objects.equals(controllerBefore, this.controller);
        if (changeOfController || prevSize != this.width || prevHeight != this.height) {
            if (this.hasLevel()) {
                this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 16);
            }
            if (this.isController()) {
                this.tankInventory.setCapacity(FluidTankBlockEntity.getCapacityMultiplier() * this.getTotalTankSize());
            }
            this.invalidateRenderBoundingBox();
        }
        if (this.isController()) {
            float fillState = this.getFillState();
            if (compound.contains("ForceFluidLevel") || this.fluidLevel == null) {
                this.fluidLevel = LerpedFloat.linear().startWithValue((double)fillState);
            }
            this.fluidLevel.chase((double)fillState, 0.5, LerpedFloat.Chaser.EXP);
        }
        if (this.luminosity != prevLum && this.hasLevel()) {
            this.level.getChunkSource().getLightEngine().checkBlock(this.worldPosition);
        }
        if (compound.contains("LazySync")) {
            this.fluidLevel.chase((double)this.fluidLevel.getChaseTarget(), 0.125, LerpedFloat.Chaser.EXP);
        }
    }

    public float getFillState() {
        return (float)this.tankInventory.getFluidAmount() / (float)this.tankInventory.getCapacity();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.updateConnectivity) {
            compound.putBoolean("Uninitialized", true);
        }
        compound.put("Boiler", (Tag)this.boiler.write());
        if (this.lastKnownPos != null) {
            compound.put("LastKnownPos", NbtUtils.writeBlockPos((BlockPos)this.lastKnownPos));
        }
        if (!this.isController()) {
            compound.put("Controller", NbtUtils.writeBlockPos((BlockPos)this.controller));
        }
        if (this.isController()) {
            compound.putBoolean("Window", this.window);
            compound.put("TankContent", (Tag)this.tankInventory.writeToNBT(registries, new CompoundTag()));
            compound.putInt("Size", this.width);
            compound.putInt("Height", this.height);
        }
        compound.putInt("Luminosity", this.luminosity);
        super.write(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        if (this.forceFluidLevelUpdate) {
            compound.putBoolean("ForceFluidLevel", true);
        }
        if (this.queuedSync) {
            compound.putBoolean("LazySync", true);
        }
        this.forceFluidLevelUpdate = false;
    }

    @Override
    public void writeSafe(CompoundTag compound, HolderLookup.Provider registries) {
        if (this.isController()) {
            compound.putBoolean("Window", this.window);
            compound.putInt("Size", this.width);
            compound.putInt("Height", this.height);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.registerAwardables(behaviours, AllAdvancements.STEAM_ENGINE_MAXED, AllAdvancements.PIPE_ORGAN);
    }

    public FluidTank getTankInventory() {
        return this.tankInventory;
    }

    public int getTotalTankSize() {
        return this.width * this.width * this.height;
    }

    public static int getMaxSize() {
        return 3;
    }

    public static int getCapacityMultiplier() {
        return (Integer)AllConfigs.server().fluids.fluidTankCapacity.get() * 1000;
    }

    public static int getMaxHeight() {
        return (Integer)AllConfigs.server().fluids.fluidTankMaxHeight.get();
    }

    public LerpedFloat getFluidLevel() {
        return this.fluidLevel;
    }

    public void setFluidLevel(LerpedFloat fluidLevel) {
        this.fluidLevel = fluidLevel;
    }

    @Override
    public void preventConnectivityUpdate() {
        this.updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (FluidTankBlock.isTank(state)) {
            state = (BlockState)state.setValue((Property)FluidTankBlock.BOTTOM, (Comparable)Boolean.valueOf(this.getController().getY() == this.getBlockPos().getY()));
            state = (BlockState)state.setValue((Property)FluidTankBlock.TOP, (Comparable)Boolean.valueOf(this.getController().getY() + this.height - 1 == this.getBlockPos().getY()));
            this.level.setBlock(this.getBlockPos(), state, 6);
        }
        if (this.isController()) {
            this.setWindows(this.window);
        }
        this.onFluidStackChanged(this.tankInventory.getFluid());
        this.updateBoilerState();
        this.setChanged();
    }

    @Override
    public void setExtraData(@Nullable Object data) {
        if (data instanceof Boolean) {
            this.window = (Boolean)data;
        }
    }

    @Override
    @Nullable
    public Object getExtraData() {
        return this.window;
    }

    @Override
    public Object modifyExtraData(Object data) {
        if (data instanceof Boolean) {
            Boolean windows = (Boolean)data;
            windows = windows | this.window;
            return windows;
        }
        return data;
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y) {
            return FluidTankBlockEntity.getMaxHeight();
        }
        return this.getMaxWidth();
    }

    @Override
    public int getMaxWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public int getTankSize(int tank) {
        return FluidTankBlockEntity.getCapacityMultiplier();
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        this.applyFluidTankSize(blocks);
    }

    @Override
    public IFluidTank getTank(int tank) {
        return this.tankInventory;
    }

    @Override
    public FluidStack getFluid(int tank) {
        return this.tankInventory.getFluid().copy();
    }
}
