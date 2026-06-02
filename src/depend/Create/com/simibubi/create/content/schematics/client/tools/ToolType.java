/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 */
package com.simibubi.create.content.schematics.client.tools;

import com.simibubi.create.content.schematics.client.tools.DeployTool;
import com.simibubi.create.content.schematics.client.tools.FlipTool;
import com.simibubi.create.content.schematics.client.tools.ISchematicTool;
import com.simibubi.create.content.schematics.client.tools.MoveTool;
import com.simibubi.create.content.schematics.client.tools.MoveVerticalTool;
import com.simibubi.create.content.schematics.client.tools.PlaceTool;
import com.simibubi.create.content.schematics.client.tools.RotateTool;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum ToolType {
    DEPLOY(new DeployTool(), AllIcons.I_TOOL_DEPLOY),
    MOVE(new MoveTool(), AllIcons.I_TOOL_MOVE_XZ),
    MOVE_Y(new MoveVerticalTool(), AllIcons.I_TOOL_MOVE_Y),
    ROTATE(new RotateTool(), AllIcons.I_TOOL_ROTATE),
    FLIP(new FlipTool(), AllIcons.I_TOOL_MIRROR),
    PRINT(new PlaceTool(), AllIcons.I_CONFIRM);

    private ISchematicTool tool;
    private AllIcons icon;

    private ToolType(ISchematicTool tool, AllIcons icon) {
        this.tool = tool;
        this.icon = icon;
    }

    public ISchematicTool getTool() {
        return this.tool;
    }

    public MutableComponent getDisplayName() {
        return CreateLang.translateDirect("schematic.tool." + Lang.asId((String)this.name()), new Object[0]);
    }

    public AllIcons getIcon() {
        return this.icon;
    }

    public static List<ToolType> getTools(boolean creative) {
        ArrayList<ToolType> tools = new ArrayList<ToolType>();
        Collections.addAll(tools, MOVE, MOVE_Y, DEPLOY, ROTATE, FLIP);
        if (creative) {
            tools.add(PRINT);
        }
        return tools;
    }

    public List<Component> getDescription() {
        return CreateLang.translatedOptions("schematic.tool." + Lang.asId((String)this.name()) + ".description", "0", "1", "2", "3");
    }
}
