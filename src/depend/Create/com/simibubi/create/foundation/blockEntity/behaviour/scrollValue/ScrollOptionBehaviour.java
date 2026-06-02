/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.foundation.blockEntity.behaviour.scrollValue;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class ScrollOptionBehaviour<E extends Enum<E>>
extends ScrollValueBehaviour {
    private E[] options;

    public ScrollOptionBehaviour(Class<E> enum_, Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
        this.options = (Enum[])enum_.getEnumConstants();
        this.between(0, this.options.length - 1);
    }

    INamedIconOptions getIconForSelected() {
        return (INamedIconOptions)this.get();
    }

    public E get() {
        return this.options[this.value];
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return new ValueSettingsBoard(this.label, this.max, 1, (List<Component>)ImmutableList.of((Object)Component.literal((String)"Select")), new ValueSettingsFormatter.ScrollOptionSettingsFormatter((INamedIconOptions[])this.options));
    }

    @Override
    public String getClipboardKey() {
        return this.options[0].getClass().getSimpleName();
    }
}
