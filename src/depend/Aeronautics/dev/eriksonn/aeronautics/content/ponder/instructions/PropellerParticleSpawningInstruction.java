/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  dev.simulated_team.simulated.mixin.accessor.WorldSectionElementAccessor
 *  dev.simulated_team.simulated.util.SimMathUtils
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.TickingInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package dev.eriksonn.aeronautics.content.ponder.instructions;

import com.mojang.math.Axis;
import dev.eriksonn.aeronautics.content.particle.PropellerAirParticleData;
import dev.simulated_team.simulated.mixin.accessor.WorldSectionElementAccessor;
import dev.simulated_team.simulated.util.SimMathUtils;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class PropellerParticleSpawningInstruction
extends TickingInstruction {
    ParticleSpawner spawner;

    public PropellerParticleSpawningInstruction(@Nullable ElementLink<WorldSectionElement> link, BlockPos location, Direction direction, int ticks, float particleAmount, float particleSpeed, float radius) {
        this(link, location, direction, ticks, particleAmount, particleSpeed, radius, false);
    }

    public PropellerParticleSpawningInstruction(@Nullable ElementLink<WorldSectionElement> link, BlockPos location, Direction direction, int ticks, float particleAmount, float particleSpeed, float radius, boolean hasCollision) {
        super(false, ticks);
        this.spawner = new ParticleSpawner(link, location, direction, particleAmount, particleSpeed, radius, hasCollision);
    }

    public void tick(PonderScene scene) {
        super.tick(scene);
        this.spawner.tick(scene);
    }

    public static class ParticleSpawner {
        protected final BlockPos location;
        protected ElementLink<WorldSectionElement> link;
        protected float radius;
        protected final Quaternionf rot = new Quaternionf();
        protected float particleAmount;
        protected float particleSpeed;
        protected boolean hasCollision;

        ParticleSpawner(@Nullable ElementLink<WorldSectionElement> link, BlockPos location, Direction direction, float particleAmount, float particleSpeed, float radius, boolean hasCollision) {
            this.link = link;
            this.location = location;
            this.hasCollision = hasCollision;
            this.radius = radius;
            this.particleAmount = particleAmount;
            this.particleSpeed = particleSpeed / 20.0f;
            if (direction.getAxis().isHorizontal()) {
                this.rot.set((Quaternionfc)Axis.YP.rotationDegrees(AngleHelper.horizontalAngle((Direction)direction.getOpposite())));
            }
            this.rot.mul((Quaternionfc)Axis.XP.rotationDegrees(-90.0f - AngleHelper.verticalAngle((Direction)direction)));
        }

        void tick(PonderScene scene) {
            WorldSectionElement element;
            PonderLevel level = scene.getWorld();
            float particleCount = this.particleAmount + level.random.nextFloat() - 1.0f;
            Vec3 totalOffset = VecHelper.getCenterOf((Vec3i)this.location);
            Quaternionf elementRot = new Quaternionf();
            if (this.link != null && (element = (WorldSectionElement)scene.resolve(this.link)) != null) {
                Vec3 elementOffset = element.getAnimatedOffset();
                Vec3 rotation = new Vec3(Math.toRadians(element.getAnimatedRotation().x), Math.toRadians(element.getAnimatedRotation().y), Math.toRadians(element.getAnimatedRotation().z));
                elementRot.mul((Quaternionfc)new Quaternionf((float)Math.sin(rotation.x / 2.0), 0.0f, 0.0f, (float)Math.cos(rotation.x / 2.0)));
                elementRot.mul((Quaternionfc)new Quaternionf(0.0f, 0.0f, (float)Math.sin(rotation.z / 2.0), (float)Math.cos(rotation.z / 2.0)));
                elementRot.mul((Quaternionfc)new Quaternionf(0.0f, (float)Math.sin(rotation.y / 2.0), 0.0f, (float)Math.cos(rotation.y / 2.0)));
                totalOffset = totalOffset.subtract(((WorldSectionElementAccessor)element).getCenterOfRotation());
                totalOffset = SimMathUtils.rotateQuatReverse((Vec3)totalOffset, (Quaternionf)elementRot);
                totalOffset = totalOffset.add(((WorldSectionElementAccessor)element).getCenterOfRotation());
                totalOffset = totalOffset.add(elementOffset);
            }
            int i = 0;
            while ((float)i < particleCount) {
                double R = (double)this.radius * Math.sqrt(level.random.nextFloat());
                double angle = Math.PI * 2 * (double)level.random.nextFloat();
                Vec3 randomOffset = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)RandomSource.create(), (float)0.5f);
                randomOffset = new Vec3(randomOffset.x, 0.0, randomOffset.z);
                Vec3 particlePos = new Vec3(Math.cos(angle) * R, 0.0, Math.sin(angle) * R).add(randomOffset);
                Vec3 speedVector = new Vec3(0.0, (double)this.particleSpeed, 0.0);
                particlePos = SimMathUtils.rotateQuatReverse((Vec3)particlePos, (Quaternionf)this.rot);
                particlePos = SimMathUtils.rotateQuatReverse((Vec3)particlePos, (Quaternionf)elementRot);
                speedVector = SimMathUtils.rotateQuatReverse((Vec3)speedVector, (Quaternionf)this.rot);
                speedVector = SimMathUtils.rotateQuatReverse((Vec3)speedVector, (Quaternionf)elementRot);
                particlePos = particlePos.add(totalOffset);
                level.addParticle((ParticleOptions)new PropellerAirParticleData(this.hasCollision, true), particlePos.x, particlePos.y, particlePos.z, speedVector.x(), speedVector.y(), speedVector.z());
                ++i;
            }
        }
    }
}
