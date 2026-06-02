/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.belt.BeltRenderer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.render_fixes;

import com.simibubi.create.content.kinetics.belt.BeltRenderer;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={BeltRenderer.class})
public class BeltRendererMixin {
    @Redirect(method={"renderItem"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D"))
    public double sable$projectDistanceTo(Vec3 eyePos, Vec3 itemPos) {
        return Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels((Level)Minecraft.getInstance().level, (Position)eyePos, (Position)itemPos));
    }
}
