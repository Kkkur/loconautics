/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.render_fixes;

import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={SafeBlockEntityRenderer.class})
public class SafeBlockEntityRendererMixin {
    @ModifyVariable(method={"shouldCullItem"}, at=@At(value="HEAD"), remap=false, argsOnly=true)
    public Vec3 sable$projectItemPos(Vec3 itemPos) {
        return Sable.HELPER.projectOutOfSubLevel((Level)Minecraft.getInstance().level, itemPos);
    }
}
