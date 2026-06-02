/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.FallingBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.mounted.MountedContraption;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockBreakingMovementBehaviour
implements MovementBehaviour {
    @Override
    public void startMoving(MovementContext context) {
        if (context.world.isClientSide) {
            return;
        }
        context.data.putInt("BreakerId", -BlockBreakingKineticBlockEntity.NEXT_BREAKER_ID.incrementAndGet());
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        Level world = context.world;
        BlockState stateVisited = world.getBlockState(pos);
        if (!stateVisited.isRedstoneConductor((BlockGetter)world, pos)) {
            this.damageEntities(context, pos, world);
        }
        if (world.isClientSide) {
            return;
        }
        if (!this.canBreak(world, pos, stateVisited)) {
            return;
        }
        context.data.put("BreakingPos", NbtUtils.writeBlockPos((BlockPos)pos));
        context.stall = true;
    }

    public void damageEntities(MovementContext context, BlockPos pos, Level world) {
        DamageSource damageSource;
        Object object = context.contraption.entity;
        if (object instanceof OrientedContraptionEntity) {
            OrientedContraptionEntity oce = (OrientedContraptionEntity)((Object)object);
            if (oce.nonDamageTicks > 0) {
                return;
            }
        }
        if ((damageSource = this.getDamageSource(world)) == null && !this.throwsEntities(world)) {
            return;
        }
        block0: for (Entity entity : world.getEntitiesOfClass(Entity.class, new AABB(pos))) {
            if (entity instanceof ItemEntity || entity instanceof AbstractContraptionEntity || entity.isPassengerOfSameVehicle((Entity)context.contraption.entity)) continue;
            if (entity instanceof AbstractMinecart) {
                for (Entity passenger : entity.getIndirectPassengers()) {
                    if (!(passenger instanceof AbstractContraptionEntity) || ((AbstractContraptionEntity)passenger).getContraption() != context.contraption) continue;
                    continue block0;
                }
            }
            if (damageSource != null && !world.isClientSide) {
                float damage = (float)Mth.clamp((double)(6.0 * Math.pow(context.relativeMotion.length(), 0.4) + 1.0), (double)2.0, (double)10.0);
                entity.hurt(damageSource, damage);
            }
            if (!this.throwsEntities(world) || world.isClientSide != entity instanceof Player) continue;
            this.throwEntity(context, entity);
        }
    }

    protected void throwEntity(MovementContext context, Entity entity) {
        Vec3 motionBoost = context.motion.add(0.0, context.motion.length() / 4.0, 0.0);
        int maxBoost = 4;
        if (motionBoost.length() > (double)maxBoost) {
            motionBoost = motionBoost.subtract(motionBoost.normalize().scale(motionBoost.length() - (double)maxBoost));
        }
        entity.setDeltaMovement(entity.getDeltaMovement().add(motionBoost));
        entity.hurtMarked = true;
    }

    protected DamageSource getDamageSource(Level level) {
        return null;
    }

    protected boolean throwsEntities(Level level) {
        return this.getDamageSource(level) != null;
    }

    @Override
    public void cancelStall(MovementContext context) {
        CompoundTag data = context.data;
        if (context.world.isClientSide) {
            return;
        }
        if (!data.contains("BreakingPos")) {
            return;
        }
        Level world = context.world;
        int id = data.getInt("BreakerId");
        BlockPos breakingPos = NBTHelper.readBlockPos((CompoundTag)data, (String)"BreakingPos");
        data.remove("Progress");
        data.remove("TicksUntilNextProgress");
        data.remove("BreakingPos");
        MovementBehaviour.super.cancelStall(context);
        world.destroyBlockProgress(id, breakingPos, -1);
    }

    @Override
    public void stopMoving(MovementContext context) {
        this.cancelStall(context);
    }

    @Override
    public void tick(MovementContext context) {
        this.tickBreaker(context);
        CompoundTag data = context.data;
        if (!data.contains("WaitingTicks")) {
            return;
        }
        int waitingTicks = data.getInt("WaitingTicks");
        if (waitingTicks-- > 0) {
            data.putInt("WaitingTicks", waitingTicks);
            context.stall = true;
            return;
        }
        BlockPos pos = NBTHelper.readBlockPos((CompoundTag)data, (String)"LastPos");
        data.remove("WaitingTicks");
        data.remove("LastPos");
        context.stall = false;
        this.visitNewPosition(context, pos);
    }

    public void tickBreaker(MovementContext context) {
        CompoundTag data = context.data;
        if (context.world.isClientSide) {
            return;
        }
        if (!data.contains("BreakingPos")) {
            context.stall = false;
            return;
        }
        if (context.relativeMotion.equals((Object)Vec3.ZERO)) {
            context.stall = false;
            return;
        }
        int ticksUntilNextProgress = data.getInt("TicksUntilNextProgress");
        if (ticksUntilNextProgress-- > 0) {
            data.putInt("TicksUntilNextProgress", ticksUntilNextProgress);
            return;
        }
        Level world = context.world;
        BlockPos breakingPos = NBTHelper.readBlockPos((CompoundTag)data, (String)"BreakingPos");
        int destroyProgress = data.getInt("Progress");
        int id = data.getInt("BreakerId");
        BlockState stateToBreak = world.getBlockState(breakingPos);
        float blockHardness = stateToBreak.getDestroySpeed((BlockGetter)world, breakingPos);
        if (!this.canBreak(world, breakingPos, stateToBreak)) {
            if (destroyProgress != 0) {
                destroyProgress = 0;
                data.remove("Progress");
                data.remove("TicksUntilNextProgress");
                data.remove("BreakingPos");
                world.destroyBlockProgress(id, breakingPos, -1);
            }
            context.stall = false;
            return;
        }
        float breakSpeed = this.getBlockBreakingSpeed(context);
        destroyProgress += Mth.clamp((int)((int)(breakSpeed / blockHardness)), (int)1, (int)(10 - destroyProgress));
        world.playSound(null, breakingPos, stateToBreak.getSoundType((LevelReader)world, breakingPos, null).getHitSound(), SoundSource.NEUTRAL, 0.25f, 1.0f);
        if (destroyProgress >= 10) {
            world.destroyBlockProgress(id, breakingPos, -1);
            BlockPos ogPos = breakingPos;
            BlockState stateAbove = world.getBlockState(breakingPos.above());
            while (stateAbove.getBlock() instanceof FallingBlock) {
                breakingPos = breakingPos.above();
                stateAbove = world.getBlockState(breakingPos.above());
            }
            stateToBreak = world.getBlockState(breakingPos);
            context.stall = false;
            if (this.shouldDestroyStartBlock(stateToBreak)) {
                this.destroyBlock(context, breakingPos);
            }
            this.onBlockBroken(context, ogPos, stateToBreak);
            ticksUntilNextProgress = -1;
            data.remove("Progress");
            data.remove("TicksUntilNextProgress");
            data.remove("BreakingPos");
            return;
        }
        ticksUntilNextProgress = (int)(blockHardness / breakSpeed);
        world.destroyBlockProgress(id, breakingPos, destroyProgress);
        data.putInt("TicksUntilNextProgress", ticksUntilNextProgress);
        data.putInt("Progress", destroyProgress);
    }

    protected void destroyBlock(MovementContext context, BlockPos breakingPos) {
        BlockHelper.destroyBlock(context.world, breakingPos, 1.0f, stack -> this.collectOrDropItem(context, (ItemStack)stack));
    }

    protected float getBlockBreakingSpeed(MovementContext context) {
        float lowerLimit = 0.0078125f;
        if (context.contraption instanceof MountedContraption) {
            lowerLimit = 1.0f;
        }
        if (context.contraption instanceof CarriageContraption) {
            lowerLimit = 2.0f;
        }
        return Mth.clamp((float)(Math.abs(context.getAnimationSpeed()) / 500.0f), (float)lowerLimit, (float)16.0f);
    }

    protected boolean shouldDestroyStartBlock(BlockState stateToBreak) {
        return true;
    }

    public boolean canBreak(Level world, BlockPos breakingPos, BlockState state) {
        float blockHardness = state.getDestroySpeed((BlockGetter)world, breakingPos);
        return BlockBreakingKineticBlockEntity.isBreakable(state, blockHardness);
    }

    protected void onBlockBroken(MovementContext context, BlockPos pos, BlockState brokenState) {
        if (!(brokenState.getBlock() instanceof FallingBlock)) {
            return;
        }
        CompoundTag data = context.data;
        data.putInt("WaitingTicks", 10);
        data.put("LastPos", NbtUtils.writeBlockPos((BlockPos)pos));
        context.stall = true;
    }
}
