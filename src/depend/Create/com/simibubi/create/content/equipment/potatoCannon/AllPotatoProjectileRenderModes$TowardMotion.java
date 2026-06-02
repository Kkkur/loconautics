/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileRenderModes;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record AllPotatoProjectileRenderModes.TowardMotion(int spriteAngleOffset, float spin) implements PotatoProjectileRenderMode
{
    public static final MapCodec<AllPotatoProjectileRenderModes.TowardMotion> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.INT.fieldOf("sprite_angle_offset").forGetter(i -> i.spriteAngleOffset), (App)Codec.FLOAT.fieldOf("spin").forGetter(i -> Float.valueOf(i.spin))).apply((Applicative)instance, AllPotatoProjectileRenderModes.TowardMotion::new));

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void transform(PoseStack ms, PotatoProjectileEntity entity, float pt) {
        Vec3 diff = entity.getDeltaMovement();
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(AngleHelper.deg((double)Mth.atan2((double)diff.x, (double)diff.z)))).rotateXDegrees(270.0f + AngleHelper.deg((double)Mth.atan2((double)diff.y, (double)(-Mth.sqrt((float)((float)(diff.x * diff.x + diff.z * diff.z)))))));
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(((float)entity.tickCount + pt) * 20.0f * this.spin + (float)AllPotatoProjectileRenderModes.entityRandom((Entity)entity, 360))).rotateZDegrees((float)(-this.spriteAngleOffset));
    }

    @Override
    public MapCodec<? extends PotatoProjectileRenderMode> codec() {
        return CODEC;
    }
}
