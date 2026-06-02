/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.flywheel;

import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={AbstractBlockEntityVisual.class})
public class AbstractBlockEntityVisualMixin {
    @Redirect(method={"relight(Lnet/minecraft/core/BlockPos;[Ldev/engine_room/flywheel/lib/instance/FlatLit;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)I"))
    private int sable$getLightColor(BlockAndTintGetter blockAndTintGetter, BlockPos blockPos) {
        ClientSubLevelContainer container = SubLevelContainer.getContainer(Minecraft.getInstance().level);
        assert (container != null);
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Vec3i)blockPos);
        if (subLevel instanceof ClientSubLevel) {
            ClientSubLevel clientSubLevel = subLevel;
            int color = LevelRenderer.getLightColor((BlockAndTintGetter)blockAndTintGetter, (BlockPos)blockPos);
            return clientSubLevel.scaleLightColor(color);
        }
        return LevelRenderer.getLightColor((BlockAndTintGetter)blockAndTintGetter, (BlockPos)blockPos);
    }
}
