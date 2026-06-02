/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponentMap$Builder
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Nameable
 *  net.minecraft.world.level.block.entity.BlockEntity$DataComponentInput
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.armor;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.particle.AirParticleData;
import java.util.List;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class BacktankBlockEntity
extends KineticBlockEntity
implements Nameable {
    public int airLevel;
    public int airLevelTimer;
    private Component defaultName;
    private Component customName;
    private int capacityEnchantLevel;
    private DataComponentPatch componentPatch;

    public BacktankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.defaultName = BacktankBlockEntity.getDefaultName(state);
        this.componentPatch = DataComponentPatch.EMPTY;
    }

    public static Component getDefaultName(BlockState state) {
        if (AllBlocks.NETHERITE_BACKTANK.has(state)) {
            ((BacktankItem)((Object)AllItems.NETHERITE_BACKTANK.get())).getDescription();
        }
        return ((BacktankItem)((Object)AllItems.COPPER_BACKTANK.get())).getDescription();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.registerAwardables(behaviours, AllAdvancements.BACKTANK);
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        if (this.getSpeed() != 0.0f) {
            this.award(AllAdvancements.BACKTANK);
        }
    }

    @Override
    public void tick() {
        BooleanProperty waterProperty;
        super.tick();
        if (this.getSpeed() == 0.0f) {
            return;
        }
        BlockState state = this.getBlockState();
        if (state.hasProperty((Property)(waterProperty = BlockStateProperties.WATERLOGGED)) && ((Boolean)state.getValue((Property)waterProperty)).booleanValue()) {
            return;
        }
        if (this.airLevelTimer > 0) {
            --this.airLevelTimer;
            return;
        }
        int max = BacktankUtil.maxAir(this.capacityEnchantLevel);
        if (this.level.isClientSide) {
            Vec3 centerOf = VecHelper.getCenterOf((Vec3i)this.worldPosition);
            Vec3 v = VecHelper.offsetRandomly((Vec3)centerOf, (RandomSource)this.level.random, (float)0.65f);
            Vec3 m = centerOf.subtract(v);
            if (this.airLevel != max) {
                this.level.addParticle((ParticleOptions)new AirParticleData(1.0f, 0.05f), v.x, v.y, v.z, m.x, m.y, m.z);
            }
            return;
        }
        if (this.airLevel == max) {
            return;
        }
        int prevComparatorLevel = this.getComparatorOutput();
        float abs = Math.abs(this.getSpeed());
        int increment = Mth.clamp((int)(((int)abs - 100) / 20), (int)1, (int)5);
        this.airLevel = Math.min(max, this.airLevel + increment);
        if (this.getComparatorOutput() != prevComparatorLevel && !this.level.isClientSide) {
            this.level.updateNeighbourForOutputSignal(this.worldPosition, state.getBlock());
        }
        if (this.airLevel == max) {
            this.sendData();
        }
        this.airLevelTimer = Mth.clamp((int)((int)(128.0f - abs / 5.0f) - 108), (int)0, (int)20);
    }

    public int getComparatorOutput() {
        int max = BacktankUtil.maxAir(this.capacityEnchantLevel);
        return ComparatorUtil.fractionToRedstoneLevel((float)this.airLevel / (float)max);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("Air", this.airLevel);
        compound.putInt("Timer", this.airLevelTimer);
        compound.putInt("CapacityEnchantment", this.capacityEnchantLevel);
        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson((Component)this.customName, (HolderLookup.Provider)registries));
        }
        compound.put("Components", (Tag)CatnipCodecUtils.encode((Codec)DataComponentPatch.CODEC, (HolderLookup.Provider)registries, (Object)this.componentPatch).orElse(new CompoundTag()));
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        int prev = this.airLevel;
        this.airLevel = compound.getInt("Air");
        this.airLevelTimer = compound.getInt("Timer");
        this.capacityEnchantLevel = compound.getInt("CapacityEnchantment");
        if (compound.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson((String)compound.getString("CustomName"), (HolderLookup.Provider)registries);
        }
        this.componentPatch = CatnipCodecUtils.decode((Codec)DataComponentPatch.CODEC, (HolderLookup.Provider)registries, (Tag)compound.getCompound("Components")).orElse(DataComponentPatch.EMPTY);
        if (prev != 0 && prev != this.airLevel && this.airLevel == BacktankUtil.maxAir(this.capacityEnchantLevel) && clientPacket) {
            this.playFilledEffect();
        }
    }

    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        this.setAirLevel((Integer)componentInput.getOrDefault(AllDataComponents.BACKTANK_AIR, (Object)0));
    }

    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        components.set(AllDataComponents.BACKTANK_AIR, (Object)this.airLevel);
    }

    protected void playFilledEffect() {
        AllSoundEvents.CONFIRM.playAt(this.level, (Vec3i)this.worldPosition, 0.4f, 1.0f, true);
        Vec3 baseMotion = new Vec3(0.25, 0.1, 0.0);
        Vec3 baseVec = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        for (int i = 0; i < 360; i += 10) {
            Vec3 m = VecHelper.rotate((Vec3)baseMotion, (double)i, (Direction.Axis)Direction.Axis.Y);
            Vec3 v = baseVec.add(m.normalize().scale(0.25));
            this.level.addParticle((ParticleOptions)ParticleTypes.SPIT, v.x, v.y, v.z, m.x, m.y, m.z);
        }
    }

    public Component getName() {
        return this.customName != null ? this.customName : this.defaultName;
    }

    public int getAirLevel() {
        return this.airLevel;
    }

    public void setAirLevel(int airLevel) {
        this.airLevel = airLevel;
        this.sendData();
    }

    public void setCustomName(Component customName) {
        this.customName = customName;
    }

    public void setCapacityEnchantLevel(int capacityEnchantLevel) {
        this.capacityEnchantLevel = capacityEnchantLevel;
    }

    public void setComponentPatch(DataComponentPatch componentPatch) {
        this.componentPatch = componentPatch;
    }

    public DataComponentPatch getComponentPatch() {
        return this.componentPatch;
    }
}
