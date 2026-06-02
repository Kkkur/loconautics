/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.zapper;

import com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class ShootGadgetPacket
implements ClientboundPacketPayload {
    protected final Vec3 location;
    protected final InteractionHand hand;
    protected final boolean self;

    public ShootGadgetPacket(Vec3 location, InteractionHand hand, boolean self) {
        this.location = location;
        this.hand = hand;
        this.self = self;
    }

    @OnlyIn(value=Dist.CLIENT)
    protected abstract void handleAdditional();

    @OnlyIn(value=Dist.CLIENT)
    protected abstract ShootableGadgetRenderHandler getHandler();

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Entity renderViewEntity = Minecraft.getInstance().getCameraEntity();
        if (renderViewEntity == null) {
            return;
        }
        if (renderViewEntity.position().distanceTo(this.location) > 100.0) {
            return;
        }
        ShootableGadgetRenderHandler handler = this.getHandler();
        this.handleAdditional();
        if (this.self) {
            handler.shoot(this.hand, this.location);
        } else {
            handler.playSound(this.hand, this.location);
        }
    }
}
