/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.catnip.outliner.LineOutline
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.physics_staff;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.createmod.catnip.outliner.LineOutline;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public static class PhysicsStaffClientHandler.PhysicsBeam {
    private static final float TARGET_SPACING = 1.5f;
    private static final int MIN_POINTS = 8;
    private final LineOutline line;
    private final double targetNodeRadius = 0.2;
    private final List<BeamNode> nodes = new ObjectArrayList();
    protected float extension;
    protected float previousExtension;
    protected float cubeScale;
    protected float previousCubeScale;
    private float intensity;
    private Vec3 start;
    private Vec3 end;
    private Vec3 previousStart;
    private Vec3 previousEnd;
    private Vec3 serverStart;
    private Vec3 serverEnd;
    private double length;
    private double currentNodeRadius = 0.0;

    public PhysicsStaffClientHandler.PhysicsBeam(Vec3 start, Vec3 end, double length) {
        this.start = start;
        this.previousStart = start;
        this.serverStart = start;
        this.end = end;
        this.previousEnd = end;
        this.serverEnd = end;
        this.intensity = 1.0f;
        this.line = new LineOutline();
        this.line.getParams().colored(0xFFFFFF).disableLineNormals().lineWidth(0.0375f);
        this.length = length;
        this.extension = 0.0f;
        this.update();
    }

    private void update() {
        double scaledLength = this.length / 1.5;
        double targetCount = 64.0 / (scaledLength + 8.0) + scaledLength;
        if (targetCount > 4096.0) {
            return;
        }
        this.currentNodeRadius = this.targetNodeRadius * Math.sqrt(scaledLength / targetCount);
        while ((double)this.nodes.size() < targetCount - 0.7) {
            this.nodes.add(new BeamNode());
        }
        while ((double)this.nodes.size() > targetCount + 0.7) {
            this.nodes.remove(0);
        }
        for (int i = 1; i < this.nodes.size() - 1; ++i) {
            this.nodes.get(i).update();
        }
        this.previousExtension = this.extension;
        this.previousCubeScale = this.cubeScale;
        this.extension = (double)this.intensity < 0.4 ? Mth.lerp((float)0.5f, (float)this.extension, (float)0.0f) : Mth.lerp((float)0.5f, (float)this.extension, (float)1.0f);
        this.cubeScale = this.extension;
    }

    private void render(Vec3 start, Vec3 end, PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, float pt) {
        Vec3 relative = end.subtract(start);
        this.length = relative.length();
        Vec3 lastPos = start;
        for (int i = 1; i < this.nodes.size(); ++i) {
            Vec3 offset = this.nodes.get((int)i).previousPosition.lerp(this.nodes.get((int)i).position, (double)pt);
            Vec3 currentPos = start.add(relative.scale((double)((float)i / (float)this.nodes.size())).add(offset.scale(this.currentNodeRadius)));
            this.line.set(lastPos, currentPos).render(ms, buffer, camera, pt);
            lastPos = currentPos;
        }
    }

    private static class BeamNode {
        Vec3 position = new Vec3(0.0, 0.0, 0.0);
        Vec3 previousPosition = new Vec3(0.0, 0.0, 0.0);

        private BeamNode() {
        }

        void update() {
            RandomSource random = Minecraft.getInstance().level.random;
            this.previousPosition = this.position;
            this.position = this.position.offsetRandom(random, 3.0f).scale(0.5);
        }
    }
}
