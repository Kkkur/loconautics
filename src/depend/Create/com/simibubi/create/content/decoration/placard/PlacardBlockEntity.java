/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.decoration.placard;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.placard.PlacardBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class PlacardBlockEntity
extends SmartBlockEntity {
    ItemStack heldItem = ItemStack.EMPTY;
    int poweredTicks = 0;

    public PlacardBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            return;
        }
        if (this.poweredTicks == 0) {
            return;
        }
        --this.poweredTicks;
        if (this.poweredTicks > 0) {
            return;
        }
        BlockState blockState = this.getBlockState();
        this.level.setBlock(this.worldPosition, (BlockState)blockState.setValue((Property)PlacardBlock.POWERED, (Comparable)Boolean.valueOf(false)), 3);
        PlacardBlock.updateNeighbours(blockState, this.level, this.worldPosition);
    }

    public ItemStack getHeldItem() {
        return this.heldItem;
    }

    public void setHeldItem(ItemStack heldItem) {
        this.heldItem = heldItem;
        this.notifyUpdate();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.putInt("PoweredTicks", this.poweredTicks);
        tag.put("Item", this.heldItem.saveOptional(registries));
        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        int prevTicks = this.poweredTicks;
        this.poweredTicks = tag.getInt("PoweredTicks");
        this.heldItem = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)tag.getCompound("Item"));
        super.read(tag, registries, clientPacket);
        if (clientPacket && prevTicks < this.poweredTicks) {
            this.spawnParticles();
        }
    }

    private void spawnParticles() {
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.PLACARD.has(blockState)) {
            return;
        }
        DustParticleOptions pParticleData = new DustParticleOptions(new Vector3f(1.0f, 0.2f, 0.0f), 1.0f);
        Vec3 centerOf = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        Vec3 normal = Vec3.atLowerCornerOf((Vec3i)PlacardBlock.connectedDirection(blockState).getNormal());
        Vec3 offset = VecHelper.axisAlingedPlaneOf((Vec3)normal);
        for (int i = 0; i < 10; ++i) {
            Vec3 v = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)this.level.random, (float)0.5f).multiply(offset).normalize().scale((double)0.45f).add(normal.scale((double)-0.45f)).add(centerOf);
            this.level.addParticle((ParticleOptions)pParticleData, v.x, v.y, v.z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
