/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.packagePort;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.packagePort.AllPackagePortTargetTypes;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetType;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public static class PackagePortTarget.TrainStationFrogportTarget
extends PackagePortTarget {
    public static MapCodec<PackagePortTarget.TrainStationFrogportTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockPos.CODEC.fieldOf("relative_pos").forGetter(i -> i.relativePos)).apply((Applicative)instance, PackagePortTarget.TrainStationFrogportTarget::new));
    public static final StreamCodec<ByteBuf, PackagePortTarget.TrainStationFrogportTarget> STREAM_CODEC = BlockPos.STREAM_CODEC.map(PackagePortTarget.TrainStationFrogportTarget::new, i -> i.relativePos);

    public PackagePortTarget.TrainStationFrogportTarget(BlockPos relativePos) {
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
        public MapCodec<PackagePortTarget.TrainStationFrogportTarget> codec() {
            return CODEC;
        }

        public StreamCodec<ByteBuf, PackagePortTarget.TrainStationFrogportTarget> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
