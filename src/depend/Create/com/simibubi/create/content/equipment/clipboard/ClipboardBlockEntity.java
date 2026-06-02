/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DynamicOps
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.clipboard;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlock;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardScreen;
import com.simibubi.create.content.logistics.AddressEditBoxHelper;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ClipboardBlockEntity
extends SmartBlockEntity {
    private UUID lastEdit;

    public ClipboardBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.updateWrittenState();
    }

    public void onEditedBy(Player player) {
        this.lastEdit = player.getUUID();
        this.notifyUpdate();
        this.updateWrittenState();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.level.isClientSide()) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> this::advertiseToAddressHelper);
        }
    }

    public void updateWrittenState() {
        boolean shouldBeWritten;
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.CLIPBOARD.has(blockState)) {
            return;
        }
        if (this.level.isClientSide()) {
            return;
        }
        boolean isWritten = (Boolean)blockState.getValue((Property)ClipboardBlock.WRITTEN);
        if (isWritten == (shouldBeWritten = this.components().has(AllDataComponents.CLIPBOARD_CONTENT))) {
            return;
        }
        this.level.setBlockAndUpdate(this.worldPosition, (BlockState)blockState.setValue((Property)ClipboardBlock.WRITTEN, (Comparable)Boolean.valueOf(shouldBeWritten)));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (clientPacket) {
            DataComponentMap.CODEC.encodeStart((DynamicOps)registries.createSerializationContext((DynamicOps)NbtOps.INSTANCE), (Object)this.components()).result().ifPresent(encoded -> tag.put("components", encoded));
            if (this.lastEdit != null) {
                tag.putUUID("LastEdit", this.lastEdit);
            }
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (clientPacket) {
            if (tag.contains("components")) {
                DataComponentMap.CODEC.decode((DynamicOps)registries.createSerializationContext((DynamicOps)NbtOps.INSTANCE), (Object)tag.getCompound("components")).result().map(Pair::getFirst).ifPresent(this::setComponents);
            }
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.readClientSide(tag));
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private void readClientSide(CompoundTag tag) {
        Minecraft mc = Minecraft.getInstance();
        Screen screen = mc.screen;
        if (!(screen instanceof ClipboardScreen)) {
            return;
        }
        ClipboardScreen cs = (ClipboardScreen)screen;
        if (tag.contains("LastEdit") && tag.getUUID("LastEdit").equals(mc.player.getUUID())) {
            return;
        }
        if (!this.worldPosition.equals((Object)cs.targetedBlock)) {
            return;
        }
        cs.reopenWith((ClipboardContent)this.components().getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, (Object)ClipboardContent.EMPTY));
    }

    @OnlyIn(value=Dist.CLIENT)
    private void advertiseToAddressHelper() {
        AddressEditBoxHelper.advertiseClipboard(this);
    }

    public void setComponents(DataComponentMap components) {
        super.setComponents(components);
    }
}
