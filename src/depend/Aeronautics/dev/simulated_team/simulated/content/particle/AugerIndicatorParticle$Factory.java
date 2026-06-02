/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.goggles.GogglesItem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.player.Player
 */
package dev.simulated_team.simulated.content.particle;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import dev.simulated_team.simulated.content.particle.AugerIndicatorParticle;
import dev.simulated_team.simulated.content.particle.AugerIndicatorParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public static class AugerIndicatorParticle.Factory
implements ParticleProvider<AugerIndicatorParticleData> {
    private final SpriteSet spriteSet;

    public AugerIndicatorParticle.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    public Particle createParticle(AugerIndicatorParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        boolean visible = worldIn != mc.level || player != null && GogglesItem.isWearingGoggles((Player)player);
        return new AugerIndicatorParticle(worldIn, x, y, z, data.color, data.radius1, data.radius2, data.angleOffset, data.speed, data.direction, data.lifeSpan, visible, this.spriteSet);
    }
}
