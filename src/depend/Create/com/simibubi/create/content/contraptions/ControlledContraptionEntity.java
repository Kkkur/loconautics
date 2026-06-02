/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ControlledContraptionEntity
extends AbstractContraptionEntity {
    protected BlockPos controllerPos;
    protected Direction.Axis rotationAxis;
    protected float prevAngle;
    protected float angle;
    protected float angleDelta;

    public ControlledContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public static ControlledContraptionEntity create(Level world, IControlContraption controller, Contraption contraption) {
        ControlledContraptionEntity entity = new ControlledContraptionEntity((EntityType)AllEntityTypes.CONTROLLED_CONTRAPTION.get(), world);
        entity.controllerPos = controller.getBlockPosition();
        entity.setContraption(contraption);
        return entity;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (!this.level().isClientSide()) {
            return;
        }
        for (Entity entity : this.getPassengers()) {
            this.positionRider(entity);
        }
    }

    @Override
    public Vec3 getContactPointMotion(Vec3 globalContactPoint) {
        if (this.contraption instanceof TranslatingContraption) {
            return this.getDeltaMovement();
        }
        return super.getContactPointMotion(globalContactPoint);
    }

    @Override
    protected void setContraption(Contraption contraption) {
        super.setContraption(contraption);
        if (contraption instanceof BearingContraption) {
            this.rotationAxis = ((BearingContraption)contraption).getFacing().getAxis();
        }
    }

    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
        super.readAdditional(compound, spawnPacket);
        if (compound.contains("ControllerRelative")) {
            this.controllerPos = NBTHelper.readBlockPos((CompoundTag)compound, (String)"ControllerRelative").offset((Vec3i)this.blockPosition());
        }
        if (compound.contains("Axis")) {
            this.rotationAxis = (Direction.Axis)NBTHelper.readEnum((CompoundTag)compound, (String)"Axis", Direction.Axis.class);
        }
        this.angle = compound.getFloat("Angle");
    }

    @Override
    protected void writeAdditional(CompoundTag compound, HolderLookup.Provider registries, boolean spawnPacket) {
        super.writeAdditional(compound, registries, spawnPacket);
        compound.put("ControllerRelative", NbtUtils.writeBlockPos((BlockPos)this.controllerPos.subtract((Vec3i)this.blockPosition())));
        if (this.rotationAxis != null) {
            NBTHelper.writeEnum((CompoundTag)compound, (String)"Axis", (Enum)this.rotationAxis);
        }
        compound.putFloat("Angle", this.angle);
    }

    @Override
    public AbstractContraptionEntity.ContraptionRotationState getRotationState() {
        AbstractContraptionEntity.ContraptionRotationState crs = new AbstractContraptionEntity.ContraptionRotationState();
        if (this.rotationAxis == Direction.Axis.X) {
            crs.xRotation = this.angle;
        }
        if (this.rotationAxis == Direction.Axis.Y) {
            crs.yRotation = this.angle;
        }
        if (this.rotationAxis == Direction.Axis.Z) {
            crs.zRotation = this.angle;
        }
        return crs;
    }

    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate((Vec3)localPos, (double)this.getAngle(partialTicks), (Direction.Axis)this.rotationAxis);
        return localPos;
    }

    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate((Vec3)localPos, (double)(-this.getAngle(partialTicks)), (Direction.Axis)this.rotationAxis);
        return localPos;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        if (!this.level().isClientSide()) {
            return;
        }
        for (Entity entity : this.getPassengers()) {
            this.positionRider(entity);
        }
    }

    public float getAngle(float partialTicks) {
        return partialTicks == 1.0f ? this.angle : AngleHelper.angleLerp((double)partialTicks, (double)this.prevAngle, (double)this.angle);
    }

    public void setRotationAxis(Direction.Axis rotationAxis) {
        this.rotationAxis = rotationAxis;
    }

    public Direction.Axis getRotationAxis() {
        return this.rotationAxis;
    }

    public void teleportTo(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
    }

    @OnlyIn(value=Dist.CLIENT)
    public void lerpTo(double pX, double pY, double pZ, float pYRot, float pXRot, int pSteps) {
    }

    @Override
    protected void tickContraption() {
        this.angleDelta = this.angle - this.prevAngle;
        this.prevAngle = this.angle;
        this.tickActors();
        if (this.controllerPos == null) {
            return;
        }
        if (!this.level().isLoaded(this.controllerPos)) {
            return;
        }
        IControlContraption controller = this.getController();
        if (controller == null) {
            this.discard();
            return;
        }
        if (!controller.isAttachedTo(this)) {
            controller.attach(this);
            if (this.level().isClientSide) {
                this.setPos(this.getX(), this.getY(), this.getZ());
            }
        }
    }

    @Override
    protected boolean shouldActorTrigger(MovementContext context, StructureTemplate.StructureBlockInfo blockInfo, MovementBehaviour actor, Vec3 actorPosition, BlockPos gridPosition) {
        if (super.shouldActorTrigger(context, blockInfo, actor, actorPosition, gridPosition)) {
            return true;
        }
        Contraption contraption = this.contraption;
        if (!(contraption instanceof BearingContraption)) {
            return false;
        }
        BearingContraption bc = (BearingContraption)contraption;
        Direction facing = bc.getFacing();
        Vec3 activeAreaOffset = actor.getActiveAreaOffset(context);
        if (!activeAreaOffset.multiply(VecHelper.axisAlingedPlaneOf((Vec3)Vec3.atLowerCornerOf((Vec3i)facing.getNormal()))).equals((Object)Vec3.ZERO)) {
            return false;
        }
        if (!VecHelper.onSameAxis((BlockPos)blockInfo.pos(), (BlockPos)BlockPos.ZERO, (Direction.Axis)facing.getAxis())) {
            return false;
        }
        context.relativeMotion = context.motion = Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale((double)this.angleDelta / 360.0);
        int timer = context.data.getInt("StationaryTimer");
        if (timer > 0) {
            context.data.putInt("StationaryTimer", timer - 1);
            return false;
        }
        context.data.putInt("StationaryTimer", 20);
        return true;
    }

    protected IControlContraption getController() {
        if (this.controllerPos == null) {
            return null;
        }
        if (!this.level().isLoaded(this.controllerPos)) {
            return null;
        }
        BlockEntity be = this.level().getBlockEntity(this.controllerPos);
        if (!(be instanceof IControlContraption)) {
            return null;
        }
        return (IControlContraption)be;
    }

    @Override
    protected StructureTransform makeStructureTransform() {
        BlockPos offset = BlockPos.containing((Position)this.getAnchorVec().add(0.5, 0.5, 0.5));
        float xRot = this.rotationAxis == Direction.Axis.X ? this.angle : 0.0f;
        float yRot = this.rotationAxis == Direction.Axis.Y ? this.angle : 0.0f;
        float zRot = this.rotationAxis == Direction.Axis.Z ? this.angle : 0.0f;
        return new StructureTransform(offset, xRot, yRot, zRot);
    }

    @Override
    protected void onContraptionStalled() {
        IControlContraption controller = this.getController();
        if (controller != null) {
            controller.onStall();
        }
        super.onContraptionStalled();
    }

    @Override
    protected float getStalledAngle() {
        return this.angle;
    }

    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        this.setPosRaw(x, y, z);
        this.angle = this.prevAngle = angle;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        float angle = this.getAngle(partialTicks);
        Direction.Axis axis = this.getRotationAxis();
        TransformStack.of((PoseStack)matrixStack).nudge(this.getId());
        if (axis != null) {
            ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)matrixStack).center()).rotateDegrees(angle, axis)).uncenter();
        }
    }
}
