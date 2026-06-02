/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.TerrainParticle
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.particle;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={TerrainParticle.class})
public abstract class TerrainParticleMixin
extends Particle {
    @Shadow
    @Final
    private BlockPos pos;

    protected TerrainParticleMixin(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    @Redirect(method={"getLightColor"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)I"))
    private int sable$getLightColor(BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, @Local int existingColor) {
        ClientSubLevelContainer container = SubLevelContainer.getContainer(Minecraft.getInstance().level);
        assert (container != null);
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Vec3i)this.pos);
        if (subLevel instanceof ClientSubLevel) {
            ClientSubLevel clientSubLevel = subLevel;
            int color = LevelRenderer.getLightColor((BlockAndTintGetter)blockAndTintGetter, (BlockPos)blockPos);
            return clientSubLevel.scaleLightColor(color);
        }
        if (container.inBounds(blockPos)) {
            return existingColor;
        }
        return LevelRenderer.getLightColor((BlockAndTintGetter)blockAndTintGetter, (BlockPos)blockPos);
    }
}
