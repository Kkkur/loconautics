/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.SimpleAnimatedParticle
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.simulated_team.simulated.content.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.simulated_team.simulated.content.particle.MagnetFieldParticleData2;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class MagnetFieldParticle2
extends SimpleAnimatedParticle {
    protected int timeUntilEnd;

    protected MagnetFieldParticle2(ClientLevel world, double x, double y, double z, double prevX, double prevY, double prevZ, double nextX, double nextY, double nextZ, SpriteSet sprite, boolean negative, int timeUntilEnd) {
        super(world, x, y, z, sprite, world.random.nextFloat() * 0.5f);
        this.hasPhysics = false;
        this.lifetime = 5;
        this.xo = prevX;
        this.yo = prevY;
        this.zo = prevZ;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = nextX;
        this.yd = nextY;
        this.zd = nextZ;
        this.timeUntilEnd = timeUntilEnd;
        this.selectSprite(0);
        this.setAlpha(0.4f);
        if (negative) {
            this.setColor(0.7f, 0.7f, 1.0f);
        } else {
            this.setColor(1.0f, 0.7f, 0.7f);
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    private void dissipate() {
        this.remove();
    }

    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Quaternionf quaternionf = new Quaternionf();
        this.getFacingCameraMode().setRotation(quaternionf, renderInfo, partialTicks);
        if (this.roll != 0.0f) {
            quaternionf.rotateZ(Mth.lerp((float)partialTicks, (float)this.oRoll, (float)this.roll));
        }
        Vector3f v = new Vector3f(1.0f, 1.0f, 1.0f);
        float t = (float)(Minecraft.getInstance().level.getGameTime() % 1000L) + partialTicks;
        t = (float)((double)t * 0.2);
        Vector3f v2 = new Vector3f();
        Vec3 vec3 = renderInfo.getPosition();
        float x = (float)(this.x - vec3.x());
        float y = (float)(this.y - vec3.y());
        float z = (float)(this.z - vec3.z());
        float dirX = (float)Mth.lerp((double)partialTicks, (double)this.xo, (double)this.xd);
        float dirY = (float)Mth.lerp((double)partialTicks, (double)this.yo, (double)this.yd);
        float dirZ = (float)Mth.lerp((double)partialTicks, (double)this.zo, (double)this.zd);
        float offsetX = (float)Mth.lerp((double)partialTicks, (double)(-this.xo), (double)this.xd) * 0.5f;
        float offsetY = (float)Mth.lerp((double)partialTicks, (double)(-this.yo), (double)this.yd) * 0.5f;
        float offsetZ = (float)Mth.lerp((double)partialTicks, (double)(-this.zo), (double)this.zd) * 0.5f;
        quaternionf.identity();
        quaternionf.lookAlong((Vector3fc)new Vector3f(dirX, dirY, dirZ), (Vector3fc)new Vector3f(x, y, z)).conjugate();
        quaternionf.rotateX(1.5707964f);
        this.renderRotatedQuad(buffer, quaternionf, x + offsetX, y + offsetY, z + offsetZ, partialTicks);
    }

    public float getQuadSize(float scaleFactor) {
        float x = (float)Mth.lerp((double)scaleFactor, (double)this.xo, (double)this.xd);
        float y = (float)Mth.lerp((double)scaleFactor, (double)this.yo, (double)this.yd);
        float z = (float)Mth.lerp((double)scaleFactor, (double)this.zo, (double)this.zd);
        return (float)Mth.length((double)x, (double)y, (double)z) * 0.5f;
    }

    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.selectSprite(this.age + 1);
    }

    public int getLightColor(float partialTick) {
        BlockPos blockpos = new BlockPos((int)this.x, (int)this.y, (int)this.z);
        return this.level.isLoaded(blockpos) ? LevelRenderer.getLightColor((BlockAndTintGetter)this.level, (BlockPos)blockpos) : 0;
    }

    private void selectSprite(int index) {
        int n = 6;
        int clampedIndex = 2 * index < 6 ? Math.min(index, this.timeUntilEnd) : Math.max(index, 6 - this.timeUntilEnd + 1);
        this.setSprite(this.sprites.get(clampedIndex, 6));
    }

    public static class Factory
    implements ParticleProvider<MagnetFieldParticleData2> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        public Particle createParticle(MagnetFieldParticleData2 data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new MagnetFieldParticle2(level, x, y, z, data.previousOffset.x, data.previousOffset.y, data.previousOffset.z, data.nextOffset.x, data.nextOffset.y, data.nextOffset.z, this.spriteSet, data.isNegative(), data.getTimeUntilEnd());
        }
    }
}
