/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.EntityStickExtension;
import dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.packet_mixin.PacketActuallyInSubLevelExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ClientPacketListener.class})
public abstract class ClientPacketListenerMixin {
    @Shadow
    private ClientLevel level;

    @WrapOperation(method={"handleTeleportEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;lerpTo(DDDFFI)V")})
    private void sable$handleTeleportEntity(Entity instance, double x, double y, double z, float yRot, float xRot, int lerpSteps, Operation<Void> original, @Local(argsOnly=true) ClientboundTeleportEntityPacket packet) {
        PacketActuallyInSubLevelExtension extension;
        this.sable$lerp(instance, x, y, z, yRot, xRot, lerpSteps, true, packet instanceof PacketActuallyInSubLevelExtension && (extension = (PacketActuallyInSubLevelExtension)packet).sable$isActuallyInSubLevel());
    }

    @WrapOperation(method={"handleMoveEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;lerpTo(DDDFFI)V", ordinal=0)})
    private void sable$handleMoveEntity(Entity instance, double x, double y, double z, float yRot, float xRot, int lerpSteps, Operation<Void> original, @Local(argsOnly=true) ClientboundMoveEntityPacket packet) {
        PacketActuallyInSubLevelExtension extension;
        this.sable$lerp(instance, x, y, z, yRot, xRot, lerpSteps, false, packet instanceof PacketActuallyInSubLevelExtension && (extension = (PacketActuallyInSubLevelExtension)packet).sable$isActuallyInSubLevel());
    }

    @Unique
    private void sable$lerp(Entity entity, double pX, double pY, double pZ, float pYRot, float pXRot, int pLerpSteps, boolean pTeleport, boolean actuallyInSubLevel) {
        EntityStickExtension extension = (EntityStickExtension)entity;
        Vec3 pos = new Vec3(pX, pY, pZ);
        ClientSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        SubLevel subLevel = Sable.HELPER.getContaining((Level)this.level, (Position)pos);
        Vec3 plotPosition = extension.sable$getPlotPosition();
        if (!actuallyInSubLevel && subLevel == null && container.inBounds(BlockPos.containing((Position)pos))) {
            return;
        }
        if (subLevel != null && !actuallyInSubLevel) {
            if (!(entity instanceof LivingEntity)) {
                pos = subLevel.logicalPose().transformPosition(pos);
                entity.lerpTo(pos.x, pos.y, pos.z, pYRot, pXRot, pLerpSteps);
                return;
            }
            if (plotPosition == null) {
                extension.sable$setPlotPosition(subLevel.logicalPose().transformPositionInverse(entity.position()));
            } else {
                SubLevel existingSubLevel = Sable.HELPER.getContaining((Level)this.level, (Position)plotPosition);
                if (existingSubLevel != null && subLevel != existingSubLevel) {
                    Vec3 globalPlotPos = existingSubLevel.logicalPose().transformPosition(plotPosition);
                    extension.sable$setPlotPosition(subLevel.logicalPose().transformPositionInverse(globalPlotPos));
                }
            }
            entity.lerpTo(pX, pY, pZ, pYRot, pXRot, pLerpSteps);
            extension.sable$plotLerpTo(pos, pLerpSteps);
        } else {
            SubLevel existingSubLevel = Sable.HELPER.getContaining((Level)this.level, (Position)entity.position());
            if (subLevel != null && actuallyInSubLevel && existingSubLevel != subLevel) {
                entity.setPos(subLevel.logicalPose().transformPositionInverse(entity.position()));
            } else if (existingSubLevel != null && subLevel == null) {
                entity.setPos(existingSubLevel.logicalPose().transformPosition(entity.position()));
            }
            entity.lerpTo(pX, pY, pZ, pYRot, pXRot, pLerpSteps);
            extension.sable$setPlotPosition(null);
        }
    }
}
