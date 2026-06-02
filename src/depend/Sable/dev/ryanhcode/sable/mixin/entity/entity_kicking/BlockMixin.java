/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package dev.ryanhcode.sable.mixin.entity.entity_kicking;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={Block.class})
public abstract class BlockMixin {
    @Shadow
    private static void popResource(Level arg, Supplier<ItemEntity> supplier, ItemStack arg2) {
    }

    @Inject(method={"popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", shift=At.Shift.BEFORE)}, locals=LocalCapture.CAPTURE_FAILHARD, cancellable=true)
    private static void sable$popResourceFromFace(Level level, BlockPos blockPos, ItemStack itemStack, CallbackInfo ci, double yOffset, double x, double y, double z) {
        SubLevel subLevel = Sable.HELPER.getContaining(level, (Vec3i)blockPos);
        if (subLevel != null) {
            BlockMixin.popResource(level, () -> {
                ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
                Vec3 deltaMovement = itemEntity.getDeltaMovement();
                deltaMovement = subLevel.logicalPose().transformNormalInverse(deltaMovement);
                itemEntity.setDeltaMovement(deltaMovement);
                return itemEntity;
            }, itemStack);
            ci.cancel();
        }
    }
}
