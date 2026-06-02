/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.math.OrientedBoundingBox3d
 *  dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3d
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.spring;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlock;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlockEntity;
import dev.simulated_team.simulated.index.SimRenderTypes;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.SimMathUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SpringRenderer
extends SmartBlockEntityRenderer<SpringBlockEntity> {
    private final Vector3d controlPointA = new Vector3d();
    private final Vector3d controlPointB = new Vector3d();
    private final Vector3d segmentALerp = new Vector3d();
    private final Vector3d segmentBLerp = new Vector3d();
    private final Vector3d segmentCLerp = new Vector3d();
    private final Vector3d startUp = new Vector3d();
    private final Vector3d endUp = new Vector3d();
    private final Vector3d startLeft = new Vector3d();
    private final Vector3d endLeft = new Vector3d();
    private final Vector3d normalizedNormal = new Vector3d();
    private final Vector3d vertex = new Vector3d();

    public SpringRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    private static int getStressColor(SpringBlockEntity be, float partialTicks, Vector3d otherCenter, Vector3dc center, Minecraft minecraft) {
        double distance = otherCenter.distance(center);
        double snapDistance = be.getSnappingDistance();
        double flashingStartExtension = Mth.lerp((double)0.7, (double)(be.getRenderLength(partialTicks) - 0.75), (double)snapDistance);
        float stressAlpha = 0.0f;
        if (distance > flashingStartExtension) {
            double renderTime = (float)minecraft.player.tickCount + partialTicks;
            stressAlpha = Mth.clamp((float)((float)((distance - flashingStartExtension) / (snapDistance - flashingStartExtension))), (float)0.0f, (float)1.0f) * 0.3f;
            stressAlpha *= Mth.lerp((float)0.25f, (float)((float)Math.sin(renderTime / 3.0) * 0.5f + 0.5f), (float)1.0f);
        }
        int color = SimColors.STRESSED_RED & 0xFFFFFF | (int)(stressAlpha * 255.0f) << 24;
        return color;
    }

    protected void renderSafe(SpringBlockEntity be, float partialTicks, PoseStack ps, MultiBufferSource bufferSource, int light, int overlay) {
        Pose3dc otherRenderPose;
        super.renderSafe((SmartBlockEntity)be, partialTicks, ps, bufferSource, light, overlay);
        if (!be.isController()) {
            return;
        }
        SpringBlockEntity other = be.getPairedSpring();
        if (other == null) {
            return;
        }
        BlockState state = be.getBlockState();
        SpringBlock.Size size = (SpringBlock.Size)((Object)state.getValue(SpringBlock.SIZE));
        String name = (String)(size == SpringBlock.Size.MEDIUM ? "" : size.getSerializedName() + "_") + "spring";
        VertexConsumer buffer = bufferSource.getBuffer(SimRenderTypes.spring(Simulated.path("textures/block/spring/" + name + ".png")));
        ps.pushPose();
        Minecraft minecraft = Minecraft.getInstance();
        ClientSubLevelContainer container = SubLevelContainer.getContainer((ClientLevel)minecraft.level);
        assert (container != null);
        UUID otherSubLevelID = be.getPartnerSubLevelID();
        ClientSubLevel otherSubLevel = otherSubLevelID != null ? (ClientSubLevel)container.getSubLevel(otherSubLevelID) : null;
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((BlockEntity)be);
        BlockPos blockPos = be.getBlockPos();
        Vector3d center = be.getCenter();
        Vector3d otherCenter = other.getCenter();
        Direction facing = (Direction)state.getValue((Property)SpringBlock.FACING);
        Direction otherFacing = (Direction)other.getBlockState().getValue((Property)SpringBlock.FACING);
        Vector3d normalA = JOMLConversion.atLowerCornerOf((Vec3i)facing.getNormal());
        Vector3d normalB = JOMLConversion.atLowerCornerOf((Vec3i)otherFacing.getNormal());
        ps.translate(center.x() - (double)blockPos.getX(), center.y() - (double)blockPos.getY(), center.z() - (double)blockPos.getZ());
        double PI2 = 1.5707963267948966;
        double PI4 = 0.7853981633974483;
        Pose3dc renderPose = subLevel != null ? subLevel.renderPose() : null;
        Pose3dc pose3dc = otherRenderPose = otherSubLevel != null ? otherSubLevel.renderPose() : null;
        if (otherRenderPose != null) {
            otherRenderPose.transformNormal(normalB);
            otherRenderPose.transformPosition(otherCenter);
        }
        if (renderPose != null) {
            renderPose.transformNormalInverse(normalB);
            renderPose.transformPositionInverse(otherCenter);
        }
        int color = SpringRenderer.getStressColor(be, partialTicks, otherCenter, (Vector3dc)center, minecraft);
        List<SplinePoint> splinePoints = this.generateSpline(JOMLConversion.ZERO, (Vector3dc)otherCenter.sub((Vector3dc)center, new Vector3d()), (Vector3dc)normalA, (Vector3dc)normalB, center.distance((Vector3dc)otherCenter) / 5.0 + 0.25);
        int totalPoints = splinePoints.size();
        Vector3d pointNormal = new Vector3d();
        Vector3d startUpDir = JOMLConversion.toJOML((Position)this.getUpDirection(be, (Vector3dc)otherCenter.sub((Vector3dc)center, new Vector3d())));
        pointNormal.set(splinePoints.getFirst().normal);
        Matrix3d matrix = new Matrix3d((Vector3dc)startUpDir, (Vector3dc)pointNormal, (Vector3dc)startUpDir.cross((Vector3dc)pointNormal, new Vector3d()));
        double totalSpringLength = 0.0;
        for (int i = 0; i < totalPoints - 1; ++i) {
            SplinePoint point = splinePoints.get(i);
            SplinePoint nextPoint = splinePoints.get(i + 1);
            totalSpringLength += point.point.distance(nextPoint.point);
            matrix.rotateLocal((Quaternionfc)SimMathUtils.getQuaternionfFromVectorRotation(point.normal, nextPoint.normal));
        }
        Quaterniond orientation = new Quaterniond();
        Quaterniondc orientation1 = renderPose != null ? renderPose.orientation() : JOMLConversion.QUAT_IDENTITY;
        Quaterniondc orientation2 = otherRenderPose != null ? otherRenderPose.orientation() : JOMLConversion.QUAT_IDENTITY;
        Quaterniond blockOrientation1 = new Quaterniond((Quaternionfc)facing.getRotation());
        Quaterniond blockOrientation2 = new Quaterniond((Quaternionfc)otherFacing.getRotation());
        blockOrientation2.premul(orientation2).premul((Quaterniondc)orientation1.conjugate(new Quaterniond()));
        Quaterniond relativeBlockOrientation = new Quaterniond((Quaterniondc)blockOrientation1).div((Quaterniondc)blockOrientation2);
        orientation.mul((Quaterniondc)new Quaterniond((Quaterniondc)relativeBlockOrientation));
        orientation.mul((Quaterniondc)matrix.getNormalizedRotation(new Quaterniond()));
        Vector3d vector3d = new Vector3d(orientation.x(), orientation.y(), orientation.z());
        if (Math.abs(OrientedBoundingBox3d.UP.dot((Vector3dc)vector3d)) < 1.0E-5) {
            orientation.rotateLocalX(Math.PI);
        }
        double d = OrientedBoundingBox3d.UP.dot((Vector3dc)new Vector3d(orientation.x(), orientation.y(), orientation.z()));
        double deg = 2.0 * Math.atan2(-d, orientation.w());
        double twist = Math.floor((deg + 0.7853981633974483) / 1.5707963267948966) * 1.5707963267948966 - deg;
        float uvScale = (float)((be.getRenderLength(partialTicks) - 0.75) / totalSpringLength);
        double runningSpringLength = 0.0;
        matrix.set((Vector3dc)startUpDir, (Vector3dc)pointNormal, (Vector3dc)startUpDir.cross((Vector3dc)pointNormal, new Vector3d()));
        for (int i = 0; i < totalPoints - 1; ++i) {
            SplinePoint point = splinePoints.get(i);
            SplinePoint nextPoint = splinePoints.get(i + 1);
            Vector3d upDir = matrix.getColumn(0, new Vector3d());
            matrix.rotateLocal((Quaternionfc)SimMathUtils.getQuaternionfFromVectorRotation(point.normal, nextPoint.normal));
            matrix.rotateY(-twist / (double)(totalPoints - 1));
            Vector3d nextUpDir = matrix.getColumn(0, new Vector3d());
            double length = point.point.distance(nextPoint.point);
            float width = switch (size) {
                default -> throw new MatchException(null, null);
                case SpringBlock.Size.SMALL -> 6.0f;
                case SpringBlock.Size.MEDIUM -> 8.0f;
                case SpringBlock.Size.LARGE -> 10.0f;
            };
            float textureWidth = switch (size) {
                default -> throw new MatchException(null, null);
                case SpringBlock.Size.SMALL -> 16.0f;
                case SpringBlock.Size.MEDIUM -> 16.0f;
                case SpringBlock.Size.LARGE -> 32.0f;
            };
            this.renderSegment(ps, point.normal, nextPoint.normal, (Vector3dc)upDir, (Vector3dc)nextUpDir, point.point, nextPoint.point, false, (float)runningSpringLength * uvScale, (float)(runningSpringLength + length) * uvScale, light, color, buffer, width, textureWidth);
            this.renderSegment(ps, (Vector3dc)point.normal.negate(new Vector3d()), (Vector3dc)nextPoint.normal.negate(new Vector3d()), (Vector3dc)upDir.negate(new Vector3d()), (Vector3dc)nextUpDir.negate(new Vector3d()), point.point, nextPoint.point, true, 0.0f - (float)runningSpringLength * uvScale, 0.0f - (float)(runningSpringLength + length) * uvScale, light, color, buffer, width, textureWidth);
            runningSpringLength += length;
        }
        ps.popPose();
    }

    private Vec3 getUpDirection(SpringBlockEntity be, Vector3dc directionToSpring) {
        Direction facing = (Direction)be.getBlockState().getValue((Property)SpringBlock.FACING);
        Vec3 normal = Vec3.atLowerCornerOf((Vec3i)facing.getNormal());
        double dot = directionToSpring.dot(normal.x, normal.y, normal.z);
        Vector3d dir = directionToSpring.sub(normal.x * dot, normal.y * dot, normal.z * dot, new Vector3d());
        if (dir.lengthSquared() < 1.0E-6) {
            return facing.getAxis().isHorizontal() ? new Vec3(0.0, 1.0, 0.0) : new Vec3(0.0, 0.0, -1.0);
        }
        return Vec3.atLowerCornerOf((Vec3i)Direction.getNearest((double)dir.x, (double)dir.y, (double)dir.z).getOpposite().getNormal());
    }

    private List<SplinePoint> generateSpline(Vector3dc pointA, Vector3dc pointB, Vector3dc normalA, Vector3dc normalB, double controlPointLength) {
        ObjectArrayList list = new ObjectArrayList();
        double influence = controlPointLength;
        pointA.fma(influence, normalA, this.controlPointA);
        pointB.fma(influence, normalB, this.controlPointB);
        double len = pointA.distance(pointB);
        int initialPointCount = Mth.clamp((int)Mth.ceil((double)len), (int)5, (int)8);
        for (int i = 0; i <= initialPointCount; ++i) {
            double t = (double)i / (double)initialPointCount;
            pointA.lerp((Vector3dc)this.controlPointA, t, this.segmentALerp);
            this.controlPointA.lerp((Vector3dc)this.controlPointB, t, this.segmentBLerp);
            this.controlPointB.lerp(pointB, t, this.segmentCLerp);
            Vector3d point = new Vector3d((Vector3dc)this.segmentALerp.lerp((Vector3dc)this.segmentBLerp, t).lerp((Vector3dc)this.segmentBLerp.lerp((Vector3dc)this.segmentCLerp, t), t));
            Vector3d normal = new Vector3d();
            if (list.isEmpty()) {
                normal.set(normalA);
            } else if (list.size() == initialPointCount) {
                normal.set(normalB).negate();
            } else {
                point.sub(((SplinePoint)list.get((int)(list.size() - 1))).point, normal).normalize();
            }
            list.add(new SplinePoint((Vector3dc)point, (Vector3dc)normal));
        }
        return list;
    }

    private void renderSegment(PoseStack ms, Vector3dc startDirection, Vector3dc endDirection, Vector3dc inputStartUp, Vector3dc inputEndUp, Vector3dc startPos, Vector3dc endPos, boolean second, float uvStart, float uvEnd, int light, int color, VertexConsumer a, float width, float textureWidth) {
        inputStartUp.cross(startDirection, this.startLeft).normalize();
        inputEndUp.cross(endDirection, this.endLeft).normalize();
        float texW = width / textureWidth;
        double scale = (double)width / 16.0 / 2.0;
        this.startLeft.mul(scale);
        inputStartUp.mul(scale, this.startUp);
        this.endLeft.mul(scale);
        inputEndUp.mul(scale, this.endUp);
        Vector3d startDown = this.startUp.negate(new Vector3d());
        Vector3d endDown = this.endUp.negate(new Vector3d());
        Vector3d startRight = this.startLeft.negate(new Vector3d());
        Vector3d endRight = this.endLeft.negate(new Vector3d());
        float uvScale = 16.0f / textureWidth;
        float uvXOffset = second ? width / textureWidth : 0.0f;
        this.vert(ms, a, (Vector3dc)startPos.add((Vector3dc)this.startLeft, this.vertex).sub((Vector3dc)this.startUp), color, 0.0f + uvXOffset, uvStart * uvScale, (Vector3dc)startDown, light);
        this.vert(ms, a, (Vector3dc)endPos.add((Vector3dc)this.endLeft, this.vertex).sub((Vector3dc)this.endUp), color, 0.0f + uvXOffset, uvEnd * uvScale, (Vector3dc)endDown, light);
        this.vert(ms, a, (Vector3dc)endPos.sub((Vector3dc)this.endLeft, this.vertex).sub((Vector3dc)this.endUp), color, texW + uvXOffset, uvEnd * uvScale, (Vector3dc)endDown, light);
        this.vert(ms, a, (Vector3dc)startPos.sub((Vector3dc)this.startLeft, this.vertex).sub((Vector3dc)this.startUp), color, texW + uvXOffset, uvStart * uvScale, (Vector3dc)startDown, light);
        this.vert(ms, a, (Vector3dc)startPos.sub((Vector3dc)this.startLeft, this.vertex).add((Vector3dc)this.startUp), color, 0.0f + uvXOffset, uvStart * uvScale, (Vector3dc)this.startUp, light);
        this.vert(ms, a, (Vector3dc)endPos.sub((Vector3dc)this.endLeft, this.vertex).add((Vector3dc)this.endUp), color, 0.0f + uvXOffset, uvEnd * uvScale, (Vector3dc)this.endUp, light);
        this.vert(ms, a, (Vector3dc)endPos.add((Vector3dc)this.endLeft, this.vertex).add((Vector3dc)this.endUp), color, texW + uvXOffset, uvEnd * uvScale, (Vector3dc)this.endUp, light);
        this.vert(ms, a, (Vector3dc)startPos.add((Vector3dc)this.startLeft, this.vertex).add((Vector3dc)this.startUp), color, texW + uvXOffset, uvStart * uvScale, (Vector3dc)this.startUp, light);
        this.vert(ms, a, (Vector3dc)startPos.sub((Vector3dc)this.startLeft, this.vertex).sub((Vector3dc)this.startUp), color, 0.0f + uvXOffset, uvStart * uvScale, (Vector3dc)startRight, light);
        this.vert(ms, a, (Vector3dc)endPos.sub((Vector3dc)this.endLeft, this.vertex).sub((Vector3dc)this.endUp), color, 0.0f + uvXOffset, uvEnd * uvScale, (Vector3dc)endRight, light);
        this.vert(ms, a, (Vector3dc)endPos.sub((Vector3dc)this.endLeft, this.vertex).add((Vector3dc)this.endUp), color, texW + uvXOffset, uvEnd * uvScale, (Vector3dc)endRight, light);
        this.vert(ms, a, (Vector3dc)startPos.sub((Vector3dc)this.startLeft, this.vertex).add((Vector3dc)this.startUp), color, texW + uvXOffset, uvStart * uvScale, (Vector3dc)startRight, light);
        this.vert(ms, a, (Vector3dc)startPos.add((Vector3dc)this.startLeft, this.vertex).add((Vector3dc)this.startUp), color, 0.0f + uvXOffset, uvStart * uvScale, (Vector3dc)this.startLeft, light);
        this.vert(ms, a, (Vector3dc)endPos.add((Vector3dc)this.endLeft, this.vertex).add((Vector3dc)this.endUp), color, 0.0f + uvXOffset, uvEnd * uvScale, (Vector3dc)this.endLeft, light);
        this.vert(ms, a, (Vector3dc)endPos.add((Vector3dc)this.endLeft, this.vertex).sub((Vector3dc)this.endUp), color, texW + uvXOffset, uvEnd * uvScale, (Vector3dc)this.endLeft, light);
        this.vert(ms, a, (Vector3dc)startPos.add((Vector3dc)this.startLeft, this.vertex).sub((Vector3dc)this.startUp), color, texW + uvXOffset, uvStart * uvScale, (Vector3dc)this.startLeft, light);
    }

    private void vert(PoseStack ms, VertexConsumer a, Vector3dc pos, int color, float u1, float v1, Vector3dc normal, int light) {
        normal.normalize(this.normalizedNormal);
        a.addVertex(ms.last().pose(), (float)pos.x(), (float)pos.y(), (float)pos.z()).setColor(color).setUv(u1, v1).setLight(light).setNormal(ms.last(), (float)this.normalizedNormal.x(), (float)this.normalizedNormal.y(), (float)this.normalizedNormal.z());
    }

    public boolean shouldRender(SpringBlockEntity blockEntity, Vec3 vec3) {
        return true;
    }

    public boolean shouldRenderOffScreen(SpringBlockEntity blockEntity) {
        return super.shouldRenderOffScreen((BlockEntity)blockEntity);
    }

    record SplinePoint(Vector3dc point, Vector3dc normal) {
    }
}
