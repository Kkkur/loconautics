/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.schematics.client.SchematicTransformation
 *  com.simibubi.create.content.schematics.client.tools.SchematicToolBase
 *  com.simibubi.create.foundation.utility.RaycastHelper
 *  com.simibubi.create.foundation.utility.RaycastHelper$PredicateTraceResult
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.schematics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.schematics.client.SchematicTransformation;
import com.simibubi.create.content.schematics.client.tools.SchematicToolBase;
import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import java.util.function.Predicate;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SchematicToolBase.class})
public class SchematicToolBaseMixin {
    @Shadow
    protected Vec3 chasingSelectedPos;
    @Shadow
    protected Vec3 lastChasingSelectedPos;

    @Inject(method={"updateSelection"}, at={@At(value="TAIL")})
    public void sable$forceUpdateSelection(CallbackInfo ci, @Local(ordinal=0) Vec3 target) {
        ActiveSableCompanion helper = Sable.HELPER;
        if (helper.getContainingClient((Position)target) != helper.getContainingClient((Position)this.lastChasingSelectedPos)) {
            this.lastChasingSelectedPos = this.chasingSelectedPos = target;
        }
    }

    @WrapOperation(method={"updateTargetPos"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/foundation/utility/RaycastHelper;rayTraceUntil(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Ljava/util/function/Predicate;)Lcom/simibubi/create/foundation/utility/RaycastHelper$PredicateTraceResult;")})
    public RaycastHelper.PredicateTraceResult sable$rayTraceSublevels(Vec3 start, Vec3 end, Predicate<BlockPos> predicate, Operation<RaycastHelper.PredicateTraceResult> original, @Local LocalPlayer player, @Local SchematicTransformation transformation) {
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Vec3i)transformation.getAnchor());
        if (subLevel != null) {
            Pose3dc pose = subLevel.renderPose();
            Vec3 plotPlayerPos = pose.transformPositionInverse(player.getEyePosition());
            Vec3 plotStart = transformation.toLocalSpace(plotPlayerPos);
            Vec3 plotEnd = transformation.toLocalSpace(RaycastHelper.getTraceTarget((Player)player, (double)70.0, (Vec3)plotPlayerPos));
            return (RaycastHelper.PredicateTraceResult)original.call(new Object[]{plotStart, plotEnd, predicate});
        }
        return (RaycastHelper.PredicateTraceResult)original.call(new Object[]{start, end, predicate});
    }
}
