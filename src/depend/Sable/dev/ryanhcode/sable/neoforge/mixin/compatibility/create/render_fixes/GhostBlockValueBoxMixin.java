/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Position
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.render_fixes;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.util.SublevelRenderOffsetHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets={"net.createmod.catnip.ghostblock.GhostBlockRenderer$TransparentGhostBlockRenderer"})
public abstract class GhostBlockValueBoxMixin {
    @Redirect(method={"render"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", ordinal=0))
    public void sable$translate(PoseStack ms, double pX, double pY, double pZ) {
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 center = new Vec3(pX + camera.x, pY + camera.y, pZ + camera.z);
        SublevelRenderOffsetHelper.posePlotToProjected(Sable.HELPER.getContainingClient((Position)center), ms);
        Vec3 translation = SublevelRenderOffsetHelper.translation(center);
        ms.translate(pX - translation.x, pY - translation.y, pZ - translation.z);
    }
}
