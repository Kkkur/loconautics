/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public static class AirFlowParticle.Factory
implements ParticleProvider<AirFlowParticleData> {
    private final SpriteSet spriteSet;

    public AirFlowParticle.Factory(SpriteSet animatedSprite) {
        this.spriteSet = animatedSprite;
    }

    public Particle createParticle(AirFlowParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        BlockEntity be = worldIn.getBlockEntity(new BlockPos(data.posX, data.posY, data.posZ));
        if (!(be instanceof IAirCurrentSource)) {
            be = null;
        }
        return new AirFlowParticle(worldIn, (IAirCurrentSource)be, x, y, z, this.spriteSet);
    }
}
