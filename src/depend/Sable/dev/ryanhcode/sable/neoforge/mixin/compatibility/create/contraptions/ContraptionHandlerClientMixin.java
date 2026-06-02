/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.AbstractContraptionEntity
 *  com.simibubi.create.content.contraptions.ContraptionHandlerClient
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.contraptions;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ContraptionHandlerClient.class})
public abstract class ContraptionHandlerClientMixin {
    @Redirect(method={"getRayInputs"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D"))
    private static double sable$projectDistanceTo1(Vec3 eyePos, Vec3 itemPos) {
        return Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels((Level)Minecraft.getInstance().level, (Position)eyePos, (Position)itemPos));
    }

    @Redirect(method={"rightClickingOnContraptionsGetsHandledLocally"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D"))
    private static double sable$projectDistanceTo2(Vec3 eyePos, Vec3 itemPos) {
        return Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels((Level)Minecraft.getInstance().level, (Position)eyePos, (Position)itemPos));
    }

    @Redirect(method={"rightClickingOnContraptionsGetsHandledLocally"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;getBoundingBox()Lnet/minecraft/world/phys/AABB;"))
    private static AABB sable$moveBoundingBoxToProjectedPos(AbstractContraptionEntity instance) {
        Vec3 projectedPos = Sable.HELPER.projectOutOfSubLevel(instance.level(), instance.getAnchorVec());
        AABB boundingBox = instance.getBoundingBox();
        return boundingBox.move(Vec3.ZERO.subtract(boundingBox.getCenter())).move(projectedPos);
    }

    @Redirect(method={"rayTraceContraption"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;toLocalVector(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"), remap=false)
    private static Vec3 sable$projectedContraptionClip(AbstractContraptionEntity abce, Vec3 localVec, float partialTicks) {
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel sublevel1 = helper.getContaining(abce.level(), (Position)localVec);
        SubLevel contraptionSublevel = helper.getContaining((Entity)abce);
        if (contraptionSublevel != sublevel1) {
            if (sublevel1 != null) {
                localVec = sublevel1.logicalPose().transformPosition(localVec);
            }
            if (contraptionSublevel != null) {
                localVec = contraptionSublevel.logicalPose().transformPositionInverse(localVec);
            }
        }
        return abce.toLocalVector(localVec, 1.0f);
    }
}
