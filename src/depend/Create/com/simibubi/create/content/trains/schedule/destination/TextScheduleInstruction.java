/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.network.chat.Component
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.schedule.destination;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class TextScheduleInstruction
extends ScheduleInstruction {
    protected String getLabelText() {
        return this.textData("Text");
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of((Object)CreateLang.translateDirect("schedule." + type + "." + this.getId().getPath() + ".summary", new Object[0]).withStyle(ChatFormatting.GOLD), (Object)CreateLang.translateDirect("generic.in_quotes", Component.literal((String)this.getLabelText())));
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addTextInput(0, 121, (e, t) -> this.modifyEditBox((EditBox)e), "Text");
    }

    @OnlyIn(value=Dist.CLIENT)
    protected void modifyEditBox(EditBox box) {
    }
}
