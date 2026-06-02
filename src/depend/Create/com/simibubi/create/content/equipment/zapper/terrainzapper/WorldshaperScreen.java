/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.zapper.ConfigureZapperPacket;
import com.simibubi.create.content.equipment.zapper.ZapperScreen;
import com.simibubi.create.content.equipment.zapper.terrainzapper.Brush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.ConfigureWorldshaperPacket;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainBrushes;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class WorldshaperScreen
extends ZapperScreen {
    protected final Component placementSection = CreateLang.translateDirect("gui.terrainzapper.placement", new Object[0]);
    protected final Component toolSection = CreateLang.translateDirect("gui.terrainzapper.tool", new Object[0]);
    protected final List<Component> brushOptions = CreateLang.translatedOptions("gui.terrainzapper.brush", "cuboid", "sphere", "cylinder", "surface", "cluster");
    protected List<IconButton> toolButtons;
    protected List<IconButton> placementButtons;
    protected ScrollInput brushInput;
    protected Label brushLabel;
    protected List<ScrollInput> brushParams = new ArrayList<ScrollInput>(3);
    protected List<Label> brushParamLabels = new ArrayList<Label>(3);
    protected IconButton followDiagonals;
    protected IconButton acrossMaterials;
    protected Indicator followDiagonalsIndicator;
    protected Indicator acrossMaterialsIndicator;
    protected TerrainBrushes currentBrush;
    protected int[] currentBrushParams = new int[]{1, 1, 1};
    protected boolean currentFollowDiagonals;
    protected boolean currentAcrossMaterials;
    protected TerrainTools currentTool;
    protected PlacementOptions currentPlacement;

    public WorldshaperScreen(ItemStack zapper, InteractionHand hand) {
        super(AllGuiTextures.TERRAINZAPPER, zapper, hand);
        this.fontColor = 0x767676;
        this.title = zapper.getHoverName();
        this.currentBrush = (TerrainBrushes)((Object)zapper.getOrDefault(AllDataComponents.SHAPER_BRUSH, (Object)TerrainBrushes.Cuboid));
        if (zapper.has(AllDataComponents.SHAPER_BRUSH_PARAMS)) {
            BlockPos paramsData = (BlockPos)zapper.get(AllDataComponents.SHAPER_BRUSH_PARAMS);
            this.currentBrushParams[0] = paramsData.getX();
            this.currentBrushParams[1] = paramsData.getY();
            this.currentBrushParams[2] = paramsData.getZ();
            if (this.currentBrushParams[1] == 0) {
                this.currentFollowDiagonals = true;
            }
            if (this.currentBrushParams[2] == 0) {
                this.currentAcrossMaterials = true;
            }
        }
        this.currentTool = (TerrainTools)((Object)zapper.getOrDefault(AllDataComponents.SHAPER_TOOL, (Object)TerrainTools.Fill));
        this.currentPlacement = (PlacementOptions)((Object)zapper.getOrDefault(AllDataComponents.SHAPER_PLACEMENT_OPTIONS, (Object)PlacementOptions.Merged));
    }

    @Override
    protected void init() {
        super.init();
        int x = this.guiLeft;
        int y = this.guiTop;
        this.brushLabel = new Label(x + 61, y + 25, CommonComponents.EMPTY).withShadow();
        this.brushInput = new SelectionScrollInput(x + 56, y + 20, 77, 18).forOptions(this.brushOptions).titled(CreateLang.translateDirect("gui.terrainzapper.brush", new Object[0])).writingTo(this.brushLabel).calling(brushIndex -> {
            this.currentBrush = TerrainBrushes.values()[brushIndex];
            this.initBrushParams(x, y);
        });
        this.brushInput.setState(this.currentBrush.ordinal());
        this.addRenderableWidget((GuiEventListener)this.brushLabel);
        this.addRenderableWidget((GuiEventListener)this.brushInput);
        this.initBrushParams(x, y);
    }

    protected void initBrushParams(int x, int y) {
        Brush currentBrush = this.currentBrush.get();
        this.removeWidgets(this.brushParamLabels);
        this.removeWidgets(this.brushParams);
        this.brushParamLabels.clear();
        this.brushParams.clear();
        for (int index = 0; index < 3; ++index) {
            Label label = new Label(x + 65 + 20 * index, y + 45, CommonComponents.EMPTY).withShadow();
            int finalIndex = index;
            ScrollInput input = new ScrollInput(x + 56 + 20 * index, y + 40, 18, 18).withRange(currentBrush.getMin(index), currentBrush.getMax(index) + 1).writingTo(label).titled(currentBrush.getParamLabel(index).plainCopy()).calling(state -> {
                this.currentBrushParams[finalIndex] = state;
                label.setX(x + 65 + 20 * finalIndex - this.font.width((FormattedText)label.text) / 2);
            });
            input.setState(this.currentBrushParams[index]);
            input.onChanged();
            if (index >= currentBrush.amtParams) {
                input.visible = false;
                label.visible = false;
                input.active = false;
            }
            this.brushParamLabels.add(label);
            this.brushParams.add(input);
        }
        this.addRenderableWidgets(this.brushParamLabels);
        this.addRenderableWidgets(this.brushParams);
        if (this.followDiagonals != null) {
            this.removeWidget((GuiEventListener)this.followDiagonals);
            this.removeWidget((GuiEventListener)this.followDiagonalsIndicator);
            this.removeWidget((GuiEventListener)this.acrossMaterials);
            this.removeWidget((GuiEventListener)this.acrossMaterialsIndicator);
            this.followDiagonals = null;
            this.followDiagonalsIndicator = null;
            this.acrossMaterials = null;
            this.acrossMaterialsIndicator = null;
        }
        if (currentBrush.hasConnectivityOptions()) {
            int x1 = x + 7 + 72;
            int y1 = y + 79;
            this.followDiagonalsIndicator = new Indicator(x1, y1 - 6, CommonComponents.EMPTY);
            this.followDiagonals = new IconButton(x1, y1, AllIcons.I_FOLLOW_DIAGONAL);
            this.acrossMaterialsIndicator = new Indicator(x1 += 18, y1 - 6, CommonComponents.EMPTY);
            this.acrossMaterials = new IconButton(x1, y1, AllIcons.I_FOLLOW_MATERIAL);
            this.followDiagonals.withCallback(() -> {
                this.followDiagonalsIndicator.state = this.followDiagonalsIndicator.state == Indicator.State.OFF ? Indicator.State.ON : Indicator.State.OFF;
                this.currentFollowDiagonals = !this.currentFollowDiagonals;
            });
            this.followDiagonals.setToolTip((Component)CreateLang.translateDirect("gui.terrainzapper.searchDiagonal", new Object[0]));
            this.acrossMaterials.withCallback(() -> {
                this.acrossMaterialsIndicator.state = this.acrossMaterialsIndicator.state == Indicator.State.OFF ? Indicator.State.ON : Indicator.State.OFF;
                this.currentAcrossMaterials = !this.currentAcrossMaterials;
            });
            this.acrossMaterials.setToolTip((Component)CreateLang.translateDirect("gui.terrainzapper.searchFuzzy", new Object[0]));
            this.addRenderableWidget((GuiEventListener)this.followDiagonals);
            this.addRenderableWidget((GuiEventListener)this.followDiagonalsIndicator);
            this.addRenderableWidget((GuiEventListener)this.acrossMaterials);
            this.addRenderableWidget((GuiEventListener)this.acrossMaterialsIndicator);
            if (this.currentFollowDiagonals) {
                this.followDiagonalsIndicator.state = Indicator.State.ON;
            }
            if (this.currentAcrossMaterials) {
                this.acrossMaterialsIndicator.state = Indicator.State.ON;
            }
        }
        if (this.toolButtons != null) {
            this.removeWidgets(this.toolButtons);
        }
        TerrainTools[] toolValues = currentBrush.getSupportedTools();
        this.toolButtons = new ArrayList<IconButton>(toolValues.length);
        for (int id = 0; id < toolValues.length; ++id) {
            TerrainTools tool = toolValues[id];
            IconButton toolButton = new IconButton(x + 7 + id * 18, y + 79, tool.icon);
            toolButton.withCallback(() -> {
                this.toolButtons.forEach(b -> {
                    b.green = false;
                });
                toolButton.green = true;
                this.currentTool = tool;
            });
            toolButton.setToolTip((Component)CreateLang.translateDirect("gui.terrainzapper.tool." + tool.translationKey, new Object[0]));
            this.toolButtons.add(toolButton);
        }
        int toolIndex = -1;
        for (int i = 0; i < toolValues.length; ++i) {
            if (this.currentTool != toolValues[i]) continue;
            toolIndex = i;
        }
        if (toolIndex == -1) {
            this.currentTool = toolValues[0];
            toolIndex = 0;
        }
        this.toolButtons.get((int)toolIndex).green = true;
        this.addRenderableWidgets(this.toolButtons);
        if (this.placementButtons != null) {
            this.removeWidgets(this.placementButtons);
        }
        if (currentBrush.hasPlacementOptions()) {
            PlacementOptions[] placementValues = PlacementOptions.values();
            this.placementButtons = new ArrayList<IconButton>(placementValues.length);
            for (int id = 0; id < placementValues.length; ++id) {
                PlacementOptions option = placementValues[id];
                IconButton placementButton = new IconButton(x + 136 + id * 18, y + 79, option.icon);
                placementButton.withCallback(() -> {
                    this.placementButtons.forEach(b -> {
                        b.green = false;
                    });
                    placementButton.green = true;
                    this.currentPlacement = option;
                });
                placementButton.setToolTip((Component)CreateLang.translateDirect("gui.terrainzapper.placement." + option.translationKey, new Object[0]));
                this.placementButtons.add(placementButton);
            }
            this.placementButtons.get((int)this.currentPlacement.ordinal()).green = true;
            this.addRenderableWidgets(this.placementButtons);
        }
    }

    @Override
    protected void drawOnBackground(GuiGraphics graphics, int x, int y) {
        super.drawOnBackground(graphics, x, y);
        Brush currentBrush = this.currentBrush.get();
        for (int index = 2; index >= currentBrush.amtParams; --index) {
            AllGuiTextures.TERRAINZAPPER_INACTIVE_PARAM.render(graphics, x + 56 + 20 * index, y + 40);
        }
        graphics.drawString(this.font, this.toolSection, x + 7, y + 69, this.fontColor, false);
        if (currentBrush.hasPlacementOptions()) {
            graphics.drawString(this.font, this.placementSection, x + 136, y + 69, this.fontColor, false);
        }
    }

    @Override
    protected ConfigureZapperPacket getConfigurationPacket() {
        int brushParamY;
        int brushParamX = this.currentBrushParams[0];
        int n = this.followDiagonalsIndicator != null ? (this.followDiagonalsIndicator.state == Indicator.State.ON ? 0 : 1) : (brushParamY = this.currentBrushParams[1]);
        int brushParamZ = this.acrossMaterialsIndicator != null ? (this.acrossMaterialsIndicator.state == Indicator.State.ON ? 0 : 1) : this.currentBrushParams[2];
        return new ConfigureWorldshaperPacket(this.hand, this.currentPattern, this.currentBrush, brushParamX, brushParamY, brushParamZ, this.currentTool, this.currentPlacement);
    }
}
