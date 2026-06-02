/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Position
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.players.PlayerList
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.plot;

import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import java.util.List;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={PlayerList.class})
public class PlayerListMixin {
    @Shadow
    @Final
    private List<ServerPlayer> players;

    @Overwrite
    public void broadcast(@Nullable Player player, double x, double y, double z, double maxDistance, ResourceKey<Level> resourceKey, Packet<?> packet) {
        ActiveSableCompanion helper = Sable.HELPER;
        for (ServerPlayer value : this.players) {
            double dist;
            Level level = value.level();
            if (value == player || level.dimension() != resourceKey || !((dist = helper.distanceSquaredWithSubLevels(level, (Position)value.position(), x, y, z)) < maxDistance * maxDistance)) continue;
            value.connection.send(packet);
        }
    }
}
