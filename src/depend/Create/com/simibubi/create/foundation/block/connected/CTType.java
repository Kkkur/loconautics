/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.resources.ResourceLocation;

public interface CTType {
    public ResourceLocation getId();

    public int getSheetSize();

    public ConnectedTextureBehaviour.ContextRequirement getContextRequirement();

    public int getTextureIndex(ConnectedTextureBehaviour.CTContext var1);
}
