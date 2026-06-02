/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;

final class AllCTTypes.5
extends AllCTTypes {
    private AllCTTypes.5(int sheetSize, ConnectedTextureBehaviour.ContextRequirement contextRequirement) {
    }

    @Override
    public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
        boolean rightDrops;
        boolean upDrops = context.down && !context.up && (context.left || context.right);
        boolean downDrops = !context.down && context.up && (context.left || context.right);
        boolean leftDrops = !context.left && context.right && (context.up || context.down);
        boolean bl = rightDrops = context.left && !context.right && (context.up || context.down);
        if (upDrops) {
            if (leftDrops) {
                return context.bottomRight ? 0 : 5;
            }
            if (rightDrops) {
                return context.bottomLeft ? 2 : 5;
            }
            return 1;
        }
        if (downDrops) {
            if (leftDrops) {
                return context.topRight ? 8 : 5;
            }
            if (rightDrops) {
                return context.topLeft ? 10 : 5;
            }
            return 9;
        }
        if (leftDrops) {
            return 4;
        }
        if (rightDrops) {
            return 6;
        }
        if (!(context.up && context.down && context.left && context.right)) {
            return 5;
        }
        if (context.bottomLeft && context.topRight) {
            if (context.topLeft && !context.bottomRight) {
                return 12;
            }
            if (context.bottomRight && !context.topLeft) {
                return 15;
            }
            if (!context.bottomRight && !context.topLeft) {
                return 7;
            }
        }
        if (context.bottomRight && context.topLeft) {
            if (context.topRight && !context.bottomLeft) {
                return 13;
            }
            if (context.bottomLeft && !context.topRight) {
                return 14;
            }
            if (!context.bottomLeft && !context.topRight) {
                return 11;
            }
        }
        return 5;
    }
}
