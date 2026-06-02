/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.schematics.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SchematicTransformation {
    private Vec3 chasingPos = Vec3.ZERO;
    private Vec3 prevChasingPos = Vec3.ZERO;
    private BlockPos target = BlockPos.ZERO;
    private LerpedFloat scaleFrontBack = LerpedFloat.linear();
    private LerpedFloat scaleLeftRight = LerpedFloat.linear();
    private LerpedFloat rotation = LerpedFloat.angular();
    private double xOrigin;
    private double zOrigin;

    public void init(BlockPos anchor, StructurePlaceSettings settings, AABB bounds) {
        int leftRight = settings.getMirror() == Mirror.LEFT_RIGHT ? -1 : 1;
        int frontBack = settings.getMirror() == Mirror.FRONT_BACK ? -1 : 1;
        this.getScaleFB().chase(0.0, (double)0.45f, LerpedFloat.Chaser.EXP).startWithValue((double)frontBack);
        this.getScaleLR().chase(0.0, (double)0.45f, LerpedFloat.Chaser.EXP).startWithValue((double)leftRight);
        this.xOrigin = bounds.getXsize() / 2.0;
        this.zOrigin = bounds.getZsize() / 2.0;
        int r = -(settings.getRotation().ordinal() * 90);
        this.rotation.chase(0.0, (double)0.45f, LerpedFloat.Chaser.EXP).startWithValue((double)r);
        this.target = this.fromAnchor(anchor);
        this.prevChasingPos = this.chasingPos = Vec3.atLowerCornerOf((Vec3i)this.target);
    }

    public void applyTransformations(PoseStack ms, Vec3 camera) {
        float pt = AnimationTickHolder.getPartialTicks();
        TransformStack.of((PoseStack)ms).translate(VecHelper.lerp((float)pt, (Vec3)this.prevChasingPos, (Vec3)this.chasingPos).subtract(camera));
        Vec3 rotationOffset = this.getRotationOffset(true);
        float fb = this.getScaleFB().getValue(pt);
        float lr = this.getScaleLR().getValue(pt);
        float rot = this.rotation.getValue(pt) + (float)(fb < 0.0f && lr < 0.0f ? 180 : 0);
        ms.translate(this.xOrigin, 0.0, this.zOrigin);
        ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).translate(rotationOffset)).rotateYDegrees(rot)).translateBack(rotationOffset);
        ms.scale(Math.abs(fb), 1.0f, Math.abs(lr));
        ms.translate(-this.xOrigin, 0.0, -this.zOrigin);
    }

    public boolean isFlipped() {
        return this.getMirrorModifier(Direction.Axis.X) < 0 != this.getMirrorModifier(Direction.Axis.Z) < 0;
    }

    public Vec3 getRotationOffset(boolean ignoreMirrors) {
        Vec3 rotationOffset = Vec3.ZERO;
        if ((int)(this.zOrigin * 2.0) % 2 != (int)(this.xOrigin * 2.0) % 2) {
            float zIn;
            boolean xGreaterZ = this.xOrigin > this.zOrigin;
            float xIn = xGreaterZ ? 0.0f : 0.5f;
            float f = zIn = !xGreaterZ ? 0.0f : 0.5f;
            if (!ignoreMirrors) {
                xIn *= (float)this.getMirrorModifier(Direction.Axis.X);
                zIn *= (float)this.getMirrorModifier(Direction.Axis.Z);
            }
            rotationOffset = new Vec3((double)xIn, 0.0, (double)zIn);
        }
        return rotationOffset;
    }

    public Vec3 toLocalSpace(Vec3 vec) {
        float pt = AnimationTickHolder.getPartialTicks();
        Vec3 rotationOffset = this.getRotationOffset(true);
        vec = vec.subtract(VecHelper.lerp((float)pt, (Vec3)this.prevChasingPos, (Vec3)this.chasingPos));
        vec = vec.subtract(this.xOrigin + rotationOffset.x, 0.0, this.zOrigin + rotationOffset.z);
        vec = VecHelper.rotate((Vec3)vec, (double)(-this.rotation.getValue(pt)), (Direction.Axis)Direction.Axis.Y);
        vec = vec.add(rotationOffset.x, 0.0, rotationOffset.z);
        vec = vec.multiply((double)this.getScaleFB().getValue(pt), 1.0, (double)this.getScaleLR().getValue(pt));
        vec = vec.add(this.xOrigin, 0.0, this.zOrigin);
        return vec;
    }

    public StructurePlaceSettings toSettings() {
        boolean mirrorfb;
        StructurePlaceSettings settings = new StructurePlaceSettings();
        int i = (int)this.rotation.getChaseTarget();
        boolean mirrorlr = this.getScaleLR().getChaseTarget() < 0.0f;
        boolean bl = mirrorfb = this.getScaleFB().getChaseTarget() < 0.0f;
        if (mirrorlr && mirrorfb) {
            mirrorfb = false;
            mirrorlr = false;
            i += 180;
        }
        if ((i %= 360) < 0) {
            i += 360;
        }
        Rotation rotation = Rotation.NONE;
        switch (i) {
            case 90: {
                rotation = Rotation.COUNTERCLOCKWISE_90;
                break;
            }
            case 180: {
                rotation = Rotation.CLOCKWISE_180;
                break;
            }
            case 270: {
                rotation = Rotation.CLOCKWISE_90;
                break;
            }
        }
        settings.setRotation(rotation);
        if (mirrorfb) {
            settings.setMirror(Mirror.FRONT_BACK);
        }
        if (mirrorlr) {
            settings.setMirror(Mirror.LEFT_RIGHT);
        }
        return settings;
    }

    public BlockPos getAnchor() {
        Vec3 vec = Vec3.ZERO.add(0.5, 0.0, 0.5);
        Vec3 rotationOffset = this.getRotationOffset(false);
        vec = vec.subtract(this.xOrigin, 0.0, this.zOrigin);
        vec = vec.subtract(rotationOffset.x, 0.0, rotationOffset.z);
        vec = vec.multiply((double)this.getScaleFB().getChaseTarget(), 1.0, (double)this.getScaleLR().getChaseTarget());
        vec = VecHelper.rotate((Vec3)vec, (double)this.rotation.getChaseTarget(), (Direction.Axis)Direction.Axis.Y);
        vec = vec.add(this.xOrigin, 0.0, this.zOrigin);
        vec = vec.add((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ());
        return BlockPos.containing((double)vec.x, (double)vec.y, (double)vec.z);
    }

    public BlockPos fromAnchor(BlockPos pos) {
        Vec3 vec = Vec3.ZERO.add(0.5, 0.0, 0.5);
        Vec3 rotationOffset = this.getRotationOffset(false);
        vec = vec.subtract(this.xOrigin, 0.0, this.zOrigin);
        vec = vec.subtract(rotationOffset.x, 0.0, rotationOffset.z);
        vec = vec.multiply((double)this.getScaleFB().getChaseTarget(), 1.0, (double)this.getScaleLR().getChaseTarget());
        vec = VecHelper.rotate((Vec3)vec, (double)this.rotation.getChaseTarget(), (Direction.Axis)Direction.Axis.Y);
        vec = vec.add(this.xOrigin, 0.0, this.zOrigin);
        return pos.subtract((Vec3i)BlockPos.containing((double)vec.x, (double)vec.y, (double)vec.z));
    }

    public int getRotationTarget() {
        return (int)this.rotation.getChaseTarget();
    }

    public int getMirrorModifier(Direction.Axis axis) {
        if (axis == Direction.Axis.Z) {
            return (int)this.getScaleLR().getChaseTarget();
        }
        return (int)this.getScaleFB().getChaseTarget();
    }

    public float getCurrentRotation() {
        float pt = AnimationTickHolder.getPartialTicks();
        return this.rotation.getValue(pt);
    }

    public void tick() {
        this.prevChasingPos = this.chasingPos;
        this.chasingPos = VecHelper.lerp((float)0.45f, (Vec3)this.chasingPos, (Vec3)Vec3.atLowerCornerOf((Vec3i)this.target));
        this.getScaleLR().tickChaser();
        this.getScaleFB().tickChaser();
        this.rotation.tickChaser();
    }

    public void flip(Direction.Axis axis) {
        if (axis == Direction.Axis.X) {
            this.getScaleLR().updateChaseTarget(this.getScaleLR().getChaseTarget() * -1.0f);
        }
        if (axis == Direction.Axis.Z) {
            this.getScaleFB().updateChaseTarget(this.getScaleFB().getChaseTarget() * -1.0f);
        }
    }

    public void rotate90(boolean clockwise) {
        this.rotation.updateChaseTarget(this.rotation.getChaseTarget() + (float)(clockwise ? -90 : 90));
    }

    public void move(int xIn, int yIn, int zIn) {
        this.moveTo(this.target.offset(xIn, yIn, zIn));
    }

    public void startAt(BlockPos pos) {
        this.prevChasingPos = this.chasingPos = Vec3.atLowerCornerOf((Vec3i)pos);
        this.moveTo(pos);
    }

    public void moveTo(BlockPos pos) {
        this.target = pos;
    }

    public void moveTo(int xIn, int yIn, int zIn) {
        this.moveTo(new BlockPos(xIn, yIn, zIn));
    }

    public LerpedFloat getScaleFB() {
        return this.scaleFrontBack;
    }

    public LerpedFloat getScaleLR() {
        return this.scaleLeftRight;
    }
}
