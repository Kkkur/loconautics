/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  foundry.veil.api.client.color.Color
 *  foundry.veil.api.client.color.Colorc
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
 *  net.minecraft.client.renderer.texture.SpriteContents$Ticker
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package dev.simulated_team.simulated.registrate.simulated_tab;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.simulated_team.simulated.client.sections.SimulatedSection;
import dev.simulated_team.simulated.index.SimResourceManagers;
import dev.simulated_team.simulated.mixin.accessor.CreativeModeInventoryScreenAccessor;
import dev.simulated_team.simulated.mixin_interface.SpriteContentsExtension;
import dev.simulated_team.simulated.mixin_interface.TickerExtension;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import dev.simulated_team.simulated.registrate.simulated_tab.CreativeTabItemTransforms;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.color.Colorc;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SimulatedCreativeTab {
    public static int CURRENT_ROW = 0;
    public static final Object2IntOpenHashMap<ResourceLocation> SECTION_Y_VALUES = new Object2IntOpenHashMap();

    public static void renderBanners(CreativeModeInventoryScreen screen, GuiGraphics graphics, int mouseX, int mouseY) {
        PoseStack ps = graphics.pose();
        ps.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int left = ((CreativeModeInventoryScreenAccessor)screen).getLeftPos() + 8;
        int top = ((CreativeModeInventoryScreenAccessor)screen).getTopPos() + 17;
        ps.translate((float)left, (float)top, 0.0f);
        List<SimulatedSection> sections = SimResourceManagers.SIMULATED_SECTION.sortedEntries();
        for (SimulatedSection section : sections) {
            ResourceLocation id = SimResourceManagers.SIMULATED_SECTION.getId(section);
            int yValue = SECTION_Y_VALUES.getInt((Object)id);
            int sectionRow = yValue - CURRENT_ROW;
            if (sectionRow < 0 || sectionRow > 4) continue;
            Font font = Minecraft.getInstance().font;
            int x = 0;
            int y = sectionRow * 18;
            int w = 162;
            int h = 18;
            ResourceLocation bannerTexture = section.sprite();
            if (section.animateOnHover()) {
                boolean isHovering = mouseX >= left + x && mouseX <= left + x + w && mouseY >= top + y && mouseY <= top + y + h;
                SimulatedCreativeTab.setPlaying(bannerTexture, isHovering);
            }
            graphics.blitSprite(bannerTexture, x, y, w, h);
            Component text = section.title().text();
            int textWidth = font.width((FormattedText)text);
            Colorc background = section.title().background();
            graphics.fill(x + 2, y + 2, x + textWidth + 8, y + h - 2, background.argb());
            Colorc light = section.title().color();
            Colorc dark = section.title().secondaryColor().orElse((Colorc)light.darken(0.2f, new Color()));
            SimulatedCreativeTab.drawAuraText(graphics, text, dark.argb(), light.argb(), x + 5, y + 5);
        }
        ps.popPose();
        RenderSystem.disableDepthTest();
    }

    public static void drawAuraText(GuiGraphics graphics, Component text, int color1, int color2, int x, int y) {
        Font font = Minecraft.getInstance().font;
        Window window = Minecraft.getInstance().getWindow();
        float scale = (float)window.getGuiScale();
        graphics.drawString(font, text, x, y, color1, true);
        PoseStack ps = graphics.pose();
        ps.pushPose();
        ps.translate(0.0f, 0.0f, 1.0f);
        Matrix4f pose = ps.last().copy().pose();
        Vector3f position = pose.transformPosition(new Vector3f((float)x, (float)y, 0.0f));
        float f = x + font.width((FormattedText)text);
        float f2 = y;
        Objects.requireNonNull(font);
        Vector3f corner = pose.transformPosition(new Vector3f(f, f2 + 9.0f / 1.8f, 0.0f));
        position.mul(scale);
        corner.mul(scale);
        int height = (int)(corner.y - position.y);
        int width = (int)(corner.x - position.x);
        RenderSystem.enableScissor((int)((int)position.x), (int)(window.getHeight() - (int)position.y - height), (int)width, (int)height);
        graphics.drawString(font, text, x, y, color2, false);
        RenderSystem.disableScissor();
        ps.popPose();
    }

    public static void processItems(Consumer<ItemStack> displayItems, Consumer<ItemStack> searchItems) {
        HashMap<SimulatedSection, List> sectionMap = new HashMap<SimulatedSection, List>();
        for (Supplier<Item> entry : SimulatedRegistrate.TAB_ITEMS) {
            Item item = entry.get();
            ItemStack stack = item.getDefaultInstance();
            ResourceLocation sectionId = SimulatedRegistrate.sectionOf(item);
            if (sectionId == null) continue;
            SimulatedSection section = SimResourceManagers.SIMULATED_SECTION.get(sectionId);
            sectionMap.computeIfAbsent(section, s -> new LinkedList()).add(stack);
        }
        for (int i = 0; i < 9; ++i) {
            displayItems.accept(ItemStack.EMPTY);
        }
        int y = 0;
        List sectionKeys = sectionMap.keySet().stream().sorted().toList();
        for (SimulatedSection key : sectionKeys) {
            int itemCount = 0;
            List sectionItems = (List)sectionMap.get(key);
            for (ItemStack item : sectionItems) {
                if (CreativeTabItemTransforms.VisibilityType.SEARCH_ONLY.has((item = CreativeTabItemTransforms.applyTransform(item)).getItem())) {
                    searchItems.accept(item);
                    continue;
                }
                if (CreativeTabItemTransforms.VisibilityType.INVISIBLE.has(item.getItem())) continue;
                displayItems.accept(item);
                searchItems.accept(item);
                ++itemCount;
            }
            ResourceLocation id = SimResourceManagers.SIMULATED_SECTION.getId(key);
            SECTION_Y_VALUES.put((Object)id, y);
            int rowCount = (int)Math.ceil((float)itemCount / 9.0f);
            y += rowCount + 1;
            if (key != null && key.equals(sectionKeys.getLast())) break;
            int padding = 9 - itemCount % 9;
            if (padding < 9) {
                padding += 9;
            }
            for (int i = 0; i < padding; ++i) {
                displayItems.accept(ItemStack.EMPTY);
            }
        }
    }

    public static void setPlaying(ResourceLocation resourceLocation, boolean playing) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getGuiSprites().getSprite(resourceLocation);
        SpriteContents.Ticker ticker = ((SpriteContentsExtension)sprite.contents()).simulated$getTicker();
        if (ticker instanceof TickerExtension) {
            TickerExtension extension = (TickerExtension)ticker;
            extension.simulated$setPlaying(playing);
        }
    }
}
