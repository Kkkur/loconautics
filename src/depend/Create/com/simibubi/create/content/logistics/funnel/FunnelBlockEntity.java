/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.visualization.VisualizationHelper
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.BlockFace
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package com.simibubi.create.content.logistics.funnel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelFilterSlotPositioning;
import com.simibubi.create.content.logistics.funnel.FunnelFlapPacket;
import com.simibubi.create.content.redstone.smartObserver.SmartObserverBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import java.lang.ref.WeakReference;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.BlockFace;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class FunnelBlockEntity
extends SmartBlockEntity
implements IHaveHoveringInformation,
Clearable {
    private FilteringBehaviour filtering;
    private InvManipulationBehaviour invManipulation;
    private VersionedInventoryTrackerBehaviour invVersionTracker;
    private int extractionCooldown = 0;
    private WeakReference<Entity> lastObserved;
    LerpedFloat flap = this.createChasingFlap();
    static final AABB coreBB = new AABB(BlockPos.ZERO);

    public FunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    Mode determineCurrentMode() {
        BlockState state = this.getBlockState();
        if (!FunnelBlock.isFunnel(state)) {
            return Mode.INVALID;
        }
        if (state.getOptionalValue((Property)BlockStateProperties.POWERED).orElse(false).booleanValue()) {
            return Mode.PAUSED;
        }
        if (state.getBlock() instanceof BeltFunnelBlock) {
            BeltFunnelBlock.Shape shape = (BeltFunnelBlock.Shape)((Object)state.getValue(BeltFunnelBlock.SHAPE));
            if (shape == BeltFunnelBlock.Shape.PULLING) {
                return Mode.TAKING_FROM_BELT;
            }
            if (shape == BeltFunnelBlock.Shape.PUSHING) {
                return Mode.PUSHING_TO_BELT;
            }
            BeltBlockEntity belt = BeltHelper.getSegmentBE((LevelAccessor)this.level, this.worldPosition.below());
            if (belt != null) {
                return belt.getMovementFacing() == state.getValue((Property)BeltFunnelBlock.HORIZONTAL_FACING) ? Mode.PUSHING_TO_BELT : Mode.TAKING_FROM_BELT;
            }
            return Mode.INVALID;
        }
        if (state.getBlock() instanceof FunnelBlock) {
            return (Boolean)state.getValue((Property)FunnelBlock.EXTRACTING) != false ? Mode.EXTRACT : Mode.COLLECT;
        }
        return Mode.INVALID;
    }

    @Override
    public void tick() {
        super.tick();
        this.flap.tickChaser();
        Mode mode = this.determineCurrentMode();
        if (this.level.isClientSide) {
            return;
        }
        if (mode == Mode.PAUSED) {
            this.extractionCooldown = 0;
        }
        if (mode == Mode.TAKING_FROM_BELT) {
            return;
        }
        if (this.extractionCooldown > 0) {
            --this.extractionCooldown;
            return;
        }
        if (mode == Mode.PUSHING_TO_BELT) {
            this.activateExtractingBeltFunnel();
        }
        if (mode == Mode.EXTRACT) {
            this.activateExtractor();
        }
    }

    private void activateExtractor() {
        Entity lastEntity;
        if (this.invVersionTracker.stillWaiting(this.invManipulation)) {
            return;
        }
        BlockState blockState = this.getBlockState();
        Direction facing = AbstractFunnelBlock.getFunnelFacing(blockState);
        if (facing == null) {
            return;
        }
        Entity entity = lastEntity = this.lastObserved != null ? (Entity)this.lastObserved.get() : null;
        if (lastEntity != null && lastEntity.isAlive()) {
            AABB area = this.getEntityOverflowScanningArea();
            if (lastEntity.getBoundingBox().intersects(area)) {
                return;
            }
            this.lastObserved = null;
        }
        int amountToExtract = this.getAmountToExtract();
        ItemHelper.ExtractionCountMode mode = this.getModeToExtract();
        ItemStack stack = ((InvManipulationBehaviour)this.invManipulation.simulate()).extract(mode, amountToExtract);
        if (stack.isEmpty()) {
            this.invVersionTracker.awaitNewVersion(this.invManipulation);
            return;
        }
        AABB area = this.getEntityOverflowScanningArea();
        for (Entity entity2 : this.level.getEntities(null, area)) {
            if (!(entity2 instanceof ItemEntity) && !(entity2 instanceof PackageEntity)) continue;
            this.lastObserved = new WeakReference<Entity>(entity2);
            return;
        }
        stack = this.invManipulation.extract(mode, amountToExtract);
        if (stack.isEmpty()) {
            return;
        }
        this.flap(false);
        this.onTransfer(stack);
        Vec3 outputPos = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        boolean vertical = facing.getAxis().isVertical();
        boolean up = facing == Direction.UP;
        outputPos = outputPos.add(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(vertical ? (up ? (double)0.15f : 0.5) : 0.25));
        if (!vertical) {
            outputPos = outputPos.subtract(0.0, (double)0.45f, 0.0);
        }
        Vec3 motion = Vec3.ZERO;
        if (up) {
            motion = new Vec3(0.0, 0.25, 0.0);
        }
        ItemEntity item = new ItemEntity(this.level, outputPos.x, outputPos.y, outputPos.z, stack.copy());
        item.setDefaultPickUpDelay();
        item.setDeltaMovement(motion);
        this.level.addFreshEntity((Entity)item);
        this.lastObserved = new WeakReference<ItemEntity>(item);
        this.startCooldown();
    }

    private AABB getEntityOverflowScanningArea() {
        Direction facing = AbstractFunnelBlock.getFunnelFacing(this.getBlockState());
        AABB bb = coreBB.move(this.worldPosition);
        if (facing == null || facing == Direction.UP) {
            return bb;
        }
        return bb.expandTowards(0.0, -1.0, 0.0);
    }

    private void activateExtractingBeltFunnel() {
        MutableBoolean deniedByInsertion;
        if (this.invVersionTracker.stillWaiting(this.invManipulation)) {
            return;
        }
        BlockState blockState = this.getBlockState();
        Direction facing = (Direction)blockState.getValue((Property)BeltFunnelBlock.HORIZONTAL_FACING);
        DirectBeltInputBehaviour inputBehaviour = BlockEntityBehaviour.get((BlockGetter)this.level, this.worldPosition.below(), DirectBeltInputBehaviour.TYPE);
        if (inputBehaviour == null) {
            return;
        }
        if (!inputBehaviour.canInsertFromSide(facing)) {
            return;
        }
        if (inputBehaviour.isOccupied(facing)) {
            return;
        }
        int amountToExtract = this.getAmountToExtract();
        ItemHelper.ExtractionCountMode mode = this.getModeToExtract();
        ItemStack stack = this.invManipulation.extract(mode, amountToExtract, arg_0 -> FunnelBlockEntity.lambda$activateExtractingBeltFunnel$0(inputBehaviour, facing, deniedByInsertion = new MutableBoolean(false), arg_0));
        if (stack.isEmpty()) {
            if (deniedByInsertion.isFalse()) {
                this.invVersionTracker.awaitNewVersion((IItemHandler)this.invManipulation.getInventory());
            }
            return;
        }
        this.flap(false);
        this.onTransfer(stack);
        inputBehaviour.handleInsertion(stack, facing, false);
        this.startCooldown();
    }

    public int getAmountToExtract() {
        if (!this.supportsAmountOnFilter()) {
            return 64;
        }
        int amountToExtract = this.invManipulation.getAmountFromFilter();
        if (!this.filtering.isActive()) {
            amountToExtract = 1;
        }
        return amountToExtract;
    }

    public ItemHelper.ExtractionCountMode getModeToExtract() {
        if (!this.supportsAmountOnFilter() || !this.filtering.isActive()) {
            return ItemHelper.ExtractionCountMode.UPTO;
        }
        return this.invManipulation.getModeFromFilter();
    }

    private int startCooldown() {
        this.extractionCooldown = (Integer)AllConfigs.server().logistics.defaultExtractionTimer.get();
        return this.extractionCooldown;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.invManipulation = new InvManipulationBehaviour(this, (w, p, s) -> new BlockFace(p, AbstractFunnelBlock.getFunnelFacing(s).getOpposite()));
        behaviours.add(this.invManipulation);
        this.invVersionTracker = new VersionedInventoryTrackerBehaviour(this);
        behaviours.add(this.invVersionTracker);
        this.filtering = new FilteringBehaviour(this, new FunnelFilterSlotPositioning());
        this.filtering.showCountWhen(this::supportsAmountOnFilter);
        this.filtering.onlyActiveWhen(this::supportsFiltering);
        this.filtering.withCallback($ -> this.invVersionTracker.reset());
        behaviours.add(this.filtering);
        behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen(this::supportsDirectBeltInput).setInsertionHandler(this::handleDirectBeltInput));
        this.registerAwardables(behaviours, AllAdvancements.FUNNEL);
    }

    private boolean supportsAmountOnFilter() {
        BlockState blockState = this.getBlockState();
        boolean beltFunnelsupportsAmount = false;
        if (blockState.getBlock() instanceof BeltFunnelBlock) {
            BeltFunnelBlock.Shape shape = (BeltFunnelBlock.Shape)((Object)blockState.getValue(BeltFunnelBlock.SHAPE));
            beltFunnelsupportsAmount = shape == BeltFunnelBlock.Shape.PUSHING ? true : BeltHelper.getSegmentBE((LevelAccessor)this.level, this.worldPosition.below()) != null;
        }
        boolean extractor = blockState.getBlock() instanceof FunnelBlock && (Boolean)blockState.getValue((Property)FunnelBlock.EXTRACTING) != false;
        return beltFunnelsupportsAmount || extractor;
    }

    private boolean supportsDirectBeltInput(Direction side) {
        BlockState blockState = this.getBlockState();
        if (blockState == null) {
            return false;
        }
        if (!(blockState.getBlock() instanceof FunnelBlock)) {
            return false;
        }
        if (((Boolean)blockState.getValue((Property)FunnelBlock.EXTRACTING)).booleanValue()) {
            return false;
        }
        return FunnelBlock.getFunnelFacing(blockState) == Direction.UP;
    }

    private boolean supportsFiltering() {
        BlockState blockState = this.getBlockState();
        return AllBlocks.BRASS_BELT_FUNNEL.has(blockState) || AllBlocks.BRASS_FUNNEL.has(blockState);
    }

    private ItemStack handleDirectBeltInput(TransportedItemStack stack, Direction side, boolean simulate) {
        ItemStack inserted = stack.stack;
        if (!this.filtering.test(inserted)) {
            return inserted;
        }
        if (this.determineCurrentMode() == Mode.PAUSED) {
            return inserted;
        }
        if (simulate) {
            this.invManipulation.simulate();
        }
        if (!simulate) {
            this.onTransfer(inserted);
        }
        return this.invManipulation.insert(inserted);
    }

    public void flap(boolean inward) {
        Level level;
        if (!this.level.isClientSide && (level = this.level) instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            CatnipServices.NETWORK.sendToClientsTrackingChunk(serverLevel, new ChunkPos(this.worldPosition), (CustomPacketPayload)new FunnelFlapPacket(this, inward));
        } else {
            this.flap.setValue(inward ? -1.0 : 1.0);
            AllSoundEvents.FUNNEL_FLAP.playAt(this.level, (Vec3i)this.worldPosition, 1.0f, 1.0f, true);
        }
    }

    public boolean hasFlap() {
        BlockState blockState = this.getBlockState();
        return AbstractFunnelBlock.getFunnelFacing(blockState).getAxis().isHorizontal();
    }

    public float getFlapOffset() {
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof BeltFunnelBlock)) {
            return -0.0625f;
        }
        return switch ((BeltFunnelBlock.Shape)((Object)blockState.getValue(BeltFunnelBlock.SHAPE))) {
            case BeltFunnelBlock.Shape.EXTENDED -> 0.5f;
            case BeltFunnelBlock.Shape.PULLING, BeltFunnelBlock.Shape.PUSHING -> -0.125f;
            default -> 0.0f;
        };
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("TransferCooldown", this.extractionCooldown);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.extractionCooldown = compound.getInt("TransferCooldown");
        if (clientPacket) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> VisualizationHelper.queueUpdate((BlockEntity)this));
        }
    }

    public void clearContent() {
        this.filtering.setFilter(ItemStack.EMPTY);
    }

    public void onTransfer(ItemStack stack) {
        ((SmartObserverBlock)AllBlocks.SMART_OBSERVER.get()).onFunnelTransfer(this.level, this.worldPosition, stack);
        this.award(AllAdvancements.FUNNEL);
    }

    private LerpedFloat createChasingFlap() {
        return LerpedFloat.linear().startWithValue(0.25).chase(0.0, (double)0.05f, LerpedFloat.Chaser.EXP);
    }

    private static /* synthetic */ boolean lambda$activateExtractingBeltFunnel$0(DirectBeltInputBehaviour inputBehaviour, Direction facing, MutableBoolean deniedByInsertion, ItemStack s) {
        ItemStack handleInsertion = inputBehaviour.handleInsertion(s, facing, true);
        if (handleInsertion.isEmpty()) {
            return true;
        }
        deniedByInsertion.setTrue();
        return false;
    }

    static enum Mode {
        INVALID,
        PAUSED,
        COLLECT,
        PUSHING_TO_BELT,
        TAKING_FROM_BELT,
        EXTRACT;

    }
}
