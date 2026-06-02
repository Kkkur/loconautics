/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.decoration.HangingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.belt.transport;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.armor.CardboardArmorHandler;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BeltMovementHandler {
    public static boolean canBeTransported(Entity entity) {
        Player p;
        if (!entity.isAlive()) {
            return false;
        }
        return !(entity instanceof Player) || !(p = (Player)entity).isShiftKeyDown() || CardboardArmorHandler.testForStealth(entity);
    }

    public static void transportEntity(BeltBlockEntity beltBE, Entity entityIn, TransportedEntityInfo info) {
        boolean movedPastEndingSlope;
        boolean movingUp;
        boolean movingDown;
        boolean onSlope;
        Vec3 movement;
        float movementSpeed;
        boolean isPlayer;
        Level world;
        block23: {
            Vec3 centering;
            block22: {
                double diffCenter;
                boolean notHorizontal;
                boolean betweenBelts;
                BlockPos pos = info.lastCollidedPos;
                world = beltBE.getLevel();
                BlockEntity be = world.getBlockEntity(pos);
                BlockEntity blockEntityBelowPassenger = world.getBlockEntity(entityIn.blockPosition());
                BlockState blockState = info.lastCollidedState;
                Direction movementFacing = Direction.fromAxisAndDirection((Direction.Axis)((Direction)blockState.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).getAxis(), (Direction.AxisDirection)(beltBE.getSpeed() < 0.0f ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE));
                boolean collidedWithBelt = be instanceof BeltBlockEntity;
                boolean bl = betweenBelts = blockEntityBelowPassenger instanceof BeltBlockEntity && blockEntityBelowPassenger != be;
                if (!collidedWithBelt || betweenBelts) {
                    return;
                }
                boolean bl2 = notHorizontal = beltBE.getBlockState().getValue(BeltBlock.SLOPE) != BeltSlope.HORIZONTAL;
                if (Math.abs(beltBE.getSpeed()) < 1.0f) {
                    return;
                }
                if (entityIn.getY() - 0.25 < (double)pos.getY()) {
                    return;
                }
                isPlayer = entityIn instanceof Player;
                if (entityIn instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity)entityIn;
                    if (!isPlayer) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1, false, false));
                    }
                }
                Direction beltFacing = (Direction)blockState.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
                BeltSlope slope = (BeltSlope)((Object)blockState.getValue(BeltBlock.SLOPE));
                Direction.Axis axis = beltFacing.getAxis();
                movementSpeed = beltBE.getBeltMovementSpeed();
                Direction movementDirection = Direction.get((Direction.AxisDirection)(axis == Direction.Axis.X ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE), (Direction.Axis)axis);
                Vec3i centeringDirection = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)beltFacing.getClockWise().getAxis()).getNormal();
                movement = Vec3.atLowerCornerOf((Vec3i)movementDirection.getNormal()).scale((double)movementSpeed);
                double d = diffCenter = axis == Direction.Axis.Z ? (double)((float)pos.getX() + 0.5f) - entityIn.getX() : (double)((float)pos.getZ() + 0.5f) - entityIn.getZ();
                if (Math.abs(diffCenter) > 0.75) {
                    return;
                }
                BeltPart part = (BeltPart)((Object)blockState.getValue(BeltBlock.PART));
                float top = 0.8125f;
                boolean bl3 = notHorizontal && (part == BeltPart.MIDDLE || part == BeltPart.PULLEY || part == (slope == BeltSlope.UPWARD ? BeltPart.END : BeltPart.START) && entityIn.getY() - (double)pos.getY() < (double)top || part == (slope == BeltSlope.UPWARD ? BeltPart.START : BeltPart.END) && entityIn.getY() - (double)pos.getY() > (double)top) ? true : (onSlope = false);
                boolean bl4 = onSlope && slope == (movementFacing == beltFacing ? BeltSlope.DOWNWARD : BeltSlope.UPWARD) ? true : (movingDown = false);
                boolean bl5 = onSlope && slope == (movementFacing == beltFacing ? BeltSlope.UPWARD : BeltSlope.DOWNWARD) ? true : (movingUp = false);
                if (beltFacing.getAxis() == Direction.Axis.Z) {
                    boolean b = movingDown;
                    movingDown = movingUp;
                    movingUp = b;
                }
                if (movingUp) {
                    movement = movement.add(0.0, Math.abs(axis.choose(movement.x, movement.y, movement.z)), 0.0);
                }
                if (movingDown) {
                    movement = movement.add(0.0, -Math.abs(axis.choose(movement.x, movement.y, movement.z)), 0.0);
                }
                centering = Vec3.atLowerCornerOf((Vec3i)centeringDirection).scale(diffCenter * (double)Math.min(Math.abs(movementSpeed), 0.1f) * 4.0);
                if (!(entityIn instanceof LivingEntity)) break block22;
                LivingEntity livingEntity = (LivingEntity)entityIn;
                if (livingEntity.zza != 0.0f || livingEntity.xxa != 0.0f) break block23;
            }
            movement = movement.add(centering);
        }
        float step = entityIn.maxUpStep();
        if (!isPlayer && entityIn instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entityIn;
            step = (float)livingEntity.getAttributeBaseValue(Attributes.STEP_HEIGHT);
            livingEntity.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0);
        }
        if (Math.abs(movementSpeed) < 0.5f) {
            Vec3 checkDistance = movement.normalize().scale(0.5);
            AABB bb = entityIn.getBoundingBox();
            AABB checkBB = new AABB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
            checkBB = checkBB.move(checkDistance).inflate(-Math.abs(checkDistance.x), -Math.abs(checkDistance.y), -Math.abs(checkDistance.z));
            List list = world.getEntities(entityIn, checkBB);
            list.removeIf(e -> BeltMovementHandler.shouldIgnoreBlocking(entityIn, e));
            if (!list.isEmpty()) {
                entityIn.setDeltaMovement(0.0, 0.0, 0.0);
                --info.ticksSinceLastCollision;
                return;
            }
        }
        entityIn.fallDistance = 0.0f;
        if (movingUp) {
            float minVelocity = 0.13f;
            float yMovement = (float)(-Math.max(Math.abs(movement.y), (double)minVelocity));
            entityIn.move(MoverType.SELF, new Vec3(0.0, (double)yMovement, 0.0));
            entityIn.move(MoverType.SELF, movement.multiply(1.0, 0.0, 1.0));
        } else if (movingDown) {
            entityIn.move(MoverType.SELF, movement.multiply(1.0, 0.0, 1.0));
            entityIn.move(MoverType.SELF, movement.multiply(0.0, 1.0, 0.0));
        } else {
            entityIn.move(MoverType.SELF, movement);
        }
        entityIn.setOnGround(true);
        if (!isPlayer && entityIn instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entityIn;
            livingEntity.getAttribute(Attributes.STEP_HEIGHT).setBaseValue((double)step);
        }
        boolean bl = movedPastEndingSlope = onSlope && (AllBlocks.BELT.has(world.getBlockState(entityIn.blockPosition())) || AllBlocks.BELT.has(world.getBlockState(entityIn.blockPosition().below())));
        if (movedPastEndingSlope && !movingDown && Math.abs(movementSpeed) > 0.0f) {
            entityIn.setPos(entityIn.getX(), entityIn.getY() + movement.y, entityIn.getZ());
        }
        if (movedPastEndingSlope) {
            entityIn.setDeltaMovement(movement);
            entityIn.hurtMarked = true;
        }
    }

    public static boolean shouldIgnoreBlocking(Entity me, Entity other) {
        if (other instanceof HangingEntity) {
            return true;
        }
        if (other.getPistonPushReaction() == PushReaction.IGNORE) {
            return true;
        }
        return BeltMovementHandler.isRidingOrBeingRiddenBy(me, other);
    }

    public static boolean isRidingOrBeingRiddenBy(Entity me, Entity other) {
        for (Entity entity : me.getPassengers()) {
            if (entity.equals((Object)other)) {
                return true;
            }
            if (!BeltMovementHandler.isRidingOrBeingRiddenBy(entity, other)) continue;
            return true;
        }
        return false;
    }

    public static class TransportedEntityInfo {
        int ticksSinceLastCollision;
        BlockPos lastCollidedPos;
        BlockState lastCollidedState;

        public TransportedEntityInfo(BlockPos collision, BlockState belt) {
            this.refresh(collision, belt);
        }

        public void refresh(BlockPos collision, BlockState belt) {
            this.ticksSinceLastCollision = 0;
            this.lastCollidedPos = new BlockPos((Vec3i)collision).immutable();
            this.lastCollidedState = belt;
        }

        public TransportedEntityInfo tick() {
            ++this.ticksSinceLastCollision;
            return this;
        }

        public int getTicksSinceLastCollision() {
            return this.ticksSinceLastCollision;
        }
    }
}
