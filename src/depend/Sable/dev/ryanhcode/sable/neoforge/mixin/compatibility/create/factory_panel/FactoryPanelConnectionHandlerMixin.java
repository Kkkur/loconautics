/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.factory_panel;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={FactoryPanelConnectionHandler.class})
public class FactoryPanelConnectionHandlerMixin {
    @Redirect(method={"clientTick"}, at=@At(value="INVOKE", target="Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z"))
    private static boolean closerThan(BlockPos instance, Vec3i pos, double maxDistance, @Local Minecraft mc) {
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)mc.level, instance.getX(), instance.getY(), instance.getZ(), pos.getX(), pos.getY(), pos.getZ()) < maxDistance * maxDistance;
    }
}
