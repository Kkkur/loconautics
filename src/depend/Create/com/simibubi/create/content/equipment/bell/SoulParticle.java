/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.world.level.Level
 *  org.joml.Quaternionf
 */
package com.simibubi.create.content.equipment.bell;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.equipment.bell.BasicParticleData;
import com.simibubi.create.content.equipment.bell.CustomRotationParticle;
import com.simibubi.create.content.equipment.bell.SoulPulseEffect;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;

public class SoulParticle
extends CustomRotationParticle {
    private final SpriteSet animatedSprite;
    protected int startTicks;
    protected int endTicks;
    protected int numLoops;
    protected int firstStartFrame = 0;
    protected int startFrames = 17;
    protected int firstLoopFrame = 17;
    protected int loopFrames = 16;
    protected int firstEndFrame = 33;
    protected int endFrames = 20;
    protected AnimationStage animationStage;
    protected int totalFrames = 53;
    protected int ticksPerFrame = 2;
    protected boolean isPerimeter = false;
    protected boolean isExpandingPerimeter = false;
    protected boolean isVisible = true;
    protected int perimeterFrames = 8;

    public SoulParticle(ClientLevel worldIn, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet, ParticleOptions data) {
        super(worldIn, x, y, z, spriteSet, 0.0f);
        this.animatedSprite = spriteSet;
        this.quadSize = 0.5f;
        this.setSize(this.quadSize, this.quadSize);
        this.loopLength = this.loopFrames + (int)(this.random.nextFloat() * 5.0f - 4.0f);
        this.startTicks = this.startFrames + (int)(this.random.nextFloat() * 5.0f - 4.0f);
        this.endTicks = this.endFrames + (int)(this.random.nextFloat() * 5.0f - 4.0f);
        this.numLoops = (int)(1.0f + this.random.nextFloat() * 2.0f);
        this.setFrame(0);
        this.stoppedByCollision = true;
        this.mirror = this.random.nextBoolean();
        this.isPerimeter = data instanceof PerimeterData;
        this.isExpandingPerimeter = data instanceof ExpandingPerimeterData;
        AnimationStage animationStage = this.animationStage = !this.isPerimeter ? new StartAnimation(this) : new PerimeterAnimation(this);
        if (this.isPerimeter) {
            this.yo = y -= 0.4921875;
            this.totalFrames = this.perimeterFrames;
            this.isVisible = false;
        }
    }

    public void tick() {
        this.animationStage.tick();
        this.animationStage = this.animationStage.getNext();
        BlockPos pos = BlockPos.containing((double)this.x, (double)this.y, (double)this.z);
        if (this.animationStage == null) {
            this.remove();
        }
        if (!SoulPulseEffect.isDark((Level)this.level, pos)) {
            this.isVisible = true;
            if (!this.isPerimeter) {
                this.remove();
            }
        } else if (this.isPerimeter) {
            this.isVisible = false;
        }
    }

    @Override
    public void render(VertexConsumer builder, Camera camera, float partialTicks) {
        if (!this.isVisible) {
            return;
        }
        super.render(builder, camera, partialTicks);
    }

    public void setFrame(int frame) {
        if (frame >= 0 && frame < this.totalFrames) {
            this.setSprite(this.animatedSprite.get(frame, this.totalFrames));
        }
    }

    @Override
    public Quaternionf getCustomRotation(Camera camera, float partialTicks) {
        if (this.isPerimeter) {
            return Axis.XP.rotationDegrees(90.0f);
        }
        return new Quaternionf().rotationXYZ(0.0f, -camera.getYRot() * ((float)Math.PI / 180), 0.0f);
    }

    public static class PerimeterData
    extends BasicParticleData<SoulParticle> {
        @Override
        public BasicParticleData.IBasicParticleFactory<SoulParticle> getBasicFactory() {
            return (worldIn, x, y, z, vx, vy, vz, spriteSet) -> new SoulParticle(worldIn, x, y, z, vx, vy, vz, spriteSet, this);
        }

        public ParticleType<?> getType() {
            return AllParticleTypes.SOUL_PERIMETER.get();
        }
    }

    public static class ExpandingPerimeterData
    extends PerimeterData {
        @Override
        public ParticleType<?> getType() {
            return AllParticleTypes.SOUL_EXPANDING_PERIMETER.get();
        }
    }

    public static class StartAnimation
    extends AnimationStage {
        public StartAnimation(SoulParticle particle) {
            super(particle);
        }

        @Override
        public void tick() {
            super.tick();
            this.particle.setFrame(this.particle.firstStartFrame + (int)(this.getAnimAge() / (float)this.particle.startTicks * (float)this.particle.startFrames));
        }

        @Override
        public AnimationStage getNext() {
            if (this.animAge < this.particle.startTicks) {
                return this;
            }
            return new LoopAnimation(this.particle);
        }
    }

    public static class PerimeterAnimation
    extends AnimationStage {
        public PerimeterAnimation(SoulParticle particle) {
            super(particle);
        }

        @Override
        public void tick() {
            super.tick();
            this.particle.setFrame((int)this.getAnimAge() % this.particle.perimeterFrames);
        }

        @Override
        public AnimationStage getNext() {
            if (this.animAge < (this.particle.isExpandingPerimeter ? 8 : this.particle.startTicks + this.particle.endTicks + this.particle.numLoops * this.particle.loopLength)) {
                return this;
            }
            return null;
        }
    }

    public static abstract class AnimationStage {
        protected final SoulParticle particle;
        protected int ticks;
        protected int animAge;

        public AnimationStage(SoulParticle particle) {
            this.particle = particle;
        }

        public void tick() {
            ++this.ticks;
            if (this.ticks % this.particle.ticksPerFrame == 0) {
                ++this.animAge;
            }
        }

        public float getAnimAge() {
            return this.animAge;
        }

        public abstract AnimationStage getNext();
    }

    public static class EndAnimation
    extends AnimationStage {
        public EndAnimation(SoulParticle particle) {
            super(particle);
        }

        @Override
        public void tick() {
            super.tick();
            this.particle.setFrame(this.particle.firstEndFrame + (int)(this.getAnimAge() / (float)this.particle.endTicks * (float)this.particle.endFrames));
        }

        @Override
        public AnimationStage getNext() {
            if (this.animAge < this.particle.endTicks) {
                return this;
            }
            return null;
        }
    }

    public static class LoopAnimation
    extends AnimationStage {
        int loops;

        public LoopAnimation(SoulParticle particle) {
            super(particle);
        }

        @Override
        public void tick() {
            super.tick();
            int loopTick = this.getLoopTick();
            if (loopTick == 0) {
                ++this.loops;
            }
            this.particle.setFrame(this.particle.firstLoopFrame + loopTick);
        }

        private int getLoopTick() {
            return this.animAge % this.particle.loopFrames;
        }

        @Override
        public AnimationStage getNext() {
            if (this.loops <= this.particle.numLoops) {
                return this;
            }
            return new EndAnimation(this.particle);
        }
    }

    public static class Data
    extends BasicParticleData<SoulParticle> {
        @Override
        public BasicParticleData.IBasicParticleFactory<SoulParticle> getBasicFactory() {
            return (worldIn, x, y, z, vx, vy, vz, spriteSet) -> new SoulParticle(worldIn, x, y, z, vx, vy, vz, spriteSet, this);
        }

        public ParticleType<?> getType() {
            return AllParticleTypes.SOUL.get();
        }
    }
}
