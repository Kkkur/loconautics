/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.FloatTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.AccumulatedItemCountDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ItemThroughputDisplaySource
extends AccumulatedItemCountDisplaySource {
    static final int POOL_SIZE = 10;

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        CompoundTag conf = context.sourceConfig();
        if (conf.contains("Inactive")) {
            return ZERO.copy();
        }
        double interval = 20.0 * Math.pow(60.0, conf.getInt("Interval"));
        double rate = (double)conf.getFloat("Rate") * interval;
        if (rate > 0.0) {
            int lastAmount;
            double timeBetweenStacks;
            long previousTime = conf.getLong("LastReceived");
            long gameTime = context.blockEntity().getLevel().getGameTime();
            int diff = (int)(gameTime - previousTime);
            if (diff > 0 && (double)diff > (timeBetweenStacks = (double)(lastAmount = conf.getInt("LastReceivedAmount")) / rate) * 2.0) {
                conf.putBoolean("Inactive", true);
            }
        }
        return CreateLang.number(rate).component();
    }

    @Override
    public void itemReceived(DisplayLinkBlockEntity be, int amount) {
        if (be.getBlockState().getOptionalValue((Property)DisplayLinkBlock.POWERED).orElse(true).booleanValue()) {
            return;
        }
        CompoundTag conf = be.getSourceConfig();
        long gameTime = be.getLevel().getGameTime();
        if (!conf.contains("LastReceived")) {
            conf.putLong("LastReceived", gameTime);
            return;
        }
        long previousTime = conf.getLong("LastReceived");
        ListTag rates = conf.getList("PrevRates", 5);
        if (rates.size() != 10) {
            rates = new ListTag();
            for (int i = 0; i < 10; ++i) {
                rates.add((Object)FloatTag.valueOf((float)-1.0f));
            }
        }
        int poolIndex = conf.getInt("Index") % 10;
        rates.set(poolIndex, (Tag)FloatTag.valueOf((float)((float)((double)amount / (double)(gameTime - previousTime)))));
        float rate = 0.0f;
        int validIntervals = 0;
        for (int i = 0; i < 10; ++i) {
            float pooledRate = rates.getFloat(i);
            if (!(pooledRate >= 0.0f)) continue;
            rate += pooledRate;
            ++validIntervals;
        }
        conf.remove("Rate");
        if (validIntervals > 0) {
            conf.putFloat("Rate", rate /= (float)validIntervals);
        }
        conf.remove("Inactive");
        conf.putInt("LastReceivedAmount", amount);
        conf.putLong("LastReceived", gameTime);
        conf.putInt("Index", poolIndex + 1);
        conf.put("PrevRates", (Tag)rates);
        be.updateGatheredData();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 80, (si, l) -> si.forOptions(CreateLang.translatedOptions("display_source.item_throughput.interval", "second", "minute", "hour")).titled(CreateLang.translateDirect("display_source.item_throughput.interval", new Object[0])), "Interval");
    }

    @Override
    protected String getTranslationKey() {
        return "item_throughput";
    }
}
