/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.simulated_team.simulated.mixin.extra_kinetics.goggle_tooltips;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={KineticBlockEntity.class})
public class KineticBlockEntityMixin {
    @Inject(method={"addToGoggleTooltip"}, at={@At(value="RETURN")}, cancellable=true)
    public void addExtraKineticsInfo(List<Component> tooltip, boolean isPlayerSneaking, CallbackInfoReturnable<Boolean> cir) {
        ExtraKinetics ek;
        KineticBlockEntity extraKinetics;
        KineticBlockEntityMixin kineticBlockEntityMixin = this;
        if (kineticBlockEntityMixin instanceof ExtraKinetics && (extraKinetics = (ek = (ExtraKinetics)((Object)kineticBlockEntityMixin)).getExtraKinetics()) instanceof ExtraKinetics.ExtraKineticsBlockEntity) {
            ExtraKinetics.ExtraKineticsBlockEntity ekb = (ExtraKinetics.ExtraKineticsBlockEntity)extraKinetics;
            ArrayList extraKineticsTooltips = new ArrayList();
            boolean applied = extraKinetics.addToGoggleTooltip(extraKineticsTooltips, isPlayerSneaking);
            if (applied) {
                if (((Boolean)cir.getReturnValue()).booleanValue()) {
                    tooltip.add((Component)Component.empty());
                }
                SimLang.translate("extra_kinetics.information", new Object[0]).text(": ").style(ChatFormatting.WHITE).add(SimLang.builder().add(ekb.getKey()).style(ChatFormatting.AQUA)).forGoggles(tooltip);
                tooltip.addAll(extraKineticsTooltips);
                cir.setReturnValue((Object)true);
            }
        }
    }
}
