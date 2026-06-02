/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.ryanhcode.sable.api.physics.force.ForceGroup
 *  dev.ryanhcode.sable.api.physics.force.ForceGroups
 *  dev.ryanhcode.sable.api.physics.force.QueuedForceGroup$PointForce
 *  javax.annotation.Nullable
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarratedElementType
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentContents
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 */
package dev.simulated_team.simulated.content.entities.diagram.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.simulated_team.simulated.content.entities.diagram.screen.DiagramScreen;
import dev.simulated_team.simulated.index.SimGUITextures;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.network.packets.contraption_diagram.DiagramDataPacket;
import java.util.List;
import javax.annotation.Nullable;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class DiagramForceGroupToggle
extends AbstractWidget {
    private final ResourceLocation groupId;
    private final ForceGroup group;
    private final DiagramScreen diagramScreen;
    private int forceCount;

    public DiagramForceGroupToggle(DiagramScreen diagramScreen, ForceGroup forceGroup, int x, int y) {
        super(x, y, 90, 10, forceGroup.name());
        this.groupId = ForceGroups.REGISTRY.getKey((Object)forceGroup);
        this.diagramScreen = diagramScreen;
        this.group = forceGroup;
    }

    private boolean isEnabled() {
        return this.diagramScreen.config.enabledForceGroups().contains(this.groupId);
    }

    private void toggleActive() {
        if (this.isEnabled()) {
            this.diagramScreen.config.enabledForceGroups().remove(this.groupId);
        } else {
            this.diagramScreen.config.enabledForceGroups().add(this.groupId);
        }
        this.diagramScreen.setConfigDirty();
    }

    public void updateForceState(@Nullable DiagramDataPacket serverData) {
        if (serverData == null) {
            this.forceCount = 0;
            return;
        }
        List<QueuedForceGroup.PointForce> forces = serverData.forces().get(this.group);
        this.forceCount = forces != null ? forces.size() : 0;
    }

    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.toggleActive();
    }

    public void renderTab(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        boolean isEnabled = this.isEnabled();
        int groupColor = this.isEnabled() ? 0xFF000000 | this.group.color() : -5592406;
        PoseStack ps = guiGraphics.pose();
        ps.pushPose();
        float paperOffset = this.diagramScreen.getPaperOffset(partialTicks);
        ps.translate((float)DiagramScreen.MAX_PAPER_OFFSET - paperOffset, 1.0f, 0.0f);
        float tabHide = 1.0f - this.diagramScreen.getTabOffset(partialTicks);
        tabHide *= 9.0f;
        if (!isEnabled) {
            tabHide = Math.max(tabHide, 3.0f);
        }
        ps.translate(tabHide, 0.0f, 0.0f);
        SimGUITextures.DIAGRAM_TAB.render(guiGraphics, this.getX() - 1, this.getY() - 1, new Color(groupColor));
        ps.popPose();
    }

    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        boolean isEnabled = this.isEnabled();
        int groupColor = 0xFF000000 | this.group.color();
        PoseStack ps = guiGraphics.pose();
        ps.pushPose();
        float paperOffset = this.diagramScreen.getPaperOffset(partialTicks);
        ps.translate((float)DiagramScreen.MAX_PAPER_OFFSET - paperOffset, 1.0f, 0.0f);
        ps.translate((double)(this.getX() + 18), (double)(this.getY() + 1), 0.0);
        ps.scale(0.75f, 0.75f, 0.0f);
        MutableComponent name = MutableComponent.create((ComponentContents)this.group.name().getContents());
        if (isEnabled) {
            guiGraphics.drawString(font, (Component)name, 1, 1, -1910333, false);
            guiGraphics.drawString(font, (Component)name, 0, 0, groupColor, false);
        } else {
            name.withStyle(ChatFormatting.STRIKETHROUGH);
            guiGraphics.drawString(font, (Component)name, 0, 0, -1431655766, false);
        }
        if (this.forceCount > 0) {
            String forceCountText = String.valueOf(this.forceCount);
            int x = 95 - font.width(forceCountText);
            if (isEnabled) {
                guiGraphics.drawString(font, forceCountText, x + 1, 1, -1910333, false);
                guiGraphics.drawString(font, forceCountText, x, 0, groupColor, false);
            } else {
                guiGraphics.drawString(font, forceCountText, x, 0, -1431655766, false);
            }
        }
        ps.popPose();
    }

    public void playDownSound(SoundManager handler) {
        if (this.isEnabled()) {
            handler.play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SimSoundEvents.DIAGRAM_ERASE.event(), (float)1.0f));
        } else {
            handler.play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SimSoundEvents.DIAGRAM_CHECKMARK.event(), (float)1.0f));
        }
    }

    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
    }
}
