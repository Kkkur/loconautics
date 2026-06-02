/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.packagePort;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlock;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.logistics.packagePort.AllPackagePortTargetTypes;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetType;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import java.util.Optional;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class PackagePortTarget {
    public static final Codec<PackagePortTarget> CODEC = CreateBuiltInRegistries.PACKAGE_PORT_TARGET_TYPE.byNameCodec().dispatch(PackagePortTarget::getType, PackagePortTargetType::codec);
    public static final StreamCodec<? super RegistryFriendlyByteBuf, PackagePortTarget> STREAM_CODEC = ByteBufCodecs.registry(CreateRegistries.PACKAGE_PORT_TARGET_TYPE).dispatch(PackagePortTarget::getType, PackagePortTargetType::streamCodec);
    public BlockPos relativePos;

    public PackagePortTarget(BlockPos relativePos) {
        this.relativePos = relativePos;
    }

    public abstract boolean export(LevelAccessor var1, BlockPos var2, ItemStack var3, boolean var4);

    public void setup(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
    }

    public void register(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
    }

    public void deregister(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
    }

    public abstract Vec3 getExactTargetLocation(PackagePortBlockEntity var1, LevelAccessor var2, BlockPos var3);

    public abstract ItemStack getIcon();

    public abstract boolean canSupport(BlockEntity var1);

    public boolean depositImmediately() {
        return false;
    }

    protected abstract PackagePortTargetType getType();

    public BlockEntity be(LevelAccessor level, BlockPos portPos) {
        Level l;
        if (level instanceof Level && !(l = (Level)level).isLoaded(portPos.offset((Vec3i)this.relativePos))) {
            return null;
        }
        return level.getBlockEntity(portPos.offset((Vec3i)this.relativePos));
    }

    public static class TrainStationFrogportTarget
    extends PackagePortTarget {
        public static MapCodec<TrainStationFrogportTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockPos.CODEC.fieldOf("relative_pos").forGetter(i -> i.relativePos)).apply((Applicative)instance, TrainStationFrogportTarget::new));
        public static final StreamCodec<ByteBuf, TrainStationFrogportTarget> STREAM_CODEC = BlockPos.STREAM_CODEC.map(TrainStationFrogportTarget::new, i -> i.relativePos);

        public TrainStationFrogportTarget(BlockPos relativePos) {
            super(relativePos);
        }

        @Override
        public ItemStack getIcon() {
            return AllBlocks.TRACK_STATION.asStack();
        }

        @Override
        public boolean export(LevelAccessor level, BlockPos portPos, ItemStack box, boolean simulate) {
            return false;
        }

        @Override
        public Vec3 getExactTargetLocation(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
            return Vec3.atCenterOf((Vec3i)portPos.offset((Vec3i)this.relativePos));
        }

        @Override
        public void register(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
            BlockEntity blockEntity = this.be(level, portPos);
            if (blockEntity instanceof StationBlockEntity) {
                StationBlockEntity sbe = (StationBlockEntity)blockEntity;
                sbe.attachPackagePort(ppbe);
            }
        }

        @Override
        public void deregister(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
            BlockEntity blockEntity = this.be(level, portPos);
            if (blockEntity instanceof StationBlockEntity) {
                StationBlockEntity sbe = (StationBlockEntity)blockEntity;
                sbe.removePackagePort(ppbe);
            }
        }

        @Override
        public boolean depositImmediately() {
            return true;
        }

        @Override
        public boolean canSupport(BlockEntity be) {
            return AllBlockEntityTypes.PACKAGE_POSTBOX.is(be);
        }

        @Override
        protected PackagePortTargetType getType() {
            return (PackagePortTargetType)AllPackagePortTargetTypes.TRAIN_STATION.value();
        }

        public static class Type
        implements PackagePortTargetType {
            public MapCodec<TrainStationFrogportTarget> codec() {
                return CODEC;
            }

            public StreamCodec<ByteBuf, TrainStationFrogportTarget> streamCodec() {
                return STREAM_CODEC;
            }
        }
    }

    public static class ChainConveyorFrogportTarget
    extends PackagePortTarget {
        public static final MapCodec<ChainConveyorFrogportTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockPos.CODEC.fieldOf("relative_pos").forGetter(i -> i.relativePos), (App)Codec.FLOAT.fieldOf("chain_pos").forGetter(i -> Float.valueOf(i.chainPos)), (App)BlockPos.CODEC.optionalFieldOf("connection").forGetter(i -> Optional.ofNullable(i.connection)), (App)Codec.BOOL.fieldOf("flipped").forGetter(i -> i.flipped)).apply((Applicative)instance, ChainConveyorFrogportTarget::new));
        public static final StreamCodec<ByteBuf, ChainConveyorFrogportTarget> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, i -> i.relativePos, (StreamCodec)ByteBufCodecs.FLOAT, i -> Float.valueOf(i.chainPos), (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)BlockPos.STREAM_CODEC), i -> i.connection, (StreamCodec)ByteBufCodecs.BOOL, i -> i.flipped, ChainConveyorFrogportTarget::new);
        public float chainPos;
        @Nullable
        public BlockPos connection;
        public boolean flipped;

        public ChainConveyorFrogportTarget(BlockPos relativePos, float chainPos, Optional<BlockPos> connection, boolean flipped) {
            this(relativePos, chainPos, (BlockPos)connection.orElse(null), flipped);
        }

        public ChainConveyorFrogportTarget(BlockPos relativePos, float chainPos, @Nullable BlockPos connection, boolean flipped) {
            super(relativePos);
            this.chainPos = chainPos;
            this.connection = connection;
            this.flipped = flipped;
        }

        @Override
        public void setup(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
            BlockEntity blockEntity = this.be(level, portPos);
            if (blockEntity instanceof ChainConveyorBlockEntity) {
                ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
                this.flipped = clbe.getSpeed() < 0.0f;
            }
        }

        @Override
        public ItemStack getIcon() {
            return AllBlocks.CHAIN_CONVEYOR.asStack();
        }

        @Override
        public boolean export(LevelAccessor level, BlockPos portPos, ItemStack box, boolean simulate) {
            BlockEntity blockEntity = this.be(level, portPos);
            if (!(blockEntity instanceof ChainConveyorBlockEntity)) {
                return false;
            }
            ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
            if (this.connection != null && !clbe.connections.contains(this.connection)) {
                return false;
            }
            if (simulate) {
                return clbe.getSpeed() != 0.0f && clbe.canAcceptPackagesFor(this.connection);
            }
            ChainConveyorPackage box2 = new ChainConveyorPackage(this.chainPos, box.copy());
            if (this.connection == null) {
                return clbe.addLoopingPackage(box2);
            }
            return clbe.addTravellingPackage(box2, this.connection);
        }

        @Override
        public void register(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
            BlockEntity blockEntity = this.be(level, portPos);
            if (!(blockEntity instanceof ChainConveyorBlockEntity)) {
                return;
            }
            ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
            ChainConveyorBlockEntity actualBe = clbe;
            if (this.connection != null && clbe.getSpeed() < 0.0f != this.flipped) {
                this.deregister(ppbe, level, portPos);
                actualBe = (ChainConveyorBlockEntity)((ChainConveyorBlock)AllBlocks.CHAIN_CONVEYOR.get()).getBlockEntity((BlockGetter)level, clbe.getBlockPos().offset((Vec3i)this.connection));
                if (actualBe == null) {
                    return;
                }
                clbe.prepareStats();
                ChainConveyorBlockEntity.ConnectionStats stats = clbe.connectionStats.get(this.connection);
                if (stats != null) {
                    this.chainPos = stats.chainLength() - this.chainPos;
                }
                this.connection = this.connection.multiply(-1);
                this.flipped = !this.flipped;
                this.relativePos = actualBe.getBlockPos().subtract((Vec3i)portPos);
                ppbe.notifyUpdate();
            }
            if (this.connection != null && !actualBe.connections.contains(this.connection)) {
                return;
            }
            String portFilter = ppbe.getFilterString();
            if (portFilter == null) {
                return;
            }
            actualBe.routingTable.receivePortInfo(portFilter, this.connection == null ? BlockPos.ZERO : this.connection);
            Map<BlockPos, ChainConveyorBlockEntity.ConnectedPort> portMap = this.connection == null ? actualBe.loopPorts : actualBe.travelPorts;
            portMap.put(this.relativePos.multiply(-1), new ChainConveyorBlockEntity.ConnectedPort(this.chainPos, this.connection, portFilter));
        }

        @Override
        public void deregister(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
            BlockEntity blockEntity = this.be(level, portPos);
            if (!(blockEntity instanceof ChainConveyorBlockEntity)) {
                return;
            }
            ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
            clbe.loopPorts.remove(this.relativePos.multiply(-1));
            clbe.travelPorts.remove(this.relativePos.multiply(-1));
            String portFilter = ppbe.getFilterString();
            if (portFilter == null) {
                return;
            }
            clbe.routingTable.entriesByDistance.removeIf(e -> e.endOfRoute() && e.port().equals(portFilter));
            clbe.routingTable.changed = true;
        }

        @Override
        public Vec3 getExactTargetLocation(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {
            BlockEntity blockEntity = this.be(level, portPos);
            if (!(blockEntity instanceof ChainConveyorBlockEntity)) {
                return Vec3.ZERO;
            }
            ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
            return clbe.getPackagePosition(this.chainPos, this.connection);
        }

        @Override
        public boolean canSupport(BlockEntity be) {
            return AllBlockEntityTypes.PACKAGE_FROGPORT.is(be);
        }

        @Override
        protected PackagePortTargetType getType() {
            return (PackagePortTargetType)AllPackagePortTargetTypes.CHAIN_CONVEYOR.value();
        }

        public static class Type
        implements PackagePortTargetType {
            public MapCodec<ChainConveyorFrogportTarget> codec() {
                return CODEC;
            }

            public StreamCodec<ByteBuf, ChainConveyorFrogportTarget> streamCodec() {
                return STREAM_CODEC;
            }
        }
    }
}
