/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllTags$AllBlockTags
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.SimpleAnimatedParticle
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.particle;

import com.simibubi.create.AllTags;
import dev.eriksonn.aeronautics.content.particle.PropellerAirParticleData;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PropellerAirParticle
extends SimpleAnimatedParticle {
    public static final double frictionScale = 0.2;
    public static final int lifeTime = 20;
    Vec3 motion;
    private boolean stoppedByCollision;
    boolean isVirtual;

    protected PropellerAirParticle(ClientLevel world, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprite, boolean enableCollision, boolean isVirtual) {
        super(world, x, y, z, sprite, world.random.nextFloat() * 0.5f);
        this.quadSize *= 0.75f;
        this.lifetime = 20;
        this.bbHeight = 0.01f;
        this.bbWidth = 0.01f;
        this.hasPhysics = enableCollision;
        this.isVirtual = isVirtual;
        this.selectSprite(0);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.motion = new Vec3(dx, dy, dz);
        this.setPos(x + dx, y + dy, z + dz);
        this.setAlpha(0.25f);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    private void dissipate() {
        this.remove();
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.selectSprite((int)Mth.clamp((float)((float)this.age / (float)this.lifetime * 8.0f), (float)0.0f, (float)7.0f));
            this.xd = this.motion.x;
            this.yd = this.motion.y;
            this.zd = this.motion.z;
            double friction = 0.2 * this.motion.length();
            friction = Math.min(friction, 0.5);
            this.motion = this.motion.scale(1.0 - friction);
            this.move(this.xd, this.yd, this.zd);
        }
    }

    public void move(double pX, double pY, double pZ) {
        if (!this.stoppedByCollision) {
            double d0 = pX;
            double d1 = pY;
            double d2 = pZ;
            if (this.isVirtual && this.hasPhysics && !this.level.getBlockState(new BlockPos((int)Math.floor(this.x + pX), (int)Math.floor(this.y + pY), (int)Math.floor(this.z + pZ))).isAir()) {
                this.stoppedByCollision = true;
            }
            if (this.hasPhysics && (pX != 0.0 || pY != 0.0 || pZ != 0.0)) {
                if (!this.level.getBlockState(new BlockPos((int)Math.floor(this.x + pX), (int)Math.floor(this.y + pY), (int)Math.floor(this.z + pZ))).is(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)) {
                    Vec3 vec3 = Entity.collideBoundingBox(null, (Vec3)new Vec3(pX, pY, pZ), (AABB)this.getBoundingBox(), (Level)this.level, List.of());
                    pX = vec3.x;
                    pY = vec3.y;
                    pZ = vec3.z;
                } else {
                    d0 = pX;
                }
            }
            if (pX != 0.0 || pY != 0.0 || pZ != 0.0) {
                this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
                this.setLocationFromBoundingbox();
            }
            if (Math.abs(d1) >= (double)1.0E-5f && Math.abs(pY) < (double)1.0E-5f) {
                this.stoppedByCollision = true;
            }
            boolean bl = this.onGround = d1 != pY && d1 < 0.0;
            if (d0 != pX) {
                this.xd = 0.0;
            }
            if (d2 != pZ) {
                this.zd = 0.0;
            }
        }
    }

    public int getLightColor(float partialTick) {
        BlockPos blockpos = new BlockPos((int)this.x, (int)this.y, (int)this.z);
        return this.level.isLoaded(blockpos) ? LevelRenderer.getLightColor((BlockAndTintGetter)this.level, (BlockPos)blockpos) : 0;
    }

    private void selectSprite(int index) {
        this.setSprite(this.sprites.get(index, 8));
    }

    public static class Factory
    implements ParticleProvider<PropellerAirParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        public Particle createParticle(PropellerAirParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new PropellerAirParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet, data.enableCollision, data.isVirtual);
        }
    }
}
