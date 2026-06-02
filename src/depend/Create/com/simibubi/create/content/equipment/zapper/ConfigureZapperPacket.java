/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.zapper;

import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.ZapperItem;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public abstract class ConfigureZapperPacket
implements ServerboundPacketPayload {
    protected final InteractionHand hand;
    protected final PlacementPatterns pattern;

    public ConfigureZapperPacket(InteractionHand hand, PlacementPatterns pattern) {
        this.hand = hand;
        this.pattern = pattern;
    }

    public void handle(ServerPlayer player) {
        ItemStack stack = player.getItemInHand(this.hand);
        if (stack.getItem() instanceof ZapperItem) {
            this.configureZapper(stack);
        }
    }

    public abstract void configureZapper(ItemStack var1);
}
