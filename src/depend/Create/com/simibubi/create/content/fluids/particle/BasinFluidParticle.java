/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.fluids.FluidStack
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.simibubi.create.content.fluids.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.particle.FluidStackParticle;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class BasinFluidParticle
extends FluidStackParticle {
    BlockPos basinPos;
    Vec3 targetPos;
    Vec3 centerOfBasin;
    float yOffset;

    public BasinFluidParticle(ClientLevel world, FluidStack fluid, double x, double y, double z, double vx, double vy, double vz) {
        super(world, fluid, x, y, z, vx, vy, vz);
        this.gravity = 0.0f;
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.yOffset = world.random.nextFloat() * 1.0f / 32.0f;
        this.quadSize = 0.0f;
        this.lifetime = 60;
        Vec3 currentPos = new Vec3(x, y += (double)this.yOffset, z);
        this.basinPos = BlockPos.containing((Position)currentPos);
        this.centerOfBasin = VecHelper.getCenterOf((Vec3i)this.basinPos);
        if (vx != 0.0) {
            this.lifetime = 20;
            Vec3 centerOf = VecHelper.getCenterOf((Vec3i)this.basinPos);
            Vec3 diff = currentPos.subtract(centerOf).multiply(1.0, 0.0, 1.0).normalize().scale(0.375);
            this.targetPos = centerOf.add(diff);
            this.xo = x = this.centerOfBasin.x;
            this.zo = z = this.centerOfBasin.z;
        }
    }

    @Override
    public void tick() {
        super.tick();
        float f = this.quadSize = this.targetPos != null ? Math.max(0.03125f, 1.0f * (float)this.age / (float)this.lifetime / 8.0f) : 0.125f * (1.0f - (float)Math.abs(this.age - this.lifetime / 2) / (1.0f * (float)this.lifetime));
        if (this.age % 2 == 0) {
            if (!AllBlocks.BASIN.has(this.level.getBlockState(this.basinPos)) && !BasinBlock.isBasin((LevelReader)this.level, this.basinPos)) {
                this.remove();
                return;
            }
            BlockEntity blockEntity = this.level.getBlockEntity(this.basinPos);
            if (blockEntity instanceof BasinBlockEntity) {
                float totalUnits = ((BasinBlockEntity)blockEntity).getTotalFluidUnits(0.0f);
                if (totalUnits < 1.0f) {
                    totalUnits = 0.0f;
                }
                float fluidLevel = Mth.clamp((float)(totalUnits / 2000.0f), (float)0.0f, (float)1.0f);
                this.y = 0.125f + (float)this.basinPos.getY() + 0.75f * fluidLevel + this.yOffset;
            }
        }
        if (this.targetPos != null) {
            float progess = 1.0f * (float)this.age / (float)this.lifetime;
            Vec3 currentPos = this.centerOfBasin.add(this.targetPos.subtract(this.centerOfBasin).scale((double)progess));
            this.x = currentPos.x;
            this.z = currentPos.z;
        }
    }

    public void render(VertexConsumer vb, Camera info, float pt) {
        Quaternionf rotation = info.rotation();
        Quaternionf prevRotation = new Quaternionf((Quaternionfc)rotation);
        rotation.set(-1.0f, 0.0f, 0.0f, 1.0f);
        rotation.normalize();
        super.render(vb, info, pt);
        rotation.set(0.0f, 0.0f, 0.0f, 1.0f);
        rotation.mul((Quaternionfc)prevRotation);
    }

    @Override
    protected boolean canEvaporate() {
        return false;
    }
}
