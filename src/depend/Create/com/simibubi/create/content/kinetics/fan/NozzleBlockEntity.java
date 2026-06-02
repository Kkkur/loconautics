/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.kinetics.fan.NozzleBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NozzleBlockEntity
extends SmartBlockEntity {
    private List<Entity> pushingEntities = new ArrayList<Entity>();
    private float range;
    private boolean pushing;
    private BlockPos fanPos;

    public NozzleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(5);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        compound.putFloat("Range", this.range);
        compound.putBoolean("Pushing", this.pushing);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        this.range = compound.getFloat("Range");
        this.pushing = compound.getBoolean("Pushing");
    }

    @Override
    public void initialize() {
        this.fanPos = this.worldPosition.relative(((Direction)this.getBlockState().getValue((Property)NozzleBlock.FACING)).getOpposite());
        super.initialize();
    }

    @Override
    public void tick() {
        super.tick();
        float range = this.calcRange();
        if (this.range != range) {
            this.setRange(range);
        }
        Vec3 center = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        if (this.level.isClientSide && range != 0.0f && this.level.random.nextInt(Mth.clamp((int)((Integer)AllConfigs.server().kinetics.fanPushDistance.get() - (int)range), (int)1, (int)10)) == 0) {
            Vec3 start = VecHelper.offsetRandomly((Vec3)center, (RandomSource)this.level.random, (float)(this.pushing ? 1.0f : range / 2.0f));
            Vec3 motion = center.subtract(start).normalize().scale((double)(Mth.clamp((float)(range * (this.pushing ? 0.025f : 1.0f)), (float)0.0f, (float)0.5f) * (float)(this.pushing ? -1 : 1)));
            this.level.addParticle((ParticleOptions)ParticleTypes.POOF, start.x, start.y, start.z, motion.x, motion.y, motion.z);
        }
        Iterator<Entity> iterator = this.pushingEntities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            Vec3 diff = entity.position().subtract(center);
            if (!(entity instanceof Player) && this.level.isClientSide) continue;
            double distance = diff.length();
            if (distance > (double)range || entity.isShiftKeyDown() || AirCurrent.isPlayerCreativeFlying(entity)) {
                iterator.remove();
                continue;
            }
            if (!this.pushing && distance < 1.5) continue;
            float factor = entity instanceof ItemEntity ? 0.0078125f : 0.03125f;
            Vec3 pushVec = diff.normalize().scale(((double)range - distance) * (double)(this.pushing ? 1 : -1));
            entity.setDeltaMovement(entity.getDeltaMovement().add(pushVec.scale((double)factor)));
            entity.fallDistance = 0.0f;
            entity.hurtMarked = true;
        }
    }

    public void setRange(float range) {
        this.range = range;
        if (range == 0.0f) {
            this.pushingEntities.clear();
        }
        this.sendData();
    }

    private float calcRange() {
        BlockEntity be = this.level.getBlockEntity(this.fanPos);
        if (!(be instanceof IAirCurrentSource)) {
            return 0.0f;
        }
        IAirCurrentSource source = (IAirCurrentSource)be;
        if (source.getAirCurrent() == null) {
            return 0.0f;
        }
        if (source.getSpeed() == 0.0f) {
            return 0.0f;
        }
        this.pushing = source.getAirFlowDirection() == source.getAirflowOriginSide();
        return source.getMaxDistance();
    }

    @Override
    public void lazyTick() {
        Entity entity2;
        super.lazyTick();
        if (this.range == 0.0f) {
            return;
        }
        Vec3 center = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        AABB bb = new AABB(center, center).inflate((double)(this.range / 2.0f));
        for (Entity entity2 : this.level.getEntitiesOfClass(Entity.class, bb)) {
            Vec3 diff = entity2.position().subtract(center);
            double distance = diff.length();
            if (distance > (double)this.range || entity2.isShiftKeyDown() || AirCurrent.isPlayerCreativeFlying(entity2)) continue;
            boolean canSee = this.canSee(entity2);
            if (!canSee) {
                this.pushingEntities.remove(entity2);
                continue;
            }
            if (this.pushingEntities.contains(entity2)) continue;
            this.pushingEntities.add(entity2);
        }
        Iterator<Entity> iterator = this.pushingEntities.iterator();
        while (iterator.hasNext()) {
            entity2 = iterator.next();
            if (entity2.isAlive()) continue;
            iterator.remove();
        }
        if (!this.pushing && this.pushingEntities.size() > 256 && !this.level.isClientSide) {
            this.level.explode(null, center.x, center.y, center.z, 2.0f, Level.ExplosionInteraction.NONE);
            iterator = this.pushingEntities.iterator();
            while (iterator.hasNext()) {
                entity2 = iterator.next();
                entity2.discard();
                iterator.remove();
            }
        }
    }

    private boolean canSee(Entity entity) {
        ClipContext context = new ClipContext(entity.position(), VecHelper.getCenterOf((Vec3i)this.worldPosition), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
        return this.worldPosition.equals((Object)this.level.clip(context).getBlockPos());
    }
}
