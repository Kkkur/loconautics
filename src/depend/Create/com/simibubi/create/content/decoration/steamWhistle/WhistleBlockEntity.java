/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.decoration.steamWhistle;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.decoration.steamWhistle.WhistleBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleExtenderBlock;
import com.simibubi.create.content.decoration.steamWhistle.WhistleSoundInstance;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.lang.ref.WeakReference;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class WhistleBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation {
    public WeakReference<FluidTankBlockEntity> source = new WeakReference<Object>(null);
    public LerpedFloat animation = LerpedFloat.linear();
    protected int pitch;
    @OnlyIn(value=Dist.CLIENT)
    protected WhistleSoundInstance soundInstance;

    public WhistleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.registerAwardables(behaviours, AllAdvancements.STEAM_WHISTLE);
    }

    public void updatePitch() {
        BlockState blockState;
        int newPitch;
        BlockPos currentPos = this.worldPosition.above();
        for (newPitch = 0; newPitch <= 24 && AllBlocks.STEAM_WHISTLE_EXTENSION.has(blockState = this.level.getBlockState(currentPos)); newPitch += 2) {
            if (blockState.getValue(WhistleExtenderBlock.SHAPE) == WhistleExtenderBlock.WhistleExtenderShape.SINGLE) {
                ++newPitch;
                break;
            }
            currentPos = currentPos.above();
        }
        if (this.pitch == newPitch) {
            return;
        }
        this.pitch = newPitch;
        this.notifyUpdate();
        FluidTankBlockEntity tank = this.getTank();
        if (tank != null && tank.boiler != null) {
            tank.boiler.checkPipeOrganAdvancement(tank);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide()) {
            if (this.isPowered()) {
                this.award(AllAdvancements.STEAM_WHISTLE);
            }
            return;
        }
        FluidTankBlockEntity tank = this.getTank();
        boolean powered = this.isPowered() && (tank != null && tank.boiler.isActive() && (tank.boiler.passiveHeat || tank.boiler.activeHeat > 0) || this.isVirtual());
        this.animation.chase(powered ? 1.0 : 0.0, powered ? 0.5 : (double)0.4f, powered ? LerpedFloat.Chaser.EXP : LerpedFloat.Chaser.LINEAR);
        this.animation.tickChaser();
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.tickAudio(this.getOctave(), powered));
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.putInt("Pitch", this.pitch);
        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        this.pitch = tag.getInt("Pitch");
        super.read(tag, registries, clientPacket);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        String[] pitches = CreateLang.translateDirect("generic.notes", new Object[0]).getString().split(";");
        CreateLang.translate("generic.pitch", pitches[this.pitch % pitches.length]).forGoggles(tooltip);
        return true;
    }

    protected boolean isPowered() {
        return this.getBlockState().getOptionalValue((Property)WhistleBlock.POWERED).orElse(false);
    }

    protected WhistleBlock.WhistleSize getOctave() {
        return this.getBlockState().getOptionalValue(WhistleBlock.SIZE).orElse(WhistleBlock.WhistleSize.MEDIUM);
    }

    @OnlyIn(value=Dist.CLIENT)
    protected void tickAudio(WhistleBlock.WhistleSize size, boolean powered) {
        if (!powered) {
            if (this.soundInstance != null) {
                this.soundInstance.fadeOut();
                this.soundInstance = null;
            }
            return;
        }
        float f = (float)Math.pow(2.0, (double)(-this.pitch) / 12.0);
        boolean particle = this.level.getGameTime() % 8L == 0L;
        Vec3 eyePosition = Minecraft.getInstance().cameraEntity.getEyePosition();
        float maxVolume = (float)Mth.clamp((double)((64.0 - eyePosition.distanceTo(Vec3.atCenterOf((Vec3i)this.worldPosition))) / 64.0), (double)0.0, (double)1.0);
        if (this.soundInstance == null || this.soundInstance.isStopped() || this.soundInstance.getOctave() != size) {
            this.soundInstance = new WhistleSoundInstance(size, this.worldPosition);
            Minecraft.getInstance().getSoundManager().play((SoundInstance)this.soundInstance);
            AllSoundEvents.WHISTLE_CHIFF.playAt(this.level, (Vec3i)this.worldPosition, maxVolume * 0.175f, size == WhistleBlock.WhistleSize.SMALL ? f + 0.75f : f, false);
            particle = true;
        }
        this.soundInstance.keepAlive();
        this.soundInstance.setPitch(f);
        if (!particle) {
            return;
        }
        Direction facing = this.getBlockState().getOptionalValue((Property)WhistleBlock.FACING).orElse(Direction.SOUTH);
        float angle = 180.0f + AngleHelper.horizontalAngle((Direction)facing);
        Vec3 sizeOffset = VecHelper.rotate((Vec3)new Vec3(0.0, (double)-0.4f, (double)(0.0625f * (float)size.ordinal())), (double)angle, (Direction.Axis)Direction.Axis.Y);
        Vec3 offset = VecHelper.rotate((Vec3)new Vec3(0.0, 1.0, 0.75), (double)angle, (Direction.Axis)Direction.Axis.Y);
        Vec3 v = offset.scale((double)0.45f).add(sizeOffset).add(Vec3.atCenterOf((Vec3i)this.worldPosition));
        Vec3 m = offset.subtract(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(0.75));
        this.level.addParticle((ParticleOptions)new SteamJetParticleData(1.0f), v.x, v.y, v.z, m.x, m.y, m.z);
    }

    public int getPitchId() {
        return this.pitch + 100 * this.getBlockState().getOptionalValue(WhistleBlock.SIZE).orElse(WhistleBlock.WhistleSize.MEDIUM).ordinal();
    }

    public FluidTankBlockEntity getTank() {
        FluidTankBlockEntity tank = (FluidTankBlockEntity)this.source.get();
        if (tank == null || tank.isRemoved()) {
            Direction facing;
            BlockEntity be;
            if (tank != null) {
                this.source = new WeakReference<Object>(null);
            }
            if ((be = this.level.getBlockEntity(this.worldPosition.relative(facing = WhistleBlock.getAttachedDirection(this.getBlockState())))) instanceof FluidTankBlockEntity) {
                FluidTankBlockEntity tankBe;
                tank = tankBe = (FluidTankBlockEntity)be;
                this.source = new WeakReference<FluidTankBlockEntity>(tank);
            }
        }
        if (tank == null) {
            return null;
        }
        return tank.getControllerBE();
    }
}
