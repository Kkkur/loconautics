/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.logistics.funnel.FunnelBlock
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.funnels;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={FunnelBlock.class})
public class FunnelBlockMixin {
    @Redirect(method={"entityInside"}, at=@At(value="INVOKE", target="Lnet/createmod/catnip/math/VecHelper;getCenterOf(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;"), remap=false)
    private Vec3 sable$projectFunnelPos(Vec3i pos, @Local(argsOnly=true) Level level) {
        return JOMLConversion.toMojang((Vector3dc)Sable.HELPER.projectOutOfSubLevel(level, JOMLConversion.atCenterOf((Vec3i)pos)));
    }
}
