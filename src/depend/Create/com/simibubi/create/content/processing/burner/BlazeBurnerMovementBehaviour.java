/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.processing.burner;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerRenderer;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlazeBurnerMovementBehaviour
implements MovementBehaviour {
    @Override
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    @Override
    public void tick(MovementContext context) {
        if (!context.world.isClientSide()) {
            return;
        }
        if (!this.shouldRender(context)) {
            return;
        }
        RandomSource r = context.world.getRandom();
        Vec3 c = context.position;
        Vec3 v = c.add(VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)r, (float)0.125f).multiply(1.0, 0.0, 1.0));
        if (r.nextInt(3) == 0 && context.motion.length() < 0.015625) {
            context.world.addParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, v.x, v.y, v.z, 0.0, 0.0, 0.0);
        }
        LerpedFloat headAngle = this.getHeadAngle(context);
        boolean quickTurn = this.shouldRenderHat(context) && !Mth.equal((double)context.relativeMotion.length(), (double)0.0);
        headAngle.chase((double)(headAngle.getValue() + AngleHelper.getShortestAngleDiff((double)headAngle.getValue(), (double)this.getTargetAngle(context))), 0.5, quickTurn ? LerpedFloat.Chaser.EXP : LerpedFloat.Chaser.exp((double)5.0));
        headAngle.tickChaser();
    }

    public void invalidate(MovementContext context) {
        context.data.remove("Conductor");
    }

    private boolean shouldRender(MovementContext context) {
        return context.state.getOptionalValue(BlazeBurnerBlock.HEAT_LEVEL).orElse(BlazeBurnerBlock.HeatLevel.NONE) != BlazeBurnerBlock.HeatLevel.NONE;
    }

    private LerpedFloat getHeadAngle(MovementContext context) {
        if (!(context.temporaryData instanceof LerpedFloat)) {
            context.temporaryData = LerpedFloat.angular().startWithValue((double)this.getTargetAngle(context));
        }
        return (LerpedFloat)context.temporaryData;
    }

    private float getTargetAngle(MovementContext context) {
        AbstractContraptionEntity abstractContraptionEntity;
        if (this.shouldRenderHat(context) && !Mth.equal((double)context.relativeMotion.length(), (double)0.0) && (abstractContraptionEntity = context.contraption.entity) instanceof CarriageContraptionEntity) {
            CarriageContraptionEntity cce = (CarriageContraptionEntity)abstractContraptionEntity;
            float angle = AngleHelper.deg((double)(-Mth.atan2((double)context.relativeMotion.x, (double)context.relativeMotion.z)));
            return cce.getInitialOrientation().getAxis() == Direction.Axis.X ? angle + 180.0f : angle;
        }
        Entity player = Minecraft.getInstance().cameraEntity;
        if (player != null && !player.isInvisible() && context.position != null) {
            Vec3 applyRotation = context.contraption.entity.reverseRotation(player.position().subtract(context.position), 1.0f);
            double dx = applyRotation.x;
            double dz = applyRotation.z;
            return AngleHelper.deg((double)(-Mth.atan2((double)dz, (double)dx))) - 90.0f;
        }
        return 0.0f;
    }

    private boolean shouldRenderHat(MovementContext context) {
        CarriageContraptionEntity cce;
        AbstractContraptionEntity abstractContraptionEntity;
        CompoundTag data = context.data;
        if (!data.contains("Conductor")) {
            data.putBoolean("Conductor", this.determineIfConducting(context));
        }
        return data.getBoolean("Conductor") && (abstractContraptionEntity = context.contraption.entity) instanceof CarriageContraptionEntity && (cce = (CarriageContraptionEntity)abstractContraptionEntity).hasSchedule();
    }

    private boolean determineIfConducting(MovementContext context) {
        Contraption contraption = context.contraption;
        if (!(contraption instanceof CarriageContraption)) {
            return false;
        }
        CarriageContraption carriageContraption = (CarriageContraption)contraption;
        Direction assemblyDirection = carriageContraption.getAssemblyDirection();
        for (Direction direction : Iterate.directionsInAxis((Direction.Axis)assemblyDirection.getAxis())) {
            if (!carriageContraption.inControl(context.localPos, direction)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!this.shouldRender(context)) {
            return;
        }
        BlazeBurnerRenderer.renderInContraption(context, renderWorld, matrices, buffer, this.getHeadAngle(context), this.shouldRenderHat(context));
    }
}
