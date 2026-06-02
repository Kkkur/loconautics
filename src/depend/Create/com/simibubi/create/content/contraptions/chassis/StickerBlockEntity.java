/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  dev.engine_room.flywheel.lib.visualization.VisualizationHelper
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.simibubi.create.content.contraptions.chassis;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.contraptions.chassis.StickerBlock;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class StickerBlockEntity
extends SmartBlockEntity {
    LerpedFloat piston = LerpedFloat.linear();
    boolean update = false;
    public AbstractComputerBehaviour computerBehaviour;

    public StickerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.STICKER.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!this.level.isClientSide) {
            return;
        }
        this.piston.startWithValue(this.isBlockStateExtended() ? 1.0 : 0.0);
    }

    public boolean isBlockStateExtended() {
        BlockState blockState = this.getBlockState();
        boolean extended = AllBlocks.STICKER.has(blockState) && (Boolean)blockState.getValue((Property)StickerBlock.EXTENDED) != false;
        return extended;
    }

    @Override
    public void tick() {
        boolean target;
        super.tick();
        if (!this.level.isClientSide) {
            return;
        }
        this.piston.tickChaser();
        if (this.isAttachedToBlock() && this.piston.getValue(0.0f) != this.piston.getValue() && this.piston.getValue() == 1.0f) {
            SuperGlueItem.spawnParticles(this.level, this.worldPosition, (Direction)this.getBlockState().getValue((Property)StickerBlock.FACING), true);
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.playSound(true));
        }
        if (!this.update) {
            return;
        }
        this.update = false;
        boolean bl = target = this.isBlockStateExtended();
        if (this.isAttachedToBlock() && !target && this.piston.getChaseTarget() == 1.0f) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.playSound(false));
        }
        this.piston.chase((double)target, (double)0.4f, LerpedFloat.Chaser.LINEAR);
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> VisualizationHelper.queueUpdate((BlockEntity)this));
    }

    public boolean isAttachedToBlock() {
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.STICKER.has(blockState)) {
            return false;
        }
        Direction direction = (Direction)blockState.getValue((Property)StickerBlock.FACING);
        return SuperGlueEntity.isValidFace(this.level, this.worldPosition.relative(direction), direction.getOpposite());
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (clientPacket) {
            this.update = true;
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public void playSound(boolean attach) {
        AllSoundEvents.SLIME_ADDED.play(this.level, (Player)Minecraft.getInstance().player, (Vec3i)this.worldPosition, 0.35f, attach ? 0.75f : 0.2f);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }
}
