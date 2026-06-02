/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.kinetics.transmission.sequencer;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.computercraft.ComputerScreen;
import com.simibubi.create.content.kinetics.transmission.sequencer.ConfigureSequencedGearshiftPacket;
import com.simibubi.create.content.kinetics.transmission.sequencer.Instruction;
import com.simibubi.create.content.kinetics.transmission.sequencer.InstructionSpeedModifiers;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.Vector;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public class SequencedGearshiftScreen
extends AbstractSimiScreen {
    private final ItemStack renderedItem = AllBlocks.SEQUENCED_GEARSHIFT.asStack();
    private final AllGuiTextures background = AllGuiTextures.SEQUENCER;
    private IconButton confirmButton;
    private SequencedGearshiftBlockEntity be;
    private Vector<Instruction> instructions;
    private Vector<Vector<ScrollInput>> inputs;

    public SequencedGearshiftScreen(SequencedGearshiftBlockEntity be) {
        super((Component)CreateLang.translateDirect("gui.sequenced_gearshift.title", new Object[0]));
        this.instructions = be.instructions;
        this.be = be;
    }

    protected void init() {
        int row;
        if (this.be.computerBehaviour.hasAttachedComputer()) {
            this.minecraft.setScreen((Screen)new ComputerScreen(this.title, this::renderAdditional, (Screen)this, this.be.computerBehaviour::hasAttachedComputer));
        }
        this.setWindowSize(this.background.getWidth(), this.background.getHeight());
        this.setWindowOffset(-20, 0);
        super.init();
        int x = this.guiLeft;
        int y = this.guiTop;
        this.inputs = new Vector(5);
        for (row = 0; row < this.inputs.capacity(); ++row) {
            this.inputs.add(new Vector(3));
        }
        for (row = 0; row < this.instructions.size(); ++row) {
            this.initInputsOfRow(row, x, y);
        }
        this.confirmButton = new IconButton(x + this.background.getWidth() - 33, y + this.background.getHeight() - 24, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> this.onClose());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
    }

    public void initInputsOfRow(int row, int backgroundX, int backgroundY) {
        int x = backgroundX + 30;
        int y = backgroundY + 20;
        int rowHeight = 22;
        Vector<ScrollInput> rowInputs = this.inputs.get(row);
        this.removeWidgets(rowInputs);
        rowInputs.clear();
        int index = row;
        Instruction instruction = this.instructions.get(row);
        ScrollInput type = new SelectionScrollInput(x, y + rowHeight * row, 50, 18).forOptions(SequencerInstructions.getOptions()).calling(state -> this.instructionUpdated(index, (int)state)).setState(instruction.instruction.ordinal()).titled(CreateLang.translateDirect("gui.sequenced_gearshift.instruction", new Object[0]));
        ScrollInput value = new ScrollInput(x + 58, y + rowHeight * row, 28, 18).calling(state -> {
            instruction.value = state;
        });
        ScrollInput direction = new SelectionScrollInput(x + 88, y + rowHeight * row, 28, 18).forOptions(InstructionSpeedModifiers.getOptions()).calling(state -> {
            instruction.speedModifier = InstructionSpeedModifiers.values()[state];
        }).titled(CreateLang.translateDirect("gui.sequenced_gearshift.speed", new Object[0]));
        rowInputs.add(type);
        rowInputs.add(value);
        rowInputs.add(direction);
        this.addRenderableWidgets(rowInputs);
        this.updateParamsOfRow(row);
    }

    public void updateParamsOfRow(int row) {
        Instruction instruction = this.instructions.get(row);
        Vector<ScrollInput> rowInputs = this.inputs.get(row);
        SequencerInstructions def = instruction.instruction;
        boolean hasValue = def.hasValueParameter;
        boolean hasModifier = def.hasSpeedParameter;
        ScrollInput value = rowInputs.get(1);
        value.active = value.visible = hasValue;
        if (hasValue) {
            value.withRange(1, def.maxValue + 1).titled(CreateLang.translateDirect(def.parameterKey, new Object[0])).withShiftStep(def.shiftStep).setState(instruction.value).onChanged();
        }
        if (def == SequencerInstructions.DELAY) {
            value.withStepFunction(context -> {
                int v = context.currentValue;
                if (!context.forward) {
                    --v;
                }
                if (v < 20) {
                    return context.shift ? 20 : 1;
                }
                return context.shift ? 100 : 20;
            });
        } else {
            value.withStepFunction(value.standardStep());
        }
        ScrollInput modifier = rowInputs.get(2);
        modifier.active = modifier.visible = hasModifier;
        if (hasModifier) {
            modifier.setState(instruction.speedModifier.ordinal());
        }
    }

    public void tick() {
        super.tick();
        if (this.be.computerBehaviour.hasAttachedComputer()) {
            this.minecraft.setScreen((Screen)new ComputerScreen(this.title, this::renderAdditional, (Screen)this, this.be.computerBehaviour::hasAttachedComputer));
        }
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int yOffset;
        AllGuiTextures toDraw;
        int row;
        int x = this.guiLeft;
        int y = this.guiTop;
        this.background.render(graphics, x, y);
        for (row = 0; row < this.instructions.capacity(); ++row) {
            toDraw = AllGuiTextures.SEQUENCER_EMPTY;
            yOffset = toDraw.getHeight() * row;
            toDraw.render(graphics, x, y + 16 + yOffset);
        }
        for (row = 0; row < this.instructions.capacity(); ++row) {
            toDraw = AllGuiTextures.SEQUENCER_EMPTY;
            yOffset = toDraw.getHeight() * row;
            if (row >= this.instructions.size()) {
                toDraw.render(graphics, x, y + 16 + yOffset);
                continue;
            }
            Instruction instruction = this.instructions.get(row);
            SequencerInstructions def = instruction.instruction;
            def.background.render(graphics, x, y + 16 + yOffset);
            this.label(graphics, 36, yOffset - 1, (Component)CreateLang.translateDirect(def.translationKey, new Object[0]));
            if (def.hasValueParameter) {
                String text = def.formatValue(instruction.value);
                int stringWidth = this.font.width(text);
                this.label(graphics, 90 + (12 - stringWidth / 2), yOffset - 1, (Component)Component.literal((String)text));
            }
            if (!def.hasSpeedParameter) continue;
            this.label(graphics, 127, yOffset - 1, instruction.speedModifier.label);
        }
        graphics.drawString(this.font, this.title, x + (this.background.getWidth() - 8) / 2 - this.font.width((FormattedText)this.title) / 2, y + 4, 5841956, false);
        this.renderAdditional(graphics, mouseX, mouseY, partialTicks, x, y, this.background);
    }

    private void renderAdditional(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, int guiLeft, int guiTop, AllGuiTextures background) {
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)this.renderedItem).at((float)(guiLeft + background.getWidth() + 6), (float)(guiTop + background.getHeight() - 56), 100.0f)).scale(5.0).render(graphics);
    }

    private void label(GuiGraphics graphics, int x, int y, Component text) {
        graphics.drawString(this.font, text, this.guiLeft + x, this.guiTop + 26 + y, 0xFFFFEE);
    }

    public void sendPacket() {
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ConfigureSequencedGearshiftPacket(this.be.getBlockPos(), this.instructions));
    }

    public void removed() {
        this.sendPacket();
    }

    private void instructionUpdated(int index, int state) {
        SequencerInstructions newValue;
        this.instructions.get((int)index).instruction = newValue = SequencerInstructions.values()[state];
        this.instructions.get((int)index).value = newValue.defaultValue;
        this.updateParamsOfRow(index);
        if (newValue == SequencerInstructions.END) {
            for (int i = this.instructions.size() - 1; i > index; --i) {
                this.instructions.remove(i);
                Vector<ScrollInput> rowInputs = this.inputs.get(i);
                this.removeWidgets(rowInputs);
                rowInputs.clear();
            }
        } else if (index + 1 < this.instructions.capacity() && index + 1 == this.instructions.size()) {
            this.instructions.add(new Instruction(SequencerInstructions.END));
            this.initInputsOfRow(index + 1, this.guiLeft, this.guiTop);
        }
    }
}
