/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption.storage.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.menu.MountedStorageMenus;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public abstract class MountedItemStorage
implements IItemHandlerModifiable {
    public static final Codec<MountedItemStorage> CODEC = MountedItemStorageType.CODEC.dispatch(storage -> storage.type, type -> type.codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, MountedItemStorage> STREAM_CODEC = StreamCodec.of((b, t) -> b.writeWithCodec((DynamicOps)RegistryOps.create((DynamicOps)NbtOps.INSTANCE, (HolderLookup.Provider)b.registryAccess()), CODEC, t), b -> (MountedItemStorage)b.readWithCodecTrusted((DynamicOps)RegistryOps.create((DynamicOps)NbtOps.INSTANCE, (HolderLookup.Provider)b.registryAccess()), CODEC));
    public final MountedItemStorageType<? extends MountedItemStorage> type;

    protected MountedItemStorage(MountedItemStorageType<?> type) {
        this.type = Objects.requireNonNull(type);
    }

    public abstract void unmount(Level var1, BlockState var2, BlockPos var3, @Nullable BlockEntity var4);

    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureTemplate.StructureBlockInfo info) {
        Consumer<Player> onClose;
        IItemHandlerModifiable handler;
        ServerLevel level = player.serverLevel();
        BlockPos localPos = info.pos();
        Vec3 localPosVec = Vec3.atCenterOf((Vec3i)localPos);
        Predicate<Player> stillValid = p -> {
            Vec3 currentPos = contraption.entity.toGlobalVector(localPosVec, 0.0f);
            return this.isMenuValid(player, contraption, currentPos);
        };
        Component menuName = this.getMenuName(info, contraption);
        OptionalInt id = player.openMenu(this.createMenuProvider(menuName, handler = this.getHandlerForMenu(info, contraption), stillValid, onClose = p -> {
            Vec3 newPos = contraption.entity.toGlobalVector(localPosVec, 0.0f);
            this.playClosingSound(level, newPos);
        }));
        if (id.isPresent()) {
            Vec3 globalPos = contraption.entity.toGlobalVector(localPosVec, 0.0f);
            this.playOpeningSound(level, globalPos);
            return true;
        }
        return false;
    }

    protected IItemHandlerModifiable getHandlerForMenu(StructureTemplate.StructureBlockInfo info, Contraption contraption) {
        return this;
    }

    protected boolean isMenuValid(ServerPlayer player, Contraption contraption, Vec3 pos) {
        return contraption.entity.isAlive() && player.distanceToSqr(pos) < 64.0;
    }

    protected Component getMenuName(StructureTemplate.StructureBlockInfo info, Contraption contraption) {
        MutableComponent blockName = info.state().getBlock().getName();
        return CreateLang.translateDirect("contraptions.moving_container", blockName);
    }

    @Nullable
    protected MenuProvider createMenuProvider(Component name, IItemHandlerModifiable handler, Predicate<Player> stillValid, Consumer<Player> onClose) {
        return MountedStorageMenus.createGeneric(name, handler, stillValid, onClose);
    }

    protected void playOpeningSound(ServerLevel level, Vec3 pos) {
        level.playSound(null, BlockPos.containing((Position)pos), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.75f, 1.0f);
    }

    protected void playClosingSound(ServerLevel level, Vec3 pos) {
    }
}
