/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.block.connected.CTTypeRegistry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.createmod.catnip.lang.Lang;
import net.minecraft.resources.ResourceLocation;

public enum AllCTTypes implements CTType
{
    HORIZONTAL(2, ConnectedTextureBehaviour.ContextRequirement.builder().horizontal().build()){

        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            return (context.right ? 1 : 0) + (context.left ? 2 : 0);
        }
    }
    ,
    HORIZONTAL_KRYPPERS(2, ConnectedTextureBehaviour.ContextRequirement.builder().horizontal().build()){

        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            return !context.right && !context.left ? 0 : (!context.right ? 3 : (!context.left ? 2 : 1));
        }
    }
    ,
    VERTICAL(2, ConnectedTextureBehaviour.ContextRequirement.builder().vertical().build()){

        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            return (context.up ? 1 : 0) + (context.down ? 2 : 0);
        }
    }
    ,
    OMNIDIRECTIONAL(8, ConnectedTextureBehaviour.ContextRequirement.builder().all().build()){

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
    ,
    ROOF(4, ConnectedTextureBehaviour.ContextRequirement.builder().all().build()){

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
    ,
    ROOF_STAIR(4, ConnectedTextureBehaviour.ContextRequirement.builder().axisAligned().build()){
        private static final int[][] MAPPING = new int[][]{{1, 6, 9, 4}, {14, 12, 13, 15}, {2, 10, 8, 0}, {5, 5, 5, 5}};

        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            int type = (context.up ? 2 : 0) + (context.right ? 1 : 0);
            int rot = (context.left ? 2 : 0) + (context.down ? 1 : 0);
            return MAPPING[type][rot];
        }
    }
    ,
    CROSS(4, ConnectedTextureBehaviour.ContextRequirement.builder().axisAligned().build()){

        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            return (context.up ? 1 : 0) + (context.down ? 2 : 0) + (context.left ? 4 : 0) + (context.right ? 8 : 0);
        }
    }
    ,
    RECTANGLE(4, ConnectedTextureBehaviour.ContextRequirement.builder().axisAligned().build()){

        @Override
        public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
            int x;
            int n = context.left && context.right ? 2 : (context.left ? 3 : (x = context.right ? 1 : 0));
            int y = context.up && context.down ? 1 : (context.up ? 2 : (context.down ? 0 : 3));
            return x + y * 4;
        }
    };

    private final ResourceLocation id = Create.asResource(Lang.asId((String)this.name()));
    private final int sheetSize;
    private final ConnectedTextureBehaviour.ContextRequirement contextRequirement;

    private AllCTTypes(int sheetSize, ConnectedTextureBehaviour.ContextRequirement contextRequirement) {
        this.sheetSize = sheetSize;
        this.contextRequirement = contextRequirement;
        CTTypeRegistry.register(this);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public int getSheetSize() {
        return this.sheetSize;
    }

    @Override
    public ConnectedTextureBehaviour.ContextRequirement getContextRequirement() {
        return this.contextRequirement;
    }
}
