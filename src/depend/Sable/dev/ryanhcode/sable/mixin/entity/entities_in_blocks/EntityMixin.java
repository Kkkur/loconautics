/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.ReportedException
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelHeightAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entities_in_blocks;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public abstract class EntityMixin {
    @Shadow
    private Level level;

    @Shadow
    public abstract boolean isAlive();

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    protected abstract void onInsideBlock(BlockState var1);

    @Inject(method={"checkInsideBlocks"}, at={@At(value="TAIL")})
    protected void checkInsideBlocks(CallbackInfo ci) {
        AABB bounds = this.getBoundingBox();
        BoundingBox3d localBounds = new BoundingBox3d(bounds);
        for (SubLevel intersecting : Sable.HELPER.getAllIntersecting(this.level, (BoundingBox3dc)new BoundingBox3d(bounds))) {
            BlockPos maxPos;
            localBounds.set(bounds);
            localBounds.transformInverse((Pose3dc)intersecting.logicalPose(), localBounds);
            BlockPos minPos = BlockPos.containing((double)(localBounds.minX + 1.0E-7), (double)(localBounds.minY + 1.0E-7), (double)(localBounds.minZ + 1.0E-7));
            if (!this.level.hasChunksAt(minPos, maxPos = BlockPos.containing((double)(localBounds.maxX - 1.0E-7), (double)(localBounds.maxY - 1.0E-7), (double)(localBounds.maxZ - 1.0E-7)))) continue;
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            for (int i = minPos.getX(); i <= maxPos.getX(); ++i) {
                for (int j = minPos.getY(); j <= maxPos.getY(); ++j) {
                    for (int k = minPos.getZ(); k <= maxPos.getZ(); ++k) {
                        if (!this.isAlive()) {
                            return;
                        }
                        mutableBlockPos.set(i, j, k);
                        BlockState blockState = this.level.getBlockState((BlockPos)mutableBlockPos);
                        try {
                            blockState.entityInside(this.level, (BlockPos)mutableBlockPos, (Entity)this);
                            this.onInsideBlock(blockState);
                            continue;
                        }
                        catch (Throwable var12) {
                            CrashReport crashReport = CrashReport.forThrowable((Throwable)var12, (String)"Colliding entity with block");
                            CrashReportCategory crashReportCategory = crashReport.addCategory("Block being collided with");
                            CrashReportCategory.populateBlockDetails((CrashReportCategory)crashReportCategory, (LevelHeightAccessor)this.level, (BlockPos)mutableBlockPos, (BlockState)blockState);
                            throw new ReportedException(crashReport);
                        }
                    }
                }
            }
        }
    }
}
