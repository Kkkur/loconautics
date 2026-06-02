/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.dispenser.BlockSource
 *  net.minecraft.core.dispenser.ProjectileDispenseBehavior
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.recoil;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ProjectileDispenseBehavior.class})
public class ProjectileDispenseBehaviorMixin {
    @Inject(method={"execute"}, at={@At(value="TAIL")})
    private void sable$applyRecoil(BlockSource blockSource, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir, @Local Position position, @Local Direction direction) {
        ServerLevel level = blockSource.level();
        SubLevel subLevel = Sable.HELPER.getContaining((Level)level, position);
        if (subLevel instanceof ServerSubLevel) {
            ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
            Vector3d impulse = new Vector3d((double)direction.getStepX(), (double)direction.getStepY(), (double)direction.getStepZ()).mul(-1.5);
            RigidBodyHandle.of(serverSubLevel).applyImpulseAtPoint((Vector3dc)JOMLConversion.toJOML((Position)position), (Vector3dc)impulse);
        }
    }
}
