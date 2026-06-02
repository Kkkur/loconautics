/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.fan;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.AirCurrentSound;
import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class AirCurrent {
    public final IAirCurrentSource source;
    public AABB bounds = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    public List<AirCurrentSegment> segments = new ArrayList<AirCurrentSegment>();
    public Direction direction;
    public boolean pushing;
    public float maxDistance;
    protected List<Pair<TransportedItemStackHandlerBehaviour, FanProcessingType>> affectedItemHandlers = new ArrayList<Pair<TransportedItemStackHandlerBehaviour, FanProcessingType>>();
    protected List<Entity> caughtEntities = new ArrayList<Entity>();
    private static final double[][] DEPTH_TEST_COORDINATES = new double[][]{{0.25, 0.25}, {0.25, 0.75}, {0.5, 0.5}, {0.75, 0.25}, {0.75, 0.75}};

    public AirCurrent(IAirCurrentSource source) {
        this.source = source;
    }

    public void tick() {
        Level world;
        if (this.direction == null) {
            this.rebuild();
        }
        if ((world = this.source.getAirCurrentWorld()) != null && world.isClientSide) {
            float offset = this.pushing ? 0.5f : this.maxDistance + 0.5f;
            Vec3 pos = VecHelper.getCenterOf((Vec3i)this.source.getAirCurrentPos()).add(Vec3.atLowerCornerOf((Vec3i)this.direction.getNormal()).scale((double)offset));
            if ((double)world.random.nextFloat() < (Double)AllConfigs.client().fanParticleDensity.get()) {
                world.addParticle((ParticleOptions)new AirFlowParticleData((Vec3i)this.source.getAirCurrentPos()), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
            }
        }
        this.tickAffectedEntities(world);
        this.tickAffectedHandlers();
    }

    protected void tickAffectedEntities(Level world) {
        Iterator<Entity> iterator = this.caughtEntities.iterator();
        while (iterator.hasNext()) {
            FanProcessingType processingType;
            Entity entity = iterator.next();
            if (!entity.isAlive() || !entity.getBoundingBox().intersects(this.bounds) || AirCurrent.isPlayerCreativeFlying(entity)) {
                iterator.remove();
                continue;
            }
            Vec3i flow = (this.pushing ? this.direction : this.direction.getOpposite()).getNormal();
            float speed = Math.abs(this.source.getSpeed());
            float sneakModifier = entity.isShiftKeyDown() ? 4096.0f : 512.0f;
            double entityDistance = VecHelper.alignedDistanceToFace((Vec3)entity.position(), (BlockPos)this.source.getAirCurrentPos(), (Direction)this.direction);
            double entityDistanceOld = entity.position().distanceTo(VecHelper.getCenterOf((Vec3i)this.source.getAirCurrentPos()));
            float acceleration = (float)((double)(speed / sneakModifier) / (entityDistanceOld / (double)this.maxDistance));
            Vec3 previousMotion = entity.getDeltaMovement();
            float maxAcceleration = 5.0f;
            double xIn = Mth.clamp((double)((double)((float)flow.getX() * acceleration) - previousMotion.x), (double)(-maxAcceleration), (double)maxAcceleration);
            double yIn = Mth.clamp((double)((double)((float)flow.getY() * acceleration) - previousMotion.y), (double)(-maxAcceleration), (double)maxAcceleration);
            double zIn = Mth.clamp((double)((double)((float)flow.getZ() * acceleration) - previousMotion.z), (double)(-maxAcceleration), (double)maxAcceleration);
            entity.setDeltaMovement(previousMotion.add(new Vec3(xIn, yIn, zIn).scale(0.125)));
            entity.fallDistance = 0.0f;
            if (CatnipServices.PLATFORM.getEnv().isClient()) {
                Client.enableClientPlayerSound(entity, Mth.clamp((float)(speed / 128.0f * 0.4f), (float)0.01f, (float)0.4f));
            }
            if (entity instanceof ServerPlayer) {
                ((ServerPlayer)entity).connection.aboveGroundTickCount = 0;
            }
            if ((processingType = this.getTypeAt((float)entityDistance)) == null) continue;
            if (entity instanceof ItemEntity) {
                IAirCurrentSource iAirCurrentSource;
                ItemEntity itemEntity = (ItemEntity)entity;
                if (world != null && world.isClientSide) {
                    processingType.spawnProcessingParticles(world, entity.position());
                    continue;
                }
                if (!FanProcessing.canProcess(itemEntity, processingType) || !FanProcessing.applyProcessing(itemEntity, processingType) || !((iAirCurrentSource = this.source) instanceof EncasedFanBlockEntity)) continue;
                EncasedFanBlockEntity fan = (EncasedFanBlockEntity)iAirCurrentSource;
                fan.award(AllAdvancements.FAN_PROCESSING);
                continue;
            }
            if (world == null) continue;
            processingType.affectEntity(entity, world);
        }
    }

    public static boolean isPlayerCreativeFlying(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            return player.isCreative() && player.getAbilities().flying;
        }
        return false;
    }

    public void tickAffectedHandlers() {
        for (Pair<TransportedItemStackHandlerBehaviour, FanProcessingType> pair : this.affectedItemHandlers) {
            TransportedItemStackHandlerBehaviour handler = (TransportedItemStackHandlerBehaviour)pair.getKey();
            Level world = handler.getWorld();
            FanProcessingType processingType = (FanProcessingType)pair.getRight();
            if (processingType == null) continue;
            handler.handleProcessingOnAllItems(transported -> {
                IAirCurrentSource patt0$temp;
                if (world.isClientSide) {
                    processingType.spawnProcessingParticles(world, handler.getWorldPositionOf((TransportedItemStack)transported));
                    return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
                }
                TransportedItemStackHandlerBehaviour.TransportedResult applyProcessing = FanProcessing.applyProcessing(transported, world, processingType);
                if (!applyProcessing.doesNothing() && (patt0$temp = this.source) instanceof EncasedFanBlockEntity) {
                    EncasedFanBlockEntity fan = (EncasedFanBlockEntity)patt0$temp;
                    fan.award(AllAdvancements.FAN_PROCESSING);
                }
                return applyProcessing;
            });
        }
    }

    public void rebuild() {
        if (this.source.getSpeed() == 0.0f) {
            this.maxDistance = 0.0f;
            this.segments.clear();
            this.bounds = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            return;
        }
        this.direction = this.source.getAirflowOriginSide();
        this.pushing = this.source.getAirFlowDirection() == this.direction;
        this.maxDistance = this.source.getMaxDistance();
        Level world = this.source.getAirCurrentWorld();
        BlockPos start = this.source.getAirCurrentPos();
        float max = this.maxDistance;
        Direction facing = this.direction;
        Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)facing.getNormal());
        this.maxDistance = AirCurrent.getFlowLimit(world, start, max, facing);
        this.segments.clear();
        AirCurrentSegment currentSegment = null;
        FanProcessingType type = null;
        int limit = this.getLimit();
        int searchStart = this.pushing ? 1 : limit;
        int searchEnd = this.pushing ? limit : 1;
        int searchStep = this.pushing ? 1 : -1;
        int toOffset = this.pushing ? -1 : 0;
        int i = searchStart;
        while (i * searchStep <= searchEnd * searchStep) {
            BlockPos currentPos = start.relative(this.direction, i);
            FanProcessingType newType = FanProcessingType.getAt(world, currentPos);
            if (newType != null) {
                type = newType;
            }
            if (currentSegment == null) {
                currentSegment = new AirCurrentSegment();
                currentSegment.startOffset = i + toOffset;
                currentSegment.type = type;
            } else if (currentSegment.type != type) {
                currentSegment.endOffset = i + toOffset;
                this.segments.add(currentSegment);
                currentSegment = new AirCurrentSegment();
                currentSegment.startOffset = i + toOffset;
                currentSegment.type = type;
            }
            i += searchStep;
        }
        if (currentSegment != null) {
            currentSegment.endOffset = searchEnd + searchStep + toOffset;
            this.segments.add(currentSegment);
        }
        if (this.maxDistance < 0.25f) {
            this.bounds = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        } else {
            float factor = this.maxDistance - 1.0f;
            Vec3 scale = directionVec.scale((double)factor);
            this.bounds = factor > 0.0f ? new AABB(start.relative(this.direction)).expandTowards(scale) : new AABB(start.relative(this.direction)).contract(scale.x, scale.y, scale.z).move(scale);
        }
        this.findAffectedHandlers();
    }

    public static float getFlowLimit(Level world, BlockPos start, float max, Direction facing) {
        int i = 0;
        while ((float)i < max) {
            VoxelShape shape;
            BlockPos currentPos = start.relative(facing, i + 1);
            if (!world.isLoaded(currentPos)) {
                return i;
            }
            BlockState state = world.getBlockState(currentPos);
            BlockState copycatState = CopycatBlock.getMaterial((BlockGetter)world, currentPos);
            if (!AirCurrent.shouldAlwaysPass(copycatState.isAir() ? state : copycatState) && !(shape = state.getCollisionShape((BlockGetter)world, currentPos)).isEmpty()) {
                if (shape == Shapes.block()) {
                    return i;
                }
                double shapeDepth = AirCurrent.findMaxDepth(shape, facing);
                if (shapeDepth != Double.POSITIVE_INFINITY) {
                    return Math.min((float)((double)i + shapeDepth + 0.03125), max);
                }
            }
            ++i;
        }
        return max;
    }

    private static double findMaxDepth(VoxelShape shape, Direction direction) {
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getAxisDirection();
        double maxDepth = 0.0;
        for (double[] coordinates : DEPTH_TEST_COORDINATES) {
            double depth;
            if (axisDirection == Direction.AxisDirection.POSITIVE) {
                double min = shape.min(axis, coordinates[0], coordinates[1]);
                if (min == Double.POSITIVE_INFINITY) {
                    return Double.POSITIVE_INFINITY;
                }
                depth = min;
            } else {
                double max = shape.max(axis, coordinates[0], coordinates[1]);
                if (max == Double.NEGATIVE_INFINITY) {
                    return Double.POSITIVE_INFINITY;
                }
                depth = 1.0 - max;
            }
            if (!(depth > maxDepth)) continue;
            maxDepth = depth;
        }
        return maxDepth;
    }

    private static boolean shouldAlwaysPass(BlockState state) {
        return AllTags.AllBlockTags.FAN_TRANSPARENT.matches(state);
    }

    private int getLimit() {
        if ((float)((int)this.maxDistance) == this.maxDistance) {
            return (int)this.maxDistance;
        }
        return (int)this.maxDistance + 1;
    }

    public void findAffectedHandlers() {
        Level world = this.source.getAirCurrentWorld();
        BlockPos start = this.source.getAirCurrentPos();
        this.affectedItemHandlers.clear();
        int limit = this.getLimit();
        block0: for (int i = 1; i <= limit; ++i) {
            FanProcessingType segmentType = this.getTypeAt(i - 1);
            for (int offset : Iterate.zeroAndOne) {
                BlockPos pos = start.relative(this.direction, i).below(offset);
                TransportedItemStackHandlerBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)world, pos, TransportedItemStackHandlerBehaviour.TYPE);
                if (behaviour != null) {
                    FanProcessingType type = FanProcessingType.getAt(world, pos);
                    if (type == null) {
                        type = segmentType;
                    }
                    this.affectedItemHandlers.add((Pair<TransportedItemStackHandlerBehaviour, FanProcessingType>)Pair.of((Object)behaviour, (Object)type));
                }
                if (this.direction.getAxis().isVertical()) continue block0;
            }
        }
    }

    public void findEntities() {
        this.caughtEntities.clear();
        this.caughtEntities = this.source.getAirCurrentWorld().getEntities(null, this.bounds);
    }

    @Nullable
    public FanProcessingType getTypeAt(float offset) {
        block4: {
            if (!(offset >= 0.0f) || !(offset <= this.maxDistance)) break block4;
            if (this.pushing) {
                for (AirCurrentSegment airCurrentSegment : this.segments) {
                    if (!(offset <= (float)airCurrentSegment.endOffset)) continue;
                    return airCurrentSegment.type;
                }
            } else {
                for (AirCurrentSegment airCurrentSegment : this.segments) {
                    if (!(offset >= (float)airCurrentSegment.endOffset)) continue;
                    return airCurrentSegment.type;
                }
            }
        }
        return null;
    }

    public static class Client {
        private static boolean isClientPlayerInAirCurrent;
        private static AirCurrentSound flyingSound;

        private static void enableClientPlayerSound(Entity e, float maxVolume) {
            if (e != Minecraft.getInstance().getCameraEntity()) {
                return;
            }
            isClientPlayerInAirCurrent = true;
            float pitch = (float)Mth.clamp((double)(e.getDeltaMovement().length() * 0.5), (double)0.5, (double)2.0);
            if (flyingSound == null || flyingSound.isStopped()) {
                flyingSound = new AirCurrentSound(SoundEvents.ELYTRA_FLYING, pitch);
                Minecraft.getInstance().getSoundManager().play((SoundInstance)flyingSound);
            }
            flyingSound.setPitch(pitch);
            flyingSound.fadeIn(maxVolume);
        }

        public static void tickClientPlayerSounds() {
            if (!isClientPlayerInAirCurrent && flyingSound != null) {
                if (flyingSound.isFaded()) {
                    flyingSound.stopSound();
                } else {
                    flyingSound.fadeOut();
                }
            }
            isClientPlayerInAirCurrent = false;
        }
    }

    private static class AirCurrentSegment {
        @Nullable
        private FanProcessingType type;
        private int startOffset;
        private int endOffset;

        private AirCurrentSegment() {
        }
    }
}
