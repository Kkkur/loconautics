/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.RelativeMovement
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.mixin.entity.teleport_players;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.Sable;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={ServerPlayer.class})
public class ServerPlayerMixin {
    @WrapMethod(method={"teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"})
    public boolean sable$teleportTo(ServerLevel serverLevel, double x, double y, double z, Set<RelativeMovement> set, float g, float h, Operation<Boolean> original) {
        Vector3d globalPos = Sable.HELPER.projectOutOfSubLevel((Level)serverLevel, new Vector3d(x, y, z));
        return (Boolean)original.call(new Object[]{serverLevel, globalPos.x, globalPos.y, globalPos.z, set, Float.valueOf(g), Float.valueOf(h)});
    }
}
