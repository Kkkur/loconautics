/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.mojang.serialization.Codec
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.createmod.catnip.codecs.CatnipCodecs
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.google.common.cache.Cache;
import com.mojang.serialization.Codec;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRoutingTable;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorShape;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.codecs.CatnipCodecs;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ChainConveyorBlockEntity
extends KineticBlockEntity
implements TransformableBlockEntity,
Clearable {
    public Set<BlockPos> connections = new HashSet<BlockPos>();
    public Map<BlockPos, ConnectionStats> connectionStats;
    public Map<BlockPos, ConnectedPort> loopPorts = new HashMap<BlockPos, ConnectedPort>();
    public Map<BlockPos, ConnectedPort> travelPorts = new HashMap<BlockPos, ConnectedPort>();
    public ChainConveyorRoutingTable routingTable = new ChainConveyorRoutingTable();
    List<ChainConveyorPackage> loopingPackages = new ArrayList<ChainConveyorPackage>();
    Map<BlockPos, List<ChainConveyorPackage>> travellingPackages = new HashMap<BlockPos, List<ChainConveyorPackage>>();
    public boolean reversed;
    public boolean cancelDrops;
    public boolean checkInvalid = true;
    BlockPos chainDestroyedEffectToSend;

    public ChainConveyorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).inflate(this.connections.isEmpty() ? 3.0 : 64.0);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        this.updateChainShapes();
    }

    public boolean canAcceptMorePackages() {
        return this.loopingPackages.size() + this.travellingPackages.size() < (Integer)AllConfigs.server().logistics.chainConveyorCapacity.get();
    }

    public boolean canAcceptPackagesFor(@Nullable BlockPos connection) {
        ChainConveyorBlockEntity otherClbe;
        BlockEntity blockEntity;
        if (connection == null && !this.canAcceptMorePackages()) {
            return false;
        }
        return connection == null || (blockEntity = this.level.getBlockEntity(this.worldPosition.offset((Vec3i)connection))) instanceof ChainConveyorBlockEntity && (otherClbe = (ChainConveyorBlockEntity)blockEntity).canAcceptMorePackages();
    }

    public boolean canAcceptMorePackagesFromOtherConveyor() {
        return this.loopingPackages.size() < (Integer)AllConfigs.server().logistics.chainConveyorCapacity.get();
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return super.addToTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public void tick() {
        Iterator iterator;
        super.tick();
        if (this.checkInvalid && !this.level.isClientSide()) {
            this.checkInvalid = false;
            this.removeInvalidConnections();
        }
        float serverSpeed = this.level.isClientSide() && !this.isVirtual() ? ServerSpeedProvider.get() : 1.0f;
        float speed = this.getSpeed() / 360.0f;
        float radius = 1.5f;
        float distancePerTick = Math.abs(speed);
        float degreesPerTick = speed / ((float)Math.PI * radius) * 360.0f;
        boolean reversedPreviously = this.reversed;
        this.prepareStats();
        if (this.level.isClientSide() && !VisualizationManager.supportsVisualization((LevelAccessor)this.level)) {
            this.tickBoxVisuals();
        }
        if (!this.level.isClientSide()) {
            this.routingTable.tick();
            if (this.routingTable.shouldAdvertise()) {
                for (BlockPos blockPos : this.connections) {
                    BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.offset((Vec3i)blockPos));
                    if (!(blockEntity instanceof ChainConveyorBlockEntity)) continue;
                    ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
                    this.routingTable.advertiseTo(blockPos, clbe.routingTable);
                }
                this.routingTable.changed = false;
                this.routingTable.lastUpdate = 0;
            }
        }
        if (speed == 0.0f) {
            this.updateBoxWorldPositions();
            return;
        }
        if (reversedPreviously != this.reversed) {
            for (Map.Entry entry : this.travellingPackages.entrySet()) {
                BlockPos offset = (BlockPos)entry.getKey();
                BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.offset((Vec3i)offset));
                if (!(blockEntity instanceof ChainConveyorBlockEntity)) continue;
                ChainConveyorBlockEntity otherLift = (ChainConveyorBlockEntity)blockEntity;
                iterator = ((List)entry.getValue()).iterator();
                while (iterator.hasNext()) {
                    ChainConveyorPackage chainConveyorPackage = (ChainConveyorPackage)iterator.next();
                    if (chainConveyorPackage.justFlipped) continue;
                    chainConveyorPackage.justFlipped = true;
                    float length = (float)Vec3.atLowerCornerOf((Vec3i)offset).length() - 1.375f;
                    chainConveyorPackage.chainPosition = length - chainConveyorPackage.chainPosition;
                    otherLift.addTravellingPackage(chainConveyorPackage, offset.multiply(-1));
                    iterator.remove();
                }
            }
            this.notifyUpdate();
        }
        for (Map.Entry entry : this.travellingPackages.entrySet()) {
            BlockPos target = (BlockPos)entry.getKey();
            ConnectionStats stats = this.connectionStats.get(target);
            if (stats == null) continue;
            iterator = ((List)entry.getValue()).iterator();
            block4: while (iterator.hasNext()) {
                BlockEntity blockEntity;
                ChainConveyorPackage chainConveyorPackage = (ChainConveyorPackage)iterator.next();
                chainConveyorPackage.justFlipped = false;
                float prevChainPosition = chainConveyorPackage.chainPosition;
                chainConveyorPackage.chainPosition += serverSpeed * distancePerTick;
                float anticipatePosition = chainConveyorPackage.chainPosition = Math.min(stats.chainLength, chainConveyorPackage.chainPosition);
                anticipatePosition += serverSpeed * distancePerTick * 4.0f;
                anticipatePosition = Math.min(stats.chainLength, anticipatePosition);
                if (this.level.isClientSide() && !this.isVirtual()) continue;
                for (Map.Entry<BlockPos, ConnectedPort> portEntry : this.travelPorts.entrySet()) {
                    boolean notAtPositionYet;
                    ConnectedPort port = portEntry.getValue();
                    float chainPosition = port.chainPosition();
                    if (prevChainPosition > chainPosition || !target.equals((Object)port.connection)) continue;
                    boolean bl = notAtPositionYet = chainConveyorPackage.chainPosition < chainPosition;
                    if (notAtPositionYet && anticipatePosition < chainPosition || !PackageItem.matchAddress(chainConveyorPackage.item, port.filter())) continue;
                    if (notAtPositionYet) {
                        this.notifyPortToAnticipate(portEntry.getKey());
                        continue;
                    }
                    if (!this.exportToPort(chainConveyorPackage, portEntry.getKey())) continue;
                    iterator.remove();
                    this.notifyUpdate();
                    continue block4;
                }
                if (chainConveyorPackage.chainPosition < stats.chainLength || !((blockEntity = this.level.getBlockEntity(this.worldPosition.offset((Vec3i)target))) instanceof ChainConveyorBlockEntity)) continue;
                ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
                chainConveyorPackage.chainPosition = this.wrapAngle(stats.tangentAngle + 180.0f + (float)(70 * (this.reversed ? -1 : 1)));
                clbe.addLoopingPackage(chainConveyorPackage);
                iterator.remove();
                this.notifyUpdate();
            }
        }
        Iterator<ChainConveyorPackage> iterator2 = this.loopingPackages.iterator();
        block6: while (iterator2.hasNext()) {
            ChainConveyorPackage chainConveyorPackage = iterator2.next();
            chainConveyorPackage.justFlipped = false;
            float prevChainPosition = chainConveyorPackage.chainPosition;
            chainConveyorPackage.chainPosition += serverSpeed * degreesPerTick;
            float anticipatePosition = chainConveyorPackage.chainPosition = this.wrapAngle(chainConveyorPackage.chainPosition);
            anticipatePosition += serverSpeed * degreesPerTick * 4.0f;
            anticipatePosition = this.wrapAngle(anticipatePosition);
            if (this.level.isClientSide()) continue;
            for (Map.Entry entry : this.loopPorts.entrySet()) {
                boolean notAtPositionYet;
                ConnectedPort port = (ConnectedPort)entry.getValue();
                float offBranchAngle = port.chainPosition();
                boolean bl = notAtPositionYet = !this.loopThresholdCrossed(chainConveyorPackage.chainPosition, prevChainPosition, offBranchAngle);
                if (notAtPositionYet && !this.loopThresholdCrossed(anticipatePosition, prevChainPosition, offBranchAngle) || !PackageItem.matchAddress(chainConveyorPackage.item, port.filter())) continue;
                if (notAtPositionYet) {
                    this.notifyPortToAnticipate((BlockPos)entry.getKey());
                    continue;
                }
                if (!this.exportToPort(chainConveyorPackage, (BlockPos)entry.getKey())) continue;
                iterator2.remove();
                this.notifyUpdate();
                continue block6;
            }
            for (BlockPos blockPos : this.connections) {
                float offBranchAngle;
                ChainConveyorBlockEntity ccbe;
                BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.offset((Vec3i)blockPos));
                if (blockEntity instanceof ChainConveyorBlockEntity && !(ccbe = (ChainConveyorBlockEntity)blockEntity).canAcceptMorePackagesFromOtherConveyor() || !this.loopThresholdCrossed(chainConveyorPackage.chainPosition, prevChainPosition, offBranchAngle = this.connectionStats.get((Object)blockPos).tangentAngle) || !this.routingTable.getExitFor(chainConveyorPackage.item).equals((Object)blockPos)) continue;
                chainConveyorPackage.chainPosition = 0.0f;
                this.addTravellingPackage(chainConveyorPackage, blockPos);
                iterator2.remove();
                continue block6;
            }
        }
        this.updateBoxWorldPositions();
    }

    public void removeInvalidConnections() {
        boolean changed = false;
        Iterator<BlockPos> iterator = this.connections.iterator();
        while (iterator.hasNext()) {
            BlockPos next = iterator.next();
            BlockPos target = this.worldPosition.offset((Vec3i)next);
            if (!this.level.isLoaded(target)) continue;
            BlockEntity blockEntity = this.level.getBlockEntity(target);
            if (blockEntity instanceof ChainConveyorBlockEntity) {
                ChainConveyorBlockEntity ccbe = (ChainConveyorBlockEntity)blockEntity;
                if (ccbe.connections.contains(next.multiply(-1))) continue;
            }
            iterator.remove();
            changed = true;
        }
        if (changed) {
            this.notifyUpdate();
        }
    }

    public void notifyConnectedToValidate() {
        for (BlockPos blockPos : this.connections) {
            BlockEntity blockEntity;
            BlockPos target = this.worldPosition.offset((Vec3i)blockPos);
            if (!this.level.isLoaded(target) || !((blockEntity = this.level.getBlockEntity(target)) instanceof ChainConveyorBlockEntity)) continue;
            ChainConveyorBlockEntity ccbe = (ChainConveyorBlockEntity)blockEntity;
            ccbe.checkInvalid = true;
        }
    }

    public void tickBoxVisuals() {
        for (ChainConveyorPackage chainConveyorPackage : this.loopingPackages) {
            this.tickBoxVisuals(chainConveyorPackage);
        }
        for (Map.Entry entry : this.travellingPackages.entrySet()) {
            for (ChainConveyorPackage box : (List)entry.getValue()) {
                this.tickBoxVisuals(box);
            }
        }
    }

    public boolean loopThresholdCrossed(float chainPosition, float prevChainPosition, float offBranchAngle) {
        int sign2;
        int sign1 = Mth.sign((double)AngleHelper.getShortestAngleDiff((double)offBranchAngle, (double)prevChainPosition));
        boolean notCrossed = sign1 >= (sign2 = Mth.sign((double)AngleHelper.getShortestAngleDiff((double)offBranchAngle, (double)chainPosition))) && !this.reversed || sign1 <= sign2 && this.reversed;
        return !notCrossed;
    }

    private boolean exportToPort(ChainConveyorPackage box, BlockPos offset) {
        BlockPos globalPos = this.worldPosition.offset((Vec3i)offset);
        BlockEntity blockEntity = this.level.getBlockEntity(globalPos);
        if (!(blockEntity instanceof FrogportBlockEntity)) {
            return false;
        }
        FrogportBlockEntity ppbe = (FrogportBlockEntity)blockEntity;
        if (ppbe.isAnimationInProgress()) {
            return false;
        }
        if (ppbe.isBackedUp()) {
            return false;
        }
        ppbe.startAnimation(box.item, false);
        return true;
    }

    private void notifyPortToAnticipate(BlockPos offset) {
        BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.offset((Vec3i)offset));
        if (blockEntity instanceof FrogportBlockEntity) {
            FrogportBlockEntity ppbe = (FrogportBlockEntity)blockEntity;
            ppbe.sendAnticipate();
        }
    }

    public boolean addTravellingPackage(ChainConveyorPackage box, BlockPos connection) {
        if (!this.connections.contains(connection)) {
            return false;
        }
        this.travellingPackages.computeIfAbsent(connection, $ -> new ArrayList()).add(box);
        if (this.level.isClientSide) {
            return true;
        }
        this.notifyUpdate();
        return true;
    }

    @Override
    public void notifyUpdate() {
        this.level.blockEntityChanged(this.worldPosition);
        this.sendData();
    }

    public boolean addLoopingPackage(ChainConveyorPackage box) {
        this.loopingPackages.add(box);
        this.notifyUpdate();
        return true;
    }

    public void prepareStats() {
        float speed = this.getSpeed();
        if (this.reversed != speed < 0.0f && speed != 0.0f) {
            this.reversed = speed < 0.0f;
            this.connectionStats = null;
        }
        if (this.connectionStats == null) {
            this.connectionStats = new HashMap<BlockPos, ConnectionStats>();
            this.connections.forEach(this::calculateConnectionStats);
        }
    }

    public void updateBoxWorldPositions() {
        this.prepareStats();
        for (Map.Entry<BlockPos, List<ChainConveyorPackage>> entry : this.travellingPackages.entrySet()) {
            BlockPos target = entry.getKey();
            ConnectionStats stats = this.connectionStats.get(target);
            if (stats == null) continue;
            for (ChainConveyorPackage box : entry.getValue()) {
                box.worldPosition = this.getPackagePosition(box.chainPosition, target);
                if (this.level == null || !this.level.isClientSide()) continue;
                Vec3 diff = stats.end.subtract(stats.start).normalize();
                box.yaw = Mth.wrapDegrees((float)((float)Mth.atan2((double)diff.x, (double)diff.z) * 57.295776f - 90.0f));
            }
        }
        for (ChainConveyorPackage box : this.loopingPackages) {
            box.worldPosition = this.getPackagePosition(box.chainPosition, null);
            box.yaw = Mth.wrapDegrees((float)box.chainPosition);
            if (!this.reversed) continue;
            box.yaw += 180.0f;
        }
    }

    public Vec3 getPackagePosition(float chainPosition, @Nullable BlockPos travelTarget) {
        if (travelTarget == null) {
            return Vec3.atBottomCenterOf((Vec3i)this.worldPosition).add(VecHelper.rotate((Vec3)new Vec3(0.0, 0.375, 0.875), (double)chainPosition, (Direction.Axis)Direction.Axis.Y));
        }
        this.prepareStats();
        ConnectionStats stats = this.connectionStats.get(travelTarget);
        if (stats == null) {
            return Vec3.ZERO;
        }
        Vec3 diff = stats.end.subtract(stats.start).normalize();
        return stats.start.add(diff.scale((double)Math.min(stats.chainLength, chainPosition)));
    }

    private void tickBoxVisuals(ChainConveyorPackage box) {
        if (box.worldPosition == null) {
            return;
        }
        ChainConveyorPackage.ChainConveyorPackagePhysicsData physicsData = box.physicsData((LevelAccessor)this.level);
        physicsData.setBE(this);
        if (!physicsData.shouldTick() && !this.isVirtual()) {
            return;
        }
        physicsData.prevTargetPos = physicsData.targetPos;
        physicsData.prevPos = physicsData.pos;
        physicsData.prevYaw = physicsData.yaw;
        physicsData.flipped = this.reversed;
        if (physicsData.pos != null) {
            if (physicsData.pos.distanceToSqr(box.worldPosition) > 2.25) {
                physicsData.pos = box.worldPosition.add(physicsData.pos.subtract(box.worldPosition).normalize().scale(1.5));
            }
            physicsData.motion = physicsData.motion.add(0.0, -0.25, 0.0).scale(0.75).add(box.worldPosition.subtract(physicsData.pos).scale(0.25));
            physicsData.pos = physicsData.pos.add(physicsData.motion);
        }
        physicsData.targetPos = box.worldPosition.subtract(0.0, 0.5625, 0.0);
        if (physicsData.pos == null) {
            physicsData.pos = physicsData.targetPos;
            physicsData.prevPos = physicsData.targetPos;
            physicsData.prevTargetPos = physicsData.targetPos;
        }
        physicsData.yaw = AngleHelper.angleLerp((double)0.25, (double)physicsData.yaw, (double)box.yaw);
    }

    private void calculateConnectionStats(BlockPos connection) {
        boolean reversed = this.getSpeed() < 0.0f;
        float offBranchDistance = 35.0f;
        float direction = 57.295776f * (float)Mth.atan2((double)connection.getX(), (double)connection.getZ());
        float angle = this.wrapAngle(direction - offBranchDistance * (float)(reversed ? -1 : 1));
        float oppositeAngle = this.wrapAngle(angle + 180.0f + 2.0f * offBranchDistance * (float)(reversed ? -1 : 1));
        Vec3 start = Vec3.atBottomCenterOf((Vec3i)this.worldPosition).add(VecHelper.rotate((Vec3)new Vec3(0.0, 0.0, 1.25), (double)angle, (Direction.Axis)Direction.Axis.Y)).add(0.0, 0.375, 0.0);
        Vec3 end = Vec3.atBottomCenterOf((Vec3i)this.worldPosition.offset((Vec3i)connection)).add(VecHelper.rotate((Vec3)new Vec3(0.0, 0.0, 1.25), (double)oppositeAngle, (Direction.Axis)Direction.Axis.Y)).add(0.0, 0.375, 0.0);
        float length = (float)start.distanceTo(end);
        this.connectionStats.put(connection, new ConnectionStats(angle, length, start, end));
    }

    public boolean addConnectionTo(BlockPos target) {
        BlockPos localTarget = target.subtract((Vec3i)this.worldPosition);
        boolean added = this.connections.add(localTarget);
        if (added) {
            this.notifyUpdate();
            this.calculateConnectionStats(localTarget);
            this.updateChainShapes();
        }
        this.detachKinetics();
        this.updateSpeed = true;
        return added;
    }

    public void chainDestroyed(BlockPos target, boolean spawnDrops, boolean sendEffect) {
        int chainCount = ChainConveyorBlockEntity.getChainCost(target);
        if (sendEffect) {
            this.chainDestroyedEffectToSend = target;
            this.sendData();
        }
        if (!spawnDrops) {
            return;
        }
        if (!this.forPointsAlongChains(target, chainCount, vec -> this.level.addFreshEntity((Entity)new ItemEntity(this.level, vec.x, vec.y, vec.z, new ItemStack((ItemLike)Items.CHAIN))))) {
            while (chainCount > 0) {
                Block.popResource((Level)this.level, (BlockPos)this.worldPosition, (ItemStack)new ItemStack((ItemLike)Blocks.CHAIN.asItem(), Math.min(chainCount, 64)));
                chainCount -= 64;
            }
        }
    }

    public boolean removeConnectionTo(BlockPos target) {
        BlockPos localTarget = target.subtract((Vec3i)this.worldPosition);
        if (!this.connections.contains(localTarget)) {
            return false;
        }
        this.detachKinetics();
        this.connections.remove(localTarget);
        this.connectionStats.remove(localTarget);
        List<ChainConveyorPackage> packages = this.travellingPackages.remove(localTarget);
        if (packages != null) {
            for (ChainConveyorPackage box : packages) {
                this.drop(box);
            }
        }
        this.notifyUpdate();
        this.updateChainShapes();
        this.updateSpeed = true;
        return true;
    }

    private void updateChainShapes() {
        this.prepareStats();
        ArrayList<ChainConveyorShape> shapes = new ArrayList<ChainConveyorShape>();
        shapes.add(new ChainConveyorShape.ChainConveyorBB(Vec3.atBottomCenterOf((Vec3i)BlockPos.ZERO)));
        for (BlockPos target : this.connections) {
            ConnectionStats stats = this.connectionStats.get(target);
            if (stats == null) continue;
            Vec3 localStart = stats.start.subtract(Vec3.atLowerCornerOf((Vec3i)this.worldPosition));
            Vec3 localEnd = stats.end.subtract(Vec3.atLowerCornerOf((Vec3i)this.worldPosition));
            shapes.add(new ChainConveyorShape.ChainConveyorOBB(target, localStart, localEnd));
        }
        if (this.level != null && this.level.isClientSide()) {
            ((Cache)ChainConveyorInteractionHandler.loadedChains.get((LevelAccessor)this.level)).put((Object)this.worldPosition, shapes);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (this.level == null || !this.level.isClientSide()) {
            return;
        }
        for (BlockPos blockPos : this.connections) {
            this.spawnDestroyParticles(blockPos);
        }
    }

    private void spawnDestroyParticles(BlockPos blockPos) {
        this.forPointsAlongChains(blockPos, (int)Math.round(Vec3.atLowerCornerOf((Vec3i)blockPos).length() * 8.0), vec -> this.level.addParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CHAIN.defaultBlockState()), vec.x, vec.y, vec.z, 0.0, 0.0, 0.0));
    }

    public void clearContent() {
        this.connections.clear();
        this.travellingPackages.clear();
        this.loopingPackages.clear();
    }

    @Override
    public void destroy() {
        super.destroy();
        for (BlockPos blockPos : this.connections) {
            this.chainDestroyed(blockPos, !this.cancelDrops, false);
            BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.offset((Vec3i)blockPos));
            if (!(blockEntity instanceof ChainConveyorBlockEntity)) continue;
            ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
            clbe.removeConnectionTo(this.worldPosition);
        }
        for (ChainConveyorPackage chainConveyorPackage : this.loopingPackages) {
            this.drop(chainConveyorPackage);
        }
        for (Map.Entry entry : this.travellingPackages.entrySet()) {
            for (ChainConveyorPackage box : (List)entry.getValue()) {
                this.drop(box);
            }
        }
    }

    public boolean forPointsAlongChains(BlockPos connection, int positions, Consumer<Vec3> callback) {
        this.prepareStats();
        ConnectionStats stats = this.connectionStats.get(connection);
        if (stats == null) {
            return false;
        }
        Vec3 start = stats.start;
        Vec3 direction = stats.end.subtract(start);
        Vec3 origin = Vec3.atCenterOf((Vec3i)this.worldPosition);
        Vec3 normal = direction.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        Vec3 offset = start.subtract(origin);
        Vec3 start2 = origin.add(offset.add(normal.scale(-2.0 * normal.dot(offset))));
        for (boolean firstChain : Iterate.trueAndFalse) {
            int steps = positions / 2;
            if (firstChain) {
                steps += positions % 2;
            }
            for (int i = 0; i < steps; ++i) {
                callback.accept((firstChain ? start : start2).add(direction.scale((0.5 + (double)i) / (double)steps)));
            }
        }
        return true;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.level != null && this.level.isClientSide()) {
            ((Cache)ChainConveyorInteractionHandler.loadedChains.get((LevelAccessor)this.level)).invalidate((Object)this.worldPosition);
        }
    }

    private void drop(ChainConveyorPackage box) {
        if (box.worldPosition != null) {
            this.level.addFreshEntity((Entity)PackageEntity.fromItemStack(this.level, box.worldPosition.subtract(0.0, 0.5, 0.0), box.item));
        }
    }

    @Override
    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        this.connections.forEach(p -> neighbours.add(this.worldPosition.offset((Vec3i)p)));
        return super.addPropagationLocations(block, state, neighbours);
    }

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        if (this.connections.contains(target.getBlockPos().subtract((Vec3i)this.worldPosition))) {
            if (!(target instanceof ChainConveyorBlockEntity)) {
                return 0.0f;
            }
            return 1.0f;
        }
        return super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeSafe(tag, registries);
        tag.put("Connections", (Tag)CatnipCodecUtils.encode((Codec)CatnipCodecs.set((Codec)BlockPos.CODEC), (HolderLookup.Provider)registries, this.connections).orElseThrow());
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if (clientPacket && this.chainDestroyedEffectToSend != null) {
            compound.put("DestroyEffect", NbtUtils.writeBlockPos((BlockPos)this.chainDestroyedEffectToSend));
            this.chainDestroyedEffectToSend = null;
        }
        compound.put("Connections", (Tag)CatnipCodecUtils.encode((Codec)CatnipCodecs.set((Codec)BlockPos.CODEC), (HolderLookup.Provider)registries, this.connections).orElseThrow());
        compound.put("TravellingPackages", (Tag)NBTHelper.writeCompoundList(this.travellingPackages.entrySet(), entry -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("Target", NbtUtils.writeBlockPos((BlockPos)((BlockPos)entry.getKey())));
            compoundTag.put("Packages", (Tag)NBTHelper.writeCompoundList((Iterable)((Iterable)entry.getValue()), p -> clientPacket ? p.writeToClient(registries) : p.write(registries)));
            return compoundTag;
        }));
        compound.put("LoopingPackages", (Tag)NBTHelper.writeCompoundList(this.loopingPackages, p -> clientPacket ? p.writeToClient(registries) : p.write(registries)));
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (clientPacket && compound.contains("DestroyEffect") && this.level != null) {
            this.spawnDestroyParticles(NBTHelper.readBlockPos((CompoundTag)compound, (String)"DestroyEffect"));
        }
        int sizeBefore = this.connections.size();
        this.connections.clear();
        CatnipCodecUtils.decode((Codec)CatnipCodecs.set((Codec)BlockPos.CODEC), (HolderLookup.Provider)registries, (Tag)compound.get("Connections")).ifPresent(this.connections::addAll);
        this.travellingPackages.clear();
        NBTHelper.iterateCompoundList((ListTag)compound.getList("TravellingPackages", 10), c -> this.travellingPackages.put(NBTHelper.readBlockPos((CompoundTag)c, (String)"Target"), NBTHelper.readCompoundList((ListTag)c.getList("Packages", 10), t -> ChainConveyorPackage.read(t, registries))));
        this.loopingPackages = NBTHelper.readCompoundList((ListTag)compound.getList("LoopingPackages", 10), t -> ChainConveyorPackage.read(t, registries));
        this.connectionStats = null;
        this.updateBoxWorldPositions();
        this.updateChainShapes();
        if (this.connections.size() != sizeBefore && this.level != null && this.level.isClientSide) {
            this.invalidateRenderBoundingBox();
        }
    }

    public float wrapAngle(float angle) {
        if ((angle %= 360.0f) < 0.0f) {
            angle += 360.0f;
        }
        return angle;
    }

    public static int getChainCost(BlockPos connection) {
        return (int)Math.max(Math.round(Vec3.atLowerCornerOf((Vec3i)connection).length() / 2.5), 1L);
    }

    public static boolean getChainsFromInventory(Player player, ItemStack chain, int cost, boolean simulate) {
        int found = 0;
        Inventory inv = player.getInventory();
        int size = inv.items.size();
        for (int j = 0; j <= size + 1; ++j) {
            boolean offhand;
            int i = j;
            boolean bl = offhand = j == size + 1;
            if (j == size) {
                i = inv.selected;
            } else if (offhand) {
                i = 0;
            } else if (j == inv.selected) continue;
            ItemStack stackInSlot = (ItemStack)(offhand ? inv.offhand : inv.items).get(i);
            if (!stackInSlot.is(chain.getItem()) || found >= cost) continue;
            int count = stackInSlot.getCount();
            if (!simulate) {
                int remainingItems = count - Math.min(cost - found, count);
                ItemStack newItem = stackInSlot.copyWithCount(remainingItems);
                if (offhand) {
                    player.setItemInHand(InteractionHand.OFF_HAND, newItem);
                } else {
                    inv.setItem(i, newItem);
                }
            }
            found += count;
        }
        return found >= cost;
    }

    public List<ChainConveyorPackage> getLoopingPackages() {
        return this.loopingPackages;
    }

    public Map<BlockPos, List<ChainConveyorPackage>> getTravellingPackages() {
        return this.travellingPackages;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state) {
        return super.getRequiredItems(state);
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        if (this.connections == null || this.connections.isEmpty()) {
            return;
        }
        this.connections = new HashSet<BlockPos>(this.connections.stream().map(transform::applyWithoutOffset).toList());
        HashMap<BlockPos, List<ChainConveyorPackage>> newMap = new HashMap<BlockPos, List<ChainConveyorPackage>>();
        this.travellingPackages.entrySet().forEach(e -> newMap.put(transform.applyWithoutOffset((BlockPos)e.getKey()), (List)e.getValue()));
        this.travellingPackages = newMap;
        this.connectionStats = null;
        this.notifyUpdate();
    }

    public record ConnectionStats(float tangentAngle, float chainLength, Vec3 start, Vec3 end) {
    }

    public record ConnectedPort(float chainPosition, @Nullable BlockPos connection, String filter) {
    }
}
