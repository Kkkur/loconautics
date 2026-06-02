/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.serialization.MapCodec
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileRenderModes;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public static enum AllPotatoProjectileRenderModes.Tumble implements PotatoProjectileRenderMode
{
    INSTANCE;

    public static final MapCodec<AllPotatoProjectileRenderModes.Tumble> CODEC;

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void transform(PoseStack ms, PotatoProjectileEntity entity, float pt) {
        AllPotatoProjectileRenderModes.Billboard.INSTANCE.transform(ms, entity, pt);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateZDegrees(((float)entity.tickCount + pt) * 2.0f * (float)AllPotatoProjectileRenderModes.entityRandom((Entity)entity, 16))).rotateXDegrees(((float)entity.tickCount + pt) * (float)AllPotatoProjectileRenderModes.entityRandom((Entity)entity, 32));
    }

    @Override
    public MapCodec<? extends PotatoProjectileRenderMode> codec() {
        return CODEC;
    }

    static {
        CODEC = MapCodec.unit((Object)INSTANCE);
    }
}
