/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase$ConfigEnum
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public static enum StockKeeperRequestScreen.SearchSyncMode implements StringRepresentable
{
    SYNC_BOTH(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_BOTH),
    SYNC_FROM_JEI(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_FROM_JEI),
    SYNC_FROM_STOCK_KEEPER(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_FROM_STOCK_KEEPER),
    NONE(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_DISABLED);

    public final AllGuiTextures buttonTexture;

    private StockKeeperRequestScreen.SearchSyncMode(AllGuiTextures buttonTexture) {
        this.buttonTexture = buttonTexture;
    }

    public boolean isBothOr(StockKeeperRequestScreen.SearchSyncMode mode) {
        return this == SYNC_BOTH || this == mode;
    }

    public StockKeeperRequestScreen.SearchSyncMode next() {
        StockKeeperRequestScreen.SearchSyncMode[] vals = StockKeeperRequestScreen.SearchSyncMode.values();
        return vals[(this.ordinal() + 1) % vals.length];
    }

    public static void cycleConfig() {
        ConfigBase.ConfigEnum<StockKeeperRequestScreen.SearchSyncMode> modeConfig = AllConfigs.client().syncRecipeViewerSearch;
        modeConfig.set((Object)((StockKeeperRequestScreen.SearchSyncMode)((Object)modeConfig.get())).next());
    }

    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }
}
