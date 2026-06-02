/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 *  com.simibubi.create.api.schematic.requirement.SpecialEntityItemRequirement
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$ItemUseType
 *  com.simibubi.create.foundation.networking.ISyncPersistentData
 *  com.simibubi.create.foundation.utility.IInteractionChecker
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.physics.force.ForceGroup
 *  dev.ryanhcode.sable.api.physics.force.ForceGroups
 *  dev.ryanhcode.sable.api.physics.force.QueuedForceGroup
 *  dev.ryanhcode.sable.api.physics.force.QueuedForceGroup$PointForce
 *  dev.ryanhcode.sable.api.physics.mass.MassData
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem
 *  foundry.veil.api.network.VeilPacketManager
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.decoration.HangingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DiodeBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.entities.diagram;

import com.mojang.serialization.DynamicOps;
import com.simibubi.create.api.schematic.requirement.SpecialEntityItemRequirement;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.networking.ISyncPersistentData;
import com.simibubi.create.foundation.utility.IInteractionChecker;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.entities.diagram.DiagramConfig;
import dev.simulated_team.simulated.content.entities.diagram.DiagramRecordingTicket;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.index.SimEntityTypes;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.network.packets.contraption_diagram.DiagramDataPacket;
import dev.simulated_team.simulated.network.packets.contraption_diagram.DiagramOpenPacket;
import dev.simulated_team.simulated.util.SimColors;
import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class DiagramEntity
extends HangingEntity
implements ISyncPersistentData,
IInteractionChecker,
SpecialEntityItemRequirement {
    private static final Map<ResourceKey<Level>, Map<ServerSubLevel, DiagramRecordingTicket>> queuedDiagramRecordings = new WeakHashMap<ResourceKey<Level>, Map<ServerSubLevel, DiagramRecordingTicket>>();
    protected int size;
    protected Direction verticalOrientation;
    protected DiagramConfig config;

    public static DiagramEntity create(EntityType<? extends HangingEntity> entityType, Level world) {
        return new DiagramEntity(entityType, world);
    }

    public DiagramEntity(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
        this.size = 1;
        this.config = DiagramConfig.makeDefault(this);
    }

    public DiagramEntity(Level world, BlockPos pos, Direction facing, Direction verticalOrientation) {
        super((EntityType)SimEntityTypes.CONTRAPTION_DIAGRAM.get(), world, pos);
        int size = 3;
        while (size > 0) {
            this.size = size--;
            this.updateFacingWithBoundingBox(facing, verticalOrientation);
            if (this.survives()) break;
        }
        this.config = DiagramConfig.makeDefault(this);
    }

    public static void queueDiagramDataFor(SubLevel subLevel, ServerPlayer player) {
        ObjectArrayList players;
        DiagramRecordingTicket ticket;
        if (!(subLevel instanceof ServerSubLevel)) {
            return;
        }
        ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
        serverSubLevel.enableIndividualQueuedForcesTracking(true);
        Map<ServerSubLevel, DiagramRecordingTicket> map = queuedDiagramRecordings.get(serverSubLevel.getLevel().dimension());
        DiagramRecordingTicket diagramRecordingTicket = ticket = map != null ? map.get(serverSubLevel) : null;
        if (ticket != null && !ticket.isValid()) {
            queuedDiagramRecordings.remove(serverSubLevel);
            ticket = null;
        }
        if (ticket == null) {
            players = new ObjectArrayList();
            ticket = new DiagramRecordingTicket(serverSubLevel, (List<ServerPlayer>)players);
            queuedDiagramRecordings.computeIfAbsent((ResourceKey<Level>)serverSubLevel.getLevel().dimension(), x -> new Object2ObjectOpenHashMap()).put(serverSubLevel, ticket);
        }
        if (!(players = ticket.players()).contains(player)) {
            players.add(player);
        }
    }

    public static void postPhysicsTick(Level level) {
        Map<ServerSubLevel, DiagramRecordingTicket> map = queuedDiagramRecordings.get(level.dimension());
        if (map == null) {
            return;
        }
        Iterator<Map.Entry<ServerSubLevel, DiagramRecordingTicket>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<ServerSubLevel, DiagramRecordingTicket> entry = iter.next();
            ServerSubLevel subLevel = entry.getKey();
            DiagramRecordingTicket ticket = entry.getValue();
            if (!ticket.isValid()) {
                iter.remove();
                subLevel.enableIndividualQueuedForcesTracking(false);
                continue;
            }
            DiagramDataPacket dataPacket = DiagramEntity.makeDiagramDataPacket(ticket.subLevel());
            for (ServerPlayer player : ticket.players()) {
                VeilPacketManager.player((ServerPlayer)player).sendPacket(new CustomPacketPayload[]{dataPacket});
            }
            subLevel.enableIndividualQueuedForcesTracking(false);
            iter.remove();
        }
    }

    private static DiagramDataPacket makeDiagramDataPacket(ServerSubLevel serverSubLevel) {
        MassData massTracker = serverSubLevel.getMassTracker();
        Object2ObjectOpenHashMap sentForces = new Object2ObjectOpenHashMap();
        Object2ObjectMap queuedForceGroups = serverSubLevel.getQueuedForceGroups();
        ServerLevel level = serverSubLevel.getLevel();
        SubLevelPhysicsSystem physicsSystem = SubLevelPhysicsSystem.get((Level)level);
        double timeStep = 0.05 / (double)physicsSystem.getConfig().substepsPerTick;
        if (queuedForceGroups != null) {
            for (Map.Entry entry : queuedForceGroups.entrySet()) {
                ForceGroup key = (ForceGroup)entry.getKey();
                QueuedForceGroup value = (QueuedForceGroup)entry.getValue();
                ObjectArrayList pointForces = new ObjectArrayList();
                for (QueuedForceGroup.PointForce pointForce : value.getRecordedPointForces()) {
                    Vector3d force = new Vector3d(pointForce.force()).div(timeStep);
                    pointForces.add(new QueuedForceGroup.PointForce(pointForce.point(), (Vector3dc)force));
                }
                if (pointForces.isEmpty()) continue;
                sentForces.put((Object)key, (Object)pointForces);
            }
        }
        Vector3dc centerOfMass = serverSubLevel.getMassTracker().getCenterOfMass();
        Pose3d pose = serverSubLevel.logicalPose();
        Vector3d localGravity = pose.transformNormalInverse(DimensionPhysicsData.getGravity((Level)level)).mul(serverSubLevel.getMassTracker().getMass());
        sentForces.put((Object)((ForceGroup)ForceGroups.GRAVITY.get()), List.of(new QueuedForceGroup.PointForce((Vector3dc)new Vector3d(centerOfMass), (Vector3dc)localGravity)));
        return new DiagramDataPacket((Map<ForceGroup, List<QueuedForceGroup.PointForce>>)sentForces, massTracker.getMass());
    }

    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putByte("Facing", (byte)this.direction.get3DDataValue());
        tag.putByte("Orientation", (byte)this.verticalOrientation.get3DDataValue());
        tag.putInt("Size", this.size);
        if (this.config != null) {
            tag.put("Config", (Tag)DiagramConfig.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.config).getOrThrow());
        }
        super.addAdditionalSaveData(tag);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Facing", 99)) {
            this.direction = Direction.from3DDataValue((int)tag.getByte("Facing"));
            this.verticalOrientation = Direction.from3DDataValue((int)tag.getByte("Orientation"));
            this.size = tag.getInt("Size");
        } else {
            this.direction = Direction.SOUTH;
            this.verticalOrientation = Direction.DOWN;
            this.size = 1;
        }
        if (tag.contains("Config", 10)) {
            CompoundTag configTag = tag.getCompound("Config");
            this.config = (DiagramConfig)DiagramConfig.CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)configTag).getOrThrow();
        } else {
            this.config = DiagramConfig.makeDefault(this);
        }
        super.readAdditionalSaveData(tag);
        this.updateFacingWithBoundingBox(this.direction, this.verticalOrientation);
    }

    protected void updateFacingWithBoundingBox(Direction facing, Direction verticalOrientation) {
        Objects.requireNonNull(facing);
        this.direction = facing;
        this.verticalOrientation = verticalOrientation;
        if (facing.getAxis().isHorizontal()) {
            this.setXRot(0.0f);
            this.setYRot(this.direction.get2DDataValue() * 90);
        } else {
            this.setXRot(-90 * facing.getAxisDirection().getStep());
            this.setYRot(verticalOrientation.getAxis().isHorizontal() ? 180.0f + verticalOrientation.toYRot() : 0.0f);
        }
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
    }

    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).withEyeHeight(0.0f);
    }

    protected AABB calculateBoundingBox(BlockPos blockPos, Direction direction) {
        Vec3 pos = Vec3.atLowerCornerOf((Vec3i)this.getPos()).add(0.5, 0.5, 0.5).subtract(Vec3.atLowerCornerOf((Vec3i)direction.getNormal()).scale(0.46875));
        double d1 = pos.x;
        double d2 = pos.y;
        double d3 = pos.z;
        this.setPosRaw(d1, d2, d3);
        Direction.Axis axis = direction.getAxis();
        if (this.size == 2) {
            pos = pos.add(Vec3.atLowerCornerOf((Vec3i)(axis.isHorizontal() ? direction.getCounterClockWise().getNormal() : this.verticalOrientation.getClockWise().getNormal())).scale(0.5)).add(Vec3.atLowerCornerOf((Vec3i)(axis.isHorizontal() ? Direction.UP.getNormal() : (direction == Direction.UP ? this.verticalOrientation.getNormal() : this.verticalOrientation.getOpposite().getNormal()))).scale(0.5));
        }
        d1 = pos.x;
        d2 = pos.y;
        d3 = pos.z;
        double d4 = this.getWidth();
        double d5 = this.getHeight();
        double d6 = this.getWidth();
        Direction.Axis direction$axis = this.direction.getAxis();
        switch (direction$axis) {
            case X: {
                d4 = 1.0;
                break;
            }
            case Y: {
                d5 = 1.0;
                break;
            }
            case Z: {
                d6 = 1.0;
            }
        }
        return new AABB(d1 - (d4 /= 32.0), d2 - (d5 /= 32.0), d3 - (d6 /= 32.0), d1 + d4, d2 + d5, d3 + d6);
    }

    public void recalculateBoundingBox() {
        if (this.direction != null && this.verticalOrientation != null) {
            this.setBoundingBox(this.calculateBoundingBox(this.pos, this.direction));
        }
    }

    public Vec3 getLightProbePosition(float partialTicks) {
        return this.position();
    }

    public boolean survives() {
        if (!this.level().noCollision((Entity)this)) {
            return false;
        }
        int i = Math.max(1, this.getWidth() / 16);
        int j = Math.max(1, this.getHeight() / 16);
        BlockPos blockpos = this.pos.relative(this.direction.getOpposite());
        Direction upDirection = this.direction.getAxis().isHorizontal() ? Direction.UP : (this.direction == Direction.UP ? this.verticalOrientation : this.verticalOrientation.getOpposite());
        Direction newDirection = this.direction.getAxis().isVertical() ? this.verticalOrientation.getClockWise() : this.direction.getCounterClockWise();
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        for (int k = 0; k < i; ++k) {
            for (int l = 0; l < j; ++l) {
                int i1 = (i - 1) / -2;
                int j1 = (j - 1) / -2;
                blockpos$mutable.set((Vec3i)blockpos).move(newDirection, k + i1).move(upDirection, l + j1);
                BlockState blockstate = this.level().getBlockState((BlockPos)blockpos$mutable);
                if (Block.canSupportCenter((LevelReader)this.level(), (BlockPos)blockpos$mutable, (Direction)this.direction) || blockstate.isSolid() || DiodeBlock.isDiode((BlockState)blockstate)) continue;
                return false;
            }
        }
        return this.level().getEntities((Entity)this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
    }

    public int getWidth() {
        return 16 * this.size;
    }

    public int getHeight() {
        return 16 * this.size;
    }

    public void dropItem(@Nullable Entity p_110128_1_) {
        if (!this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            return;
        }
        this.playSound(SoundEvents.PAINTING_BREAK, 1.0f, 1.0f);
        if (p_110128_1_ instanceof Player) {
            Player playerentity = (Player)p_110128_1_;
            if (playerentity.getAbilities().instabuild) {
                return;
            }
        }
        this.spawnAtLocation(SimItems.CONTRAPTION_DIAGRAM.asStack());
    }

    public ItemStack getPickResult() {
        return SimItems.CONTRAPTION_DIAGRAM.asStack();
    }

    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0f, 1.0f);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public void moveTo(double x, double y, double z, float p_70012_7_, float p_70012_8_) {
        this.setPos(x, y, z);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void lerpTo(double pX, double pY, double pZ, float pYRot, float pXRot, int pSteps) {
        BlockPos blockpos = this.pos.offset((Vec3i)BlockPos.containing((double)(pX - this.getX()), (double)(pY - this.getY()), (double)(pZ - this.getZ())));
        this.setPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }

    public void setPos(double pX, double pY, double pZ) {
        this.setPosRaw(pX, pY, pZ);
        super.setPos(pX, pY, pZ);
    }

    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (this.level().isClientSide) {
            SubLevel subLevel = Sable.HELPER.getContaining((Entity)this);
            if (subLevel == null) {
                player.displayClientMessage((Component)SimLang.translate("contraption_diagram.cannot_use", new Object[0]).color(SimColors.NUH_UH_RED).component(), true);
            }
        } else {
            SubLevel subLevel = Sable.HELPER.getContaining((Entity)this);
            if (subLevel != null) {
                DiagramEntity.queueDiagramDataFor(subLevel, (ServerPlayer)player);
                VeilPacketManager.player((ServerPlayer)((ServerPlayer)player)).sendPacket(new CustomPacketPayload[]{new DiagramOpenPacket(this.getId(), this.config)});
                SimAdvancements.MEASURE_ONCE_BUILD_TWICE.awardTo(player);
            }
        }
        return InteractionResult.SUCCESS;
    }

    public void onPersistentDataUpdated() {
    }

    public boolean canPlayerUse(Player player) {
        AABB box = this.getBoundingBox();
        double dx = 0.0;
        if (box.minX > player.getX()) {
            dx = box.minX - player.getX();
        } else if (player.getX() > box.maxX) {
            dx = player.getX() - box.maxX;
        }
        double dy = 0.0;
        if (box.minY > player.getY()) {
            dy = box.minY - player.getY();
        } else if (player.getY() > box.maxY) {
            dy = player.getY() - box.maxY;
        }
        double dz = 0.0;
        if (box.minZ > player.getZ()) {
            dz = box.minZ - player.getZ();
        } else if (player.getZ() > box.maxZ) {
            dz = player.getZ() - box.maxZ;
        }
        return dx * dx + dy * dy + dz * dz <= 64.0;
    }

    public ItemRequirement getRequiredItems() {
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, (Item)SimItems.CONTRAPTION_DIAGRAM.get());
    }

    public void setConfig(DiagramConfig config) {
        this.config = config;
    }
}
