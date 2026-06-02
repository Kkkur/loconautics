/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.lectern_controller;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.LecternControllerBlockEntityExtension;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LecternControllerBlockEntity.class})
public abstract class LecternControllerBlockEntityMixin
extends SmartBlockEntity
implements LecternControllerBlockEntityExtension {
    @Shadow
    private UUID user;
    @Unique
    private boolean sable$noDrop;

    public LecternControllerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    protected abstract void stopUsing(Player var1);

    @Inject(method={"dropController"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z")}, cancellable=true)
    public void sable$dropController(BlockState state, CallbackInfo ci) {
        if (!this.sable$noDrop) {
            return;
        }
        ci.cancel();
        Entity entity = ((ServerLevel)this.level).getEntity(this.user);
        if (entity instanceof Player) {
            Player player = (Player)entity;
            this.stopUsing(player);
        }
    }

    @Redirect(method={"playerInRange"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
    private static double sable$fixDistanceCheck(Vec3 a, Vec3 b, @Local(argsOnly=true) Level level) {
        return Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)a, (Position)b);
    }

    @Override
    public void sable$setNoDrop() {
        this.sable$noDrop = true;
    }
}
