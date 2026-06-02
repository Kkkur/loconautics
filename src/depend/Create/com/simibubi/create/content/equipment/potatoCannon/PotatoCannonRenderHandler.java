/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler;
import com.simibubi.create.foundation.particle.AirParticleData;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PotatoCannonRenderHandler
extends ShootableGadgetRenderHandler {
    private float nextPitch;

    @Override
    protected void playSound(InteractionHand hand, Vec3 position) {
        PotatoProjectileEntity.playLaunchSound((Level)Minecraft.getInstance().level, position, this.nextPitch);
    }

    @Override
    protected boolean appliesTo(ItemStack stack) {
        return stack.getItem() instanceof PotatoCannonItem;
    }

    public void beforeShoot(float nextPitch, Vec3 location, Vec3 motion, ItemStack stack) {
        this.nextPitch = nextPitch;
        if (stack.isEmpty()) {
            return;
        }
        ClientLevel world = Minecraft.getInstance().level;
        for (int i = 0; i < 2; ++i) {
            Vec3 m = VecHelper.offsetRandomly((Vec3)motion.scale((double)0.1f), (RandomSource)world.random, (float)0.025f);
            world.addParticle((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, stack), location.x, location.y, location.z, m.x, m.y, m.z);
            Vec3 m2 = VecHelper.offsetRandomly((Vec3)motion.scale(2.0), (RandomSource)world.random, (float)0.5f);
            world.addParticle((ParticleOptions)new AirParticleData(1.0f, 0.25f), location.x, location.y, location.z, m2.x, m2.y, m2.z);
        }
    }

    @Override
    protected void transformTool(PoseStack ms, float flip, float equipProgress, float recoil, float pt) {
        ms.translate(flip * -0.1f, 0.0f, 0.14f);
        ms.scale(0.75f, 0.75f, 0.75f);
        TransformStack.of((PoseStack)ms).rotateXDegrees(recoil * 80.0f);
    }

    @Override
    protected void transformHand(PoseStack ms, float flip, float equipProgress, float recoil, float pt) {
        ms.translate((double)flip * -0.09, -0.275, -0.25);
        TransformStack.of((PoseStack)ms).rotateZDegrees(flip * -10.0f);
    }
}
