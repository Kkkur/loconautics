/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.api.equipment.potatoCannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import java.util.function.Function;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface PotatoProjectileRenderMode {
    public static final Codec<PotatoProjectileRenderMode> CODEC = CreateBuiltInRegistries.POTATO_PROJECTILE_RENDER_MODE.byNameCodec().dispatch(PotatoProjectileRenderMode::codec, Function.identity());

    @OnlyIn(value=Dist.CLIENT)
    public void transform(PoseStack var1, PotatoProjectileEntity var2, float var3);

    public MapCodec<? extends PotatoProjectileRenderMode> codec();
}
