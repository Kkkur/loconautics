/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  com.simibubi.create.content.kinetics.belt.BeltBlockEntity
 *  com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler
 *  com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler$TransportedEntityInfo
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.belt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BeltMovementHandler.class})
public class BeltMovementHandlerMixin {
    @WrapOperation(method={"transportEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getY()D", ordinal=0), @At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getY()D", ordinal=1), @At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;getY()D", ordinal=2)})
    private static double sable$getLocalEntityY(Entity instance, Operation<Double> original, @Local(argsOnly=true) BeltBlockEntity be) {
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)be);
        if (subLevel != null) {
            return subLevel.logicalPose().transformPositionInverse((Vec3)instance.position()).y;
        }
        return (Double)original.call(new Object[]{instance});
    }

    @ModifyVariable(method={"transportEntity"}, at=@At(value="STORE"), ordinal=0)
    private static double sable$diffCenter(double originalValue, @Local(argsOnly=true) BeltBlockEntity be, @Local(argsOnly=true) Entity entity, @Local(ordinal=0) BlockPos pos, @Local Direction.Axis axis) {
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)be);
        if (subLevel == null) {
            return originalValue;
        }
        Vec3 entityPos = subLevel.logicalPose().transformPositionInverse(entity.position());
        return axis == Direction.Axis.Z ? (double)pos.getX() + 0.5 - entityPos.x() : (double)pos.getZ() + 0.5 - entityPos.z();
    }

    @Inject(method={"transportEntity"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;maxUpStep()F")})
    private static void sable$maxUpStep(BeltBlockEntity beltBE, Entity entityIn, BeltMovementHandler.TransportedEntityInfo info, CallbackInfo ci, @Local(ordinal=0) LocalRef<Vec3> movement) {
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)beltBE);
        if (subLevel != null) {
            movement.set((Object)subLevel.logicalPose().transformNormal((Vec3)movement.get()));
        }
    }
}
