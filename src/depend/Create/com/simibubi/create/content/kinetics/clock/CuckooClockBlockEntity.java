/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.clock;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.clock.CuckooClockBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import java.util.List;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CuckooClockBlockEntity
extends KineticBlockEntity {
    public LerpedFloat hourHand = LerpedFloat.angular();
    public LerpedFloat minuteHand = LerpedFloat.angular();
    public LerpedFloat animationProgress = LerpedFloat.linear();
    public Animation animationType = Animation.NONE;
    private boolean sendAnimationUpdate;

    public CuckooClockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.registerAwardables(behaviours, AllAdvancements.CUCKOO_CLOCK);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (clientPacket && compound.contains("Animation")) {
            this.animationType = (Animation)NBTHelper.readEnum((CompoundTag)compound, (String)"Animation", Animation.class);
            this.animationProgress.startWithValue(0.0);
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (clientPacket && this.sendAnimationUpdate) {
            NBTHelper.writeEnum((CompoundTag)compound, (String)"Animation", (Enum)this.animationType);
        }
        this.sendAnimationUpdate = false;
        super.write(compound, registries, clientPacket);
    }

    @Override
    public void tick() {
        block23: {
            block26: {
                block24: {
                    block25: {
                        super.tick();
                        if (this.getSpeed() == 0.0f) {
                            return;
                        }
                        boolean isNatural = this.level.dimensionType().natural();
                        int dayTime = (int)(this.level.getDayTime() * (long)(isNatural ? 1 : 24) % 24000L);
                        int hours = (dayTime / 1000 + 6) % 24;
                        int minutes = dayTime % 1000 * 60 / 1000;
                        if (!isNatural) {
                            if (this.level.isClientSide) {
                                this.moveHands(hours, minutes);
                                if (AnimationTickHolder.getTicks() % 6 == 0) {
                                    this.playSound((SoundEvent)SoundEvents.NOTE_BLOCK_HAT.value(), 0.0625f, 2.0f);
                                } else if (AnimationTickHolder.getTicks() % 3 == 0) {
                                    this.playSound((SoundEvent)SoundEvents.NOTE_BLOCK_HAT.value(), 0.0625f, 1.5f);
                                }
                            }
                            return;
                        }
                        if (!this.level.isClientSide) {
                            if (this.animationType == Animation.NONE) {
                                if (hours == 12 && minutes < 5) {
                                    this.startAnimation(Animation.PIG);
                                }
                                if (hours == 18 && minutes < 36 && minutes > 31) {
                                    this.startAnimation(Animation.CREEPER);
                                }
                            } else {
                                float value = this.animationProgress.getValue();
                                this.animationProgress.setValue((double)(value + 1.0f));
                                if (value > 100.0f) {
                                    this.animationType = Animation.NONE;
                                }
                                if (this.animationType == Animation.SURPRISE && Mth.equal((float)this.animationProgress.getValue(), (float)50.0f)) {
                                    Vec3 center = VecHelper.getCenterOf((Vec3i)this.worldPosition);
                                    this.level.destroyBlock(this.worldPosition, false);
                                    DamageSource damageSource = CreateDamageSources.cuckooSurprise(this.level);
                                    this.level.explode(null, damageSource, null, center.x, center.y, center.z, 3.0f, false, Level.ExplosionInteraction.BLOCK);
                                }
                            }
                        }
                        if (!this.level.isClientSide) break block23;
                        this.moveHands(hours, minutes);
                        if (this.animationType != Animation.NONE) break block24;
                        if (AnimationTickHolder.getTicks() % 32 != 0) break block25;
                        this.playSound((SoundEvent)SoundEvents.NOTE_BLOCK_HAT.value(), 0.0625f, 2.0f);
                        break block26;
                    }
                    if (AnimationTickHolder.getTicks() % 16 != 0) break block26;
                    this.playSound((SoundEvent)SoundEvents.NOTE_BLOCK_HAT.value(), 0.0625f, 1.5f);
                    break block26;
                }
                boolean isSurprise = this.animationType == Animation.SURPRISE;
                float value = this.animationProgress.getValue();
                this.animationProgress.setValue((double)(value + 1.0f));
                if (value > 100.0f) {
                    this.animationType = null;
                }
                if (value == 1.0f) {
                    this.playSound((SoundEvent)SoundEvents.NOTE_BLOCK_CHIME.value(), 2.0f, 0.5f);
                }
                if (value == 21.0f) {
                    this.playSound((SoundEvent)SoundEvents.NOTE_BLOCK_CHIME.value(), 2.0f, 0.793701f);
                }
                if (value > 30.0f && isSurprise) {
                    Vec3 pos = VecHelper.offsetRandomly((Vec3)VecHelper.getCenterOf((Vec3i)this.worldPosition), (RandomSource)this.level.random, (float)0.5f);
                    this.level.addParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
                }
                if (value == 40.0f && isSurprise) {
                    this.playSound(SoundEvents.TNT_PRIMED, 1.0f, 1.0f);
                }
                int step = isSurprise ? 3 : 15;
                for (int phase = 30; phase <= 60; phase += step) {
                    if (value == (float)(phase - step / 3)) {
                        this.playSound(SoundEvents.CHEST_OPEN, 0.0625f, 2.0f);
                    }
                    if (value == (float)phase) {
                        if (this.animationType == Animation.PIG) {
                            this.playSound(SoundEvents.PIG_AMBIENT, 0.25f, 1.0f);
                        } else {
                            this.playSound(SoundEvents.CREEPER_HURT, 0.25f, 3.0f);
                        }
                    }
                    if (value != (float)(phase + step / 3)) continue;
                    this.playSound(SoundEvents.CHEST_CLOSE, 0.0625f, 2.0f);
                }
            }
            return;
        }
    }

    public void startAnimation(Animation animation) {
        this.animationType = animation;
        if (animation != null && CuckooClockBlock.containsSurprise(this.getBlockState())) {
            this.animationType = Animation.SURPRISE;
        }
        this.animationProgress.startWithValue(0.0);
        this.sendAnimationUpdate = true;
        if (animation == Animation.CREEPER) {
            this.awardIfNear(AllAdvancements.CUCKOO_CLOCK, 32);
        }
        this.sendData();
    }

    public void moveHands(int hours, int minutes) {
        float hourTarget = 30 * (hours % 12);
        float minuteTarget = 6 * minutes;
        this.hourHand.chase((double)hourTarget, (double)0.2f, LerpedFloat.Chaser.EXP);
        this.minuteHand.chase((double)minuteTarget, (double)0.2f, LerpedFloat.Chaser.EXP);
        this.hourHand.tickChaser();
        this.minuteHand.tickChaser();
    }

    private void playSound(SoundEvent sound, float volume, float pitch) {
        Vec3 vec = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        this.level.playLocalSound(vec.x, vec.y, vec.z, sound, SoundSource.BLOCKS, volume, pitch, false);
    }

    static enum Animation {
        PIG,
        CREEPER,
        SURPRISE,
        NONE;

    }
}
