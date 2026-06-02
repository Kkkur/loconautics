/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.block.AirBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.concurrent.atomic.AtomicInteger;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class BlockBreakingKineticBlockEntity
extends KineticBlockEntity {
    public static final AtomicInteger NEXT_BREAKER_ID = new AtomicInteger();
    protected int ticksUntilNextProgress;
    protected int destroyProgress;
    protected int breakerId = -NEXT_BREAKER_ID.incrementAndGet();
    protected BlockPos breakingPos;

    public BlockBreakingKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        if (this.destroyProgress == -1) {
            this.destroyNextTick();
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.ticksUntilNextProgress == -1) {
            this.destroyNextTick();
        }
    }

    public void destroyNextTick() {
        this.ticksUntilNextProgress = 1;
    }

    protected abstract BlockPos getBreakingPos();

    protected boolean shouldRun() {
        return true;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("Progress", this.destroyProgress);
        compound.putInt("NextTick", this.ticksUntilNextProgress);
        if (this.breakingPos != null) {
            compound.put("Breaking", NbtUtils.writeBlockPos((BlockPos)this.breakingPos));
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.destroyProgress = compound.getInt("Progress");
        this.ticksUntilNextProgress = compound.getInt("NextTick");
        this.breakingPos = null;
        if (compound.contains("Breaking")) {
            this.breakingPos = NBTHelper.readBlockPos((CompoundTag)compound, (String)"Breaking");
        }
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!this.level.isClientSide && this.destroyProgress != 0) {
            this.level.destroyBlockProgress(this.breakerId, this.breakingPos, -1);
        }
    }

    @Override
    public void tick() {
        float blockHardness;
        super.tick();
        if (this.level.isClientSide) {
            return;
        }
        if (!this.shouldRun()) {
            return;
        }
        if (this.getSpeed() == 0.0f) {
            return;
        }
        this.breakingPos = this.getBreakingPos();
        if (this.ticksUntilNextProgress < 0) {
            return;
        }
        if (this.ticksUntilNextProgress-- > 0) {
            return;
        }
        BlockState stateToBreak = this.level.getBlockState(this.breakingPos);
        if (!this.canBreak(stateToBreak, blockHardness = stateToBreak.getDestroySpeed((BlockGetter)this.level, this.breakingPos))) {
            if (this.destroyProgress != 0) {
                this.destroyProgress = 0;
                this.level.destroyBlockProgress(this.breakerId, this.breakingPos, -1);
            }
            return;
        }
        float breakSpeed = this.getBreakSpeed();
        this.destroyProgress += Mth.clamp((int)((int)(breakSpeed / blockHardness)), (int)1, (int)(10 - this.destroyProgress));
        this.level.playSound(null, this.worldPosition, stateToBreak.getSoundType().getHitSound(), SoundSource.BLOCKS, 0.25f, 1.0f);
        if (this.destroyProgress >= 10) {
            this.onBlockBroken(stateToBreak);
            this.destroyProgress = 0;
            this.ticksUntilNextProgress = -1;
            this.level.destroyBlockProgress(this.breakerId, this.breakingPos, -1);
            return;
        }
        this.ticksUntilNextProgress = (int)(blockHardness / breakSpeed);
        this.level.destroyBlockProgress(this.breakerId, this.breakingPos, this.destroyProgress);
    }

    public boolean canBreak(BlockState stateToBreak, float blockHardness) {
        return BlockBreakingKineticBlockEntity.isBreakable(stateToBreak, blockHardness);
    }

    public static boolean isBreakable(BlockState stateToBreak, float blockHardness) {
        return !stateToBreak.liquid() && !(stateToBreak.getBlock() instanceof AirBlock) && blockHardness != -1.0f && !AllTags.AllBlockTags.NON_BREAKABLE.matches(stateToBreak);
    }

    public void onBlockBroken(BlockState stateToBreak) {
        Vec3 vec = VecHelper.offsetRandomly((Vec3)VecHelper.getCenterOf((Vec3i)this.breakingPos), (RandomSource)this.level.random, (float)0.125f);
        BlockHelper.destroyBlock(this.level, this.breakingPos, 1.0f, stack -> {
            if (stack.isEmpty()) {
                return;
            }
            if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
                return;
            }
            if (this.level.restoringBlockSnapshots) {
                return;
            }
            ItemEntity itementity = new ItemEntity(this.level, vec.x, vec.y, vec.z, stack);
            itementity.setDefaultPickUpDelay();
            itementity.setDeltaMovement(Vec3.ZERO);
            this.level.addFreshEntity((Entity)itementity);
        });
    }

    protected float getBreakSpeed() {
        return Math.abs(this.getSpeed() / 100.0f);
    }
}
