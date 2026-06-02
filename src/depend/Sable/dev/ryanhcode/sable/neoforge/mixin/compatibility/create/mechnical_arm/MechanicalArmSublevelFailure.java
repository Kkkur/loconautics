/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointHandler
 *  net.createmod.catnip.lang.LangBuilder
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.mechnical_arm;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointHandler;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ArmInteractionPointHandler.class})
public class MechanicalArmSublevelFailure {
    @Inject(method={"flushSettings"}, at={@At(value="HEAD")})
    private static void sable$gatherSublevelInformation(BlockPos pos, CallbackInfo ci, @Share(value="parentSublevel") LocalRef<SubLevel> parentSublevel, @Share(value="pointsRemovedSublevel") LocalRef<Integer> pointsRemovedSublevel) {
        parentSublevel.set((Object)Sable.HELPER.getContainingClient((Vec3i)pos));
        pointsRemovedSublevel.set((Object)0);
    }

    @Inject(method={"flushSettings"}, at={@At(value="INVOKE", target="Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z")})
    private static void sable$removeDifferentSublevelPoints(BlockPos pos, CallbackInfo ci, @Local(name={"point"}) ArmInteractionPoint point, @Share(value="pointsRemovedSublevel") LocalRef<Integer> pointsRemovedSublevel, @Share(value="parentSublevel") LocalRef<SubLevel> parentSublevel) {
        ClientSubLevel pointsublevel = Sable.HELPER.getContainingClient((Vec3i)point.getPos());
        if (parentSublevel.get() != pointsublevel) {
            pointsRemovedSublevel.set((Object)((Integer)pointsRemovedSublevel.get() + 1));
        }
    }

    @Redirect(method={"flushSettings"}, at=@At(value="INVOKE", target="Lnet/createmod/catnip/lang/LangBuilder;translate(Ljava/lang/String;[Ljava/lang/Object;)Lnet/createmod/catnip/lang/LangBuilder;"))
    private static LangBuilder sable$relayRemovedPoints(LangBuilder instance, String langKey, Object[] args, @Local(name={"removed"}) int removed, @Share(value="pointsRemovedSublevel") LocalRef<Integer> pointsRemovedSublevel) {
        Integer arg = (Integer)args[0];
        MutableComponent errorComponent = Component.empty();
        if ((Integer)pointsRemovedSublevel.get() == 0) {
            instance.translate(langKey, args);
        } else {
            errorComponent = arg - (Integer)pointsRemovedSublevel.get() == 0 ? Component.translatable((String)"sable.create.remove.points_removed_sublevel", (Object[])new Object[]{removed}).withStyle(ChatFormatting.RED) : Component.translatable((String)"sable.create.mechanical_arm.points_removed_sublevel_and_range", (Object[])new Object[]{removed}).withStyle(ChatFormatting.RED);
        }
        instance.add((Component)errorComponent);
        return instance;
    }
}
