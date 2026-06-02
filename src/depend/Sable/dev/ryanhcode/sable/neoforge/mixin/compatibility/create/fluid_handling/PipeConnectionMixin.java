/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.fluids.PipeConnection
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.fluid_handling;

import com.simibubi.create.content.fluids.PipeConnection;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={PipeConnection.class})
public class PipeConnectionMixin {
    @Redirect(method={"isRenderEntityWithinDistance"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D"))
    private static double sable$distanceIncludingSubLevels(Vec3 instance, Vec3 vec3) {
        return Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels((Level)Minecraft.getInstance().level, (Position)instance, (Position)vec3));
    }
}
