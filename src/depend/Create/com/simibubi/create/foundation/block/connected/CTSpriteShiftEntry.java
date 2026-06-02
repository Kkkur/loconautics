/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.CTType;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class CTSpriteShiftEntry
extends SpriteShiftEntry {
    protected final CTType type;

    public CTSpriteShiftEntry(CTType type) {
        this.type = type;
    }

    public CTType getType() {
        return this.type;
    }

    public float getTargetU(float localU, int index) {
        float uOffset = index % this.type.getSheetSize();
        return this.getTarget().getU((CTSpriteShiftEntry.getUnInterpolatedU((TextureAtlasSprite)this.getOriginal(), (float)localU) + uOffset) / (float)this.type.getSheetSize());
    }

    public float getTargetV(float localV, int index) {
        float vOffset = index / this.type.getSheetSize();
        return this.getTarget().getV((CTSpriteShiftEntry.getUnInterpolatedV((TextureAtlasSprite)this.getOriginal(), (float)localV) + vOffset) / (float)this.type.getSheetSize());
    }
}
