/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import java.util.EnumMap;

private static class FactoryPanelModel.FactoryPanelModelData {
    public FactoryPanelBlock.PanelType type;
    public EnumMap<FactoryPanelBlock.PanelSlot, FactoryPanelBlock.PanelState> states = new EnumMap(FactoryPanelBlock.PanelSlot.class);
    private boolean ponder;

    private FactoryPanelModel.FactoryPanelModelData() {
    }
}
