/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.SimpleAnimatedParticle
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AirFlowParticle
extends SimpleAnimatedParticle {
    private final IAirCurrentSource source;
    private final Access access = new Access();

    protected AirFlowParticle(ClientLevel world, IAirCurrentSource source, double x, double y, double z, SpriteSet sprite) {
        super(world, x, y, z, sprite, world.random.nextFloat() * 0.5f);
        this.source = source;
        this.quadSize *= 0.75f;
        this.lifetime = 40;
        this.hasPhysics = false;
        this.selectSprite(7);
        Vec3 offset = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)this.random, (float)0.25f);
        this.setPos(x + offset.x, y + offset.y, z + offset.z);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.setColor(0xEEEEEE);
        this.setAlpha(0.25f);
    }

    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        if (this.source == null || this.source.isSourceRemoved()) {
            this.remove();
            return;
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            double distance;
            AirCurrent airCurrent = this.source.getAirCurrent();
            if (airCurrent == null || !airCurrent.bounds.inflate(0.25).contains(this.x, this.y, this.z)) {
                this.remove();
                return;
            }
            Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)airCurrent.direction.getNormal());
            Vec3 motion = directionVec.scale(0.125);
            if (!this.source.getAirCurrent().pushing) {
                motion = motion.scale(-1.0);
            }
            if ((distance = new Vec3(this.x, this.y, this.z).subtract(VecHelper.getCenterOf((Vec3i)this.source.getAirCurrentPos())).multiply(directionVec).length() - 0.5) > (double)(airCurrent.maxDistance + 1.0f) || distance < -0.25) {
                this.remove();
                return;
            }
            motion = motion.scale((double)airCurrent.maxDistance - (distance - 1.0)).scale(0.5);
            FanProcessingType type = this.getType(distance);
            if (type == null) {
                this.setColor(0xEEEEEE);
                this.setAlpha(0.25f);
                this.selectSprite((int)Mth.clamp((double)(distance / (double)airCurrent.maxDistance * 8.0 + (double)this.random.nextInt(4)), (double)0.0, (double)7.0));
            } else {
                type.morphAirFlow(this.access, this.random);
                this.selectSprite(this.random.nextInt(3));
            }
            this.xd = motion.x;
            this.yd = motion.y;
            this.zd = motion.z;
            if (this.onGround) {
                this.xd *= 0.7;
                this.zd *= 0.7;
            }
            this.move(this.xd, this.yd, this.zd);
        }
    }

    @Nullable
    private FanProcessingType getType(double distance) {
        if (this.source.getAirCurrent() == null) {
            return null;
        }
        return this.source.getAirCurrent().getTypeAt((float)distance);
    }

    public int getLightColor(float partialTick) {
        BlockPos blockpos = BlockPos.containing((double)this.x, (double)this.y, (double)this.z);
        return this.level.isLoaded(blockpos) ? LevelRenderer.getLightColor((BlockAndTintGetter)this.level, (BlockPos)blockpos) : 0;
    }

    private void selectSprite(int index) {
        this.setSprite(this.sprites.get(index, 8));
    }

    private class Access
    implements FanProcessingType.AirFlowParticleAccess {
        private Access() {
        }

        @Override
        public void setColor(int color) {
            AirFlowParticle.this.setColor(color);
        }

        @Override
        public void setAlpha(float alpha) {
            AirFlowParticle.this.setAlpha(alpha);
        }

        @Override
        public void spawnExtraParticle(ParticleOptions options, float speedMultiplier) {
            AirFlowParticle.this.level.addParticle(options, AirFlowParticle.this.x, AirFlowParticle.this.y, AirFlowParticle.this.z, AirFlowParticle.this.xd * (double)speedMultiplier, AirFlowParticle.this.yd * (double)speedMultiplier, AirFlowParticle.this.zd * (double)speedMultiplier);
        }
    }

    public static class Factory
    implements ParticleProvider<AirFlowParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
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
}
