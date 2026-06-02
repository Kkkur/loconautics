/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.item;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public record ItemDescription(ImmutableList<Component> lines, ImmutableList<Component> linesOnShift, ImmutableList<Component> linesOnCtrl) {
    private static final Map<Item, Supplier<String>> CUSTOM_TOOLTIP_KEYS = new IdentityHashMap<Item, Supplier<String>>();

    @Nullable
    public static ItemDescription create(Item item, FontHelper.Palette palette) {
        return ItemDescription.create(ItemDescription.getTooltipTranslationKey(item), palette);
    }

    @Nullable
    public static ItemDescription create(String translationKey, FontHelper.Palette palette) {
        if (!ItemDescription.canFillBuilder(translationKey + ".summary")) {
            return null;
        }
        Builder builder = new Builder(palette);
        ItemDescription.fillBuilder(builder, translationKey);
        return builder.build();
    }

    public static boolean canFillBuilder(String translationKey) {
        return I18n.exists((String)translationKey);
    }

    public static void fillBuilder(Builder builder, String translationKey) {
        int i;
        String summaryKey = translationKey + ".summary";
        if (I18n.exists((String)summaryKey)) {
            builder.addSummary(I18n.get((String)summaryKey, (Object[])new Object[0]));
        }
        for (i = 1; i < 100; ++i) {
            String conditionKey = translationKey + ".condition" + i;
            String behaviourKey = translationKey + ".behaviour" + i;
            if (!I18n.exists((String)conditionKey)) break;
            builder.addBehaviour(I18n.get((String)conditionKey, (Object[])new Object[0]), I18n.get((String)behaviourKey, (Object[])new Object[0]));
        }
        for (i = 1; i < 100; ++i) {
            String controlKey = translationKey + ".control" + i;
            String actionKey = translationKey + ".action" + i;
            if (!I18n.exists((String)controlKey)) break;
            builder.addAction(I18n.get((String)controlKey, (Object[])new Object[0]), I18n.get((String)actionKey, (Object[])new Object[0]));
        }
    }

    public static void useKey(Item item, Supplier<String> supplier) {
        CUSTOM_TOOLTIP_KEYS.put(item, supplier);
    }

    public static void useKey(ItemLike item, String string) {
        ItemDescription.useKey(item.asItem(), () -> string);
    }

    public static void referKey(ItemLike item, Supplier<? extends ItemLike> otherItem) {
        ItemDescription.useKey(item.asItem(), () -> ((ItemLike)otherItem.get()).asItem().getDescriptionId());
    }

    public static String getTooltipTranslationKey(Item item) {
        if (CUSTOM_TOOLTIP_KEYS.containsKey(item)) {
            return CUSTOM_TOOLTIP_KEYS.get(item).get() + ".tooltip";
        }
        return item.getDescriptionId() + ".tooltip";
    }

    public ImmutableList<Component> getCurrentLines() {
        if (Screen.hasShiftDown()) {
            return this.linesOnShift;
        }
        if (Screen.hasControlDown()) {
            return this.linesOnCtrl;
        }
        return this.lines;
    }

    public static class Builder {
        protected final FontHelper.Palette palette;
        protected final List<String> summary = new ArrayList<String>();
        protected final List<Pair<String, String>> behaviours = new ArrayList<Pair<String, String>>();
        protected final List<Pair<String, String>> actions = new ArrayList<Pair<String, String>>();

        public Builder(FontHelper.Palette palette) {
            this.palette = palette;
        }

        public Builder addSummary(String summaryLine) {
            this.summary.add(summaryLine);
            return this;
        }

        public Builder addBehaviour(String condition, String behaviour) {
            this.behaviours.add((Pair<String, String>)Pair.of((Object)condition, (Object)behaviour));
            return this;
        }

        public Builder addAction(String condition, String action) {
            this.actions.add((Pair<String, String>)Pair.of((Object)condition, (Object)action));
            return this;
        }

        public ItemDescription build() {
            boolean bl;
            String condition;
            ArrayList lines = new ArrayList();
            ArrayList<Object> linesOnShift = new ArrayList<Object>();
            ArrayList<Object> linesOnCtrl = new ArrayList<Object>();
            for (String string : this.summary) {
                linesOnShift.addAll(TooltipHelper.cutStringTextComponent(string, this.palette));
            }
            if (!this.behaviours.isEmpty()) {
                linesOnShift.add(CommonComponents.EMPTY);
            }
            for (Pair pair : this.behaviours) {
                condition = (String)pair.getLeft();
                String behaviour = (String)pair.getRight();
                linesOnShift.add(Component.literal((String)condition).withStyle(ChatFormatting.GRAY));
                linesOnShift.addAll(TooltipHelper.cutStringTextComponent(behaviour, this.palette.primary(), this.palette.highlight(), 1));
            }
            for (Pair pair : this.actions) {
                condition = (String)pair.getLeft();
                String action = (String)pair.getRight();
                linesOnCtrl.add(Component.literal((String)condition).withStyle(ChatFormatting.GRAY));
                linesOnCtrl.addAll(TooltipHelper.cutStringTextComponent(action, this.palette.primary(), this.palette.highlight(), 1));
            }
            boolean hasDescription = !linesOnShift.isEmpty();
            boolean bl2 = bl = !linesOnCtrl.isEmpty();
            if (hasDescription || bl) {
                String[] holdDesc = CreateLang.translateDirect("tooltip.holdForDescription", "$").getString().split("\\$");
                String[] holdCtrl = CreateLang.translateDirect("tooltip.holdForControls", "$").getString().split("\\$");
                MutableComponent keyShift = CreateLang.translateDirect("tooltip.keyShift", new Object[0]);
                MutableComponent keyCtrl = CreateLang.translateDirect("tooltip.keyCtrl", new Object[0]);
                for (List list : Arrays.asList(lines, linesOnShift, linesOnCtrl)) {
                    MutableComponent tabBuilder;
                    boolean ctrl;
                    boolean shift = list == linesOnShift;
                    boolean bl3 = ctrl = list == linesOnCtrl;
                    if (holdDesc.length != 2 || holdCtrl.length != 2) {
                        list.add(0, Component.literal((String)"Invalid lang formatting!"));
                        continue;
                    }
                    if (bl) {
                        tabBuilder = Component.empty();
                        tabBuilder.append((Component)Component.literal((String)holdCtrl[0]).withStyle(ChatFormatting.DARK_GRAY));
                        tabBuilder.append((Component)keyCtrl.plainCopy().withStyle(ctrl ? ChatFormatting.WHITE : ChatFormatting.GRAY));
                        tabBuilder.append((Component)Component.literal((String)holdCtrl[1]).withStyle(ChatFormatting.DARK_GRAY));
                        list.add(0, tabBuilder);
                    }
                    if (hasDescription) {
                        tabBuilder = Component.empty();
                        tabBuilder.append((Component)Component.literal((String)holdDesc[0]).withStyle(ChatFormatting.DARK_GRAY));
                        tabBuilder.append((Component)keyShift.plainCopy().withStyle(shift ? ChatFormatting.WHITE : ChatFormatting.GRAY));
                        tabBuilder.append((Component)Component.literal((String)holdDesc[1]).withStyle(ChatFormatting.DARK_GRAY));
                        list.add(0, tabBuilder);
                    }
                    if (!shift && !ctrl) continue;
                    list.add(hasDescription && bl ? 2 : 1, CommonComponents.EMPTY);
                }
            }
            if (!hasDescription) {
                linesOnCtrl.clear();
                linesOnShift.addAll(lines);
            }
            if (!bl) {
                linesOnCtrl.clear();
                linesOnCtrl.addAll(lines);
            }
            return new ItemDescription((ImmutableList<Component>)ImmutableList.copyOf(lines), (ImmutableList<Component>)ImmutableList.copyOf(linesOnShift), (ImmutableList<Component>)ImmutableList.copyOf(linesOnCtrl));
        }
    }

    public static class Modifier
    implements TooltipModifier {
        protected final Item item;
        protected final FontHelper.Palette palette;
        protected String cachedLanguage;
        protected ItemDescription description;

        public Modifier(Item item, FontHelper.Palette palette) {
            this.item = item;
            this.palette = palette;
        }

        @Override
        public void modify(ItemTooltipEvent context) {
            if (this.checkLocale()) {
                this.description = ItemDescription.create(this.item, this.palette);
            }
            if (this.description == null) {
                return;
            }
            context.getToolTip().addAll(1, this.description.getCurrentLines());
        }

        protected boolean checkLocale() {
            String currentLanguage = Minecraft.getInstance().getLanguageManager().getSelected();
            if (!currentLanguage.equals(this.cachedLanguage)) {
                this.cachedLanguage = currentLanguage;
                return true;
            }
            return false;
        }
    }
}
