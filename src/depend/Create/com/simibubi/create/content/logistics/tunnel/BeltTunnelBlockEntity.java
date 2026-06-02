/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  dev.engine_room.flywheel.lib.visualization.VisualizationHelper
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.IntTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.content.logistics.tunnel;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import com.simibubi.create.content.logistics.tunnel.TunnelFlapPacket;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

public class BeltTunnelBlockEntity
extends SmartBlockEntity {
    public Map<Direction, LerpedFloat> flaps = new EnumMap<Direction, LerpedFloat>(Direction.class);
    public Set<Direction> sides = new HashSet<Direction>();
    protected IItemHandler cap = null;
    protected List<Pair<Direction, Boolean>> flapsToSend = new LinkedList<Pair<Direction, Boolean>>();

    public BeltTunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.ANDESITE_TUNNEL.get(), (be, context) -> {
            IItemHandler capBelow;
            BlockEntity beBelow;
            if (be.cap == null && AllBlocks.BELT.has(be.level.getBlockState(be.worldPosition.below())) && (beBelow = be.level.getBlockEntity(be.worldPosition.below())) != null && (capBelow = (IItemHandler)be.level.getCapability(Capabilities.ItemHandler.BLOCK, be.worldPosition.below(), (Object)Direction.UP)) != null) {
                be.cap = capBelow;
            }
            return be.cap;
        });
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.invalidateCapabilities();
    }

    protected void writeFlapsAndSides(CompoundTag compound) {
        ListTag flapsNBT = new ListTag();
        for (Direction direction : this.flaps.keySet()) {
            flapsNBT.add((Object)IntTag.valueOf((int)direction.get3DDataValue()));
        }
        compound.put("Flaps", (Tag)flapsNBT);
        ListTag sidesNBT = new ListTag();
        for (Direction direction : this.sides) {
            sidesNBT.add((Object)IntTag.valueOf((int)direction.get3DDataValue()));
        }
        compound.put("Sides", (Tag)sidesNBT);
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        this.writeFlapsAndSides(tag);
        super.writeSafe(tag, registries);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.writeFlapsAndSides(compound);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        HashSet<Direction> newFlaps = new HashSet<Direction>(6);
        ListTag flapsNBT = compound.getList("Flaps", 3);
        for (Tag inbt : flapsNBT) {
            if (!(inbt instanceof IntTag)) continue;
            newFlaps.add(Direction.from3DDataValue((int)((IntTag)inbt).getAsInt()));
        }
        this.sides.clear();
        ListTag sidesNBT = compound.getList("Sides", 3);
        for (Tag inbt : sidesNBT) {
            if (!(inbt instanceof IntTag)) continue;
            this.sides.add(Direction.from3DDataValue((int)((IntTag)inbt).getAsInt()));
        }
        for (Direction d : Iterate.directions) {
            if (!newFlaps.contains(d)) {
                this.flaps.remove(d);
                continue;
            }
            if (this.flaps.containsKey(d)) continue;
            this.flaps.put(d, this.createChasingFlap());
        }
        if (!compound.contains("Sides") && compound.contains("Flaps")) {
            this.sides.addAll(this.flaps.keySet());
        }
        super.read(compound, registries, clientPacket);
        if (clientPacket) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> VisualizationHelper.queueUpdate((BlockEntity)this));
        }
    }

    private LerpedFloat createChasingFlap() {
        return LerpedFloat.linear().startWithValue(0.25).chase(0.0, (double)0.05f, LerpedFloat.Chaser.EXP);
    }

    public void updateTunnelConnections() {
        this.flaps.clear();
        this.sides.clear();
        BlockState tunnelState = this.getBlockState();
        for (Direction direction : Iterate.horizontalDirections) {
            BlockState nextState;
            if (direction.getAxis() != tunnelState.getValue((Property)BlockStateProperties.HORIZONTAL_AXIS)) {
                boolean positive = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ^ direction.getAxis() == Direction.Axis.Z;
                BeltTunnelBlock.Shape shape = (BeltTunnelBlock.Shape)((Object)tunnelState.getValue(BeltTunnelBlock.SHAPE));
                if (BeltTunnelBlock.isStraight(tunnelState) || positive && shape == BeltTunnelBlock.Shape.T_LEFT || !positive && shape == BeltTunnelBlock.Shape.T_RIGHT) continue;
            }
            this.sides.add(direction);
            if (this.level == null || (nextState = this.level.getBlockState(this.worldPosition.relative(direction))).getBlock() instanceof BeltTunnelBlock || nextState.getBlock() instanceof BeltFunnelBlock && nextState.getValue(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.EXTENDED && nextState.getValue((Property)BeltFunnelBlock.HORIZONTAL_FACING) == direction.getOpposite()) continue;
            this.flaps.put(direction, this.createChasingFlap());
        }
        this.sendData();
    }

    public void flap(Direction side, boolean inward) {
        if (this.level.isClientSide) {
            if (this.flaps.containsKey(side)) {
                this.flaps.get(side).setValue(inward ? -1.0 : 1.0);
            }
            return;
        }
        this.flapsToSend.add((Pair<Direction, Boolean>)Pair.of((Object)side, (Object)inward));
    }

    @Override
    public void initialize() {
        super.initialize();
        this.updateTunnelConnections();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (!this.flapsToSend.isEmpty()) {
                this.sendFlaps();
            }
            return;
        }
        this.flaps.forEach((d, value) -> value.tickChaser());
    }

    private void sendFlaps() {
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            CatnipServices.NETWORK.sendToClientsTrackingChunk(serverLevel, new ChunkPos(this.worldPosition), (CustomPacketPayload)new TunnelFlapPacket(this, this.flapsToSend));
        }
        this.flapsToSend.clear();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
