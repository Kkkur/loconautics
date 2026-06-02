/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Camera$NearPlane
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.FogType
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entity_swimming;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.Arrays;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Camera.class})
public abstract class CameraMixin {
    @Shadow
    private Vec3 position;
    @Shadow
    private BlockGetter level;

    @Shadow
    public abstract Camera.NearPlane getNearPlane();

    @Inject(method={"getFluidInCamera"}, at={@At(value="RETURN")}, cancellable=true)
    public void sable$getFluidInCamera(CallbackInfoReturnable<FogType> cir) {
        if (cir.getReturnValue() == FogType.NONE) {
            BoundingBox3d bounds = new BoundingBox3d(this.position.x - 0.5, this.position.y - 0.5, this.position.z - 0.5, this.position.x + 0.5, this.position.y + 0.5, this.position.z + 0.5);
            Iterable<SubLevel> intersecting = Sable.HELPER.getAllIntersecting((Level)this.level, (BoundingBox3dc)bounds);
            for (SubLevel subLevel : intersecting) {
                FogType fogType = this.sable$getFluidInCameraAt(((ClientSubLevel)subLevel).renderPose());
                if (fogType == null) continue;
                cir.setReturnValue((Object)fogType);
                return;
            }
        }
    }

    @Unique
    private FogType sable$getFluidInCameraAt(Pose3dc pose) {
        Vec3 localPosition = pose.transformPositionInverse(this.position);
        BlockPos localBlockPosition = BlockPos.containing((Position)localPosition);
        FluidState fluidState = this.level.getFluidState(localBlockPosition);
        if (fluidState.is(FluidTags.WATER) && localPosition.y < (double)((float)localBlockPosition.getY() + fluidState.getHeight(this.level, localBlockPosition))) {
            return FogType.WATER;
        }
        Camera.NearPlane nearPlane = this.getNearPlane();
        for (Vec3 planeDir : Arrays.asList(nearPlane.getPointOnPlane(0.0f, 0.0f), nearPlane.getTopLeft(), nearPlane.getTopRight(), nearPlane.getBottomLeft(), nearPlane.getBottomRight())) {
            Vec3 localPos = pose.transformPositionInverse(this.position.add(planeDir));
            BlockPos blockPos = BlockPos.containing((Position)localPos);
            FluidState fluidState2 = this.level.getFluidState(blockPos);
            if (fluidState2.is(FluidTags.LAVA)) {
                if (!(localPos.y <= (double)(fluidState2.getHeight(this.level, blockPos) + (float)blockPos.getY()))) continue;
                return FogType.LAVA;
            }
            BlockState blockState = this.level.getBlockState(blockPos);
            if (!blockState.is(Blocks.POWDER_SNOW)) continue;
            return FogType.POWDER_SNOW;
        }
        return FogType.NONE;
    }
}
