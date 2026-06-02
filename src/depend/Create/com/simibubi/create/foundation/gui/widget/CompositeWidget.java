/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.TickableGuiEventListener
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.AbstractContainerEventHandler
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  net.minecraft.client.gui.narration.NarratableEntry$NarrationPriority
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 */
package com.simibubi.create.foundation.gui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.gui.TickableGuiEventListener;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class CompositeWidget
extends AbstractContainerEventHandler
implements NarratableEntry,
Renderable,
TickableGuiEventListener {
    private final List<GuiEventListener> children = new ArrayList<GuiEventListener>();
    private final List<Renderable> renderables = new ArrayList<Renderable>();
    private GuiEventListener hovered;

    public <T extends GuiEventListener> T add(T child) {
        this.children.add(child);
        if (child instanceof Renderable) {
            Renderable renderable = (Renderable)child;
            this.renderables.add(renderable);
        }
        return child;
    }

    public <T extends Renderable> T addRenderableOnly(T renderable) {
        this.renderables.add(renderable);
        return renderable;
    }

    public <T extends GuiEventListener> boolean remove(T child) {
        boolean removed = this.children.remove(child);
        if (child instanceof Renderable) {
            removed |= this.renderables.remove(child);
        }
        return removed;
    }

    public <T extends Renderable> boolean removeRenderableOnly(T renderable) {
        return this.renderables.remove(renderable);
    }

    public void clear() {
        this.children.clear();
        this.renderables.clear();
    }

    public List<? extends GuiEventListener> children() {
        return Collections.unmodifiableList(this.children);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.getChildAt(mouseX, mouseY).ifPresent(hovered -> {
            this.hovered = hovered;
        });
        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.getFocused() instanceof NarratableEntry) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        }
        if (this.hovered instanceof NarratableEntry) {
            return NarratableEntry.NarrationPriority.HOVERED;
        }
        return NarratableEntry.NarrationPriority.NONE;
    }

    public void updateNarration(NarrationElementOutput output) {
        GuiEventListener guiEventListener = this.hovered;
        if (guiEventListener instanceof NarratableEntry) {
            NarratableEntry narratable = (NarratableEntry)guiEventListener;
            narratable.updateNarration(output);
        } else {
            guiEventListener = this.getFocused();
            if (guiEventListener instanceof NarratableEntry) {
                NarratableEntry narratable = (NarratableEntry)guiEventListener;
                narratable.updateNarration(output);
            }
        }
    }

    public void tick() {
        for (GuiEventListener child : this.children) {
            if (!(child instanceof TickableGuiEventListener)) continue;
            TickableGuiEventListener tickable = (TickableGuiEventListener)child;
            tickable.tick();
        }
    }

    public void mouseMoved(double mouseX, double mouseY) {
        for (GuiEventListener child : this.children) {
            child.mouseMoved(mouseX, mouseY);
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.getChildAt(mouseX, mouseY).isPresent();
    }
}
