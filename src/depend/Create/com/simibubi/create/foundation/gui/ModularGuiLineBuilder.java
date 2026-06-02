/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.network.chat.CommonComponents
 */
package com.simibubi.create.foundation.gui;

import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.gui.widget.TooltipArea;
import java.util.function.BiConsumer;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;

public class ModularGuiLineBuilder {
    private ModularGuiLine target;
    private Font font;
    private int x;
    private int y;

    public ModularGuiLineBuilder(Font font, ModularGuiLine target, int x, int y) {
        this.font = font;
        this.target = target;
        this.x = x;
        this.y = y;
    }

    public ModularGuiLineBuilder addScrollInput(int x, int width, BiConsumer<ScrollInput, Label> inputTransform, String dataKey) {
        ScrollInput input = new ScrollInput(x + this.x, this.y - 4, width, 18);
        this.addScrollInput(input, inputTransform, dataKey);
        return this;
    }

    public ModularGuiLineBuilder addSelectionScrollInput(int x, int width, BiConsumer<SelectionScrollInput, Label> inputTransform, String dataKey) {
        SelectionScrollInput input = new SelectionScrollInput(x + this.x, this.y - 4, width, 18);
        this.addScrollInput(input, inputTransform, dataKey);
        return this;
    }

    public ModularGuiLineBuilder customArea(int x, int width) {
        this.target.customBoxes.add((Couple<Integer>)Couple.create((Object)x, (Object)width));
        return this;
    }

    public ModularGuiLineBuilder speechBubble() {
        this.target.speechBubble = true;
        return this;
    }

    private <T extends ScrollInput> void addScrollInput(T input, BiConsumer<T, Label> inputTransform, String dataKey) {
        Label label = new Label(input.getX() + 5, this.y, CommonComponents.EMPTY);
        label.withShadow();
        inputTransform.accept(input, label);
        input.writingTo(label);
        this.target.add((Pair<AbstractWidget, String>)Pair.of((Object)((Object)label), (Object)"Dummy"));
        this.target.add((Pair<AbstractWidget, String>)Pair.of(input, (Object)dataKey));
    }

    public ModularGuiLineBuilder addIntegerTextInput(int x, int width, BiConsumer<EditBox, TooltipArea> inputTransform, String dataKey) {
        return this.addTextInput(x, width, inputTransform.andThen((editBox, $) -> editBox.setFilter(s -> {
            if (s.isEmpty()) {
                return true;
            }
            try {
                Integer.parseInt(s);
                return true;
            }
            catch (NumberFormatException e) {
                return false;
            }
        })), dataKey);
    }

    public ModularGuiLineBuilder addTextInput(int x, int width, BiConsumer<EditBox, TooltipArea> inputTransform, String dataKey) {
        EditBox input = new EditBox(this.font, x + this.x + 5, this.y, width - 9, 8, CommonComponents.EMPTY);
        input.setBordered(false);
        input.setTextColor(0xFFFFFF);
        input.setFocused(false);
        input.mouseClicked(0.0, 0.0, 0);
        TooltipArea tooltipArea = new TooltipArea(this.x + x, this.y - 4, width, 18);
        inputTransform.accept(input, tooltipArea);
        this.target.add((Pair<AbstractWidget, String>)Pair.of((Object)input, (Object)dataKey));
        this.target.add((Pair<AbstractWidget, String>)Pair.of((Object)((Object)tooltipArea), (Object)"Dummy"));
        return this;
    }
}
