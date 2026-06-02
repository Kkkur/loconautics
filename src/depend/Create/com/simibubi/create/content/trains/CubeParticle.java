/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.createmod.ponder.enums.PonderSpecialTextures
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.trains;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.content.trains.CubeParticleData;
import net.createmod.ponder.enums.PonderSpecialTextures;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CubeParticle
extends Particle {
    public static final Vec3[] CUBE = new Vec3[]{new Vec3(1.0, 1.0, -1.0), new Vec3(1.0, 1.0, 1.0), new Vec3(-1.0, 1.0, 1.0), new Vec3(-1.0, 1.0, -1.0), new Vec3(-1.0, -1.0, -1.0), new Vec3(-1.0, -1.0, 1.0), new Vec3(1.0, -1.0, 1.0), new Vec3(1.0, -1.0, -1.0), new Vec3(-1.0, -1.0, 1.0), new Vec3(-1.0, 1.0, 1.0), new Vec3(1.0, 1.0, 1.0), new Vec3(1.0, -1.0, 1.0), new Vec3(1.0, -1.0, -1.0), new Vec3(1.0, 1.0, -1.0), new Vec3(-1.0, 1.0, -1.0), new Vec3(-1.0, -1.0, -1.0), new Vec3(-1.0, -1.0, -1.0), new Vec3(-1.0, 1.0, -1.0), new Vec3(-1.0, 1.0, 1.0), new Vec3(-1.0, -1.0, 1.0), new Vec3(1.0, -1.0, 1.0), new Vec3(1.0, 1.0, 1.0), new Vec3(1.0, 1.0, -1.0), new Vec3(1.0, -1.0, -1.0)};
    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType(){

        @NotNull
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            PonderSpecialTextures.BLANK.bind();
            RenderSystem.depthMask((boolean)false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
            BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            return builder;
        }
    };
    protected float scale;
    protected boolean hot;
    private boolean billowing = false;

    public CubeParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.setScale(0.2f);
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setSize(scale * 0.5f, scale * 0.5f);
    }

    public void averageAge(int age) {
        this.lifetime = (int)((double)age + (this.random.nextDouble() * 2.0 - 1.0) * 8.0);
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    public void tick() {
        if (this.hot && this.age > 0) {
            if (this.yo == this.y) {
                this.billowing = true;
                this.stoppedByCollision = false;
                if (this.xd == 0.0 && this.zd == 0.0) {
                    Vec3 diff = Vec3.atLowerCornerOf((Vec3i)BlockPos.containing((double)this.x, (double)this.y, (double)this.z)).add(0.5, 0.5, 0.5).subtract(this.x, this.y, this.z);
                    this.xd = -diff.x * 0.1;
                    this.zd = -diff.z * 0.1;
                }
                this.xd *= 1.1;
                this.yd *= 0.9;
                this.zd *= 1.1;
            } else if (this.billowing) {
                this.yd *= 1.2;
            }
        }
        super.tick();
    }

    public void render(VertexConsumer builder, Camera renderInfo, float p_225606_3_) {
        Vec3 projectedView = renderInfo.getPosition();
        float lerpedX = (float)(Mth.lerp((double)p_225606_3_, (double)this.xo, (double)this.x) - projectedView.x());
        float lerpedY = (float)(Mth.lerp((double)p_225606_3_, (double)this.yo, (double)this.y) - projectedView.y());
        float lerpedZ = (float)(Mth.lerp((double)p_225606_3_, (double)this.zo, (double)this.z) - projectedView.z());
        int light = 0xF000F0;
        double ageMultiplier = 1.0 - Math.pow(Mth.clamp((float)((float)this.age + p_225606_3_), (float)0.0f, (float)this.lifetime), 3.0) / Math.pow(this.lifetime, 3.0);
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 4; ++j) {
                Vec3 vec = CUBE[i * 4 + j].scale(-1.0);
                vec = vec.scale((double)this.scale * ageMultiplier).add((double)lerpedX, (double)lerpedY, (double)lerpedZ);
                builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z).setUv((float)j / 2.0f, (float)(j % 2)).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
            }
        }
    }

    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static class Factory
    implements ParticleProvider<CubeParticleData> {
        public Particle createParticle(CubeParticleData data, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
            CubeParticle particle = new CubeParticle(world, x, y, z, motionX, motionY, motionZ);
            particle.setColor(data.r, data.g, data.b);
            particle.setScale(data.scale);
            particle.averageAge(data.avgAge);
            particle.setHot(data.hot);
            return particle;
        }
    }
}
