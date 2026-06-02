/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink;

import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.ClickToLinkBlockItem;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DisplayLinkBlockItem
extends ClickToLinkBlockItem {
    public DisplayLinkBlockItem(Block pBlock, Item.Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public AABB getSelectionBounds(BlockPos pos) {
        ClientLevel world = Minecraft.getInstance().level;
        DisplayTarget target = DisplayTarget.get((LevelAccessor)world, pos);
        if (target != null) {
            return target.getMultiblockBounds((LevelAccessor)world, pos);
        }
        return super.getSelectionBounds(pos);
    }

    @Override
    public int getMaxDistanceFromSelection() {
        return (Integer)AllConfigs.server().logistics.displayLinkRange.get();
    }

    @Override
    public String getMessageTranslationKey() {
        return "display_link";
    }
}
