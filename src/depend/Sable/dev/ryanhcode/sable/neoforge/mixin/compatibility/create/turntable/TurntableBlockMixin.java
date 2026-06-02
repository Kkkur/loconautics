/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.kinetics.turntable.TurntableBlock
 *  com.simibubi.create.content.kinetics.turntable.TurntableBlockEntity
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.turntable;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.turntable.TurntableBlock;
import com.simibubi.create.content.kinetics.turntable.TurntableBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={TurntableBlock.class})
public class TurntableBlockMixin {
    @Redirect(method={"lambda$entityInside$0"}, at=@At(value="INVOKE", target="Lnet/createmod/catnip/math/VecHelper;getCenterOf(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 sable$fixPos(Vec3i pos, @Local(argsOnly=true) TurntableBlockEntity be) {
        Level level = be.getLevel();
        return JOMLConversion.toMojang((Vector3dc)Sable.HELPER.projectOutOfSubLevel(level, JOMLConversion.atCenterOf((Vec3i)pos)));
    }

    @Redirect(method={"entityInside"}, at=@At(value="INVOKE", target="Lnet/minecraft/core/BlockPos;getY()I"))
    private int sable$fixPosY(BlockPos instance, @Local(argsOnly=true) Level level) {
        return (int)Sable.HELPER.projectOutOfSubLevel((Level)level, (Vector3d)JOMLConversion.atLowerCornerOf((Vec3i)instance)).y;
    }
}
