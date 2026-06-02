/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlock
 *  com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.frogports;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlock;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={FrogportBlock.class})
public class FrogportBlockMixin {
    @WrapOperation(method={"lambda$setPlacedBy$0"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;position()Lnet/minecraft/world/phys/Vec3;")})
    private static Vec3 sable$projectPlayerPosition(LivingEntity instance, Operation<Vec3> original, @Local(argsOnly=true, name={"arg2"}) FrogportBlockEntity be) {
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)be);
        if (subLevel == null) {
            return (Vec3)original.call(new Object[]{instance});
        }
        return subLevel.logicalPose().transformPositionInverse((Vec3)original.call(new Object[]{instance}));
    }
}
