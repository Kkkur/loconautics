/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Position
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.EntityStickExtension;
import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.EntityMovementExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public abstract class EntityMixin
implements EntityStickExtension {
    @Shadow
    private Level level;
    @Unique
    private Vec3 sable$plotPosition = null;

    @Shadow
    public abstract void setPos(Vec3 var1);

    @Shadow
    public abstract void moveTo(Vec3 var1);

    @Shadow
    public abstract void moveTo(double var1, double var3, double var5);

    @Inject(method={"tick"}, at={@At(value="RETURN")})
    private void sable$updateSubLevelPosition(CallbackInfo ci) {
        Player player;
        Entity self = (Entity)this;
        if (this.sable$plotPosition != null) {
            SubLevel subLevel = Sable.HELPER.getContaining(this.level, (Position)this.sable$plotPosition);
            if (subLevel != null) {
                this.setPos(subLevel.logicalPose().transformPosition(this.sable$plotPosition));
                ((EntityMovementExtension)((Object)this)).sable$setTrackingSubLevel(subLevel);
            } else {
                this.sable$plotPosition = null;
            }
        } else if (!(!this.level.isClientSide || self instanceof Player && (player = (Player)self).isLocalPlayer() || self instanceof ItemEntity)) {
            ((EntityMovementExtension)((Object)this)).sable$setTrackingSubLevel(null);
        }
    }

    @Override
    public void sable$plotLerpTo(Vec3 pos, int lerpSteps) {
        this.sable$setPlotPosition(pos);
    }

    @Override
    public void sable$setPlotPosition(@Nullable Vec3 position) {
        this.sable$plotPosition = position;
    }

    @Override
    @Nullable
    public Vec3 sable$getPlotPosition() {
        return this.sable$plotPosition;
    }

    @Inject(method={"recreateFromPacket"}, at={@At(value="TAIL")})
    public void sable$recreateFromPacket(ClientboundAddEntityPacket packet, CallbackInfo ci) {
        if (!EntitySubLevelUtil.shouldKick((Entity)this)) {
            return;
        }
        double packetX = packet.getX();
        double packetY = packet.getY();
        double packetZ = packet.getZ();
        SubLevel packetSubLevel = Sable.HELPER.getContaining(this.level, packetX, packetZ);
        if (packetSubLevel != null) {
            Vector3d globalPacketPos = packetSubLevel.logicalPose().transformPosition(new Vector3d(packetX, packetY, packetZ));
            this.moveTo(globalPacketPos.x, globalPacketPos.y, globalPacketPos.z);
        }
    }
}
