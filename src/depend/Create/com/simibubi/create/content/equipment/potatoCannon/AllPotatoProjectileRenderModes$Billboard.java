/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.serialization.MapCodec
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public static enum AllPotatoProjectileRenderModes.Billboard implements PotatoProjectileRenderMode
{
    INSTANCE;

    public static final MapCodec<AllPotatoProjectileRenderModes.Billboard> CODEC;

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void transform(PoseStack ms, PotatoProjectileEntity entity, float pt) {
        Minecraft mc = Minecraft.getInstance();
        Vec3 p1 = mc.getCameraEntity().getEyePosition(pt);
        Vec3 diff = entity.getBoundingBox().getCenter().subtract(p1);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(AngleHelper.deg((double)Mth.atan2((double)diff.x, (double)diff.z)) + 180.0f)).rotateXDegrees(AngleHelper.deg((double)Mth.atan2((double)diff.y, (double)Mth.sqrt((float)((float)(diff.x * diff.x + diff.z * diff.z))))));
    }

    @Override
    public MapCodec<? extends PotatoProjectileRenderMode> codec() {
        return CODEC;
    }

    static {
        CODEC = MapCodec.unit((Object)INSTANCE);
    }
}
