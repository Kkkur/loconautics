/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.SignBlockEntity
 */
package com.simibubi.create.content.redstone.displayLink.target;

import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class SignDisplayTarget
extends DisplayTarget {
    @Override
    public void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context) {
        BlockEntity be = context.getTargetBlockEntity();
        if (!(be instanceof SignBlockEntity)) {
            return;
        }
        SignBlockEntity sign = (SignBlockEntity)be;
        boolean changed = false;
        Couple signText = Couple.createWithContext(arg_0 -> ((SignBlockEntity)sign).getText(arg_0));
        int i = 0;
        while (i < text.size() && i + line < 4) {
            if (i == 0) {
                SignDisplayTarget.reserve(i + line, (BlockEntity)sign, context);
            }
            if (i > 0 && this.isReserved(i + line, (BlockEntity)sign, context)) break;
            int iFinal = i++;
            String content = text.get(iFinal).getString(sign.getMaxTextLineWidth());
            signText = signText.map(st -> st.setMessage(iFinal + line, (Component)Component.literal((String)content)));
            changed = true;
        }
        if (changed) {
            signText.forEachWithContext((arg_0, arg_1) -> ((SignBlockEntity)sign).setText(arg_0, arg_1));
            context.level().sendBlockUpdated(context.getTargetPos(), sign.getBlockState(), sign.getBlockState(), 2);
        }
    }

    @Override
    public DisplayTargetStats provideStats(DisplayLinkContext context) {
        return new DisplayTargetStats(4, 15, this);
    }

    @Override
    public boolean requiresComponentSanitization() {
        return true;
    }
}
