/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.player.RemotePlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.CocoaBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.Shapes$DoubleLineConsumer
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.tuple.MutablePair
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionColliderLockPacket;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.TrainCollisionPacket;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.sync.ClientMotionPacket;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.collision.CollisionList;
import com.simibubi.create.foundation.collision.ContinuousOBBCollider;
import com.simibubi.create.foundation.collision.Matrix3d;
import com.simibubi.create.foundation.collision.OrientedBB;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.tuple.MutablePair;

public class ContraptionCollider {
    private static MutablePair<WeakReference<AbstractContraptionEntity>, Double> safetyLock = new MutablePair();
    private static Map<AbstractContraptionEntity, Map<Player, Double>> remoteSafetyLocks = new WeakHashMap<AbstractContraptionEntity, Map<Player, Double>>();
    private static int packetCooldown = 0;

    static void collideEntities(AbstractContraptionEntity contraptionEntity) {
        Level world = contraptionEntity.getCommandSenderWorld();
        Contraption contraption = contraptionEntity.getContraption();
        AABB bounds = contraptionEntity.getBoundingBox();
        if (contraption == null) {
            return;
        }
        if (bounds == null) {
            return;
        }
        Vec3 contraptionPosition = contraptionEntity.position();
        Vec3 contraptionMotion = contraptionPosition.subtract(contraptionEntity.getPrevPositionVec());
        Vec3 anchorVec = contraptionEntity.getAnchorVec();
        AbstractContraptionEntity.ContraptionRotationState rotation = null;
        if (world.isClientSide() && ContraptionCollider.safetyLock.left != null && ((WeakReference)ContraptionCollider.safetyLock.left).get() == contraptionEntity) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> ContraptionCollider.saveClientPlayerFromClipping(contraptionEntity, contraptionMotion));
        }
        boolean skipClientPlayer = false;
        CollisionList denseViableColliders = new CollisionList();
        List entitiesWithinAABB = world.getEntitiesOfClass(Entity.class, bounds.inflate(2.0).expandTowards(0.0, 32.0, 0.0), contraptionEntity::canCollideWith);
        for (Entity entity : entitiesWithinAABB) {
            double d1;
            double idealVerticalMotion;
            boolean anyCollision;
            Vec3 entityMotion;
            if (!entity.isAlive() || world.tickRateManager().isEntityFrozen(entity)) continue;
            PlayerType playerType = ContraptionCollider.getPlayerType(entity);
            if (playerType == PlayerType.REMOTE) {
                if (!(contraption instanceof TranslatingContraption)) continue;
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> ContraptionCollider.saveRemotePlayerFromClipping((Player)entity, contraptionEntity, contraptionMotion));
                continue;
            }
            entity.getSelfAndPassengers().forEach(e -> {
                if (e instanceof ServerPlayer) {
                    ((ServerPlayer)e).connection.aboveGroundTickCount = 0;
                }
            });
            if (playerType == PlayerType.SERVER) continue;
            if (playerType == PlayerType.CLIENT) {
                if (skipClientPlayer) continue;
                skipClientPlayer = true;
            }
            if (rotation == null) {
                rotation = contraptionEntity.getRotationState();
            }
            Matrix3d rotationMatrix = rotation.asMatrix();
            Vec3 entityPosition = entity.position();
            AABB entityBounds = entity.getBoundingBox();
            Vec3 motion = entity.getDeltaMovement();
            float yawOffset = rotation.getYawOffset();
            Vec3 position = ContraptionCollider.getWorldToLocalTranslation(entity, anchorVec, rotationMatrix, yawOffset);
            if (playerType == PlayerType.CLIENT && entityBounds.getYsize() > 1.0) {
                entityBounds = entityBounds.contract(0.0, 0.125, 0.0);
            }
            motion = motion.subtract(contraptionMotion);
            motion = rotationMatrix.transform(motion);
            AABB localBB = entityBounds.move(position).inflate(1.0E-7);
            OrientedBB obb = new OrientedBB(localBB);
            obb.setRotation(rotationMatrix);
            CollisionList collidableBBs = contraption.getSimplifiedEntityColliders();
            if (collidableBBs == null) {
                collidableBBs = new CollisionList();
                ContraptionCollider.getPotentiallyCollidedShapes(world, contraption, localBB.expandTowards(motion), new CollisionList.Populate(collidableBBs));
            }
            ContinuousOBBCollider.CollisionResponse collisionResult = ContinuousOBBCollider.collideMany(collidableBBs, denseViableColliders, obb, motion, entity.maxUpStep(), !rotation.hasVerticalRotation());
            Vec3 entityMotionNoTemporal = entityMotion = entity.getDeltaMovement();
            Vec3 collisionNormal = collisionResult.normal;
            Vec3 collisionLocation = collisionResult.location;
            Vec3 totalResponse = collisionResult.collisionResponse;
            boolean surfaceCollision = collisionResult.surfaceCollision;
            boolean hardCollision = !totalResponse.equals((Object)Vec3.ZERO);
            boolean temporalCollision = collisionResult.temporalResponse != 1.0;
            Vec3 motionResponse = !temporalCollision ? motion : motion.normalize().scale(motion.length() * collisionResult.temporalResponse);
            motionResponse = rotationMatrix.transformTransposed(motionResponse).add(contraptionMotion);
            totalResponse = rotationMatrix.transformTransposed(totalResponse);
            totalResponse = VecHelper.rotate((Vec3)totalResponse, (double)yawOffset, (Direction.Axis)Direction.Axis.Y);
            collisionNormal = rotationMatrix.transformTransposed(collisionNormal);
            collisionNormal = VecHelper.rotate((Vec3)collisionNormal, (double)yawOffset, (Direction.Axis)Direction.Axis.Y);
            collisionNormal = collisionNormal.normalize();
            collisionLocation = rotationMatrix.transformTransposed(collisionLocation);
            collisionLocation = VecHelper.rotate((Vec3)collisionLocation, (double)yawOffset, (Direction.Axis)Direction.Axis.Y);
            double bounce = 0.0;
            double slide = 0.0;
            if (!collisionLocation.equals((Object)Vec3.ZERO)) {
                BlockState blockState;
                collisionLocation = collisionLocation.add(entity.position().add(entity.getBoundingBox().getCenter()).scale(0.5));
                if (temporalCollision) {
                    collisionLocation = collisionLocation.add(0.0, motionResponse.y, 0.0);
                }
                BlockPos pos = BlockPos.containing((Position)contraptionEntity.toLocalVector(entity.position(), 0.0f));
                if (contraption.getBlocks().containsKey(pos) && (blockState = contraption.getBlocks().get(pos).state()).is(BlockTags.CLIMBABLE)) {
                    surfaceCollision = true;
                    totalResponse = totalResponse.add(0.0, (double)0.1f, 0.0);
                }
                pos = BlockPos.containing((Position)contraptionEntity.toLocalVector(collisionLocation, 0.0f));
                if (contraption.getBlocks().containsKey(pos)) {
                    blockState = contraption.getBlocks().get(pos).state();
                    MovingInteractionBehaviour movingInteractionBehaviour = contraption.interactors.get(pos);
                    if (movingInteractionBehaviour != null) {
                        movingInteractionBehaviour.handleEntityCollision(entity, pos, contraptionEntity);
                    }
                    bounce = BlockHelper.getBounceMultiplier(blockState.getBlock());
                    slide = Math.max(0.0f, blockState.getFriction((LevelReader)contraption.collisionLevel, pos, entity) - 0.6f);
                }
            }
            boolean hasNormal = !collisionNormal.equals((Object)Vec3.ZERO);
            boolean bl = anyCollision = hardCollision || temporalCollision;
            if (bounce > 0.0 && hasNormal && anyCollision && ContraptionCollider.bounceEntity(entity, collisionNormal, contraptionEntity, bounce)) {
                entity.level().playSound(playerType == PlayerType.CLIENT ? (Player)entity : null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SLIME_BLOCK_FALL, SoundSource.BLOCKS, 0.5f, 1.0f);
                continue;
            }
            if (temporalCollision && (idealVerticalMotion = motionResponse.y) != entityMotion.y) {
                entity.setDeltaMovement(entityMotion.multiply(1.0, 0.0, 1.0).add(0.0, idealVerticalMotion, 0.0));
                entityMotion = entity.getDeltaMovement();
            }
            if (hardCollision) {
                double motionX = entityMotion.x();
                double motionY = entityMotion.y();
                double motionZ = entityMotion.z();
                double intersectX = totalResponse.x();
                double intersectY = totalResponse.y();
                double intersectZ = totalResponse.z();
                double horizonalEpsilon = 0.0078125;
                if (motionX != 0.0 && Math.abs(intersectX) > horizonalEpsilon && motionX > 0.0 == intersectX < 0.0) {
                    entityMotion = entityMotion.multiply(0.0, 1.0, 1.0);
                }
                if (motionY != 0.0 && intersectY != 0.0 && motionY > 0.0 == intersectY < 0.0) {
                    entityMotion = entityMotion.multiply(1.0, 0.0, 1.0).add(0.0, contraptionMotion.y, 0.0);
                }
                if (motionZ != 0.0 && Math.abs(intersectZ) > horizonalEpsilon && motionZ > 0.0 == intersectZ < 0.0) {
                    entityMotion = entityMotion.multiply(1.0, 1.0, 0.0);
                }
            }
            if (bounce == 0.0 && slide > 0.0 && hasNormal && anyCollision && rotation.hasVerticalRotation()) {
                double slideFactor = collisionNormal.multiply(1.0, 0.0, 1.0).length() * 1.25;
                Vec3 motionIn = entityMotionNoTemporal.multiply(0.0, 0.9, 0.0).add(0.0, (double)-0.01f, 0.0);
                Vec3 slideNormal = collisionNormal.cross(motionIn.cross(collisionNormal)).normalize();
                Vec3 newMotion = entityMotion.multiply(0.85, 0.0, 0.85).add(slideNormal.scale(((double)0.2f + slide) * motionIn.length() * slideFactor).add(0.0, (double)-0.1f - collisionNormal.y * 0.125, 0.0));
                entity.setDeltaMovement(newMotion);
                entityMotion = entity.getDeltaMovement();
            }
            if (!hardCollision && !surfaceCollision) continue;
            Vec3 allowedMovement = ContraptionCollider.collide(totalResponse, entity);
            entity.setPos(entityPosition.x + allowedMovement.x, entityPosition.y + allowedMovement.y, entityPosition.z + allowedMovement.z);
            entityPosition = entity.position();
            entityMotion = ContraptionCollider.handleDamageFromTrain(world, contraptionEntity, contraptionMotion, entity, entityMotion, playerType);
            entity.hurtMarked = true;
            Vec3 contactPointMotion = Vec3.ZERO;
            if (surfaceCollision) {
                boolean canWalk;
                contraptionEntity.registerColliding(entity);
                entity.fallDistance = 0.0f;
                for (Entity rider : entity.getIndirectPassengers()) {
                    if (ContraptionCollider.getPlayerType(rider) != PlayerType.CLIENT) continue;
                    CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ClientMotionPacket(rider.getDeltaMovement(), true, 0.0f));
                }
                boolean bl2 = canWalk = bounce != 0.0 || slide == 0.0;
                if (canWalk || !rotation.hasVerticalRotation()) {
                    if (canWalk) {
                        entity.setOnGround(true);
                    }
                    if (entity instanceof ItemEntity) {
                        entityMotion = entityMotion.multiply(0.5, 1.0, 0.5);
                    }
                }
                contactPointMotion = contraptionEntity.getContactPointMotion(entityPosition);
                allowedMovement = ContraptionCollider.collide(contactPointMotion, entity);
                entity.setPos(entityPosition.x + allowedMovement.x, entityPosition.y, entityPosition.z + allowedMovement.z);
            }
            entity.setDeltaMovement(entityMotion);
            if (playerType != PlayerType.CLIENT) continue;
            double d0 = entity.getX() - entity.xo - contactPointMotion.x;
            float limbSwing = Mth.sqrt((float)((float)(d0 * d0 + (d1 = entity.getZ() - entity.zo - contactPointMotion.z) * d1))) * 4.0f;
            if (limbSwing > 1.0f) {
                limbSwing = 1.0f;
            }
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ClientMotionPacket(entityMotion, true, limbSwing));
            if (!entity.onGround() || !(contraption instanceof TranslatingContraption)) continue;
            safetyLock.setLeft(new WeakReference<AbstractContraptionEntity>(contraptionEntity));
            safetyLock.setRight((Object)(entity.getY() - contraptionEntity.getY()));
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private static void saveClientPlayerFromClipping(AbstractContraptionEntity contraptionEntity, Vec3 contraptionMotion) {
        LocalPlayer entity = Minecraft.getInstance().player;
        if (entity.isPassenger()) {
            return;
        }
        double prevDiff = (Double)ContraptionCollider.safetyLock.right;
        double currentDiff = entity.getY() - contraptionEntity.getY();
        double motion = contraptionMotion.subtract((Vec3)entity.getDeltaMovement()).y;
        double trend = Math.signum(currentDiff - prevDiff);
        ClientPacketListener handler = entity.connection;
        if (handler.getOnlinePlayers().size() > 1) {
            if (packetCooldown > 0) {
                --packetCooldown;
            }
            if (packetCooldown == 0) {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ContraptionColliderLockPacket.ContraptionColliderLockPacketRequest(contraptionEntity.getId(), currentDiff));
                packetCooldown = 3;
            }
        }
        if (trend == 0.0) {
            return;
        }
        if (trend == Math.signum(motion)) {
            return;
        }
        double speed = contraptionMotion.multiply(0.0, 1.0, 0.0).lengthSqr();
        if (trend > 0.0 && speed < 0.1) {
            return;
        }
        if (speed < 0.05) {
            return;
        }
        if (!ContraptionCollider.savePlayerFromClipping((Player)entity, contraptionEntity, contraptionMotion, prevDiff)) {
            safetyLock.setLeft(null);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void lockPacketReceived(int contraptionId, int remotePlayerId, double suggestedOffset) {
        ClientLevel level = Minecraft.getInstance().level;
        Entity entity = level.getEntity(contraptionId);
        if (!(entity instanceof ControlledContraptionEntity)) {
            return;
        }
        ControlledContraptionEntity contraptionEntity = (ControlledContraptionEntity)entity;
        Entity entity2 = level.getEntity(remotePlayerId);
        if (!(entity2 instanceof RemotePlayer)) {
            return;
        }
        RemotePlayer player = (RemotePlayer)entity2;
        remoteSafetyLocks.computeIfAbsent(contraptionEntity, $ -> new WeakHashMap()).put(player, suggestedOffset);
    }

    @OnlyIn(value=Dist.CLIENT)
    private static void saveRemotePlayerFromClipping(Player entity, AbstractContraptionEntity contraptionEntity, Vec3 contraptionMotion) {
        if (entity.isPassenger()) {
            return;
        }
        Map locksOnThisContraption = remoteSafetyLocks.getOrDefault((Object)contraptionEntity, Collections.emptyMap());
        double prevDiff = locksOnThisContraption.getOrDefault(entity, entity.getY() - contraptionEntity.getY());
        if (!ContraptionCollider.savePlayerFromClipping(entity, contraptionEntity, contraptionMotion, prevDiff) && locksOnThisContraption.containsKey(entity)) {
            locksOnThisContraption.remove(entity);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private static boolean savePlayerFromClipping(Player entity, AbstractContraptionEntity contraptionEntity, Vec3 contraptionMotion, double yStartOffset) {
        AABB bb = entity.getBoundingBox().deflate(0.25, 0.0, 0.25);
        double shortestDistance = Double.MAX_VALUE;
        double yStart = (double)entity.maxUpStep() + contraptionEntity.getY() + yStartOffset;
        double rayLength = Math.max(5.0, Math.abs(entity.getY() - yStart));
        for (int rayIndex = 0; rayIndex < 4; ++rayIndex) {
            Vec3 end;
            Vec3 start = new Vec3(rayIndex / 2 == 0 ? bb.minX : bb.maxX, yStart, rayIndex % 2 == 0 ? bb.minZ : bb.maxZ);
            BlockHitResult hitResult = ContraptionHandlerClient.rayTraceContraption(start, end = start.add(0.0, -rayLength, 0.0), contraptionEntity);
            if (hitResult == null) continue;
            Vec3 hit = contraptionEntity.toGlobalVector(hitResult.getLocation(), 1.0f);
            double hitDiff = start.y - hit.y;
            if (!(shortestDistance > hitDiff)) continue;
            shortestDistance = hitDiff;
        }
        if (shortestDistance > rayLength) {
            return false;
        }
        entity.setPos(entity.getX(), yStart - shortestDistance, entity.getZ());
        return true;
    }

    private static Vec3 handleDamageFromTrain(Level world, AbstractContraptionEntity contraptionEntity, Vec3 contraptionMotion, Entity entity, Vec3 entityMotion, PlayerType playerType) {
        Player p;
        if (!(contraptionEntity instanceof CarriageContraptionEntity)) {
            return entityMotion;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)contraptionEntity;
        if (!entity.onGround()) {
            return entityMotion;
        }
        CompoundTag persistentData = entity.getPersistentData();
        if (persistentData.contains("ContraptionGrounded")) {
            persistentData.remove("ContraptionGrounded");
            return entityMotion;
        }
        if (cce.collidingEntities.containsKey(entity)) {
            return entityMotion;
        }
        if (entity instanceof ItemEntity) {
            return entityMotion;
        }
        if (cce.nonDamageTicks != 0) {
            return entityMotion;
        }
        if (!((Boolean)AllConfigs.server().trains.trainsCauseDamage.get()).booleanValue()) {
            return entityMotion;
        }
        Vec3 diffMotion = contraptionMotion.subtract(entity.getDeltaMovement());
        if (diffMotion.length() <= (double)0.35f || contraptionMotion.length() <= (double)0.35f) {
            return entityMotion;
        }
        DamageSource source = CreateDamageSources.runOver(world, contraptionEntity);
        double damage = diffMotion.length();
        if (entity.getClassification(false) == MobCategory.MONSTER) {
            damage *= 2.0;
        }
        if (entity instanceof Player && ((p = (Player)entity).isCreative() || p.isSpectator())) {
            return entityMotion;
        }
        if (playerType == PlayerType.CLIENT) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new TrainCollisionPacket((int)(damage * 16.0), contraptionEntity.getId()));
            world.playSound((Player)entity, entity.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.NEUTRAL, 1.0f, 0.75f);
        } else {
            entity.hurt(source, (float)((int)(damage * 16.0)));
            world.playSound(null, entity.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.NEUTRAL, 1.0f, 0.75f);
            if (!entity.isAlive()) {
                contraptionEntity.getControllingPlayer().map(arg_0 -> ((Level)world).getPlayerByUUID(arg_0)).ifPresent(AllAdvancements.TRAIN_ROADKILL::awardTo);
            }
        }
        Vec3 added = entityMotion.add(contraptionMotion.multiply(1.0, 0.0, 1.0).normalize().add(0.0, 0.25, 0.0).scale(damage * 4.0)).add(diffMotion);
        return VecHelper.clamp((Vec3)added, (float)3.0f);
    }

    static boolean bounceEntity(Entity entity, Vec3 normal, AbstractContraptionEntity contraption, double factor) {
        if (factor == 0.0) {
            return false;
        }
        if (entity.isSuppressingBounce()) {
            return false;
        }
        Vec3 contactPointMotion = contraption.getContactPointMotion(entity.position());
        Vec3 motion = entity.getDeltaMovement().subtract(contactPointMotion);
        Vec3 deltav = normal.scale(factor * 2.0 * motion.dot(normal));
        if (deltav.dot(deltav) < (double)0.1f) {
            return false;
        }
        entity.setDeltaMovement(entity.getDeltaMovement().subtract(deltav));
        return true;
    }

    public static Vec3 getWorldToLocalTranslation(Entity entity, Vec3 anchorVec, Matrix3d rotationMatrix, float yawOffset) {
        Vec3 entityPosition = entity.position();
        Vec3 centerY = new Vec3(0.0, entity.getBoundingBox().getYsize() / 2.0, 0.0);
        Vec3 position = entityPosition;
        position = position.add(centerY);
        position = ContraptionCollider.worldToLocalPos(position, anchorVec, rotationMatrix, yawOffset);
        position = position.subtract(centerY);
        position = position.subtract(entityPosition);
        return position;
    }

    public static Vec3 worldToLocalPos(Vec3 worldPos, AbstractContraptionEntity contraptionEntity) {
        return ContraptionCollider.worldToLocalPos(worldPos, contraptionEntity.getAnchorVec(), contraptionEntity.getRotationState());
    }

    public static Vec3 worldToLocalPos(Vec3 worldPos, Vec3 anchorVec, AbstractContraptionEntity.ContraptionRotationState rotation) {
        return ContraptionCollider.worldToLocalPos(worldPos, anchorVec, rotation.asMatrix(), rotation.getYawOffset());
    }

    public static Vec3 worldToLocalPos(Vec3 worldPos, Vec3 anchorVec, Matrix3d rotationMatrix, float yawOffset) {
        Vec3 localPos = worldPos;
        localPos = localPos.subtract(anchorVec);
        localPos = localPos.subtract(VecHelper.CENTER_OF_ORIGIN);
        localPos = VecHelper.rotate((Vec3)localPos, (double)(-yawOffset), (Direction.Axis)Direction.Axis.Y);
        localPos = rotationMatrix.transform(localPos);
        localPos = localPos.add(VecHelper.CENTER_OF_ORIGIN);
        return localPos;
    }

    static Vec3 collide(Vec3 p_20273_, Entity e) {
        boolean flag3;
        AABB aabb = e.getBoundingBox();
        List list = e.level().getEntityCollisions(e, aabb.expandTowards(p_20273_));
        Vec3 vec3 = p_20273_.lengthSqr() == 0.0 ? p_20273_ : Entity.collideBoundingBox((Entity)e, (Vec3)p_20273_, (AABB)aabb, (Level)e.level(), (List)list);
        boolean flag = p_20273_.x != vec3.x;
        boolean flag1 = p_20273_.y != vec3.y;
        boolean flag2 = p_20273_.z != vec3.z;
        boolean bl = flag3 = flag1 && p_20273_.y < 0.0;
        if (e.maxUpStep() > 0.0f && flag3 && (flag || flag2)) {
            Vec3 vec33;
            Vec3 vec31 = Entity.collideBoundingBox((Entity)e, (Vec3)new Vec3(p_20273_.x, (double)e.maxUpStep(), p_20273_.z), (AABB)aabb, (Level)e.level(), (List)list);
            Vec3 vec32 = Entity.collideBoundingBox((Entity)e, (Vec3)new Vec3(0.0, (double)e.maxUpStep(), 0.0), (AABB)aabb.expandTowards(p_20273_.x, 0.0, p_20273_.z), (Level)e.level(), (List)list);
            if (vec32.y < (double)e.maxUpStep() && (vec33 = Entity.collideBoundingBox((Entity)e, (Vec3)new Vec3(p_20273_.x, 0.0, p_20273_.z), (AABB)aabb.move(vec32), (Level)e.level(), (List)list).add(vec32)).horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
                vec31 = vec33;
            }
            if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
                return vec31.add(Entity.collideBoundingBox((Entity)e, (Vec3)new Vec3(0.0, -vec31.y + p_20273_.y, 0.0), (AABB)aabb.move(vec31), (Level)e.level(), (List)list));
            }
        }
        return vec3;
    }

    private static PlayerType getPlayerType(Entity entity) {
        if (!(entity instanceof Player)) {
            return PlayerType.NONE;
        }
        if (!entity.level().isClientSide) {
            return PlayerType.SERVER;
        }
        MutableBoolean isClient = new MutableBoolean(false);
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> isClient.setValue(ContraptionCollider.isClientPlayerEntity(entity)));
        return isClient.booleanValue() ? PlayerType.CLIENT : PlayerType.REMOTE;
    }

    @OnlyIn(value=Dist.CLIENT)
    private static boolean isClientPlayerEntity(Entity entity) {
        return entity instanceof LocalPlayer;
    }

    private static void getPotentiallyCollidedShapes(Level world, Contraption contraption, AABB localBB, Shapes.DoubleLineConsumer out) {
        double width;
        double height = localBB.getYsize();
        double horizontalFactor = height > (width = localBB.getXsize()) && width != 0.0 ? height / width : 1.0;
        double verticalFactor = width > height && height != 0.0 ? width / height : 1.0;
        AABB blockScanBB = localBB.inflate(0.5);
        blockScanBB = blockScanBB.inflate(horizontalFactor, verticalFactor, horizontalFactor);
        BlockPos min = BlockPos.containing((double)blockScanBB.minX, (double)blockScanBB.minY, (double)blockScanBB.minZ);
        BlockPos max = BlockPos.containing((double)blockScanBB.maxX, (double)blockScanBB.maxY, (double)blockScanBB.maxZ);
        for (BlockPos p : BlockPos.betweenClosed((BlockPos)min, (BlockPos)max)) {
            if (!contraption.blocks.containsKey(p) || contraption.isHiddenInPortal(p)) continue;
            StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(p);
            BlockState blockState = info.state();
            BlockPos pos = info.pos();
            VoxelShape collisionShape = blockState.getCollisionShape((BlockGetter)world, p).move((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
            if (collisionShape.isEmpty()) continue;
            collisionShape.forAllBoxes(out);
        }
    }

    public static boolean collideBlocks(AbstractContraptionEntity contraptionEntity) {
        if (!contraptionEntity.supportsTerrainCollision()) {
            return false;
        }
        Level world = contraptionEntity.getCommandSenderWorld();
        Vec3 motion = contraptionEntity.getDeltaMovement();
        TranslatingContraption contraption = (TranslatingContraption)contraptionEntity.getContraption();
        AABB bounds = contraptionEntity.getBoundingBox();
        Vec3 position = contraptionEntity.position();
        BlockPos gridPos = BlockPos.containing((Position)position);
        if (contraption == null) {
            return false;
        }
        if (bounds == null) {
            return false;
        }
        if (motion.equals((Object)Vec3.ZERO)) {
            return false;
        }
        Direction movementDirection = Direction.getNearest((double)motion.x, (double)motion.y, (double)motion.z);
        if (movementDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            gridPos = gridPos.relative(movementDirection);
        }
        if (ContraptionCollider.isCollidingWithWorld(world, contraption, gridPos, movementDirection)) {
            return true;
        }
        for (ControlledContraptionEntity otherContraptionEntity : world.getEntitiesOfClass(ControlledContraptionEntity.class, bounds.inflate(1.0), e -> !e.equals((Object)contraptionEntity))) {
            if (!otherContraptionEntity.supportsTerrainCollision()) continue;
            Vec3 otherMotion = otherContraptionEntity.getDeltaMovement();
            TranslatingContraption otherContraption = (TranslatingContraption)otherContraptionEntity.getContraption();
            AABB otherBounds = otherContraptionEntity.getBoundingBox();
            Vec3 otherPosition = otherContraptionEntity.position();
            if (otherContraption == null) {
                return false;
            }
            if (otherBounds == null) {
                return false;
            }
            if (!bounds.move(motion).intersects(otherBounds.move(otherMotion))) continue;
            for (BlockPos colliderPos : contraption.getOrCreateColliders(world, movementDirection)) {
                colliderPos = colliderPos.offset((Vec3i)gridPos).subtract((Vec3i)BlockPos.containing((Position)otherPosition));
                if (!otherContraption.getBlocks().containsKey(colliderPos)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean isCollidingWithWorld(Level world, TranslatingContraption contraption, BlockPos anchor, Direction movementDirection) {
        for (BlockPos pos : contraption.getOrCreateColliders(world, movementDirection)) {
            BlockPos colliderPos = pos.offset((Vec3i)anchor);
            if (!world.isLoaded(colliderPos)) {
                return true;
            }
            BlockState collidedState = world.getBlockState(colliderPos);
            StructureTemplate.StructureBlockInfo blockInfo = contraption.getBlocks().get(pos);
            boolean emptyCollider = collidedState.getCollisionShape((BlockGetter)world, pos).isEmpty();
            if (collidedState.getBlock() instanceof CocoaBlock) continue;
            MovementBehaviour movementBehaviour = MovementBehaviour.REGISTRY.get((StateHolder<Block, ?>)blockInfo.state());
            if (movementBehaviour != null) {
                if (movementBehaviour instanceof BlockBreakingMovementBehaviour) {
                    BlockBreakingMovementBehaviour behaviour = (BlockBreakingMovementBehaviour)movementBehaviour;
                    if (behaviour.canBreak(world, colliderPos, collidedState) || emptyCollider) continue;
                    return true;
                }
                if (movementBehaviour instanceof HarvesterMovementBehaviour) {
                    HarvesterMovementBehaviour harvesterMovementBehaviour = (HarvesterMovementBehaviour)movementBehaviour;
                    if (harvesterMovementBehaviour.isValidCrop(world, colliderPos, collidedState) || harvesterMovementBehaviour.isValidOther(world, colliderPos, collidedState) || emptyCollider) continue;
                    return true;
                }
            }
            if (AllBlocks.PULLEY_MAGNET.has(collidedState) && pos.equals((Object)BlockPos.ZERO) && movementDirection == Direction.UP || collidedState.canBeReplaced() || emptyCollider) continue;
            return true;
        }
        return false;
    }

    static enum PlayerType {
        NONE,
        CLIENT,
        REMOTE,
        SERVER;

    }
}
