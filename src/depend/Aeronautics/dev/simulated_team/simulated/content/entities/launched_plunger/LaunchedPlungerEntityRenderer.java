/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.render.CachedBuffers
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.player.PlayerRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package dev.simulated_team.simulated.content.entities.launched_plunger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.entities.launched_plunger.LaunchedPlungerEntity;
import dev.simulated_team.simulated.content.items.plunger_launcher.PlungerLauncherItemRenderer;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.index.SimRenderTypes;
import dev.simulated_team.simulated.util.CatmulRomSpline;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class LaunchedPlungerEntityRenderer
extends EntityRenderer<LaunchedPlungerEntity> {
    private static final Quaternionf POSITIVE_Y = new Quaternionf().setAngleAxis(1.5707963267948966, 1.0, 0.0, 0.0);
    private static final Quaternionf NEGATIVE_Y = new Quaternionf().setAngleAxis(-1.5707963267948966, 1.0, 0.0, 0.0);
    private static final Matrix4f FRUSTUM = new Matrix4f();
    private static final Matrix4f PROJECTION = new Matrix4f();
    private static final Quaternionf ORIENTATION = new Quaternionf();
    private static final Quaternionf NEXT_ORIENTATION = new Quaternionf();
    private static final Vector3f POS = new Vector3f();
    private static final Vector3f NORMAL = new Vector3f();
    private static final Vector3f NEXT_NORMAL = new Vector3f();
    private static final Vector3f FACE_NORMAL = new Vector3f();
    private static final Vector3f TARGET = new Vector3f();
    private static final Vector3f SELF = new Vector3f();
    private static final List<Vec3> CABLE_POINTS = new ArrayList<Vec3>();
    private static final List<Vec3> PREV_CABLE_POINTS = new ArrayList<Vec3>();
    private static final BlockPos.MutableBlockPos LIGHT_POS = new BlockPos.MutableBlockPos();

    public LaunchedPlungerEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public static Vec3 getFirstPersonFocusPos(float pt) {
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        Camera camera = gameRenderer.getMainCamera();
        Vector3d focusPoint = new Vector3d((Vector3dc)PlungerLauncherItemRenderer.focusPos);
        Quaternionf orientation = camera.rotation();
        orientation.transformInverse(focusPoint);
        Vector4f v4 = new Vector4f((float)focusPoint.x, (float)focusPoint.y, (float)focusPoint.z, 1.0f);
        Matrix4f actualProjMat = gameRenderer.getProjectionMatrix(gameRenderer.getFov(camera, AnimationTickHolder.getPartialTicks(), true));
        actualProjMat.invert(new Matrix4f()).transform(v4);
        PlungerLauncherItemRenderer.itemProjMat.transform(v4);
        Vec3 cameraPosition = camera.getPosition();
        focusPoint.set((double)v4.x, (double)v4.y, (double)v4.z);
        orientation.transform(focusPoint);
        double fov = gameRenderer.getFov(camera, pt, true);
        focusPoint.mul(100.0 / fov);
        focusPoint.add(cameraPosition.x, cameraPosition.y, cameraPosition.z);
        return JOMLConversion.toMojang((Vector3dc)focusPoint);
    }

    /*
     * Unable to fully structure code
     */
    public void render(LaunchedPlungerEntity entity, float f, float pt, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        block19: {
            block20: {
                block18: {
                    super.render((Entity)entity, f, pt, poseStack, multiBufferSource, light);
                    other = entity.getOther();
                    selfNormal = Vec3.ZERO;
                    perpendicularNormal = Vec3.ZERO;
                    dir = entity.getData(LaunchedPlungerEntity.PLUNGED_DIRECTION);
                    if (entity.isPlunged()) {
                        selfNormal = Vec3.atLowerCornerOf((Vec3i)dir.getNormal());
                        perpendicularNormal = dir.getAxis().isHorizontal() ? Vec3.atLowerCornerOf((Vec3i)Direction.UP.getNormal()) : Vec3.atLowerCornerOf((Vec3i)Direction.NORTH.getNormal());
                    } else {
                        selfNormal = entity.calculateViewVector(-Mth.lerp((float)pt, (float)entity.xRotO, (float)entity.getXRot()), -Mth.lerp((float)pt, (float)entity.yRotO, (float)entity.getYRot())).reverse();
                        perpendicularNormal = entity.calculateViewVector(-Mth.lerp((float)pt, (float)entity.xRotO, (float)entity.getXRot()), -Mth.lerp((float)pt, (float)entity.yRotO, (float)entity.getYRot()) - 90.0f).reverse();
                    }
                    poseStack.pushPose();
                    oldPos = new Vec3(entity.xo, entity.yo, entity.zo);
                    newPos = entity.position();
                    scalingFactor = 0.6f;
                    subLevel = Sable.HELPER.getContainingClient((Position)newPos);
                    if (subLevel != null) {
                        clientSubLevel = subLevel;
                        clientPos = clientSubLevel.renderPose(pt);
                        newPos = clientPos.transformPosition(newPos);
                        selfNormal = clientPos.transformNormal(selfNormal);
                        perpendicularNormal = clientPos.transformNormal(perpendicularNormal);
                        quaterniondc = clientPos.orientation();
                        poseStack.mulPose(new Quaternionf(quaterniondc.x(), quaterniondc.y(), quaterniondc.z(), quaterniondc.w()).conjugate());
                    }
                    if ((oldSubLevel = Sable.HELPER.getContainingClient((Position)oldPos)) != null) {
                        clientSubLevel = oldSubLevel;
                        clientPos = clientSubLevel.renderPose(pt);
                        oldPos = clientPos.transformPosition(oldPos);
                    }
                    renderPos = oldPos.lerp(newPos, (double)pt);
                    pos = renderPos.add(selfNormal.scale(0.6000000238418579));
                    poseStack.translate(-renderPos.x, -renderPos.y, -renderPos.z);
                    if (other == null) break block18;
                    otherNormal = Vec3.ZERO;
                    if (other.isPlunged()) {
                        otherDir = other.getData(LaunchedPlungerEntity.PLUNGED_DIRECTION);
                        otherNormal = Vec3.atLowerCornerOf((Vec3i)otherDir.getNormal());
                    } else {
                        otherNormal = other.calculateViewVector(-Mth.lerp((float)pt, (float)other.xRotO, (float)other.getXRot()), -Mth.lerp((float)pt, (float)other.yRotO, (float)other.getYRot())).reverse();
                    }
                    targetOldPos = new Vec3(other.xo, other.yo, other.zo);
                    targetNewPos = other.position();
                    if (other.isRemoved()) {
                        targetNewPos = targetOldPos = (Vec3)entity.getEntityData().get(LaunchedPlungerEntity.TARGET_POS);
                    }
                    if ((targetSublevel = Sable.HELPER.getContainingClient((Position)targetNewPos)) != null) {
                        targetNewPos = targetSublevel.renderPose(pt).transformPosition(targetNewPos);
                        otherNormal = targetSublevel.renderPose(pt).transformNormal(otherNormal);
                    }
                    if ((targetOldSublevel = Sable.HELPER.getContainingClient((Position)other.getPosition(pt))) != null) {
                        targetOldPos = targetOldSublevel.renderPose(pt).transformPosition(targetOldPos);
                    }
                    target = targetOldPos.lerp(targetNewPos, (double)pt).add(otherNormal.scale(0.6000000238418579));
                    break block19;
                }
                owner = entity.getOwner();
                if (!entity.getData(LaunchedPlungerEntity.OTHER_PLUNGER).isEmpty() || owner != Minecraft.getInstance().player || !Minecraft.getInstance().options.getCameraType().isFirstPerson()) break block20;
                target = LaunchedPlungerEntityRenderer.getFirstPersonFocusPos(pt);
                break block19;
            }
            if (!(owner instanceof AbstractClientPlayer)) ** GOTO lbl-1000
            player = (AbstractClientPlayer)owner;
            if (entity.getData(LaunchedPlungerEntity.OTHER_PLUNGER).isEmpty()) {
                playerrenderer = (PlayerRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)player);
                headYDirection = Mth.lerp((float)pt, (float)player.yHeadRotO, (float)player.yHeadRot);
                bodyDifference = Math.abs(headYDirection - player.getPreciseBodyRotation(pt)) / 50.0f;
                headXDirection = Mth.lerp((float)pt, (float)player.xRotO, (float)player.getXRot());
                lookDelta = Math.abs(Mth.map((float)headXDirection, (float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f));
                headYDirection = Mth.lerp((float)lookDelta, (float)headYDirection, (float)player.getPreciseBodyRotation(pt));
                viewDirection = player.calculateViewVector(headXDirection, headYDirection);
                handDirection = player.calculateViewVector(0.0f, headYDirection + 90.0f);
                target = player.getPosition(pt).add(0.0, 1.28, 0.0).add(viewDirection.scale(0.875)).add(handDirection.scale((double)(Math.abs(Mth.map((float)headXDirection, (float)90.0f, (float)0.0f, (float)0.325f, (float)0.0f)) * 1.0f)));
            } else lbl-1000:
            // 2 sources

            {
                target = Vec3.ZERO;
            }
        }
        firstRotation = new Vector3f();
        secondRotation = new Vector3f();
        finalRotation = new Vector3f();
        poseStack.popPose();
        if ((entity.getData(LaunchedPlungerEntity.IS_FIRST).booleanValue() || other == null || other.isRemoved()) && !target.equals((Object)Vec3.ZERO)) {
            renderTime = (float)entity.tickCount + pt + entity.getAnimationOffset();
            points = new ObjectArrayList();
            start = pos;
            end = target;
            toTarget = end.subtract(start);
            normalizedScalar = toTarget.normalize();
            length = (float)renderPos.distanceTo(target);
            points.add(start);
            if ((double)length < 1000.0) {
                for (j = 0.01f; j < length; j += 0.5f) {
                    finalRotation.set(0.0f, 0.0f, 0.0f);
                    delta = j / length;
                    point = start.add(toTarget.scale((double)delta));
                    firstRotation.set(Math.cos(renderTime / 10.0f + j) * (1.0 - Math.abs(normalizedScalar.x)), Math.cos(renderTime / 10.0f + j) * (1.0 - Math.abs(normalizedScalar.y)), Math.cos(renderTime / 10.0f + j / 2.0f) * (1.0 - Math.abs(normalizedScalar.z)));
                    secondRotation.set(Math.sin(renderTime / 10.0f + j / 4.0f) * (1.0 - Math.abs(normalizedScalar.x)) * 2.0, Math.sin(renderTime / 10.0f + j / 4.0f) * (1.0 - Math.abs(normalizedScalar.y)), Math.sin(renderTime / 10.0f + j / 4.0f) * (1.0 - Math.abs(normalizedScalar.z)));
                    finalRotation.add((Vector3fc)firstRotation);
                    finalRotation.add((Vector3fc)secondRotation);
                    finalRotation.mul(Math.max(0.0f, 1.0f - ((float)entity.tickCount + pt) / 40.0f - ((float)entity.getPlungedTime() + (entity.getPlungedTime() > 0 ? pt : 0.0f)) / 8.0f));
                    finalRotation.mul((float)(1.0 - Math.pow(2.0f * delta - 1.0f, 2.0)));
                    points.add(point.subtract((double)finalRotation.x, (double)finalRotation.y, (double)finalRotation.z));
                }
            }
            points.add(start.add(toTarget));
            points.add(start.add(toTarget));
            LaunchedPlungerEntityRenderer.renderRope((List<Vec3>)points, multiBufferSource, (BlockAndTintGetter)Minecraft.getInstance().level, poseStack);
        }
        distanceIncludingSublevels = pos.distanceTo(target);
        stack = TransformStack.of((PoseStack)poseStack);
        poseStack.pushPose();
        if (entity.isPlunged()) {
            stack.rotate((Quaternionfc)dir.getRotation());
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp((float)pt, (float)entity.yRotO, (float)entity.getYRot()) - 90.0f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp((float)pt, (float)entity.xRotO, (float)entity.getXRot())));
            stack.rotateZDegrees(90.0f);
        }
        stack.rotateXDegrees(-90.0f);
        stack.scale(1.75f, 1.75f, 1.75f);
        stack.translate(0.0f, 0.0f, 0.15625f);
        vb = multiBufferSource.getBuffer(RenderType.solid());
        body = CachedBuffers.partial((PartialModel)SimPartialModels.LAUNCHED_PLUNGER_BODY, (BlockState)Blocks.AIR.defaultBlockState());
        spool = CachedBuffers.partial((PartialModel)SimPartialModels.LAUNCHED_PLUNGER_SPOOL, (BlockState)Blocks.AIR.defaultBlockState());
        joint = CachedBuffers.partial((PartialModel)SimPartialModels.LAUNCHED_PLUNGER_JOINT, (BlockState)Blocks.AIR.defaultBlockState());
        stack.rotateZDegrees(90.0f);
        body.light(light).renderInto(poseStack, vb);
        LaunchedPlungerEntityRenderer.FACE_NORMAL.set(selfNormal.x, selfNormal.y, selfNormal.z);
        LaunchedPlungerEntityRenderer.SELF.set(pos.x, pos.y, pos.z);
        LaunchedPlungerEntityRenderer.TARGET.set(target.x, target.y, target.z);
        LaunchedPlungerEntityRenderer.TARGET.add((Vector3fc)LaunchedPlungerEntityRenderer.SELF.mul(-1.0f)).normalize();
        LaunchedPlungerEntityRenderer.POS.set(perpendicularNormal.x, perpendicularNormal.y, perpendicularNormal.z);
        angle = 0.0f;
        if (entity.getData(LaunchedPlungerEntity.IS_PLUNGED).booleanValue()) {
            angle = (float)((double)LaunchedPlungerEntityRenderer.POS.angleSigned((Vector3fc)LaunchedPlungerEntityRenderer.TARGET, (Vector3fc)LaunchedPlungerEntityRenderer.FACE_NORMAL) + 1.5707963267948966);
        }
        if (Float.isNaN(angle)) {
            angle = 0.0f;
        }
        poseStack.pushPose();
        stack.rotateZDegrees((float)Math.toDegrees(angle));
        joint.light(light).renderInto(poseStack, vb);
        stack.translate(0.0f, 0.0f, 0.1875f);
        stack.rotateXDegrees((float)distanceIncludingSublevels * 90.0f * 2.6f);
        spool.light(light).renderInto(poseStack, vb);
        poseStack.popPose();
        poseStack.popPose();
    }

    public static void renderRope(List<Vec3> positions, MultiBufferSource multiBufferSource, BlockAndTintGetter level, PoseStack poseStack) {
        Vec3 first = positions.getFirst();
        Vector3d origin = new Vector3d();
        Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        RenderType renderType = SimRenderTypes.rope();
        VertexConsumer builder = multiBufferSource.getBuffer(renderType);
        CABLE_POINTS.clear();
        for (Vec3 position : positions) {
            CABLE_POINTS.add(position.subtract(cameraPosition));
        }
        List<Vec3> splinePoints = CatmulRomSpline.generateSpline(CABLE_POINTS, 4);
        int color = -1;
        float constantRadius = 0.0625f;
        float u = 0.125f;
        float v = 0.0f;
        for (int i = 0; i < splinePoints.size() - 1; ++i) {
            float delta = (float)i / (float)(splinePoints.size() - 1);
            float nextDelta = (float)(i + 1) / (float)(splinePoints.size() - 1);
            float cableRadius = 0.0625f - 0.001f * delta;
            float nextCableRadius = 0.0625f - 0.001f * nextDelta;
            Vec3 point = splinePoints.get(i);
            Vec3 nextPoint = splinePoints.get(i + 1);
            double x = point.x;
            double y = point.y;
            double z = point.z;
            double nextX = nextPoint.x;
            double nextY = nextPoint.y;
            double nextZ = nextPoint.z;
            if (i < splinePoints.size() - 2) {
                LaunchedPlungerEntityRenderer.calculateOrientation(NEXT_ORIENTATION, nextX, nextY, nextZ, splinePoints.get(i + 2));
            } else {
                NEXT_ORIENTATION.set((Quaternionfc)ORIENTATION);
            }
            int lightStart = LevelRenderer.getLightColor((BlockAndTintGetter)level, (BlockPos)LIGHT_POS.set(x + cameraPosition.x, y + cameraPosition.y, z + cameraPosition.z));
            int lightEnd = LevelRenderer.getLightColor((BlockAndTintGetter)level, (BlockPos)LIGHT_POS.set(nextX + cameraPosition.x, nextY + cameraPosition.y, nextZ + cameraPosition.z));
            double length = Math.sqrt((nextX - x) * (nextX - x) + (nextY - y) * (nextY - y) + (nextZ - z) * (nextZ - z));
            float nextV = v + (float)(length * 1.0625);
            ORIENTATION.transform(NORMAL.set(0.0f, -1.0f, 0.0f));
            NEXT_ORIENTATION.transform(NEXT_NORMAL.set(0.0f, -1.0f, 0.0f));
            NEXT_ORIENTATION.transform(POS.set(-nextCableRadius, -nextCableRadius, 0.0f));
            builder.addVertex((float)(nextX - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(nextY - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(nextZ - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.0f, nextV).setLight(lightEnd).setNormal(LaunchedPlungerEntityRenderer.NEXT_NORMAL.x, LaunchedPlungerEntityRenderer.NEXT_NORMAL.y, LaunchedPlungerEntityRenderer.NEXT_NORMAL.z);
            ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0.0f));
            builder.addVertex((float)(x - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(y - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(z - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.0f, v).setLight(lightStart).setNormal(LaunchedPlungerEntityRenderer.NORMAL.x, LaunchedPlungerEntityRenderer.NORMAL.y, LaunchedPlungerEntityRenderer.NORMAL.z);
            ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0.0f));
            builder.addVertex((float)(x - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(y - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(z - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.125f, v).setLight(lightStart).setNormal(LaunchedPlungerEntityRenderer.NORMAL.x, LaunchedPlungerEntityRenderer.NORMAL.y, LaunchedPlungerEntityRenderer.NORMAL.z);
            NEXT_ORIENTATION.transform(POS.set(nextCableRadius, -nextCableRadius, 0.0f));
            builder.addVertex((float)(nextX - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(nextY - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(nextZ - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.125f, nextV).setLight(lightEnd).setNormal(LaunchedPlungerEntityRenderer.NEXT_NORMAL.x, LaunchedPlungerEntityRenderer.NEXT_NORMAL.y, LaunchedPlungerEntityRenderer.NEXT_NORMAL.z);
            ORIENTATION.transform(NORMAL.set(0.0f, 1.0f, 0.0f));
            NEXT_ORIENTATION.transform(NEXT_NORMAL.set(0.0f, 1.0f, 0.0f));
            ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0.0f));
            builder.addVertex((float)(x - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(y - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(z - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.0f, v).setLight(lightStart).setNormal(LaunchedPlungerEntityRenderer.NORMAL.x, LaunchedPlungerEntityRenderer.NORMAL.y, LaunchedPlungerEntityRenderer.NORMAL.z);
            NEXT_ORIENTATION.transform(POS.set(-nextCableRadius, nextCableRadius, 0.0f));
            builder.addVertex((float)(nextX - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(nextY - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(nextZ - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.0f, nextV).setLight(lightEnd).setNormal(LaunchedPlungerEntityRenderer.NEXT_NORMAL.x, LaunchedPlungerEntityRenderer.NEXT_NORMAL.y, LaunchedPlungerEntityRenderer.NEXT_NORMAL.z);
            NEXT_ORIENTATION.transform(POS.set(nextCableRadius, nextCableRadius, 0.0f));
            builder.addVertex((float)(nextX - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(nextY - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(nextZ - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.125f, nextV).setLight(lightEnd).setNormal(LaunchedPlungerEntityRenderer.NEXT_NORMAL.x, LaunchedPlungerEntityRenderer.NEXT_NORMAL.y, LaunchedPlungerEntityRenderer.NEXT_NORMAL.z);
            ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0.0f));
            builder.addVertex((float)(x - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(y - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(z - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.125f, v).setLight(lightStart).setNormal(LaunchedPlungerEntityRenderer.NORMAL.x, LaunchedPlungerEntityRenderer.NORMAL.y, LaunchedPlungerEntityRenderer.NORMAL.z);
            ORIENTATION.transform(NORMAL.set(-1.0f, 0.0f, 0.0f));
            NEXT_ORIENTATION.transform(NEXT_NORMAL.set(-1.0f, 0.0f, 0.0f));
            NEXT_ORIENTATION.transform(POS.set(-nextCableRadius, -nextCableRadius, 0.0f));
            builder.addVertex((float)(nextX - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(nextY - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(nextZ - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.125f, nextV).setLight(lightEnd).setNormal(LaunchedPlungerEntityRenderer.NEXT_NORMAL.x, LaunchedPlungerEntityRenderer.NEXT_NORMAL.y, LaunchedPlungerEntityRenderer.NEXT_NORMAL.z);
            NEXT_ORIENTATION.transform(POS.set(-nextCableRadius, nextCableRadius, 0.0f));
            builder.addVertex((float)(nextX - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(nextY - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(nextZ - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.0f, nextV).setLight(lightEnd).setNormal(LaunchedPlungerEntityRenderer.NEXT_NORMAL.x, LaunchedPlungerEntityRenderer.NEXT_NORMAL.y, LaunchedPlungerEntityRenderer.NEXT_NORMAL.z);
            ORIENTATION.transform(POS.set(-cableRadius, cableRadius, 0.0f));
            builder.addVertex((float)(x - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(y - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(z - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.0f, v).setLight(lightStart).setNormal(LaunchedPlungerEntityRenderer.NORMAL.x, LaunchedPlungerEntityRenderer.NORMAL.y, LaunchedPlungerEntityRenderer.NORMAL.z);
            ORIENTATION.transform(POS.set(-cableRadius, -cableRadius, 0.0f));
            builder.addVertex((float)(x - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(y - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(z - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.125f, v).setLight(lightStart).setNormal(LaunchedPlungerEntityRenderer.NORMAL.x, LaunchedPlungerEntityRenderer.NORMAL.y, LaunchedPlungerEntityRenderer.NORMAL.z);
            ORIENTATION.transform(NORMAL.set(1.0f, 0.0f, 0.0f));
            NEXT_ORIENTATION.transform(NEXT_NORMAL.set(1.0f, 0.0f, 0.0f));
            ORIENTATION.transform(POS.set(cableRadius, -cableRadius, 0.0f));
            builder.addVertex((float)(x - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(y - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(z - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.125f, v).setLight(lightStart).setNormal(LaunchedPlungerEntityRenderer.NORMAL.x, LaunchedPlungerEntityRenderer.NORMAL.y, LaunchedPlungerEntityRenderer.NORMAL.z);
            ORIENTATION.transform(POS.set(cableRadius, cableRadius, 0.0f));
            builder.addVertex((float)(x - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(y - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(z - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.0f, v).setLight(lightStart).setNormal(LaunchedPlungerEntityRenderer.NORMAL.x, LaunchedPlungerEntityRenderer.NORMAL.y, LaunchedPlungerEntityRenderer.NORMAL.z);
            NEXT_ORIENTATION.transform(POS.set(nextCableRadius, nextCableRadius, 0.0f));
            builder.addVertex((float)(nextX - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(nextY - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(nextZ - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.0f, nextV).setLight(lightEnd).setNormal(LaunchedPlungerEntityRenderer.NEXT_NORMAL.x, LaunchedPlungerEntityRenderer.NEXT_NORMAL.y, LaunchedPlungerEntityRenderer.NEXT_NORMAL.z);
            NEXT_ORIENTATION.transform(POS.set(nextCableRadius, -nextCableRadius, 0.0f));
            builder.addVertex((float)(nextX - origin.x() + (double)LaunchedPlungerEntityRenderer.POS.x), (float)(nextY - origin.y() + (double)LaunchedPlungerEntityRenderer.POS.y), (float)(nextZ - origin.z() + (double)LaunchedPlungerEntityRenderer.POS.z)).setColor(-1).setUv(0.125f, nextV).setLight(lightEnd).setNormal(LaunchedPlungerEntityRenderer.NEXT_NORMAL.x, LaunchedPlungerEntityRenderer.NEXT_NORMAL.y, LaunchedPlungerEntityRenderer.NEXT_NORMAL.z);
            ORIENTATION.set((Quaternionfc)NEXT_ORIENTATION);
            v = nextV;
        }
    }

    private static void calculateOrientation(Quaternionf store, double x, double y, double z, Vec3 nextPoint) {
        double dx = nextPoint.x - x;
        double dy = nextPoint.y - y;
        double dz = nextPoint.z - z;
        float factor = 0.0f;
        store.identity().rotateAxis((float)Math.atan2(dx, dz), 0.0f, 1.0f, 0.0f).rotateAxis((float)(Math.acos(dy / Math.sqrt(dx * dx + dy * dy + dz * dz)) - 1.5707963267948966), 1.0f, 0.0f, 0.0f).slerp((Quaternionfc)(dy < 0.0 ? POSITIVE_Y : NEGATIVE_Y), 0.0f);
    }

    public ResourceLocation getTextureLocation(LaunchedPlungerEntity entity) {
        return ResourceLocation.withDefaultNamespace((String)"missing");
    }

    public boolean shouldRender(LaunchedPlungerEntity entity, Frustum frustum, double d, double e, double f) {
        return true;
    }
}
