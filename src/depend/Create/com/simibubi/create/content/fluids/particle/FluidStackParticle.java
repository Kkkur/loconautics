/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.TextureSheetParticle
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.particles.ColorParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.FluidStack
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.fluids.particle;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.fluids.particle.BasinFluidParticle;
import com.simibubi.create.content.fluids.particle.FluidParticleData;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class FluidStackParticle
extends TextureSheetParticle {
    private final float uo;
    private final float vo;
    private final FluidStack fluid;
    private final IClientFluidTypeExtensions clientFluid;

    public static FluidStackParticle create(ParticleType<FluidParticleData> type, ClientLevel world, FluidStack fluid, double x, double y, double z, double vx, double vy, double vz) {
        if (type == AllParticleTypes.BASIN_FLUID.get()) {
            return new BasinFluidParticle(world, fluid, x, y, z, vx, vy, vz);
        }
        return new FluidStackParticle(world, fluid, x, y, z, vx, vy, vz);
    }

    public FluidStackParticle(ClientLevel world, FluidStack fluid, double x, double y, double z, double vx, double vy, double vz) {
        super(world, x, y, z, vx, vy, vz);
        this.clientFluid = IClientFluidTypeExtensions.of((Fluid)fluid.getFluid());
        this.fluid = fluid;
        this.setSprite((TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(this.clientFluid.getStillTexture(fluid)));
        this.gravity = 1.0f;
        this.rCol = 0.8f;
        this.gCol = 0.8f;
        this.bCol = 0.8f;
        this.multiplyColor(this.clientFluid.getTintColor(fluid));
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.quadSize /= 2.0f;
        this.uo = this.random.nextFloat() * 3.0f;
        this.vo = this.random.nextFloat() * 3.0f;
    }

    protected int getLightColor(float p_189214_1_) {
        int brightnessForRender = super.getLightColor(p_189214_1_);
        int skyLight = brightnessForRender >> 20;
        int blockLight = brightnessForRender >> 4 & 0xF;
        blockLight = Math.max(blockLight, this.fluid.getFluid().getFluidType().getLightLevel(this.fluid));
        return skyLight << 20 | blockLight << 4;
    }

    protected void multiplyColor(int color) {
        this.rCol *= (float)(color >> 16 & 0xFF) / 255.0f;
        this.gCol *= (float)(color >> 8 & 0xFF) / 255.0f;
        this.bCol *= (float)(color & 0xFF) / 255.0f;
    }

    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0f) / 4.0f);
    }

    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0f);
    }

    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0f);
    }

    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0f) / 4.0f);
    }

    public void tick() {
        super.tick();
        if (!this.canEvaporate()) {
            return;
        }
        if (this.onGround) {
            this.remove();
        }
        if (!this.removed) {
            return;
        }
        if (!this.onGround && this.level.random.nextFloat() < 0.125f) {
            return;
        }
        Color color = new Color(this.clientFluid.getTintColor(this.fluid));
        this.level.addParticle((ParticleOptions)ColorParticleOption.create((ParticleType)ParticleTypes.ENTITY_EFFECT, (float)color.getRedAsFloat(), (float)color.getGreenAsFloat(), (float)color.getBlueAsFloat()), this.x, this.y, this.z, 0.0, 0.0, 0.0);
    }

    protected boolean canEvaporate() {
        return this.fluid.getFluid() instanceof PotionFluid;
    }

    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }
}
