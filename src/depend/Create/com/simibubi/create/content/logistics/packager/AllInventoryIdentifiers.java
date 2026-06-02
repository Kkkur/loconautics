/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.WorldlyContainer
 *  net.minecraft.world.WorldlyContainerHolder
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.ChestBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.ChestType
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.logistics.packager;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;

public class AllInventoryIdentifiers {
    public static void registerDefaults() {
        InventoryIdentifier.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(AllTags.AllBlockTags.SINGLE_BLOCK_INVENTORIES.tag, AllInventoryIdentifiers::single));
        InventoryIdentifier.REGISTRY.registerProvider(block -> {
            Collection properties = block.getStateDefinition().getProperties();
            if (properties.contains(ChestBlock.TYPE) && properties.contains(ChestBlock.FACING)) {
                return AllInventoryIdentifiers::chest;
            }
            return null;
        });
        InventoryIdentifier.REGISTRY.registerProvider(block -> {
            if (block instanceof WorldlyContainerHolder) {
                return AllInventoryIdentifiers::worldlyContainerBlock;
            }
            return null;
        });
        InventoryIdentifier.REGISTRY.register((Block)AllBlocks.ITEM_VAULT.get(), (level, state, face) -> {
            InventoryIdentifier inventoryIdentifier;
            BlockEntity be = level.getBlockEntity(face.getPos());
            if (be instanceof ItemVaultBlockEntity) {
                ItemVaultBlockEntity vault = (ItemVaultBlockEntity)be;
                inventoryIdentifier = vault.getInvId();
            } else {
                inventoryIdentifier = null;
            }
            return inventoryIdentifier;
        });
    }

    private static InventoryIdentifier single(Level level, BlockState state, BlockFace face) {
        return new InventoryIdentifier.Single(face.getPos());
    }

    private static InventoryIdentifier chest(Level level, BlockState state, BlockFace face) {
        ChestType type = (ChestType)state.getValue((Property)ChestBlock.TYPE);
        if (type != ChestType.SINGLE) {
            Direction toOther = ChestBlock.getConnectedDirection((BlockState)state);
            BlockPos otherPos = face.getPos().relative(toOther);
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(state.getBlock()) && ChestBlock.getConnectedDirection((BlockState)otherState) == toOther.getOpposite()) {
                return new InventoryIdentifier.Pair(face.getPos(), otherPos);
            }
        }
        return new InventoryIdentifier.Single(face.getPos());
    }

    private static InventoryIdentifier worldlyContainerBlock(Level level, BlockState state, BlockFace face) {
        WorldlyContainerHolder holder = (WorldlyContainerHolder)state.getBlock();
        WorldlyContainer container = holder.getContainer(state, (LevelAccessor)level, face.getPos());
        return AllInventoryIdentifiers.ofWorldlyContainer(container, face);
    }

    private static InventoryIdentifier ofWorldlyContainer(WorldlyContainer container, BlockFace face) {
        Direction side = face.getFace();
        int[] slots = container.getSlotsForFace(side);
        EnumSet<Direction> directions = EnumSet.of(side);
        for (Direction direction : Iterate.directions) {
            int[] faceSlots;
            if (direction == side || !Arrays.equals(slots, faceSlots = container.getSlotsForFace(direction))) continue;
            directions.add(direction);
        }
        return new InventoryIdentifier.MultiFace(face.getPos(), directions);
    }

    public static InventoryIdentifier fallback(Level level, BlockState state, BlockFace face) {
        BlockEntity be = level.getBlockEntity(face.getPos());
        if (be instanceof WorldlyContainer) {
            WorldlyContainer container = (WorldlyContainer)be;
            return AllInventoryIdentifiers.ofWorldlyContainer(container, face);
        }
        return null;
    }
}
