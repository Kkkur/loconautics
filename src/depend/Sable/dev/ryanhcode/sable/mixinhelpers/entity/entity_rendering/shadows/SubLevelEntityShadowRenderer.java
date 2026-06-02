/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.dimension.DimensionType
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 */
package dev.ryanhcode.sable.mixinhelpers.entity.entity_rendering.shadows;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

public class SubLevelEntityShadowRenderer {
    public static final double INFLATION = 1.01;
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Vector3d CENTER = new Vector3d();
    private static final Vector3d ENTITY_RELATIVE_CENTER = new Vector3d();
    private static final Vector3d NORMAL = new Vector3d();
    private static final Vector3d LOCAL_POS = new Vector3d();
    private static final Vector3d ENTITY_LOCAL_POS = new Vector3d();
    private static final Vector3f RENDER_POSITION = new Vector3f();
    private static final BoundingBox3d BOUNDS = new BoundingBox3d();
    private static final BlockPos.MutableBlockPos TEMP = new BlockPos.MutableBlockPos();
    private static final Vector3d[] CORNERS = new Vector3d[]{new Vector3d(), new Vector3d(), new Vector3d(), new Vector3d()};
    private static final Vector3d[] REVERSE_CORNERS = new Vector3d[]{CORNERS[3], CORNERS[2], CORNERS[1], CORNERS[0]};

