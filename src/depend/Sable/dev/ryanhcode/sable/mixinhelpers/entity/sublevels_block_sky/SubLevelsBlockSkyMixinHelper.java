/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.mixinhelpers.entity.sublevels_block_sky;

import dev.ryanhcode.sable.mixinterface.clip_overwrite.ClipContextExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SubLevelsBlockSkyMixinHelper {
    @ApiStatus.Internal
    public static boolean checkSkyWithSublevels(Level level, BlockPos pos) {
        Vec3 start = Vec3.atBottomCenterOf((Vec3i)pos);
        ClipContext context = new ClipContext(start, new Vec3(start.x, (double)level.getMaxBuildHeight(), start.z), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty());
        ((ClipContextExtension)context).sable$setIgnoreMainLevel(true);
        return level.clip(context).getType() != HitResult.Type.MISS;
    }
}
