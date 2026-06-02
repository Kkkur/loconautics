/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Leashable
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entity_rendering;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityRenderer.class})
public abstract class EntityRendererMixin {
    @Shadow
    @Final
    protected EntityRenderDispatcher entityRenderDispatcher;

    @ModifyReturnValue(method={"getPackedLightCoords"}, at={@At(value="RETURN")})
    public final int getPackedLightCoords(int original, Entity arg, float f) {
        Vec3 lightProbeOffset = arg.getLightProbePosition(f).subtract(arg.getEyePosition(f));
        Vector3d lightProbePosition = JOMLConversion.toJOML((Position)Sable.HELPER.getEyePositionInterpolated(arg, f)).add(lightProbeOffset.x, lightProbeOffset.y, lightProbeOffset.z);
        BlockPos blockpos = BlockPos.containing((double)lightProbePosition.x, (double)lightProbePosition.y, (double)lightProbePosition.z);
        return LightTexture.pack((int)EntityRendererMixin.sable$getSubLevelAccountedBlockLight(original, arg.level(), LightLayer.BLOCK, blockpos, (Vector3dc)lightProbePosition), (int)EntityRendererMixin.sable$getSubLevelAccountedSkyLight(original, arg.level(), LightLayer.SKY, blockpos, (Vector3dc)lightProbePosition));
    }

    @Redirect(method={"getSkyLightLevel"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I"))
    private int sable$getSkyLightLevel(Level instance, LightLayer lightLayer, BlockPos blockPos) {
        return EntityRendererMixin.sable$getSubLevelAccountedSkyLight(-1, instance, lightLayer, blockPos, (Vector3dc)JOMLConversion.atCenterOf((Vec3i)blockPos));
    }

    @Unique
    private static int sable$getSubLevelAccountedSkyLight(int original, Level instance, LightLayer lightLayer, BlockPos blockPos, Vector3dc probePosition) {
        Iterable<SubLevel> all = Sable.HELPER.getAllIntersecting(instance, (BoundingBox3dc)new BoundingBox3d(blockPos));
        int baseBrightness = original == -1 ? instance.getBrightness(lightLayer, blockPos) : LightTexture.sky((int)original);
        BlockPos.MutableBlockPos localPosition = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos heightmapPos = new BlockPos.MutableBlockPos();
        Vector3d tempProbePosition = new Vector3d();
        for (SubLevel subLevel : all) {
            ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
            clientSubLevel.renderPose().transformPositionInverse(probePosition, tempProbePosition);
            localPosition.set(tempProbePosition.x, tempProbePosition.y, tempProbePosition.z);
            Level level = subLevel.getLevel();
            heightmapPos.setWithOffset((Vec3i)localPosition, Direction.UP);
            LevelPlot plot = subLevel.getPlot();
            boolean isAboveGround = false;
            while (heightmapPos.getY() >= plot.getBoundingBox().minY()) {
                if (!level.getBlockState((BlockPos)heightmapPos).isAir()) {
                    isAboveGround = true;
                    break;
                }
                heightmapPos.move(Direction.DOWN);
            }
            if (!isAboveGround) continue;
            if (lightLayer == LightLayer.BLOCK) {
                baseBrightness = Math.max(baseBrightness, level.getBrightness(lightLayer, (BlockPos)localPosition));
                continue;
            }
            if (lightLayer != LightLayer.SKY) continue;
            int brightness = clientSubLevel.scaleSkyLight(level.getBrightness(lightLayer, (BlockPos)localPosition));
            baseBrightness = Math.min(baseBrightness, brightness);
        }
        return baseBrightness;
    }

    @Redirect(method={"getBlockLightLevel"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I"))
    private int sable$getBlockLightLevel(Level instance, LightLayer lightLayer, BlockPos blockPos) {
        return EntityRendererMixin.sable$getSubLevelAccountedBlockLight(-1, instance, lightLayer, blockPos, (Vector3dc)JOMLConversion.atCenterOf((Vec3i)blockPos));
    }

    @Unique
    private static int sable$getSubLevelAccountedBlockLight(int original, Level instance, LightLayer lightLayer, BlockPos blockPos, Vector3dc lightProbePosition) {
        Iterable<SubLevel> all = Sable.HELPER.getAllIntersecting(instance, (BoundingBox3dc)new BoundingBox3d(blockPos).expand(2.0));
        int l = original == -1 ? instance.getBrightness(lightLayer, blockPos) : LightTexture.block((int)original);
        BlockPos.MutableBlockPos probeBlockPos = new BlockPos.MutableBlockPos();
        Vector3d tempProbePosition = new Vector3d();
        for (SubLevel subLevel : all) {
            ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
            clientSubLevel.renderPose().transformPositionInverse(lightProbePosition, tempProbePosition);
            l = Math.max(l, subLevel.getLevel().getBrightness(lightLayer, (BlockPos)probeBlockPos.set(tempProbePosition.x, tempProbePosition.y, tempProbePosition.z)));
        }
        return l;
    }

    @Inject(method={"shouldRender"}, at={@At(value="HEAD")}, cancellable=true)
    private <E extends Entity> void sable$shouldRender(E entity, Frustum frustum, double pCamX, double pCamY, double pCamZ, CallbackInfoReturnable<Boolean> cir) {
        if (entity.noCulling) {
            cir.setReturnValue((Object)true);
            return;
        }
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient(entity);
        if (subLevel != null) {
            Vec3 globalPos = subLevel.renderPose().transformPosition(entity.position());
            AABB aabb = new AABB(globalPos.x - 2.0, globalPos.y - 2.0, globalPos.z - 2.0, globalPos.x + 2.0, globalPos.y + 2.0, globalPos.z + 2.0);
            cir.setReturnValue((Object)frustum.isVisible(aabb));
            return;
        }
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel(entity);
        if (trackingSubLevel != null) {
            float pt = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
            Vec3 positionInterpolated = Sable.HELPER.getEyePositionInterpolated(entity, pt).subtract(0.0, (double)entity.getEyeHeight(), 0.0);
            AABB aABB = entity.getBoundingBoxForCulling().inflate(0.5);
            if (aABB.hasNaN() || aABB.getSize() == 0.0) {
                aABB = new AABB(entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0);
            }
            if (frustum.isVisible(aABB = aABB.move(positionInterpolated.subtract(entity.position())))) {
                cir.setReturnValue((Object)true);
            } else {
                Leashable leashable;
                Entity entity2;
                if (entity instanceof Leashable && (entity2 = (leashable = (Leashable)entity).getLeashHolder()) != null) {
                    cir.setReturnValue((Object)frustum.isVisible(entity2.getBoundingBoxForCulling()));
                    return;
                }
                cir.setReturnValue((Object)false);
            }
        }
    }
}
