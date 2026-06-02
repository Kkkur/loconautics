/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.minecart.capability;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

private static class MinecartController.Empty
extends MinecartController {
    private MinecartController.Empty() {
        super(null);
    }

    public MinecartController.Empty(AbstractMinecart minecart) {
        super(minecart);
    }

    @Override
    @NotNull
    protected MinecartController.Type getType() {
        return MinecartController.Type.EMPTY;
    }

    private static void warn() {
        Create.LOGGER.warn("Method called on EMPTY MinecartController", (Throwable)new Exception());
    }

    @Override
    public void tick() {
        MinecartController.Empty.warn();
    }

    @Override
    public boolean isFullyCoupled() {
        MinecartController.Empty.warn();
        return false;
    }

    @Override
    public boolean isLeadingCoupling() {
        MinecartController.Empty.warn();
        return false;
    }

    @Override
    public boolean isConnectedToCoupling() {
        MinecartController.Empty.warn();
        return false;
    }

    @Override
    public boolean isCoupledThroughContraption() {
        MinecartController.Empty.warn();
        return false;
    }

    @Override
    public boolean hasContraptionCoupling(boolean current) {
        MinecartController.Empty.warn();
        return false;
    }

    @Override
    public float getCouplingLength(boolean leading) {
        MinecartController.Empty.warn();
        return 0.0f;
    }

    @Override
    public void decouple() {
        MinecartController.Empty.warn();
    }

    @Override
    public void removeConnection(boolean main) {
        MinecartController.Empty.warn();
    }

    @Override
    public void prepareForCoupling(boolean isLeading) {
        MinecartController.Empty.warn();
    }

    @Override
    public void coupleWith(boolean isLeading, UUID coupled, float length, boolean contraption) {
        MinecartController.Empty.warn();
    }

    @Override
    @Nullable
    public UUID getCoupledCart(boolean asMain) {
        MinecartController.Empty.warn();
        return null;
    }

    @Override
    public boolean isStalled() {
        MinecartController.Empty.warn();
        return false;
    }

    @Override
    public void setStalledExternally(boolean stall) {
        MinecartController.Empty.warn();
    }

    @Override
    public void sendData() {
        super.sendData();
    }

    @Override
    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        return super.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
    }

    @Override
    public boolean isPresent() {
        return super.isPresent();
    }

    @Override
    public AbstractMinecart cart() {
        return super.cart();
    }
}
