/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  com.simibubi.create.content.kinetics.saw.SawMovementBehaviour
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.behaviour_compatibility.block_breaking_behaviour;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={SawMovementBehaviour.class})
public class SawMovementBehaviourMixin {
    @Redirect(method={"dropItemFromCutTree"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D"))
    public double sable$fixSpeed(Vec3 instance, Vec3 vec3, @Local(argsOnly=true) MovementContext context) {
        return Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels(context.world, (Position)instance, (Position)vec3));
    }

    @Redirect(method={"dropItemFromCutTree"}, at=@At(value="FIELD", target="Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;relativeMotion:Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 sable$fixRelativeMotion(MovementContext instance, @Local(argsOnly=true) MovementContext context, @Local(ordinal=0) Vec3 dropPos) {
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel parentSublevel = helper.getContaining(context.world, (Vec3i)context.contraption.anchor);
        SubLevel targetSublevel = helper.getContaining(context.world, (Position)dropPos);
        Vec3 orignalMotion = context.relativeMotion;
        if (parentSublevel != null) {
            orignalMotion = parentSublevel.logicalPose().transformNormal(orignalMotion);
        }
        if (targetSublevel != null) {
            orignalMotion = targetSublevel.logicalPose().transformNormalInverse(orignalMotion);
        }
        return orignalMotion;
    }
}
