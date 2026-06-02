/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.createmod.catnip.gui.UIRenderHelper
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.index;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.simulated_team.simulated.Simulated;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class SimGUITextures
extends Enum<SimGUITextures>
implements ScreenElement {
    public static final /* enum */ SimGUITextures MODULATINGLINK = new SimGUITextures("modulating_linked_receiver", 182, 99);
    public static final /* enum */ SimGUITextures MODULATINGLINK_MARKER = new SimGUITextures("modulating_linked_receiver", 193, 4, 3, 20);
    public static final /* enum */ SimGUITextures MODULATINGLINK_POWERED_LANE = new SimGUITextures("modulating_linked_receiver", 0, 100, 120, 16);
    public static final /* enum */ SimGUITextures MODULATINGLINK_TARGET = new SimGUITextures("modulating_linked_receiver", 188, 4, 4, 4);
    public static final /* enum */ SimGUITextures ALTITUDE_SENSOR = new SimGUITextures("altitude_sensor", 42, 206);
    public static final /* enum */ SimGUITextures ALTITUDE_SENSOR_BAR_LIT = new SimGUITextures("altitude_sensor", 48, 0, 10, 200);
    public static final /* enum */ SimGUITextures ALTITUDE_SENSOR_GRABBY_THING = new SimGUITextures("altitude_sensor", 64, 0, 26, 16);
    public static final /* enum */ SimGUITextures DIAGRAM = new SimGUITextures("diagram", 0, 64, 256, 192, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_PAPER = new SimGUITextures("diagram", 256, 0, 96, 128, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_STICKY_NOTE = new SimGUITextures("diagram", 256, 128, 112, 112, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_TAB = new SimGUITextures("diagram", 240, 0, 11, 10, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_MAGNIFYING_GLASS = new SimGUITextures("diagram", 32, 16, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_FORCES = new SimGUITextures("diagram", 0, 48, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_FORCES_SEPARATED = new SimGUITextures("diagram", 32, 32, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_FORCES_MERGED = new SimGUITextures("diagram", 48, 32, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_MASS = new SimGUITextures("diagram", 16, 48, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_COM_TOGGLE = new SimGUITextures("diagram", 32, 48, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_COM = new SimGUITextures("diagram", 48, 48, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_COM_TINY = new SimGUITextures("diagram", 48, 0, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_COM_ARROW = new SimGUITextures("diagram", 48, 16, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_ARROW_IN_PAGE_SHADOW = new SimGUITextures("diagram", 0, 16, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_ARROW_OUT_PAGE_SHADOW = new SimGUITextures("diagram", 16, 16, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_ARROW_IN_PAGE = new SimGUITextures("diagram", 0, 32, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_ARROW_OUT_PAGE = new SimGUITextures("diagram", 16, 32, 16, 16, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_TURN_LEFT = new SimGUITextures("diagram", 192, 48, 8, 13, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_TURN_RIGHT = new SimGUITextures("diagram", 240, 48, 8, 13, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_TURN_DOWN = new SimGUITextures("diagram", 208, 48, 7, 7, 512, 256);
    public static final /* enum */ SimGUITextures DIAGRAM_ICON_TURN_UP = new SimGUITextures("diagram", 224, 48, 7, 7, 512, 256);
    public static final /* enum */ SimGUITextures LINKED_REMOTE = new SimGUITextures("linked_remote", 7, 1, 108, 109);
    public static final /* enum */ SimGUITextures LINKED_REMOTE_COLOR = new SimGUITextures("linked_remote", 16, 112, 9, 40);
    public static final /* enum */ SimGUITextures ASSEMBLER_TRACK_START = new SimGUITextures("assembler", 0, 0, 14, 6);
    public static final /* enum */ SimGUITextures ASSEMBLER_TRACK_MIDDLE = new SimGUITextures("assembler", 0, 7, 14, 10);
    public static final /* enum */ SimGUITextures ASSEMBLER_TRACK_END = new SimGUITextures("assembler", 0, 18, 14, 5);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_MAIN = new SimGUITextures("linked_typewriter/linked_typewriter", 0, 0, 246, 127);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_KEYS_MENU = new SimGUITextures("linked_typewriter/linked_typewriter_keys", 0, 0, 238, 195);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_KEY_BINDING = new SimGUITextures("linked_typewriter/linked_typewriter_keys", 0, 195, 191, 30);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_KEY_ENTRY = new SimGUITextures("linked_typewriter/linked_typewriter_keys", 0, 225, 214, 30);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_KEY_MODIFICATION_MENU = new SimGUITextures("linked_typewriter/linked_typewriter", 0, 145, 214, 80);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_FREQUENCY = new SimGUITextures("linked_typewriter/linked_typewriter", 0, 127, 36, 18);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_TOOLTIP_ARROW = new SimGUITextures("linked_typewriter/linked_typewriter", 36, 127, 18, 11);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_TOOLTIP_BACKGROUND = new SimGUITextures(Simulated.path("widgets/item_background"), 0, 0, 6, 6, 6, 6);
    public static final /* enum */ SimGUITextures KEY_START = new SimGUITextures("linked_typewriter/linked_typewriter", 60, 127, 6, 14);
    public static final /* enum */ SimGUITextures KEY_MIDDLE = new SimGUITextures("linked_typewriter/linked_typewriter", 66, 127, 2, 14);
    public static final /* enum */ SimGUITextures KEY_END = new SimGUITextures("linked_typewriter/linked_typewriter", 68, 127, 6, 14);
    public static final /* enum */ SimGUITextures INACTIVE_KEY_START = new SimGUITextures("linked_typewriter/linked_typewriter", 73, 127, 6, 14);
    public static final /* enum */ SimGUITextures INACTIVE_KEY_MIDDLE = new SimGUITextures("linked_typewriter/linked_typewriter", 79, 127, 2, 14);
    public static final /* enum */ SimGUITextures INACTIVE_KEY_END = new SimGUITextures("linked_typewriter/linked_typewriter", 81, 127, 6, 14);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_BIND = new SimGUITextures("linked_typewriter/linked_typewriter", 0, 154, 212, 89);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_ARROW_LEFT = new SimGUITextures("linked_typewriter/linked_typewriter", 0, 244, 9, 7);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_ARROW_RIGHT = new SimGUITextures("linked_typewriter/linked_typewriter", 10, 244, 9, 7);
    public static final /* enum */ SimGUITextures LINKED_TYPEWRITER_TRASH_CONFIRM_HOVER = new SimGUITextures("linked_typewriter/linked_typewriter", 113, 127, 18, 18);
    @NotNull
    public final ResourceLocation location;
    public final int width;
    public final int height;
    public final int startX;
    public final int startY;
    public final int texWidth;
    public final int texHeight;
    private static final /* synthetic */ SimGUITextures[] $VALUES;

    public static SimGUITextures[] values() {
        return (SimGUITextures[])$VALUES.clone();
    }

    public static SimGUITextures valueOf(String name) {
        return Enum.valueOf(SimGUITextures.class, name);
    }

    private SimGUITextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private SimGUITextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private SimGUITextures(String location, int startX, int startY, int width, int height) {
        this("simulated", location, startX, startY, width, height);
    }

    private SimGUITextures(String location, int startX, int startY, int width, int height, int texWidth, int texHeight) {
        this("simulated", location, startX, startY, width, height, texWidth, texHeight);
    }

    private SimGUITextures(String namespace, String location, int startX, int startY, int width, int height) {
        this(namespace, location, startX, startY, width, height, 256, 256);
    }

    private SimGUITextures(ResourceLocation location, int startX, int startY, int width, int height, int texWidth, int texHeight) {
        this.location = location;
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    private SimGUITextures(String namespace, String location, int startX, int startY, int width, int height, int texWidth, int texHeight) {
        ResourceLocation loc = ResourceLocation.tryBuild((String)namespace, (String)("textures/gui/" + location + ".png"));
        assert (loc != null);
        this.location = loc;
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    public void bind() {
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)this.location);
    }

    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(this.location, x, y, (float)this.startX, (float)this.startY, this.width, this.height, this.texWidth, this.texHeight);
    }

    public void render(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.blit(this.location, x, y, (float)this.startX, (float)this.startY, width, height, this.texWidth, this.texHeight);
    }

    public void render(GuiGraphics graphics, int x, int y, Color c) {
        this.bind();
        UIRenderHelper.drawColoredTexture((GuiGraphics)graphics, (Color)c, (int)x, (int)y, (int)0, (float)this.startX, (float)this.startY, (int)this.width, (int)this.height, (int)this.texWidth, (int)this.texHeight);
    }

    private static /* synthetic */ SimGUITextures[] $values() {
        return new SimGUITextures[]{MODULATINGLINK, MODULATINGLINK_MARKER, MODULATINGLINK_POWERED_LANE, MODULATINGLINK_TARGET, ALTITUDE_SENSOR, ALTITUDE_SENSOR_BAR_LIT, ALTITUDE_SENSOR_GRABBY_THING, DIAGRAM, DIAGRAM_PAPER, DIAGRAM_STICKY_NOTE, DIAGRAM_TAB, DIAGRAM_ICON_MAGNIFYING_GLASS, DIAGRAM_ICON_FORCES, DIAGRAM_ICON_FORCES_SEPARATED, DIAGRAM_ICON_FORCES_MERGED, DIAGRAM_ICON_MASS, DIAGRAM_ICON_COM_TOGGLE, DIAGRAM_ICON_COM, DIAGRAM_ICON_COM_TINY, DIAGRAM_ICON_COM_ARROW, DIAGRAM_ICON_ARROW_IN_PAGE_SHADOW, DIAGRAM_ICON_ARROW_OUT_PAGE_SHADOW, DIAGRAM_ICON_ARROW_IN_PAGE, DIAGRAM_ICON_ARROW_OUT_PAGE, DIAGRAM_ICON_TURN_LEFT, DIAGRAM_ICON_TURN_RIGHT, DIAGRAM_ICON_TURN_DOWN, DIAGRAM_ICON_TURN_UP, LINKED_REMOTE, LINKED_REMOTE_COLOR, ASSEMBLER_TRACK_START, ASSEMBLER_TRACK_MIDDLE, ASSEMBLER_TRACK_END, LINKED_TYPEWRITER_MAIN, LINKED_TYPEWRITER_KEYS_MENU, LINKED_TYPEWRITER_KEY_BINDING, LINKED_TYPEWRITER_KEY_ENTRY, LINKED_TYPEWRITER_KEY_MODIFICATION_MENU, LINKED_TYPEWRITER_FREQUENCY, LINKED_TYPEWRITER_TOOLTIP_ARROW, LINKED_TYPEWRITER_TOOLTIP_BACKGROUND, KEY_START, KEY_MIDDLE, KEY_END, INACTIVE_KEY_START, INACTIVE_KEY_MIDDLE, INACTIVE_KEY_END, LINKED_TYPEWRITER_BIND, LINKED_TYPEWRITER_ARROW_LEFT, LINKED_TYPEWRITER_ARROW_RIGHT, LINKED_TYPEWRITER_TRASH_CONFIRM_HOVER};
    }

    static {
        $VALUES = SimGUITextures.$values();
    }
}
