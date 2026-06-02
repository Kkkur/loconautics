/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.content.kinetics.belt;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.BeltModel;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler;
import com.simibubi.create.content.kinetics.belt.transport.BeltTunnelInteractionHandler;
import com.simibubi.create.content.kinetics.belt.transport.ItemHandlerBeltSegment;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.IItemHandler;

public class BeltBlockEntity
extends KineticBlockEntity
implements Clearable {
    public Map<Entity, BeltMovementHandler.TransportedEntityInfo> passengers;
    public Optional<DyeColor> color;
    public int beltLength;
    public int index;
    public Direction lastInsert;
    public CasingType casing;
    public boolean covered;
    protected BlockPos controller = BlockPos.ZERO;
    protected BeltInventory inventory;
    protected IItemHandler itemHandler = null;
    public VersionedInventoryTrackerBehaviour invVersionTracker;
    public CompoundTag trackerUpdateTag;

    public BeltBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.casing = CasingType.NONE;
        this.color = Optional.empty();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.BELT.get(), (be, context) -> {
            if (!BeltBlock.canTransportObjects(be.getBlockState())) {
                return null;
            }
            if (!be.isRemoved() && be.itemHandler == null) {
                be.initializeItemHandler();
            }
            return be.itemHandler;
        });
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen(this::canInsertFrom).setInsertionHandler(this::tryInsertingFromSide).considerOccupiedWhen(this::isOccupied));
        behaviours.add(new TransportedItemStackHandlerBehaviour(this, this::applyToAllItems).withStackPlacement(this::getWorldPositionOf));
        this.invVersionTracker = new VersionedInventoryTrackerBehaviour(this);
        behaviours.add(this.invVersionTracker);
    }

    @Override
    public void tick() {
        if (this.beltLength == 0) {
            BeltBlock.initBelt(this.level, this.worldPosition);
        }
        super.tick();
        if (!AllBlocks.BELT.has(this.level.getBlockState(this.worldPosition))) {
            return;
        }
        this.initializeItemHandler();
        if (!this.isController()) {
            return;
        }
        this.invalidateRenderBoundingBox();
        this.getInventory().tick();
        if (this.getSpeed() == 0.0f) {
            return;
        }
        if (this.passengers == null) {
            this.passengers = new HashMap<Entity, BeltMovementHandler.TransportedEntityInfo>();
        }
        ArrayList toRemove = new ArrayList();
        this.passengers.forEach((entity, info) -> {
            boolean leftTheBelt;
            boolean canBeTransported = BeltMovementHandler.canBeTransported(entity);
            boolean bl = leftTheBelt = info.getTicksSinceLastCollision() > (this.getBlockState().getValue(BeltBlock.SLOPE) != BeltSlope.HORIZONTAL ? 3 : 1);
            if (!canBeTransported || leftTheBelt) {
                toRemove.add(entity);
                return;
            }
            info.tick();
            BeltMovementHandler.transportEntity(this, entity, info);
        });
        toRemove.forEach(this.passengers::remove);
    }

    @Override
    public float calculateStressApplied() {
        if (!this.isController()) {
            return 0.0f;
        }
        return super.calculateStressApplied();
    }

    @Override
    public AABB createRenderBoundingBox() {
        if (!this.isController()) {
            return super.createRenderBoundingBox();
        }
        return super.createRenderBoundingBox().inflate((double)(this.beltLength + 1));
    }

    protected void initializeItemHandler() {
        if (this.level.isClientSide || this.itemHandler != null) {
            return;
        }
        if (this.beltLength == 0 || this.controller == null) {
            return;
        }
        if (!this.level.isLoaded(this.controller)) {
            return;
        }
        BlockEntity be = this.level.getBlockEntity(this.controller);
        if (be == null || !(be instanceof BeltBlockEntity)) {
            return;
        }
        BeltInventory inventory = ((BeltBlockEntity)be).getInventory();
        if (inventory == null) {
            return;
        }
        this.itemHandler = new ItemHandlerBeltSegment(inventory, this.index);
        this.invalidateCapabilities();
    }

    public void clearContent() {
        if (this.inventory != null) {
            this.inventory.getTransportedItems().clear();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.isController()) {
            this.getInventory().ejectAll();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.invalidateCapabilities();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.controller != null) {
            compound.put("Controller", NbtUtils.writeBlockPos((BlockPos)this.controller));
        }
        compound.putBoolean("IsController", this.isController());
        compound.putInt("Length", this.beltLength);
        compound.putInt("Index", this.index);
        NBTHelper.writeEnum((CompoundTag)compound, (String)"Casing", (Enum)this.casing);
        compound.putBoolean("Covered", this.covered);
        this.color.ifPresent(dyeColor -> NBTHelper.writeEnum((CompoundTag)compound, (String)"Dye", (Enum)dyeColor));
        if (this.isController()) {
            compound.put("Inventory", (Tag)this.getInventory().write(registries));
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (compound.getBoolean("IsController")) {
            this.controller = this.worldPosition;
        }
        Optional<Object> optional = this.color = compound.contains("Dye") ? Optional.of((DyeColor)NBTHelper.readEnum((CompoundTag)compound, (String)"Dye", DyeColor.class)) : Optional.empty();
        if (!this.wasMoved) {
            if (!this.isController()) {
                this.controller = NBTHelper.readBlockPos((CompoundTag)compound, (String)"Controller");
            }
            this.trackerUpdateTag = compound;
            this.index = compound.getInt("Index");
            this.beltLength = compound.getInt("Length");
        }
        if (this.isController()) {
            this.getInventory().read(compound.getCompound("Inventory"), registries);
        }
        CasingType casingBefore = this.casing;
        boolean coverBefore = this.covered;
        this.casing = (CasingType)NBTHelper.readEnum((CompoundTag)compound, (String)"Casing", CasingType.class);
        this.covered = compound.getBoolean("Covered");
        if (!clientPacket) {
            return;
        }
        if (casingBefore == this.casing && coverBefore == this.covered) {
            return;
        }
        if (!this.isVirtual()) {
            this.requestModelDataUpdate();
        }
        if (this.hasLevel()) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 16);
        }
    }

    @Override
    public void clearKineticInformation() {
        super.clearKineticInformation();
        this.beltLength = 0;
        this.index = 0;
        this.controller = null;
        this.trackerUpdateTag = new CompoundTag();
    }

    public boolean applyColor(DyeColor colorIn) {
        if (colorIn == null ? !this.color.isPresent() : this.color.isPresent() && this.color.get() == colorIn) {
            return false;
        }
        if (this.level.isClientSide()) {
            return true;
        }
        for (BlockPos blockPos : BeltBlock.getBeltChain((LevelAccessor)this.level, this.getController())) {
            BeltBlockEntity belt = BeltHelper.getSegmentBE((LevelAccessor)this.level, blockPos);
            if (belt == null) continue;
            belt.color = Optional.ofNullable(colorIn);
            belt.setChanged();
            belt.sendData();
        }
        return true;
    }

    public BeltBlockEntity getControllerBE() {
        if (this.controller == null) {
            return null;
        }
        if (!this.level.isLoaded(this.controller)) {
            return null;
        }
        BlockEntity be = this.level.getBlockEntity(this.controller);
        if (be == null || !(be instanceof BeltBlockEntity)) {
            return null;
        }
        return (BeltBlockEntity)be;
    }

    public void setController(BlockPos controller) {
        this.controller = controller;
    }

    public BlockPos getController() {
        return this.controller == null ? this.worldPosition : this.controller;
    }

    public boolean isController() {
        return this.controller != null && this.worldPosition.getX() == this.controller.getX() && this.worldPosition.getY() == this.controller.getY() && this.worldPosition.getZ() == this.controller.getZ();
    }

    public float getBeltMovementSpeed() {
        return this.getSpeed() / 480.0f;
    }

    public float getDirectionAwareBeltMovementSpeed() {
        int offset = this.getBeltFacing().getAxisDirection().getStep();
        if (this.getBeltFacing().getAxis() == Direction.Axis.X) {
            offset *= -1;
        }
        return this.getBeltMovementSpeed() * (float)offset;
    }

    public boolean hasPulley() {
        if (!AllBlocks.BELT.has(this.getBlockState())) {
            return false;
        }
        return this.getBlockState().getValue(BeltBlock.PART) != BeltPart.MIDDLE;
    }

    protected boolean isLastBelt() {
        if (this.getSpeed() == 0.0f) {
            return false;
        }
        Direction direction = this.getBeltFacing();
        if (this.getBlockState().getValue(BeltBlock.SLOPE) == BeltSlope.VERTICAL) {
            return false;
        }
        BeltPart part = (BeltPart)((Object)this.getBlockState().getValue(BeltBlock.PART));
        if (part == BeltPart.MIDDLE) {
            return false;
        }
        boolean movingPositively = this.getSpeed() > 0.0f == (direction.getAxisDirection().getStep() == 1) ^ direction.getAxis() == Direction.Axis.X;
        return part == BeltPart.START ^ movingPositively;
    }

    public Vec3i getMovementDirection(boolean firstHalf) {
        return this.getMovementDirection(firstHalf, false);
    }

    public Vec3i getBeltChainDirection() {
        return this.getMovementDirection(true, true);
    }

    protected Vec3i getMovementDirection(boolean firstHalf, boolean ignoreHalves) {
        boolean movingUp;
        boolean onSlope;
        boolean notHorizontal;
        if (this.getSpeed() == 0.0f) {
            return BlockPos.ZERO;
        }
        BlockState blockState = this.getBlockState();
        Direction beltFacing = (Direction)blockState.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
        BeltSlope slope = (BeltSlope)((Object)blockState.getValue(BeltBlock.SLOPE));
        BeltPart part = (BeltPart)((Object)blockState.getValue(BeltBlock.PART));
        Direction.Axis axis = beltFacing.getAxis();
        Direction movementFacing = Direction.get((Direction.AxisDirection)(axis == Direction.Axis.X ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE), (Direction.Axis)axis);
        boolean bl = notHorizontal = blockState.getValue(BeltBlock.SLOPE) != BeltSlope.HORIZONTAL;
        if (this.getSpeed() < 0.0f) {
            movementFacing = movementFacing.getOpposite();
        }
        Vec3i movement = movementFacing.getNormal();
        boolean slopeBeforeHalf = part == BeltPart.END == (beltFacing.getAxisDirection() == Direction.AxisDirection.POSITIVE);
        boolean bl2 = onSlope = notHorizontal && (part == BeltPart.MIDDLE || slopeBeforeHalf == firstHalf || ignoreHalves);
        boolean bl3 = onSlope && slope == (movementFacing == beltFacing ? BeltSlope.UPWARD : BeltSlope.DOWNWARD) ? true : (movingUp = false);
        if (!onSlope) {
            return movement;
        }
        return new Vec3i(movement.getX(), movingUp ? 1 : -1, movement.getZ());
    }

    public Direction getMovementFacing() {
        Direction.Axis axis = this.getBeltFacing().getAxis();
        return Direction.fromAxisAndDirection((Direction.Axis)axis, (Direction.AxisDirection)(this.getBeltMovementSpeed() < 0.0f ^ axis == Direction.Axis.X ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE));
    }

    protected Direction getBeltFacing() {
        return (Direction)this.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
    }

    public BeltInventory getInventory() {
        if (!this.isController()) {
            BeltBlockEntity controllerBE = this.getControllerBE();
            if (controllerBE != null) {
                return controllerBE.getInventory();
            }
            return null;
        }
        if (this.inventory == null) {
            this.inventory = new BeltInventory(this);
        }
        return this.inventory;
    }

    private void applyToAllItems(float maxDistanceFromCenter, Function<TransportedItemStack, TransportedItemStackHandlerBehaviour.TransportedResult> processFunction) {
        BeltBlockEntity controller = this.getControllerBE();
        if (controller == null) {
            return;
        }
        BeltInventory inventory = controller.getInventory();
        if (inventory != null) {
            inventory.applyToEachWithin((float)this.index + 0.5f, maxDistanceFromCenter, processFunction);
        }
    }

    private Vec3 getWorldPositionOf(TransportedItemStack transported) {
        BeltBlockEntity controllerBE = this.getControllerBE();
        if (controllerBE == null) {
            return Vec3.ZERO;
        }
        return BeltHelper.getVectorForOffset(controllerBE, transported.beltPosition);
    }

    public void setCasingType(CasingType type) {
        boolean shouldBlockHaveCasing;
        if (this.casing == type) {
            return;
        }
        BlockState blockState = this.getBlockState();
        boolean bl = shouldBlockHaveCasing = type != CasingType.NONE;
        if (this.level.isClientSide) {
            this.casing = type;
            this.level.setBlock(this.worldPosition, (BlockState)blockState.setValue((Property)BeltBlock.CASING, (Comparable)Boolean.valueOf(shouldBlockHaveCasing)), 0);
            this.requestModelDataUpdate();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 16);
            return;
        }
        if (this.casing != CasingType.NONE) {
            this.level.levelEvent(2001, this.worldPosition, Block.getId((BlockState)(this.casing == CasingType.ANDESITE ? AllBlocks.ANDESITE_CASING.getDefaultState() : AllBlocks.BRASS_CASING.getDefaultState())));
        }
        if ((Boolean)blockState.getValue((Property)BeltBlock.CASING) != shouldBlockHaveCasing) {
            KineticBlockEntity.switchToBlockState(this.level, this.worldPosition, (BlockState)blockState.setValue((Property)BeltBlock.CASING, (Comparable)Boolean.valueOf(shouldBlockHaveCasing)));
        }
        this.casing = type;
        this.setChanged();
        this.sendData();
    }

    private boolean canInsertFrom(Direction side) {
        if (this.getSpeed() == 0.0f) {
            return false;
        }
        BlockState state = this.getBlockState();
        if (state.hasProperty(BeltBlock.SLOPE) && (state.getValue(BeltBlock.SLOPE) == BeltSlope.SIDEWAYS || state.getValue(BeltBlock.SLOPE) == BeltSlope.VERTICAL)) {
            return false;
        }
        return this.getMovementFacing() != side.getOpposite();
    }

    private boolean isOccupied(Direction side) {
        BeltBlockEntity nextBeltController = this.getControllerBE();
        if (nextBeltController == null) {
            return true;
        }
        BeltInventory nextInventory = nextBeltController.getInventory();
        if (nextInventory == null) {
            return true;
        }
        if (this.getSpeed() == 0.0f) {
            return true;
        }
        if (this.getMovementFacing() == side.getOpposite()) {
            return true;
        }
        return !nextInventory.canInsertAtFromSide(this.index, side);
    }

    private ItemStack tryInsertingFromSide(TransportedItemStack transportedStack, Direction side, boolean simulate) {
        BrassTunnelBlockEntity tunnelBE;
        BeltBlockEntity nextBeltController = this.getControllerBE();
        ItemStack inserted = transportedStack.stack;
        ItemStack empty = ItemStack.EMPTY;
        if (!BeltBlock.canTransportObjects(this.getBlockState())) {
            return inserted;
        }
        if (nextBeltController == null) {
            return inserted;
        }
        BeltInventory nextInventory = nextBeltController.getInventory();
        if (nextInventory == null) {
            return inserted;
        }
        BlockEntity teAbove = this.level.getBlockEntity(this.worldPosition.above());
        if (teAbove instanceof BrassTunnelBlockEntity && (tunnelBE = (BrassTunnelBlockEntity)teAbove).hasDistributionBehaviour()) {
            if (!tunnelBE.getStackToDistribute().isEmpty()) {
                return inserted;
            }
            if (!tunnelBE.testFlapFilter(side.getOpposite(), inserted)) {
                return inserted;
            }
            if (!simulate) {
                BeltTunnelInteractionHandler.flapTunnel(nextInventory, this.index, side.getOpposite(), true);
                tunnelBE.setStackToDistribute(inserted, side.getOpposite());
            }
            return empty;
        }
        if (this.isOccupied(side)) {
            return inserted;
        }
        if (simulate) {
            return empty;
        }
        transportedStack = transportedStack.copy();
        transportedStack.beltPosition = (float)this.index + 0.5f - Math.signum(this.getDirectionAwareBeltMovementSpeed()) / 16.0f;
        Direction movementFacing = this.getMovementFacing();
        if (!side.getAxis().isVertical()) {
            if (movementFacing != side) {
                transportedStack.sideOffset = (float)side.getAxisDirection().getStep() * 0.675f;
                if (side.getAxis() == Direction.Axis.X) {
                    transportedStack.sideOffset *= -1.0f;
                }
            } else {
                float extraOffset = transportedStack.prevBeltPosition != 0.0f && BeltHelper.getSegmentBE((LevelAccessor)this.level, this.worldPosition.relative(movementFacing.getOpposite())) != null ? 0.26f : 0.0f;
                transportedStack.beltPosition = this.getDirectionAwareBeltMovementSpeed() > 0.0f ? (float)this.index - extraOffset : (float)(this.index + 1) + extraOffset;
            }
        }
        transportedStack.prevSideOffset = transportedStack.sideOffset;
        transportedStack.insertedAt = this.index;
        transportedStack.insertedFrom = side;
        transportedStack.prevBeltPosition = transportedStack.beltPosition;
        BeltTunnelInteractionHandler.flapTunnel(nextInventory, this.index, side.getOpposite(), true);
        nextInventory.addItem(transportedStack);
        nextBeltController.setChanged();
        nextBeltController.sendData();
        return empty;
    }

    public ModelData getModelData() {
        return ModelData.builder().with(BeltModel.CASING_PROPERTY, (Object)this.casing).with(BeltModel.COVER_PROPERTY, (Object)this.covered).build();
    }

    @Override
    protected boolean canPropagateDiagonally(IRotate block, BlockState state) {
        return state.hasProperty(BeltBlock.SLOPE) && (state.getValue(BeltBlock.SLOPE) == BeltSlope.UPWARD || state.getValue(BeltBlock.SLOPE) == BeltSlope.DOWNWARD);
    }

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        if (target instanceof BeltBlockEntity && !connectedViaAxes) {
            return this.getController().equals((Object)((BeltBlockEntity)target).getController()) ? 1.0f : 0.0f;
        }
        return 0.0f;
    }

    public void invalidateItemHandler() {
        this.invalidateCapabilities();
        this.itemHandler = null;
    }

    public boolean shouldRenderNormally() {
        if (this.level == null) {
            return this.isController();
        }
        BlockState state = this.getBlockState();
        return state != null && state.hasProperty(BeltBlock.PART) && state.getValue(BeltBlock.PART) == BeltPart.START;
    }

    public void setCovered(boolean blockCoveringBelt) {
        if (blockCoveringBelt == this.covered) {
            return;
        }
        this.covered = blockCoveringBelt;
        this.notifyUpdate();
    }

    public static enum CasingType {
        NONE,
        ANDESITE,
        BRASS;

    }
}
