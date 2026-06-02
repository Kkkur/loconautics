/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public interface IDisplayAssemblyExceptions {
    default public boolean addExceptionToTooltip(List<Component> tooltip) {
        AssemblyException e = this.getLastAssemblyException();
        if (e == null) {
            return false;
        }
        if (!tooltip.isEmpty()) {
            tooltip.add(CommonComponents.EMPTY);
        }
        CreateLang.translate("gui.assembly.exception", new Object[0]).style(ChatFormatting.GOLD).forGoggles(tooltip);
        String text = e.component.getString();
        Arrays.stream(text.split("\n")).forEach(l -> TooltipHelper.cutStringTextComponent(l, FontHelper.Palette.GRAY_AND_WHITE).forEach(c -> CreateLang.builder().add(c).forGoggles(tooltip)));
        return true;
    }

    public AssemblyException getLastAssemblyException();
}
