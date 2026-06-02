/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorShape
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.frogports;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorShape;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.neoforge.mixin.compatibility.create.frogports.ChainConveyorShapeAccessor;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ChainConveyorInteractionHandler.class})
public class ChainConveyorInteractionHandlerMixin {
    @Shadow
    public static BlockPos selectedLift;
    @Shadow
    public static ChainConveyorShape selectedShape;

    @Redirect(method={"clientTick"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
    private static double sable$addParticleInternal(Vec3 instance, Vec3 vec3) {
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)Minecraft.getInstance().level, (Position)instance, (Position)vec3);
    }

    @Redirect(method={"clientTick"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", ordinal=0))
    private static Vec3 sable$fromSubLiftVec(Vec3 from, Vec3 liftVec, @Local(ordinal=0) ChainConveyorShape shape) {
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)liftVec);
        if (subLevel != null) {
            return subLevel.logicalPose().transformPositionInverse(from).subtract(liftVec);
        }
        return from.subtract(liftVec);
    }

    @Redirect(method={"clientTick"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", ordinal=1))
    private static Vec3 sable$toSubLiftVec(Vec3 to, Vec3 liftVec, @Local(ordinal=0) ChainConveyorShape shape) {
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)liftVec);
        if (subLevel != null) {
            return subLevel.logicalPose().transformPositionInverse(to).subtract(liftVec);
        }
        return to.subtract(liftVec);
    }

    @Overwrite
    public static void drawCustomBlockSelection(PoseStack ms, MultiBufferSource buffer, Vec3 camera) {
        if (selectedLift == null || selectedShape == null) {
            return;
        }
        VertexConsumer vb = buffer.getBuffer(RenderType.lines());
        ms.pushPose();
        Vec3 pos = Vec3.atLowerCornerOf((Vec3i)selectedLift);
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)pos);
        if (subLevel instanceof ClientSubLevel) {
            ClientSubLevel clientSubLevel = subLevel;
            Pose3dc renderPose = clientSubLevel.renderPose();
            pos = renderPose.transformPosition(pos);
            ms.translate(pos.x() - camera.x, pos.y() - camera.y, pos.z() - camera.z);
            ms.mulPose(new Quaternionf(renderPose.orientation()));
        } else {
            ms.translate(pos.x() - camera.x, pos.y() - camera.y, pos.z() - camera.z);
        }
        ((ChainConveyorShapeAccessor)selectedShape).invokeDrawOutline(selectedLift, ms, vb);
        ms.popPose();
    }
}
