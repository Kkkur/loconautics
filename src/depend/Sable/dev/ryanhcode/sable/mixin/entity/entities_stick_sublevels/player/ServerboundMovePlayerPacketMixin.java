/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
 *  net.minecraft.server.level.ServerPlayer
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels.player;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.player.ServerboundMovePlayerPacketExtension;
import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.EntityMovementExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ServerboundMovePlayerPacket.class})
public class ServerboundMovePlayerPacketMixin
implements ServerboundMovePlayerPacketExtension {
    @Mutable
    @Shadow
    @Final
    protected double x;
    @Mutable
    @Shadow
    @Final
    protected double y;
    @Mutable
    @Shadow
    @Final
    protected double z;
    @Shadow
    @Final
    protected boolean hasPos;

    @Override
    public void sable$handle(ServerPlayer player) {
        SubLevelContainer container;
        if (!this.hasPos) {
            return;
        }
        SubLevel subLevel = Sable.HELPER.getContaining(player.level(), this.x, this.z);
        if (subLevel == null && (container = SubLevelContainer.getContainer(player.level())) != null && container.inBounds(BlockPos.containing((double)this.x, (double)this.y, (double)this.z))) {
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
            ((EntityMovementExtension)player).sable$setTrackingSubLevel(null);
            return;
        }
        ((EntityMovementExtension)player).sable$setTrackingSubLevel(subLevel);
        if (subLevel != null) {
            Vector3d newPos = subLevel.logicalPose().transformPosition(new Vector3d(this.x, this.y, this.z));
            this.x = newPos.x;
            this.y = newPos.y;
            this.z = newPos.z;
        }
    }
}
