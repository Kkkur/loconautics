/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.block_placement;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelHelper;
import dev.ryanhcode.sable.api.math.LevelReusedVectors;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockPlaceContext.class})
public abstract class BlockPlaceContextMixin
extends UseOnContext {
    @Unique
    private final LevelReusedVectors sable$sink = new LevelReusedVectors();
    @Shadow
    protected boolean replaceClicked;

    public BlockPlaceContextMixin(Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        super(pPlayer, pHand, pHitResult);
    }

    @Shadow
    public abstract BlockPos getClickedPos();

    @Redirect(method={"*"}, at=@At(value="INVOKE", target="Lnet/minecraft/core/Direction;getFacingAxis(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Direction$Axis;)Lnet/minecraft/core/Direction;"))
    private Direction sable$getFacingAxis(Entity player, Direction.Axis axis) {
        SubLevel subLevel = Sable.HELPER.getContaining(this.getLevel(), (Vec3i)this.getClickedPos());
        if (subLevel != null) {
            SubLevelHelper.pushEntityLocal(subLevel, player);
            Direction facingAxis = Direction.getFacingAxis((Entity)player, (Direction.Axis)axis);
            SubLevelHelper.popEntityLocal(subLevel, player);
            return facingAxis;
        }
        return Direction.getFacingAxis((Entity)player, (Direction.Axis)axis);
    }

    @Redirect(method={"*"}, at=@At(value="INVOKE", target="Lnet/minecraft/core/Direction;orderedByNearest(Lnet/minecraft/world/entity/Entity;)[Lnet/minecraft/core/Direction;"))
    private Direction[] sable$orderedByNearest(Entity player) {
        SubLevel subLevel = Sable.HELPER.getContaining(this.getLevel(), (Vec3i)this.getClickedPos());
        if (subLevel != null) {
            SubLevelHelper.pushEntityLocal(subLevel, player);
            Direction[] nearest = Direction.orderedByNearest((Entity)player);
            SubLevelHelper.popEntityLocal(subLevel, player);
            return nearest;
        }
        return Direction.orderedByNearest((Entity)player);
    }

    @Inject(method={"canPlace"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$canPlace(CallbackInfoReturnable<Boolean> cir) {
        BlockPos clicked = this.getClickedPos();
        SubLevel subLevel = Sable.HELPER.getContaining(this.getLevel(), (Vec3i)this.getClickedPos());
        BoundingBox3d placedBoxBoundingBox = new BoundingBox3d(clicked);
        Quaterniond placedBoxOrientation = new Quaterniond();
        Vector3d placedBoxPosition = new Vector3d((double)clicked.getX() + 0.5, (double)clicked.getY() + 0.5, (double)clicked.getZ() + 0.5);
        if (subLevel != null) {
            subLevel.logicalPose().transformPosition(placedBoxPosition);
            placedBoxOrientation.set((Quaterniondc)subLevel.logicalPose().orientation());
            placedBoxBoundingBox.transform((Pose3dc)subLevel.logicalPose(), placedBoxBoundingBox);
        }
        Iterable<SubLevel> subLevels = Sable.HELPER.getAllIntersecting(this.getLevel(), (BoundingBox3dc)placedBoxBoundingBox);
        for (SubLevel otherSubLevel : subLevels) {
            boolean cancelled;
            if (otherSubLevel == subLevel || !(cancelled = this.sable$intersectBlocks(cir, otherSubLevel, (BoundingBox3dc)placedBoxBoundingBox, this.sable$sink, placedBoxPosition, placedBoxOrientation))) continue;
            return;
        }
        this.sable$intersectBlocks(cir, null, (BoundingBox3dc)placedBoxBoundingBox, this.sable$sink, placedBoxPosition, placedBoxOrientation);
    }

    @Unique
    private boolean sable$intersectBlocks(CallbackInfoReturnable<Boolean> cir, @Nullable SubLevel otherSubLevel, BoundingBox3dc placedBoxBoundingBox, LevelReusedVectors sink, Vector3d placedBoxPosition, Quaterniond placedBoxOrientation) {
        BoundingBox3d localBase = placedBoxBoundingBox.expand(0.36602540380000004, new BoundingBox3d());
        if (otherSubLevel != null) {
            localBase.transformInverse((Pose3dc)otherSubLevel.logicalPose(), localBase);
        }
        Iterable stream = BlockPos.betweenClosed((int)Mth.floor((double)localBase.minX()), (int)Mth.floor((double)localBase.minY()), (int)Mth.floor((double)localBase.minZ()), (int)Mth.floor((double)localBase.maxX()), (int)Mth.floor((double)localBase.maxY()), (int)Mth.floor((double)localBase.maxZ()));
        for (BlockPos position : stream) {
            boolean replaced = this.replaceClicked || this.getLevel().getBlockState(position).canBeReplaced((BlockPlaceContext)this);
            Vector3d inWorldBoxPosition = new Vector3d((double)position.getX() + 0.5, (double)position.getY() + 0.5, (double)position.getZ() + 0.5);
            Quaterniond inWorldBoxOrientation = new Quaterniond();
            if (otherSubLevel != null) {
                inWorldBoxPosition = otherSubLevel.logicalPose().transformPosition(inWorldBoxPosition);
                inWorldBoxOrientation.set((Quaterniondc)otherSubLevel.logicalPose().orientation());
            }
            OrientedBoundingBox3d inWorldBox = new OrientedBoundingBox3d((Vector3dc)inWorldBoxPosition, (Vector3dc)new Vector3d(1.0, 1.0, 1.0), (Quaterniondc)inWorldBoxOrientation, sink);
            OrientedBoundingBox3d justPlacedBox = new OrientedBoundingBox3d((Vector3dc)placedBoxPosition, (Vector3dc)new Vector3d(1.0, 1.0, 1.0), (Quaterniondc)placedBoxOrientation, sink);
            if (replaced || !(OrientedBoundingBox3d.sat(inWorldBox, justPlacedBox).lengthSquared() > 0.05)) continue;
            cir.setReturnValue((Object)false);
            return true;
        }
        return false;
    }
}
