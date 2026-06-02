/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.createmod.catnip.outliner.Outliner
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.zapper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler;
import com.simibubi.create.content.equipment.zapper.ZapperItem;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ZapperRenderHandler
extends ShootableGadgetRenderHandler {
    public List<LaserBeam> cachedBeams;

    @Override
    protected boolean appliesTo(ItemStack stack) {
        return stack.getItem() instanceof ZapperItem;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.cachedBeams == null) {
            this.cachedBeams = new LinkedList<LaserBeam>();
        }
        this.cachedBeams.removeIf(b -> b.itensity < 0.1f);
        if (this.cachedBeams.isEmpty()) {
            return;
        }
        this.cachedBeams.forEach(beam -> Outliner.getInstance().endChasingLine(beam, beam.start, beam.end, 1.0f - beam.itensity, false).disableLineNormals().colored(0xFFFFFF).lineWidth(beam.itensity * 1.0f / 8.0f));
        this.cachedBeams.forEach(b -> b.itensity *= 0.6f);
    }

    @Override
    protected void transformTool(PoseStack ms, float flip, float equipProgress, float recoil, float pt) {
        ms.translate(flip * -0.1f, 0.1f, -0.4f);
        ms.mulPose(Axis.YP.rotationDegrees(flip * 5.0f));
    }

    @Override
    protected void transformHand(PoseStack ms, float flip, float equipProgress, float recoil, float pt) {
    }

    @Override
    protected void playSound(InteractionHand hand, Vec3 position) {
        float pitch = hand == InteractionHand.MAIN_HAND ? 0.1f : 0.9f;
        Minecraft mc = Minecraft.getInstance();
        AllSoundEvents.WORLDSHAPER_PLACE.play((Level)mc.level, (Player)mc.player, position, 0.1f, pitch);
    }

    public void addBeam(LaserBeam beam) {
        RandomSource random = Minecraft.getInstance().level.random;
        double x = beam.end.x;
        double y = beam.end.y;
        double z = beam.end.z;
        ClientLevel world = Minecraft.getInstance().level;
        Supplier<Double> randomSpeed = () -> (random.nextDouble() - 0.5) * (double)0.2f;
        Supplier<Double> randomOffset = () -> (random.nextDouble() - 0.5) * (double)0.2f;
        for (int i = 0; i < 10; ++i) {
            world.addParticle((ParticleOptions)ParticleTypes.END_ROD, x, y, z, randomSpeed.get().doubleValue(), randomSpeed.get().doubleValue(), randomSpeed.get().doubleValue());
            world.addParticle((ParticleOptions)ParticleTypes.FIREWORK, x + randomOffset.get(), y + randomOffset.get(), z + randomOffset.get(), 0.0, 0.0, 0.0);
        }
        this.cachedBeams.add(beam);
    }

    public static class LaserBeam {
        float itensity;
        Vec3 start;
        Vec3 end;

        public LaserBeam(Vec3 start, Vec3 end) {
            this.start = start;
            this.end = end;
            this.itensity = 1.0f;
        }
    }
}
