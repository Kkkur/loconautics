/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.AttackEntityEvent
 */
package com.simibubi.create.content.logistics.box;

import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.foundation.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber(value={Dist.CLIENT})
public class PackageClientInteractionHandler {
    @SubscribeEvent
    @OnlyIn(value=Dist.CLIENT)
    public static void onPlayerPunchPackage(AttackEntityEvent event) {
        Player attacker = event.getEntity();
        if (!attacker.level().isClientSide()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (attacker != mc.player) {
            return;
        }
        if (!(event.getTarget() instanceof PackageEntity)) {
            return;
        }
        ((MinecraftAccessor)mc).create$setMissTime(10);
    }
}
