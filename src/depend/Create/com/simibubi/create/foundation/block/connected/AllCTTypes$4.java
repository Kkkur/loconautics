/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;

final class AllCTTypes.4
extends AllCTTypes {
    private AllCTTypes.4(int sheetSize, ConnectedTextureBehaviour.ContextRequirement contextRequirement) {
    }

    @Override
    public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
        ConnectedTextureBehaviour.CTContext c = context;
        int tileX = 0;
        int tileY = 0;
        int borders = (!c.up ? 1 : 0) + (!c.down ? 1 : 0) + (!c.left ? 1 : 0) + (!c.right ? 1 : 0);
        if (c.up) {
            ++tileX;
        }
        if (c.down) {
            tileX += 2;
        }
        if (c.left) {
            ++tileY;
        }
        if (c.right) {
            tileY += 2;
        }
        if (borders == 0) {
            if (c.topRight) {
                ++tileX;
            }
            if (c.topLeft) {
                tileX += 2;
            }
            if (c.bottomRight) {
                tileY += 2;
            }
            if (c.bottomLeft) {
                ++tileY;
            }
        }
        if (borders == 1) {
            if (!c.right && (c.topLeft || c.bottomLeft)) {
                tileY = 4;
                tileX = -1 + (c.bottomLeft ? 1 : 0) + (c.topLeft ? 1 : 0) * 2;
            }
            if (!c.left && (c.topRight || c.bottomRight)) {
                tileY = 5;
                tileX = -1 + (c.bottomRight ? 1 : 0) + (c.topRight ? 1 : 0) * 2;
            }
            if (!c.down && (c.topLeft || c.topRight)) {
                tileY = 6;
                tileX = -1 + (c.topLeft ? 1 : 0) + (c.topRight ? 1 : 0) * 2;
            }
            if (!c.up && (c.bottomLeft || c.bottomRight)) {
                tileY = 7;
                tileX = -1 + (c.bottomLeft ? 1 : 0) + (c.bottomRight ? 1 : 0) * 2;
            }
        }
        if (borders == 2 && (c.up && c.left && c.topLeft || c.down && c.left && c.bottomLeft || c.up && c.right && c.topRight || c.down && c.right && c.bottomRight)) {
            tileX += 3;
        }
        return tileX + 8 * tileY;
    }
}
