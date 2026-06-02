/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.trains.track.TrackTargetingClient
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.Translate
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.tracks;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.track.TrackTargetingClient;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.Translate;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={TrackTargetingClient.class})
public class TrackTargetingClientMixin {
    @Redirect(method={"render"}, at=@At(value="INVOKE", target="Ldev/engine_room/flywheel/lib/transform/PoseTransformStack;translate(Lnet/minecraft/world/phys/Vec3;)Ldev/engine_room/flywheel/lib/transform/Translate;"))
    private static Translate sable$manipulateMatrixStack(PoseTransformStack instance, Vec3 vec3, @Local(ordinal=0) Minecraft minecraft, @Local(ordinal=0) BlockPos pos, @Local(argsOnly=true) Vec3 camera) {
        ClientLevel level = minecraft.level;
        SubLevel subLevel = Sable.HELPER.getContaining((Level)level, (Vec3i)pos);
        if (subLevel instanceof ClientSubLevel) {
            ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
            Pose3dc renderPose = clientSubLevel.renderPose();
            Vec3 renderPos = renderPose.transformPosition(Vec3.atLowerCornerOf((Vec3i)pos));
            Quaternionf renderOrientation = new Quaternionf(renderPose.orientation());
            return ((PoseTransformStack)instance.translate(renderPos.x() - camera.x(), renderPos.y() - camera.y(), renderPos.z() - camera.z())).rotate((Quaternionfc)renderOrientation);
        }
        return instance.translate(vec3);
    }
}
