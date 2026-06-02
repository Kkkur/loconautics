/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  dev.simulated_team.simulated.api.BearingSlowdownController
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.ponder.instructions;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerParticleSpawningInstruction;
import dev.simulated_team.simulated.api.BearingSlowdownController;
import java.util.ArrayList;
import java.util.List;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class PropellerRotateInstruction
extends PonderInstruction {
    BlockPos pos;
    List<ElementLink<WorldSectionElement>> contraptions;
    long activatedContraptionsIndex = 0L;
    final Direction direction;
    final Vec3 normal;
    float lastAngle;
    float targetSpeed;
    final float originalSails;
    final float originalSpeed;
    float currentSpeed;
    float sailSmoothingAmount;
    boolean stopped;
    BearingSlowdownController slowdownController = null;
    PropellerParticleSpawningInstruction.ParticleSpawner spawner = null;
    float particleSpeedScale;
    float particleAmountScale;

    public PropellerRotateInstruction(BlockPos pos, ElementLink<WorldSectionElement> contraption, Direction direction, float targetSpeed, float sailSmoothingAmount) {
        this.pos = pos;
        this.contraptions = new ArrayList<ElementLink<WorldSectionElement>>();
        this.contraptions.add(contraption);
        this.direction = direction;
        this.originalSpeed = this.targetSpeed = targetSpeed;
        this.originalSails = this.sailSmoothingAmount = sailSmoothingAmount;
        Vec3i n = direction.getNormal();
        this.normal = new Vec3((double)Math.abs(n.getX()), (double)Math.abs(n.getY()), (double)Math.abs(n.getZ()));
    }

    public void reset(PonderScene scene) {
        super.reset(scene);
        this.sailSmoothingAmount = this.originalSails;
        this.targetSpeed = this.originalSpeed;
        this.currentSpeed = 0.0f;
        this.lastAngle = 0.0f;
        this.slowdownController = null;
        this.stopped = false;
    }

    public boolean isComplete() {
        return this.stopped;
    }

    public void tick(PonderScene scene) {
        BlockEntity blockEntity = scene.getWorld().getBlockEntity(this.pos);
        if (blockEntity instanceof PropellerBearingBlockEntity) {
            PropellerBearingBlockEntity bearing = (PropellerBearingBlockEntity)blockEntity;
            float angle = bearing.getInterpolatedAngle(0.0f);
            if (this.slowdownController != null) {
                this.stopped = this.slowdownController.stepGoal();
                this.currentSpeed = this.slowdownController.getSpeed(0.0f);
                angle = this.slowdownController.getAngle(0.0f);
            } else {
                this.currentSpeed = Mth.lerp((float)(0.4f / (float)Math.sqrt(this.sailSmoothingAmount)), (float)this.currentSpeed, (float)KineticBlockEntity.convertToAngular((float)this.targetSpeed));
                angle += this.currentSpeed;
            }
            if (this.spawner != null) {
                this.spawner.particleSpeed = this.currentSpeed * this.particleSpeedScale / 200.0f;
                this.spawner.particleAmount = Math.abs(this.currentSpeed) * this.particleAmountScale / 10.0f;
                this.spawner.tick(scene);
            }
            for (ElementLink<WorldSectionElement> contraption : this.contraptions) {
                WorldSectionElement link = (WorldSectionElement)scene.resolve(contraption);
                if (link == null) continue;
                this.updateLinkAngle(link, angle, false);
            }
            bearing.setAngle(angle);
        }
    }

    public void addSection(PonderScene scene, ElementLink<WorldSectionElement> section) {
        this.contraptions.add(section);
        BlockEntity blockEntity = scene.getWorld().getBlockEntity(this.pos);
        if (blockEntity instanceof PropellerBearingBlockEntity) {
            PropellerBearingBlockEntity bearing = (PropellerBearingBlockEntity)blockEntity;
            float angle = bearing.getInterpolatedAngle(0.0f);
            WorldSectionElement link = (WorldSectionElement)scene.resolve(section);
            if (link != null) {
                this.updateLinkAngle(link, angle, true);
            }
        }
    }

    void updateLinkAngle(WorldSectionElement link, float angle, boolean forced) {
        Vec3 v = link.getAnimatedRotation();
        double d = v.dot(this.normal);
        link.setAnimatedRotation(v.add(this.normal.scale((double)angle - d)), forced);
    }
}
