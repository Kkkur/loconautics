/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  com.simibubi.create.content.equipment.zapper.ZapperItem
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.zapper;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.equipment.zapper.ZapperItem;
import dev.ryanhcode.sable.Sable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ZapperItem.class})
public class ZapperItemMixin {
    @Inject(method={"use"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/equipment/zapper/ShootableGadgetItemMethods;applyCooldown(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Ljava/util/function/Predicate;I)V")})
    private void sable$projectTargetPos(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, @Local LocalRef<BlockHitResult> raytrace) {
        BlockHitResult blockHitResult = (BlockHitResult)raytrace.get();
        raytrace.set((Object)new BlockHitResult(Sable.HELPER.projectOutOfSubLevel(world, blockHitResult.getLocation()), blockHitResult.getDirection(), blockHitResult.getBlockPos(), blockHitResult.isInside()));
    }
}
