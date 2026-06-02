/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record AllPotatoProjectileRenderModes.StuckToEntity(Vec3 offset) implements PotatoProjectileRenderMode
{
    public static final MapCodec<AllPotatoProjectileRenderModes.StuckToEntity> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Vec3.CODEC.fieldOf("offset").forGetter(i -> i.offset)).apply((Applicative)instance, AllPotatoProjectileRenderModes.StuckToEntity::new));

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void transform(PoseStack ms, PotatoProjectileEntity entity, float pt) {
        TransformStack.of((PoseStack)ms).rotateYDegrees(AngleHelper.deg((double)Mth.atan2((double)this.offset.x, (double)this.offset.z)));
    }

    @Override
    public MapCodec<? extends PotatoProjectileRenderMode> codec() {
        return CODEC;
    }
}
