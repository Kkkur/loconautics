/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.Sable
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.lasers;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.simulated_team.simulated.index.SimTags;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;

public class LaserBehaviour
extends BlockEntityBehaviour {
    private static final BehaviourType<LaserBehaviour> TYPE = new BehaviourType();
    private BooleanSupplier shouldCast;
    private final Supplier<Couple<Vec3>> laserPositions;
    private final Supplier<Float> range;
    private final Supplier<ClipContext> context;
    private BlockHitResult blockHitResult;
    private EntityHitResult entityHitResult;
    private HitResult closestHitResult;
    private ClipContext.Block blockCollide = ClipContext.Block.VISUAL;
    private ClipContext.Fluid fluidCollide = ClipContext.Fluid.NONE;
    private Vec3 virtualHitPos = Vec3.ZERO;

    public LaserBehaviour(SmartBlockEntity be, Supplier<Couple<Vec3>> positions, Supplier<Float> range) {
        super(be);
        this.shouldCast = () -> true;
        this.laserPositions = positions;
        this.range = range;
        this.context = () -> this.getClipContext((Vec3)((Couple)positions.get()).getFirst(), (Vec3)((Couple)positions.get()).getSecond());
    }

    public void tick() {
        if (!this.blockEntity.isVirtual() && this.shouldCast()) {
            this.castRay();
        }
    }

    public void initialize() {
        if (this.shouldCast()) {
            this.castRay();
        }
    }

    private void castRay() {
        Level level = this.blockEntity.getLevel();
        if (level != null) {
            this.blockHitResult = level.clip(this.context.get());
            Couple<Vec3> positions = this.laserPositions.get();
            Vec3 start = (Vec3)positions.getFirst();
            Vec3 end = (Vec3)positions.getSecond();
            AABB checkingBB = new AABB(start, start).inflate(0.5).expandTowards(end.subtract(start));
            this.entityHitResult = ProjectileUtil.getEntityHitResult((Level)level, null, (Vec3)start, (Vec3)end, (AABB)checkingBB, e -> !e.getType().is(SimTags.Misc.LASER_BLACKLIST) && !e.isSpectator(), (float)0.1f);
            this.closestHitResult = this.entityHitResult != null && Sable.HELPER.distanceSquaredWithSubLevels(this.getWorld(), (Position)positions.getFirst(), (Position)this.entityHitResult.getLocation()) < Sable.HELPER.distanceSquaredWithSubLevels(this.getWorld(), (Position)positions.getFirst(), (Position)this.blockHitResult.getLocation()) ? this.entityHitResult : this.blockHitResult;
        } else {
            this.blockHitResult = null;
            this.entityHitResult = null;
            this.closestHitResult = null;
        }
    }

    @NotNull
    private ClipContext getClipContext(Vec3 start, Vec3 end) {
        return new ClipContext(start, end, this.blockCollide, this.fluidCollide, CollisionContext.empty());
    }

    public void setBlockCollide(ClipContext.Block blockCollide) {
        this.blockCollide = blockCollide;
    }

    public void setFluidCollide(ClipContext.Fluid fluidCollide) {
        this.fluidCollide = fluidCollide;
    }

    public void setShouldCast(BooleanSupplier shouldCast) {
        this.shouldCast = shouldCast;
    }

    public Supplier<Couple<Vec3>> getLaserPositions() {
        return this.laserPositions;
    }

    public BlockHitResult getBlockHitResult() {
        return this.blockHitResult;
    }

    public float getRange() {
        return this.range.get().floatValue();
    }

    public BehaviourType<?> getType() {
        return TYPE;
    }

    public void setVirtualHitPos(Vec3 virtualHitPos) {
        this.virtualHitPos = virtualHitPos;
    }

    public Vec3 getVirtualHitPos() {
        return this.virtualHitPos;
    }

    public boolean shouldCast() {
        return this.shouldCast.getAsBoolean();
    }

    public EntityHitResult getEntityHitResult() {
        return this.entityHitResult;
    }

    public HitResult getClosestHitResult() {
        return this.closestHitResult;
    }
}
