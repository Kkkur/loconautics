/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.outliner.LineOutline
 *  net.createmod.catnip.render.PonderRenderTypes
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector4f
 */
package dev.simulated_team.simulated.ponder.outliners;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.ponder.records.PonderLineRecord;
import net.createmod.catnip.outliner.LineOutline;
import net.createmod.catnip.render.PonderRenderTypes;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector4f;

public class LerpedLineOutline
extends LineOutline {
    Vector3d prevStart;
    Vector3d prevEnd;

    public LerpedLineOutline(PonderLineRecord initialLine) {
        this.prevStart = JOMLConversion.toJOML((Position)initialLine.startPos());
        this.prevEnd = JOMLConversion.toJOML((Position)initialLine.endPos());
    }

    public LerpedLineOutline(Vec3 initialPoint) {
        this.prevStart = JOMLConversion.toJOML((Position)initialPoint);
        this.prevEnd = JOMLConversion.toJOML((Position)initialPoint);
    }

    public void update(Vec3 prevStart, Vec3 prevEnd, Vec3 start, Vec3 end) {
        this.prevStart = JOMLConversion.toJOML((Position)prevStart);
        this.prevEnd = JOMLConversion.toJOML((Position)prevEnd);
        this.set(start, end);
    }

    public void render(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, float pt) {
        float width = this.params.getLineWidth();
        if (width == 0.0f) {
            return;
        }
        VertexConsumer consumer = buffer.getBuffer(PonderRenderTypes.outlineSolid());
        this.params.loadColor(this.colorTemp);
        Vector4f color = this.colorTemp;
        int lightmap = 0xF000F0;
        boolean disableLineNormals = false;
        this.renderInner(ms, consumer, camera, pt, width, color, 0xF000F0, false);
    }

    protected void renderInner(PoseStack ms, VertexConsumer consumer, Vec3 camera, float pt, float width, Vector4f color, int lightmap, boolean disableNormals) {
        this.bufferCuboidLine(ms, consumer, camera, LerpedLineOutline.interpolatePoint(this.prevStart, this.start, pt), LerpedLineOutline.interpolatePoint(this.prevEnd, this.end, pt), width, color, lightmap, disableNormals);
    }

    public static Vector3d interpolatePoint(Vector3d current, Vector3d target, float pt) {
        return new Vector3d(Mth.lerp((double)pt, (double)current.x, (double)target.x), Mth.lerp((double)pt, (double)current.y, (double)target.y), Mth.lerp((double)pt, (double)current.z, (double)target.z));
    }
}
