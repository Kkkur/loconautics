/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.common.extensions.IBlockExtension
 */
package com.simibubi.create.api.contraption;

import com.simibubi.create.api.contraption.ContraptionMovementSetting;
import net.neoforged.neoforge.common.extensions.IBlockExtension;

public static interface ContraptionMovementSetting.MovementSettingProvider
extends IBlockExtension {
    public ContraptionMovementSetting getContraptionMovementSetting();
}
