/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.player.Player
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.kinetics.base.RotationIndicatorParticle;
import com.simibubi.create.content.kinetics.base.RotationIndicatorParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public static class RotationIndicatorParticle.Factory
implements ParticleProvider<RotationIndicatorParticleData> {
    private final SpriteSet spriteSet;

    public RotationIndicatorParticle.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    public Particle createParticle(RotationIndicatorParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        boolean visible = worldIn != mc.level || player != null && GogglesItem.isWearingGoggles((Player)player);
        return new RotationIndicatorParticle(worldIn, x, y, z, data.color, data.radius1, data.radius2, data.speed, data.getAxis(), data.lifeSpan, visible, this.spriteSet);
    }
}
