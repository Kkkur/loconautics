/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.kinetics.base.IRotate$StressImpact
 *  com.simibubi.create.foundation.item.KineticStats
 *  com.simibubi.create.foundation.item.TooltipHelper
 *  net.createmod.catnip.lang.LangBuilder
 *  net.minecraft.world.level.block.Block
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.simulated_team.simulated.mixin.dynamic_stress;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import dev.simulated_team.simulated.api.CustomStressImpactTooltipProvider;
import dev.simulated_team.simulated.data.SimLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={KineticStats.class})
public class KineticStatsMixin {
    @WrapOperation(method={"getKineticStats"}, at={@At(value="INVOKE", target="Lnet/createmod/catnip/lang/LangBuilder;add(Lnet/createmod/catnip/lang/LangBuilder;)Lnet/createmod/catnip/lang/LangBuilder;", ordinal=0)}, remap=false)
    private static LangBuilder aeronautics$getKinetidStats(LangBuilder instance, LangBuilder otherBuilder, Operation<LangBuilder> original, @Local(argsOnly=true) Block block, @Local(name={"impactId"}) IRotate.StressImpact impactId) {
        if (block instanceof CustomStressImpactTooltipProvider) {
            CustomStressImpactTooltipProvider impact = (CustomStressImpactTooltipProvider)block;
            return instance.add(SimLang.text(TooltipHelper.makeProgressBar((int)impact.getBarLength(), (int)impact.getFilledBarLength()))).style(impactId.getAbsoluteColor());
        }
        return instance.add(otherBuilder);
    }

    @WrapOperation(method={"getKineticStats"}, at={@At(value="INVOKE", target="Lnet/createmod/catnip/lang/LangBuilder;add(Lnet/createmod/catnip/lang/LangBuilder;)Lnet/createmod/catnip/lang/LangBuilder;", ordinal=2)}, remap=false)
    private static LangBuilder aeronautics$getKinetidStats2(LangBuilder instance, LangBuilder otherBuilder, Operation<LangBuilder> original, @Local(argsOnly=true) Block block) {
        if (block instanceof CustomStressImpactTooltipProvider) {
            CustomStressImpactTooltipProvider impact = (CustomStressImpactTooltipProvider)block;
            return instance.add(otherBuilder.text(" x ")).add(impact.getCustomImpactLang());
        }
        return instance.add(otherBuilder);
    }

    @WrapOperation(method={"getKineticStats"}, at={@At(value="INVOKE", target="Lnet/createmod/catnip/lang/LangBuilder;translate(Ljava/lang/String;[Ljava/lang/Object;)Lnet/createmod/catnip/lang/LangBuilder;", ordinal=0)}, remap=false)
    private static LangBuilder aeronautics$getKinetidStats4(LangBuilder instance, String langKey, Object[] args, Operation<LangBuilder> original, @Local(argsOnly=true) Block block, @Local(name={"impactId"}) IRotate.StressImpact impactId) {
        if (block instanceof CustomStressImpactTooltipProvider) {
            return SimLang.space().translate("tooltip.dynamic_stress_impact", new Object[0]).style(impactId.getAbsoluteColor());
        }
        return instance.translate(langKey, args);
    }
}
