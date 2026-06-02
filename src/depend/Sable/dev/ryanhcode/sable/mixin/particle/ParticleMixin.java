/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Constant
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyConstant
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.particle;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.api.particle.ParticleSubLevelKickable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.ClipContextExtension;
import dev.ryanhcode.sable.mixinterface.particle.ParticleExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.ClientLevelPlot;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Particle.class})
public abstract class ParticleMixin
implements ParticleExtension {
    @Unique
    private static final double LIGHT_QUERY_AREA = 8.0;
    @Unique
    private static final BoundingBox3d TEMP_BOX = new BoundingBox3d();
    @Unique
    private final Vector3d sable$inheritedVelocity = new Vector3d();
    @Shadow
    public double x;
    @Shadow
    public double y;
    @Shadow
    public double z;
    @Shadow
    protected double xd;
    @Shadow
    protected double zd;
    @Shadow
    protected double yd;
    @Shadow
    @Final
    protected ClientLevel level;
    @Shadow
    protected boolean onGround;
    @Unique
    private boolean sable$checkedInitialKick = false;
    @Unique
    @Nullable
    private ClientSubLevel sable$trackingSubLevel = null;
    @Unique
    @Nullable
    private Vector3d sable$localTrackingAnchor = null;
    @Unique
    private List<ClientSubLevel> sable$nearbySubLevels;
    @Shadow
    private boolean stoppedByCollision;

    @Shadow
    public abstract void setPos(double var1, double var3, double var5);

    @Shadow
    public abstract void move(double var1, double var3, double var5);

    @Shadow
    protected abstract void setLocationFromBoundingbox();

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract void setBoundingBox(AABB var1);

    @Shadow
    public abstract void tick();

    @ModifyConstant(method={"Lnet/minecraft/client/particle/Particle;<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)V"}, constant={@Constant(ordinal=13)})
    private double sable$removeUpwardsVelocity(double originalBlockDamageDistanceConstant) {
        return 0.0;
    }

    @Inject(method={"Lnet/minecraft/client/particle/Particle;<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)V"}, at={@At(value="TAIL")})
    private void sable$addUpwardsVelocity(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, CallbackInfo ci) {
        Vec3 particlePosition = new Vec3(this.x, this.y, this.z);
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)particlePosition);
        if (subLevel != null) {
            Vec3 stupidVanillaVelocity = subLevel.logicalPose().transformNormalInverse(new Vec3(0.0, 0.1, 0.0));
            this.xd += stupidVanillaVelocity.x;
            this.yd += stupidVanillaVelocity.y;
            this.zd += stupidVanillaVelocity.z;
            this.sable$setTrackingSubLevel(subLevel, particlePosition);
        }
    }

    @Override
    public void sable$initialKickOut() {
        Vec3 particlePosition = new Vec3(this.x, this.y, this.z);
        if (!this.sable$checkedInitialKick) {
            ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)particlePosition);
            if (subLevel != null) {
                Pose3d pose = subLevel.logicalPose();
                Vec3 globalPosition = pose.transformPosition(particlePosition);
                Vec3 globalVelocity = pose.transformNormal(new Vec3(this.xd, this.yd, this.zd));
                this.x = globalPosition.x;
                this.y = globalPosition.y;
                this.z = globalPosition.z;
                this.xd = globalVelocity.x;
                this.yd = globalVelocity.y;
                this.zd = globalVelocity.z;
                this.setPos(this.x, this.y, this.z);
                this.sable$setTrackingSubLevel(subLevel, particlePosition);
            }
            this.sable$checkedInitialKick = true;
        }
    }

    private void sable$kickFromTracking() {
        Vector3d currentLocalPos = this.sable$trackingSubLevel.logicalPose().transformPositionInverse(new Vector3d(this.x, this.y, this.z));
        Sable.HELPER.getVelocity((Level)this.level, (Vector3dc)currentLocalPos, this.sable$inheritedVelocity);
        this.sable$inheritedVelocity.mul(0.05);
        this.sable$localTrackingAnchor = null;
        this.sable$trackingSubLevel = null;
    }

    @Override
    public void sable$moveWithInheritedVelocity() {
    }

    @Override
    public void sable$setTrackingSubLevel(ClientSubLevel subLevel, Vec3 particlePosition) {
        this.sable$trackingSubLevel = subLevel;
        this.sable$localTrackingAnchor = new Vector3d();
        this.sable$localTrackingAnchor.set(particlePosition.x, particlePosition.y, particlePosition.z);
        this.sable$inheritedVelocity.zero();
    }

    @Override
    public SubLevel sable$getTrackingSubLevel() {
        return this.sable$trackingSubLevel;
    }

    @WrapMethod(method={"move"})
    private void sable$moveWithSubLevels(double motionX, double motionY, double motionZ, Operation<Void> original) {
        ParticleSubLevelKickable kickable;
        ParticleMixin particleMixin;
        ParticleSubLevelKickable kickable2;
        boolean ignoreIntersecting;
        AABB bounds = this.getBoundingBox();
        BoundingBox3d globalBounds = new BoundingBox3d(bounds).expand(0.5);
        ObjectOpenHashSet intersecting = new ObjectOpenHashSet();
        ParticleMixin particleMixin2 = this;
        boolean bl = ignoreIntersecting = particleMixin2 instanceof ParticleSubLevelKickable && !(kickable2 = (ParticleSubLevelKickable)((Object)particleMixin2)).sable$shouldCareAboutIntersectingSubLevels();
        if (!ignoreIntersecting) {
            Iterable<SubLevel> subLevels = Sable.HELPER.getAllIntersecting((Level)this.level, (BoundingBox3dc)globalBounds);
            for (SubLevel subLevel : subLevels) {
                intersecting.add((Object)subLevel);
            }
        }
        if (this.sable$trackingSubLevel != null) {
            intersecting.add((Object)this.sable$trackingSubLevel);
        }
        if (this.sable$trackingSubLevel != null && this.sable$trackingSubLevel.isRemoved()) {
            this.sable$trackingSubLevel = null;
            this.sable$localTrackingAnchor = null;
        }
        Vector3d movementFromPushing = new Vector3d();
        Vector3d localPosition = new Vector3d();
        Vector3d globalBoundsCenter = new Vector3d();
        Vector3d localRayStart = new Vector3d();
        Vector3d localRayEnd = new Vector3d();
        Vector3d movement = new Vector3d(motionX, motionY, motionZ);
        movement.add((Vector3dc)this.sable$inheritedVelocity);
        boolean isGrounded = false;
        for (SubLevel subLevel : intersecting) {
            double verticalDot;
            ParticleSubLevelKickable kickable3;
            ParticleMixin particleMixin3;
            boolean shouldCollide;
            Pose3d pose = subLevel.logicalPose();
            Pose3dc last = subLevel.lastPose();
            movementFromPushing.zero();
            if (this.sable$trackingSubLevel == subLevel) {
                JOMLConversion.getAABBCenter((AABB)bounds, (Vector3d)globalBoundsCenter);
                last.transformPositionInverse((Vector3dc)globalBoundsCenter, localPosition);
                Vector3d newGlobalPosition = pose.transformPosition(localPosition);
                movementFromPushing.add((Vector3dc)newGlobalPosition).sub((Vector3dc)globalBoundsCenter);
            } else {
                JOMLConversion.getAABBCenter((AABB)bounds, (Vector3d)globalBoundsCenter);
                last.transformPositionInverse((Vector3dc)globalBoundsCenter, localRayStart);
                pose.transformPositionInverse((Vector3dc)globalBoundsCenter, localRayEnd);
                ClipContext clipContext = new ClipContext(JOMLConversion.toMojang((Vector3dc)localRayStart), JOMLConversion.toMojang((Vector3dc)localRayEnd), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
                ((ClipContextExtension)clipContext).sable$setDoNotProject(true);
                BlockHitResult result = this.level.clip(clipContext);
                if (result.getType() == HitResult.Type.BLOCK) {
                    pose.transformPosition(JOMLConversion.toJOML((Position)result.getLocation(), (Vector3d)movementFromPushing)).sub((Vector3dc)globalBoundsCenter);
                    if (this.sable$trackingSubLevel == null) {
                        this.sable$setTrackingSubLevel((ClientSubLevel)subLevel, result.getLocation());
                    }
                }
            }
            boolean bl2 = shouldCollide = !(this.sable$trackingSubLevel == subLevel && (particleMixin3 = this) instanceof ParticleSubLevelKickable && !(kickable3 = (ParticleSubLevelKickable)((Object)particleMixin3)).sable$shouldCollideWithTrackingSubLevel());
            if (shouldCollide) {
                Vector3d pushedPosition = JOMLConversion.getAABBCenter((AABB)bounds, (Vector3d)globalBoundsCenter).add((Vector3dc)movementFromPushing);
                pose.transformPositionInverse((Vector3dc)pushedPosition, localRayStart);
                pose.transformPositionInverse(pushedPosition.add((Vector3dc)movement, localRayEnd));
                ClipContext clipContext = new ClipContext(JOMLConversion.toMojang((Vector3dc)localRayStart), JOMLConversion.toMojang((Vector3dc)localRayEnd), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
                ((ClipContextExtension)clipContext).sable$setDoNotProject(true);
                BlockHitResult result = this.level.clip(clipContext);
                if (result != null && result.getType() == HitResult.Type.BLOCK) {
                    Vec3 diff = pose.transformPosition(result.getLocation()).subtract(pushedPosition.x(), pushedPosition.y(), pushedPosition.z());
                    movement.set(diff.x, diff.y, diff.z);
                }
            }
            movement.add((Vector3dc)movementFromPushing);
            if (!shouldCollide) continue;
            Vec3 collisionBoxCenter = pose.transformPositionInverse(bounds.getCenter().add(movement.x, movement.y, movement.z));
            double radius = Math.max(bounds.getXsize(), Math.max(bounds.getYsize(), bounds.getZsize())) / 2.0;
            BoundingBox3d collisionBounds = new BoundingBox3d();
            collisionBounds.set(collisionBoxCenter.x - radius, collisionBoxCenter.y - radius, collisionBoxCenter.z - radius, collisionBoxCenter.x + radius, collisionBoxCenter.y + radius, collisionBoxCenter.z + radius);
            Vector3d mtv = this.resolveAABBCollision(collisionBounds);
            if (!(mtv.lengthSquared() > 0.0)) continue;
            subLevel.logicalPose().transformNormal(mtv);
            Vector3d nmtv = mtv.normalize(new Vector3d());
            Vector3dc upDirection = OrientedBoundingBox3d.UP;
            ParticleMixin particleMixin4 = this;
            if (particleMixin4 instanceof ParticleSubLevelKickable) {
                ParticleSubLevelKickable kickable4 = (ParticleSubLevelKickable)((Object)particleMixin4);
                upDirection = kickable4.sable$getUpDirection();
            }
            if ((verticalDot = nmtv.dot(upDirection)) > 0.6) {
                isGrounded = true;
            }
            double dot = nmtv.dot(this.xd, this.yd, this.zd);
            this.xd -= dot * nmtv.x;
            this.yd -= dot * nmtv.y;
            this.zd -= dot * nmtv.z;
            if (verticalDot > 0.6 || verticalDot < 0.6) {
                this.xd = upDirection.x() * this.xd;
                this.yd = upDirection.y() * this.yd;
                this.zd = upDirection.z() * this.zd;
            }
            movement.add((Vector3dc)mtv);
            if (this.sable$trackingSubLevel != null) continue;
            this.sable$setTrackingSubLevel((ClientSubLevel)subLevel, collisionBoxCenter);
        }
        original.call(new Object[]{movement.x, movement.y, movement.z});
        this.onGround |= isGrounded;
        if (this.sable$trackingSubLevel != null && (!((particleMixin = this) instanceof ParticleSubLevelKickable) || (kickable = (ParticleSubLevelKickable)((Object)particleMixin)).sable$shouldKickFromTracking())) {
            Vector3d vector3d = new Vector3d();
            if (this.sable$trackingSubLevel.logicalPose().transformPosition((Vector3dc)this.sable$localTrackingAnchor, vector3d).distanceSquared(this.x, this.y, this.z) > 0.25) {
                this.sable$kickFromTracking();
            }
        }
    }

    private Vector3d resolveAABBCollision(BoundingBox3d box) {
        Vector3d totalMTV = new Vector3d();
        Vector3d mtv = new Vector3d();
        double[] maxMTVLengthSq = new double[]{0.0};
        int minX = (int)Math.floor(box.minX());
        int minY = (int)Math.floor(box.minY());
        int minZ = (int)Math.floor(box.minZ());
        int maxX = (int)Math.floor(box.maxX());
        int maxY = (int)Math.floor(box.maxY());
        int maxZ = (int)Math.floor(box.maxZ());
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    VoxelShape shape;
                    BlockPos.MutableBlockPos blockPos = mpos.set(x, y, z);
                    BlockState state = this.level.getBlockState((BlockPos)blockPos);
                    if (state.isAir() || (shape = state.getCollisionShape((BlockGetter)this.level, (BlockPos)blockPos)).isEmpty()) continue;
                    int finalX = x;
                    int finalY = y;
                    int finalZ = z;
                    if (state.isCollisionShapeFullBlock((BlockGetter)this.level, (BlockPos)blockPos)) {
                        TEMP_BOX.setUnchecked(0.0 + (double)finalX, 0.0 + (double)finalY, 0.0 + (double)finalZ, 1.0 + (double)finalX, 1.0 + (double)finalY, 1.0 + (double)finalZ);
                        mtv.zero();
                        this.resolveAABBAABBCollision(box, (BoundingBox3dc)TEMP_BOX, mtv);
                        double lenSq = mtv.lengthSquared();
                        if (!(lenSq > maxMTVLengthSq[0])) continue;
                        maxMTVLengthSq[0] = lenSq;
                        totalMTV.set((Vector3dc)mtv);
                        continue;
                    }
                    shape.forAllBoxes((minXb, minYb, minZb, maxXb, maxYb, maxZb) -> {
                        TEMP_BOX.setUnchecked(minXb + (double)finalX, minYb + (double)finalY, minZb + (double)finalZ, maxXb + (double)finalX, maxYb + (double)finalY, maxZb + (double)finalZ);
                        mtv.zero();
                        this.resolveAABBAABBCollision(box, (BoundingBox3dc)TEMP_BOX, mtv);
                        double lenSq = mtv.lengthSquared();
                        if (lenSq > maxMTVLengthSq[0]) {
                            maxMTVLengthSq[0] = lenSq;
                            totalMTV.set((Vector3dc)mtv);
                        }
                    });
                }
            }
        }
        return totalMTV;
    }

    private void resolveAABBAABBCollision(BoundingBox3d a, BoundingBox3dc b, Vector3d mtv) {
        double dx1 = b.maxX() - a.minX();
        double dx2 = a.maxX() - b.minX();
        if (dx1 <= 0.0 || dx2 <= 0.0) {
            return;
        }
        double dy1 = b.maxY() - a.minY();
        double dy2 = a.maxY() - b.minY();
        if (dy1 <= 0.0 || dy2 <= 0.0) {
            return;
        }
        double dz1 = b.maxZ() - a.minZ();
        double dz2 = a.maxZ() - b.minZ();
        if (dz1 <= 0.0 || dz2 <= 0.0) {
            return;
        }
        double minOverlap = dx1;
        mtv.set(dx1, 0.0, 0.0);
        if (dx2 < minOverlap) {
            minOverlap = dx2;
            mtv.set(-dx2, 0.0, 0.0);
        }
        if (dy1 < minOverlap) {
            minOverlap = dy1;
            mtv.set(0.0, dy1, 0.0);
        }
        if (dy2 < minOverlap) {
            minOverlap = dy2;
            mtv.set(0.0, -dy2, 0.0);
        }
        if (dz1 < minOverlap) {
            minOverlap = dz1;
            mtv.set(0.0, 0.0, dz1);
        }
        if (dz2 < minOverlap) {
            minOverlap = dz2;
            mtv.set(0.0, 0.0, -dz2);
        }
    }

    @Inject(method={"getLightColor"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$checkSubLevelLightColor(float f, CallbackInfoReturnable<Integer> cir) {
        int skyLight;
        int blockLight;
        BlockPos pos = BlockPos.containing((double)this.x, (double)this.y, (double)this.z);
        boolean hasChunk = this.level.hasChunkAt(pos);
        if (!hasChunk) {
            return;
        }
        BlockState state = this.level.getBlockState(pos);
        if (state.emissiveRendering((BlockGetter)this.level, pos)) {
            cir.setReturnValue((Object)0xF000F0);
            return;
        }
        if (this.sable$trackingSubLevel != null) {
            blockLight = this.level.getBrightness(LightLayer.BLOCK, pos);
            skyLight = this.level.getBrightness(LightLayer.SKY, pos);
            particlePos = new Vector3d();
            BlockPos.MutableBlockPos localBlockPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos heightmapPos = new BlockPos.MutableBlockPos();
            Pose3d pose = this.sable$trackingSubLevel.logicalPose();
            pose.transformPositionInverse(particlePos.set(this.x, this.y, this.z));
            localBlockPos.set(particlePos.x, particlePos.y, particlePos.z);
            blockLight = Math.max(blockLight, this.sable$trackingSubLevel.getLevel().getBrightness(LightLayer.BLOCK, (BlockPos)localBlockPos));
            heightmapPos.setWithOffset((Vec3i)localBlockPos, 0, 1, 0);
            ClientLevelPlot plot = this.sable$trackingSubLevel.getPlot();
            boolean isAboveGround = false;
            while (heightmapPos.getY() >= plot.getBoundingBox().minY()) {
                if (!this.level.getBlockState((BlockPos)heightmapPos).isAir()) {
                    isAboveGround = true;
                    break;
                }
                heightmapPos.move(0, -1, 0);
            }
            if (isAboveGround) {
                skyLight = Math.min(skyLight, this.sable$trackingSubLevel.scaleSkyLight(this.level.getBrightness(LightLayer.SKY, (BlockPos)localBlockPos)));
            }
        } else {
            if (this.sable$nearbySubLevels == null) {
                this.sable$nearbySubLevels = new ObjectArrayList(6);
                Iterable<SubLevel> all = Sable.HELPER.getAllIntersecting((Level)this.level, (BoundingBox3dc)new BoundingBox3d(pos).expand(8.0));
                for (SubLevel subLevel : all) {
                    this.sable$nearbySubLevels.add((ClientSubLevel)subLevel);
                }
            }
            if (this.sable$nearbySubLevels.isEmpty()) {
                return;
            }
            blockLight = this.level.getBrightness(LightLayer.BLOCK, pos);
            skyLight = this.level.getBrightness(LightLayer.SKY, pos);
            particlePos = new Vector3d();
            BlockPos.MutableBlockPos localBlockPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos heightmapPos = new BlockPos.MutableBlockPos();
            BoundingBox3d box = new BoundingBox3d(pos).expand(0.5);
            for (ClientSubLevel subLevel : this.sable$nearbySubLevels) {
                if (!subLevel.boundingBox().intersects((BoundingBox3dc)box)) continue;
                Pose3d pose = subLevel.logicalPose();
                pose.transformPositionInverse(particlePos.set(this.x, this.y, this.z));
                localBlockPos.set(particlePos.x, particlePos.y, particlePos.z);
                blockLight = Math.max(blockLight, subLevel.getLevel().getBrightness(LightLayer.BLOCK, (BlockPos)localBlockPos));
                heightmapPos.setWithOffset((Vec3i)localBlockPos, 0, 1, 0);
                ClientLevelPlot plot = subLevel.getPlot();
                boolean isAboveGround = false;
                while (heightmapPos.getY() >= plot.getBoundingBox().minY()) {
                    if (!this.level.getBlockState((BlockPos)heightmapPos).isAir()) {
                        isAboveGround = true;
                        break;
                    }
                    heightmapPos.move(0, -1, 0);
                }
                if (!isAboveGround) continue;
                skyLight = Math.min(skyLight, subLevel.scaleSkyLight(this.level.getBrightness(LightLayer.SKY, (BlockPos)localBlockPos)));
            }
        }
        int k = state.getLightEmission();
        if (blockLight < k) {
            blockLight = k;
        }
        cir.setReturnValue((Object)LightTexture.pack((int)blockLight, (int)skyLight));
    }
}
