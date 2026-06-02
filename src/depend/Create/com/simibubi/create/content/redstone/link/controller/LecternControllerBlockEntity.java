/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.ItemContainerContents
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.link.controller;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlock;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class LecternControllerBlockEntity
extends SmartBlockEntity {
    private ItemContainerContents controllerData = ItemContainerContents.EMPTY;
    private UUID user;
    private UUID prevUser;
    private boolean deactivatedThisTick;

    public LecternControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("ControllerData", (Tag)CatnipCodecUtils.encode((Codec)ItemContainerContents.CODEC, (HolderLookup.Provider)registries, (Object)this.controllerData).orElseThrow());
        if (this.user != null) {
            compound.putUUID("User", this.user);
        }
    }

    @Override
    public void writeSafe(CompoundTag compound, HolderLookup.Provider registries) {
        super.writeSafe(compound, registries);
        compound.put("ControllerData", (Tag)CatnipCodecUtils.encode((Codec)ItemContainerContents.CODEC, (HolderLookup.Provider)registries, (Object)this.controllerData).orElseThrow());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.controllerData = CatnipCodecUtils.decode((Codec)ItemContainerContents.CODEC, (HolderLookup.Provider)registries, (Tag)compound.get("ControllerData")).orElse(ItemContainerContents.EMPTY);
        this.user = compound.hasUUID("User") ? compound.getUUID("User") : null;
    }

    public ItemStack getController() {
        return this.createLinkedController();
    }

    public boolean hasUser() {
        return this.user != null;
    }

    public boolean isUsedBy(Player player) {
        return this.hasUser() && this.user.equals(player.getUUID());
    }

    public void tryStartUsing(Player player) {
        if (!this.deactivatedThisTick && !this.hasUser() && !LecternControllerBlockEntity.playerIsUsingLectern(player) && LecternControllerBlockEntity.playerInRange(player, this.level, this.worldPosition)) {
            this.startUsing(player);
        }
    }

    public void tryStopUsing(Player player) {
        if (this.isUsedBy(player)) {
            this.stopUsing(player);
        }
    }

    private void startUsing(Player player) {
        this.user = player.getUUID();
        player.getPersistentData().putBoolean("IsUsingLecternController", true);
        this.sendData();
    }

    private void stopUsing(Player player) {
        this.user = null;
        if (player != null) {
            player.getPersistentData().remove("IsUsingLecternController");
        }
        this.deactivatedThisTick = true;
        this.sendData();
    }

    public static boolean playerIsUsingLectern(Player player) {
        return player.getPersistentData().contains("IsUsingLecternController");
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> this::tryToggleActive);
            this.prevUser = this.user;
        }
        if (!this.level.isClientSide) {
            this.deactivatedThisTick = false;
            if (!(this.level instanceof ServerLevel)) {
                return;
            }
            if (this.user == null) {
                return;
            }
            Entity entity = ((ServerLevel)this.level).getEntity(this.user);
            if (!(entity instanceof Player)) {
                this.stopUsing(null);
                return;
            }
            Player player = (Player)entity;
            if (!LecternControllerBlockEntity.playerInRange(player, this.level, this.worldPosition) || !LecternControllerBlockEntity.playerIsUsingLectern(player)) {
                this.stopUsing(player);
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private void tryToggleActive() {
        if (this.user == null && Minecraft.getInstance().player.getUUID().equals(this.prevUser)) {
            LinkedControllerClientHandler.deactivateInLectern();
        } else if (this.prevUser == null && Minecraft.getInstance().player.getUUID().equals(this.user)) {
            LinkedControllerClientHandler.activateInLectern(this.worldPosition);
        }
    }

    public void setController(ItemStack newController) {
        if (newController != null) {
            this.controllerData = (ItemContainerContents)newController.getOrDefault(AllDataComponents.LINKED_CONTROLLER_ITEMS, (Object)ItemContainerContents.EMPTY);
            AllSoundEvents.CONTROLLER_PUT.playOnServer(this.level, (Vec3i)this.worldPosition);
        }
    }

    public void swapControllers(ItemStack stack, Player player, InteractionHand hand, BlockState state) {
        ItemStack newController = stack.copy();
        stack.setCount(0);
        if (player.getItemInHand(hand).isEmpty()) {
            player.setItemInHand(hand, this.createLinkedController());
        } else {
            this.dropController(state);
        }
        this.setController(newController);
    }

    public void dropController(BlockState state) {
        Entity entity = ((ServerLevel)this.level).getEntity(this.user);
        if (entity instanceof Player) {
            Player player = (Player)entity;
            this.stopUsing(player);
        }
        Direction dir = (Direction)state.getValue((Property)LecternControllerBlock.FACING);
        double x = (double)this.worldPosition.getX() + 0.5 + 0.25 * (double)dir.getStepX();
        double y = this.worldPosition.getY() + 1;
        double z = (double)this.worldPosition.getZ() + 0.5 + 0.25 * (double)dir.getStepZ();
        ItemEntity itementity = new ItemEntity(this.level, x, y, z, this.createLinkedController());
        itementity.setDefaultPickUpDelay();
        this.level.addFreshEntity((Entity)itementity);
        this.controllerData = ItemContainerContents.EMPTY;
    }

    public static boolean playerInRange(Player player, Level world, BlockPos pos) {
        double reach = 0.4 * player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        return player.getEyePosition().distanceToSqr(Vec3.atCenterOf((Vec3i)pos)) < reach * reach;
    }

    private ItemStack createLinkedController() {
        ItemStack stack = AllItems.LINKED_CONTROLLER.asStack();
        stack.set(AllDataComponents.LINKED_CONTROLLER_ITEMS, (Object)this.controllerData);
        return stack;
    }
}
