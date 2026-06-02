/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.ScaffoldingBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4d
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.sublevel.entity_collision;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.math.LevelReusedVectors;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.index.SableTags;
import dev.ryanhcode.sable.mixinterface.EntityExtension;
import dev.ryanhcode.sable.mixinterface.voxel_shape_iteration.FastVoxelShapeIterable;
import dev.ryanhcode.sable.physics.impl.SubLevelEntityCollisionContext;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.LevelAccelerator;
import dev.ryanhcode.sable.util.SableMathUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SubLevelEntityCollision {
    public static CollisionInfo collide(Entity entity, Vec3 collisionMotionMoj, Vec3 velocityMotionMoj, LevelReusedVectors sink) {
        Player player;
        if (entity instanceof ServerPlayer) {
            CollisionInfo collisionInfo = new CollisionInfo();
            collisionInfo.motion = collisionMotionMoj;
            SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel(entity);
            if (trackingSubLevel != null) {
                entity.setOnGround(true);
                collisionInfo.verticalCollisionBelow = true;
                collisionInfo.verticalCollision = true;
                collisionInfo.trackingSubLevel = trackingSubLevel;
                if (entity.getDeltaMovement().y < 0.0) {
                    entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                }
            }
            return collisionInfo;
        }
        SubLevel existingTrackingSubLevel = Sable.HELPER.getTrackingSubLevel(entity);
        if (existingTrackingSubLevel != null && EntitySubLevelUtil.shouldKick(entity) && existingTrackingSubLevel.getPlot().contains(entity.position())) {
            EntitySubLevelUtil.kickEntity(existingTrackingSubLevel, entity);
        }
        BoundingBox3d fullContextBounds = sink.fullContextBounds.set(entity.getBoundingBox().minmax(entity.getBoundingBox().move(collisionMotionMoj)));
        BoundingBox3d rotatedContextBounds = sink.rotatedContextBounds;
        AABB entityBounds = entity.getBoundingBox();
        Vector3d collisionMotion = sink.collisionMotion.set(collisionMotionMoj.x, collisionMotionMoj.y, collisionMotionMoj.z);
        Vector3d velocityMotion = sink.velocityMotion.set(velocityMotionMoj.x, velocityMotionMoj.y, velocityMotionMoj.z);
        Level level = entity.level();
        LevelAccelerator accel = new LevelAccelerator(level);
        Quaterniondc customEntityOrientation = EntitySubLevelUtil.getCustomEntityOrientation(entity, 0.0f);
        sink.entityUpDirection.set(OrientedBoundingBox3d.UP);
        BoundingBox3d considerationBounds = sink.considerationBounds.set((BoundingBox3dc)fullContextBounds);
        if (customEntityOrientation != null) {
            customEntityOrientation.transform(sink.entityUpDirection);
            considerationBounds.expand((double)entity.getEyeHeight());
        }
        considerationBounds.expand(1.0);
        ObjectOpenHashSet intersecting = new ObjectOpenHashSet();
        for (SubLevel subLevel : Sable.HELPER.getAllIntersecting(level, (BoundingBox3dc)considerationBounds)) {
            intersecting.add((Object)subLevel);
        }
        CollisionInfo collisionInfo = new CollisionInfo();
        collisionInfo.trackingSubLevel = existingTrackingSubLevel;
        if (collisionInfo.trackingSubLevel != null) {
            intersecting.add((Object)collisionInfo.trackingSubLevel);
        }
        if (!intersecting.iterator().hasNext()) {
            collisionInfo.motion = collisionMotionMoj.add(velocityMotionMoj);
            return collisionInfo;
        }
        BoundingBox3d localBounds = sink.localBounds;
        BoundingBox3d localBounds2 = sink.localBounds2;
        int substeps = Math.min(10, Math.max(1, (int)(collisionMotion.length() / 0.015625)));
        if (entity instanceof Player && (player = (Player)entity).isLocalPlayer()) {
            substeps = 8;
        }
        Vec3 originalEntityPosition = entity.position();
        Vector3d originalEntityFootPosition = Sable.HELPER.getFeetPos(entity, 0.0f, customEntityOrientation);
        Vector3d entityBoundsCenter = JOMLConversion.getAABBCenter((AABB)entityBounds, (Vector3d)sink.entityBoundsCenter);
        SubLevelEntityCollision.transformEntityBoundsCenter(sink, customEntityOrientation, entity, entityBoundsCenter);
        sink.entityBoxOrientation.identity();
        OrientedBoundingBox3d entityBoundsOBB = new OrientedBoundingBox3d(entityBoundsCenter.x + collisionMotion.x + velocityMotion.x, entityBoundsCenter.y + collisionMotion.y + velocityMotion.y, entityBoundsCenter.z + collisionMotion.z + velocityMotion.z, entityBounds.getXsize(), entityBounds.getYsize(), entityBounds.getZsize(), (Quaterniondc)sink.entityBoxOrientation, sink);
        OrientedBoundingBox3d cubeOBB = new OrientedBoundingBox3d(sink);
        Pose3d lastPose = sink.lastPose;
        Pose3d lastSubLevelPose = sink.lastSubLevelPose;
        Pose3d subLevelPose = sink.subLevelPose;
        Matrix4d bakedMatrix = sink.bakedMatrix;
        Vector3d mtv = sink.mtv;
        Vector3d normalizedMtv = sink.normalizedMtv;
        Vector3d existingDeltaMovement = sink.existingDeltaMovement;
        Vector3d maxMTV = sink.maxMTV;
        BoundingBox3d maxAABB = sink.maxAABB;
        Vector3d center = sink.center;
        collisionMotion.zero();
        Vector3d steppingMotion = JOMLConversion.toJOML((Position)collisionMotionMoj);
        Vector3d steppingVelocityMotion = JOMLConversion.toJOML((Position)velocityMotionMoj);
        boolean swappedTrackingAlready = false;
        boolean stopTrackingAtEnd = false;
        Object2ObjectArrayMap firstCollisions = new Object2ObjectArrayMap();
        for (int i = 1; i <= substeps; ++i) {
            double delta = 1.0 / (double)substeps;
            collisionMotion.fma(delta, (Vector3dc)steppingMotion);
            if (collisionInfo.trackingSubLevel == null) {
                collisionMotion.fma(delta, (Vector3dc)steppingVelocityMotion);
            }
            sink.entityBoxOrientation.identity();
            double yaw = SubLevelEntityCollision.getHitBoxYaw((Pose3dc)subLevelPose);
            sink.entityBoxOrientation.rotateY(yaw);
            Vector3d entityUp = sink.entityUpDirection;
            if (customEntityOrientation != null) {
                entityBoundsCenter.fma((double)entity.getEyeHeight() - entity.getBoundingBox().getYsize() / 2.0, (Vector3dc)entityUp);
                customEntityOrientation = EntitySubLevelUtil.getCustomEntityOrientation(entity, (float)i / (float)substeps);
                entityUp.set(OrientedBoundingBox3d.UP);
                SubLevelEntityCollision.transformEntityBoundingBox(customEntityOrientation, sink.entityBoxOrientation, entityUp);
                entityBoundsCenter.fma(-((double)entity.getEyeHeight() - entity.getBoundingBox().getYsize() / 2.0), (Vector3dc)entityUp);
            } else {
                entityUp.set(OrientedBoundingBox3d.UP);
            }
            entityBoundsOBB.setOrientation((Quaterniondc)sink.entityBoxOrientation);
            entityBoundsCenter.add((Vector3dc)collisionMotion, entityBoundsOBB.getPosition());
            for (SubLevel subLevel : intersecting) {
                if (Sable.HELPER.getVehicleSubLevel(entity) == subLevel) continue;
                Pose3d logicalPose = subLevel.logicalPose();
                lastPose.set(subLevel.lastPose());
                if (lastPose.rotationPoint().lengthSquared() <= 0.0) {
                    lastPose.rotationPoint().set((Vector3dc)logicalPose.rotationPoint());
                }
                lastPose.lerp((Pose3dc)logicalPose, (double)(i - 1) / (double)substeps, lastSubLevelPose);
                lastPose.lerp((Pose3dc)logicalPose, (double)i / (double)substeps, subLevelPose);
                rotatedContextBounds.set((BoundingBox3dc)fullContextBounds);
                if (customEntityOrientation != null) {
                    entityBoundsOBB.vertices(sink.a);
                    for (Vector3d vec : sink.a) {
                        rotatedContextBounds.expandTo((Vector3dc)vec);
                        rotatedContextBounds.expandTo((Vector3dc)vec.sub(collisionMotion.x, collisionMotion.y, collisionMotion.z));
                    }
                    rotatedContextBounds.expand((double)0.35f);
                }
                rotatedContextBounds.transformInverse((Pose3dc)lastPose, bakedMatrix, localBounds);
                rotatedContextBounds.transformInverse((Pose3dc)logicalPose, bakedMatrix, localBounds2);
                localBounds.expandTo((BoundingBox3dc)localBounds2, localBounds);
                if (localBounds.volume() > 1.25E8) {
                    Sable.LOGGER.info("Enormous local sub-level collision bounds, quitting.");
                    continue;
                }
                Iterable blocks = BlockPos.betweenClosed((BlockPos)sink.minPos.set(localBounds.minX, localBounds.minY - 1.0, localBounds.minZ), (BlockPos)sink.maxPos.set(localBounds.maxX, localBounds.maxY, localBounds.maxZ));
                cubeOBB.getOrientation().set((Quaterniondc)subLevelPose.orientation());
                if (collisionInfo.trackingSubLevel == subLevel) {
                    float verticalAnchorPosition = 0.0f;
                    Vector3d feetOffset = entityUp.mul(0.0 - entity.getBoundingBox().getYsize() / 2.0, sink.posMinusCenter);
                    sink.trackingPosition.set((Vector3dc)entityBoundsCenter).add((Vector3dc)feetOffset);
                    subLevelPose.transformPosition(lastSubLevelPose.transformPositionInverse(sink.trackingPosition)).sub((Vector3dc)feetOffset, entityBoundsCenter);
                    entityBoundsCenter.add((Vector3dc)collisionMotion, entityBoundsOBB.getPosition());
                    entityBoundsCenter.fma(0.0 - entity.getBoundingBox().getYsize() / 2.0, (Vector3dc)entityUp, sink.tempEyePosition).sub(0.0, 0.0, 0.0);
                    ((EntityExtension)entity).sable$setPosSuperRaw(new Vec3(sink.tempEyePosition.x, sink.tempEyePosition.y, sink.tempEyePosition.z));
                    boolean anySurroundingBlocksSolid = false;
                    for (BlockPos block : blocks) {
                        if (accel.getBlockState(block).isAir()) continue;
                        anySurroundingBlocksSolid = true;
                        break;
                    }
                    if (!anySurroundingBlocksSolid) {
                        stopTrackingAtEnd = true;
                    }
                }
                for (int maxIter = 0; maxIter < 4; ++maxIter) {
                    mtv.set(Double.MAX_VALUE);
                    maxMTV.zero();
                    double maxMTVLength = Double.MIN_VALUE;
                    BlockPos.MutableBlockPos maxBlockPos = sink.maxBlockPos;
                    BlockState maxBlockState = null;
                    for (BlockPos block : blocks) {
                        BlockState state = accel.getBlockState(block);
                        VoxelShape voxelShape = SubLevelEntityCollision.getSubLevelEntityCollisionShape(entity, (Vector3dc)entityBoundsCenter, (Pose3dc)subLevelPose, state, accel, block, sink);
                        if (state.isAir()) continue;
                        Iterator<BoundingBox3dc> iterator = ((FastVoxelShapeIterable)voxelShape).sable$allBoxes();
                        while (iterator.hasNext()) {
                            double lengthMtv;
                            BoundingBox3dc box = iterator.next();
                            box.center(center);
                            cubeOBB.getPosition().set((double)block.getX() + center.x, (double)block.getY() + center.y, (double)block.getZ() + center.z);
                            subLevelPose.transformPosition(cubeOBB.getPosition());
                            box.size(cubeOBB.getDimensions());
                            OrientedBoundingBox3d.sat(entityBoundsOBB, cubeOBB, mtv);
                            if (!(mtv.lengthSquared() > 0.0) || mtv.x == Double.MAX_VALUE || mtv.y == Double.MAX_VALUE || mtv.z == Double.MAX_VALUE || !((lengthMtv = mtv.lengthSquared()) > maxMTVLength)) continue;
                            maxMTVLength = lengthMtv;
                            maxMTV.set((Vector3dc)mtv);
                            box.move((double)block.getX(), (double)block.getY(), (double)block.getZ(), maxAABB);
                            maxBlockPos.set((Vec3i)block);
                            maxBlockState = state;
                        }
                    }
                    if (!(maxMTV.lengthSquared() > 0.0)) continue;
                    if (collisionInfo.trackingSubLevel == null) {
                        collisionInfo.trackingSubLevel = subLevel;
                        stopTrackingAtEnd = false;
                    }
                    Vector3d localMtv = subLevelPose.transformNormalInverse((Vector3dc)maxMTV, sink.localMtv).normalize();
                    int offsetX = (int)Math.round(localMtv.x());
                    int offsetY = (int)Math.round(localMtv.y());
                    int offsetZ = (int)Math.round(localMtv.z());
                    BlockPos.MutableBlockPos newPos = sink.offsetPos.setWithOffset((Vec3i)maxBlockPos, offsetX, offsetY, offsetZ);
                    BlockState offsetState = accel.getBlockState((BlockPos)newPos);
                    VoxelShape offsetShape = SubLevelEntityCollision.getSubLevelEntityCollisionShape(entity, (Vector3dc)entityBoundsCenter, (Pose3dc)subLevelPose, offsetState, accel, (BlockPos)newPos, sink);
                    Direction direction = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)Direction.getNearest((float)offsetX, (float)offsetY, (float)offsetZ).getAxis());
                    BoundingBox3d offsetAABB = sink.offsetAABB;
                    BoundingBox3d compressedMinAABB = sink.compressedMinAABB;
                    BoundingBox3d compressedOffsetAABB = sink.compressedOffsetAABB;
                    BoundingBox3d intersection = sink.intersection;
                    boolean discard = false;
                    Iterator<BoundingBox3dc> iterator = ((FastVoxelShapeIterable)offsetShape).sable$allBoxes();
                    while (iterator.hasNext()) {
                        BoundingBox3dc box = iterator.next();
                        box.move((double)newPos.getX(), (double)newPos.getY(), (double)newPos.getZ(), offsetAABB).expand(0.001);
                        if (!maxAABB.intersects((BoundingBox3dc)offsetAABB)) continue;
                        compressedMinAABB.set(maxAABB.minX * (1.0 - (double)direction.getStepX()), maxAABB.minY * (1.0 - (double)direction.getStepY()), maxAABB.minZ * (1.0 - (double)direction.getStepZ()), maxAABB.maxX * (1.0 - (double)direction.getStepX()) + (double)direction.getStepX(), maxAABB.maxY * (1.0 - (double)direction.getStepY()) + (double)direction.getStepY(), maxAABB.maxZ * (1.0 - (double)direction.getStepZ()) + (double)direction.getStepZ());
                        compressedOffsetAABB.set(offsetAABB.minX * (1.0 - (double)direction.getStepX()), offsetAABB.minY * (1.0 - (double)direction.getStepY()), offsetAABB.minZ * (1.0 - (double)direction.getStepZ()), offsetAABB.maxX * (1.0 - (double)direction.getStepX()) + (double)direction.getStepX(), offsetAABB.maxY * (1.0 - (double)direction.getStepY()) + (double)direction.getStepY(), offsetAABB.maxZ * (1.0 - (double)direction.getStepZ()) + (double)direction.getStepZ());
                        compressedMinAABB.intersect((BoundingBox3dc)compressedOffsetAABB, intersection);
                        if (!(Math.abs(intersection.volume() - compressedMinAABB.volume()) < 0.01)) continue;
                        discard = true;
                        break;
                    }
                    if (discard) continue;
                    maxMTV.normalize(normalizedMtv);
                    double dot = normalizedMtv.dot((Vector3dc)entityUp);
                    boolean verticalCollision = Math.abs(dot) > 0.6;
                    BlockState collidedBlockState = maxBlockState;
                    firstCollisions.computeIfAbsent(subLevel, sl -> {
                        Vector3d localBoundsCenter = subLevelPose.transformPositionInverse(new Vector3d((Vector3dc)entityBoundsCenter));
                        return new FirstCollisionInfo((Vector3dc)localBoundsCenter, (Vector3dc)new Vector3d((Vector3dc)maxMTV).normalize(), !verticalCollision, collidedBlockState.is(SableTags.BOUNCY), collidedBlockState);
                    });
                    if (verticalCollision) {
                        collisionInfo.verticalCollision = true;
                        if (dot > 0.0) {
                            entity.setOnGround(true);
                            collisionInfo.verticalCollisionBelow = true;
                            if (collisionInfo.trackingSubLevel != subLevel && !swappedTrackingAlready) {
                                swappedTrackingAlready = true;
                                collisionInfo.trackingSubLevel = subLevel;
                            }
                        }
                        if (dot > 0.8) {
                            double preLength = maxMTV.length();
                            entityUp.mul(maxMTV.dot((Vector3dc)entityUp), maxMTV).normalize(preLength);
                        }
                    } else {
                        collisionInfo.subLevelHorizontalCollision = collisionInfo.subLevelHorizontalCollision | !SubLevelEntityCollision.tryStepUp(entity, accel, sink, (Pose3dc)subLevelPose, blocks, (Vector3dc)entityBoundsCenter, entityBounds, entityBoundsOBB, cubeOBB, (Vector3dc)maxMTV, (Vector3dc)normalizedMtv, collisionMotion);
                        if (collisionInfo.subLevelHorizontalCollision) {
                            JOMLConversion.toJOML((Position)entity.getDeltaMovement(), (Vector3d)existingDeltaMovement);
                            Vector3d deltaMovementLoss = normalizedMtv.mul(normalizedMtv.dot((Vector3dc)existingDeltaMovement));
                            if (deltaMovementLoss.length() > existingDeltaMovement.length() * 0.1) {
                                entity.setSprinting(false);
                            }
                            double friction = 0.995;
                            Vector3d newDeltaMovement = existingDeltaMovement.sub((Vector3dc)deltaMovementLoss);
                            double upVelocity = entityUp.dot((Vector3dc)newDeltaMovement);
                            newDeltaMovement.fma(-upVelocity, (Vector3dc)entityUp).mul(0.995).fma(upVelocity, (Vector3dc)entityUp);
                            entity.setDeltaMovement(JOMLConversion.toMojang((Vector3dc)newDeltaMovement));
                        }
                    }
                    collisionMotion.add((Vector3dc)maxMTV);
                    entityBoundsCenter.add((Vector3dc)collisionMotion, entityBoundsOBB.getPosition());
                }
            }
        }
        collisionInfo.inheritedMotion = JOMLConversion.toMojang((Vector3dc)Sable.HELPER.getFeetPos(entity, 0.0f, customEntityOrientation).sub((Vector3dc)originalEntityFootPosition));
        if (collisionInfo.inheritedMotion.lengthSqr() < 1.0E-8) {
            collisionInfo.inheritedMotion = null;
        }
        if (stopTrackingAtEnd) {
            collisionInfo.trackingSubLevel = null;
        }
        ((EntityExtension)entity).sable$setPosSuperRaw(originalEntityPosition);
        collisionInfo.motion = JOMLConversion.toMojang((Vector3dc)collisionMotion);
        collisionInfo.firstCollisions = firstCollisions;
        return collisionInfo;
    }

    public static void transformEntityBoundsCenter(LevelReusedVectors sink, Quaterniondc customOrientation, Entity entity, Vector3d center) {
        if (customOrientation == null) {
            return;
        }
        Vector3d offset = sink.anchorRelativePosition.set(0.0, (double)entity.getEyeHeight() - entity.getBoundingBox().getYsize() / 2.0, 0.0);
        center.add((Vector3dc)offset).sub((Vector3dc)customOrientation.transform(offset));
    }

    public static void transformEntityBoundingBox(Quaterniondc customOrientation, Quaterniond bounds, Vector3d upDir) {
        if (customOrientation == null) {
            return;
        }
        bounds.premul(customOrientation);
        customOrientation.transform(upDir);
    }

    public static double getHitBoxYaw(Pose3dc subLevelPose) {
        Quaterniondc subLevelOrientation = subLevelPose.orientation();
        Quaterniond snapped = SableMathUtils.clampQuaternionToGrid(subLevelOrientation, (Iterable<Quaterniondc>)((Object)SableMathUtils.GridQuats.REAL), new Quaterniond());
        Quaterniond relativeOrientation = subLevelOrientation.div((Quaterniondc)snapped, snapped);
        double dot = OrientedBoundingBox3d.UP.dot((Vector3dc)new Vector3d(relativeOrientation.x(), relativeOrientation.y(), relativeOrientation.z()));
        return -2.0 * Math.atan2(-dot, relativeOrientation.w());
    }

    @NotNull
    private static VoxelShape getSubLevelEntityCollisionShape(Entity entity, Vector3dc boundsCenter, Pose3dc subLevelPose, BlockState state, LevelAccelerator level, BlockPos pos, LevelReusedVectors sink) {
        if (state.getBlock() instanceof ScaffoldingBlock) {
            VoxelShape originalShape = state.getCollisionShape((BlockGetter)level, pos, (CollisionContext)new SubLevelEntityCollisionContext(entity));
            double skew = 0.05;
            if (entity.isShiftKeyDown()) {
                return originalShape;
            }
            Vector3d vector3d = new Vector3d();
            if (subLevelPose.transformPositionInverse((Vector3d)boundsCenter.fma((double)(-(entity.getBoundingBox().getYsize() / 2.0 - 0.05)), (Vector3dc)sink.entityUpDirection, (Vector3d)vector3d)).y > (double)pos.getY() + 1.0 + 0.05) {
                return sink.SCAFFOLDING_TOP;
            }
            return originalShape;
        }
        return state.getCollisionShape((BlockGetter)level, pos);
    }

    private static boolean tryStepUp(Entity entity, LevelAccelerator accel, LevelReusedVectors sink, Pose3dc subLevelPose, Iterable<BlockPos> blocks, Vector3dc entityBoundsCenter, AABB entityBounds, OrientedBoundingBox3d entityBoundsOBB, OrientedBoundingBox3d cubeOBB, Vector3dc maxMTV, Vector3dc normalizedMTV, Vector3d collisionMotion) {
        double currentStepUp;
        if (!entity.onGround()) {
            return false;
        }
        if (collisionMotion.dot(normalizedMTV) > 0.0) {
            return true;
        }
        double checkIncrement = 0.0625;
        double maxStepHeight = entity.maxUpStep();
        Vector3d lastStepTestMTV = sink.lastStepTestMTV.zero();
        int collidingCount = 0;
        int freeCount = 0;
        double inflation = 0.1;
        entityBoundsOBB.getDimensions().set(entityBounds.getXsize(), entityBounds.getYsize(), entityBounds.getZsize()).add(0.1, 0.1, 0.1);
        for (currentStepUp = 0.0; currentStepUp <= maxStepHeight; currentStepUp += 0.0625) {
            Vector3d boundsCenter = sink.stepHeightEntityBoundsCenter;
            boundsCenter.set(entityBoundsCenter).fma(currentStepUp, (Vector3dc)sink.entityUpDirection).fma(-0.125, normalizedMTV);
            if (SubLevelEntityCollision.hasCollision(accel, sink, subLevelPose, blocks, entityBoundsOBB, cubeOBB, boundsCenter)) {
                lastStepTestMTV.set((Vector3dc)sink.mtv);
                ++collidingCount;
                continue;
            }
            ++freeCount;
            break;
        }
        entityBoundsOBB.getDimensions().set(entityBounds.getXsize(), entityBounds.getYsize(), entityBounds.getZsize());
        if (freeCount > 0 && collidingCount > 0 && lastStepTestMTV.normalize().dot((Vector3dc)sink.entityUpDirection) > 0.8) {
            collisionMotion.fma(currentStepUp, (Vector3dc)sink.entityUpDirection).fma(-0.0625, normalizedMTV);
            return true;
        }
        return false;
    }

    private static boolean hasCollision(LevelAccelerator accel, LevelReusedVectors sink, Pose3dc subLevelPose, Iterable<BlockPos> blocks, OrientedBoundingBox3d entityBoundsOBB, OrientedBoundingBox3d cubeOBB, Vector3d boundsCenter) {
        entityBoundsOBB.setPosition((Vector3dc)boundsCenter);
        for (BlockPos block : blocks) {
            BlockState state = accel.getBlockState(block);
            VoxelShape voxelShape = state.getCollisionShape((BlockGetter)accel, block);
            if (state.isAir()) continue;
            Iterator<BoundingBox3dc> iterator = ((FastVoxelShapeIterable)voxelShape).sable$allBoxes();
            Vector3d center = sink.center;
            Vector3d mtv = sink.mtv;
            while (iterator.hasNext()) {
                BoundingBox3dc box = iterator.next();
                box.center(center);
                cubeOBB.getPosition().set((double)block.getX() + center.x, (double)block.getY() + center.y, (double)block.getZ() + center.z);
                subLevelPose.transformPosition(cubeOBB.getPosition());
                box.size(cubeOBB.getDimensions());
                OrientedBoundingBox3d.sat(entityBoundsOBB, cubeOBB, mtv);
                if (!(mtv.lengthSquared() > 0.0) || mtv.x == Double.MAX_VALUE || mtv.y == Double.MAX_VALUE || mtv.z == Double.MAX_VALUE) continue;
                return true;
            }
        }
        return false;
    }

    public static class CollisionInfo {
        public SubLevel preTrackingSubLevel;
        public Vec3 preDeltaMovement;
        public boolean subLevelHorizontalCollision;
        public boolean horizontalCollision;
        public boolean verticalCollision;
        public boolean verticalCollisionBelow;
        public boolean minorHorizontalCollision;
        public Vec3 inheritedMotion;
        public Vec3 motion;
        public SubLevel trackingSubLevel;
        public Map<SubLevel, FirstCollisionInfo> firstCollisions;
    }

    public record FirstCollisionInfo(Vector3dc localLocation, Vector3dc globalDirection, boolean horizontal, boolean bouncy, BlockState block) {
    }
}
