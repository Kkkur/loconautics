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
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceLocation
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
import com.simibubi.create.Create;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AllPotatoProjectileRenderModes {
    public static void init() {
    }

    private static void register(String name, MapCodec<? extends PotatoProjectileRenderMode> codec) {
        Registry.register(CreateBuiltInRegistries.POTATO_PROJECTILE_RENDER_MODE, (ResourceLocation)Create.asResource(name), codec);
    }

    private static int entityRandom(Entity entity, int maxValue) {
        return System.identityHashCode(entity) * 31 % maxValue;
    }

    static {
        AllPotatoProjectileRenderModes.register("billboard", Billboard.CODEC);
        AllPotatoProjectileRenderModes.register("tumble", Tumble.CODEC);
        AllPotatoProjectileRenderModes.register("toward_motion", TowardMotion.CODEC);
        AllPotatoProjectileRenderModes.register("stuck_to_entity", StuckToEntity.CODEC);
    }

    public static enum Billboard implements PotatoProjectileRenderMode
    {
        INSTANCE;

        public static final MapCodec<Billboard> CODEC;

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

    public static enum Tumble implements PotatoProjectileRenderMode
    {
        INSTANCE;

        public static final MapCodec<Tumble> CODEC;

        @Override
        @OnlyIn(value=Dist.CLIENT)
        public void transform(PoseStack ms, PotatoProjectileEntity entity, float pt) {
            Billboard.INSTANCE.transform(ms, entity, pt);
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

    public record TowardMotion(int spriteAngleOffset, float spin) implements PotatoProjectileRenderMode
    {
        public static final MapCodec<TowardMotion> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.INT.fieldOf("sprite_angle_offset").forGetter(i -> i.spriteAngleOffset), (App)Codec.FLOAT.fieldOf("spin").forGetter(i -> Float.valueOf(i.spin))).apply((Applicative)instance, TowardMotion::new));

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

    public record StuckToEntity(Vec3 offset) implements PotatoProjectileRenderMode
    {
        public static final MapCodec<StuckToEntity> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Vec3.CODEC.fieldOf("offset").forGetter(i -> i.offset)).apply((Applicative)instance, StuckToEntity::new));

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
}
