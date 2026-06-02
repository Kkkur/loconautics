/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption.storage.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public abstract class MountedFluidStorage
implements IFluidHandler {
    public static final Codec<MountedFluidStorage> CODEC = MountedFluidStorageType.CODEC.dispatch(storage -> storage.type, type -> type.codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, MountedFluidStorage> STREAM_CODEC = StreamCodec.of((b, t) -> b.writeWithCodec((DynamicOps)RegistryOps.create((DynamicOps)NbtOps.INSTANCE, (HolderLookup.Provider)b.registryAccess()), CODEC, t), b -> (MountedFluidStorage)b.readWithCodecTrusted((DynamicOps)RegistryOps.create((DynamicOps)NbtOps.INSTANCE, (HolderLookup.Provider)b.registryAccess()), CODEC));
    public final MountedFluidStorageType<? extends MountedFluidStorage> type;

    protected MountedFluidStorage(MountedFluidStorageType<?> type) {
        this.type = Objects.requireNonNull(type);
    }

    public abstract void unmount(Level var1, BlockState var2, BlockPos var3, @Nullable BlockEntity var4);
}
