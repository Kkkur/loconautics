/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.contraptions.Contraption
 *  com.simibubi.create.content.contraptions.ControlledContraptionEntity
 *  com.simibubi.create.content.contraptions.IControlContraption
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.simulated_team.simulated.util.SimMathUtils
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.bearing.contraption;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import dev.eriksonn.aeronautics.index.AeroEntityTypes;
import dev.simulated_team.simulated.util.SimMathUtils;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class PropellerBearingContraptionEntity
extends ControlledContraptionEntity {
    public Quaternionf tiltQuat = new Quaternionf();
    public Quaternionf previousTiltQuat = new Quaternionf();
    public Direction direction = Direction.UP;
    Quaternionf interpolatedQuat = new Quaternionf();

    public PropellerBearingContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public static ControlledContraptionEntity create(Level world, IControlContraption controller, Contraption contraption) {
        PropellerBearingContraptionEntity entity = new PropellerBearingContraptionEntity((EntityType)AeroEntityTypes.PROPELLER_CONTROLLED_CONTRAPTION.get(), world);
        entity.setControllerPos(controller.getBlockPosition());
        entity.setContraption(contraption);
        return entity;
    }

    public PropellerBearingBlockEntity getBearingEntity() {
        if (this.controllerPos == null) {
            return null;
        }
        if (!this.level().isLoaded(this.controllerPos)) {
            return null;
        }
        BlockEntity te = this.level().getBlockEntity(this.controllerPos);
        if (!(te instanceof PropellerBearingBlockEntity)) {
            return null;
        }
        return (PropellerBearingBlockEntity)te;
    }

    Quaternionf getInterpolatedQuat(float partialTick) {
        return this.previousTiltQuat.slerp((Quaternionfc)this.tiltQuat, partialTick, this.interpolatedQuat);
    }

    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate((Vec3)localPos, (double)this.getAngle(partialTicks), (Direction.Axis)this.rotationAxis);
        localPos = SimMathUtils.rotateQuatReverse((Vec3)localPos, (Quaternionf)this.getInterpolatedQuat(partialTicks));
        return localPos;
    }

    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        localPos = SimMathUtils.rotateQuat((Vec3)localPos, (Quaternionf)this.getInterpolatedQuat(partialTicks));
        localPos = VecHelper.rotate((Vec3)localPos, (double)(-this.getAngle(partialTicks)), (Direction.Axis)this.rotationAxis);
        return localPos;
    }

    public float getAngle(float partialTicks) {
        IControlContraption controller = this.getController();
        if (controller instanceof PropellerBearingBlockEntity) {
            PropellerBearingBlockEntity tile = (PropellerBearingBlockEntity)controller;
            if (tile.disassemblySlowdown) {
                return tile.getInterpolatedAngle(partialTicks - 1.0f);
            }
        }
        return partialTicks == 1.0f ? this.angle : AngleHelper.angleLerp((double)partialTicks, (double)this.prevAngle, (double)this.angle);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void applyLocalTransforms(PoseStack poseStack, float partialTicks) {
        float angle = this.getAngle(partialTicks);
        Direction.Axis axis = this.getRotationAxis();
        Vec3 normal = new Vec3((double)this.direction.getStepX(), (double)this.direction.getStepY(), (double)this.direction.getStepZ());
        normal = normal.scale(0.75);
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).nudge(this.getId())).center()).translate(normal.scale(-1.0))).rotate((Quaternionfc)this.getInterpolatedQuat(partialTicks)).translate(normal)).rotateDegrees(angle, axis)).uncenter();
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    public void setContraption(Contraption contraption) {
        super.setContraption(contraption);
    }
}
