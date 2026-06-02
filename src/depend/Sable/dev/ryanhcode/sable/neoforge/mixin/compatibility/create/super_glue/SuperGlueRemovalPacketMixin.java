/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.glue.SuperGlueRemovalPacket
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.super_glue;

import com.simibubi.create.content.contraptions.glue.SuperGlueRemovalPacket;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={SuperGlueRemovalPacket.class})
public class SuperGlueRemovalPacketMixin {
    @Redirect(method={"handle"}, at=@At(value="INVOKE", target="Lnet/minecraft/server/level/ServerPlayer;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
    private double sable$distanceSquared(ServerPlayer instance, Vec3 vec3) {
        return Sable.HELPER.distanceSquaredWithSubLevels(instance.level(), (Position)instance.position(), (Position)vec3);
    }
}
