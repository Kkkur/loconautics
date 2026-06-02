/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalFloatRef
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.projectile.windcharge.WindCharge
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Explosion
 *  net.minecraft.world.level.ExplosionDamageCalculator
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package dev.ryanhcode.sable.mixin.explosion;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={Explosion.class})
public class ExplosionMixin {
    @Shadow
    @Final
    private Level level;
    @Shadow
    @Final
    private double x;
    @Shadow
    @Final
    private double y;
    @Shadow
    @Final
    private double z;
    @Shadow
    @Final
    private ExplosionDamageCalculator damageCalculator;
    @Shadow
    @Final
    @Nullable
    private Entity source;

    @Inject(method={"explode"}, at={@At(value="HEAD")})
    private void sable$preExplode(CallbackInfo ci, @Share(value="explodedSet") LocalRef<Set<BlockPos>> explodedSet) {
        explodedSet.set((Object)new ObjectOpenHashSet());
    }

    @Inject(method={"explode"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/ExplosionDamageCalculator;getBlockExplosionResistance(Lnet/minecraft/world/level/Explosion;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Ljava/util/Optional;")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void sable$redirectBlockExplosionResistance(CallbackInfo ci, Set<BlockPos> set, int i, int j, int k, int l, double d0, double d1, double d2, double d3, float f, double d4, double d6, double d8, float f1, BlockPos blockpos, BlockState blockstate, FluidState fluidstate, @Local(ordinal=0) LocalFloatRef fReference, @Share(value="explodedSet") LocalRef<Set<BlockPos>> explodedSet) {
        Explosion self = (Explosion)this;
        if (!blockstate.isAir()) {
            return;
        }
        BoundingBox3d globalBounds = new BoundingBox3d(blockpos);
        Iterable<SubLevel> subLevels = Sable.HELPER.getAllIntersecting(this.level, (BoundingBox3dc)globalBounds);
        SubLevelContainer container = SubLevelContainer.getContainer(this.level);
        for (SubLevel subLevel : subLevels) {
            Pose3d pose = subLevel.logicalPose();
            BoundingBox3d localBounds = new BoundingBox3d();
            globalBounds.transformInverse((Pose3dc)pose, localBounds);
            BoundingBox3i blockBounds = new BoundingBox3i(Mth.floor((double)localBounds.minX()), Mth.floor((double)localBounds.minY()), Mth.floor((double)localBounds.minZ()), Mth.floor((double)localBounds.maxX()), Mth.floor((double)localBounds.maxY()), Mth.floor((double)localBounds.maxZ()));
            Vec3 localExplosionPosition = pose.transformPositionInverse(new Vec3(this.x, this.y, this.z));
            for (int x = blockBounds.minX(); x <= blockBounds.maxX(); ++x) {
                for (int z = blockBounds.minZ(); z <= blockBounds.maxZ(); ++z) {
                    for (int y = blockBounds.minY(); y <= blockBounds.maxY(); ++y) {
                        boolean wind;
                        blockpos = new BlockPos(x, y, z);
                        blockstate = this.level.getBlockState(blockpos);
                        fluidstate = this.level.getFluidState(blockpos);
                        boolean canExplodeBefore = (double)f > 0.0;
                        Optional optional = this.damageCalculator.getBlockExplosionResistance(self, (BlockGetter)this.level, blockpos, blockstate, fluidstate);
                        if (optional.isPresent()) {
                            f -= (((Float)optional.get()).floatValue() + 0.3f) * 0.3f;
                        }
                        if (f > 0.0f && this.damageCalculator.shouldBlockExplode(self, (BlockGetter)this.level, blockpos, blockstate, f)) {
                            set.add(blockpos);
                        }
                        boolean bl = wind = this.source instanceof WindCharge && !blockstate.isAir();
                        if (!canExplodeBefore || !(f < 0.0f) && !wind || !((Set)explodedSet.get()).add(blockpos)) continue;
                        ((Set)explodedSet.get()).add(blockpos);
                        if (!(subLevel instanceof ServerSubLevel)) continue;
                        ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
                        SubLevelPhysicsSystem physicsSystem = ((ServerSubLevelContainer)container).physicsSystem();
                        RigidBodyHandle handle = physicsSystem.getPhysicsHandle(serverSubLevel);
                        Vec3 pos = blockpos.getCenter();
                        Vec3 force = pos.subtract(localExplosionPosition).normalize().scale(5.0);
                        handle.applyImpulseAtPoint(pos, force);
                    }
                }
            }
        }
        fReference.set(f);
    }
}
