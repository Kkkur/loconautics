/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.Create
 *  com.simibubi.create.content.redstone.link.IRedstoneLinkable
 *  com.simibubi.create.content.redstone.link.LinkBehaviour
 *  com.simibubi.create.content.redstone.link.RedstoneLinkBlock
 *  com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler$Frequency
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Dual
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Tuple
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.tuple.Pair
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.redstone;

import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.redstone.LinkedReceiverFrequencySlot;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public abstract class AbstractLinkedReceiverBlockEntity
extends SmartBlockEntity {
    protected LinkBehaviour link;
    protected int lastCheckedStatus;
    public int receivedSignal;
    public double rawSignalValue;
    protected boolean receivedSignalChanged;

    public AbstractLinkedReceiverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.createLink();
        behaviours.add((BlockEntityBehaviour)this.link);
    }

    protected void createLink() {
        Pair slots = ValueBoxTransform.Dual.makeSlots(LinkedReceiverFrequencySlot::new);
        this.link = LinkBehaviour.receiver((SmartBlockEntity)this, (Pair)slots, signal -> {});
    }

    public void setSignal(int power, double rawValue) {
        if (this.receivedSignal != power || rawValue != this.rawSignalValue) {
            this.receivedSignalChanged = true;
        }
        this.receivedSignal = power;
        this.rawSignalValue = rawValue;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            return;
        }
        int networkStatus = Create.REDSTONE_LINK_NETWORK_HANDLER.globalPowerVersion.get();
        if (networkStatus != this.lastCheckedStatus) {
            this.lastCheckedStatus = networkStatus;
        }
        this.updateSignal();
        BlockState blockState = this.getBlockState();
        if (this.getReceivedSignal() > 0 != (Boolean)blockState.getValue((Property)RedstoneLinkBlock.POWERED)) {
            this.receivedSignalChanged = true;
            this.level.setBlockAndUpdate(this.worldPosition, (BlockState)blockState.cycle((Property)RedstoneLinkBlock.POWERED));
        }
        if (this.receivedSignalChanged) {
            Direction attachedFace = ((Direction)blockState.getValue((Property)RedstoneLinkBlock.FACING)).getOpposite();
            BlockPos attachedPos = this.worldPosition.relative(attachedFace);
            this.level.blockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition).getBlock());
            this.level.blockUpdated(attachedPos, this.level.getBlockState(attachedPos).getBlock());
            this.receivedSignalChanged = false;
        }
    }

    public void updateSignal() {
        Couple freq;
        int newSignal = 0;
        double rawValue = 0.0;
        Map map = Create.REDSTONE_LINK_NETWORK_HANDLER.networksIn((LevelAccessor)this.level);
        Set set = (Set)map.get(freq = this.link.getNetworkKey());
        if (set != null && !set.isEmpty()) {
            Vector3d currentPos = JOMLConversion.atCenterOf((Vec3i)this.getBlockPos());
            SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
            if (subLevel != null) {
                subLevel.logicalPose().transformPosition(currentPos);
            }
            for (IRedstoneLinkable link : set) {
                Tuple<Integer, Double> signal;
                if (link.getTransmittedStrength() <= 0) continue;
                Vector3d targetPos = JOMLConversion.atCenterOf((Vec3i)link.getLocation());
                SubLevel targetWs = Sable.HELPER.getContaining(this.level, (Vec3i)link.getLocation());
                if (targetWs != null) {
                    targetWs.logicalPose().transformPosition(targetPos);
                }
                Vector3d relativePos = targetPos.sub((Vector3dc)currentPos);
                if (subLevel != null) {
                    subLevel.logicalPose().transformNormalInverse(relativePos);
                }
                if ((Integer)(signal = this.getSignalFromLink(JOMLConversion.toMojang((Vector3dc)relativePos), link.getTransmittedStrength())).getA() <= newSignal) continue;
                newSignal = (Integer)signal.getA();
                rawValue = (Double)signal.getB();
            }
        }
        this.setSignal(newSignal, rawValue);
    }

    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("Receive", this.getReceivedSignal());
        compound.putDouble("ReceivedValue", this.rawSignalValue);
        compound.putBoolean("ReceivedChanged", this.receivedSignalChanged);
        super.write(compound, registries, clientPacket);
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.receivedSignal = compound.getInt("Receive");
        this.rawSignalValue = compound.getDouble("ReceivedValue");
        this.receivedSignalChanged = compound.getBoolean("ReceivedChanged");
    }

    public int getReceivedSignal() {
        return this.receivedSignal;
    }

    public Couple<RedstoneLinkNetworkHandler.Frequency> getFrequency() {
        return this.link.getNetworkKey();
    }

    public abstract Tuple<Integer, Double> getSignalFromLink(Vec3 var1, int var2);

    public void remove() {
        super.remove();
        Direction attachedFace = ((Direction)this.getBlockState().getValue((Property)RedstoneLinkBlock.FACING)).getOpposite();
        BlockPos attachedPos = this.worldPosition.relative(attachedFace);
        this.level.blockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition).getBlock());
        this.level.blockUpdated(attachedPos, this.level.getBlockState(attachedPos).getBlock());
    }
}
