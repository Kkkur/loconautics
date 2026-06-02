/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.tunnel;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelFilterSlot;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelItemHandler;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelModeSlot;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.SidedFilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class BrassTunnelBlockEntity
extends BeltTunnelBlockEntity
implements IHaveGoggleInformation {
    SidedFilteringBehaviour filtering;
    boolean connectedLeft;
    boolean connectedRight;
    ItemStack stackToDistribute;
    Direction stackEnteredFrom = null;
    float distributionProgress;
    int distributionDistanceLeft;
    int distributionDistanceRight;
    int previousOutputIndex = 0;
    Couple<List<Pair<BlockPos, Direction>>> distributionTargets = Couple.create(ArrayList::new);
    private boolean newItemArrived;
    private boolean syncedOutputActive = false;
    private Set<BrassTunnelBlockEntity> syncSet = new HashSet<BrassTunnelBlockEntity>();
    protected ScrollOptionBehaviour<SelectionMode> selectionMode;
    private IItemHandler beltCapability = null;
    private IItemHandler tunnelCapability;
    private static Map<Pair<BrassTunnelBlockEntity, Direction>, ItemStack> distributed = new IdentityHashMap<Pair<BrassTunnelBlockEntity, Direction>, ItemStack>();
    private static Set<Pair<BrassTunnelBlockEntity, Direction>> full = new HashSet<Pair<BrassTunnelBlockEntity, Direction>>();

    public BrassTunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.stackToDistribute = ItemStack.EMPTY;
        this.tunnelCapability = new BrassTunnelItemHandler(this);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.BRASS_TUNNEL.get(), (be, context) -> be.tunnelCapability);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.selectionMode = new ScrollOptionBehaviour<SelectionMode>(SelectionMode.class, (Component)CreateLang.translateDirect("logistics.when_multiple_outputs_available", new Object[0]), this, new BrassTunnelModeSlot());
        behaviours.add(this.selectionMode);
        this.selectionMode.onlyActiveWhen(this::hasDistributionBehaviour);
        this.selectionMode.withCallback(setting -> {
            for (boolean side : Iterate.trueAndFalse) {
                BrassTunnelBlockEntity adjacent;
                if (!this.isConnected(side) || (adjacent = this.getAdjacent(side)) == null) continue;
                adjacent.selectionMode.setValue((int)setting);
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        BeltBlockEntity beltBelow = BeltHelper.getSegmentBE((LevelAccessor)this.level, this.worldPosition.below());
        if (this.distributionProgress > 0.0f) {
            this.distributionProgress -= 1.0f;
        }
        if (beltBelow == null || beltBelow.getSpeed() == 0.0f) {
            return;
        }
        if (this.stackToDistribute.isEmpty() && !this.syncedOutputActive) {
            return;
        }
        if (this.level.isClientSide && !this.isVirtual()) {
            return;
        }
        if (this.distributionProgress == -1.0f) {
            this.distributionTargets.forEach(List::clear);
            this.distributionDistanceLeft = 0;
            this.distributionDistanceRight = 0;
            this.syncSet.clear();
            List<Pair<BrassTunnelBlockEntity, Direction>> validOutputs = this.gatherValidOutputs();
            if (this.selectionMode.get() == SelectionMode.SYNCHRONIZE) {
                boolean notifySyncedOut;
                boolean allEmpty = true;
                boolean allFull = true;
                for (BrassTunnelBlockEntity be2 : this.syncSet) {
                    boolean hasStack = !be2.stackToDistribute.isEmpty();
                    allEmpty &= !hasStack;
                    allFull &= hasStack;
                }
                boolean bl = notifySyncedOut = !allEmpty;
                if (allFull || allEmpty) {
                    this.syncSet.forEach(be -> {
                        be.syncedOutputActive = notifySyncedOut;
                    });
                }
            }
            if (validOutputs == null) {
                return;
            }
            if (this.stackToDistribute.isEmpty()) {
                return;
            }
            for (Pair<BrassTunnelBlockEntity, Direction> pair : validOutputs) {
                Direction output;
                BrassTunnelBlockEntity tunnel = (BrassTunnelBlockEntity)pair.getKey();
                if (this.insertIntoTunnel(tunnel, output = (Direction)pair.getValue(), this.stackToDistribute, true) == null) continue;
                ((List)this.distributionTargets.get(!tunnel.flapFilterEmpty(output))).add(Pair.of((Object)tunnel.worldPosition, (Object)output));
                int distance = tunnel.worldPosition.getX() + tunnel.worldPosition.getZ() - this.worldPosition.getX() - this.worldPosition.getZ();
                if (distance < 0) {
                    this.distributionDistanceLeft = Math.max(this.distributionDistanceLeft, -distance);
                    continue;
                }
                this.distributionDistanceRight = Math.max(this.distributionDistanceRight, distance);
            }
            if (((List)this.distributionTargets.getFirst()).isEmpty() && ((List)this.distributionTargets.getSecond()).isEmpty()) {
                return;
            }
            if (this.newItemArrived) {
                this.newItemArrived = false;
                this.distributionProgress = 2.0f;
            } else {
                if (this.selectionMode.get() != SelectionMode.SYNCHRONIZE || this.syncedOutputActive) {
                    this.distributionProgress = ((Integer)AllConfigs.server().logistics.brassTunnelTimer.get()).intValue();
                    this.sendData();
                }
                return;
            }
        }
        if (this.distributionProgress != 0.0f) {
            return;
        }
        this.distributionTargets.forEach(list -> {
            if (this.stackToDistribute.isEmpty()) {
                return;
            }
            ArrayList<Pair<BrassTunnelBlockEntity, Direction>> validTargets = new ArrayList<Pair<BrassTunnelBlockEntity, Direction>>();
            for (Pair pair : list) {
                BlockEntity be;
                BlockPos tunnelPos = (BlockPos)pair.getKey();
                Direction output = (Direction)pair.getValue();
                if (tunnelPos.equals((Object)this.worldPosition) && output == this.stackEnteredFrom || !((be = this.level.getBlockEntity(tunnelPos)) instanceof BrassTunnelBlockEntity)) continue;
                validTargets.add((Pair<BrassTunnelBlockEntity, Direction>)Pair.of((Object)((BrassTunnelBlockEntity)be), (Object)output));
            }
            this.distribute(validTargets);
            this.distributionProgress = -1.0f;
        });
    }

    private void distribute(List<Pair<BrassTunnelBlockEntity, Direction>> validTargets) {
        boolean robin;
        int amountTargets = validTargets.size();
        if (amountTargets == 0) {
            return;
        }
        distributed.clear();
        full.clear();
        int indexStart = this.previousOutputIndex % amountTargets;
        SelectionMode mode = this.selectionMode.get();
        boolean force = mode == SelectionMode.FORCED_ROUND_ROBIN || mode == SelectionMode.FORCED_SPLIT;
        boolean split = mode == SelectionMode.FORCED_SPLIT || mode == SelectionMode.SPLIT;
        boolean bl = robin = mode == SelectionMode.FORCED_ROUND_ROBIN || mode == SelectionMode.ROUND_ROBIN;
        if (mode == SelectionMode.RANDOMIZE) {
            indexStart = this.level.random.nextInt(amountTargets);
        }
        if (mode == SelectionMode.PREFER_NEAREST || mode == SelectionMode.SYNCHRONIZE) {
            indexStart = 0;
        }
        ItemStack toDistribute = this.stackToDistribute.copy();
        for (boolean distributeAgain : Iterate.trueAndFalse) {
            ItemStack toDistributeThisCycle = null;
            int remainingOutputs = amountTargets;
            int leftovers = 0;
            block1: for (boolean simulate : Iterate.trueAndFalse) {
                if (remainingOutputs == 0) break;
                leftovers = 0;
                int index = indexStart;
                int stackSize = toDistribute.getCount();
                int splitStackSize = stackSize / remainingOutputs;
                int splitRemainder = stackSize % remainingOutputs;
                int visited = 0;
                toDistributeThisCycle = toDistribute.copy();
                if (!force && !split && simulate) continue;
                while (visited < amountTargets) {
                    ItemStack remainder;
                    int increasedCount;
                    Pair<BrassTunnelBlockEntity, Direction> pair = validTargets.get(index);
                    BrassTunnelBlockEntity tunnel = (BrassTunnelBlockEntity)pair.getKey();
                    Direction side = (Direction)pair.getValue();
                    index = (index + 1) % amountTargets;
                    ++visited;
                    if (full.contains(pair)) {
                        if (!split || !simulate) continue;
                        --remainingOutputs;
                        continue;
                    }
                    int count = split ? splitStackSize + (splitRemainder > 0 ? 1 : 0) : stackSize;
                    ItemStack toOutput = toDistributeThisCycle.copyWithCount(count);
                    boolean testWithIncreasedCount = distributed.containsKey(pair);
                    int n = increasedCount = testWithIncreasedCount ? distributed.get(pair).getCount() : 0;
                    if (testWithIncreasedCount) {
                        toOutput.grow(increasedCount);
                    }
                    if ((remainder = this.insertIntoTunnel(tunnel, side, toOutput, true)) == null || remainder.getCount() == (testWithIncreasedCount ? count + 1 : count)) {
                        if (force) {
                            return;
                        }
                        if (split && simulate) {
                            --remainingOutputs;
                        }
                        if (!simulate) {
                            full.add(pair);
                        }
                        if (!robin) continue;
                        continue block1;
                    }
                    if (!remainder.isEmpty() && !simulate) {
                        full.add(pair);
                    }
                    if (!simulate) {
                        toOutput.shrink(remainder.getCount());
                        distributed.put(pair, toOutput);
                    }
                    leftovers += remainder.getCount();
                    toDistributeThisCycle.shrink(count);
                    if (toDistributeThisCycle.isEmpty()) continue block1;
                    --splitRemainder;
                    if (split) continue;
                    continue block1;
                }
            }
            toDistribute.setCount(toDistributeThisCycle.getCount() + leftovers);
            if (leftovers == 0 && distributeAgain || !split) break;
        }
        int failedTransferrals = 0;
        for (Map.Entry<Pair<BrassTunnelBlockEntity, Direction>, ItemStack> entry : distributed.entrySet()) {
            Pair<BrassTunnelBlockEntity, Direction> pair = entry.getKey();
            failedTransferrals += this.insertIntoTunnel((BrassTunnelBlockEntity)pair.getKey(), (Direction)pair.getValue(), entry.getValue(), false).getCount();
        }
        toDistribute.grow(failedTransferrals);
        this.stackToDistribute = this.stackToDistribute.copyWithCount(toDistribute.getCount());
        if (this.stackToDistribute.isEmpty()) {
            this.stackEnteredFrom = null;
        }
        ++this.previousOutputIndex;
        this.previousOutputIndex %= amountTargets;
        this.notifyUpdate();
    }

    public void setStackToDistribute(ItemStack stack, @Nullable Direction enteredFrom) {
        this.stackToDistribute = stack;
        this.stackEnteredFrom = enteredFrom;
        this.distributionProgress = -1.0f;
        if (!stack.isEmpty()) {
            this.newItemArrived = true;
        }
        this.sendData();
        this.setChanged();
    }

    public ItemStack getStackToDistribute() {
        return this.stackToDistribute;
    }

    public List<ItemStack> grabAllStacksOfGroup(boolean simulate) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        ItemStack own = this.getStackToDistribute();
        if (!own.isEmpty()) {
            list.add(own);
            if (!simulate) {
                this.setStackToDistribute(ItemStack.EMPTY, null);
            }
        }
        for (boolean left : Iterate.trueAndFalse) {
            BrassTunnelBlockEntity adjacent = this;
            while (adjacent != null) {
                ItemStack other;
                if (!this.level.isLoaded(adjacent.getBlockPos())) {
                    return null;
                }
                if ((adjacent = adjacent.getAdjacent(left)) == null || (other = adjacent.getStackToDistribute()).isEmpty()) continue;
                list.add(other);
                if (simulate) continue;
                adjacent.setStackToDistribute(ItemStack.EMPTY, null);
            }
        }
        return list;
    }

    @Nullable
    protected ItemStack insertIntoTunnel(BrassTunnelBlockEntity tunnel, Direction side, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return stack;
        }
        if (!tunnel.testFlapFilter(side, stack)) {
            return null;
        }
        BeltBlockEntity below = BeltHelper.getSegmentBE((LevelAccessor)this.level, tunnel.worldPosition.below());
        if (below == null) {
            return null;
        }
        BlockPos offset = tunnel.getBlockPos().below().relative(side);
        DirectBeltInputBehaviour sideOutput = BlockEntityBehaviour.get((BlockGetter)this.level, offset, DirectBeltInputBehaviour.TYPE);
        if (sideOutput != null) {
            if (!sideOutput.canInsertFromSide(side)) {
                return null;
            }
            ItemStack result = sideOutput.handleInsertion(stack, side, simulate);
            if (result.isEmpty() && !simulate) {
                tunnel.flap(side, false);
            }
            return result;
        }
        Direction movementFacing = below.getMovementFacing();
        if (side == movementFacing && !BlockHelper.hasBlockSolidSide(this.level.getBlockState(offset), (BlockGetter)this.level, offset, side.getOpposite())) {
            BeltBlockEntity controllerBE = below.getControllerBE();
            if (controllerBE == null) {
                return null;
            }
            if (!simulate) {
                tunnel.flap(side, true);
                ItemStack ejected = stack;
                float beltMovementSpeed = below.getDirectionAwareBeltMovementSpeed();
                float movementSpeed = Math.max(Math.abs(beltMovementSpeed), 0.125f);
                int additionalOffset = beltMovementSpeed > 0.0f ? 1 : 0;
                Vec3 outPos = BeltHelper.getVectorForOffset(controllerBE, below.index + additionalOffset);
                Vec3 outMotion = Vec3.atLowerCornerOf((Vec3i)side.getNormal()).scale((double)movementSpeed).add(0.0, 0.125, 0.0);
                outPos.add(outMotion.normalize());
                ItemEntity entity = new ItemEntity(this.level, outPos.x, outPos.y + 0.375, outPos.z, ejected);
                entity.setDeltaMovement(outMotion);
                entity.setDefaultPickUpDelay();
                entity.hurtMarked = true;
                this.level.addFreshEntity((Entity)entity);
            }
            return ItemStack.EMPTY;
        }
        return null;
    }

    public boolean testFlapFilter(Direction side, ItemStack stack) {
        if (this.filtering == null) {
            return false;
        }
        if (this.filtering.get(side) == null) {
            FilteringBehaviour adjacentFilter = BlockEntityBehaviour.get((BlockGetter)this.level, this.worldPosition.relative(side), FilteringBehaviour.TYPE);
            if (adjacentFilter == null) {
                return true;
            }
            return adjacentFilter.test(stack);
        }
        return this.filtering.test(side, stack);
    }

    public boolean flapFilterEmpty(Direction side) {
        if (this.filtering == null) {
            return false;
        }
        if (this.filtering.get(side) == null) {
            FilteringBehaviour adjacentFilter = BlockEntityBehaviour.get((BlockGetter)this.level, this.worldPosition.relative(side), FilteringBehaviour.TYPE);
            if (adjacentFilter == null) {
                return true;
            }
            return adjacentFilter.getFilter().isEmpty();
        }
        return this.filtering.getFilter(side).isEmpty();
    }

    @Override
    public void initialize() {
        if (this.filtering == null) {
            this.filtering = this.createSidedFilter();
            this.attachBehaviourLate(this.filtering);
        }
        super.initialize();
    }

    public boolean canInsert(Direction side, ItemStack stack) {
        if (this.filtering != null && !this.filtering.test(side, stack)) {
            return false;
        }
        if (!this.hasDistributionBehaviour()) {
            return true;
        }
        return this.stackToDistribute.isEmpty();
    }

    public boolean hasDistributionBehaviour() {
        if (this.flaps.isEmpty()) {
            return false;
        }
        if (this.connectedLeft || this.connectedRight) {
            return true;
        }
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.BRASS_TUNNEL.has(blockState)) {
            return false;
        }
        Direction.Axis axis = (Direction.Axis)blockState.getValue(BrassTunnelBlock.HORIZONTAL_AXIS);
        for (Direction direction : this.flaps.keySet()) {
            if (direction.getAxis() == axis) continue;
            return true;
        }
        return false;
    }

    private List<Pair<BrassTunnelBlockEntity, Direction>> gatherValidOutputs() {
        ArrayList<Pair<BrassTunnelBlockEntity, Direction>> validOutputs = new ArrayList<Pair<BrassTunnelBlockEntity, Direction>>();
        boolean synchronize = this.selectionMode.get() == SelectionMode.SYNCHRONIZE;
        this.addValidOutputsOf(this, validOutputs);
        for (boolean left : Iterate.trueAndFalse) {
            BrassTunnelBlockEntity adjacent = this;
            while (adjacent != null) {
                if (!this.level.isLoaded(adjacent.getBlockPos())) {
                    return null;
                }
                if ((adjacent = adjacent.getAdjacent(left)) == null) continue;
                this.addValidOutputsOf(adjacent, validOutputs);
            }
        }
        if (!this.syncedOutputActive && synchronize) {
            return null;
        }
        return validOutputs;
    }

    private void addValidOutputsOf(BrassTunnelBlockEntity tunnelBE, List<Pair<BrassTunnelBlockEntity, Direction>> validOutputs) {
        this.syncSet.add(tunnelBE);
        BeltBlockEntity below = BeltHelper.getSegmentBE((LevelAccessor)this.level, tunnelBE.worldPosition.below());
        if (below == null) {
            return;
        }
        Direction movementFacing = below.getMovementFacing();
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.BRASS_TUNNEL.has(blockState)) {
            return;
        }
        boolean prioritizeSides = tunnelBE == this;
        for (boolean sidePass : Iterate.trueAndFalse) {
            if (!prioritizeSides && sidePass) continue;
            for (Direction direction : Iterate.horizontalDirections) {
                BlockPos offset;
                BlockState potentialFunnel;
                if (direction == movementFacing && below.getSpeed() == 0.0f || prioritizeSides && sidePass == (direction.getAxis() == movementFacing.getAxis()) || direction == movementFacing.getOpposite() || !tunnelBE.sides.contains(direction) || (potentialFunnel = this.level.getBlockState((offset = tunnelBE.worldPosition.below().relative(direction)).above())).getBlock() instanceof BeltFunnelBlock && potentialFunnel.getValue(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.PULLING && FunnelBlock.getFunnelFacing(potentialFunnel) == direction) continue;
                DirectBeltInputBehaviour inputBehaviour = BlockEntityBehaviour.get((BlockGetter)this.level, offset, DirectBeltInputBehaviour.TYPE);
                if (inputBehaviour == null) {
                    if (direction != movementFacing || BlockHelper.hasBlockSolidSide(this.level.getBlockState(offset), (BlockGetter)this.level, offset, direction.getOpposite())) continue;
                    validOutputs.add((Pair<BrassTunnelBlockEntity, Direction>)Pair.of((Object)tunnelBE, (Object)direction));
                    continue;
                }
                if (!inputBehaviour.canInsertFromSide(direction)) continue;
                validOutputs.add((Pair<BrassTunnelBlockEntity, Direction>)Pair.of((Object)tunnelBE, (Object)direction));
            }
        }
    }

    @Override
    public void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours) {
        super.addBehavioursDeferred(behaviours);
        this.filtering = this.createSidedFilter();
        behaviours.add(this.filtering);
    }

    protected SidedFilteringBehaviour createSidedFilter() {
        return new SidedFilteringBehaviour(this, new BrassTunnelFilterSlot(), this::makeFilter, this::isValidFaceForFilter);
    }

    private FilteringBehaviour makeFilter(Direction side, FilteringBehaviour filter) {
        return filter;
    }

    private boolean isValidFaceForFilter(Direction side) {
        return this.sides.contains(side);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("SyncedOutput", this.syncedOutputActive);
        compound.putBoolean("ConnectedLeft", this.connectedLeft);
        compound.putBoolean("ConnectedRight", this.connectedRight);
        compound.put("StackToDistribute", this.stackToDistribute.saveOptional(registries));
        if (this.stackEnteredFrom != null) {
            NBTHelper.writeEnum((CompoundTag)compound, (String)"StackEnteredFrom", (Enum)this.stackEnteredFrom);
        }
        compound.putFloat("DistributionProgress", this.distributionProgress);
        compound.putInt("PreviousIndex", this.previousOutputIndex);
        compound.putInt("DistanceLeft", this.distributionDistanceLeft);
        compound.putInt("DistanceRight", this.distributionDistanceRight);
        for (boolean filtered : Iterate.trueAndFalse) {
            compound.put(filtered ? "FilteredTargets" : "Targets", (Tag)NBTHelper.writeCompoundList((Iterable)((Iterable)this.distributionTargets.get(filtered)), pair -> {
                CompoundTag nbt = new CompoundTag();
                nbt.put("Pos", NbtUtils.writeBlockPos((BlockPos)((BlockPos)pair.getKey())));
                nbt.putInt("Face", ((Direction)pair.getValue()).get3DDataValue());
                return nbt;
            }));
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        boolean wasConnectedLeft = this.connectedLeft;
        boolean wasConnectedRight = this.connectedRight;
        this.syncedOutputActive = compound.getBoolean("SyncedOutput");
        this.connectedLeft = compound.getBoolean("ConnectedLeft");
        this.connectedRight = compound.getBoolean("ConnectedRight");
        this.stackToDistribute = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)compound.getCompound("StackToDistribute"));
        this.stackEnteredFrom = compound.contains("StackEnteredFrom") ? (Direction)NBTHelper.readEnum((CompoundTag)compound, (String)"StackEnteredFrom", Direction.class) : null;
        this.distributionProgress = compound.getFloat("DistributionProgress");
        this.previousOutputIndex = compound.getInt("PreviousIndex");
        this.distributionDistanceLeft = compound.getInt("DistanceLeft");
        this.distributionDistanceRight = compound.getInt("DistanceRight");
        for (boolean filtered : Iterate.trueAndFalse) {
            this.distributionTargets.set(filtered, (Object)NBTHelper.readCompoundList((ListTag)compound.getList(filtered ? "FilteredTargets" : "Targets", 10), nbt -> {
                BlockPos pos = NBTHelper.readBlockPos((CompoundTag)nbt, (String)"Pos");
                Direction face = Direction.from3DDataValue((int)nbt.getInt("Face"));
                return Pair.of((Object)pos, (Object)face);
            }));
        }
        super.read(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        if (wasConnectedLeft != this.connectedLeft || wasConnectedRight != this.connectedRight) {
            this.requestModelDataUpdate();
            if (this.hasLevel()) {
                this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 16);
            }
        }
        this.filtering.updateFilterPresence();
    }

    public boolean isConnected(boolean leftSide) {
        return leftSide ? this.connectedLeft : this.connectedRight;
    }

    @Override
    public void updateTunnelConnections() {
        BrassTunnelBlockEntity adjacent;
        super.updateTunnelConnections();
        boolean connectivityChanged = false;
        boolean nowConnectedLeft = this.determineIfConnected(true);
        boolean nowConnectedRight = this.determineIfConnected(false);
        if (this.connectedLeft != nowConnectedLeft) {
            this.connectedLeft = nowConnectedLeft;
            connectivityChanged = true;
            adjacent = this.getAdjacent(true);
            if (adjacent != null && !this.level.isClientSide) {
                adjacent.updateTunnelConnections();
                adjacent.selectionMode.setValue(this.selectionMode.getValue());
            }
        }
        if (this.connectedRight != nowConnectedRight) {
            this.connectedRight = nowConnectedRight;
            connectivityChanged = true;
            adjacent = this.getAdjacent(false);
            if (adjacent != null && !this.level.isClientSide) {
                adjacent.updateTunnelConnections();
                adjacent.selectionMode.setValue(this.selectionMode.getValue());
            }
        }
        if (this.filtering != null) {
            this.filtering.updateFilterPresence();
        }
        if (connectivityChanged) {
            this.sendData();
        }
    }

    protected boolean determineIfConnected(boolean leftSide) {
        if (this.flaps.isEmpty()) {
            return false;
        }
        BrassTunnelBlockEntity adjacentTunnelBE = this.getAdjacent(leftSide);
        return adjacentTunnelBE != null && !adjacentTunnelBE.flaps.isEmpty();
    }

    @Nullable
    protected BrassTunnelBlockEntity getAdjacent(boolean leftSide) {
        if (!this.hasLevel()) {
            return null;
        }
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.BRASS_TUNNEL.has(blockState)) {
            return null;
        }
        Direction.Axis axis = (Direction.Axis)blockState.getValue(BrassTunnelBlock.HORIZONTAL_AXIS);
        Direction baseDirection = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        Direction direction = leftSide ? baseDirection.getCounterClockWise() : baseDirection.getClockWise();
        BlockPos adjacentPos = this.worldPosition.relative(direction);
        BlockState adjacentBlockState = this.level.getBlockState(adjacentPos);
        if (!AllBlocks.BRASS_TUNNEL.has(adjacentBlockState)) {
            return null;
        }
        if (adjacentBlockState.getValue(BrassTunnelBlock.HORIZONTAL_AXIS) != axis) {
            return null;
        }
        BlockEntity adjacentBE = this.level.getBlockEntity(adjacentPos);
        if (adjacentBE.isRemoved()) {
            return null;
        }
        if (!(adjacentBE instanceof BrassTunnelBlockEntity)) {
            return null;
        }
        return (BrassTunnelBlockEntity)adjacentBE;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.invalidateCapabilities();
    }

    @Override
    public void destroy() {
        super.destroy();
        Block.popResource((Level)this.level, (BlockPos)this.worldPosition, (ItemStack)this.stackToDistribute);
        this.stackEnteredFrom = null;
    }

    public IItemHandler getBeltCapability() {
        BlockEntity blockEntity;
        if (this.beltCapability == null && (blockEntity = this.level.getBlockEntity(this.worldPosition.below())) != null) {
            this.beltCapability = (IItemHandler)this.level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null);
        }
        return this.beltCapability;
    }

    public boolean canTakeItems() {
        return this.stackToDistribute.isEmpty() && !this.syncedOutputActive;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        List<ItemStack> allStacks = this.grabAllStacksOfGroup(true);
        if (allStacks.isEmpty()) {
            return false;
        }
        CreateLang.translate("tooltip.brass_tunnel.contains", new Object[0]).style(ChatFormatting.WHITE).forGoggles(tooltip);
        for (ItemStack item : allStacks) {
            CreateLang.translate("tooltip.brass_tunnel.contains_entry", Component.translatable((String)item.getDescriptionId()).getString(), item.getCount()).style(ChatFormatting.GRAY).forGoggles(tooltip);
        }
        CreateLang.translate("tooltip.brass_tunnel.retrieve", new Object[0]).style(ChatFormatting.DARK_GRAY).forGoggles(tooltip);
        return true;
    }

    public static enum SelectionMode implements INamedIconOptions
    {
        SPLIT(AllIcons.I_TUNNEL_SPLIT),
        FORCED_SPLIT(AllIcons.I_TUNNEL_FORCED_SPLIT),
        ROUND_ROBIN(AllIcons.I_TUNNEL_ROUND_ROBIN),
        FORCED_ROUND_ROBIN(AllIcons.I_TUNNEL_FORCED_ROUND_ROBIN),
        PREFER_NEAREST(AllIcons.I_TUNNEL_PREFER_NEAREST),
        RANDOMIZE(AllIcons.I_TUNNEL_RANDOMIZE),
        SYNCHRONIZE(AllIcons.I_TUNNEL_SYNCHRONIZE);

        private final String translationKey;
        private final AllIcons icon;

        private SelectionMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "create.tunnel.selection_mode." + Lang.asId((String)this.name());
        }

        @Override
        public AllIcons getIcon() {
            return this.icon;
        }

        @Override
        public String getTranslationKey() {
            return this.translationKey;
        }
    }
}
