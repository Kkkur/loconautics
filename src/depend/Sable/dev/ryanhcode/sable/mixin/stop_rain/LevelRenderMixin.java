/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.stop_rain;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.LevelAccelerator;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={LevelRenderer.class})
public class LevelRenderMixin {
    @Unique
    private BlockPos.MutableBlockPos sable$tempPos;

    @Unique
    private static int sable$getSubLevelHeight(Level level, int pX, int yOffset, int pZ) {
        LevelAccelerator accelerator = new LevelAccelerator(level);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        Vector3d checkingPos = new Vector3d();
        Vector3d localUp = new Vector3d(0.0, 1.0, 0.0);
        BoundingBox3d minMaxBB = new BoundingBox3d((double)pX, (double)level.getMinBuildHeight(), (double)pZ, (double)(pX + 1), (double)level.getMaxBuildHeight(), (double)(pZ + 1));
        int maxHeight = Integer.MIN_VALUE;
        block0: for (SubLevel subLevel : Sable.HELPER.getAllIntersecting(level, (BoundingBox3dc)minMaxBB)) {
            subLevel.logicalPose().transformPositionInverse(checkingPos.set((double)((float)pX + 0.5f), subLevel.boundingBox().maxY(), (double)((float)pZ + 0.5f)));
            subLevel.logicalPose().transformNormalInverse(localUp.set(0.0, 1.0, 0.0));
            double checkingDistance = subLevel.boundingBox().maxY() - subLevel.boundingBox().minY();
            int i = 0;
            while ((double)i < checkingDistance) {
                checkingPos.sub((Vector3dc)localUp);
                BlockState gatheredState = accelerator.getBlockState((BlockPos)mutableBlockPos.set(checkingPos.x, checkingPos.y, checkingPos.z));
                if (gatheredState.blocksMotion() || !gatheredState.getFluidState().isEmpty()) {
                    subLevel.logicalPose().transformPosition(checkingPos);
                    maxHeight = (int)Math.max((double)maxHeight, checkingPos.y + (double)yOffset);
                    continue block0;
                }
                ++i;
            }
        }
        return maxHeight;
    }

    @WrapOperation(method={"renderSnowAndRain"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I")})
    public int sable$preventRainThoughSubLevel(Level instance, Heightmap.Types types, int i, int j, Operation<Integer> original) {
        return Math.max((Integer)original.call(new Object[]{instance, types, i, j}), LevelRenderMixin.sable$getSubLevelHeight(instance, i, 1, j));
    }

    @WrapOperation(method={"tickRain"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/LevelReader;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;")})
    public BlockPos sable$stopSplashParticles(LevelReader instance, Heightmap.Types types, BlockPos blockPos, Operation<BlockPos> original) {
        int height = ((BlockPos)original.call(new Object[]{instance, types, blockPos})).getY();
        if (instance instanceof Level) {
            Level level = (Level)instance;
            height = Math.max(height, LevelRenderMixin.sable$getSubLevelHeight(level, blockPos.getX(), 2, blockPos.getZ()));
        }
        return new BlockPos(blockPos.getX(), height, blockPos.getZ());
    }
}
