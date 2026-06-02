/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.lang.LangBuilder
 *  net.createmod.catnip.lang.LangNumberFormat
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.foundation.utility;

import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

public class CreateLang
extends Lang {
    public static MutableComponent translateDirect(String key, Object ... args) {
        Object[] args1 = LangBuilder.resolveBuilders((Object[])args);
        return Component.translatable((String)("create." + key), (Object[])args1);
    }

    public static List<Component> translatedOptions(String prefix, String ... keys) {
        ArrayList<Component> result = new ArrayList<Component>(keys.length);
        for (String key : keys) {
            result.add((Component)CreateLang.translate((String)(prefix != null ? prefix + "." : "") + key, new Object[0]).component());
        }
        return result;
    }

    public static LangBuilder builder() {
        return new LangBuilder("create");
    }

    public static LangBuilder blockName(BlockState state) {
        return CreateLang.builder().add(state.getBlock().getName());
    }

    public static LangBuilder itemName(ItemStack stack) {
        return CreateLang.builder().add(stack.getHoverName().copy());
    }

    public static LangBuilder fluidName(FluidStack stack) {
        return CreateLang.builder().add(stack.getHoverName().copy());
    }

    public static LangBuilder number(double d) {
        return CreateLang.builder().text(LangNumberFormat.format((double)d));
    }

    public static LangBuilder translate(String langKey, Object ... args) {
        return CreateLang.builder().translate(langKey, args);
    }

    public static LangBuilder text(String text) {
        return CreateLang.builder().text(text);
    }

    @Deprecated
    public static LangBuilder temporaryText(String text) {
        return CreateLang.builder().text(text);
    }
}
