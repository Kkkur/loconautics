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
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.particle;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.particle.AirParticleData;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.phys.Vec3;

public class AirParticle
extends SimpleAnimatedParticle {
    private float originX;
    private float originY;
    private float originZ;
    private float targetX;
    private float targetY;
    private float targetZ;
    private float drag;
    private float twirlRadius;
    private float twirlAngleOffset;
    private Direction.Axis twirlAxis;

    protected AirParticle(ClientLevel world, AirParticleData data, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprite) {
        super(world, x, y, z, sprite, world.random.nextFloat() * 0.5f);
        this.quadSize *= 0.75f;
        this.hasPhysics = false;
        this.setPos(x, y, z);
        this.xo = x;
        this.originX = (float)this.xo;
        this.yo = y;
        this.originY = (float)this.yo;
        this.zo = z;
        this.originZ = (float)this.zo;
        this.targetX = (float)(x + dx);
        this.targetY = (float)(y + dy);
        this.targetZ = (float)(z + dz);
        this.drag = data.drag;
        this.twirlRadius = Create.RANDOM.nextFloat() / 6.0f;
        this.twirlAngleOffset = Create.RANDOM.nextFloat() * 360.0f;
        this.twirlAxis = Create.RANDOM.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
        double length = new Vec3(dx, dy, dz).length();
        this.lifetime = Math.min((int)(length / (double)data.speed), 60);
        this.selectSprite(7);
        this.setAlpha(0.25f);
        if (length == 0.0) {
            this.remove();
            this.setAlpha(0.0f);
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        float progress = (float)Math.pow((float)this.age / (float)this.lifetime, this.drag);
        float angle = (progress * 2.0f * 360.0f + this.twirlAngleOffset) % 360.0f;
        Vec3 twirl = VecHelper.rotate((Vec3)new Vec3(0.0, (double)this.twirlRadius, 0.0), (double)angle, (Direction.Axis)this.twirlAxis);
        float x = (float)((double)Mth.lerp((float)progress, (float)this.originX, (float)this.targetX) + twirl.x);
        float y = (float)((double)Mth.lerp((float)progress, (float)this.originY, (float)this.targetY) + twirl.y);
        float z = (float)((double)Mth.lerp((float)progress, (float)this.originZ, (float)this.targetZ) + twirl.z);
        this.xd = (double)x - this.x;
        this.yd = (double)y - this.y;
        this.zd = (double)z - this.z;
        this.setSpriteFromAge(this.sprites);
        this.move(this.xd, this.yd, this.zd);
    }

    public int getLightColor(float partialTick) {
        BlockPos blockpos = BlockPos.containing((double)this.x, (double)this.y, (double)this.z);
        return this.level.isLoaded(blockpos) ? LevelRenderer.getLightColor((BlockAndTintGetter)this.level, (BlockPos)blockpos) : 0;
    }

    private void selectSprite(int index) {
        this.setSprite(this.sprites.get(index, 8));
    }

    public static class Factory
    implements ParticleProvider<AirParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        public Particle createParticle(AirParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new AirParticle(worldIn, data, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
