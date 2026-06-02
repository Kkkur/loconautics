/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.outliner.ChasingAABBOutline
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.render_fixes;

import net.createmod.catnip.outliner.ChasingAABBOutline;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ChasingAABBOutline.class})
public class ChasingAABBOutlinerMixin {
    @Inject(method={"interpolateBBs"}, at={@At(value="HEAD")}, remap=false, cancellable=true)
    private static void sable$bbDistanceCheck(AABB current, AABB target, float pt, CallbackInfoReturnable<AABB> cir) {
        if (current.getCenter().distanceTo(target.getCenter()) > 100.0) {
            cir.setReturnValue((Object)target);
        }
    }
}
