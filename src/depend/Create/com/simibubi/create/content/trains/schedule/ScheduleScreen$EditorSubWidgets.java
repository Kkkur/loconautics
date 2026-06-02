/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.nbt.CompoundTag
 */
package com.simibubi.create.content.trains.schedule;

import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.widget.ScreenOverlay;
import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.nbt.CompoundTag;

protected static final class ScheduleScreen.EditorSubWidgets
extends ScreenOverlay {
    private final ModularGuiLine line = new ModularGuiLine();

    protected ScheduleScreen.EditorSubWidgets() {
        super(200);
    }

    protected void save(CompoundTag data) {
        this.line.saveValues(data);
    }

    protected void load(CompoundTag data) {
        this.line.loadValues(data, x$0 -> this.add((GuiEventListener)x$0), x$0 -> {
            GuiEventListener cfr_ignored_0 = (GuiEventListener)this.addRenderableOnly((Renderable)x$0);
        });
    }

    protected void forEach(Consumer<GuiEventListener> consumer) {
        this.line.forEach(consumer);
    }

    protected void reset() {
        this.line.forEach(this::remove);
        this.line.clear();
    }

    @Override
    public void clear() {
        super.clear();
        this.line.clear();
    }

    protected ModularGuiLineBuilder newLineBuilder(Font font, int x, int y) {
        return new ModularGuiLineBuilder(font, this.line, x, y);
    }

    protected void renderBg(int guiLeft, GuiGraphics graphics) {
        this.line.renderWidgetBG(guiLeft, graphics);
    }
}
