/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.contraptions.minecart;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.minecart.CouplingCreationPacket;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class CouplingHandlerClient {
    static AbstractMinecart selectedCart;
    static RandomSource r;

    public static void tick() {
        if (selectedCart == null) {
            return;
        }
        CouplingHandlerClient.spawnSelectionParticles(selectedCart.getBoundingBox(), false);
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack heldItemMainhand = player.getMainHandItem();
        ItemStack heldItemOffhand = player.getOffhandItem();
        if (AllItems.MINECART_COUPLING.isIn(heldItemMainhand) || AllItems.MINECART_COUPLING.isIn(heldItemOffhand)) {
            return;
        }
        selectedCart = null;
    }

    static void onCartClicked(Player player, AbstractMinecart entity) {
        if (Minecraft.getInstance().player != player) {
            return;
        }
        if (selectedCart == null || selectedCart == entity) {
            selectedCart = entity;
            CouplingHandlerClient.spawnSelectionParticles(selectedCart.getBoundingBox(), true);
            return;
        }
        CouplingHandlerClient.spawnSelectionParticles(entity.getBoundingBox(), true);
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new CouplingCreationPacket(selectedCart, entity));
        selectedCart = null;
    }

    static void sneakClick() {
        selectedCart = null;
    }

    private static void spawnSelectionParticles(AABB AABB2, boolean highlight) {
        ClientLevel world = Minecraft.getInstance().level;
        Vec3 center = AABB2.getCenter();
        int amount = highlight ? 100 : 2;
        SimpleParticleType particleData = highlight ? ParticleTypes.END_ROD : new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
        for (int i = 0; i < amount; ++i) {
            Vec3 v = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)r, (float)1.0f);
            double yOffset = v.y;
            v = v.multiply(1.0, 0.0, 1.0).normalize().add(0.0, yOffset / 8.0, 0.0).add(center);
            world.addParticle((ParticleOptions)particleData, v.x, v.y, v.z, 0.0, 0.0, 0.0);
        }
    }

    static {
        r = RandomSource.create();
    }
}
