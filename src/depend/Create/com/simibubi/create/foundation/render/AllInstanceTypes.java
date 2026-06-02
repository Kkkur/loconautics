/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.InstanceType
 *  dev.engine_room.flywheel.api.layout.FloatRepr
 *  dev.engine_room.flywheel.api.layout.IntegerRepr
 *  dev.engine_room.flywheel.api.layout.LayoutBuilder
 *  dev.engine_room.flywheel.api.layout.ValueRepr
 *  dev.engine_room.flywheel.lib.instance.SimpleInstanceType
 *  dev.engine_room.flywheel.lib.util.ExtraMemoryOps
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.lwjgl.system.MemoryUtil
 */
package com.simibubi.create.foundation.render;

import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.FluidInstance;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import com.simibubi.create.content.processing.burner.ScrollTransformedInstance;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.api.layout.FloatRepr;
import dev.engine_room.flywheel.api.layout.IntegerRepr;
import dev.engine_room.flywheel.api.layout.LayoutBuilder;
import dev.engine_room.flywheel.api.layout.ValueRepr;
import dev.engine_room.flywheel.lib.instance.SimpleInstanceType;
import dev.engine_room.flywheel.lib.util.ExtraMemoryOps;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(value=Dist.CLIENT)
public class AllInstanceTypes {
    public static final InstanceType<RotatingInstance> ROTATING = SimpleInstanceType.builder(RotatingInstance::new).cullShader(Create.asResource("instance/cull/rotating.glsl")).vertexShader(Create.asResource("instance/rotating.vert")).layout(LayoutBuilder.create().vector("color", (ValueRepr)FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4).vector("light", (ValueRepr)IntegerRepr.SHORT, 2).vector("overlay", (ValueRepr)IntegerRepr.SHORT, 2).vector("rotation", (ValueRepr)FloatRepr.FLOAT, 4).vector("pos", (ValueRepr)FloatRepr.FLOAT, 3).scalar("speed", (ValueRepr)FloatRepr.FLOAT).scalar("offset", (ValueRepr)FloatRepr.FLOAT).vector("axis", (ValueRepr)FloatRepr.NORMALIZED_BYTE, 3).build()).writer((ptr, instance) -> {
        MemoryUtil.memPutByte((long)ptr, (byte)instance.red);
        MemoryUtil.memPutByte((long)(ptr + 1L), (byte)instance.green);
        MemoryUtil.memPutByte((long)(ptr + 2L), (byte)instance.blue);
        MemoryUtil.memPutByte((long)(ptr + 3L), (byte)instance.alpha);
        ExtraMemoryOps.put2x16((long)(ptr + 4L), (int)instance.light);
        ExtraMemoryOps.put2x16((long)(ptr + 8L), (int)instance.overlay);
        ExtraMemoryOps.putQuaternionf((long)(ptr + 12L), (Quaternionfc)instance.rotation);
        MemoryUtil.memPutFloat((long)(ptr + 28L), (float)instance.x);
        MemoryUtil.memPutFloat((long)(ptr + 32L), (float)instance.y);
        MemoryUtil.memPutFloat((long)(ptr + 36L), (float)instance.z);
        MemoryUtil.memPutFloat((long)(ptr + 40L), (float)instance.rotationalSpeed);
        MemoryUtil.memPutFloat((long)(ptr + 44L), (float)instance.rotationOffset);
        MemoryUtil.memPutByte((long)(ptr + 48L), (byte)instance.rotationAxisX);
        MemoryUtil.memPutByte((long)(ptr + 49L), (byte)instance.rotationAxisY);
        MemoryUtil.memPutByte((long)(ptr + 50L), (byte)instance.rotationAxisZ);
    }).build();
    public static final InstanceType<ScrollInstance> SCROLLING = SimpleInstanceType.builder(ScrollInstance::new).cullShader(Create.asResource("instance/cull/scrolling.glsl")).vertexShader(Create.asResource("instance/scrolling.vert")).layout(LayoutBuilder.create().vector("color", (ValueRepr)FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4).vector("light", (ValueRepr)IntegerRepr.SHORT, 2).vector("overlay", (ValueRepr)IntegerRepr.SHORT, 2).vector("pos", (ValueRepr)FloatRepr.FLOAT, 3).vector("rotation", (ValueRepr)FloatRepr.FLOAT, 4).vector("speed", (ValueRepr)FloatRepr.FLOAT, 2).vector("diff", (ValueRepr)FloatRepr.FLOAT, 2).vector("scale", (ValueRepr)FloatRepr.FLOAT, 2).vector("offset", (ValueRepr)FloatRepr.FLOAT, 2).build()).writer((ptr, instance) -> {
        MemoryUtil.memPutByte((long)ptr, (byte)instance.red);
        MemoryUtil.memPutByte((long)(ptr + 1L), (byte)instance.green);
        MemoryUtil.memPutByte((long)(ptr + 2L), (byte)instance.blue);
        MemoryUtil.memPutByte((long)(ptr + 3L), (byte)instance.alpha);
        ExtraMemoryOps.put2x16((long)(ptr + 4L), (int)instance.light);
        ExtraMemoryOps.put2x16((long)(ptr + 8L), (int)instance.overlay);
        MemoryUtil.memPutFloat((long)(ptr + 12L), (float)instance.x);
        MemoryUtil.memPutFloat((long)(ptr + 16L), (float)instance.y);
        MemoryUtil.memPutFloat((long)(ptr + 20L), (float)instance.z);
        ExtraMemoryOps.putQuaternionf((long)(ptr + 24L), (Quaternionfc)instance.rotation);
        MemoryUtil.memPutFloat((long)(ptr + 40L), (float)instance.speedU);
        MemoryUtil.memPutFloat((long)(ptr + 44L), (float)instance.speedV);
        MemoryUtil.memPutFloat((long)(ptr + 48L), (float)instance.diffU);
        MemoryUtil.memPutFloat((long)(ptr + 52L), (float)instance.diffV);
        MemoryUtil.memPutFloat((long)(ptr + 56L), (float)instance.scaleU);
        MemoryUtil.memPutFloat((long)(ptr + 60L), (float)instance.scaleV);
        MemoryUtil.memPutFloat((long)(ptr + 64L), (float)instance.offsetU);
        MemoryUtil.memPutFloat((long)(ptr + 68L), (float)instance.offsetV);
    }).build();
    public static final InstanceType<ScrollTransformedInstance> SCROLLING_TRANSFORMED = SimpleInstanceType.builder(ScrollTransformedInstance::new).cullShader(Create.asResource("instance/cull/scrolling_transformed.glsl")).vertexShader(Create.asResource("instance/scrolling_transformed.vert")).layout(LayoutBuilder.create().matrix("pose", FloatRepr.FLOAT, 4).vector("color", (ValueRepr)FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4).vector("light", (ValueRepr)IntegerRepr.SHORT, 2).vector("overlay", (ValueRepr)IntegerRepr.SHORT, 2).vector("speed", (ValueRepr)FloatRepr.FLOAT, 2).vector("diff", (ValueRepr)FloatRepr.FLOAT, 2).vector("scale", (ValueRepr)FloatRepr.FLOAT, 2).vector("offset", (ValueRepr)FloatRepr.FLOAT, 2).build()).writer((ptr, instance) -> {
        ExtraMemoryOps.putMatrix4f((long)ptr, (Matrix4fc)instance.pose);
        MemoryUtil.memPutByte((long)(ptr + 64L), (byte)instance.red);
        MemoryUtil.memPutByte((long)(ptr + 65L), (byte)instance.green);
        MemoryUtil.memPutByte((long)(ptr + 66L), (byte)instance.blue);
        MemoryUtil.memPutByte((long)(ptr + 67L), (byte)instance.alpha);
        ExtraMemoryOps.put2x16((long)(ptr + 68L), (int)instance.light);
        ExtraMemoryOps.put2x16((long)(ptr + 72L), (int)instance.overlay);
        MemoryUtil.memPutFloat((long)(ptr + 76L), (float)instance.speedU);
        MemoryUtil.memPutFloat((long)(ptr + 80L), (float)instance.speedV);
        MemoryUtil.memPutFloat((long)(ptr + 84L), (float)instance.diffU);
        MemoryUtil.memPutFloat((long)(ptr + 88L), (float)instance.diffV);
        MemoryUtil.memPutFloat((long)(ptr + 92L), (float)instance.scaleU);
        MemoryUtil.memPutFloat((long)(ptr + 96L), (float)instance.scaleV);
        MemoryUtil.memPutFloat((long)(ptr + 100L), (float)instance.offsetU);
        MemoryUtil.memPutFloat((long)(ptr + 104L), (float)instance.offsetV);
    }).build();
    public static final InstanceType<FluidInstance> FLUID = SimpleInstanceType.builder(FluidInstance::new).cullShader(Create.asResource("instance/cull/fluid.glsl")).vertexShader(Create.asResource("instance/fluid.vert")).layout(LayoutBuilder.create().matrix("pose", FloatRepr.FLOAT, 4).vector("color", (ValueRepr)FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4).vector("light", (ValueRepr)IntegerRepr.SHORT, 2).vector("overlay", (ValueRepr)IntegerRepr.SHORT, 2).scalar("progress", (ValueRepr)FloatRepr.FLOAT).scalar("vScale", (ValueRepr)FloatRepr.FLOAT).scalar("v0", (ValueRepr)FloatRepr.FLOAT).build()).writer((ptr, instance) -> {
        ExtraMemoryOps.putMatrix4f((long)ptr, (Matrix4fc)instance.pose);
        MemoryUtil.memPutByte((long)(ptr + 64L), (byte)instance.red);
        MemoryUtil.memPutByte((long)(ptr + 65L), (byte)instance.green);
        MemoryUtil.memPutByte((long)(ptr + 66L), (byte)instance.blue);
        MemoryUtil.memPutByte((long)(ptr + 67L), (byte)instance.alpha);
        ExtraMemoryOps.put2x16((long)(ptr + 68L), (int)instance.light);
        ExtraMemoryOps.put2x16((long)(ptr + 72L), (int)instance.overlay);
        MemoryUtil.memPutFloat((long)(ptr + 76L), (float)instance.progress);
        MemoryUtil.memPutFloat((long)(ptr + 80L), (float)instance.vScale);
        MemoryUtil.memPutFloat((long)(ptr + 84L), (float)instance.v0);
    }).build();

    public static void init() {
    }
}
