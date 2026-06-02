/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.world.entity.Entity
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entities_turn_with_sub_levels;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.UUID;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GameRenderer.class})
public class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private final Quaterniond sable$lastOrientation = new Quaterniond();
    @Unique
    private final Quaterniond sable$relativeOrientation = new Quaterniond();
    @Unique
    private UUID sable$lastSubLevel = null;

    @Inject(method={"renderLevel"}, at={@At(value="HEAD")})
    public void renderLevel(DeltaTracker deltaTracker, CallbackInfo ci) {
        LocalPlayer player = this.minecraft.player;
        SubLevel standingSubLevel = Sable.HELPER.getTrackingSubLevel((Entity)player);
        if (standingSubLevel != null && player.getVehicle() == null && !standingSubLevel.isRemoved() && !EntitySubLevelUtil.hasCustomEntityOrientation((Entity)player)) {
            Quaterniond relativeOrientation;
            Quaterniondc customOrientation;
            Quaterniondc current = ((ClientSubLevel)standingSubLevel).renderPose().orientation();
            if (this.sable$lastSubLevel == null || !this.sable$lastSubLevel.equals(standingSubLevel.getUniqueId())) {
                this.sable$lastOrientation.set(current);
                this.sable$lastSubLevel = standingSubLevel.getUniqueId();
            }
            if ((customOrientation = EntitySubLevelUtil.getCustomEntityOrientation((Entity)player, 1.0f)) != null) {
                Quaterniond inverseCustom = new Quaterniond(customOrientation).conjugate();
                Quaterniond currentLocal = current.premul((Quaterniondc)inverseCustom, new Quaterniond());
                Quaterniond lastLocal = this.sable$lastOrientation.premul((Quaterniondc)inverseCustom, new Quaterniond());
                relativeOrientation = currentLocal.div((Quaterniondc)lastLocal, this.sable$relativeOrientation);
            } else {
                current.div((Quaterniondc)this.sable$lastOrientation, this.sable$relativeOrientation);
                relativeOrientation = current.div((Quaterniondc)this.sable$lastOrientation, this.sable$relativeOrientation);
            }
            double angleDiff = 2.0 * relativeOrientation.y / relativeOrientation.w;
            float delta = (float)Math.toDegrees(angleDiff);
            player.yBodyRot -= delta;
            player.yBodyRotO -= delta;
            player.yHeadRot -= delta;
            player.yHeadRotO -= delta;
            player.setYRot(player.getYRot() - delta);
            player.yRotO -= delta;
            this.sable$lastOrientation.set(current);
        } else {
            this.sable$lastSubLevel = null;
        }
    }
}
