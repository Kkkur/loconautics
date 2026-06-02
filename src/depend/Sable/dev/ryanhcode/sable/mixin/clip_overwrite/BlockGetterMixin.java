/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.ryanhcode.sable.mixin.clip_overwrite;

import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.ClipContextExtension;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={BlockGetter.class}, priority=1100)
public interface BlockGetterMixin {
    @Shadow
    public BlockState getBlockState(BlockPos var1);

    @Overwrite
    default public BlockHitResult clip(ClipContext clipContext) {
        BlockHitResult minResult;
        ClipContextExtension extension;
        SubLevel toSubLevel;
        Predicate<SubLevel> predicate;
        SubLevel ignoredSubLevel;
        Level level;
        BlockGetter self;
        block16: {
            block15: {
                ClipContextExtension extension2;
                self = (BlockGetter)this;
                BlockGetterMixin blockGetterMixin = this;
                if (!(blockGetterMixin instanceof Level)) break block15;
                level = (Level)blockGetterMixin;
                if (!(clipContext instanceof ClipContextExtension) || !(extension2 = (ClipContextExtension)clipContext).sable$doNotProject()) break block16;
            }
            return BlockGetterMixin.originalClip(self, clipContext);
        }
        if (clipContext instanceof ClipContextExtension) {
            ClipContextExtension extension3 = (ClipContextExtension)clipContext;
            v0 = extension3.sable$getIgnoredSubLevel();
        } else {
            v0 = ignoredSubLevel = null;
        }
        if (clipContext instanceof ClipContextExtension) {
            ClipContextExtension extension4 = (ClipContextExtension)clipContext;
            predicate = extension4.sable$getSubLevelIgnoring();
        } else {
            predicate = null;
        }
        Predicate<SubLevel> subLevelIgnoring = predicate;
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel fromSubLevel = helper.getContaining(level, (Position)clipContext.getFrom());
        if (fromSubLevel != null) {
            Pose3d pose = fromSubLevel.logicalPose();
            if (level instanceof LevelPoseProviderExtension) {
                LevelPoseProviderExtension extension5 = (LevelPoseProviderExtension)level;
                pose = extension5.sable$getPose(fromSubLevel);
            }
            Vector3d from = pose.transformPosition(JOMLConversion.toJOML((Position)clipContext.getFrom()));
            clipContext = new ClipContext(JOMLConversion.toMojang((Vector3dc)from), clipContext.getTo(), clipContext.block, clipContext.fluid, clipContext.collisionContext);
        }
        if ((toSubLevel = helper.getContaining(level, (Position)clipContext.getTo())) != null) {
            Pose3d pose = toSubLevel.logicalPose();
            if (level instanceof LevelPoseProviderExtension) {
                LevelPoseProviderExtension extension6 = (LevelPoseProviderExtension)level;
                pose = extension6.sable$getPose(toSubLevel);
            }
            Vector3d to = pose.transformPosition(JOMLConversion.toJOML((Position)clipContext.getTo()));
            clipContext = new ClipContext(clipContext.getFrom(), JOMLConversion.toMojang((Vector3dc)to), clipContext.block, clipContext.fluid, clipContext.collisionContext);
        }
        double minDistance = Double.MAX_VALUE;
        if (clipContext instanceof ClipContextExtension && (extension = (ClipContextExtension)clipContext).sable$isIgnoreMainLevel()) {
            Vec3 diff = clipContext.getFrom().subtract(clipContext.getTo());
            minResult = BlockHitResult.miss((Vec3)clipContext.getTo(), (Direction)Direction.getNearest((double)diff.x, (double)diff.y, (double)diff.z), (BlockPos)BlockPos.containing((Position)clipContext.getTo()));
        } else {
            minResult = BlockGetterMixin.originalClip(self, clipContext);
            minDistance = minResult.getLocation().distanceTo(clipContext.getFrom());
        }
        BoundingBox3d bounds = new BoundingBox3d(clipContext.getFrom(), clipContext.getTo());
        Iterable<SubLevel> subLevels = helper.getAllIntersecting(level, (BoundingBox3dc)bounds);
        for (SubLevel subLevel : subLevels) {
            if (subLevel == ignoredSubLevel || subLevelIgnoring != null && subLevelIgnoring.test(subLevel)) continue;
            Pose3d pose = subLevel.logicalPose();
            if (level instanceof LevelPoseProviderExtension) {
                LevelPoseProviderExtension extension7 = (LevelPoseProviderExtension)level;
                pose = extension7.sable$getPose(subLevel);
            }
            Vector3d from = pose.transformPositionInverse(JOMLConversion.toJOML((Position)clipContext.getFrom()));
            Vector3d to = pose.transformPositionInverse(JOMLConversion.toJOML((Position)clipContext.getTo()));
            if (helper.getContaining(level, (Vector3dc)from) != subLevel) continue;
            ClipContext subClipContext = new ClipContext(JOMLConversion.toMojang((Vector3dc)from), JOMLConversion.toMojang((Vector3dc)to), clipContext.block, clipContext.fluid, clipContext.collisionContext);
            BlockHitResult subResult = BlockGetterMixin.originalClip((BlockGetter)subLevel.getLevel(), subClipContext);
            double distance = subResult.getLocation().distanceTo(subClipContext.getFrom());
            if (!(distance < minDistance) && minResult.getType() != HitResult.Type.MISS || subResult.getType() == HitResult.Type.MISS) continue;
            minResult = subResult;
            minDistance = distance;
        }
        return minResult;
    }

    @Unique
    @NotNull
    private static BlockHitResult originalClip(BlockGetter level, ClipContext clipContext) {
        return (BlockHitResult)BlockGetter.traverseBlocks((Vec3)clipContext.getFrom(), (Vec3)clipContext.getTo(), (Object)clipContext, (clipContextx, blockPos) -> {
            BlockState blockState = level.getBlockState(blockPos);
            FluidState fluidState = level.getFluidState(blockPos);
            Vec3 vec3 = clipContextx.getFrom();
            Vec3 vec32 = clipContextx.getTo();
            VoxelShape voxelShape = clipContextx.getBlockShape(blockState, level, blockPos);
            BlockHitResult blockHitResult = level.clipWithInteractionOverride(vec3, vec32, blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = clipContextx.getFluidShape(fluidState, level, blockPos);
            BlockHitResult blockHitResult2 = voxelShape2.clip(vec3, vec32, blockPos);
            double d = blockHitResult == null ? Double.MAX_VALUE : clipContextx.getFrom().distanceToSqr(blockHitResult.getLocation());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : clipContextx.getFrom().distanceToSqr(blockHitResult2.getLocation());
            return d <= e ? blockHitResult : blockHitResult2;
        }, clipContextx -> {
            Vec3 vec3 = clipContextx.getFrom().subtract(clipContextx.getTo());
            return BlockHitResult.miss((Vec3)clipContextx.getTo(), (Direction)Direction.getNearest((double)vec3.x, (double)vec3.y, (double)vec3.z), (BlockPos)BlockPos.containing((Position)clipContextx.getTo()));
        });
    }
}
