/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Container
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.ChestBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.ChestType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 *  net.neoforged.neoforge.items.wrapper.InvWrapper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.contraption.storage.item.chest;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class ChestMountedStorage
extends SimpleMountedStorage {
    public static final MapCodec<ChestMountedStorage> CODEC = SimpleMountedStorage.codec(ChestMountedStorage::new);

    protected ChestMountedStorage(MountedItemStorageType<?> type, IItemHandler handler) {
        super(type, handler);
    }

    public ChestMountedStorage(IItemHandler handler) {
        this((MountedItemStorageType)AllMountedStorageTypes.CHEST.get(), handler);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof Container) {
            Container container = (Container)be;
            if (this.getSlots() == container.getContainerSize()) {
                ItemHelper.copyContents((IItemHandler)this, (IItemHandlerModifiable)new InvWrapper(container));
            }
        }
    }

    @Override
    protected IItemHandlerModifiable getHandlerForMenu(StructureTemplate.StructureBlockInfo info, Contraption contraption) {
        BlockState state = info.state();
        ChestType type = (ChestType)state.getValue((Property)ChestBlock.TYPE);
        if (type == ChestType.SINGLE) {
            return this;
        }
        Direction facing = (Direction)state.getValue((Property)ChestBlock.FACING);
        Direction connectedDirection = ChestBlock.getConnectedDirection((BlockState)state);
        BlockPos otherHalfPos = info.pos().relative(connectedDirection);
        MountedItemStorage otherHalf = this.getOtherHalf(contraption, otherHalfPos, state.getBlock(), facing, type);
        if (otherHalf == null) {
            return this;
        }
        if (type == ChestType.RIGHT) {
            return new CombinedInvWrapper(new IItemHandlerModifiable[]{this, otherHalf});
        }
        return new CombinedInvWrapper(new IItemHandlerModifiable[]{otherHalf, this});
    }

    @Nullable
    protected MountedItemStorage getOtherHalf(Contraption contraption, BlockPos localPos, Block block, Direction thisFacing, ChestType thisType) {
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(localPos);
        if (info == null) {
            return null;
        }
        BlockState state = info.state();
        if (!state.is(block)) {
            return null;
        }
        Direction facing = (Direction)state.getValue((Property)ChestBlock.FACING);
        ChestType type = (ChestType)state.getValue((Property)ChestBlock.TYPE);
        return facing == thisFacing && type == thisType.getOpposite() ? (MountedItemStorage)contraption.getStorage().getMountedItems().storages.get((Object)localPos) : null;
    }

    @Override
    protected void playOpeningSound(ServerLevel level, Vec3 pos) {
        level.playSound(null, BlockPos.containing((Position)pos), SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.75f, 1.0f);
    }

    @Override
    protected void playClosingSound(ServerLevel level, Vec3 pos) {
        level.playSound(null, BlockPos.containing((Position)pos), SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.75f, 1.0f);
    }
}