    public static void renderEntityShadowOnSubLevels(Entity entity, float f, float partialTick, float shadowRadius, VertexConsumer vertexConsumer, PoseStack.Pose pose) {
        Vec3 entityOrigin;
        Quaterniondc customOrientation = EntitySubLevelUtil.getCustomEntityOrientation(entity, partialTick);
        Vec3 entityFeet = entityOrigin = entity.getPosition(partialTick);
        Vector3dc upDir = OrientedBoundingBox3d.UP;
        Vec3 eyePos = entity.getEyePosition(partialTick);
        if (customOrientation != null) {
            entityFeet = eyePos.subtract(JOMLConversion.toMojang((Vector3dc)customOrientation.transform(new Vector3d(0.0, (double)entity.getEyeHeight(), 0.0))));
            upDir = customOrientation.transform(new Vector3d(upDir));
        }
        Level level = entity.level();
        float shadowHeight = Math.min(f / 0.5f, shadowRadius) * 3.0f;
        BoundingBox3d bounds = new BoundingBox3d(entityFeet.x - (double)shadowRadius, entityFeet.y - (double)shadowHeight, entityFeet.z - (double)shadowRadius, entityFeet.x + (double)shadowRadius, entityFeet.y + 0.2, entityFeet.z + (double)shadowRadius);
        BoundingBox3d localBounds = new BoundingBox3d();
        if (customOrientation != null) {
            bounds.transform((Matrix4dc)new Matrix4d().translate(entityFeet.x, entityFeet.y, entityFeet.z).rotate(customOrientation).translate(-entityFeet.x, -entityFeet.y, -entityFeet.z), bounds);
        }
        Iterable<SubLevel> intersecting = Sable.HELPER.getAllIntersecting(level, (BoundingBox3dc)bounds);
        for (SubLevel subLevel : intersecting) {
            Pose3dc renderPose = ((ClientSubLevel)subLevel).renderPose();
            bounds.transformInverse(renderPose, localBounds);
            for (BlockPos subLevelBlockPos : BlockPos.betweenClosed((int)Mth.floor((double)localBounds.minX), (int)Mth.floor((double)localBounds.minY), (int)Mth.floor((double)localBounds.minZ), (int)Mth.floor((double)localBounds.maxX), (int)Mth.floor((double)localBounds.maxY), (int)Mth.floor((double)localBounds.maxZ))) {
                VoxelShape voxelShape;
                BlockState blockState = level.getBlockState(subLevelBlockPos);
                if (blockState.getRenderShape() == RenderShape.INVISIBLE || level.getMaxLocalRawBrightness(entity.blockPosition()) <= 3 || !blockState.isCollisionShapeFullBlock((BlockGetter)level, subLevelBlockPos) || (voxelShape = blockState.getShape((BlockGetter)level, subLevelBlockPos)).isEmpty()) continue;
                float light = LightTexture.getBrightness((DimensionType)level.dimensionType(), (int)level.getMaxLocalRawBrightness(entity.blockPosition()));
                BoundingBox3d shapeBounds = BOUNDS.set(voxelShape.bounds()).move((double)subLevelBlockPos.getX(), (double)subLevelBlockPos.getY(), (double)subLevelBlockPos.getZ(), BOUNDS);
                Vector3d center = shapeBounds.center(CENTER);
                double centerX = center.x;
                double centerY = center.y;
                double centerZ = center.z;
                renderPose.transformPosition(center);
                for (Direction direction : DIRECTIONS) {
                    Vector3d[] corners;
                    BlockPos.MutableBlockPos offset = TEMP.setWithOffset((Vec3i)subLevelBlockPos, direction);
                    BlockState offsetState = level.getBlockState((BlockPos)offset);
                    if (offsetState.getRenderShape() != RenderShape.INVISIBLE && offsetState.isCollisionShapeFullBlock((BlockGetter)level, (BlockPos)offset) || renderPose.transformNormal(JOMLConversion.atLowerCornerOf((Vec3i)direction.getNormal(), (Vector3d)NORMAL)).dot(upDir) < 0.6 || center.sub(entityFeet.x, entityFeet.y, entityFeet.z, ENTITY_RELATIVE_CENTER).dot((Vector3dc)NORMAL) >= 0.0) continue;
                    double xHalfExtent = (shapeBounds.maxX - shapeBounds.minX) / 2.0;
                    double zHalfExtent = (shapeBounds.maxZ - shapeBounds.minZ) / 2.0;
                    double yHalfExtent = (shapeBounds.maxY - shapeBounds.minY) / 2.0;
                    if (direction.getAxis() == Direction.Axis.Y) {
                        double yStep = (double)direction.getStepY() * 1.01;
                        CORNERS[0].set(centerX - xHalfExtent, centerY + yStep * yHalfExtent, centerZ + zHalfExtent);
                        CORNERS[1].set(centerX + xHalfExtent, centerY + yStep * yHalfExtent, centerZ + zHalfExtent);
                        CORNERS[2].set(centerX + xHalfExtent, centerY + yStep * yHalfExtent, centerZ - zHalfExtent);
                        CORNERS[3].set(centerX - xHalfExtent, centerY + yStep * yHalfExtent, centerZ - zHalfExtent);
                    } else if (direction.getAxis() == Direction.Axis.X) {
                        double xStep = (double)direction.getStepX() * 1.01;
                        CORNERS[0].set(centerX + xStep * xHalfExtent, centerY + yHalfExtent, centerZ + zHalfExtent);
                        CORNERS[1].set(centerX + xStep * xHalfExtent, centerY - yHalfExtent, centerZ + zHalfExtent);
                        CORNERS[2].set(centerX + xStep * xHalfExtent, centerY - yHalfExtent, centerZ - zHalfExtent);
                        CORNERS[3].set(centerX + xStep * xHalfExtent, centerY + yHalfExtent, centerZ - zHalfExtent);
                    } else if (direction.getAxis() == Direction.Axis.Z) {
                        double zStep = (double)direction.getStepZ() * 1.01;
                        CORNERS[0].set(centerX + xHalfExtent, centerY + yHalfExtent, centerZ + zStep * zHalfExtent);
                        CORNERS[1].set(centerX - xHalfExtent, centerY + yHalfExtent, centerZ + zStep * zHalfExtent);
                        CORNERS[2].set(centerX - xHalfExtent, centerY - yHalfExtent, centerZ + zStep * zHalfExtent);
                        CORNERS[3].set(centerX + xHalfExtent, centerY - yHalfExtent, centerZ + zStep * zHalfExtent);
                    }
                    for (Vector3d corner : corners = (switch (direction.getAxisDirection()) {
                        default -> throw new MatchException(null, null);
                        case Direction.AxisDirection.POSITIVE -> CORNERS;
                        case Direction.AxisDirection.NEGATIVE -> REVERSE_CORNERS;
                    })) {
                        renderPose.transformPosition((Vector3dc)corner, LOCAL_POS).sub(entityFeet.x, entityFeet.y, entityFeet.z);
                        Vector3d entityLocalPos = ENTITY_LOCAL_POS.set((Vector3dc)LOCAL_POS);
                        if (customOrientation != null) {
                            customOrientation.transformInverse(entityLocalPos);
                        }
                        double yDiff = entityLocalPos.y;
                        int alpha = Mth.floor((float)((float)Math.max(0.0, (double)((f - (float)(-yDiff) * 0.5f) * 0.5f * light)) * 255.0f));
                        LOCAL_POS.add(entityFeet.x - entityOrigin.x, entityFeet.y - entityOrigin.y, entityFeet.z - entityOrigin.z);
                        SubLevelEntityShadowRenderer.shadowVertex(pose, vertexConsumer, alpha << 24 | 0xFFFFFF, (float)SubLevelEntityShadowRenderer.LOCAL_POS.x, (float)SubLevelEntityShadowRenderer.LOCAL_POS.y, (float)SubLevelEntityShadowRenderer.LOCAL_POS.z, (float)((entityLocalPos.x + (double)shadowRadius) / (double)(shadowRadius * 2.0f)), (float)((entityLocalPos.z + (double)shadowRadius) / (double)(shadowRadius * 2.0f)));
                    }
                }
            }
        }
    }

    private static void shadowVertex(PoseStack.Pose pose, VertexConsumer vertexConsumer, int i, float f, float g, float h, float j, float k) {
        Vector3f vector3f = pose.pose().transformPosition(f, g, h, RENDER_POSITION);
        vertexConsumer.addVertex(vector3f.x(), vector3f.y(), vector3f.z(), i, j, k, OverlayTexture.NO_OVERLAY, 0xF000F0, 0.0f, 1.0f, 0.0f);
    }
}
