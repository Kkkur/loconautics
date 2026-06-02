/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.logistics.filter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AttributeFilterScreen
extends AbstractFilterScreen<AttributeFilterMenu> {
    private static final String PREFIX = "gui.attribute_filter.";
    private Component addDESC = CreateLang.translateDirect("gui.attribute_filter.add_attribute", new Object[0]);
    private Component addInvertedDESC = CreateLang.translateDirect("gui.attribute_filter.add_inverted_attribute", new Object[0]);
    private Component allowDisN = CreateLang.translateDirect("gui.attribute_filter.allow_list_disjunctive", new Object[0]);
    private Component allowDisDESC = CreateLang.translateDirect("gui.attribute_filter.allow_list_disjunctive.description", new Object[0]);
    private Component allowConN = CreateLang.translateDirect("gui.attribute_filter.allow_list_conjunctive", new Object[0]);
    private Component allowConDESC = CreateLang.translateDirect("gui.attribute_filter.allow_list_conjunctive.description", new Object[0]);
    private Component denyN = CreateLang.translateDirect("gui.attribute_filter.deny_list", new Object[0]);
    private Component denyDESC = CreateLang.translateDirect("gui.attribute_filter.deny_list.description", new Object[0]);
    private Component referenceH = CreateLang.translateDirect("gui.attribute_filter.add_reference_item", new Object[0]);
    private Component noSelectedT = CreateLang.translateDirect("gui.attribute_filter.no_selected_attributes", new Object[0]);
    private Component selectedT = CreateLang.translateDirect("gui.attribute_filter.selected_attributes", new Object[0]);
    private IconButton whitelistDis;
    private IconButton whitelistCon;
    private IconButton blacklist;
    private IconButton add;
    private IconButton addInverted;
    private ItemStack lastItemScanned = ItemStack.EMPTY;
    private List<ItemAttribute> attributesOfItem = new ArrayList<ItemAttribute>();
    private List<Component> selectedAttributes = new ArrayList<Component>();
    private SelectionScrollInput attributeSelector;
    private Label attributeSelectorLabel;

    public AttributeFilterScreen(AttributeFilterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, AllGuiTextures.ATTRIBUTE_FILTER);
    }

    @Override
    protected void init() {
        this.setWindowOffset(-11, 7);
        super.init();
        int x = this.leftPos;
        int y = this.topPos;
        this.whitelistDis = new IconButton(x + 38, y + 61, AllIcons.I_WHITELIST_OR);
        this.whitelistDis.withCallback(() -> {
            ((AttributeFilterMenu)this.menu).whitelistMode = AttributeFilterWhitelistMode.WHITELIST_DISJ;
            this.sendOptionUpdate(FilterScreenPacket.Option.WHITELIST);
        });
        this.whitelistDis.setToolTip(this.allowDisN);
        this.whitelistCon = new IconButton(x + 56, y + 61, AllIcons.I_WHITELIST_AND);
        this.whitelistCon.withCallback(() -> {
            ((AttributeFilterMenu)this.menu).whitelistMode = AttributeFilterWhitelistMode.WHITELIST_CONJ;
            this.sendOptionUpdate(FilterScreenPacket.Option.WHITELIST2);
        });
        this.whitelistCon.setToolTip(this.allowConN);
        this.blacklist = new IconButton(x + 74, y + 61, AllIcons.I_WHITELIST_NOT);
        this.blacklist.withCallback(() -> {
            ((AttributeFilterMenu)this.menu).whitelistMode = AttributeFilterWhitelistMode.BLACKLIST;
            this.sendOptionUpdate(FilterScreenPacket.Option.BLACKLIST);
        });
        this.blacklist.setToolTip(this.denyN);
        this.addRenderableWidgets((GuiEventListener[])new IconButton[]{this.blacklist, this.whitelistCon, this.whitelistDis});
        this.add = new IconButton(x + 182, y + 26, AllIcons.I_ADD);
        this.addRenderableWidget((GuiEventListener)this.add);
        this.addInverted = new IconButton(x + 200, y + 26, AllIcons.I_ADD_INVERTED_ATTRIBUTE);
        this.addRenderableWidget((GuiEventListener)this.addInverted);
        this.add.withCallback(() -> this.handleAddedAttibute(false));
        this.add.setToolTip(this.addDESC);
        this.addInverted.withCallback(() -> this.handleAddedAttibute(true));
        this.addInverted.setToolTip(this.addInvertedDESC);
        this.handleIndicators();
        this.attributeSelectorLabel = new Label(x + 43, y + 31, CommonComponents.EMPTY).colored(15985630).withShadow();
        this.attributeSelector = new SelectionScrollInput(x + 39, y + 26, 137, 18);
        this.attributeSelector.forOptions(Arrays.asList(CommonComponents.EMPTY));
        this.attributeSelector.removeCallback();
        this.referenceItemChanged(((AttributeFilterMenu)this.menu).ghostInventory.getStackInSlot(0));
        this.addRenderableWidget((GuiEventListener)this.attributeSelector);
        this.addRenderableWidget((GuiEventListener)this.attributeSelectorLabel);
        this.selectedAttributes.clear();
        this.selectedAttributes.add((Component)(((AttributeFilterMenu)this.menu).selectedAttributes.isEmpty() ? this.noSelectedT : this.selectedT).plainCopy().withStyle(ChatFormatting.YELLOW));
        ((AttributeFilterMenu)this.menu).selectedAttributes.forEach(at -> this.selectedAttributes.add((Component)Component.literal((String)"- ").append((Component)at.attribute().format(at.inverted())).withStyle(ChatFormatting.GRAY)));
    }

    private void referenceItemChanged(ItemStack stack) {
        RegistryAccess registries = Minecraft.getInstance().level.registryAccess();
        this.lastItemScanned = stack;
        if (stack.isEmpty()) {
            this.attributeSelector.active = false;
            this.attributeSelector.visible = false;
            this.attributeSelectorLabel.text = this.referenceH.plainCopy().withStyle(ChatFormatting.ITALIC);
            this.add.active = false;
            this.addInverted.active = false;
            this.attributeSelector.calling(s -> {});
            return;
        }
        this.add.active = true;
        this.addInverted.active = true;
        this.attributeSelector.titled(CreateLang.text(stack.getHoverName().getString() + "...").color(ScrollInput.HEADER_RGB.getRGB()).component());
        this.attributesOfItem.clear();
        for (ItemAttributeType type : CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE) {
            this.attributesOfItem.addAll(type.getAllAttributes(stack, (Level)this.minecraft.level));
        }
        List options = this.attributesOfItem.stream().map(a -> a.format(false)).collect(Collectors.toList());
        this.attributeSelector.forOptions(options);
        this.attributeSelector.active = true;
        this.attributeSelector.visible = true;
        this.attributeSelector.setState(0);
        this.attributeSelector.calling(arg_0 -> this.lambda$referenceItemChanged$8(options, (HolderLookup.Provider)registries, arg_0));
        this.attributeSelector.onChanged();
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        ItemStack stack = ((AttributeFilterMenu)this.menu).ghostInventory.getStackInSlot(1);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(0.0f, 0.0f, 150.0f);
        graphics.renderItemDecorations(this.font, stack, this.leftPos + 16, this.topPos + 62, String.valueOf(this.selectedAttributes.size() - 1));
        matrixStack.popPose();
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        ItemStack stackInSlot = ((AttributeFilterMenu)this.menu).ghostInventory.getStackInSlot(0);
        if (!ItemStack.matches((ItemStack)stackInSlot, (ItemStack)this.lastItemScanned)) {
            this.referenceItemChanged(stackInSlot);
        }
    }

    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (((AttributeFilterMenu)this.menu).getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.hoveredSlot.index == 37) {
                graphics.renderComponentTooltip(this.font, this.selectedAttributes, mouseX, mouseY);
                return;
            }
            graphics.renderTooltip(this.font, this.hoveredSlot.getItem(), mouseX, mouseY);
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected List<IconButton> getTooltipButtons() {
        return Arrays.asList(this.blacklist, this.whitelistCon, this.whitelistDis);
    }

    @Override
    protected List<MutableComponent> getTooltipDescriptions() {
        return Arrays.asList(this.denyDESC.plainCopy(), this.allowConDESC.plainCopy(), this.allowDisDESC.plainCopy());
    }

    protected boolean handleAddedAttibute(boolean inverted) {
        int index = this.attributeSelector.getState();
        if (index >= this.attributesOfItem.size()) {
            return false;
        }
        this.add.active = false;
        this.addInverted.active = false;
        ItemAttribute itemAttribute = this.attributesOfItem.get(index);
        CompoundTag tag = ItemAttribute.saveStatic(itemAttribute, (HolderLookup.Provider)Minecraft.getInstance().level.registryAccess());
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new FilterScreenPacket(inverted ? FilterScreenPacket.Option.ADD_INVERTED_TAG : FilterScreenPacket.Option.ADD_TAG, tag));
        ((AttributeFilterMenu)this.menu).appendSelectedAttribute(itemAttribute, inverted);
        if (((AttributeFilterMenu)this.menu).selectedAttributes.size() == 1) {
            this.selectedAttributes.set(0, (Component)this.selectedT.plainCopy().withStyle(ChatFormatting.YELLOW));
        }
        this.selectedAttributes.add((Component)Component.literal((String)"- ").append((Component)itemAttribute.format(inverted)).withStyle(ChatFormatting.GRAY));
        return true;
    }

    @Override
    protected void contentsCleared() {
        this.selectedAttributes.clear();
        this.selectedAttributes.add((Component)this.noSelectedT.plainCopy().withStyle(ChatFormatting.YELLOW));
        if (!this.lastItemScanned.isEmpty()) {
            this.add.active = true;
            this.addInverted.active = true;
        }
    }

    @Override
    protected boolean isButtonEnabled(IconButton button) {
        if (button == this.blacklist) {
            return ((AttributeFilterMenu)this.menu).whitelistMode != AttributeFilterWhitelistMode.BLACKLIST;
        }
        if (button == this.whitelistCon) {
            return ((AttributeFilterMenu)this.menu).whitelistMode != AttributeFilterWhitelistMode.WHITELIST_CONJ;
        }
        if (button == this.whitelistDis) {
            return ((AttributeFilterMenu)this.menu).whitelistMode != AttributeFilterWhitelistMode.WHITELIST_DISJ;
        }
        return true;
    }

    private /* synthetic */ void lambda$referenceItemChanged$8(List options, HolderLookup.Provider registries, Integer i) {
        this.attributeSelectorLabel.setTextAndTrim((Component)options.get(i), true, 112);
        ItemAttribute selected = this.attributesOfItem.get(i);
        for (ItemAttribute.ItemAttributeEntry existing : ((AttributeFilterMenu)this.menu).selectedAttributes) {
            CompoundTag testTag2;
            CompoundTag testTag = ItemAttribute.saveStatic(existing.attribute(), registries);
            if (!testTag.equals((Object)(testTag2 = ItemAttribute.saveStatic(selected, registries)))) continue;
            this.add.active = false;
            this.addInverted.active = false;
            return;
        }
        this.add.active = true;
        this.addInverted.active = true;
    }
}
