/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.EnchantingTableBlock
 *  net.minecraft.world.level.block.entity.EnchantingTableBlockEntity
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;

public class EnchantPowerDisplaySource
extends NumericSingleLineDisplaySource {
    protected static final RandomSource random = RandomSource.create();
    protected static final ItemStack stack = new ItemStack((ItemLike)Items.DIAMOND_PICKAXE);

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof EnchantingTableBlockEntity)) {
            return ZERO.copy();
        }
        BlockPos pos = context.getSourcePos();
        Level level = context.level();
        float enchantPower = 0.0f;
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            if (!EnchantingTableBlock.isValidBookShelf((Level)level, (BlockPos)pos, (BlockPos)offset)) continue;
            enchantPower += level.getBlockState(pos.offset((Vec3i)offset)).getEnchantPowerBonus((LevelReader)level, pos.offset((Vec3i)offset));
        }
        int cost = EnchantmentHelper.getEnchantmentCost((RandomSource)random, (int)2, (int)((int)enchantPower), (ItemStack)stack);
        return Component.literal((String)String.valueOf(cost));
    }

    @Override
    protected String getTranslationKey() {
        return "max_enchant_level";
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
